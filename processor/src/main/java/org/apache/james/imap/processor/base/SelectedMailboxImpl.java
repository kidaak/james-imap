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
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.james.api.imap.AbstractLogEnabled;
import org.apache.james.api.imap.process.SelectedMailbox;
import org.apache.james.imap.mailbox.MailboxException;
import org.apache.james.imap.mailbox.MailboxManager;
import org.apache.james.imap.mailbox.MailboxSession;
import org.apache.james.imap.mailbox.util.MailboxEventAnalyser;
import org.apache.james.imap.mailbox.util.UidToMsnConverter;

public class SelectedMailboxImpl extends AbstractLogEnabled implements
        SelectedMailbox {

    private final MailboxEventAnalyser events;

    private final UidToMsnConverter converter;

    private final Set<Long> recentUids;

    private boolean recentUidRemoved;

    public SelectedMailboxImpl(final MailboxManager mailboxManager, final List<Long> uids,
            final MailboxSession mailboxSession, final String name) throws MailboxException {
        recentUids = new TreeSet<Long>();
        recentUidRemoved = false;
        final long sessionId = mailboxSession.getSessionId();
        events = new MailboxEventAnalyser(sessionId, name);
        // Ignore events from our session
        events.setSilentFlagChanges(true);
        mailboxManager.addListener(name, events);
        converter = new UidToMsnConverter(uids);
        mailboxManager.addListener(name, converter);
    }

    /**
     * @see org.apache.james.api.imap.process.SelectedMailbox#deselect()
     */
    public void deselect() {
        converter.close();
        events.close();
    }

    public boolean isSizeChanged() {
        return events.isSizeChanged();
    }

    public int msn(long uid) {
        return converter.getMsn(uid);
    }

    /**
     * Is the mailbox deleted?
     * 
     * @return true when the mailbox has been deleted by another session, false
     *         otherwise
     */
    public boolean isDeletedByOtherSession() {
        return events.isDeletedByOtherSession();
    }

    public long uid(int msn) {
        return converter.getUid(msn);
    }

    public boolean removeRecent(long uid) {
        final boolean result = recentUids.remove(new Long(uid));
        if (result) {
            recentUidRemoved = true;
        }
        return result;
    }

    public boolean addRecent(long uid) {
        final boolean result = recentUids.add(new Long(uid));
        return result;
    }

    public Collection<Long> getRecent() {
        checkExpungedRecents();
        return new ArrayList<Long>(recentUids);
    }

    public int recentCount() {
        checkExpungedRecents();
        return recentUids.size();
    }

    public String getName() {
        return events.getMailboxName();
    }

    private void checkExpungedRecents() {
        for (final Long uid: events.expungedUids()) {
            removeRecent(uid.longValue());
        }
    }

    public boolean isRecent(long uid) {
        boolean result = false;
        for (final Long recentUid: recentUids) {
            if (recentUid.longValue() == uid) {
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean isRecentUidRemoved() {
        return recentUidRemoved;
    }

    public void resetRecentUidRemoved() {
        recentUidRemoved = false;
    }

    public void resetEvents() {
        events.reset();
    }

    public Collection<Long> expungedUids() {
        return events.expungedUids();
    }

    public void expunged(Collection<Long> expungedUids) {
        for (final Long uid: expungedUids) {
            final long uidValue = uid.longValue();
            converter.expunge(uidValue);
        }
    }

    public Collection<Long> flagUpdateUids() {
        return events.flagUpdateUids();
    }
}