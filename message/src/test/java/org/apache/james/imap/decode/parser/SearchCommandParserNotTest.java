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

import static org.junit.Assert.assertEquals;

import org.apache.james.imap.api.ImapCommand;
import org.apache.james.imap.api.ImapMessage;
import org.apache.james.imap.api.message.IdRange;
import org.apache.james.imap.api.message.request.DayMonthYear;
import org.apache.james.imap.api.message.request.SearchKey;
import org.apache.james.imap.decode.ImapRequestLine;
import org.apache.james.imap.encode.MockImapResponseComposer;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class SearchCommandParserNotTest {

    SearchCommandParser parser;

    ImapCommand command;

    ImapMessage message;

    private Mockery context = new JUnit4Mockery();
    
    @Before
    public void setUp() throws Exception {
        parser = new SearchCommandParser();
        command = ImapCommand.anyStateCommand("Command");
        message = context.mock(ImapMessage.class);
    }

    @Test
    public void testShouldParseNotSequence() throws Exception {
        IdRange[] range = { new IdRange(Long.MIN_VALUE, 100), new IdRange(110),
                new IdRange(200, 201), new IdRange(400, Long.MAX_VALUE) };
        SearchKey notdKey = SearchKey.buildSequenceSet(range);
        SearchKey key = SearchKey.buildNot(notdKey);
        checkValid("NOT *:100,110,200:201,400:*\r\n", key);
    }

    @Test
    public void testShouldParseNotUid() throws Exception {
        IdRange[] range = { new IdRange(Long.MIN_VALUE, 100), new IdRange(110),
                new IdRange(200, 201), new IdRange(400, Long.MAX_VALUE) };
        SearchKey notdKey = SearchKey.buildUidSet(range);
        SearchKey key = SearchKey.buildNot(notdKey);
        checkValid("NOT UID *:100,110,200:201,400:*\r\n", key);
    }

    @Test
    public void testShouldParseNotHeaderKey() throws Exception {
        SearchKey notdKey = SearchKey.buildHeader("FROM", "Smith");
        SearchKey key = SearchKey.buildNot(notdKey);
        checkValid("NOT HEADER FROM Smith\r\n", key);
        checkValid("NOT header FROM Smith\r\n", key);
    }

    @Test
    public void testShouldParseNotDateParameterKey() throws Exception {
        SearchKey notdKey = SearchKey.buildSince(new DayMonthYear(11, 1, 2001));
        SearchKey key = SearchKey.buildNot(notdKey);
        checkValid("NOT since 11-Jan-2001\r\n", key);
        checkValid("NOT SINCE 11-Jan-2001\r\n", key);
    }

    @Test
    public void testShouldParseNotStringParameterKey() throws Exception {
        SearchKey notdKey = SearchKey.buildFrom("Smith");
        SearchKey key = SearchKey.buildNot(notdKey);
        checkValid("NOT FROM Smith\r\n", key);
        checkValid("NOT FROM \"Smith\"\r\n", key);
    }

    @Test
    public void testShouldParseNotStringQuotedParameterKey() throws Exception {
        SearchKey notdKey = SearchKey.buildFrom("Smith And Jones");
        SearchKey key = SearchKey.buildNot(notdKey);
        checkValid("NOT FROM \"Smith And Jones\"\r\n", key);
    }

    @Test
    public void testShouldParseNotNoParameterKey() throws Exception {
        SearchKey notdKey = SearchKey.buildNew();
        SearchKey key = SearchKey.buildNot(notdKey);
        checkValid("NOT NEW\r\n", key);
        checkValid("Not NEW\r\n", key);
        checkValid("not new\r\n", key);
    }

    private void checkValid(String input, final SearchKey key) throws Exception {
        ImapRequestLine reader = new ImapRequestLine(input.getBytes("US-ASCII"),
                new MockImapResponseComposer());

        assertEquals(key, parser.searchKey(reader, null, false));
    }
}
