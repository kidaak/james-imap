/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.james.imap.processor;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.james.imap.api.ImapCommand;
import org.apache.james.imap.api.ImapConstants;
import org.apache.james.imap.api.ImapMessage;
import org.apache.james.imap.api.ImapSession;
import org.apache.james.imap.api.ImapSessionUtils;
import org.apache.james.imap.api.display.HumanReadableText;
import org.apache.james.imap.api.message.request.ImapRequest;
import org.apache.james.imap.api.message.response.StatusResponseFactory;
import org.apache.james.imap.api.process.ImapProcessor;
import org.apache.james.imap.message.request.LsubRequest;
import org.apache.james.imap.message.response.LSubResponse;
import org.apache.james.mailbox.MailboxConstants;
import org.apache.james.mailbox.MailboxException;
import org.apache.james.mailbox.MailboxManager;
import org.apache.james.mailbox.MailboxPath;
import org.apache.james.mailbox.MailboxQuery;
import org.apache.james.mailbox.MailboxSession;
import org.apache.james.mailbox.SubscriptionException;
import org.apache.james.mailbox.SubscriptionManager;

public class LSubProcessor extends AbstractSubscriptionProcessor {

    public LSubProcessor(ImapProcessor next, MailboxManager mailboxManager, SubscriptionManager subscriptionManager, StatusResponseFactory factory) {
        super(next, mailboxManager, subscriptionManager, factory);
    }

    protected boolean isAcceptable(ImapMessage message) {
        return (message instanceof LsubRequest);
    }

    private void listSubscriptions(ImapSession session, Responder responder,
            final String referenceName, final String mailboxName)
            throws SubscriptionException, MailboxException {
        final MailboxSession mailboxSession = ImapSessionUtils.getMailboxSession(session);
        final Collection<String> mailboxes = getSubscriptionManager().subscriptions(mailboxSession);
        // If the mailboxName is fully qualified, ignore the reference name.
        String finalReferencename = referenceName;
        
        if (mailboxName.charAt(0) == ImapConstants.NAMESPACE_PREFIX_CHAR) {
            finalReferencename = "";
        }
      
        // Is the interpreted (combined) pattern relative?
        boolean isRelative = ((finalReferencename + mailboxName).charAt(0) != ImapConstants.NAMESPACE_PREFIX_CHAR);
        MailboxPath basePath = null;
        if (isRelative) {
            basePath = new MailboxPath(MailboxConstants.USER_NAMESPACE,
                    mailboxSession.getUser().getUserName(), finalReferencename);
        }
        else {
            basePath = buildFullPath(session, finalReferencename);
        }
       

        final MailboxQuery expression = new MailboxQuery(basePath, mailboxName, '*', '%', mailboxSession.getPathDelimiter());
        final Collection<String> mailboxResponses = new ArrayList<String>();
        for (final String mailbox: mailboxes) {
            respond(responder, expression, mailbox, true, mailboxes, mailboxResponses, mailboxSession.getPathDelimiter());
        }
    }

    private void respond(Responder responder, final MailboxQuery expression,
            final String mailboxName, final boolean originalSubscription,
            final Collection<String> mailboxes, final Collection<String> mailboxResponses, final char delimiter) {
        if (expression.isExpressionMatch(mailboxName)) {
            if (!mailboxResponses.contains(mailboxName)) {
                final LSubResponse response = new LSubResponse(mailboxName, !originalSubscription, delimiter);
                responder.respond(response);
                mailboxResponses.add(mailboxName);
            }
        }
        else {
            final int lastDelimiter = mailboxName.lastIndexOf(delimiter);
            if (lastDelimiter > 0) {
                final String parentMailbox = mailboxName.substring(0, lastDelimiter);
                if (!mailboxes.contains(parentMailbox)) {
                    respond(responder, expression, parentMailbox, false, mailboxes, mailboxResponses, delimiter);
                }
            }
        }
    }

    /**
     * An empty mailboxPattern signifies a request for the hierarchy delimiter
     * and root name of the referenceName argument
     * 
     * @param referenceName
     *            IMAP reference name, possibly null
     */
    private void respondWithHierarchyDelimiter(final Responder responder, final char delimiter) {
        final LSubResponse response = new LSubResponse("", true, delimiter);
        responder.respond(response);
    }

    @Override
    protected void doProcessRequest(ImapRequest message, ImapSession session, String tag, ImapCommand command, Responder responder) {
        final LsubRequest request = (LsubRequest) message;
        final String referenceName = request.getBaseReferenceName();
        final String mailboxPattern = request.getMailboxPattern();
        final MailboxSession mailboxSession = ImapSessionUtils.getMailboxSession(session);
        
        try {
            if (mailboxPattern.length() == 0) {
                respondWithHierarchyDelimiter(responder, mailboxSession.getPathDelimiter());
            } else {
                listSubscriptions(session, responder, referenceName, mailboxPattern);
            }

            okComplete(command, tag, responder);

        } catch (SubscriptionException e) {
            session.getLog().debug("Subscription failed", e);
            no(command, tag, responder, HumanReadableText.GENERIC_LSUB_FAILURE);
        } catch (MailboxException e) {
            session.getLog().debug("Subscription failed", e);
            final HumanReadableText displayTextKey = HumanReadableText.GENERIC_LSUB_FAILURE;
            no(command, tag, responder, displayTextKey);
        }        
    }
}
