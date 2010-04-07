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

package org.apache.james.imap.store;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * {@link FilterInputStream} implementation which skip the given bytes as late as possible.
 * 
 *
 */
public class LazySkippingInputStream extends FilterInputStream{

    private long skipBytes;
    private boolean skipped = false;

    public LazySkippingInputStream(InputStream in, long skipBytes) {
        super(in);
        this.skipBytes = skipBytes;
    }

    @Override
    public int read() throws IOException {
        skipIfNeeded();

        return super.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        skipIfNeeded();
        return super.read(b, off, len);
    }

    @Override
    public int read(byte[] b) throws IOException {
        skipIfNeeded();
        return super.read(b);
    }
    
    @Override
    public int available() throws IOException {
        skipIfNeeded();

        return super.available();
    }

    @Override
    public synchronized void mark(int readlimit) {
        // not supported
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public long skip(long n) throws IOException {
        skipIfNeeded();
        return super.skip(n);
    }

    private void skipIfNeeded() throws IOException {
        if (skipped == false) {
            super.skip(skipBytes);
            skipped = true;
        }
    }

}
