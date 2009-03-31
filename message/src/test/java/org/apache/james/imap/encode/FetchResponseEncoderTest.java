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

import javax.mail.Flags;

import org.apache.james.imap.api.ImapCommand;
import org.apache.james.imap.api.ImapMessage;
import org.apache.james.imap.encode.FetchResponseEncoder;
import org.apache.james.imap.encode.ImapEncoder;
import org.apache.james.imap.encode.ImapResponseComposer;
import org.apache.james.imap.message.response.FetchResponse;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.integration.junit3.MockObjectTestCase;

public class FetchResponseEncoderTest extends MockObjectTestCase {

    Flags flags;

    ImapResponseComposer composer;


    ImapEncoder mockNextEncoder;

    FetchResponseEncoder encoder;

    ImapCommand stubCommand;

    protected void setUp() throws Exception {
        super.setUp();
        composer = mock(ImapResponseComposer.class);
        mockNextEncoder = mock(ImapEncoder.class);
        encoder = new FetchResponseEncoder(mockNextEncoder);
        stubCommand = ImapCommand.anyStateCommand("COMMAND");
        flags = new Flags(Flags.Flag.DELETED);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testShouldNotAcceptUnknownResponse() throws Exception {
        assertFalse(encoder.isAcceptable(mock(ImapMessage.class)));
    }

    public void testShouldAcceptFetchResponse() throws Exception {
        assertTrue(encoder.isAcceptable(new FetchResponse(11, null, null, null,
                null, null, null, null, null)));
    }

    public void testShouldEncodeFlagsResponse() throws Exception {
        FetchResponse message = new FetchResponse(100, flags, null, null, null,
                null, null, null, null);
        checking(new Expectations() {{
            final Sequence sequence = sequence("composition");
            oneOf(composer).openFetchResponse(with(equal(100L))); inSequence(sequence);
            oneOf(composer).flags(with(equal(flags))); inSequence(sequence);
            oneOf(composer).closeFetchResponse(); inSequence(sequence);
        }});
        encoder.doEncode(message, composer, new FakeImapSession());
    }

    public void testShouldEncodeUidResponse() throws Exception {
        FetchResponse message = new FetchResponse(100, null, new Long(72),
                null, null, null, null, null, null);
        checking(new Expectations() {{
            final Sequence sequence = sequence("composition");
            oneOf(composer).openFetchResponse(with(equal(100L))); inSequence(sequence);
            oneOf(composer).message(with(equal("UID"))); inSequence(sequence);
            oneOf(composer).message(with(equal(72L))); inSequence(sequence);
            oneOf(composer).closeFetchResponse(); inSequence(sequence);
        }});
        encoder.doEncode(message, composer, new FakeImapSession());
    }

    public void testShouldEncodeAllResponse() throws Exception {
        FetchResponse message = new FetchResponse(100, flags, new Long(72),
                null, null, null, null, null, null);
        checking(new Expectations() {{
            final Sequence sequence = sequence("composition");
            oneOf(composer).openFetchResponse(with(equal(100L))); inSequence(sequence);
            oneOf(composer).flags(with(equal(flags))); inSequence(sequence);
            oneOf(composer).message(with(equal("UID"))); inSequence(sequence);
            oneOf(composer).message(with(equal(72L))); inSequence(sequence);
            oneOf(composer).closeFetchResponse(); inSequence(sequence);
        }});
        encoder.doEncode(message, composer, new FakeImapSession());
    }
}
