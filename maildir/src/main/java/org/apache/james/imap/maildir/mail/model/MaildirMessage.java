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
package org.apache.james.imap.maildir.mail.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Flags;

import org.apache.commons.lang.NotImplementedException;
import org.apache.james.imap.api.display.HumanReadableText;
import org.apache.james.imap.mailbox.MailboxException;
import org.apache.james.imap.maildir.MaildirMessageName;
import org.apache.james.imap.store.mail.model.AbstractMessage;
import org.apache.james.imap.store.mail.model.Message;
import org.apache.james.imap.store.mail.model.Header;
import org.apache.james.imap.store.mail.model.Mailbox;
import org.apache.james.imap.store.mail.model.MailboxMembership;
import org.apache.james.imap.store.mail.model.Property;
import org.apache.james.imap.store.mail.model.PropertyBuilder;
import org.apache.james.imap.store.streaming.StreamUtils;

public class MaildirMessage extends AbstractMessage implements MailboxMembership<Integer> {

    // Document
    private int bodyStartOctet;
    private InputStream rawFullContent;
    private List<MaildirHeader> headers;
    private String mediaType;
    private List<MaildirProperty> properties;
    private String subType;
    private Long textualLineCount;
    
    // MailboxMembership
    private Date internalDate;
    private long size;
    private long uid;
    private boolean answered;
    private boolean deleted;
    private boolean draft;
    private boolean flagged;
    private boolean recent;
    private boolean seen;
    
    private boolean newMessage;
    private boolean modified = false;
    
    /**
     * This constructor is called when appending a new message.
     * @param mailbox
     * @param internalDate
     * @param size
     * @param flags
     * @param documentIn
     * @param bodyStartOctet
     * @param maildirHeaders
     * @param propertyBuilder
     */
    public MaildirMessage(Mailbox<Integer> mailbox, Date internalDate,
            int size, Flags flags, InputStream documentIn, int bodyStartOctet,
            List<MaildirHeader> maildirHeaders, PropertyBuilder propertyBuilder) {
        super();
        // Document
        this.rawFullContent = documentIn;
        this.bodyStartOctet = bodyStartOctet;
        this.headers = new ArrayList<MaildirHeader>(maildirHeaders);
        this.textualLineCount = propertyBuilder.getTextualLineCount();
        this.mediaType = propertyBuilder.getMediaType();
        this.subType = propertyBuilder.getSubType();
        final List<Property> properties = propertyBuilder.toProperties();
        this.properties = new ArrayList<MaildirProperty>(properties.size());
        int order = 0;
        for (final Property property:properties) {
            this.properties.add(new MaildirProperty(property, order++));
        }
        // MailboxMembership
        this.internalDate = internalDate;
        this.size = size;
        setFlags(flags);
        // this message is new (this constructor is only used for such)
        this.newMessage = true;
    }
    
    /**
     * This constructor is used when parsing a already stored message.
     * @param mailbox
     * @param size
     * @param documentIn
     * @param bodyStartOctet
     * @param maildirHeaders
     * @param propertyBuilder
     */
    public MaildirMessage(Mailbox<Integer> mailbox, int size, InputStream documentIn, int bodyStartOctet,
            List<MaildirHeader> maildirHeaders, PropertyBuilder propertyBuilder) {
        super();
        // Document
        this.rawFullContent = documentIn;
        this.bodyStartOctet = bodyStartOctet;
        this.headers = new ArrayList<MaildirHeader>(maildirHeaders);
        this.textualLineCount = propertyBuilder.getTextualLineCount();
        this.mediaType = propertyBuilder.getMediaType();
        this.subType = propertyBuilder.getSubType();
        final List<Property> properties = propertyBuilder.toProperties();
        this.properties = new ArrayList<MaildirProperty>(properties.size());
        int order = 0;
        for (final Property property:properties) {
            this.properties.add(new MaildirProperty(property, order++));
        }
        // MailboxMembership
        this.size = size;
        // this message is not new (this constructor is only used for such)
        this.newMessage = false;
    }
    
    /**
     * Create a copy of the given message
     * 
     * @param mailbox
     * @param message The message to copy
     * @throws IOException 
     */
    public MaildirMessage(Mailbox<Integer> mailbox, MaildirMessage message) throws MailboxException {
        this.internalDate = message.getInternalDate();
        this.size = message.getDocument().getFullContentOctets();
        this.answered = message.isAnswered();
        this.deleted = message.isDeleted();
        this.draft = message.isDraft();
        this.flagged = message.isFlagged();
        this.recent = message.isRecent();
        this.seen = message.isSeen();
        
        try {
            this.rawFullContent = new ByteArrayInputStream(StreamUtils.toByteArray(message.getFullContent()));
        } catch (IOException e) {
            throw new MailboxException(HumanReadableText.FAILURE_MAIL_PARSE,e);
        }
       
        this.bodyStartOctet = (int) (message.getFullContentOctets() - message.getBodyOctets());
        this.headers = new ArrayList<MaildirHeader>();
        
        List<Header> originalHeaders = message.getHeaders();
        for (int i = 0; i < originalHeaders.size(); i++) {
            headers.add(new MaildirHeader(originalHeaders.get(i)));
        }

        PropertyBuilder pBuilder = new PropertyBuilder(message.getProperties());
        this.textualLineCount = pBuilder.getTextualLineCount();
        this.mediaType = pBuilder.getMediaType();
        this.subType = pBuilder.getSubType();
        final List<Property> properties = pBuilder.toProperties();
        this.properties = new ArrayList<MaildirProperty>(properties.size());
        int order = 0;
        for (final Property property:properties) {
            this.properties.add(new MaildirProperty(property, order++));
        }
        // this is a copy and thus new
        newMessage = true;
    }

    /* 
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.AbstractDocument#getBodyStartOctet()
     */
    @Override
    protected int getBodyStartOctet() {
        return bodyStartOctet;
    }

    /* 
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.AbstractDocument#getRawFullContent()
     */
    @Override
    protected InputStream getRawFullContent() {
        return rawFullContent;
    }

    /* 
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.Document#getFullContentOctets()
     */
    public long getFullContentOctets() {
        return size;
    }

    /* 
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.Document#getHeaders()
     */
    public List<Header> getHeaders() {
        return new ArrayList<Header>(headers);
    }

    /* 
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.Document#getMediaType()
     */
    public String getMediaType() {
        return mediaType;
    }

    /* 
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.Document#getProperties()
     */
    public List<Property> getProperties() {
        return new ArrayList<Property>(properties);
    }

    /* 
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.Document#getSubType()
     */
    public String getSubType() {
        return subType;
    }

    /* 
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.Document#getTextualLineCount()
     */
    public Long getTextualLineCount() {
        return textualLineCount;
    }

    /* 
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.MailboxMembership#createFlags()
     */
    public Flags createFlags() {
        final Flags flags = new Flags();

        if (isAnswered()) {
            flags.add(Flags.Flag.ANSWERED);
        }
        if (isDeleted()) {
            flags.add(Flags.Flag.DELETED);
        }
        if (isDraft()) {
            flags.add(Flags.Flag.DRAFT);
        }
        if (isFlagged()) {
            flags.add(Flags.Flag.FLAGGED);
        }
        if (isRecent()) {
            flags.add(Flags.Flag.RECENT);
        }
        if (isSeen()) {
            flags.add(Flags.Flag.SEEN);
        }
        return flags;
    }

    /* 
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.MailboxMembership#getDocument()
     */
    public Message getDocument() {
        return this;
    }

    /* 
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.MailboxMembership#getInternalDate()
     */
    public Date getInternalDate() {
        return internalDate;
    }
    
    /**
     * Set the internal date
     * @param date
     */
    public void setInternalDate(Date date) {
        this.internalDate = date;
    }

    /* 
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.MailboxMembership#getMailboxId()
     */
    public Integer getMailboxId() {
        throw new NotImplementedException();
    }

    /* 
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.MailboxMembership#getUid()
     */
    public long getUid() {
        return uid;
    }
    
    /**
     * Set the Uid
     * @param uid
     */
    public void setUid(long uid) {
        modified = true;
        this.uid = uid;
    }

    /* 
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.MailboxMembership#isAnswered()
     */
    public boolean isAnswered() {
        return answered;
    }

    /* 
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.MailboxMembership#isDeleted()
     */
    public boolean isDeleted() {
        return deleted;
    }

    /* 
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.MailboxMembership#isDraft()
     */
    public boolean isDraft() {
        return draft;
    }

    /* 
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.MailboxMembership#isFlagged()
     */
    public boolean isFlagged() {
        return flagged;
    }

    /* 
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.MailboxMembership#isRecent()
     */
    public boolean isRecent() {
        return recent;
    }

    /* 
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.MailboxMembership#isSeen()
     */
    public boolean isSeen() {
        return seen;
    }

    /* 
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.MailboxMembership#setFlags(javax.mail.Flags)
     */
    public void setFlags(Flags flags) {
        if (flags != null) {
            modified = true;
            answered = flags.contains(Flags.Flag.ANSWERED);
            deleted = flags.contains(Flags.Flag.DELETED);
            draft = flags.contains(Flags.Flag.DRAFT);
            flagged = flags.contains(Flags.Flag.FLAGGED);
            recent = flags.contains(Flags.Flag.RECENT);
            seen = flags.contains(Flags.Flag.SEEN);
        }
    }

    /* 
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.MailboxMembership#unsetRecent()
     */
    public void unsetRecent() {
        modified = true;
        recent = false;
    }
    
    /**
     * Indicates whether this MaildirMessage reflects a new message or one that already
     * exists in the file system.
     * @return true if it is new, false if it already exists
     */
    public boolean isNew() {
        return newMessage;
    }
    
    /**
     * Indicates whether this MaildirMessage has been modified since its creation.
     * @return true if modified (flags, recent or uid changed), false otherwise
     */
    public boolean isModified() {
        return modified;
    }
    
    @Override
    public String toString() {
        StringBuffer theString = new StringBuffer("MaildirMessage ");
        theString.append(uid);
        theString.append(" {");
        Flags flags = createFlags();
        if (flags.contains(Flags.Flag.DRAFT))
            theString.append(MaildirMessageName.FLAG_DRAFT);
        if (flags.contains(Flags.Flag.FLAGGED))
            theString.append(MaildirMessageName.FLAG_FLAGGED);
        if (flags.contains(Flags.Flag.ANSWERED))
            theString.append(MaildirMessageName.FLAG_ANSWERD);
        if (flags.contains(Flags.Flag.SEEN))
            theString.append(MaildirMessageName.FLAG_SEEN);
        if (flags.contains(Flags.Flag.DELETED))
            theString.append(MaildirMessageName.FLAG_DELETED);
        theString.append("} ");
        theString.append(getInternalDate());
        return theString.toString();
    }

}
