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
import org.apache.james.imap.api.message.IdRange;
import org.apache.james.imap.decode.ImapRequestLine;
import org.apache.james.imap.message.request.ExpungeRequest;

/**
 * Parse EXPUNGE commands
 *
 */
public class ExpungeCommandParser extends AbstractUidCommandParser {

    public ExpungeCommandParser() {
        super(ImapCommand.selectedStateCommand(ImapConstants.EXPUNGE_COMMAND_NAME));
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.decode.parser.AbstractUidCommandParser#decode(org.apache.james.imap.api.ImapCommand, org.apache.james.imap.decode.ImapRequestLineReader, java.lang.String, boolean, org.apache.james.imap.api.process.ImapSession, org.apache.james.imap.api.ImapMessageCallback)
     */
    protected void decode(ImapCommand command, ImapRequestLine request, String tag, boolean useUids, ImapSession session, ImapMessageCallback callback) {
        try {
            IdRange[] uidSet = null;
            if (useUids) {
                uidSet = parseIdRange(request);
            }
            endLine(request);
            final ImapMessage result = new ExpungeRequest(command, tag, uidSet);
            callback.onMessage(result);
        } catch (DecodingException ex) {
            callback.onException(ex);
        }

    }

}
