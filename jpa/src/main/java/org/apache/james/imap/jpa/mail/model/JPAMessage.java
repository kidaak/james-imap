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
package org.apache.james.imap.jpa.mail.model;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;

import org.apache.james.imap.store.mail.model.PropertyBuilder;

@Entity(name="Message")
public class JPAMessage extends AbstractJPAMessage{


    /** The value for the body field. Lazy loaded */
    /** We use a max length to represent 1gb data. Thats prolly overkill, but who knows */
    @Basic(optional=false, fetch=FetchType.LAZY) @Column(length=1048576000)  @Lob private byte[] content;

    @Deprecated
    public JPAMessage() {}

    public JPAMessage(final InputStream content, final long contentOctets, final int bodyStartOctet, final List<JPAHeader> headers, final PropertyBuilder propertyBuilder) {
        super(contentOctets,bodyStartOctet,headers,propertyBuilder);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] buf = new byte[1024];
            int i = 0;
            while ((i = content.read(buf)) != -1) {
                out.write(buf, 0, i);
            }
            this.content = out.toByteArray();
            if (out != null)
                out.close();

        } catch (Exception e) {
            this.content = new byte[0];
        }
    }

    /**
     * Create a copy of the given message
     * 
     * @param message
     */
    public JPAMessage(JPAMessage message) {
        super(message);
        ByteBuffer buf = message.getFullContent().duplicate(); 
        int a = 0;
        this.content = new byte[buf.capacity()];
        while(buf.hasRemaining()) {
            content[a] = buf.get();
            a++;
        }     
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.Document#getFullContent()
     */
    public ByteBuffer getFullContent() {
        return ByteBuffer.wrap(content);
    }

}
