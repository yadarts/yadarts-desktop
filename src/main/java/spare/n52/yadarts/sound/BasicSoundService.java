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
import spare.n52.yadarts.games.Score;

public class BasicSoundService implements SoundService, LineListener {

	private final ExecutorService executor;
	private final Queue<SoundId> soundIdQueueQueue;
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
	protected void playSound(final SoundId id) {
		queueSound(id);
		playQueue();
	}

	protected synchronized void playQueue() {
		//TODO check for RuntimeExceptions. Otherwise sound system breaks for this app runtime
		if (!soundIdQueueQueue.isEmpty()) {
			final Sound sound = new Sound(getSoundPackageName(), soundIdQueueQueue.poll());
			sound.addLineListener(this);
			if (executor != null) {
				executor.execute(sound);
			}
		}
	}

	private String getSoundPackageName(){
		return Configuration.Instance.instance().getSoundPackage();
	}

	protected void queueSound(final SoundId id) {
		soundIdQueueQueue.add(id);
	}

	private void playNextSound() {
		if (!soundIdQueueQueue.isEmpty()) {
			playQueue();
		}
	}

	private SoundId getSoundIdForMultiplier(final int number) {
		switch (number) {
		case 2:
			return SoundId.Double;
		case 3:
			return SoundId.Triple;
		default:
			return SoundId.None;
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		executor.shutdown();
	}

	@Override
	public void onFinishingCombination(
			final List<List<PointEvent>> finishingCombinations) {
	}

	@Override
	public void onCurrentPlayerChanged(final Player currentPlayer, final Score score) {
	}

	@Override
	public void onBust(final Player currentPlayer, final Score score) {
		playSound(SoundId.Bust);
	}

	@Override
	public void onRoundStarted(final int rounds) {
	}

	@Override
	public void onTurnFinished(final Player finishedPlayer, final Score score) {
		playSound(SoundId.RemoveDarts);
	}

	@Override
	public void onRemainingScoreForPlayer(final Player currentPlayer, final Score score) {
	}

	@Override
	public void requestNextPlayerEvent() {
		playSound(SoundId.PleasePressNextPlayer);
	}

	@Override
	public void onPlayerFinished(final Player currentPlayer) {
	}

	@Override
	public void onGameFinished(final Map<Player, Score> playerScoreMap, final List<Player> winner) {
		for (final Player player : playerScoreMap.keySet()) {
			playSound(SoundId.None);
		}
		// TODO implement something sophisticated
		/*
		 * check for resource matching player
		 * if not available play according number
		 * PlayerScoreMap is ordered accordingly
		 */
	}

	@Override
	public void onPointEvent(final PointEvent event) {

		final int multiplier = event.getMultiplier();
		final int baseNumber = event.getBaseNumber();
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
	public void update(final LineEvent event) {
		if (event.getType() != Type.STOP) {
			return;
		} else {
			playNextSound();
		}

	}

}