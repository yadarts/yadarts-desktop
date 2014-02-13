package spare.n52.yadarts.sound;

import org.mockito.Mockito;

import spare.n52.yadarts.entity.PointEvent;

public class CopyOfSoundTest {

	
	public static void main(String[] args){
		

		BasicSoundService soundService = new BasicSoundService();
		
		PointEvent pointEvent = Mockito.mock(PointEvent.class);
		
		Mockito.when(pointEvent.getBaseNumber()).thenReturn(1);
		Mockito.when(pointEvent.getMultiplier()).thenReturn(3);
		
		soundService.onPointEvent(pointEvent);
		
		soundService = null;
		
		System.gc();
	}
	
}
