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
import com.t07m.mcsecurity.config.UsersConfig.UserConfig;

public class UserManager extends Manager<McSecurity, User>{

	private static final Logger logger = LoggerFactory.getLogger(UserManager.class);
	
	public UserManager(McSecurity app) {
		super(app, new User[0]);
	}
	
	public void init() {
		logger.debug("Initializing UserManager");
		for(UserConfig userConfig : getApp().getUsersConfig().getUsersConfig()) {
			addUser(userConfig);
		}
	}

	private void addUser(UserConfig userConfig) {
		for(User user : getAllChildren()) {
			if(user.getConfig().equals(userConfig)) {
				return;
			}else if(user.getConfig().getName().equals(userConfig.getName())) {
				logger.warn("Attempted to load User with duplicate name! " + userConfig.getName());
				return;
			}
		}
		UserGroup group = getApp().getGroupManager().getGroup(userConfig.getUserGroup());
		User user = new User(userConfig, group);
		addChild(user);
		logger.info("Loaded User: " + userConfig.getName());
	}
	
	public void cleanup() {
		clearChildren();
	}
	
	public User[] getAllUsers() {
		return getAllChildren();
	}
	
	public User getUser(String name) {
		for(User user : getAllUsers()) {
			if(user.getConfig().getName().equals(name)) {
				return user;
			}
		}
		return null;
	}

}
