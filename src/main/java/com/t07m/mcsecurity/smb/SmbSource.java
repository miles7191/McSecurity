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
package com.t07m.mcsecurity.smb;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jcifs.CIFSContext;
import jcifs.CIFSException;
import jcifs.SmbResource;
import jcifs.context.SingletonContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SmbSource {

	private static final Logger logger = LoggerFactory.getLogger(SmbSource.class);
	
	private final String ip;
	private final String domain;
	private final String username;
	private final String password;

	public long getLastModified(String path) {
		try {
			SmbResource resource = getContext().get(getRemoteURL(path));
			return resource.lastModified();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public byte[] readFile(String path) {
		try {
			SmbResource resource = getContext().get(getRemoteURL(path));
			if(resource.isFile()) {
				InputStream is = resource.openInputStream();
				byte[] data = IOUtils.toByteArray(is);
				is.close();
				return data;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String[] list(String path) {
		return list(path, "*");
	}
	
	public String[] list(String path, String wildcard) {
		try {
			if(!path.endsWith("/"))
				path = path + "/";
			SmbResource resource = getContext().get(getRemoteURL(path));
			if(resource.isDirectory()) {
				List<String> names = new ArrayList<String>();
				Iterator<SmbResource> itr = resource.children(wildcard);
				while(itr.hasNext()) {
					names.add(itr.next().getName());
				}
				return names.toArray(new String[names.size()]);
			}
		} catch (CIFSException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String[] listDirectories(String path) {
		return listDirectories(path, "*");
	}


	public String[] listDirectories(String path, String wildcard) {
		try {
			if(!path.endsWith("/"))
				path = path + "/";
			SmbResource resource = getContext().get(getRemoteURL(path));
			if(resource.isDirectory()) {
				List<String> names = new ArrayList<String>();
				Iterator<SmbResource> itr = resource.children(wildcard);
				while(itr.hasNext()) {
					SmbResource res = itr.next();
					if(res.isDirectory()) {
						names.add(res.getName());
					}
				}
				return names.toArray(new String[names.size()]);
			}
		} catch (CIFSException e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	private CIFSContext getContext() {
		return SingletonContext.getInstance().withCredentials(getAuth());
	}

	private NtlmPasswordAuthenticator getAuth() {
		return new NtlmPasswordAuthenticator(domain, username, password);
	}

	private String getRemoteURL(String path) {
		if(!path.startsWith("/") && !ip.endsWith("/"))
			path = "/" + path;
		return "smb://" + ip + path;
	}	

}
