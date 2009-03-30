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

import org.apache.james.imap.api.ImapCommand;
import org.apache.james.imap.api.ImapMessage;
import org.apache.james.imap.api.display.HumanReadableTextKey;
import org.apache.james.imap.api.message.request.ImapRequest;
import org.apache.james.imap.api.message.response.StatusResponseFactory;
import org.apache.james.imap.api.process.ImapProcessor;
import org.apache.james.imap.api.process.ImapSession;
import org.apache.james.imap.mailbox.MailboxException;
import org.apache.james.imap.mailbox.MailboxExistsException;
import org.apache.james.imap.mailbox.MailboxManager;
import org.apache.james.imap.mailbox.MailboxManagerProvider;
import org.apache.james.imap.mailbox.MailboxNotFoundException;
import org.apache.james.imap.message.request.RenameRequest;

public class RenameProcessor extends AbstractMailboxProcessor {

    public RenameProcessor(final ImapProcessor next,
            final MailboxManagerProvider mailboxManagerProvider,
            final StatusResponseFactory factory) {
        super(next, mailboxManagerProvider, factory);
    }

    protected boolean isAcceptable(ImapMessage message) {
        return (message instanceof RenameRequest);
    }

    protected void doProcess(ImapRequest message, ImapSession session,
            String tag, ImapCommand command, Responder responder) {
        final RenameRequest request = (RenameRequest) message;
        final String existingName = request.getExistingName();
        final String newName = request.getNewName();
        try {

            final String fullExistingName = buildFullName(session, existingName);
            final String fullNewName = buildFullName(session, newName);
            final MailboxManager mailboxManager = getMailboxManager();
            mailboxManager.renameMailbox(fullExistingName, fullNewName);
            okComplete(command, tag, responder);
            unsolicitedResponses(session, responder, false);

        } catch (MailboxExistsException e) {
            no(command, tag, responder,
                    HumanReadableTextKey.FAILURE_MAILBOX_EXISTS);
        } catch (MailboxNotFoundException e) {
            no(command, tag, responder,
                    HumanReadableTextKey.FAILURE_NO_SUCH_MAILBOX);
        } catch (MailboxException e) {
            no(command, tag, responder, e, session);
        }
    }
}