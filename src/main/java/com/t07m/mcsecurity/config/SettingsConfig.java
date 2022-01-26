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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.cubespace.Yamler.Config.YamlConfig;

public class SettingsConfig extends YamlConfig {

	public SettingsConfig() {
		super("settings.yml");
		CONFIG_HEADER = new String[] {"McSecurity Settings Config"};
	}
	
	private @Getter @Setter String appName = "McSecurity";
	private @Getter @Setter String store = "00000";
	
	private @Getter @Setter KeystationConfig[] keystationConfigs = new KeystationConfig[] {
			new KeystationConfig("Default", false, "10.0.0.0", ".", "user", "password", "/path", new String[] {"Camera01"})
	};
	
	private @Getter @Setter CameraConfig[] cameraConfigs = new CameraConfig[] {
			new CameraConfig("Default", false, "url", "user", "pass")
	};
	
	private @Getter @Setter String SMTPHost = "";
	private @Getter @Setter String SMTPPort = "";
	private @Getter @Setter String SMTPSSL = "";
	private @Getter @Setter String SMTPUser = "";
	private @Getter @Setter String SMTPPassword = "";
	
	private @Getter @Setter String TwilioAccountSID = "";
	private @Getter @Setter String TwilioAuthToken = "";
	private @Getter @Setter String TwilioPhoneNumber = "";
	
	private @Getter @Setter boolean AutoHide = true;
	
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public class KeystationConfig extends YamlConfig {
		private @Getter @Setter String name;
		private @Getter @Setter boolean enabled;
		private @Getter @Setter String ip;
		private @Getter @Setter String domain;
		private @Getter @Setter String username;
		private @Getter @Setter String password;
		private @Getter @Setter String reprintpath;
		private @Getter @Setter String[] cameras;
	}
	
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public class CameraConfig extends YamlConfig {
		private @Getter @Setter String name;
		private @Getter @Setter boolean enabled;
		private @Getter @Setter String url;
		private @Getter @Setter String username;
		private @Getter @Setter String password;
	}
	
}
