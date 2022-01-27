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
package com.t07m.mcsecurity.productoutage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.application.Handler;
import com.t07m.mcsecurity.McSecurity;

import lombok.Getter;

public class ProductOutageHandler extends Handler<McSecurity> {

	private static final Logger logger = LoggerFactory.getLogger(ProductOutageHandler.class);

	private ProductOutageFetcher fetcher;

	private @Getter ProductOutageFile productOutageFile;

	public ProductOutageHandler(McSecurity app) {
		super(app);
	}
	
	public void init() {
		logger.debug("Initializing ProductOutageHandler");
		this.fetcher = new ProductOutageFetcher(this);
		getApp().registerService(fetcher);
	}

	public void cleanup() {
		getApp().registerService(fetcher);
	}

	public void generateProductOutage(byte[] data) {
		if(data != null) {
			productOutageFile = new ProductOutageFile(new String(data));
		}
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
}
