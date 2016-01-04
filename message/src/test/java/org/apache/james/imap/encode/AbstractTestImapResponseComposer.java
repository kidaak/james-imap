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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.mail.Flags;

import org.apache.james.imap.api.ImapCommand;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public abstract class AbstractTestImapResponseComposer {

    private static final long[] ONE_TWO_THREE = { 1, 2, 3 };

    private static final long[] FIBS = { 1, 1, 2, 3, 5, 8, 13, 21, 34, 65, 99 };

    private static final long[] EMPTY = {};


    @Test
    public void testSearch() throws Exception {
        checkSearchResponseEncode("* SEARCH 1 2 3\r\n", ONE_TWO_THREE);
        checkSearchResponseEncode("* SEARCH 1 1 2 3 5 8 13 21 34 65 99\r\n",
                FIBS);
        checkSearchResponseEncode("* SEARCH\r\n", EMPTY);
    }
    
    @Test
    public void testQuotedDelimiter() throws Exception {
        checkListResponseEncode("* LSUB () \"\\\"\" \"#news\"\r\n", "LSUB",
                null, '\"', "#news");
        checkListResponseEncode("* LIST () \"\\\"\" \"#INBOX\"\r\n", "LIST",
                null, '\"', "#INBOX");
        checkListResponseEncode("* LSUB () \"\\\\\" \"#news\"\r\n", "LSUB",
                null, '\\', "#news");
        checkListResponseEncode("* LIST () \"\\\\\" \"#INBOX\"\r\n", "LIST",
                null, '\\', "#INBOX");
    }
    
    @Test
    public void testNilDelimiter() throws Exception {
        checkListResponseEncode("* LSUB () NIL \"#news\"\r\n", "LSUB", null,
                (char)Character.UNASSIGNED, "#news");
        checkListResponseEncode("* LIST () NIL \"#INBOX\"\r\n", "LIST", null,
        		(char)Character.UNASSIGNED, "#INBOX");
    }

    @Test
    public void testSimple() throws Exception {
        checkListResponseEncode("* LSUB () \".\" \"#news\"\r\n", "LSUB", null,
                '.', "#news");
        checkListResponseEncode("* LIST () \".\" \"#INBOX\"\r\n", "LIST", null,
                '.', "#INBOX");
        checkListResponseEncode("* LSUB () \".\" \"#news.sub\"\r\n", "LSUB",
                null, '.', "#news.sub");
        checkListResponseEncode("* LIST () \".\" \"#INBOX.sub\"\r\n", "LIST",
                null, '.', "#INBOX.sub");
    }

    @Test
    public void testSpecialNames() throws Exception {
        checkListResponseEncode(
                "* LSUB () \"\\\\\" \"#news\\\\sub\\\\directory\"\r\n", "LSUB",
                null, '\\', "#news\\sub\\directory");
        checkListResponseEncode(
                "* LIST () \"\\\\\" \"#INBOX\\\\sub\\\\directory\"\r\n",
                "LIST", null, '\\', "#INBOX\\sub\\directory");
        checkListResponseEncode(
                "* LSUB () \".\" \"#news.sub directory.what\"\r\n", "LSUB",
                null, '.', "#news.sub directory.what");
        checkListResponseEncode(
                "* LIST () \".\" \"#INBOX.sub directory.what\"\r\n", "LIST",
                null, '.', "#INBOX.sub directory.what");
        checkListResponseEncode(
                "* LSUB () \".\" \"#news.\\\"sub directory\\\".what\"\r\n",
                "LSUB", null, '.', "#news.\"sub directory\".what");
        checkListResponseEncode(
                "* LIST () \".\" \"#INBOX.\\\"sub directory\\\".what\"\r\n",
                "LIST", null, '.', "#INBOX.\"sub directory\".what");
    }

    @Test
    public void testAttributes() throws Exception {
        List<String> attributes = new ArrayList<String>();
        attributes.add("\\one");
        attributes.add("\\two");
        attributes.add("\\three");
        attributes.add("\\four");
        checkListResponseEncode(
                "* LSUB (\\one \\two \\three \\four) \".\" \"#news\"\r\n",
                "LSUB", attributes, '.', "#news");
        checkListResponseEncode(
                "* LIST (\\one \\two \\three \\four) \".\" \"#INBOX\"\r\n",
                "LIST", attributes, '.', "#INBOX");
    }

    @Test
    public void testEncodeStatus() throws Exception {
        checkStatusResponseEncode(
                "* STATUS \"#INBOX.\\\"sub directory\\\".what\" (MESSAGES 3 RECENT 5 UIDNEXT 7 UIDVALIDITY 11 UNSEEN 13)\r\n",
                new Long(3), new Long(5), new Long(7), new Long(11), new Long(
                        13), "#INBOX.\"sub directory\".what");
        checkStatusResponseEncode("* STATUS \"#INBOX\" (MESSAGES 42)\r\n",
                new Long(42), null, null, null, null, "#INBOX");
        checkStatusResponseEncode("* STATUS \"#INBOX\" (RECENT 42)\r\n", null,
                new Long(42), null, null, null, "#INBOX");
        checkStatusResponseEncode("* STATUS \"#INBOX\" (UIDNEXT 42)\r\n", null,
                null, new Long(42), null, null, "#INBOX");
        checkStatusResponseEncode("* STATUS \"#INBOX\" (UIDVALIDITY 42)\r\n",
                null, null, null, new Long(42), null, "#INBOX");
        checkStatusResponseEncode("* STATUS \"#INBOX\" (UNSEEN 42)\r\n", null,
                null, null, null, new Long(42), "#INBOX");
    }

    @Test
    public void testShouldEncodeFlagsCorrectly() throws Exception {
        checkFlagsEncode(" FLAGS (\\Seen)", new Flags(Flags.Flag.SEEN));
        checkFlagsEncode(" FLAGS (\\Recent)", new Flags(Flags.Flag.RECENT));
        checkFlagsEncode(" FLAGS (\\Draft)", new Flags(Flags.Flag.DRAFT));
        checkFlagsEncode(" FLAGS (\\Answered)", new Flags(Flags.Flag.ANSWERED));
        checkFlagsEncode(" FLAGS (\\Flagged)", new Flags(Flags.Flag.FLAGGED));
        checkFlagsEncode(" FLAGS (\\Deleted)", new Flags(Flags.Flag.DELETED));
        Flags flags = new Flags();
        flags.add(Flags.Flag.SEEN);
        flags.add(Flags.Flag.ANSWERED);
        flags.add(Flags.Flag.FLAGGED);
        flags.add(Flags.Flag.DELETED);
        flags.add(Flags.Flag.SEEN);
        flags.add(Flags.Flag.DRAFT);
        checkFlagsEncode(
                " FLAGS (\\Answered \\Deleted \\Draft \\Flagged \\Seen)", flags);
    }

    @Test
    public void testShouldEncodeUnparameterisedStatus() throws Exception {
        checkStatusResponseEncode("A1 NO [ALERT] APPEND failed\r\n", "A1",
                command("APPEND"), "NO", "ALERT", new ArrayList<String>(), 0,
                "failed");
        checkStatusResponseEncode("A1 BAD [TRYCREATE] SELECT whatever\r\n",
                "A1", command("SELECT"), "BAD", "TRYCREATE",
                new ArrayList<String>(), 0, "whatever");
    }

    @Test
    public void testShouldEncodeListParameterStatus() throws Exception {
        Collection<String> parameters = new ArrayList<String>();
        parameters.add("ONE");
        parameters.add("TWO");
        parameters.add("THREE");
        checkStatusResponseEncode(
                "A1 NO [BADCHARSET (ONE TWO THREE)] APPEND failed\r\n", "A1",
                command("APPEND"), "NO", "BADCHARSET", parameters, 0, "failed");
    }

    @Test
    public void testShouldEncodeNumberParameterStatus() throws Exception {
        checkStatusResponseEncode("A1 NO [UIDNEXT 10] APPEND failed\r\n", "A1",
                command("APPEND"), "NO", "UIDNEXT", null, 10, "failed");
    }

    private void checkFlagsEncode(String expected, Flags flags)
            throws Exception {
        StringBuffer buffer = new StringBuffer();
        byte[] output = encodeFlagsResponse(flags);
        for (int i = 0; i < output.length; i++) {
            buffer.append((char) output[i]);
        }
        assertEquals(expected, buffer.toString());
        clear();
    }

    protected abstract byte[] encodeFlagsResponse(Flags flags) throws Exception;

    private void checkSearchResponseEncode(String expected, long[] ids)
            throws Exception {
        StringBuffer buffer = new StringBuffer();
        byte[] output = encodeSearchResponse(ids);
        for (int i = 0; i < output.length; i++) {
            buffer.append((char) output[i]);
        }
        assertEquals(expected, buffer.toString());
        clear();
    }

    protected abstract byte[] encodeSearchResponse(long[] ids) throws Exception;

    private void checkListResponseEncode(String expected, String typeName,
            List<String> attributes, char hierarchyDelimiter, String name)
            throws Exception {
        StringBuffer buffer = new StringBuffer();
        byte[] output = encodeListResponse(typeName, attributes,
                hierarchyDelimiter, name);
        for (int i = 0; i < output.length; i++) {
            buffer.append((char) output[i]);
        }
        assertEquals(expected, buffer.toString());
        clear();
    }

    protected abstract byte[] encodeListResponse(String typeName,
            List<String> attributes, char hierarchyDelimiter, String name)
            throws Exception;

    private void checkStatusResponseEncode(String expected, Long messages,
            Long recent, Long uidNext, Long uidValidity, Long unseen,
            String mailbox) throws Exception {
        StringBuffer buffer = new StringBuffer();
        byte[] output = encodeStatusResponse(messages, recent, uidNext,
                uidValidity, unseen, mailbox);
        for (int i = 0; i < output.length; i++) {
            buffer.append((char) output[i]);
        }
        assertEquals(expected, buffer.toString());
        clear();
    }

    protected abstract byte[] encodeStatusResponse(Long messages, Long recent,
            Long uidNext, Long uidValidity, Long unseen, String mailbox)
            throws Exception;

    protected abstract void clear() throws Exception;

    protected abstract byte[] encodeStatusResponse(String tag,
            ImapCommand command, String type, String responseCode,
            Collection<String> parameters, int number, String text) throws Exception;

    private void checkStatusResponseEncode(String expected, String tag,
            ImapCommand command, String type, String responseCode,
            Collection<String> parameters, int number, String text) throws Exception {
        StringBuffer buffer = new StringBuffer();
        byte[] output = encodeStatusResponse(tag, command, type, responseCode,
                parameters, number, text);
        for (int i = 0; i < output.length; i++) {
            buffer.append((char) output[i]);
        }
        assertEquals(expected, buffer.toString());
        clear();
    }

    private ImapCommand command(final String name) {
        return ImapCommand.anyStateCommand(name);
    }
}
