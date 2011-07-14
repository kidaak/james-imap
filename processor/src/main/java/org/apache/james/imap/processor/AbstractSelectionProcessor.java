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

package org.apache.james.imap.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.mail.Flags;

import org.apache.james.imap.api.ImapCommand;
import org.apache.james.imap.api.ImapConstants;
import org.apache.james.imap.api.ImapSessionUtils;
import org.apache.james.imap.api.display.HumanReadableText;
import org.apache.james.imap.api.message.IdRange;
import org.apache.james.imap.api.message.response.StatusResponse;
import org.apache.james.imap.api.message.response.StatusResponse.ResponseCode;
import org.apache.james.imap.api.message.response.StatusResponseFactory;
import org.apache.james.imap.api.process.ImapProcessor;
import org.apache.james.imap.api.process.ImapSession;
import org.apache.james.imap.api.process.SearchResUtil;
import org.apache.james.imap.api.process.SelectedMailbox;
import org.apache.james.imap.message.request.AbstractMailboxSelectionRequest;
import org.apache.james.imap.message.response.ExistsResponse;
import org.apache.james.imap.message.response.RecentResponse;
import org.apache.james.imap.message.response.VanishedResponse;
import org.apache.james.imap.processor.base.FetchGroupImpl;
import org.apache.james.imap.processor.base.SelectedMailboxImpl;
import org.apache.james.mailbox.MailboxException;
import org.apache.james.mailbox.MailboxManager;
import org.apache.james.mailbox.MailboxNotFoundException;
import org.apache.james.mailbox.MailboxPath;
import org.apache.james.mailbox.MailboxSession;
import org.apache.james.mailbox.MessageManager;
import org.apache.james.mailbox.MessageRange.Type;
import org.apache.james.mailbox.SearchQuery;
import org.apache.james.mailbox.MessageManager.MetaData;
import org.apache.james.mailbox.SearchQuery.NumericRange;
import org.apache.james.mailbox.MessageRange;
import org.apache.james.mailbox.MessageResult;

abstract class AbstractSelectionProcessor<M extends AbstractMailboxSelectionRequest> extends AbstractMailboxProcessor<M> {

    private final Flags flags = new Flags();

    final StatusResponseFactory statusResponseFactory;

    private final boolean openReadOnly;

    public AbstractSelectionProcessor(final Class<M> acceptableClass, final ImapProcessor next, final MailboxManager mailboxManager, final StatusResponseFactory statusResponseFactory, final boolean openReadOnly) {
        super(acceptableClass, next, mailboxManager, statusResponseFactory);
        this.statusResponseFactory = statusResponseFactory;
        this.openReadOnly = openReadOnly;
        flags.add(Flags.Flag.ANSWERED);
        flags.add(Flags.Flag.DELETED);
        flags.add(Flags.Flag.DRAFT);
        flags.add(Flags.Flag.FLAGGED);
        flags.add(Flags.Flag.SEEN);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.james.imap.processor.AbstractMailboxProcessor#doProcess(org
     * .apache.james.imap.api.message.request.ImapRequest,
     * org.apache.james.imap.api.process.ImapSession, java.lang.String,
     * org.apache.james.imap.api.ImapCommand,
     * org.apache.james.imap.api.process.ImapProcessor.Responder)
     */
    protected void doProcess(AbstractMailboxSelectionRequest request, ImapSession session, String tag, ImapCommand command, Responder responder) {
        final String mailboxName = request.getMailboxName();
        try {
            final MailboxPath fullMailboxPath = buildFullPath(session, mailboxName);

            respond(tag, command, session, fullMailboxPath, request, responder);
           
            
        } catch (MailboxNotFoundException e) {
            session.getLog().debug("Select failed", e);
            responder.respond(statusResponseFactory.taggedNo(tag, command, HumanReadableText.FAILURE_NO_SUCH_MAILBOX));
        } catch (MailboxException e) {
            session.getLog().debug("Select failed", e);
            no(command, tag, responder, HumanReadableText.SELECT);
        }
    }

    private void respond(String tag, ImapCommand command, ImapSession session, MailboxPath fullMailboxPath, AbstractMailboxSelectionRequest request, Responder responder) throws MailboxException {
        
        Long lastKnownUidValidity = request.getLastKnownUidValidity();
        // Check if a QRESYNC parameter was used and if so if QRESYNC was enabled before.
        // If it was not enabled before its needed to return a BAD response
        //
        // From RFC5162 3.1. QRESYNC Parameter to SELECT/EXAMINE
        //
        //    A server MUST respond with a tagged BAD response if the Quick
        //    Resynchronization parameter to SELECT/EXAMINE command is specified
        //    and the client hasn't issued "ENABLE QRESYNC" in the current
        //    connection.
        if (lastKnownUidValidity != null && !EnableProcessor.getEnabledCapabilities(session).contains(ImapConstants.SUPPORTS_QRESYNC)) {
            taggedBad(command, tag, responder, HumanReadableText.QRESYNC_NOT_ENABLED);
            return;
        }
        
        final MessageManager.MetaData metaData = selectMailbox(fullMailboxPath, session, request.getCondstore());
        final SelectedMailbox selected = session.getSelected();

        flags(responder, selected);
        exists(responder, metaData);
        recent(responder, selected);
        uidValidity(responder, metaData);
        unseen(responder, metaData, selected, ImapSessionUtils.getMailboxSession(session));
        permanentFlags(responder, metaData, selected);
        highestModSeq(responder, metaData, selected);
        uidNext(responder, metaData);
        
        // Now do the QRESYNC processing if necessary
        // 
        // If the mailbox does nto store the mod-sequence in a permanent way its needed to not process the QRESYNC paramters
        // The same is true if none are given ;)
        if (metaData.isModSeqPermanent() && lastKnownUidValidity != null) {
            if (lastKnownUidValidity == metaData.getUidValidity()) {
                
                final MailboxManager mailboxManager = getMailboxManager();
                final MailboxSession mailboxSession = ImapSessionUtils.getMailboxSession(session);
                final MessageManager mailbox = mailboxManager.getMailbox(fullMailboxPath, mailboxSession);
               
                
                //  If the provided UIDVALIDITY matches that of the selected mailbox, the
                //  server then checks the last known modification sequence.
                //
                //  The server sends the client any pending flag changes (using FETCH
                //  responses that MUST contain UIDs) and expunges those that have
                //  occurred in this mailbox since the provided modification sequence.
                SearchQuery sq = new SearchQuery();
                sq.andCriteria(SearchQuery.modSeqGreaterThan(request.getKnownModSeq()));
                
                IdRange[] uidSet = request.getUidSet();

                if (uidSet == null) {
                    // See mailbox had some messages stored before, if not we don't need to query at all
                    if (metaData.getUidNext() != 1) {
                        uidSet = new IdRange[] {new IdRange(1, selected.getLastUid())};
                    }
                }
                // Check if the know uid set was provided. If so we only need to get the uids of these messages that matched here
                if (uidSet != null) {
                    NumericRange[] nranges = new NumericRange[uidSet.length];

                    for (int i = 0 ; i < uidSet.length; i++) {
                        IdRange r = uidSet[i];
                        long low = r.getLowVal();
                        long high = r.getHighVal();
                        if (low == high) {
                            nranges[i] = new NumericRange(low);
                        } else {
                            nranges[i] = new NumericRange(low, high);
                        }
                    
                    }
                    sq.andCriteria(SearchQuery.uid(nranges));
                }

                Iterator<Long> uidsIt = mailbox.search(sq, mailboxSession);
                List<Long> uids = new ArrayList<Long>();
                while(uidsIt.hasNext()) {
                    uids.add(uidsIt.next());
                }
                if (uids.isEmpty() == false) {
                    if (uidSet != null) {
                        List<Long> vanished = new ArrayList<Long>();
                        for (int i = 0; i < uids.size(); i++) {
                            long uid = uids.get(i);
                            boolean match = false;
                            for (int a = 0; a < uidSet.length; a++) {
                                if (uidSet[a].includes(uid)) {
                                    match = true;
                                    break;
                                }
                            }
                            if (!match) {
                                vanished.add(uid);
                            }
                        }
                        if (!vanished.isEmpty()) {
                            List<MessageRange> ranges = MessageRange.toRanges(vanished);
                            IdRange[] idRanges  = new IdRange[ranges.size()];
                            for (int i = 0; i < ranges.size(); i++) {
                                MessageRange r = ranges.get(i);
                                if (r.getType() == Type.ONE) {
                                    idRanges[i] = new IdRange(r.getUidFrom());
                                } else {
                                    idRanges[i] = new IdRange(r.getUidFrom(), r.getUidTo());
                                }
                                
                            }
                            //TODO: Send also VANISHED responses as stated in QRESYNC RFC
                            responder.respond(new VanishedResponse(idRanges, true));
                        }
                      
                    }
                    List<MessageRange> ranges = MessageRange.toRanges(uids);
                    for (int i = 0; i < ranges.size(); i++) {
                        addFlagsResponses(session, selected, responder, true, ranges.get(i), mailbox, mailboxSession);
                    }
                }
                taggedOk(responder, tag, command, metaData, HumanReadableText.SELECT);
            } else {
                
                taggedOk(responder, tag, command, metaData, HumanReadableText.QRESYNC_UIDVALIDITY_MISMATCH);
            }
        } else {
            taggedOk(responder, tag, command, metaData, HumanReadableText.SELECT);
        }
        
        
        
        
        // Reset the saved sequence-set after successful SELECT / EXAMINE
        // See RFC 5812 2.1. Normative Description of the SEARCHRES Extension
        SearchResUtil.resetSavedSequenceSet(session);
    }

    private void highestModSeq(Responder responder, MetaData metaData, SelectedMailbox selected) {
        final StatusResponse untaggedOk;
        if (metaData.isModSeqPermanent()) {
            final long highestModSeq = metaData.getHighestModSeq();
            untaggedOk = statusResponseFactory.untaggedOk(HumanReadableText.HIGHEST_MOD_SEQ, ResponseCode.highestModSeq(highestModSeq));
        } else {
            untaggedOk = statusResponseFactory.untaggedOk(HumanReadableText.NO_MOD_SEQ, ResponseCode.noModSeq());
        }
        responder.respond(untaggedOk);        
    }

    private void uidNext(final Responder responder, final MessageManager.MetaData metaData) throws MailboxException {
        final long uid = metaData.getUidNext();
        final StatusResponse untaggedOk = statusResponseFactory.untaggedOk(HumanReadableText.UIDNEXT, ResponseCode.uidNext(uid));
        responder.respond(untaggedOk);
    }

    private void taggedOk(final Responder responder, final String tag, final ImapCommand command, final MetaData metaData, HumanReadableText text) {
        final boolean writeable = metaData.isWriteable() && !openReadOnly;
        final ResponseCode code;
        if (writeable) {
            code = ResponseCode.readWrite();
        } else {
            code = ResponseCode.readOnly();
        }
        final StatusResponse taggedOk = statusResponseFactory.taggedOk(tag, command, text, code);
        responder.respond(taggedOk);
    }

    private void unseen(Responder responder, MessageManager.MetaData metaData, final SelectedMailbox selected, MailboxSession session) throws MailboxException {
        final Long firstUnseen = metaData.getFirstUnseen();
        if (firstUnseen != null) {
            final long unseenUid = firstUnseen;
            int msn = selected.msn(unseenUid);

            if (msn == SelectedMailbox.NO_SUCH_MESSAGE)
                throw new MailboxException("No message found with uid " + unseenUid + " in mailbox " + selected.getPath().getFullName(session.getPathDelimiter()));

            final StatusResponse untaggedOk = statusResponseFactory.untaggedOk(HumanReadableText.unseen(msn), ResponseCode.unseen(msn));
            responder.respond(untaggedOk);
        }

    }

    private void uidValidity(Responder responder, MessageManager.MetaData metaData) throws MailboxException {
        final long uidValidity = metaData.getUidValidity();
        final StatusResponse untaggedOk = statusResponseFactory.untaggedOk(HumanReadableText.UID_VALIDITY, ResponseCode.uidValidity(uidValidity));
        responder.respond(untaggedOk);
    }

    private void recent(Responder responder, final SelectedMailbox selected) {
        final int recentCount = selected.recentCount();
        final RecentResponse recentResponse = new RecentResponse(recentCount);
        responder.respond(recentResponse);
    }

    private void exists(Responder responder, MessageManager.MetaData metaData) throws MailboxException {
        final long messageCount = metaData.getMessageCount();
        final ExistsResponse existsResponse = new ExistsResponse(messageCount);
        responder.respond(existsResponse);
    }

    private MessageManager.MetaData selectMailbox(MailboxPath mailboxPath, ImapSession session, boolean condstore) throws MailboxException {
        final MailboxManager mailboxManager = getMailboxManager();
        final MailboxSession mailboxSession = ImapSessionUtils.getMailboxSession(session);
        final MessageManager mailbox = mailboxManager.getMailbox(mailboxPath, mailboxSession);

        final SelectedMailbox sessionMailbox;
        final SelectedMailbox currentMailbox = session.getSelected();
        if (currentMailbox == null || !currentMailbox.getPath().equals(mailboxPath)) {
            
            // QRESYNC EXTENSION
            //
            // Response with the CLOSE return-code when the currently selected mailbox is closed implicitly using the SELECT/EXAMINE command on another mailbox
            //
            // See rfc5162 3.7. CLOSED Response Code
            if (currentMailbox != null) {
                getStatusResponseFactory().untaggedOk(HumanReadableText.QRESYNC_CLOSED, ResponseCode.closed());
            }
            sessionMailbox = createNewSelectedMailbox(mailbox, mailboxSession, session, mailboxPath, condstore);
            
        } else {
            // TODO: Check if we need to handle CONDSTORE there too 
            sessionMailbox = currentMailbox;
        }
        final MessageManager.MetaData metaData = mailbox.getMetaData(!openReadOnly, mailboxSession, MessageManager.MetaData.FetchGroup.FIRST_UNSEEN);
        addRecent(metaData, sessionMailbox);
        return metaData;
    }

    private SelectedMailbox createNewSelectedMailbox(final MessageManager mailbox, final MailboxSession mailboxSession, ImapSession session, MailboxPath path, boolean condstore) throws MailboxException {
        
        Iterator<MessageResult> messages = mailbox.getMessages(MessageRange.all(), FetchGroupImpl.MINIMAL, mailboxSession);
        Flags applicableFlags = new Flags(flags);
        List<Long> uids = new ArrayList<Long>();
        while(messages.hasNext()) {
            MessageResult mr = messages.next();
            applicableFlags.add(mr.getFlags());
            uids.add(mr.getUid());
        }
        
        
        // \RECENT is not a applicable flag in imap so remove it from the list
        applicableFlags.remove(Flags.Flag.RECENT);
        
        final SelectedMailbox sessionMailbox = new SelectedMailboxImpl(getMailboxManager(), uids.iterator(),applicableFlags,  session, path, condstore);
        session.selected(sessionMailbox);
        return sessionMailbox;
    }

    private void addRecent(final MessageManager.MetaData metaData, SelectedMailbox sessionMailbox) throws MailboxException {
        final List<Long> recentUids = metaData.getRecent();
        for (int i = 0; i < recentUids.size(); i++) {
            long uid = recentUids.get(i);
            sessionMailbox.addRecent(uid);
        }
    }
}
