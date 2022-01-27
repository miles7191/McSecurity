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
import java.util.concurrent.TimeUnit;

import com.t07m.application.Service;
import com.t07m.mcsecurity.McSecurity;

public class CameraWatcher extends Service<McSecurity>{

	private Camera camera;
	
	CameraWatcher(Camera camera) {
		super(TimeUnit.SECONDS.toMillis(3));
		this.camera = camera;
	}
	
	public void process() {
		BufferedImage image = camera.pullImage();
		long timestamp = System.currentTimeMillis();
		if(image != null) {
			camera.submitImage(image, timestamp);
		}
	}
	
}
