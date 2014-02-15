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

import org.junit.Test;
import org.mockito.Mockito;

import spare.n52.yadarts.entity.PointEvent;

public class SoundTest {

	@Test
	public void testFilenames(){
		

		BasicSoundService soundService = new BasicSoundService();	
		
		PointEvent pointEvent = Mockito.mock(PointEvent.class);
		
		Mockito.when(pointEvent.getBaseNumber()).thenReturn(25);
		Mockito.when(pointEvent.getMultiplier()).thenReturn(2);
		
		soundService.onPointEvent(pointEvent);
	}
	
}
