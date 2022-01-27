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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
		String[] directories = smb.listDirectories("/d/NewPos61/STLD/TEMP");
		if(directories != null) {
			LocalDate date = null;
			for(String s : directories) {
				s = s.replace("/", "");
				try {
					LocalDate d = LocalDate.parse(s, DateTimeFormatter.BASIC_ISO_DATE);
					if(date == null || d.isAfter(date)) {
						date = d;
					}
				}catch(Exception e) {}
			}
			if(date != null) {
				long timestamp = System.currentTimeMillis();
				byte[] data = smb.readFile("/d/NewPos61/STLD/TEMP/" + DateTimeFormatter.BASIC_ISO_DATE.format(date) + "/STLD.TLD");
				if(data != null) {
					handler.generateProductOutage(data);
				}
			}
		}
	}
}
