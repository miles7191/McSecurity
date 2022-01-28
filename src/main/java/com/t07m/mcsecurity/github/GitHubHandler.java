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
package com.t07m.mcsecurity.github;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.application.Handler;
import com.t07m.mcsecurity.McSecurity;

import net.cubespace.Yamler.Config.InvalidConfigurationException;

public class GitHubHandler extends Handler<McSecurity> {

	private static final Logger logger = LoggerFactory.getLogger(GitHubHandler.class);

	private GitHubFetcher fetcher;

	public GitHubHandler(McSecurity app) {
		super(app);
	}

	public void init() {
		fetcher = new GitHubFetcher(this);
		getApp().registerService(fetcher);
	}

	public void cleanup() {
		getApp().removeService(fetcher);
	}

	void applyNewCommit(String sha1, String date, InputStream alerts, InputStream groups, InputStream users) {
		try {
			FileUtils.copyInputStreamToFile(alerts, new File("alerts.yml"));
			FileUtils.copyInputStreamToFile(groups, new File("groups.yml"));
			FileUtils.copyInputStreamToFile(users, new File("users.yml"));
			getApp().getSettingsConfig().setGitHubCommitSHA1(sha1);
			getApp().getSettingsConfig().setGitHubCommitDate(date);
			getApp().getSettingsConfig().save();
			if(getApp().getSettingsConfig().isAutoRestartConfigured()) {
				logger.info("Stopping application in 5 seconds to apply new configuration.");
				try {
					Thread.sleep(5000);
				} catch(InterruptedException e) {}
				getApp().stop();
			}else {
				logger.info("Restarting application in 5 seconds to apply new configuration.");
				try {
					Thread.sleep(5000);
				} catch(InterruptedException e) {}
				getApp().restart();
			}
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	String getGitHubToken() {
		return getApp().getSettingsConfig().getGitHubToken();
	}

	String getGitHubRepo() {
		return getApp().getSettingsConfig().getGitHubRepo();
	}

	String getGitHubCommitSHA1() {
		return getApp().getSettingsConfig().getGitHubCommitSHA1();
	}

	String getGitHubCommitDate() {
		return getApp().getSettingsConfig().getGitHubCommitDate();
	}
}
