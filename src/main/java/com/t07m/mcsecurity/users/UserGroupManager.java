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

import com.t07m.application.Manager;
import com.t07m.mcsecurity.McSecurity;
import com.t07m.mcsecurity.config.GroupsConfig.UserGroupConfig;

public class UserGroupManager extends Manager<McSecurity, UserGroup>{

	private static final Logger logger = LoggerFactory.getLogger(UserGroupManager.class);
	
	public UserGroupManager(McSecurity app) {
		super(app, new UserGroup [0]);
	}

	public void init() {
		logger.debug("Initilizing UserGroupManager");
		for(UserGroupConfig groupConfig : getApp().getGroupsConfig().getUserGroupConfigs()) {
			addGroup(groupConfig);
		}
	}
	
	private void addGroup(UserGroupConfig groupConfig) {
		for(UserGroup userGroup : getAllChildren()) {
			if(userGroup.getConfig().equals(groupConfig)) {
				return;
			}else if(userGroup.getConfig().getName().equals(groupConfig.getName())) {
				logger.warn("Attempted to load UserGroup with duplicate name! " + groupConfig.getName());
				return;
			}
		}
		UserGroup userGroup = new UserGroup(groupConfig);
		addChild(userGroup);
		logger.info("Loaded UserGroup: " + groupConfig.getName());
	}

	public void cleanup() {
		clearChildren();
	}

	public UserGroup[] getAllGroups() {
		return getAllChildren();
	}
	
	public UserGroup getGroup(String name) {
		for(UserGroup group : getAllGroups()) {
			if(group.getConfig().getName().equals(name)) {
				return group;
			}
		}
		return null;
	}
	
}
