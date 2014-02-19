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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import kuusisto.tinysound.TinySound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spare.n52.yadarts.common.Services;
import spare.n52.yadarts.config.Configuration;

public class SoundExecutorThread implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(SoundExecutorThread.class);
	private final Queue<SoundId> soundIdQueueQueue = new LinkedList<SoundId>();
	private boolean running = true;
	private String soundPackage;
	private Map<SoundId, Sound> idToSoundMap = new HashMap<>();
	
	public SoundExecutorThread() {
		TinySound.init();
	}
	
	@Override
	public void run() {
		while (running) {
			SoundId theSound;
			synchronized (this) {
				while (soundIdQueueQueue.isEmpty()) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						logger.warn(e.getMessage());
					}
				}
				
				theSound = soundIdQueueQueue.poll();
			}
			
			Sound sound = resolveSound(theSound);
			
			sound.run();
		}
	}

	private Sound resolveSound(SoundId theSound) {
		if (!idToSoundMap.containsKey(theSound)) {
			idToSoundMap.put(theSound, new Sound(getSoundPackageName(), theSound));
		}
		
		Sound result = idToSoundMap.get(theSound);
		result.stop();
		
		return result;
	}

	public void setRunning(boolean running) {
		this.running = running;
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
		this.soundIdQueueQueue.add(id);
		this.notifyAll();
	}

	
}
