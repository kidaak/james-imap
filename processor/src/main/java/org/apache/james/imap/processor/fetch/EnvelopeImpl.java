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

/**
 * 
 */
package org.apache.james.imap.processor.fetch;

import org.apache.james.imap.message.response.FetchResponse;

final class EnvelopeImpl implements FetchResponse.Envelope {

    private final Address[] bcc;

    private final Address[] cc;

    private final String date;

    private final Address[] from;

    private final String inReplyTo;

    private final String messageId;

    private final Address[] replyTo;

    private final Address[] sender;

    private final String subject;

    private final Address[] to;

    public EnvelopeImpl(final String date, final String subject,
            final Address[] from, final Address[] sender,
            final Address[] replyTo, final Address[] to, final Address[] cc,
            final Address[] bcc, final String inReplyTo, final String messageId) {
        super();
        this.bcc = bcc;
        this.cc = cc;
        this.date = date;
        this.from = from;
        this.inReplyTo = inReplyTo;
        this.messageId = messageId;
        this.replyTo = replyTo;
        this.sender = sender;
        this.subject = subject;
        this.to = to;
    }

    public Address[] getBcc() {
        return bcc;
    }

    public Address[] getCc() {
        return cc;
    }

    public String getDate() {
        return date;
    }

    public Address[] getFrom() {
        return from;
    }

    public String getInReplyTo() {
        return inReplyTo;
    }

    public String getMessageId() {
        return messageId;
    }

    public Address[] getReplyTo() {
        return replyTo;
    }

    public Address[] getSender() {
        return sender;
    }

    public String getSubject() {
        return subject;
    }

    public Address[] getTo() {
        return to;
    }
}