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

import org.apache.james.imap.api.DecodingException;
import org.apache.james.imap.api.ImapCommand;
import org.apache.james.imap.api.ImapConstants;
import org.apache.james.imap.api.ImapMessage;
import org.apache.james.imap.api.ImapMessageCallback;
import org.apache.james.imap.api.ImapSession;
import org.apache.james.imap.decode.ImapRequestLine;
import org.apache.james.imap.decode.base.AbstractImapCommandParser;
import org.apache.james.imap.message.request.UnselectRequest;

/**
 * 
 *  Parse UNSELECT commands
 *  
 * 
 * See RFC3691
 *
 */
public class UnselectCommandParser extends AbstractImapCommandParser  {
    
    public UnselectCommandParser() {
        // from the RFC it seems like the command should be valid in any state. At least kind of, as
        // we will return a "BAD" response if no mailbox is currently selected in the UnselectProcessor
        super(ImapCommand.authenticatedStateCommand(ImapConstants.UNSELECT_COMMAND_NAME));

    }

    @Override
    protected void decode(ImapCommand command, ImapRequestLine request, String tag, ImapSession session, ImapMessageCallback callback) {
        try {
            endLine(request);
            final ImapMessage result = new UnselectRequest(tag, command);
            callback.onMessage(result);
        } catch (DecodingException e) {
            callback.onException(e);
        }
    }


}
