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

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ProductOutageFile {

	private static final Logger logger = LoggerFactory.getLogger(ProductOutageFile.class);

	private Document document;
	private String raw;
	
	ProductOutageFile(String xml) {
		this.raw = xml;
	}
	
	public Document getDocument() {
		if(document == null) {
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db;
				db = dbf.newDocumentBuilder();
				InputSource is = new InputSource(new StringReader(raw));
				document = db.parse(is);
				raw = null;
				document.getDocumentElement().normalize();
			} catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			}
		}
		return document;
	}	
}
