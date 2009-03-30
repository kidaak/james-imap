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
package org.apache.james.imap.decode.parser;

import org.apache.commons.logging.Log;
import org.apache.james.imap.api.Imap4Rev1CommandFactory;
import org.apache.james.imap.api.Imap4Rev1MessageFactory;
import org.apache.james.imap.api.ImapCommand;
import org.apache.james.imap.api.ImapConstants;
import org.apache.james.imap.api.ImapMessage;
import org.apache.james.imap.api.message.StatusDataItems;
import org.apache.james.imap.decode.ImapRequestLineReader;
import org.apache.james.imap.decode.InitialisableCommandFactory;
import org.apache.james.imap.decode.ProtocolException;
import org.apache.james.imap.decode.base.AbstractImapCommandParser;

class StatusCommandParser extends AbstractImapCommandParser implements
        InitialisableCommandFactory {
    public StatusCommandParser() {
    }

    /**
     * @see org.apache.james.imap.decode.InitialisableCommandFactory#init(org.apache.james.imap.api.Imap4Rev1CommandFactory)
     */
    public void init(Imap4Rev1CommandFactory factory) {
        final ImapCommand command = factory.getStatus();
        setCommand(command);
    }

    StatusDataItems statusDataItems(ImapRequestLineReader request)
            throws ProtocolException {
        StatusDataItems items = new StatusDataItems();

        request.nextWordChar();
        consumeChar(request, '(');
        CharacterValidator validator = new NoopCharValidator();
        String nextWord = consumeWord(request, validator);
        while (!nextWord.endsWith(")")) {
            addItem(nextWord, items);
            nextWord = consumeWord(request, validator);
        }
        // Got the closing ")", may be attached to a word.
        if (nextWord.length() > 1) {
            addItem(nextWord.substring(0, nextWord.length() - 1), items);
        }

        return items;
    }

    private void addItem(String nextWord, StatusDataItems items)
            throws ProtocolException {
        if (nextWord.equals(ImapConstants.STATUS_MESSAGES)) {
            items.setMessages(true);
        } else if (nextWord.equals(ImapConstants.STATUS_RECENT)) {
            items.setRecent(true);
        } else if (nextWord.equals(ImapConstants.STATUS_UIDNEXT)) {
            items.setUidNext(true);
        } else if (nextWord.equals(ImapConstants.STATUS_UIDVALIDITY)) {
            items.setUidValidity(true);
        } else if (nextWord.equals(ImapConstants.STATUS_UNSEEN)) {
            items.setUnseen(true);
        } else {
            throw new ProtocolException("Unknown status item: '" + nextWord
                    + "'");
        }
    }

    protected ImapMessage decode(ImapCommand command,
            ImapRequestLineReader request, String tag, Log logger) throws ProtocolException {
        final String mailboxName = mailbox(request);
        final StatusDataItems statusDataItems = statusDataItems(request);
        endLine(request);
        final Imap4Rev1MessageFactory factory = getMessageFactory();
        final ImapMessage result = factory.createStatusMessage(command,
                mailboxName, statusDataItems, tag);
        return result;
    }
}