/*
 * Copyright (C) 2022 Matthew Rosato
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.t07m.mcsecurity.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PermissionChecker{

	private static final Logger logger = LoggerFactory.getLogger(PermissionChecker.class);

	static boolean has(User user, String permission) {
		if(!hasNode(user.getConfig().getPermissions(), "-"+permission)) {
			if(hasNode(user.getConfig().getPermissions(), permission)){
				return true;
			}
			if(user.getGroup() != null) {
				if(!hasNode(user.getGroup().getConfig().getPermissions(), "-"+permission)) {
					if(hasNode(user.getGroup().getConfig().getPermissions(), permission)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static boolean hasNode(String[] nodes, String node) {
		if(nodes != null) {
			for(String n : nodes) {
				String[] n1 = n.split("\\.");
				String[] n2 = node.split("\\.");
				if(n1 != null && n2 != null && n1.length == n2.length) {
					if(n1[n1.length-1].equals("*")) {
						boolean match = true;
						for(int i = 0; i < n1.length-1 ; i ++) {
							if(!n1[i].equals(n2[i])) {
								match = false;
								break;
							}
						}
						if(match)
							return true;
					}else {
						boolean match = true;
						for(int i = 0; i < n1.length ; i ++) {
							if(!n1[i].equals(n2[i])) {
								match = false;
								break;
							}
						}
						if(match)
							return true;
					}
				}
			}
		}
		return false;
	}

}
