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
package com.t07m.mcsecurity.camera;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.mcsecurity.McSecurity;
import com.t07m.mcsecurity.config.SettingsConfig.CameraConfig;

import lombok.Getter;
import lombok.Setter;

public class Camera {

	private static final Logger logger = LoggerFactory.getLogger(Camera.class);
	
	private Object syncLock = new Object();

	private ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
	private ArrayList<Long> timestamps = new ArrayList<Long>();

	private @Getter @Setter CameraConfig config;
	private @Getter CameraWatcher cameraWatcher;

	public Camera(CameraConfig config) {
		this.config = config;
		this.cameraWatcher = new CameraWatcher(this);
	}
	
	BufferedImage pullImage() {
		HttpURLConnection connection = null;
		try {
			if(config.getUsername() != null && config.getPassword() != null) {
				Authenticator.setDefault (new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication (config.getUsername(), config.getPassword().toCharArray());
					}
				});
			}
			connection = (HttpURLConnection) new URL(config.getUrl()).openConnection();
			connection.connect();
			BufferedImage image = ImageIO.read(connection.getInputStream());
			connection.disconnect();
			return image;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	void submitImage(BufferedImage image, long timestamp) {
		image = getScaledImage(image, config.getMaxHeight());
		synchronized(syncLock) {
			while(images.size() >= config.getBuffer()) {
				images.remove(0);
				timestamps.remove(0);
			}
			images.add(image);
			timestamps.add(timestamp);
		}
	}

	private BufferedImage getScaledImage(BufferedImage image, int maxHeight) {
		if(image != null) {
			if(image.getHeight() <= maxHeight) {
				return image;
			}else {
				return Scalr.resize(image, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_TO_HEIGHT, 0, maxHeight, Scalr.OP_ANTIALIAS);
			}
		}
		return null;
	}

	public BufferedImage getImage() {
		return getImage(-1);
	}
	
	public BufferedImage getImage(long timestamp) {
		synchronized(syncLock) {
			if(images.size() > 0) {
				if(timestamp == -1) {
					return images.get(images.size()-1);
				}
				int selected = 0;
				long timediff = Long.MAX_VALUE;
				for(int i = 0; i < timestamps.size(); i++) {
					long diff = Math.abs(timestamp - timestamps.get(i));
					if(diff < timediff) {
						timediff = diff;
						selected = i;
					}
				}
				return images.get(selected);
			}
		}
		return getScaledImage(pullImage(), config.getMaxHeight());
	}
}
