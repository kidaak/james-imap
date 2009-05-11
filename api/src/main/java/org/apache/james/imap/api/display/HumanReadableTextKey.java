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

package org.apache.james.imap.api.display;

import org.apache.james.imap.api.ImapConstants;

/**
 * Keys human response text that may be displayed to the user.
 */
public class HumanReadableTextKey {

    public static final HumanReadableTextKey SELECT = new HumanReadableTextKey(
            "org.apache.james.imap.SELECT", "completed.");

    public static final HumanReadableTextKey UNSEEN = new HumanReadableTextKey(
            "org.apache.james.imap.UNSEEN", "");

    public static final HumanReadableTextKey UID_VALIDITY = new HumanReadableTextKey(
            "org.apache.james.imap.UID_VALIDITY", "");

    public static final HumanReadableTextKey PERMANENT_FLAGS = new HumanReadableTextKey(
            "org.apache.james.imap.PERMANENT_FLAGS", "");

    public static final HumanReadableTextKey GENERIC_LSUB_FAILURE = new HumanReadableTextKey(
            "org.apache.james.imap.GENERIC_SUBSCRIPTION_FAILURE",
            "Cannot list subscriptions.");

    public static final HumanReadableTextKey GENERIC_UNSUBSCRIPTION_FAILURE = new HumanReadableTextKey(
            "org.apache.james.imap.GENERIC_SUBSCRIPTION_FAILURE",
            "Cannot unsubscribe.");

    public static final HumanReadableTextKey GENERIC_SUBSCRIPTION_FAILURE = new HumanReadableTextKey(
            "org.apache.james.imap.GENERIC_SUBSCRIPTION_FAILURE",
            "Cannot subscribe.");

    public static final HumanReadableTextKey INVALID_COMMAND = new HumanReadableTextKey(
            "org.apache.james.imap.INVALID_COMMAND",
            "failed. Command not valid in this state.");

    public static final HumanReadableTextKey ILLEGAL_TAG = new HumanReadableTextKey(
            "org.apache.james.imap.ILLEGAL_TAG", "Illegal tag.");

    public static final HumanReadableTextKey FAILURE_EXISTS_COUNT = new HumanReadableTextKey(
            "org.apache.james.imap.FAILURE_EXISTS_COUNT", "Cannot count number of existing records.");
    
    public static final HumanReadableTextKey FAILURE_TO_LOAD_FLAGS= new HumanReadableTextKey(
            "org.apache.james.imap.FAILURE_TO_LOAD_FLAGS", "Failed to retrieve flags data.");   
    
    public static final HumanReadableTextKey ILLEGAL_ARGUMENTS = new HumanReadableTextKey(
            "org.apache.james.imap.ILLEGAL_ARGUMENTS",
            "failed. Illegal arguments.");

    public static final HumanReadableTextKey FAILURE_MAIL_PARSE = new HumanReadableTextKey(
            "org.apache.james.imap.FAILURE_MAIL_PARSE",
            "failed. Mail cannot be parsed.");

    public static final HumanReadableTextKey FAILURE_NO_SUCH_MAILBOX = new HumanReadableTextKey(
            "org.apache.james.imap.FAILURE_NO_SUCH_MAILBOX",
            "failed. No such mailbox.");

    public static final HumanReadableTextKey START_TRANSACTION_FAILED = new HumanReadableTextKey(
            "org.apache.james.imap.START_TRANSACTION_FAILED",
            "failed. Cannot start transaction.");
    
    public static final HumanReadableTextKey COMMIT_TRANSACTION_FAILED = new HumanReadableTextKey(
            "org.apache.james.imap.COMMIT_TRANSACTION_FAILED",
            "failed. Transaction commit failed.");
    
    public static final HumanReadableTextKey DELETED_FAILED = new HumanReadableTextKey(
            "org.apache.james.imap.DELETED_FAILED",
            "failed. Deletion failed.");
    
    public static final HumanReadableTextKey SEARCH_FAILED = new HumanReadableTextKey(
            "org.apache.james.imap.SEARCH_FAILED",
            "failed. Search failed.");
    
    public static final HumanReadableTextKey COUNT_FAILED = new HumanReadableTextKey(
            "org.apache.james.imap.COUNT_FAILED",
            "failed. Count failed.");
    
    public static final HumanReadableTextKey SAVE_FAILED = new HumanReadableTextKey(
            "org.apache.james.imap.SAVE_FAILED",
            "failed. Save failed.");
    
    public static final HumanReadableTextKey UNSUPPORTED_SEARCH = new HumanReadableTextKey(
            "org.apache.james.imap.UNSUPPORTED_SEARCH",
            "failed. Unsupported search.");
  
    public static final HumanReadableTextKey LOCK_FAILED = new HumanReadableTextKey(
            "org.apache.james.imap.LOCK_FAILED",
            "failed. Failed to lock mailbox.");
    
    public static final HumanReadableTextKey UNSUPPORTED = new HumanReadableTextKey(
            "org.apache.james.imap.UNSUPPORTED",
            "failed. Unsupported operation.");
    
    public static final HumanReadableTextKey DUPLICATE_MAILBOXES = new HumanReadableTextKey(
            "org.apache.james.imap.DUPLICATE_MAILBOXES",
            "failed. Expected unique mailbox but duplicate exists.");
    
    public static final HumanReadableTextKey MAILBOX_EXISTS = new HumanReadableTextKey(
            "org.apache.james.imap.MAILBOX_EXISTS",
            "failed. Mailbox already exists.");
    
    public static final HumanReadableTextKey MAILBOX_NOT_FOUND = new HumanReadableTextKey(
            "org.apache.james.imap.MAILBOX_NOT_FOUND",
            "failed. Mailbox not found.");
    
    public static final HumanReadableTextKey MAILBOX_DELETED = new HumanReadableTextKey(
            "org.apache.james.imap.MAILBOX_DELETED",
            "failed. Mailbox has been deleted.");
    
    public static final HumanReadableTextKey COMSUME_UID_FAILED = new HumanReadableTextKey(
            "org.apache.james.imap.COMSUME_UID_FAILED",
            "failed. Failed to acquire UID.");
    
    public static final HumanReadableTextKey GENERIC_FAILURE_DURING_PROCESSING = new HumanReadableTextKey(
            "org.apache.james.imap.GENERIC_FAILURE_DURING_PROCESSING",
            "processing failed.");

    public static final HumanReadableTextKey FAILURE_MAILBOX_EXISTS = new HumanReadableTextKey(
            "org.apache.james.imap.FAILURE_NO_SUCH_MAILBOX",
            "failed. Mailbox already exists.");

    public static final HumanReadableTextKey SOCKET_IO_FAILURE = new HumanReadableTextKey(
            "org.apache.james.imap.SOCKET_IO_FAILURE",
            "failed. IO failure.");

    public static final HumanReadableTextKey BAD_IO_ENCODING = new HumanReadableTextKey(
            "org.apache.james.imap.BAD_IO_ENCODING",
            "failed. Illegal encoding.");   
    public static final HumanReadableTextKey COMPLETED = new HumanReadableTextKey(
            "org.apache.james.imap.COMPLETED", "completed.");

    public static final HumanReadableTextKey INVALID_LOGIN = new HumanReadableTextKey(
            "org.apache.james.imap.INVALID_LOGIN",
            "failed. Invalid login/password.");

    public static final HumanReadableTextKey UNSUPPORTED_SEARCH_CRITERIA = new HumanReadableTextKey(
            "org.apache.james.imap.UNSUPPORTED_CRITERIA",
            "failed. One or more search criteria is unsupported.");

    public static final HumanReadableTextKey UNSUPPORTED_AUTHENTICATION_MECHANISM = new HumanReadableTextKey(
            "org.apache.james.imap.UNSUPPORTED_AUTHENTICATION_MECHANISM",
            "failed. Authentication mechanism is unsupported.");

    public static final HumanReadableTextKey UNKNOWN_COMMAND = new HumanReadableTextKey(
            "org.apache.james.imap.UNKNOWN_COMMAND", "failed. Unknown command.");

    public static final HumanReadableTextKey BAD_CHARSET = new HumanReadableTextKey(
            "org.apache.james.imap.BAD_CHARSET",
            "failed. Charset is unsupported.");

    public static final HumanReadableTextKey MAILBOX_IS_READ_ONLY = new HumanReadableTextKey(
            "org.apache.james.imap.MAILBOX_IS_READ_ONLY",
            "failed. Mailbox is read only.");

    public static final HumanReadableTextKey BYE = new HumanReadableTextKey(
            "org.apache.james.imap.BYE", ImapConstants.VERSION
                    + " Server logging out");

    public static final HumanReadableTextKey TOO_MANY_FAILURES = new HumanReadableTextKey(
            "org.apache.james.imap.TOO_MANY_FAILURES",
            "Login failed too many times.");

    public static final HumanReadableTextKey BYE_UNKNOWN_COMMAND = new HumanReadableTextKey(
            "org.apache.james.imap.BYE_UNKNOWN_COMMAND", "Unknown command.");

    private final String defaultValue;

    private final String key;

    public HumanReadableTextKey(final String key, final String defaultValue) {
        super();
        this.defaultValue = defaultValue;
        this.key = key;
    }

    /**
     * Gets the default value for this text.
     * 
     * @return default human readable text, not null
     */
    public final String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Gets a unique key that can be used to loopup the text. How this is
     * performed is implementation independent.
     * 
     * @return key value, not null
     */
    public final String getKey() {
        return key;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final HumanReadableTextKey other = (HumanReadableTextKey) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        return true;
    }

    public String toString() {
        return defaultValue;
    }
}
