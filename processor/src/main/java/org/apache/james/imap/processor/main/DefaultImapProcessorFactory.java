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

package org.apache.james.imap.processor.main;

import org.apache.james.imap.api.message.response.StatusResponseFactory;
import org.apache.james.imap.api.process.ImapProcessor;
import org.apache.james.imap.api.process.ImapProcessorFactory;
import org.apache.james.imap.mailbox.MailboxManager;
import org.apache.james.imap.message.response.UnpooledStatusResponseFactory;
import org.apache.james.imap.processor.DefaultProcessorChain;
import org.apache.james.imap.processor.base.ImapResponseMessageProcessor;
import org.apache.james.imap.processor.base.UnknownRequestProcessor;

/**
 * 
 */
public class DefaultImapProcessorFactory implements ImapProcessorFactory {

    public static final ImapProcessor createDefaultProcessor(final MailboxManager mailboxManager) {
        final StatusResponseFactory statusResponseFactory = new UnpooledStatusResponseFactory();
        final UnknownRequestProcessor unknownRequestImapProcessor = new UnknownRequestProcessor(
                statusResponseFactory);
        final ImapProcessor imap4rev1Chain = DefaultProcessorChain
                .createStartTLSSupportingChain(unknownRequestImapProcessor,
                        mailboxManager, statusResponseFactory, true);
        final ImapProcessor result = new ImapResponseMessageProcessor(
                imap4rev1Chain);
        return result;
    }

    private MailboxManager mailboxManager;

    public final void configure(MailboxManager mailboxManager) {
        setMailboxManager(mailboxManager);
    }

    public final MailboxManager getMailboxManager() {
        return mailboxManager;
    }

    public final void setMailboxManager(MailboxManager mailboxManager) {
        this.mailboxManager = mailboxManager;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.api.process.ImapProcessorFactory#buildImapProcessor()
     */
    public ImapProcessor buildImapProcessor() {
        return createDefaultProcessor(mailboxManager);
    }
}
