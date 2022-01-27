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
package com.t07m.mcsecurity;

import java.awt.Frame;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javafaker.Faker;
import com.github.zafarkhaja.semver.Version;
import com.t07m.application.Application;
import com.t07m.application.Manager;
import com.t07m.console.swing.ConsoleWindow;
import com.t07m.mcsecurity.camera.CameraManager;
import com.t07m.mcsecurity.config.AlertsConfig;
import com.t07m.mcsecurity.config.DataConfig;
import com.t07m.mcsecurity.config.GroupsConfig;
import com.t07m.mcsecurity.config.SettingsConfig;
import com.t07m.mcsecurity.config.UsersConfig;

import lombok.Getter;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.cubespace.Yamler.Config.YamlConfig;

public class McSecurity extends Application {

	public static final Version VERSION = Version.valueOf("0.0.1");
	
	private static Logger logger = LoggerFactory.getLogger(McSecurity.class);
	
	private static String identity;
	
	private @Getter AlertsConfig alertsConfig;
	private @Getter DataConfig dataConfig;
	private @Getter GroupsConfig groupsConfig;
	private @Getter SettingsConfig settingsConfig;
	private @Getter UsersConfig usersConfig;
	
	private @Getter CameraManager cameraManager;
	
	public static void main(String[] args) {
		boolean gui = true;
		if(args.length > 0) {
			for(String arg : args) {
				if(arg.equalsIgnoreCase("-nogui")) {
					gui = false;
				}
			}
		}
		new McSecurity(gui).start();
	}

	public McSecurity(boolean gui) {
		super(gui, "McSecurity");
	}

	@SuppressWarnings("rawtypes")
	public void init() {
		logger.info("Loading configuration files.");
		this.alertsConfig = new AlertsConfig();
		this.dataConfig = new DataConfig();
		this.groupsConfig = new GroupsConfig();
		this.settingsConfig = new SettingsConfig();
		this.usersConfig = new UsersConfig();
		for(YamlConfig config : new YamlConfig[] {
				this.alertsConfig,
				this.dataConfig,
				this.groupsConfig,
				this.settingsConfig,
				this.usersConfig
		}) {
			try {
				config.init();
				config.save();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
				logger.error("Unable to load " + config.getClass().getSimpleName() + " file!");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {}
				System.exit(-1);
			}
		}
		logger.info("Launching Application - " + getIdentity() + " Store:" + settingsConfig.getStore());
		this.cameraManager = new CameraManager(this);
		for(Manager manager : new Manager[] {
				this.cameraManager
		}) {
			manager.init();
		}
		if(this.getConsole() instanceof ConsoleWindow) {
			if(settingsConfig.isAutoHide())
				((ConsoleWindow)(this.getConsole())).setState(Frame.ICONIFIED);
		}
	}
	
	public static String getIdentity() {
		if(identity == null) {
			Faker faker = new Faker(new Locale("en-US"));
			identity = faker.name().firstName() + " " + faker.name().lastName();
		}
		return identity;		
	}
	
}
