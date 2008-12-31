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

package org.apache.james.imap.jpa;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import junit.framework.TestCase;

public class PartContentBuilderMultipartAlternativeTest extends TestCase {

    private static final String CONTENT_TYPE_PLAIN = "text/plain;charset=us-ascii";

    private static final String CONTENT_TYPE_HTML = "text/html;charset=us-ascii";

    private static final String CONTENT_TYPE_XHTML = "application/xhtml;charset=us-ascii";

    private static final String CONTENT_TYPE = "Content-Type";

    private static final String ALT_PLAIN_BODY = "Rhubarb!Rhubard!Rhubard!\r\n";

    private static final String ALT_XHTML_BODY = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Strict//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd'>"
            + "<html><head><title>Rhubard!</title></head><body><p>Rhubarb!Rhubard!Rhubard!</p></body></html>\r\n";

    private static final String ALT_HTML_BODY = "<html><head><title>Rhubard!</title></head><body><p>Rhubarb!Rhubard!Rhubard!</p></body></html>\r\n";

    private static final String ALT_PART_XHTML = CONTENT_TYPE + ": "
            + CONTENT_TYPE_XHTML + "\r\n" + "\r\n" + ALT_XHTML_BODY;

    private static final String ALT_PART_HTML = CONTENT_TYPE + ": "
            + CONTENT_TYPE_HTML + "\r\n" + "\r\n" + ALT_HTML_BODY;

    private static final String ALT_PART_PLAIN = CONTENT_TYPE + ": "
            + CONTENT_TYPE_PLAIN + "\r\n" + "\r\n" + ALT_PLAIN_BODY;

    private static final String MULTIPART_ALTERNATIVE = "From: Samual Smith <samual@example.org>\r\n"
            + "To: John Smith <john@example.org>\r\n"
            + "Date: Sun, 10 Feb 2008 08:00:00 -0800 (PST)\r\n"
            + "Subject: Rhubarb!\r\n"
            + "Content-Type: multipart/alternative;boundary=4242\r\n"
            + "\r\n"
            + "--4242\r\n"
            + ALT_PART_PLAIN
            + "\r\n--4242\r\n"
            + ALT_PART_HTML
            + "\r\n--4242\r\n" + ALT_PART_XHTML + "\r\n--4242\r\n";

    PartContentBuilder builder;

    protected void setUp() throws Exception {
        super.setUp();
        builder = new PartContentBuilder();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testShouldLocatePartsOfMultipartAlterative() throws Exception {
        assertEquals(ALT_PLAIN_BODY, bodyContent(MULTIPART_ALTERNATIVE, 1));
        assertEquals(ALT_HTML_BODY, bodyContent(MULTIPART_ALTERNATIVE, 2));
        assertEquals(ALT_XHTML_BODY, bodyContent(MULTIPART_ALTERNATIVE, 3));
    }

    public void testShouldLocateHeadersOfMultipartAlterative() throws Exception {
        checkContentType(CONTENT_TYPE_PLAIN, MULTIPART_ALTERNATIVE, 1);
        checkContentType(CONTENT_TYPE_HTML, MULTIPART_ALTERNATIVE, 2);
        checkContentType(CONTENT_TYPE_XHTML, MULTIPART_ALTERNATIVE, 3);
    }

    public void testShouldLocateFullContentOfMultipartAlterative()
            throws Exception {
        assertEquals(ALT_PART_PLAIN, fullContent(MULTIPART_ALTERNATIVE, 1));
        assertEquals(ALT_PART_HTML, fullContent(MULTIPART_ALTERNATIVE, 2));
        assertEquals(ALT_PART_XHTML, fullContent(MULTIPART_ALTERNATIVE, 3));
    }

    private String fullContent(String mail, int position) throws Exception {
        InputStream in = new ByteArrayInputStream(Charset.forName("us-ascii")
                .encode(mail).array());
        builder.parse(in);
        builder.to(position);
        StringBuffer buffer = new StringBuffer();
        builder.getFullContent().writeTo(buffer);
        return buffer.toString();
    }

    private String bodyContent(String mail, int position) throws Exception {
        InputStream in = new ByteArrayInputStream(Charset.forName("us-ascii")
                .encode(mail).array());
        builder.parse(in);
        builder.to(position);
        StringBuffer buffer = new StringBuffer();
        builder.getMimeBodyContent().writeTo(buffer);
        return buffer.toString();
    }

    private void checkContentType(String contentType, String mail, int position)
            throws Exception {
        List headers = headers(mail, position);
        assertEquals(1, headers.size());
        ResultHeader header = (ResultHeader) headers.get(0);
        assertEquals(CONTENT_TYPE, header.getName());
        assertEquals(contentType, header.getValue());
    }

    private List headers(String mail, int position) throws Exception {
        InputStream in = new ByteArrayInputStream(Charset.forName("us-ascii")
                .encode(mail).array());
        builder.parse(in);
        builder.to(position);
        return builder.getMimeHeaders();
    }
}