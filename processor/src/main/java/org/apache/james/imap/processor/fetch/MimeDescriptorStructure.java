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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.james.imap.message.response.FetchResponse;
import org.apache.james.imap.message.response.FetchResponse.Envelope;
import org.apache.james.imap.message.response.FetchResponse.Structure;
import org.apache.james.mailbox.MailboxException;
import org.apache.james.mailbox.MimeDescriptor;
import org.apache.james.mime4j.field.address.parser.ParseException;

final class MimeDescriptorStructure implements FetchResponse.Structure {

    private final MimeDescriptor descriptor;

    private final List<String> parameters;

    private final List<Structure> parts;

    private final String disposition;

    private final Map<String, String> dispositionParams;

    private final String location;

    private final String md5;

    private final List<String> languages;

    private final Structure embeddedMessageStructure;

    private final Envelope envelope;

    public MimeDescriptorStructure(final boolean allowExtensions,
            MimeDescriptor descriptor, EnvelopeBuilder builder)
            throws MailboxException, ParseException {
        super();
        this.descriptor = descriptor;
        parameters = createParameters(descriptor);
        parts = createParts(allowExtensions, descriptor, builder);

        languages = descriptor.getLanguages();
        this.dispositionParams = descriptor.getDispositionParams();
        this.disposition = descriptor.getDisposition();

        this.md5 = descriptor.getContentMD5();
        this.location = descriptor.getContentLocation();

        final MimeDescriptor embeddedMessage = descriptor.embeddedMessage();
        if (embeddedMessage == null) {
            embeddedMessageStructure = null;
            envelope = null;
        } else {
            embeddedMessageStructure = new MimeDescriptorStructure(
                    allowExtensions, embeddedMessage, builder);
            envelope = builder.buildEnvelope(embeddedMessage);
        }
    }

    private static List<Structure> createParts(final boolean allowExtensions,
            final MimeDescriptor descriptor, final EnvelopeBuilder builder)
            throws MailboxException, ParseException {
        final List<Structure> results = new ArrayList<Structure>();
        for (Iterator<MimeDescriptor> it = descriptor.parts(); it.hasNext();) {
            final MimeDescriptor partDescriptor = it.next();
            results.add(new MimeDescriptorStructure(allowExtensions,
                    partDescriptor, builder));
        }
        return results;
    }

    private static List<String> createParameters(MimeDescriptor descriptor)
            throws MailboxException {
        final List<String> results = new ArrayList<String>();
        // TODO: consider revising this 
        for (Map.Entry<String, String> entry:descriptor.contentTypeParameters().entrySet()) {
            results.add(entry.getKey());
            results.add(entry.getValue());
        }
        return results;
    }

    public String getDescription() {
        return descriptor.getContentDescription();
    }

    public String getEncoding() {
        return descriptor.getTransferContentEncoding();
    }

    public String getId() {
        return descriptor.getContentID();
    }

    public long getLines() {
        return descriptor.getLines();
    }

    public String getMediaType() {
        return descriptor.getMimeType();
    }

    public long getOctets() {
        return descriptor.getBodyOctets();
    }

    public List<String> getParameters() {
        return parameters;
    }

    public String getSubType() {
        return descriptor.getMimeSubType();
    }

    public Iterator<Structure> parts() {
        return parts.iterator();
    }

    public String getDisposition() {
        return disposition;
    }

    public String getLocation() {
        return location;
    }

    public String getMD5() {
        return md5;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public Structure getBody() {
        return embeddedMessageStructure;
    }

    public Map<String, String> getDispositionParams() {
        return dispositionParams;
    }

    public Envelope getEnvelope() {
        return envelope;
    }

}