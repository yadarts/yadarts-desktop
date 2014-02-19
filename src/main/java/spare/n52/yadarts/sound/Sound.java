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

import java.net.URL;

import kuusisto.tinysound.Music;
import kuusisto.tinysound.TinySound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sound implements Runnable {
	
	private static final Logger logger = LoggerFactory.getLogger(Sound.class);

	private static final String WAVE_SUFFIX_STRING = "wav";

	private Music clip;

	private int frameLength;
	
    public Sound(final String resourcename, final SoundId soundId) {
    	if (soundId.equals(SoundId.None)) {
    		logger.debug("'{}' sound selected --> skipping",soundId.name());
    		return;
    	}
    	final String resourcePath = "/sounds/"+ resourcename + "/" + soundId.name().toLowerCase() + "." + WAVE_SUFFIX_STRING;
        logger.debug("Try to load resource '{}'", resourcePath);

        final URL resource = getClass().getResource(resourcePath);
        
        if (resource == null) {
        	return;
        }
        
        // open the audio input stream
        clip = TinySound.loadMusic(resource);
    }

	@Override
	public synchronized void run() {
		if (clip != null) {
			/*
			 * rewind
			 */
			clip.play(false);
			try {
				while (!clip.playing()) {
					Thread.sleep(10);
				}
				while (clip.playing()) {
					Thread.sleep(10);
				}	
			} catch (InterruptedException e) {
				logger.warn(e.getMessage(), e);
			}
			
			clip.stop();
			this.frameLength = clip.getLoopPositionByFrame();
			clip.rewind();
		}
	}

    public synchronized void stop(){
    	if (clip != null) {
    		clip.stop();
	    }
	}
}