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
package org.apache.james.imap.inmemory.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.james.imap.inmemory.mail.model.InMemoryMailbox;
import org.apache.james.imap.mailbox.MailboxException;
import org.apache.james.imap.mailbox.MailboxNotFoundException;
import org.apache.james.imap.mailbox.StorageException;
import org.apache.james.imap.store.mail.MailboxMapper;
import org.apache.james.imap.store.mail.model.Mailbox;

public class InMemoryMailboxMapper implements MailboxMapper<Long> {
    
    private static final int INITIAL_SIZE = 128;
    private final Map<Long, InMemoryMailbox> mailboxesById;
    private final char delimiter;

    public InMemoryMailboxMapper(char delimiter) {
        mailboxesById = new ConcurrentHashMap<Long, InMemoryMailbox>(INITIAL_SIZE);
        this.delimiter = delimiter;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.MailboxMapper#countMailboxesWithName(java.lang.String)
     */
    public long countMailboxesWithName(String name) throws StorageException {
        int total = 0;
        for (final InMemoryMailbox mailbox:mailboxesById.values()) {
            if (mailbox.getName().equals(name)) {
                total++;
            }
        }
        return total;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.MailboxMapper#delete(org.apache.james.imap.store.mail.model.Mailbox)
     */
    public void delete(Mailbox<Long> mailbox) throws StorageException {
        mailboxesById.remove(mailbox.getMailboxId());
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.MailboxMapper#deleteAll()
     */
    public void deleteAll() throws StorageException {
        mailboxesById.clear();
    }


    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.MailboxMapper#findMailboxById(java.lang.Object)
     */
    public Mailbox<Long> findMailboxById(Long mailboxId) throws StorageException, MailboxNotFoundException {
        return mailboxesById.get(mailboxesById);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.MailboxMapper#findMailboxByName(java.lang.String)
     */
    public synchronized Mailbox<Long> findMailboxByName(String name) throws StorageException, MailboxNotFoundException {
        Mailbox<Long> result = null;
        for (final InMemoryMailbox mailbox:mailboxesById.values()) {
            if (mailbox.getName().equals(name)) {
                result = mailbox;
                break;
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.MailboxMapper#findMailboxWithNameLike(java.lang.String)
     */
    public List<Mailbox<Long>> findMailboxWithNameLike(String name) throws StorageException {
        final String regex = name.replace("%", ".*");
        List<Mailbox<Long>> results = new ArrayList<Mailbox<Long>>();
        for (final InMemoryMailbox mailbox:mailboxesById.values()) {
            if (mailbox.getName().matches(regex)) {
                results.add(mailbox);
            }
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.MailboxMapper#save(org.apache.james.imap.store.mail.model.Mailbox)
     */
    public void save(Mailbox<Long> mailbox) throws StorageException {
        mailboxesById.put(mailbox.getMailboxId(), (InMemoryMailbox) mailbox);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.store.transaction.TransactionalMapper#execute(org.apache.james.imap.store.transaction.TransactionalMapper.Transaction)
     */
    public void execute(Transaction transaction) throws MailboxException {
        transaction.run();
    }

    public void endRequest() {
        // TODO Auto-generated method stub
        
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.MailboxMapper#hasChildren(org.apache.james.imap.store.mail.model.Mailbox)
     */
    public boolean hasChildren(Mailbox<Long> mailbox) throws StorageException,
            MailboxNotFoundException {
        String mailboxName = mailbox.getName() + delimiter;
        for (final InMemoryMailbox box:mailboxesById.values()) {
            if (box.getName().startsWith(mailboxName)) {
                return true;
            }
        }
        return false;
    }
    
}