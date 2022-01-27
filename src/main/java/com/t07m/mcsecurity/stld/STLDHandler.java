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
package com.t07m.mcsecurity.stld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.application.Handler;
import com.t07m.mcsecurity.McSecurity;

import lombok.Getter;

public class STLDHandler extends Handler<McSecurity> {
	
	private static final Logger logger = LoggerFactory.getLogger(STLDHandler.class);

	private STLDFetcher fetcher;
	
	private @Getter STLD stld;
	private long stldTimestamp;
	
	public STLDHandler(McSecurity app) {
		super(app);
	}

	public STLD getSTLD(long minTimeStamp) {
		if(stldTimestamp >= minTimeStamp) {
			return stld;
		}else {
			fetcher.request();
		}
		return null;
	}
	
	boolean generateSTLD(byte[] bytes, long time) {
		if(bytes != null) {
			stld = new STLD(STLDSanitizer.sanitizeTLD(new String(bytes)));
			stldTimestamp = time;
			return true;
		}
		return false;
	}
	
	String getWaystationIP() {
		return getApp().getSettingsConfig().getWaystationIP();
	}
	
	String getWaystationDomain() {
		return getApp().getSettingsConfig().getWaystationDomain();
	}
	
	String getWaystationUsername() {
		return getApp().getSettingsConfig().getWaystationUsername();
	}
	
	String getWaystationPassword() {
		return getApp().getSettingsConfig().getWaystationPassword();
	}
	
	public void init() {
		this.fetcher = new STLDFetcher(this);
		getApp().registerService(fetcher);
	}

	public void cleanup() {
		getApp().removeService(fetcher);
	}

	
	
}
