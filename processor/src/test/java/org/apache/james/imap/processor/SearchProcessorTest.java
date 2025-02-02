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

package org.apache.james.imap.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.mail.Flags.Flag;

import org.apache.james.imap.api.ImapCommand;
import org.apache.james.imap.api.ImapConstants;
import org.apache.james.imap.api.ImapSession;
import org.apache.james.imap.api.ImapSessionUtils;
import org.apache.james.imap.api.display.HumanReadableText;
import org.apache.james.imap.api.message.IdRange;
import org.apache.james.imap.api.message.request.DayMonthYear;
import org.apache.james.imap.api.message.request.SearchKey;
import org.apache.james.imap.api.message.response.StatusResponse;
import org.apache.james.imap.api.message.response.StatusResponseFactory;
import org.apache.james.imap.api.process.ImapProcessor;
import org.apache.james.imap.api.process.SelectedMailbox;
import org.apache.james.imap.message.request.SearchRequest;
import org.apache.james.imap.message.response.SearchResponse;
import org.apache.james.mailbox.MailboxManager;
import org.apache.james.mailbox.MailboxPath;
import org.apache.james.mailbox.MailboxSession;
import org.apache.james.mailbox.MessageManager;
import org.apache.james.mailbox.SearchQuery;
import org.apache.james.mailbox.SearchQuery.Criterion;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class SearchProcessorTest {
    private static final int DAY = 6;

    private static final int MONTH = 6;

    private static final int YEAR = 1944;

    private static final DayMonthYear DAY_MONTH_YEAR = new DayMonthYear(DAY,
            MONTH, YEAR);

    private static final long SIZE = 1729;

    private static final String KEYWORD = "BD3";

    private static final long[] EMPTY = {};

    private static final String TAG = "TAG";

    private static final String ADDRESS = "John Smith <john@example.org>";

    private static final String SUBJECT = "Myriad Harbour";

    private static final IdRange[] IDS = { new IdRange(1),
            new IdRange(42, 1048) };

    private static final SearchQuery.NumericRange[] RANGES = {
            new SearchQuery.NumericRange(1),
            new SearchQuery.NumericRange(42, 1048) };
    
    private static final MailboxPath mailboxPath = new MailboxPath("namespace", "user", "name");

    SearchProcessor processor;

    ImapProcessor next;

    ImapProcessor.Responder responder;

    ImapSession session;

    ImapCommand command;

    StatusResponseFactory serverResponseFactory;

    StatusResponse statusResponse;

    MessageManager mailbox;
    
    MailboxManager mailboxManager;
    
    MailboxSession mailboxSession;

    SelectedMailbox selectedMailbox;

    private Mockery mockery = new JUnit4Mockery();
    
    @Before
    public void setUp() throws Exception {
        serverResponseFactory = mockery.mock(StatusResponseFactory.class);
        session = mockery.mock(ImapSession.class);
        command = ImapCommand.anyStateCommand("Command");
        next = mockery.mock(ImapProcessor.class);
        responder = mockery.mock(ImapProcessor.Responder.class);
        statusResponse = mockery.mock(StatusResponse.class);
        mailbox = mockery.mock(MessageManager.class);
        mailboxManager = mockery.mock(MailboxManager.class);
        mailboxSession = mockery.mock(MailboxSession.class);
        selectedMailbox = mockery.mock(SelectedMailbox.class);
        
        processor = new SearchProcessor(next,  mailboxManager, serverResponseFactory);
        expectOk();
    }

    @Test
    public void testSequenceSetLowerUnlimited() throws Exception {
        expectsGetSelectedMailbox();
        final IdRange[] ids = { new IdRange(Long.MIN_VALUE, 1729) };
        final SearchQuery.NumericRange[] ranges = { new SearchQuery.NumericRange(
                Long.MAX_VALUE, 1729L) };
        mockery.checking(new Expectations() {{
            oneOf(selectedMailbox).uid(with(equal(1729)));will(returnValue(1729L));
        }});
        allowUnsolicitedResponses();
        
        check(SearchKey.buildSequenceSet(ids), SearchQuery.uid(ranges));
    }

    private void allowUnsolicitedResponses() {
        mockery.checking(new Expectations() {{
            atMost(1).of(session).getAttribute(
                    with(equal(ImapSessionUtils.MAILBOX_USER_ATTRIBUTE_SESSION_KEY)));will(returnValue("user"));
            atMost(1).of(session).getAttribute(
                    with(equal(ImapSessionUtils.MAILBOX_SESSION_ATTRIBUTE_SESSION_KEY)));will(returnValue(mailboxSession));
        }});
    }

    @Test
    public void testSequenceSetUpperUnlimited() throws Exception {
        expectsGetSelectedMailbox();
        final IdRange[] ids = { new IdRange(1, Long.MAX_VALUE) };
        final SearchQuery.NumericRange[] ranges = { new SearchQuery.NumericRange(
                42, Long.MAX_VALUE) };
        mockery.checking(new Expectations() {{
            oneOf(selectedMailbox).uid(with(equal(1)));will(returnValue(42L));

        }});
        allowUnsolicitedResponses();
        check(SearchKey.buildSequenceSet(ids), SearchQuery.uid(ranges));
    }

    @Test
    public void testSequenceSetMsnRange() throws Exception {
        expectsGetSelectedMailbox();
        final IdRange[] ids = { new IdRange(1, 5) };
        final SearchQuery.NumericRange[] ranges = { new SearchQuery.NumericRange(
                42, 1729) };
        mockery.checking(new Expectations() {{
            oneOf(selectedMailbox).uid(with(equal(1)));will(returnValue(42L));
            oneOf(selectedMailbox).uid(with(equal(5)));will(returnValue(1729L));
        }});
        allowUnsolicitedResponses();
        check(SearchKey.buildSequenceSet(ids), SearchQuery.uid(ranges));
    }

    @Test
    public void testSequenceSetSingleMsn() throws Exception {
        expectsGetSelectedMailbox();
        final IdRange[] ids = { new IdRange(1) };
        final SearchQuery.NumericRange[] ranges = { new SearchQuery.NumericRange(
                42) };
        mockery.checking(new Expectations() {{
            exactly(2).of(selectedMailbox).uid(with(equal(1)));will(returnValue(42L));
        }});
        allowUnsolicitedResponses();
        check(SearchKey.buildSequenceSet(ids), SearchQuery.uid(ranges));
    }

    @Test
    public void testALL() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildAll(), SearchQuery.all());
    }

    private void expectsGetSelectedMailbox() throws Exception {
        mockery.checking(new Expectations() {{
            atMost(1).of(mailboxManager).getMailbox(with(equal(mailboxPath)),  with(same(mailboxSession)));will(returnValue(mailbox));
            atMost(1).of(mailboxManager).getMailbox(with(equal(mailboxPath)), with(same(mailboxSession)));will(returnValue(mailbox));
            allowing(session).getSelected();will(returnValue(selectedMailbox));
            atMost(1).of(selectedMailbox).isRecentUidRemoved();will(returnValue(false));
            atLeast(1).of(selectedMailbox).isSizeChanged();will(returnValue(false));
            atLeast(1).of(selectedMailbox).getPath();will(returnValue(mailboxPath));
            atMost(1).of(selectedMailbox).flagUpdateUids();will(returnValue(Collections.EMPTY_LIST));
            atMost(1).of(selectedMailbox).resetEvents();
            
            oneOf(selectedMailbox).getRecent();will(returnValue(new ArrayList<Long>()));
        }});
    }

    @Test
    public void testANSWERED() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildAnswered(), SearchQuery.flagIsSet(Flag.ANSWERED));
    }

    @Test
    public void testBCC() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildBcc(ADDRESS), SearchQuery.headerContains(
                ImapConstants.RFC822_BCC, ADDRESS));
    }

    @Test
    public void testBEFORE() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildBefore(DAY_MONTH_YEAR), SearchQuery
                .internalDateBefore(DAY, MONTH, YEAR));
    }

    @Test
    public void testBODY() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildBody(SUBJECT), SearchQuery.bodyContains(SUBJECT));
    }

    @Test
    public void testCC() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildCc(ADDRESS), SearchQuery.headerContains(
                ImapConstants.RFC822_CC, ADDRESS));
    }

    @Test
    public void testDELETED() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildDeleted(), SearchQuery.flagIsSet(Flag.DELETED));
    }

    @Test
    public void testDRAFT() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildDraft(), SearchQuery.flagIsSet(Flag.DRAFT));
    }

    @Test
    public void testFLAGGED() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildFlagged(), SearchQuery.flagIsSet(Flag.FLAGGED));
    }

    @Test
    public void testFROM() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildFrom(ADDRESS), SearchQuery.headerContains(
                ImapConstants.RFC822_FROM, ADDRESS));
    }

    @Test
    public void testHEADER() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildHeader(ImapConstants.RFC822_IN_REPLY_TO, ADDRESS),
                SearchQuery.headerContains(ImapConstants.RFC822_IN_REPLY_TO,
                        ADDRESS));
    }

    @Test
    public void testKEYWORD() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildKeyword(KEYWORD), SearchQuery.flagIsSet(KEYWORD));
    }

    @Test
    public void testLARGER() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildLarger(SIZE), SearchQuery.sizeGreaterThan(SIZE));
    }

    @Test
    public void testNEW() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildNew(), SearchQuery.and(SearchQuery
                .flagIsSet(Flag.RECENT), SearchQuery.flagIsUnSet(Flag.SEEN)));
    }

    @Test
    public void testNOT() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildNot(SearchKey.buildOn(DAY_MONTH_YEAR)),
                SearchQuery.not(SearchQuery.internalDateOn(DAY, MONTH, YEAR)));
    }


    @Test
    public void testOLD() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildOld(), SearchQuery.flagIsUnSet(Flag.RECENT));
    }

    @Test
    public void testON() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildOn(DAY_MONTH_YEAR), SearchQuery.internalDateOn(
                DAY, MONTH, YEAR));
    }

    @Test
    public void testAND() throws Exception {
        expectsGetSelectedMailbox();
        List<SearchKey> keys = new ArrayList<SearchKey>();
        keys.add(SearchKey.buildOn(DAY_MONTH_YEAR));
        keys.add(SearchKey.buildOld());
        keys.add(SearchKey.buildLarger(SIZE));
        List<Criterion> criteria = new ArrayList<Criterion>();
        criteria.add(SearchQuery.internalDateOn(DAY, MONTH, YEAR));
        criteria.add(SearchQuery.flagIsUnSet(Flag.RECENT));
        criteria.add(SearchQuery.sizeGreaterThan(SIZE));
        check(SearchKey.buildAnd(keys), SearchQuery.and(criteria));
    }

    @Test
    public void testOR() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildOr(SearchKey.buildOn(DAY_MONTH_YEAR), SearchKey
                .buildOld()), SearchQuery.or(SearchQuery.internalDateOn(DAY,
                MONTH, YEAR), SearchQuery.flagIsUnSet(Flag.RECENT)));
    }

    @Test
    public void testRECENT() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildRecent(), SearchQuery.flagIsSet(Flag.RECENT));
    }
    
    @Test
    public void testSEEN() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildSeen(), SearchQuery.flagIsSet(Flag.SEEN));
    }

    @Test
    public void testSENTBEFORE() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildSentBefore(DAY_MONTH_YEAR), SearchQuery
                .headerDateBefore(ImapConstants.RFC822_DATE, DAY, MONTH, YEAR));
    }

    @Test
    public void testSENTON() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildSentOn(DAY_MONTH_YEAR), SearchQuery.headerDateOn(
                ImapConstants.RFC822_DATE, DAY, MONTH, YEAR));
    }
    @Test
    public void testSENTSINCE() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildSentSince(DAY_MONTH_YEAR), SearchQuery
                .headerDateAfter(ImapConstants.RFC822_DATE, DAY, MONTH, YEAR));
    }

    @Test
    public void testSINCE() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildSince(DAY_MONTH_YEAR), SearchQuery
                .internalDateAfter(DAY, MONTH, YEAR));
    }

    @Test
    public void testSMALLER() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildSmaller(SIZE), SearchQuery.sizeLessThan(SIZE));
    }

    @Test
    public void testSUBJECT() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildSubject(SUBJECT), SearchQuery.headerContains(
                ImapConstants.RFC822_SUBJECT, SUBJECT));
    }

    @Test
    public void testTEXT() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildText(SUBJECT), SearchQuery.mailContains(SUBJECT));
    }

    @Test
    public void testTO() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildTo(ADDRESS), SearchQuery.headerContains(
                ImapConstants.RFC822_TO, ADDRESS));
    }

    @Test
    public void testUID() throws Exception {
    	mockery.checking(new Expectations() {{

    		atLeast(1).of(selectedMailbox).getFirstUid();will(returnValue(1L));
    		atLeast(1).of(selectedMailbox).getLastUid();will(returnValue(1048L));
            }});
    	
        expectsGetSelectedMailbox();            
        
        
        check(SearchKey.buildUidSet(IDS), SearchQuery.uid(RANGES));
    }

    @Test
    public void testUNANSWERED() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildUnanswered(), SearchQuery
                .flagIsUnSet(Flag.ANSWERED));
    }

    @Test
    public void testUNDELETED() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildUndeleted(), SearchQuery.flagIsUnSet(Flag.DELETED));
    }

    @Test
    public void testUNDRAFT() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildUndraft(), SearchQuery.flagIsUnSet(Flag.DRAFT));
    }

    @Test
    public void testUNFLAGGED() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildUnflagged(), SearchQuery.flagIsUnSet(Flag.FLAGGED));
    }

    @Test
    public void testUNKEYWORD() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildUnkeyword(KEYWORD), SearchQuery
                .flagIsUnSet(KEYWORD));
    }

    @Test
    public void testUNSEEN() throws Exception {
        expectsGetSelectedMailbox();
        check(SearchKey.buildUnseen(), SearchQuery.flagIsUnSet(Flag.SEEN));
    }

   
    private void check(SearchKey key, SearchQuery.Criterion criterion)
            throws Exception {
        SearchQuery query = new SearchQuery();
        query.andCriteria(criterion);
        check(key, query);
    }

    private void check(final SearchKey key, final SearchQuery query) throws Exception {        
        mockery.checking(new Expectations() {{
            allowing(session).getAttribute(
                    with(equal(ImapSessionUtils.MAILBOX_SESSION_ATTRIBUTE_SESSION_KEY))); will(returnValue((MailboxSession) mailboxSession));
            oneOf(mailbox).search(
                    with(equal(query)),
                    with(equal(mailboxSession)));will(
                            returnValue(new ArrayList<Long>().iterator()));
            oneOf(responder).respond(with(equal(new SearchResponse(EMPTY))));
            
        }});
        SearchRequest message = new SearchRequest(command, key, false, TAG);
        processor.doProcess(message, session, TAG, command, responder);
    }

    private void expectOk() {
        mockery.checking(new Expectations() {{
            oneOf(serverResponseFactory).taggedOk(
                    with(equal(TAG)),
                    with(same(command)), 
                    with(equal(HumanReadableText.COMPLETED)));will(returnValue(statusResponse));    
            oneOf(responder).respond(with(same(statusResponse)));
        }});
    }
}
