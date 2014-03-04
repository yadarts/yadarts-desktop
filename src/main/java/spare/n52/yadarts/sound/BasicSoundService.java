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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import spare.n52.yadarts.entity.Player;
import spare.n52.yadarts.entity.PointEvent;
import spare.n52.yadarts.games.Score;

public class BasicSoundService implements SoundService {

	private final SoundExecutor executor;

	private static final Logger logger = LoggerFactory.getLogger(BasicSoundService.class);

	public static final String SOUND_THEME = "SOUND_THEME";

	public BasicSoundService() {
		executor = new SoundExecutor();
	}

	/**
	 * Play sound.
	 * 
	 * @param id
	 */
	protected void playSound(final SoundId id) {
		executor.add(id);
	}
	
	private void playSoundSequence(List<SoundId> list) {
		executor.add(list);
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
		if (this.executor != null) {
			this.executor.shutdown();
		}
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
		if(score.getTotalScore() == 26){
			playSound(SoundId.Classic);
		}
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
		playSound(SoundId.Hit);
		
		List<SoundId> list = new ArrayList<>();
		final int multiplier = event.getMultiplier();
		final int baseNumber = event.getBaseNumber();
		
		if (multiplier > 1) {
			list.add(getSoundIdForMultiplier(multiplier));
			list.add(SoundId.get(baseNumber));
			if (multiplier == 3 && baseNumber >= 10) {
				list.add(SoundId.Praise);
			}
		} else {
			list.add(SoundId.get(baseNumber));
		}
		
		playSoundSequence(list);
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
	public void shutdown() {
		try {
			finalize();
		} catch (Throwable e) {
			logger.warn(e.getMessage(), e);
		}
	}

}