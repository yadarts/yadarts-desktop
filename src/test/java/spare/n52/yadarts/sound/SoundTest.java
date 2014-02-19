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

import java.io.IOException;

import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;

import org.junit.Test;

public class SoundTest {

	@Test
	public void testFilenames() throws IOException, InterruptedException {
		// initialize TinySound
		TinySound.init();
		// load a sound and music
		// note: you can also load with Files, URLs and InputStreams
		Music song = TinySound.loadMusic(getClass().getResource("/sounds/robot/bullseye.wav"));
		Sound coin = TinySound.loadSound(getClass().getResource("/sounds/robot/jingle.wav"));
		// start playing the music on loop
		song.play(true);
		song.playing();
		// play the sound a few times in a loop
		for (int i = 0; i < 20; i++) {
			coin.play();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		// be sure to shutdown TinySound when done
		TinySound.shutdown();
	}

}
