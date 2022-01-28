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

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.application.Service;
import com.t07m.mcsecurity.McSecurity;

public class GitHubFetcher extends Service<McSecurity>{

	private static final Logger logger = LoggerFactory.getLogger(GitHubFetcher.class);

	private final GitHubHandler handler;

	public GitHubFetcher(GitHubHandler handler) {
		super(TimeUnit.MINUTES.toMillis(30));
		this.handler = handler;
	}

	public void process() {
		try {
			String token = handler.getGitHubToken();
			GitHub github = new GitHubBuilder().withOAuthToken(token).build();
			GHRepository repo = github.getRepository(handler.getGitHubRepo());
			Iterator<GHCommit> itr = repo.listCommits().iterator();
			if(itr.hasNext()) {
				GHCommit commit = itr.next();
				if(!handler.getGitHubCommitSHA1().equals(commit.getSHA1())) {
					logger.debug("Found new GitHub Commit.");
					GHContent alerts, groups, users;
					alerts = repo.getFileContent("alerts.yml");
					groups = repo.getFileContent("groups.yml");
					users = repo.getFileContent("users.yml");
					if(alerts != null && groups != null && users != null) {
						handler.applyNewCommit(
								commit.getSHA1(),
								commit.getCommitDate().toString(),
								alerts.read(),
								groups.read(),
								users.read());
					}
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

}
