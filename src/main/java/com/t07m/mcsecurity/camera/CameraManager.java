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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.application.Manager;
import com.t07m.mcsecurity.McSecurity;
import com.t07m.mcsecurity.config.SettingsConfig.CameraConfig;

public class CameraManager extends Manager<McSecurity, Camera> {

	public CameraManager(McSecurity app) {
		super(app, new Camera[0]);
	}

	private static Logger logger = LoggerFactory.getLogger(CameraManager.class);

	public void init() {
		logger.debug("Initializing CameraManager");
		for(CameraConfig cameraConfig : getApp().getSettingsConfig().getCameraConfigs()) {
			addCamera(cameraConfig);
		}
	}
	
	private void addCamera(CameraConfig cameraConfig) {
		for(Camera camera : getAllChildren()) {
			if(camera.getConfig().equals(cameraConfig)) {
				return;
			}else if(camera.getConfig().getName().equals(cameraConfig.getName())) {
				logger.warn("Attempted to load camera with duplicate Name! " + cameraConfig.getName());
				return;
			}
		}
		Camera camera = new Camera(cameraConfig);
		addChild(camera);
		logger.info("Loaded Camera: " + cameraConfig.getName());
		if(camera.getConfig().isEnabled()) {
			getApp().registerService(camera.getCameraWatcher());
		}
	}

	public Camera getCamera(String name) {
		for(Camera camera : getAllChildren()) {
			if(camera.getConfig().getName().equals(name)) {
				return camera;
			}
		}
		return null;
	}
	
	public void cleanup() {
		Camera[] cameras = getAllChildren();
		clearChildren();
		for(Camera camera : cameras) {
			getApp().removeService(camera.getCameraWatcher());
		}
	}
	
}
