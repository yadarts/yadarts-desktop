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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kuusisto.tinysound.TinySound;
import spare.n52.yadarts.common.Services;
import spare.n52.yadarts.config.Configuration;

public class SoundExecutor {

	public static final Logger logger = LoggerFactory.getLogger(SoundExecutor.class);
	private String soundPackage;
	private Map<SoundId, Sound> idToSoundMap = new HashMap<>();
	public boolean running = true;
	private ExecutorThread executor;
	
	public SoundExecutor() {
		startThread();
	}
	
	private void startThread() {
		executor = new ExecutorThread();
		Thread t = new Thread(executor);
		t.start();
	}

	private SoundSequence resolveSounds(List<SoundId> theSounds) {
		SoundSequence result = new SoundSequence(theSounds.size());
		
		for (SoundId sound : theSounds) {
			if (!idToSoundMap.containsKey(sound)) {
				idToSoundMap.put(sound, new Sound(getSoundPackageName(), sound));
			}
			
			logger.info("adding ID to sequence: {}", sound);
			result.add(idToSoundMap.get(sound));
		}
		
		return result;
	}

	public void shutdown() {
		this.running = false;
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
		SoundSequence sounds = resolveSounds(list);
		executor.setPendingSequence(sounds);
	}

	private class ExecutorThread implements Runnable {

		private Queue<SoundSequence> pending = new LinkedList<>();
		private Object mutex = new Object();
		private SoundSequence current;
		private int pendingSize;

		@Override
		public void run() {
			TinySound.init();
			
			while (running) {
				synchronized (mutex) {
					while (pending.isEmpty()) {
						try {
							logger.info("waiting for pending");
							mutex.wait();
							logger.info("got pending");
						} catch (InterruptedException e) {
							logger.warn(e.getMessage(), e);
						}
					}
					
					this.current = pending.poll();
					this.pendingSize = pending.size();
				}
				
				List<Sound> sounds = this.current.getSounds();

				if (sounds.size() == 0) {
					continue;
				}
				
				/*
				 * play at least the first in the sequence
				 */
				logger.info("playing current sequence: {}", sounds.size());
				sounds.get(0).run();
				for (int i = 1; i < sounds.size(); i++) {
					if (running && this.pendingSize == this.pending.size()) {
						sounds.get(i).run();
					}
				}
			}
			
			TinySound.shutdown();
		}

		public void setPendingSequence(SoundSequence sounds) {
			this.pending.add(sounds);
			
			synchronized (mutex) {
				this.mutex.notifyAll();	
			}
			
		}
		
	}
	
}
