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
package com.t07m.mcsecurity.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.cubespace.Yamler.Config.YamlConfig;

public class UsersConfig extends YamlConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(UsersConfig.class);

	public UsersConfig() {
		super("users.yml");
		CONFIG_HEADER = new String[] {"McSecurtiy User Config"};
	}
	
	private @Getter @Setter UserConfig[] usersConfig = new UserConfig[] {
			new UserConfig("Default", true, "email", "phone", new String[] {"00000"}, "Default", new String[] {"*"})
	};
	
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public class UserConfig extends YamlConfig {
		private @Getter @Setter String name;
		private @Getter @Setter boolean suppressAlerts;
		private @Getter @Setter String email;
		private @Getter @Setter String phoneNumber;
		private @Getter @Setter String[] stores;
		private @Getter @Setter String userGroup;
		private @Getter @Setter String[] permissions;
	}
	
}
