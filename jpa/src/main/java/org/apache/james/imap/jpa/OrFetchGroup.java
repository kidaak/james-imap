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

package org.apache.james.imap.jpa;

import java.util.Set;

import org.apache.james.mailboxmanager.MessageResult.FetchGroup;

/**
 * Wraps a fetch group and ORs content.
 */
public final class OrFetchGroup implements FetchGroup {

    private final FetchGroup delegate;

    private final int or;

    public OrFetchGroup(final FetchGroup delegate, final int or) {
        super();
        this.delegate = delegate;
        this.or = or;
    }

    public int content() {
        return or | delegate.content();
    }

    public String toString() {
        return "Fetch " + or + " OR " + delegate;
    }

    public Set getPartContentDescriptors() {
        return delegate.getPartContentDescriptors();
    }

}