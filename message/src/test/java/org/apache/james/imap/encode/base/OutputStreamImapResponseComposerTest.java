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

package org.apache.james.imap.encode.base;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.List;

import javax.mail.Flags;

import org.apache.james.imap.api.ImapCommand;
import org.apache.james.imap.encode.AbstractTestImapResponseComposer;
import org.apache.james.imap.main.stream.OutputStreamImapResponseComposer;
import org.junit.Before;

public class OutputStreamImapResponseComposerTest extends
        AbstractTestImapResponseComposer {

    private OutputStreamImapResponseComposer composer;

    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    @Before
    public void setUp() throws Exception {
        composer = new OutputStreamImapResponseComposer(out);
    }


    protected byte[] encodeListResponse(String typeName, List<String> attributes,
            char hierarchyDelimiter, String name) throws Exception {
        composer.listResponse(typeName, attributes, hierarchyDelimiter, name);
        return out.toByteArray();
    }

    protected void clear() throws Exception {
        out.reset();
    }

    protected byte[] encodeSearchResponse(long[] ids) throws Exception {
        composer.searchResponse(ids);
        return out.toByteArray();
    }

    protected byte[] encodeFlagsResponse(Flags flags) throws Exception {
        composer.flags(flags);
        return out.toByteArray();
    }

    protected byte[] encodeStatusResponse(Long messages, Long recent,
            Long uidNext, Long uidValidity, Long unseen, String mailbox)
            throws Exception {
        composer.statusResponse(messages, recent, uidNext, uidValidity, unseen,
                mailbox);
        return out.toByteArray();
    }

    protected byte[] encodeStatusResponse(String tag, ImapCommand command,
            String type, String responseCode, Collection<String> parameters,
            int number, String text) throws Exception {
        composer.statusResponse(tag, command, type, responseCode, parameters,
                number, text);
        return out.toByteArray();
    }

}
