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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spare.n52.yadarts.common.Services;
import spare.n52.yadarts.config.Configuration;


import spare.n52.yadarts.entity.Player;
import spare.n52.yadarts.entity.PointEvent;
import spare.n52.yadarts.games.Score;
import spare.n52.yadarts.sound.jsyn.JsynSoundExecutor;

public class BasicSoundService implements SoundService {

	private final SoundExecutor executor;

	private static final Logger logger = LoggerFactory.getLogger(BasicSoundService.class);

	public static final String SOUND_THEME = "SOUND_THEME";
	
	public static final int lowPraiseThreshold = 30;
	public static final int highPraiseThreshold = 50;

	private Map<Integer, Integer> pointsMap = new HashMap<>(3);
	
	private boolean bounceOutPressed;
	
	public BasicSoundService() {
		executor = new JsynSoundExecutor();
                executor.init(Services.getImplementation(Configuration.class).getSoundPackage());
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
		pointsMap.clear();
		bounceOutPressed = false;		
		List<SoundId> list = new ArrayList<>();		
		list.add(SoundId.get(currentPlayer.getName()));
		list.add(SoundId.PleaseThrowDarts);
		playSoundSequence(list);
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
		List<SoundId> list = new ArrayList<>();
		if (pointsMap.size() == 3 && !bounceOutPressed) {
			
			boolean single1Hit = (pointsMap.keySet().contains(1) && (pointsMap.get(1) == 1));
			boolean single5Hit = (pointsMap.keySet().contains(5) && (pointsMap.get(5) == 1));
			boolean single20Hit = (pointsMap.keySet().contains(20) && (pointsMap.get(20) == 1));
			
			if (single1Hit && single5Hit && single20Hit && !score.getLastTurn().isBusted()) {
				list.add(SoundId.Upper_Classic);
			}
			
			boolean single3Hit = (pointsMap.keySet().contains(3) && (pointsMap.get(3) == 1));
			boolean single7Hit = (pointsMap.keySet().contains(7) && (pointsMap.get(7) == 1));
			boolean single19Hit = (pointsMap.keySet().contains(19) && (pointsMap.get(19) == 1));
			
			if (single3Hit && single7Hit && single19Hit && !score.getLastTurn().isBusted()) {
				list.add(SoundId.Lower_Classic);
			}

		}
		list.add(SoundId.RemoveDarts);
		playSoundSequence(list);
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
		List<SoundId> list = new ArrayList<>();		
		list.add(SoundId.get(currentPlayer.getName()));
		list.add(SoundId.IsTheWinner);
		playSoundSequence(list);
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
		pointsMap.put(event.getBaseNumber(), event.getMultiplier());
		
		List<SoundId> list = new ArrayList<>();
		list.add(SoundId.Hit);
		final int multiplier = event.getMultiplier();
		final int baseNumber = event.getBaseNumber();
		
		int score = event.getMultiplier() * event.getBaseNumber();
		
		if (multiplier > 1) {
			list.add(getSoundIdForMultiplier(multiplier));
		}
		list.add(SoundId.get(baseNumber));
		
		if(score >= lowPraiseThreshold && score < highPraiseThreshold){
			list.add(SoundId.Praise_low);
		}else if(score >= highPraiseThreshold){
			list.add(SoundId.Praise_high);
		}
		
		playSoundSequence(list);
	}


	@Override
	public void onNextPlayerPressed() {
	}

	@Override
	public void onBounceOutPressed() {
		bounceOutPressed = true;
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