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

package org.apache.james.imap.encode;

import java.io.IOException;

import org.apache.james.imap.api.ImapMessage;
import org.apache.james.imap.api.ImapSession;
import org.apache.james.imap.api.message.response.ImapResponseComposer;
import org.apache.james.imap.encode.base.AbstractChainedImapEncoder;
import org.apache.james.imap.message.response.ExpungeResponse;

public class ExpungeResponseEncoder extends AbstractChainedImapEncoder {

    public ExpungeResponseEncoder(final ImapEncoder next) {
        super(next);
    }

    public boolean isAcceptable(ImapMessage message) {
        return (message instanceof ExpungeResponse);
    }

    protected void doEncode(ImapMessage acceptableMessage,
            ImapResponseComposer composer, ImapSession session) throws IOException {
        final ExpungeResponse expungeResponse = (ExpungeResponse) acceptableMessage;
        final int messageSequenceNumber = expungeResponse
                .getMessageSequenceNumber();
        composer.expungeResponse(messageSequenceNumber);
    }
}
