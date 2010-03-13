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
package org.apache.james.imap.jcr;

import org.apache.jackrabbit.util.Text;

public class JCRUtils implements JCRImapConstants{

	public static String createPath(String... subNodes) {
		StringBuffer pathBuf = new StringBuffer();
		
		for (int i = 0; i < subNodes.length; i++ ) {
			String path = subNodes[i];
			/*
			if (path.startsWith(PROPERTY_PREFIX) == false) {
				pathBuf.append(PROPERTY_PREFIX);
			}
			*/
			pathBuf.append(Text.escapeIllegalJcrChars(path));
			
			if (i +1 != subNodes.length) {
				pathBuf.append(NODE_DELIMITER);
			}
		}
        return pathBuf.toString();
		
	}
}
