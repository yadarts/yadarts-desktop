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
