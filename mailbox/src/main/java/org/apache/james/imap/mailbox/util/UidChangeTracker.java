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

package org.apache.james.imap.mailbox.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.mail.Flags;
import javax.mail.MessagingException;

import org.apache.james.imap.mailbox.Constants;
import org.apache.james.imap.mailbox.Mailbox;
import org.apache.james.imap.mailbox.MailboxListener;
import org.apache.james.imap.mailbox.MailboxException;
import org.apache.james.imap.mailbox.MessageResult;

public class UidChangeTracker implements Constants {

    private final MailboxEventDispatcher eventDispatcher;

    private final TreeMap cache;

    private long lastUidAtStart;

    private long lastUid;

    private long lastScannedUid = 0;

    public UidChangeTracker(long lastUid) {
        this.lastUidAtStart = lastUid;
        this.lastUid = lastUid;
        eventDispatcher = new MailboxEventDispatcher();
        cache = new TreeMap();
    }

    public synchronized void expunged(final long[] uidsExpunged) {
        final int length = uidsExpunged.length;
        for (int i = 0; i < length; i++) {
            final long uid = uidsExpunged[i];
            cache.remove(new Long(uid));
            eventDispatcher.expunged(uid, 0);
        }
    }

    /**
     * Indicates that the flags on the given messages may have been updated.
     * 
     * @param messageFlags
     *            flags
     * @param sessionId
     *            id of the session upating the flags
     * @see #flagsUpdated(MessageResult, long)
     */
    public synchronized void flagsUpdated(MessageFlags[] messageFlags,
            long sessionId) {
        if (messageFlags != null) {
            final int length = messageFlags.length;
            for (int i = 0; i < length; i++) {
                final MessageFlags result = messageFlags[i];
                final long uid = result.getUid();
                final Flags flags = result.getFlags();
                final Long uidObject = new Long(uid);
                updatedFlags(uid, flags, uidObject, sessionId);
            }
        }
    }

    /**
     * Indicates that the flags on the given messages may have been updated.
     * 
     * @param messageResults
     *            results
     * @param sessionId
     *            id of the session upating the flags
     * @throws MailboxException
     * @see #flagsUpdated(MessageResult, long)
     */
    public synchronized void flagsUpdated(Collection messageResults,
            long sessionId) throws MailboxException {
        if (messageResults != null) {
            for (final Iterator it = messageResults.iterator(); it.hasNext();) {
                final MessageResult result = (MessageResult) it.next();
                flagsUpdated(result, sessionId);
            }
        }
    }

    /**
     * Indicates that the flags on the given message may have been updated.
     * 
     * @param messageResult
     *            result of update
     * @param sessionId
     *            id of the session updating the flags
     * @throws MailboxException
     */
    public synchronized void flagsUpdated(MessageResult messageResult,
            long sessionId) throws MailboxException {
        if (messageResult != null) {
            final Flags flags = messageResult.getFlags();
            final long uid = messageResult.getUid();
            final Long uidLong = new Long(uid);
            updatedFlags(uid, flags, uidLong, sessionId);
        }
    }

    public synchronized void found(UidRange range,
            final Collection messageResults) throws MessagingException {
        found(range, MessageFlags.toMessageFlags(messageResults));
    }

    public synchronized void found(UidRange range,
            final MessageFlags[] messageFlags) {
        Set expectedSet = getSubSet(range);
        final int length = messageFlags.length;
        for (int i = 0; i < length; i++) {
            final MessageFlags message = messageFlags[i];
            if (message != null) {
                long uid = message.getUid();
                if (uid > lastScannedUid) {
                    lastScannedUid = uid;
                }
                final Flags flags = message.getFlags();
                final Long uidLong = new Long(uid);
                if (expectedSet.contains(uidLong)) {
                    expectedSet.remove(uidLong);
                    updatedFlags(uid, flags, uidLong, Mailbox.ANONYMOUS_SESSION);
                } else {
                    cache.put(uidLong, flags);
                    if (uid > lastUidAtStart) {
                        eventDispatcher.added(uid, 0);
                    }
                }
            }

        }

        if (lastScannedUid > lastUid) {
            lastUid = lastScannedUid;
        }
        if (range.getToUid() == UID_INFINITY || range.getToUid() >= lastUid) {
            lastScannedUid = lastUid;
        } else if (range.getToUid() != UID_INFINITY
                && range.getToUid() < lastUid
                && range.getToUid() > lastScannedUid) {
            lastScannedUid = range.getToUid();
        }

        for (Iterator iter = expectedSet.iterator(); iter.hasNext();) {
            long uid = ((Long) iter.next()).longValue();
            eventDispatcher.expunged(uid, 0);
        }
    }

    private void updatedFlags(final long uid, final Flags flags,
            final Long uidLong, final long sessionId) {
        if (flags != null) {
            Flags cachedFlags = (Flags) cache.get(uidLong);
            if (cachedFlags == null || !flags.equals(cachedFlags)) {
                if (cachedFlags != null) {
                    eventDispatcher.flagsUpdated(uid, sessionId, cachedFlags,
                            flags);
                }
                cache.put(uidLong, flags);
            }
        }
    }

    private SortedSet getSubSet(UidRange range) {
        final Long rangeStartLong = new Long(range.getFromUid());
        if (range.getToUid() > 0) {
            final long nextUidAfterRange = range.getToUid() + 1;
            final Long nextUidAfterRangeLong = new Long(nextUidAfterRange);
            final SortedMap subMap = cache.subMap(rangeStartLong,
                    nextUidAfterRangeLong);
            final Set keySet = subMap.keySet();
            return new TreeSet(keySet);
        } else {
            return new TreeSet(cache.tailMap(rangeStartLong).keySet());
        }

    }

    public synchronized void found(MessageResult messageResult)
            throws MessagingException {
        if (messageResult != null) {
            long uid = messageResult.getUid();
            Collection results = new ArrayList();
            results.add(messageResult);
            found(new UidRange(uid, uid), results);
        }
    }

    public synchronized long getLastUid() {
        return lastUid;
    }

    public synchronized void foundLastUid(long foundLastUid) {
        if (foundLastUid > lastUid) {
            lastUid = foundLastUid;
        }
    }

    public synchronized long getLastScannedUid() {
        return lastScannedUid;
    }

    public synchronized void addMailboxListener(MailboxListener listener) {
        eventDispatcher.addMailboxListener(listener);
    }

    public synchronized void removeMailboxListener(MailboxListener listener) {
        eventDispatcher.removeMailboxListener(listener);
    }

    public synchronized void mailboxDeleted(long sessionId) {
        eventDispatcher.mailboxDeleted(sessionId);
    }

}