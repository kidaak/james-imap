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
package org.apache.james.imap.decode.imap4rev1;

import org.apache.james.api.imap.ImapCommand;
import org.apache.james.api.imap.ImapMessage;
import org.apache.james.api.imap.imap4rev1.Imap4Rev1CommandFactory;
import org.apache.james.api.imap.imap4rev1.Imap4Rev1MessageFactory;
import org.apache.james.imap.decode.ImapRequestLineReader;
import org.apache.james.imap.decode.InitialisableCommandFactory;
import org.apache.james.imap.decode.ProtocolException;
import org.apache.james.imap.decode.base.AbstractImapCommandParser;

class CloseCommandParser extends AbstractImapCommandParser implements
        InitialisableCommandFactory {

    public CloseCommandParser() {
    }

    /**
     * @see org.apache.james.imap.decode.InitialisableCommandFactory#init(org.apache.james.api.imap.imap4rev1.Imap4Rev1CommandFactory)
     */
    public void init(Imap4Rev1CommandFactory factory) {
        final ImapCommand command = factory.getClose();
        setCommand(command);
    }

    protected ImapMessage decode(ImapCommand command,
            ImapRequestLineReader request, String tag) throws ProtocolException {
        endLine(request);
        final Imap4Rev1MessageFactory factory = getMessageFactory();
        final ImapMessage result = factory.createCloseMessage(command, tag);
        return result;
    }

}