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
package org.apache.james.imap.decode.main;

import static org.junit.Assert.assertEquals;
import junit.framework.Assert;

import org.apache.james.imap.api.DecodingException;
import org.apache.james.imap.api.ImapCommand;
import org.apache.james.imap.api.ImapConstants;
import org.apache.james.imap.api.ImapMessageCallback;
import org.apache.james.imap.api.ImapSession;
import org.apache.james.imap.api.message.IdRange;
import org.apache.james.imap.decode.ImapRequestLine;
import org.apache.james.imap.decode.base.AbstractImapCommandParser;
import org.apache.james.imap.encode.MockImapResponseComposer;
import org.junit.Test;

public class IdRangeParseTest  {

	private final AbstractImapCommandParser parser = new AbstractImapCommandParser(null) {

        /*
         * (non-Javadoc)
         * @see org.apache.james.imap.decode.base.AbstractImapCommandParser#decode(org.apache.james.imap.api.ImapCommand, org.apache.james.imap.decode.ImapRequestLineReader, java.lang.String, org.apache.james.imap.api.process.ImapSession, org.apache.james.imap.api.ImapMessageCallback)
         */
        protected void decode(ImapCommand command, ImapRequestLine request, String tag, ImapSession session, ImapMessageCallback callback) {
            // TODO Auto-generated method stub
            
        }

		
	};
	
	
	/**
	 * Test for https://issues.apache.org/jira/browse/IMAP-212
	 * @throws DecodingException
	 */
	@Test
	public void testRangeInRandomOrder() throws DecodingException {
		int val1 = 1;
		int val2 = 3;
		
		IdRange[] ranges1 = ranges(rangeAsString(val1, val2));
		assertEquals(1, ranges1.length);
		assertEquals(val1, ranges1[0].getLowVal());
		assertEquals(val2, ranges1[0].getHighVal());
		
		IdRange[] ranges2 = ranges(rangeAsString(val2, val1));
		assertEquals(1, ranges2.length);
		assertEquals(val1, ranges2[0].getLowVal());
		assertEquals(val2, ranges2[0].getHighVal());
	}
	
	@Test
	public void testRangeUnsigned() throws DecodingException {
		int val1 = 1;
		
		try {
			ranges(rangeAsString(0, val1));
			Assert.fail();
		} catch (DecodingException e) {
			// number smaller then 1 should not work
		}
	
		
		try {
			ranges(rangeAsString(Long.MAX_VALUE, val1));
			Assert.fail();
		} catch (DecodingException e) {
			// number smaller then 1 should not work
		}
		
		IdRange[] ranges2 = ranges(rangeAsString(ImapConstants.MIN_NZ_NUMBER, ImapConstants.MAX_NZ_NUMBER));
		assertEquals(1, ranges2.length);
		assertEquals(ImapConstants.MIN_NZ_NUMBER, ranges2[0].getLowVal());
		assertEquals(ImapConstants.MAX_NZ_NUMBER, ranges2[0].getHighVal());
		
	}
	
	private String rangeAsString(long val1, long val2) {
		return val1 + ":" + val2;
	}
	
	private IdRange[] ranges(String rangesAsString) throws DecodingException {

        ImapRequestLine reader = new ImapRequestLine((rangesAsString + "\r\n").getBytes(),
                new MockImapResponseComposer());
        
		return parser.parseIdRange(reader);
	}
}
