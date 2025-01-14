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

import java.util.Arrays;

import org.apache.james.imap.api.ImapConstants;

/**
 * Keys human response text that may be displayed to the user.
 */
public class HumanReadableText {

    public static final HumanReadableText STARTTLS = new HumanReadableText(
            "org.apache.james.imap.STARTTLS", "Begin TLS negotiation now.");
    
    public static final HumanReadableText SELECT = new HumanReadableText(
            "org.apache.james.imap.SELECT", "completed.");

    public static final HumanReadableText UNSEEN = new HumanReadableText(
            "org.apache.james.imap.UNSEEN", "");

    public static final HumanReadableText UID_VALIDITY = new HumanReadableText(
            "org.apache.james.imap.UID_VALIDITY", "");

    public static final HumanReadableText PERMANENT_FLAGS = new HumanReadableText(
            "org.apache.james.imap.PERMANENT_FLAGS", "");

    public static final HumanReadableText GENERIC_LSUB_FAILURE = new HumanReadableText(
            "org.apache.james.imap.GENERIC_SUBSCRIPTION_FAILURE",
            "Cannot list subscriptions.");

    public static final HumanReadableText GENERIC_UNSUBSCRIPTION_FAILURE = new HumanReadableText(
            "org.apache.james.imap.GENERIC_SUBSCRIPTION_FAILURE",
            "Cannot unsubscribe.");

    public static final HumanReadableText GENERIC_SUBSCRIPTION_FAILURE = new HumanReadableText(
            "org.apache.james.imap.GENERIC_SUBSCRIPTION_FAILURE",
            "Cannot subscribe.");

    public static final HumanReadableText INVALID_MESSAGESET = new HumanReadableText(
            "org.apache.james.imap.INVALID_MESSAGESET",
            "failed. Invalid messageset.");
    
    public static final HumanReadableText INVALID_COMMAND = new HumanReadableText(
            "org.apache.james.imap.INVALID_COMMAND",
            "failed. Command not valid in this state.");

    public static final HumanReadableText ILLEGAL_TAG = new HumanReadableText(
            "org.apache.james.imap.ILLEGAL_TAG", "Illegal tag.");

    public static final HumanReadableText FAILURE_EXISTS_COUNT = new HumanReadableText(
            "org.apache.james.imap.FAILURE_EXISTS_COUNT", "Cannot count number of existing records.");
    
    public static final HumanReadableText FAILURE_TO_LOAD_FLAGS= new HumanReadableText(
            "org.apache.james.imap.FAILURE_TO_LOAD_FLAGS", "Failed to retrieve flags data.");   
    
    public static final HumanReadableText ILLEGAL_ARGUMENTS = new HumanReadableText(
            "org.apache.james.imap.ILLEGAL_ARGUMENTS",
            "failed. Illegal arguments.");

    public static final HumanReadableText FAILURE_MAIL_PARSE = new HumanReadableText(
            "org.apache.james.imap.FAILURE_MAIL_PARSE",
            "failed. Mail cannot be parsed.");

    public static final HumanReadableText FAILURE_NO_SUCH_MAILBOX = new HumanReadableText(
            "org.apache.james.imap.FAILURE_NO_SUCH_MAILBOX",
            "failed. No such mailbox.");

    public static final HumanReadableText START_TRANSACTION_FAILED = new HumanReadableText(
            "org.apache.james.imap.START_TRANSACTION_FAILED",
            "failed. Cannot start transaction.");
    
    public static final HumanReadableText COMMIT_TRANSACTION_FAILED = new HumanReadableText(
            "org.apache.james.imap.COMMIT_TRANSACTION_FAILED",
            "failed. Transaction commit failed.");
    
    public static final HumanReadableText DELETED_FAILED = new HumanReadableText(
            "org.apache.james.imap.DELETED_FAILED",
            "failed. Deletion failed.");
    
    public static final HumanReadableText SEARCH_FAILED = new HumanReadableText(
            "org.apache.james.imap.SEARCH_FAILED",
            "failed. Search failed.");
    
    public static final HumanReadableText COUNT_FAILED = new HumanReadableText(
            "org.apache.james.imap.COUNT_FAILED",
            "failed. Count failed.");
    
    public static final HumanReadableText SAVE_FAILED = new HumanReadableText(
            "org.apache.james.imap.SAVE_FAILED",
            "failed. Save failed.");
    
    public static final HumanReadableText UNSUPPORTED_SEARCH = new HumanReadableText(
            "org.apache.james.imap.UNSUPPORTED_SEARCH",
            "failed. Unsupported search.");
  
    public static final HumanReadableText LOCK_FAILED = new HumanReadableText(
            "org.apache.james.imap.LOCK_FAILED",
            "failed. Failed to lock mailbox.");
    
    public static final HumanReadableText UNSUPPORTED = new HumanReadableText(
            "org.apache.james.imap.UNSUPPORTED",
            "failed. Unsupported operation.");
    
    public static final HumanReadableText DUPLICATE_MAILBOXES = new HumanReadableText(
            "org.apache.james.imap.DUPLICATE_MAILBOXES",
            "failed. Expected unique mailbox but duplicate exists.");
    
    public static final HumanReadableText MAILBOX_EXISTS = new HumanReadableText(
            "org.apache.james.imap.MAILBOX_EXISTS",
            "failed. Mailbox already exists.");
    
    public static final HumanReadableText MAILBOX_NOT_FOUND = new HumanReadableText(
            "org.apache.james.imap.MAILBOX_NOT_FOUND",
            "failed. Mailbox not found.");
    
    public static final HumanReadableText MAILBOX_DELETED = new HumanReadableText(
            "org.apache.james.imap.MAILBOX_DELETED",
            "failed. Mailbox has been deleted.");
    
    public static final HumanReadableText COMSUME_UID_FAILED = new HumanReadableText(
            "org.apache.james.imap.COMSUME_UID_FAILED",
            "failed. Failed to acquire UID.");
    
    public static final HumanReadableText GENERIC_FAILURE_DURING_PROCESSING = new HumanReadableText(
            "org.apache.james.imap.GENERIC_FAILURE_DURING_PROCESSING",
            "processing failed.");

    public static final HumanReadableText FAILURE_MAILBOX_EXISTS = new HumanReadableText(
            "org.apache.james.imap.FAILURE_NO_SUCH_MAILBOX",
            "failed. Mailbox already exists.");
    
    public static final HumanReadableText INIT_FAILED = new HumanReadableText(
            "org.apache.james.imap.INIT_FAILED",
            "failed. Cannot initialise.");

    public static final HumanReadableText SOCKET_IO_FAILURE = new HumanReadableText(
            "org.apache.james.imap.SOCKET_IO_FAILURE",
            "failed. IO failure.");

    public static final HumanReadableText BAD_IO_ENCODING = new HumanReadableText(
            "org.apache.james.imap.BAD_IO_ENCODING",
            "failed. Illegal encoding.");   
    public static final HumanReadableText COMPLETED = new HumanReadableText(
            "org.apache.james.imap.COMPLETED", "completed.");

    public static final HumanReadableText INVALID_LOGIN = new HumanReadableText(
            "org.apache.james.imap.INVALID_LOGIN",
            "failed. Invalid login/password.");

    public static final HumanReadableText UNSUPPORTED_SEARCH_CRITERIA = new HumanReadableText(
            "org.apache.james.imap.UNSUPPORTED_CRITERIA",
            "failed. One or more search criteria is unsupported.");

    public static final HumanReadableText UNSUPPORTED_AUTHENTICATION_MECHANISM = new HumanReadableText(
            "org.apache.james.imap.UNSUPPORTED_AUTHENTICATION_MECHANISM",
            "failed. Authentication mechanism is unsupported.");

    public static final HumanReadableText UNKNOWN_COMMAND = new HumanReadableText(
            "org.apache.james.imap.UNKNOWN_COMMAND", "failed. Unknown command.");

    public static final HumanReadableText BAD_CHARSET = new HumanReadableText(
            "org.apache.james.imap.BAD_CHARSET",
            "failed. Charset is unsupported.");

    public static final HumanReadableText MAILBOX_IS_READ_ONLY = new HumanReadableText(
            "org.apache.james.imap.MAILBOX_IS_READ_ONLY",
            "failed. Mailbox is read only.");

    public static final HumanReadableText BYE = new HumanReadableText(
            "org.apache.james.imap.BYE", ImapConstants.VERSION
                    + " Server logging out");

    public static final HumanReadableText TOO_MANY_FAILURES = new HumanReadableText(
            "org.apache.james.imap.TOO_MANY_FAILURES",
            "Login failed too many times.");

    public static final HumanReadableText BYE_UNKNOWN_COMMAND = new HumanReadableText(
            "org.apache.james.imap.BYE_UNKNOWN_COMMAND", "Unknown command.");

    public static final HumanReadableText IDLING = new HumanReadableText(
            "org.apache.james.imap.IDLING", "Idling");
    
    public static final HumanReadableText UNSELECT = new HumanReadableText(
            "org.apache.james.imap.UNSELECT", "No Mailbox selected.");
    private final String defaultValue;

    private final String key;
    
    private final Object[] parameters;


    public HumanReadableText(final String key, final String defaultValue) {
        this(key, defaultValue, (Object[])null);
    }
    
    public HumanReadableText(final String key, final String defaultValue, final Object... parameters) {
        super();
        this.defaultValue = defaultValue;
        this.key = key;
        this.parameters = parameters;
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

    /**
     * Gets parameters that may be substituted into the text.
     * @return substitution paramters, possibly null
     */
    public Object[] getParameters() {
        return parameters;
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = PRIME * result + ((key == null) ? 0 : key.hashCode());
        result = PRIME * result + Arrays.hashCode(parameters);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final HumanReadableText other = (HumanReadableText) obj;
        if (defaultValue == null) {
            if (other.defaultValue != null)
                return false;
        } else if (!defaultValue.equals(other.defaultValue))
            return false;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        if (!Arrays.equals(parameters, other.parameters))
            return false;
        return true;
    }

    public String toString() {
        return defaultValue;
    }
}
