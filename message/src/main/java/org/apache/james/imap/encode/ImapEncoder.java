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

/**
 * TODO: correct this API
 */
public interface ImapEncoder {

    /**
     * Writes response. TODO: pass in writer rather than composer
     * 
     * @param message
     *            <code>ImapMessage</code>, not null
     * @param composer
     *            <code>ImapResponseComposer</code>, not null
     * @param session TODO
     * @throws IOException when message encoding fails
     */
    void encode(ImapMessage message, ImapResponseComposer composer, ImapSession session)
            throws IOException;
}
