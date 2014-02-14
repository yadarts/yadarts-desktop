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
package spare.n52.yadarts.layout;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spare.n52.yadarts.AlreadyRunningException;
import spare.n52.yadarts.EventEngine;
import spare.n52.yadarts.InitializationException;
import spare.n52.yadarts.entity.Player;
import spare.n52.yadarts.entity.PointEvent;
import spare.n52.yadarts.entity.impl.PlayerImpl;
import spare.n52.yadarts.games.GameStatusUpdateListener;
import spare.n52.yadarts.games.Score;
import spare.n52.yadarts.games.x01.GenericX01Game;
import spare.n52.yadarts.i18n.I18N;
import spare.n52.yadarts.layout.GameParameter.Bounds;
import spare.n52.yadarts.layout.board.BoardView;
import spare.n52.yadarts.persistence.HighscorePersistence;
import spare.n52.yadarts.persistence.PersistencyException;
import spare.n52.yadarts.sound.BasicSoundService;
import spare.n52.yadarts.themes.BorderedControlContainer;
import spare.n52.yadarts.themes.Theme;

public abstract class BasicX01GameView implements
		GameStatusUpdateListener, GameView {

	/**
	 * 
	 */
	private static final String MISSED = "missed!";
	/**
	 * 
	 */
	private static final String BOUNCE_OUT = "bounce out!";
	private static final Logger logger = LoggerFactory
			.getLogger(BasicX01GameView.class);
	public static final String PLAYERS_PARAMETER = "playersInput";
	private Label currentScore;
	private BoardView theBoard;
	private Label turnThrows;
	private Label turnScore;
	private Composite leftBar;
	private Composite rightBar;

	private Composite bottomBar;
	private Label statusBar;
	private GenericX01Game x01Game;
	private List<Player> players;
	private PlayerTableView playerTable;
	private int targetScore;
	private Label roundLabel;
	private Image background;
	private Composite wrapper;
	/**
	 * stores the turnScore for the currently throwing player
	 */
	private final Stack<PointEvent> turnScoreMemory = new Stack<>();
	
	/**
	 * @return the X01 score (e.g. 701)
	 */
	protected abstract int getDesiredTargetScore();
	
	@Override
	public List<GameParameter<?>> getInputParameters() {
		final List<GameParameter<?>> result = new ArrayList<>();
		result.add(new GameParameter<String>(String.class, PLAYERS_PARAMETER, Bounds.unbound(2)));
		return result;
	}
	
	@Override
	public void initialize(final Composite parent, final int style, final List<GameParameter<?>> inputValues) {
		players = resolvePlayers(inputValues);
		if (players == null) {
			throw new IllegalStateException("No players found!");
		}
		
		targetScore = getDesiredTargetScore();
		
		wrapper = new Composite(parent, style);
		
		try {
			background = Theme.getCurrentTheme().getBackground(wrapper.getDisplay());
		} catch (final FileNotFoundException e1) {
			logger.warn(e1.getMessage(), e1);
			throw new IllegalStateException("The theme does not provide a valid background resource");
		}
		
		wrapper.setBackgroundImage(background);
		
		final FormLayout formLayout = new FormLayout();
		formLayout.marginHeight = 0;
		formLayout.marginWidth = 0;
		formLayout.spacing = 0;
		wrapper.setLayout(formLayout);

		initFirstRow(wrapper);

		initSecondRow(wrapper);

		wrapper.pack();

		try {
			startGame();
		} catch (InitializationException | AlreadyRunningException e) {
			logger.warn(e.getMessage(), e);
		}
	}


	private List<Player> resolvePlayers(final List<GameParameter<?>> inputValues) {
		for (final GameParameter<?> gameParameter : inputValues) {
			switch (gameParameter.getName()) {
			case PLAYERS_PARAMETER:
				final List<Player> result = new ArrayList<>();
				
				final Object value = gameParameter.getValue();

				for (final String player : (List<String>) value) {
					result.add(new PlayerImpl(player));
				}
				
				return result;
			default:
				break;
			}
		}
		return null;
	}

	private void startGame() throws InitializationException,
			AlreadyRunningException {
		final EventEngine engine = EventEngine.instance();
		x01Game = GenericX01Game.create(players, targetScore);
		x01Game.registerGameListener(this);
		
		/*
		 * TODO: check Configuration for existing SoundService 
		 */
		x01Game.registerGameListener(new BasicSoundService());
		engine.registerListener(x01Game);
		engine.start();
	}

	private void initFirstRow(final Composite container) {
		createLeftBar(container);

		theBoard = new BoardView(container, SWT.NONE);
		final FormData theBoardData = new FormData();
		theBoardData.top = new FormAttachment(0);
		theBoardData.left = new FormAttachment(leftBar);
		theBoardData.right = new FormAttachment(80);
		theBoardData.bottom = new FormAttachment(80);
		theBoard.setLayoutData(theBoardData);

		createRightBar(container);
	}


	private void createRightBar(final Composite container) {
		rightBar = new BorderedControlContainer(container, SWT.NONE) {
			
			@Override
			protected Control createContents(final Composite parent) {
				final Composite rightBarContainer = new Composite(parent, SWT.NONE);
				rightBarContainer.setBackgroundMode(SWT.INHERIT_FORCE);
				
				final RowLayout leftBarLayout = new RowLayout(SWT.VERTICAL);
				leftBarLayout.spacing = 5;
				rightBarContainer.setLayout(leftBarLayout);
				
				new Label(rightBarContainer, SWT.UNDERLINE_SINGLE).setText(I18N.getString("currentRound").concat(":"));
				roundLabel = new Label(rightBarContainer, SWT.NONE);
				roundLabel.setText("1");
				roundLabel.setFont(new Font(getDisplay(), new FontData("Arial", 16,
						SWT.NONE)));

				new Label(rightBarContainer, SWT.UNDERLINE_SINGLE).setText(I18N.getString("turnThrows").concat(":"));
				turnThrows = new Label(rightBarContainer, SWT.NONE);
				turnThrows.setFont(new Font(getDisplay(), new FontData("Arial", 16,
						SWT.NONE)));
				
				new Label(rightBarContainer, SWT.UNDERLINE_SINGLE).setText(I18N.getString("turnScore").concat(":"));
				turnScore = new Label(rightBarContainer, SWT.NONE);
				turnScore.setFont(new Font(getDisplay(), new FontData("Arial", 16,
						SWT.NONE)));
				return rightBarContainer;
			}
		};
		final FormData rightBarData = new FormData();
		rightBarData.top = new FormAttachment(0);
		rightBarData.left = new FormAttachment(theBoard);
		rightBarData.right = new FormAttachment(100);
		rightBarData.bottom = new FormAttachment(80);
		rightBar.setLayoutData(rightBarData);


	}

	private void createLeftBar(final Composite container) {
		leftBar = new BorderedControlContainer(container, SWT.NONE) {
			
			@Override
			protected Control createContents(final Composite parent) {
				final Composite leftBarContainer = new Composite(parent, SWT.NONE);
				leftBarContainer.setBackgroundMode(SWT.INHERIT_FORCE);
				
				final GridLayout leftBarLayout = new GridLayout(1, true);
				leftBarContainer.setLayout(leftBarLayout);
				
				new Label(leftBarContainer, SWT.UNDERLINE_SINGLE).setText(I18N.getString("theTurnIsOn").concat(":"));
				playerTable = new PlayerTableView(leftBarContainer, SWT.NONE, players, targetScore);
				final GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
				playerTable.setLayoutData(data);

				new Label(leftBarContainer, SWT.UNDERLINE_SINGLE).setText(I18N.getString("remainingScore").concat(":"));
				currentScore = new Label(leftBarContainer, SWT.NONE);
				currentScore.setFont(new Font(getDisplay(), new FontData("Arial", 24,
						SWT.NONE)));
				currentScore.setText("301");
				return leftBarContainer;
			}
		};
		final FormData leftBarData = new FormData();
		leftBarData.top = new FormAttachment(0);
		leftBarData.left = new FormAttachment(0);
		leftBarData.right = new FormAttachment(20);
		leftBarData.bottom = new FormAttachment(80);
		leftBar.setLayoutData(leftBarData);
	}

	private void initSecondRow(final Composite container) {
		bottomBar = new BorderedControlContainer(container, SWT.NONE) {
			
			@Override
			protected Control createContents(final Composite parent) {
				final Composite bottomBarContainer = new Composite(parent, SWT.NONE);
				bottomBarContainer.setLayout(new FillLayout());
				bottomBarContainer.setBackgroundMode(SWT.INHERIT_FORCE);
				statusBar = new Label(bottomBarContainer, SWT.NONE);
				statusBar.setFont(new Font(getDisplay(), new FontData("Arial", 14,
						SWT.NONE)));
				return bottomBarContainer;
			}
		};
		
		final FormData leftBarData = new FormData();
		leftBarData.top = new FormAttachment(leftBar);
		leftBarData.left = new FormAttachment(0);
		leftBarData.right = new FormAttachment(100);
		leftBarData.bottom = new FormAttachment(100);
		bottomBar.setLayoutData(leftBarData);

	}

	private void updateLabel(final Label theLabel, final String value) {
		wrapper.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				theLabel.setText(value);
				theLabel.pack();
			}
		});
	}

	@Override
	public void onCurrentPlayerChanged(final Player p, final Score remaining) {
		playerTable.setCurrentPlayer(p, remaining);
		updateLabel(turnThrows, "");
		updateLabel(turnScore,"");
		turnScoreMemory.clear();
		onRemainingScoreForPlayer(p, remaining);
		theBoard.removeAllArrows();
	}

	@Override
	public void onBust(final Player p, final Score remaining) {
		final String s = String.format(I18N.getString("playerBusted"),
				p.getName(), remaining.getTotalScore());
		logger.info(s);
		updateLabel(statusBar, s);
		playerTable.setRemainingScore(p, remaining);
	}

	@Override
	public void onPointEvent(final PointEvent event) {
		turnScoreMemory.push(event);
		processPointEvent(Integer.toString(event.getScoreValue()));
		theBoard.onPointEvent(event);
	}

	private void processPointEvent(final String eventValueString) {
		wrapper.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				final String current = turnThrows.getText();

				String result;
				if (current != null && !current.isEmpty()) {
					result = String.format("%s, %s", current, eventValueString);
				} else {
					result = eventValueString;
				}
				updateLabel(turnScore, getSum(turnScoreMemory));
				updateLabel(turnThrows, result);
			}

			private String getSum(final Stack<PointEvent> turnScoreMemory) {
				int sum = 0;
				for (final PointEvent pointEvent : turnScoreMemory) {
					sum += pointEvent.getScoreValue();
				}
				return Integer.toString(sum);
			}
		});
	}

	@Override
	public void onRoundStarted(final int rounds) {
		logger.info("+++++++++++++++++++");
		logger.info("Round {} started!", rounds);
		logger.info("+++++++++++++++++++");
		updateLabel(statusBar, String.format(I18N.getString("roundStarted"), rounds));
		updateLabel(roundLabel, Integer.toString(rounds));
	}

	@Override
	public void onFinishingCombination(
			final List<List<PointEvent>> finishingCombinations) {
		logger.info("Player can finished with the following combinations:");

		if (finishingCombinations == null) {
			return;
		}

		StringBuilder sb;
		for (final List<PointEvent> list : finishingCombinations) {
			sb = new StringBuilder();
			for (final PointEvent pe : list) {
				sb.append(pe);
				sb.append(" + ");
			}
			logger.info(sb.toString());
		}
	}

	@Override
	public void onTurnFinished(final Player finishedPlayer, final Score remainingScore) {
		logger.info("Player {} finished the turn. Remaining points: {}",
				finishedPlayer, remainingScore.getTotalScore());
		updateLabel(statusBar, String.format(
				I18N.getString("playerFinishedTurn"),
				finishedPlayer.getName(), remainingScore.getTotalScore()));
	}

	@Override
	public void onRemainingScoreForPlayer(final Player currentPlayer, final Score remainingScore) {
		logger.info("Player {}'s remaining points: {}", currentPlayer,
				remainingScore.getTotalScore());
		updateLabel(currentScore, Integer.toString(remainingScore.getTotalScore()));
		playerTable.setRemainingScore(currentPlayer, remainingScore);
	}

	@Override
	public void requestNextPlayerEvent() {
		logger.info("Please press 'Next Player'!");
		updateLabel(statusBar, I18N.getString("requestNextPlayer"));
	}

	@Override
	public void onPlayerFinished(final Player currentPlayer) {
		logger.info("Player {} finished!!!!!!! You are a Dart god!",
				currentPlayer);
		updateLabel(statusBar, String.format(I18N.getString("playerFinished"), currentPlayer.getName()));
	}

	@Override
	public void onGameFinished(final Map<Player, Score> playerScoreMap, final List<Player> winner) {
		logger.info("The game has ended!");

		for (final Player player : playerScoreMap.keySet()) {
			final Score score = playerScoreMap.get(player);
			logger.info("{}: {}", player, score);
			
			if (score.getTotalScore() == 0) {
				try {
					HighscorePersistence.Instance.instance().addHighscoreEntry(x01Game.getClass(), score);
				} catch (final PersistencyException e) {
					logger.warn(e.getMessage(), e);
				}
			}
		}

		updateLabel(statusBar, String.format(I18N.getString("gameHasEnded"), winner.toString()));
		
		try {
			EventEngine.instance().shutdown();
		} catch (final InitializationException e) {
			logger.warn(e.getMessage(), e);
		}
	}

	@Override
	public void onNextPlayerPressed() {
		theBoard.removeAllArrows();
	}

	@Override
	public void onBounceOutPressed() {
		turnScoreMemory.pop();
		processPointEvent(BOUNCE_OUT);
		theBoard.removeLastHit();
	}

	@Override
	public void onDartMissedPressed() {
		processPointEvent(MISSED);
	}

}
