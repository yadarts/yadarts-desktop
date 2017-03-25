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
package spare.n52.yadarts.sound.tinysound;

import spare.n52.yadarts.sound.tinysound.Sound;
import java.util.ArrayList;
import java.util.List;

/**
 * a {@link SoundSequence} is a series of {@link Sound} objects
 * which form a phrase or a combination of sounds that correspond
 * (e.g. "Triple" + "20" + "You're good!")
 */
public class SoundSequence {

	private List<Sound> sounds;
	
	public SoundSequence() {
		this(1);
	}
	
	public SoundSequence(int length) {
		sounds = new ArrayList<>(length);
	}

	public void add(Sound s) {
		this.sounds.add(s);
	}
	
	public List<Sound> getSounds() {
		return sounds;
	}

	public void setSounds(List<Sound> sounds) {
		this.sounds = sounds;
	}
	
	public void stop() {
		for (Sound s : sounds) {
			s.stop();
		}
	}

}
