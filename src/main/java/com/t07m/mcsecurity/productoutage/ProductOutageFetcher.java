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

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.application.Service;
import com.t07m.mcsecurity.McSecurity;
import com.t07m.mcsecurity.smb.SmbSource;

public class ProductOutageFetcher extends Service<McSecurity> {

	private static final Logger logger = LoggerFactory.getLogger(ProductOutageFetcher.class);

	private final ProductOutageHandler handler;
	
	public ProductOutageFetcher(ProductOutageHandler handler) {
		super(TimeUnit.MINUTES.toMillis(5));
		this.handler = handler;
	}
	
	public void process() {
		SmbSource smb = new SmbSource(
				handler.getWaystationIP(),
				handler.getWaystationDomain(),
				handler.getWaystationUsername(),
				handler.getWaystationPassword());
		String[] files = smb.list("/d/NewPos61/posdata", "*.xml");
		if(files != null) {
			for(String file : files) {
				if(file.equals("prodoutage.xml")) {
					byte[] data = smb.readFile("/d/NewPos61/posdata/prodoutage.xml");
					if(data != null) {
						handler.generateProductOutage(data);
					}
				}else if(file.equals("names-db.xml")) {
					byte[] data = smb.readFile("/d/NewPos61/posdata/names-db.xml");
					if(data != null) {
						handler.generateNamesDB(data);
					}
				}
			}
		}
	}
}
