/**
 * Copyright 2014 the staff of 52°North Initiative for Geospatial Open
 * Source Software GmbH in their free time
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spare.n52.yadarts.sound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kuusisto.tinysound.TinySound;

import spare.n52.yadarts.common.Services;
import spare.n52.yadarts.config.Configuration;

public class SoundExecutor {

	private String soundPackage;
	private Map<SoundId, Sound> idToSoundMap = new HashMap<>();
	private ExecutorService executor = Executors.newFixedThreadPool(3);
	
	public SoundExecutor() {
		TinySound.init();
	}
	
	private List<Sound> resolveSounds(List<SoundId> theSounds) {
		List<Sound> result = new ArrayList<>(theSounds.size());
		
		for (SoundId sound : theSounds) {
			if (!idToSoundMap.containsKey(sound)) {
				idToSoundMap.put(sound, new Sound(getSoundPackageName(), sound));
			}
			
			result.add(idToSoundMap.get(sound));
		}
		
		return result;
	}

	public void shutdown() {
		this.executor.shutdown();
		TinySound.shutdown();
	}
	
	private String getSoundPackageName() {
		synchronized (this) {
			if (soundPackage == null) {
				soundPackage = Services.getImplementation(Configuration.class).getSoundPackage();
			}
		}
		
		return soundPackage;
	}

	public synchronized void add(SoundId id) {
		add(Collections.singletonList(id));
	}

	public synchronized void add(final List<SoundId> list) {
		this.executor.submit(new Runnable() {

			@Override
			public void run() {
				List<Sound> sounds = resolveSounds(list);
				
				for (Sound sound : sounds) {
					sound.run();
				}
			}
			
		});
	}

	
}
