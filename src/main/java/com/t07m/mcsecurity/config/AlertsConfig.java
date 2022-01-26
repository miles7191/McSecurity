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

import java.io.File;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.YamlConfig;

public class AlertsConfig extends YamlConfig {

	public AlertsConfig() {
		super("alerts.yml");
		CONFIG_HEADER = new String[] {"McSecurity Alert Config"};
	}
	
	private @Getter @Setter ReceiptAlertConfig[] receiptAlertConfigs = new ReceiptAlertConfig[] {
			new ReceiptAlertConfig("Default", false, new String[] {"all"}, -1, -1, 1, -1)
	};
	
	private @Getter @Setter LoyaltyAlertConfig[] loyaltyAlertConfigs = new LoyaltyAlertConfig[] {
			new LoyaltyAlertConfig("Default", false, "", 3, 60)
	};
	
	private @Getter @Setter ProductOutageAlertConfig[] ProductOutageAlertConfigs = new ProductOutageAlertConfig[] {
			new ProductOutageAlertConfig("Default", false, 60, 4320, 22, 5, 4)
	};
	
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public class ReceiptAlertConfig extends YamlConfig {
		private @Getter @Setter String name;
		private @Getter @Setter boolean enabled;
		private @Getter @Setter String[] transactionTypes;
		private @Getter @Setter double minimumValue;
		private @Getter @Setter double maximumValue;
		private @Getter @Setter double minimumSavings;
		private @Getter @Setter double maximumSavings;
	}
	
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public class LoyaltyAlertConfig extends YamlConfig {
		private @Getter @Setter String name;
		private @Getter @Setter boolean enabled;
		@Comment("Leave empty unless blacklisting a specific ID")
		private @Getter @Setter String customerID;
		@Comment("How many of the same customerID should trigger an alert")
		private @Getter @Setter int frequency;
		@Comment("Frequency time frame (minutes)")
		private @Getter @Setter int duration;
	}

	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public class ProductOutageAlertConfig extends YamlConfig {
		private @Getter @Setter String name;
		private @Getter @Setter boolean enabled;
		@Comment("How long a product must be outaged to trigger an alert (minutes)")
		private @Getter @Setter int minDuration;
		@Comment("How long a product must stay in outage before stopping alerts (minutes)")
		private @Getter @Setter int maxDuration;
		@Comment("When outage alerts stop (hour)")
		private @Getter @Setter int suppressStartTime;
		@Comment("When outage alerts begin (hour)")
		private @Getter @Setter int suppressStopTime;
		@Comment("How often an alert should be sent (minutes)")
		private @Getter @Setter int frequency;
	}
}
