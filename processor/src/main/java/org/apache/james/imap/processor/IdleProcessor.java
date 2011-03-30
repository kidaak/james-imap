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

import static org.apache.james.imap.api.ImapConstants.SUPPORTS_IDLE;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.james.imap.api.ImapCommand;
import org.apache.james.imap.api.ImapSessionUtils;
import org.apache.james.imap.api.display.HumanReadableText;
import org.apache.james.imap.api.message.response.StatusResponse;
import org.apache.james.imap.api.message.response.StatusResponseFactory;
import org.apache.james.imap.api.process.ImapLineHandler;
import org.apache.james.imap.api.process.ImapProcessor;
import org.apache.james.imap.api.process.ImapSession;
import org.apache.james.imap.api.process.SelectedMailbox;
import org.apache.james.imap.message.request.IdleRequest;
import org.apache.james.imap.message.response.ContinuationResponse;
import org.apache.james.mailbox.MailboxException;
import org.apache.james.mailbox.MailboxListener;
import org.apache.james.mailbox.MailboxManager;
import org.apache.james.mailbox.MailboxSession;

public class IdleProcessor extends AbstractMailboxProcessor<IdleRequest> implements CapabilityImplementingProcessor {
	
	
    private final ScheduledExecutorService heartbeatExecutor; 

    private final static String HEARTBEAT_FUTURE = "IDLE_HEARTBEAT_FUTURE";
    
    // 2 minutes
    public final static long DEFAULT_HEARTBEAT_INTERVAL_IN_SECONDS = 2 * 60;
    public final static TimeUnit DEFAULT_HEARTBEAT_INTERVAL_UNIT = TimeUnit.SECONDS;
    public final static int DEFAULT_SCHEDULED_POOL_CORE_SIZE = 5;
    private final static String DONE = "DONE";
    private final TimeUnit heartbeatIntervalUnit;
    private final long heartbeatInterval;
    
    public IdleProcessor(final ImapProcessor next, final MailboxManager mailboxManager,
            final StatusResponseFactory factory) {
        this(next, mailboxManager, factory, DEFAULT_HEARTBEAT_INTERVAL_IN_SECONDS, DEFAULT_HEARTBEAT_INTERVAL_UNIT, Executors.newScheduledThreadPool(DEFAULT_SCHEDULED_POOL_CORE_SIZE));
        
    }

    public IdleProcessor(final ImapProcessor next, final MailboxManager mailboxManager,
            final StatusResponseFactory factory, long heartbeatInterval, TimeUnit heartbeatIntervalUnit, ScheduledExecutorService heartbeatExecutor) {
        super(IdleRequest.class, next, mailboxManager, factory);
        this.heartbeatInterval = heartbeatInterval;
        this.heartbeatIntervalUnit = heartbeatIntervalUnit;
        this.heartbeatExecutor = heartbeatExecutor;
        
    }

    protected void doProcess(final IdleRequest message, final ImapSession session,
            final String tag, final ImapCommand command, final Responder responder) {
        final AtomicBoolean closed = new AtomicBoolean(false);

        try {        
            responder.respond(new ContinuationResponse(HumanReadableText.IDLING));
            unsolicitedResponses(session, responder, false);

            final MailboxManager mailboxManager = getMailboxManager();
            final MailboxSession mailboxSession = ImapSessionUtils.getMailboxSession(session);
            SelectedMailbox sm = session.getSelected();
            if(sm != null) {
                mailboxManager.addListener(sm.getPath(), 
                    new IdleMailboxListener(closed, session, responder), mailboxSession);
            }
        
            
            session.pushLineHandler(new ImapLineHandler() {
                
                /*
                 * (non-Javadoc)
                 * @see org.apache.james.imap.api.process.ImapLineHandler#onLine(org.apache.james.imap.api.process.ImapSession, byte[])
                 */
                public void onLine(ImapSession session, byte[] data) {
                    String line;
                    if (data.length > 2) {
                        line = new String(data, 0, data.length -2 );
                    } else {
                        line = "";
                    }
                        
                    closed.set(true);
                    session.popLineHandler();
                    if (!DONE.equals(line.toUpperCase(Locale.US))) {
                        StatusResponse response = getStatusResponseFactory().taggedBad(tag, command,
                                HumanReadableText.INVALID_COMMAND);
                        responder.respond(response);
                    } else {
                        okComplete(command, tag, responder);
                        
                        // See if we need to cancel the idle heartbeat handling
                        Object oFuture = session.getAttribute(HEARTBEAT_FUTURE);
                        if (oFuture != null) {
                        	ScheduledFuture<?> future = (ScheduledFuture<?>)oFuture;
                        	if (future.cancel(true) == false) {
                        		// unable to cancel the future so better logout now!
                        		session.getLog().error("Unable to disable idle heartbeat for unknown reason! Force logout");
                        		session.logout();
                        	}
                        }
                    }                    
                }
            });
            
            // Check if we should send heartbeats 
            if (heartbeatInterval > 0) {
            	ScheduledFuture<?> heartbeatFuture = heartbeatExecutor.scheduleWithFixedDelay(new Runnable() {
				
            		public void run() {
            			// Send a heartbeat to the client to make sure we reset the idle timeout. This is kind of the same workaround as dovecot use.
            			//
            			// This is mostly needed because of the broken outlook client, but can't harm for other clients too.
            			// See IMAP-272
            			StatusResponse response = getStatusResponseFactory().untaggedOk(HumanReadableText.HEARTBEAT);
            			responder.respond(response);
            		}
            	}, heartbeatInterval, heartbeatInterval, heartbeatIntervalUnit);
            	
            	// store future for later usage
            	session.setAttribute(HEARTBEAT_FUTURE, heartbeatFuture);
            }
            
            
            
        } catch (MailboxException e) {
            closed.set(true);
            // TODO: What should we do here?
            no(command, tag, responder, HumanReadableText.GENERIC_FAILURE_DURING_PROCESSING);
        }
    }

    public List<String> getImplementedCapabilities(ImapSession session) {
        return Arrays.asList(SUPPORTS_IDLE);
    }
    
    private class IdleMailboxListener implements MailboxListener {
        
        private final AtomicBoolean closed;
        private final ImapSession session;
        private final Responder responder;
        
        public IdleMailboxListener(AtomicBoolean closed, ImapSession session, Responder responder) {
            this.closed = closed;
            this.session = session;
            this.responder = responder;
        }

        public void event(Event event) {
            synchronized (session) {
                if (isClosed()) {
                    return;
                }
                if (event instanceof Added || event instanceof Expunged || event instanceof FlagsUpdated) {
                    unsolicitedResponses(session, responder, false);
                }
            }
        }

        public boolean isClosed() {
            return closed.get();
        }
        
    }
}
