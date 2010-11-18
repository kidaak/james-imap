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
package org.apache.james.mailbox.store.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import org.apache.james.mailbox.Content;
import org.apache.james.mailbox.store.mail.model.Message;

/**
 * {@link Content} which is stored in a {@link InputStream}
 *
 */
public final class InputStreamContent implements Content{
    private Message m;
    private Type type;

    public enum Type {
        Full,
        Body
    }
    public InputStreamContent(Message m, Type type) throws IOException{
        this.m = m;
        this.type = type;
    }
    
    /*
     * (non-Javadoc)
     * @see org.apache.james.mailbox.Content#size()
     */
    public long size() {
        switch (type) {
        case Full:
            return m.getFullContentOctets();

        default:
            return m.getBodyOctets();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.mailbox.Content#writeTo(java.nio.channels.WritableByteChannel)
     */
    public void writeTo(WritableByteChannel channel) throws IOException {
        InputStream in = null;
        try {
            switch (type) {
            case Full:
                in = m.getFullContent();
                break;
            default:
                in = m.getBodyContent();
                break;
            }
            // read all the content of the underlying InputStream in 16384 byte chunks, wrap them
            // in a ByteBuffer and finally write the Buffer to the channel
            byte[] buf = new byte[16384];
            int i = 0;
            while ((i = in.read(buf)) != -1) {
                ByteBuffer buffer = ByteBuffer.wrap(buf);
                // set the limit of the buffer to the returned bytes
                    buffer.limit(i);
                    channel.write(buffer);
            }
        } finally {
            if(in != null) {
                in.close();
            }
        }
        
    }
    
   

}
