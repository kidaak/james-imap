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

package org.apache.james.imap.api.process;

import org.apache.james.imap.api.ImapMessage;
import org.apache.james.imap.api.ImapSession;
import org.apache.james.imap.api.message.response.ImapResponseMessage;

/**
 * <p>
 * Processable IMAP command.
 * </p>
 * <p>
 * <strong>Note:</strong> this is a transitional API and is liable to change.
 * </p>
 */
public interface ImapProcessor {

    /**
     * Performs processing of the command. If this processor does not understand
     * the given message then it must return an appropriate message as per the
     * specification. RuntimeException should not be thrown in this
     * circumstance.
     * 
     * @param <code>ImapMessage</code>, not null
     * @param session
     *            <code>ImapSession</code>
     * @return response, not null
     */
    public void process(ImapMessage message, Responder responder,
            ImapSession session);

    /**
     * Response message sink.
     */
    public interface Responder {
        /**
         * Writes the given response.
         * 
         * @param message
         *            <code>ImapResponseMessage</code>, not null
         */
        public void respond(ImapResponseMessage message);
    }
}
