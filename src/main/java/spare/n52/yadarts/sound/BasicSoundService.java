package spare.n52.yadarts.sound;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;

import spare.n52.yadarts.config.Configuration;
import spare.n52.yadarts.entity.Player;
import spare.n52.yadarts.entity.PointEvent;
import spare.n52.yadarts.games.GameStatusUpdateListener;
import spare.n52.yadarts.games.Score;

public class BasicSoundService implements GameStatusUpdateListener, LineListener {

	private ExecutorService executor;
	private Queue<SoundId> soundIdQueueQueue;
	public static final String SOUND_THEME = "SOUND_THEME";

	public BasicSoundService() {
		executor = Executors.newFixedThreadPool(5);
		soundIdQueueQueue = new LinkedList<SoundId>();
	}

	/**
	 * Play sound.
	 * 
	 * @param id
	 */
	protected void playSound(SoundId id) {
		queueSound(id);
		playQueue();
	}

	protected void playQueue() {
		if (!soundIdQueueQueue.isEmpty()) {
			Sound sound = new Sound(getSoundPackageName(),soundIdQueueQueue.poll());
			sound.addLineListener(this);
			if (executor != null) {
				executor.execute(sound);
			}
		}
	}

	private String getSoundPackageName(){
		return Configuration.Instance.instance().getSoundPackage();
	}

	protected void queueSound(SoundId id) {
		soundIdQueueQueue.add(id);
	}

	private void playNextSound() {
		if (!soundIdQueueQueue.isEmpty()) {
			playQueue();
		}
	}

	private SoundId getSoundIdForMultiplier(int number) {
		switch (number) {
		case 2:
			return SoundId.Double;
		case 3:
			return SoundId.Triple;
		default:
			return SoundId.Hit;
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		executor.shutdown();
	}

	@Override
	public void provideFinishingCombination(
			List<List<PointEvent>> finishingCombinations) {
	}

	@Override
	public void onCurrentPlayerChanged(Player currentPlayer, int remainingScore) {
	}

	@Override
	public void onBust(Player currentPlayer, int remainingScore) {
		playSound(SoundId.Bust);
	}

	@Override
	public void roundStarted(int rounds) {
	}

	@Override
	public void onTurnFinished(Player finishedPlayer, int totalScore) {

		playSound(SoundId.RemoveDarts);

	}

	@Override
	public void remainingScoreForPlayer(Player currentPlayer, int remainingScore) {
	}

	@Override
	public void requestNextPlayerEvent() {
		playSound(SoundId.PleasePressNextPlayer);
	}

	@Override
	public void playerFinished(Player currentPlayer) {
	}

	@Override
	public void onGameFinished(Map<Player, Score> playerScoreMap) {
		for (Player player : playerScoreMap.keySet()) {
			playSound(SoundId.Hit);
		}
		// TODO implement something sophisticated
		/*
		 * check for resource matching player
		 * if not available play according number
		 * PlayerScoreMap is ordered accordingly
		 */
	}

	@Override
	public void onPointEvent(PointEvent event) {

		int multiplier = event.getMultiplier();
		int baseNumber = event.getBaseNumber();
		queueSound(SoundId.Hit);

		if (multiplier > 1) {
			queueSound(getSoundIdForMultiplier(multiplier));
			queueSound(SoundId.get(baseNumber));
			if (multiplier == 3) {
				queueSound(SoundId.Praise);
			}
		} else {
			queueSound(SoundId.get(baseNumber));
		}
		playQueue();
	}

	@Override
	public void onNextPlayerPressed() {
	}

	@Override
	public void onBounceOutPressed() {
		playSound(SoundId.BounceOut);
	}

	@Override
	public void onDartMissedPressed() {
		playSound(SoundId.Missed);
	}

	@Override
	public void update(LineEvent event) {
		if (event.getType() != Type.STOP) {
			return;
		} else {
			playNextSound();
		}

	}

}