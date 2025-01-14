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

import static org.apache.james.imap.api.ImapConstants.SUPPORTS_NAMESPACES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.james.imap.api.ImapCommand;
import org.apache.james.imap.api.ImapMessage;
import org.apache.james.imap.api.ImapSession;
import org.apache.james.imap.api.ImapSessionUtils;
import org.apache.james.imap.api.message.request.ImapRequest;
import org.apache.james.imap.api.message.response.StatusResponseFactory;
import org.apache.james.imap.api.process.ImapProcessor;
import org.apache.james.imap.message.request.NamespaceRequest;
import org.apache.james.imap.message.response.NamespaceResponse;
import org.apache.james.mailbox.MailboxManager;
import org.apache.james.mailbox.MailboxSession;

/**
 * Processes a NAMESPACE command into a suitable set of responses.
 */
public class NamespaceProcessor extends AbstractMailboxProcessor implements CapabilityImplementingProcessor{
    
    public NamespaceProcessor(ImapProcessor next,
            MailboxManager mailboxManager,
            StatusResponseFactory factory) {
        super(next, mailboxManager, factory);
    }

    @Override
    protected void doProcess(ImapRequest message, ImapSession session,
            String tag, ImapCommand command, Responder responder) {
        final MailboxSession mailboxSession = ImapSessionUtils.getMailboxSession(session);
        final List<NamespaceResponse.Namespace> personalNamespaces = buildPersonalNamespaces(mailboxSession);
        final List<NamespaceResponse.Namespace> otherUsersNamespaces = buildOtherUsersSpaces(mailboxSession);
        final List<NamespaceResponse.Namespace> sharedNamespaces = buildSharedNamespaces(mailboxSession);
        final NamespaceResponse response = new NamespaceResponse(personalNamespaces, otherUsersNamespaces, sharedNamespaces);
        responder.respond(response);
        unsolicitedResponses(session, responder, false);
        okComplete(command, tag, responder);
    }

    /**
     * Builds personal namespaces from the session.
     * @param mailboxSession not null
     * @return personal namespaces, not null
     */
    private List<NamespaceResponse.Namespace> buildPersonalNamespaces(final MailboxSession mailboxSession) {
        final List<NamespaceResponse.Namespace> personalSpaces = new ArrayList<NamespaceResponse.Namespace>();
        personalSpaces.add(new NamespaceResponse.Namespace(mailboxSession.getPersonalSpace(), mailboxSession.getPathDelimiter()));
        return personalSpaces;
    }

    private List<NamespaceResponse.Namespace> buildOtherUsersSpaces(final MailboxSession mailboxSession) {
        final String otherUsersSpace = mailboxSession.getOtherUsersSpace();
        final List<NamespaceResponse.Namespace> otherUsersSpaces;
        if (otherUsersSpace == null) {
            otherUsersSpaces = null;
        } else {
            otherUsersSpaces = new ArrayList<NamespaceResponse.Namespace>(1);
            otherUsersSpaces.add(new NamespaceResponse.Namespace(otherUsersSpace, mailboxSession.getPathDelimiter()));
        }
        return otherUsersSpaces;
    }
    
    private List<NamespaceResponse.Namespace> buildSharedNamespaces(final MailboxSession mailboxSession) {
        List<NamespaceResponse.Namespace> sharedNamespaces = null;
        final Collection<String> sharedSpaces = mailboxSession.getSharedSpaces();
        if (!sharedSpaces.isEmpty()) {
            sharedNamespaces = new ArrayList<NamespaceResponse.Namespace>(sharedSpaces.size());
            for (String space: sharedSpaces) {
                sharedNamespaces.add(new NamespaceResponse.Namespace(space, mailboxSession.getPathDelimiter()));
            }    
        }
        return sharedNamespaces;
    }

    @Override
    protected boolean isAcceptable(ImapMessage message) {
        return message instanceof NamespaceRequest;
    }


    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.processor.CapabilityImplementingProcessor#getImplementedCapabilities(org.apache.james.imap.api.process.ImapSession)
     */
    public List<String> getImplementedCapabilities(ImapSession session) {
        return Arrays.asList(SUPPORTS_NAMESPACES);
    }

}
