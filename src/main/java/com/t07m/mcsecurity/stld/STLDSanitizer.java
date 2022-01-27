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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class STLDSanitizer {
	
	private static final Logger logger = LoggerFactory.getLogger(STLDSanitizer.class);

	private static final String EVENT_REGEX = "<Event[^>]*>.*?</Event>";
	
	public static String sanitizeTLD(String result) {
		Pattern p = Pattern.compile(EVENT_REGEX);
		Matcher matcher = p.matcher(result);
		StringBuilder sb = new StringBuilder();
		sb.append("<log>");
		while (matcher.find()) {
			sb.append(matcher.group());
		}
		sb.append("</log>");
		return sb.toString();
	}

	public static String sanitizeSTLD(String result) {
		result = result.replaceAll("#END#", "");
		return result;
	}
	
}
