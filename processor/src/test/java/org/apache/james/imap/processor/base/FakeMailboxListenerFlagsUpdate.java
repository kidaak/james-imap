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

package org.apache.james.imap.processor.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.mail.Flags;
import javax.mail.Flags.Flag;

import org.apache.james.mailbox.MailboxPath;
import org.apache.james.mailbox.MailboxListener.FlagsUpdated;

public class FakeMailboxListenerFlagsUpdate extends FlagsUpdated {

    public List<Flags.Flag> flags = new ArrayList<Flags.Flag>();

    public long subjectUid;

    public Flags newFlags;

    public FakeMailboxListenerFlagsUpdate(long subjectUid, Flags newFlags,
            long sessionId, MailboxPath path) {
        super(sessionId, path);
        this.subjectUid = subjectUid;
        this.newFlags = newFlags;
    }

    public long getSubjectUid() {
        return subjectUid;
    }

    public Iterator<Flag> flagsIterator() {
        return flags.iterator();
    }

    public Flags getNewFlags() {
        return newFlags;
    }
}
