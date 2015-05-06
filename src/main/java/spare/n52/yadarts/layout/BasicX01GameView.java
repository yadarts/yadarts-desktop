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

import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
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
import spare.n52.yadarts.InitializationException;
import spare.n52.yadarts.common.Services;
import spare.n52.yadarts.entity.Player;
import spare.n52.yadarts.entity.PointEvent;
import spare.n52.yadarts.games.AbstractGame;
import spare.n52.yadarts.games.GameAlreadyActiveException;
import spare.n52.yadarts.games.GameEventBus;
import spare.n52.yadarts.games.GameStatusUpdateListener;
import spare.n52.yadarts.games.NoGameActiveException;
import spare.n52.yadarts.games.Score;
import spare.n52.yadarts.games.x01.GenericX01Game;
import spare.n52.yadarts.i18n.I18N;
import spare.n52.yadarts.layout.board.BoardView;
import spare.n52.yadarts.persistence.HighscorePersistence;
import spare.n52.yadarts.persistence.PersistencyException;
import spare.n52.yadarts.sound.SoundService;
import spare.n52.yadarts.themes.BorderedControlContainer;

public abstract class BasicX01GameView extends AbstractGameView implements
		GameStatusUpdateListener {

	private static final Logger logger = LoggerFactory
			.getLogger(BasicX01GameView.class);
	
	private Label currentScore;
	private BoardView theBoard;
	private Label currentPlayer;
	private Label turnThrows;
	private Label turnScore;
	private Label finishingCombinationsLabel;
	private Label finishingCombinations;
	private Composite leftBar;
	private Composite rightBar;

	private Composite bottomBar;
	private Label statusBar;
	private AbstractGame x01Game;
	private List<Player> players;
	private PlayerTableView playerTable;
	private int targetScore;
	private Label roundLabel;
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
	public Composite initialize(Composite parent, int style, List<GameParameter<?>> inputValues) {
		this.players = resolvePlayers(inputValues);
		if (this.players == null) {
			throw new IllegalStateException("No players found!");
		}
		
		this.targetScore = getDesiredTargetScore();
		
		this.wrapper = new Composite(parent, SWT.INHERIT_FORCE);
		
		this.wrapper.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (x01Game != null) {
					try {
						GameEventBus.instance().endGame(x01Game);
					} catch (NoGameActiveException e1) {
						logger.warn(e1.getMessage(), e1);
					}
					
				}
			}
		});
		
//		try {
//			background = Theme.getCurrentTheme().getBackground(wrapper.getDisplay());
//		} catch (final FileNotFoundException e1) {
//			logger.warn(e1.getMessage(), e1);
//			throw new IllegalStateException("The theme does not provide a valid background resource");
//		}
		
//		wrapper.setBackgroundImage(background);
		
		FormLayout formLayout = new FormLayout();
		formLayout.marginHeight = 0;
		formLayout.marginWidth = 0;
		formLayout.spacing = 0;
		wrapper.setLayout(formLayout);

		initFirstRow(wrapper);

		initSecondRow(wrapper);

//		wrapper.pack();
		wrapper.setSize(parent.getSize());
//		wrapper.layout();
		
		currentPlayer.setText(players.get(0).getName());

		try {
			startGame();
		} catch (InitializationException | AlreadyRunningException e) {
			logger.warn(e.getMessage(), e);
		}
		
		return this.wrapper;
	}


	private void startGame() throws InitializationException,
			AlreadyRunningException {
		x01Game = GenericX01Game.create(players, targetScore);
		x01Game.registerGameListener(this);
		
		/*
		 * TODO: check Configuration for existing SoundService 
		 */
		x01Game.registerGameListener(Services.getImplementation(SoundService.class));
		
		try {
			GameEventBus.instance().startGame(x01Game);
		} catch (GameAlreadyActiveException e) {
			logger.warn(e.getMessage(), e);
		}
		
	}

	private void initFirstRow(final Composite container) {
		createLeftBar(container);

		theBoard = new BoardView(container, SWT.INHERIT_FORCE);
		FormData theBoardData = new FormData();
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
			protected Control createContents(Composite parent) {
				Composite rightBarContainer = new Composite(parent, SWT.NONE);
				rightBarContainer.setBackgroundMode(SWT.INHERIT_FORCE);
				
				RowLayout leftBarLayout = new RowLayout(SWT.VERTICAL);
				leftBarLayout.spacing = 5;
				rightBarContainer.setLayout(leftBarLayout);
				
				new Label(rightBarContainer, SWT.UNDERLINE_DOUBLE).setText(I18N.getString("currentPlayer").concat(":"));
				currentPlayer = new Label(rightBarContainer, SWT.NONE);
				currentPlayer.setFont(new Font(getDisplay(), new FontData(FONT, LARGE_FONT,SWT.NONE)));
				
				new Label(rightBarContainer, SWT.UNDERLINE_SINGLE).setText(I18N.getString("turnThrows").concat(":"));
				turnThrows = new Label(rightBarContainer, SWT.NONE);
				turnThrows.setFont(new Font(getDisplay(), new FontData(FONT, NORMAL_FONT,
						SWT.NONE)));
				
				new Label(rightBarContainer, SWT.UNDERLINE_SINGLE).setText(I18N.getString("turnScore").concat(":"));
				turnScore = new Label(rightBarContainer, SWT.NONE);
				turnScore.setFont(new Font(getDisplay(), new FontData(FONT, LARGE_FONT,
						SWT.NONE)));
				
				new Label(rightBarContainer, SWT.UNDERLINE_SINGLE).setText(I18N.getString("remainingScore").concat(":"));
				currentScore = new Label(rightBarContainer, SWT.NONE);
				currentScore.setFont(new Font(getDisplay(), new FontData(FONT, LARGE_FONT,
						SWT.NONE)));
				currentScore.setText(Integer.toString(getDesiredTargetScore()));
				
				finishingCombinationsLabel = new Label(rightBarContainer, SWT.UNDERLINE_SINGLE);
				finishingCombinationsLabel.setText(I18N.getString("finishingCombinations").concat(":"));
				finishingCombinationsLabel.setVisible(false);
				finishingCombinations = new Label(rightBarContainer, SWT.NONE);
				finishingCombinations.setFont(new Font(getDisplay(), new FontData(FONT, NORMAL_FONT,
						SWT.NONE)));
				finishingCombinations.setVisible(false);
				
				return rightBarContainer;
			}
		};
		
		FormData rightBarData = new FormData();
		rightBarData.top = new FormAttachment(0);
		rightBarData.left = new FormAttachment(theBoard);
		rightBarData.right = new FormAttachment(100);
		rightBarData.bottom = new FormAttachment(80);
		rightBar.setLayoutData(rightBarData);

	}

	private void createLeftBar(Composite container) {
		leftBar = new BorderedControlContainer(container, SWT.NONE) {
			
			@Override
			protected Control createContents(Composite parent) {
				Composite leftBarContainer = new Composite(parent, SWT.NONE);
//				leftBarContainer.setBackgroundMode(SWT.INHERIT_FORCE);
				
				GridLayout leftBarLayout = new GridLayout(1, true);
				leftBarContainer.setLayout(leftBarLayout);
				
				new Label(leftBarContainer, SWT.UNDERLINE_SINGLE).setText(I18N.getString("currentRound").concat(":"));
				roundLabel = new Label(leftBarContainer, SWT.NONE);
				roundLabel.setText("1");
				roundLabel.setFont(new Font(getDisplay(), new FontData(FONT, NORMAL_FONT,
						SWT.NONE)));
				
				new Label(leftBarContainer, SWT.UNDERLINE_SINGLE).setText(I18N.getString("theTurnIsOn").concat(":"));
				playerTable = new PlayerTableView(leftBarContainer, SWT.NONE, players, targetScore);
				final GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
				playerTable.setLayoutData(data);

				return leftBarContainer;
			}
		};
		FormData leftBarData = new FormData();
		leftBarData.top = new FormAttachment(0);
		leftBarData.left = new FormAttachment(0);
		leftBarData.right = new FormAttachment(20);
		leftBarData.bottom = new FormAttachment(80);
		leftBar.setLayoutData(leftBarData);
	}

	private void initSecondRow(Composite container) {
		bottomBar = new BorderedControlContainer(container, SWT.NONE) {
			
			@Override
			protected Control createContents(Composite parent) {
				Composite bottomBarContainer = new Composite(parent, SWT.NONE);
				bottomBarContainer.setLayout(new FillLayout());
				bottomBarContainer.setBackgroundMode(SWT.INHERIT_FORCE);
				statusBar = new Label(bottomBarContainer, SWT.NONE);
				statusBar.setFont(new Font(getDisplay(), new FontData(FONT, 14,
						SWT.NONE)));
				return bottomBarContainer;
			}
		};
		
		FormData leftBarData = new FormData();
		leftBarData.top = new FormAttachment(leftBar);
		leftBarData.left = new FormAttachment(0);
		leftBarData.right = new FormAttachment(100);
		leftBarData.bottom = new FormAttachment(100);
		bottomBar.setLayoutData(leftBarData);

	}

	@Override
	public void onCurrentPlayerChanged(final Player p, final Score remaining) {
		playerTable.setCurrentPlayer(p, remaining);
		updateLabel(this.wrapper, currentPlayer, p.getName());
		updateLabel(this.wrapper, turnThrows, "");
		updateLabel(this.wrapper, turnScore,"");
		turnScoreMemory.clear();
		onRemainingScoreForPlayer(p, remaining);
		theBoard.removeAllArrows();
	}

	@Override
	public void onBust(final Player p, final Score remaining) {
		final String s = String.format(I18N.getString("playerBusted"),
				p.getName(), remaining.getTotalScore());
		logger.info(s);
		updateLabel(this.wrapper, statusBar, s);
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
				updateLabel(wrapper, turnScore, getSum(turnScoreMemory));
				updateLabel(wrapper, turnThrows, result);
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
	public void onRoundStarted(int rounds) {
		logger.info("+++++++++++++++++++");
		logger.info("Round {} started!", rounds);
		logger.info("+++++++++++++++++++");
		updateLabel(this.wrapper, statusBar, String.format(I18N.getString("roundStarted"), rounds));
		updateLabel(this.wrapper, roundLabel, Integer.toString(rounds));
	}

	@Override
	public void onFinishingCombination(
			List<List<PointEvent>> finishingCombinations) {
		logger.info("Player can finished with the following combinations:");

		if (finishingCombinations == null) {
			return;
		}

		StringBuilder sb;
		for (List<PointEvent> list : finishingCombinations) {
			sb = new StringBuilder();
			for (PointEvent pe : list) {
				sb.append(pe);
				sb.append(" + ");
			}
			logger.info(sb.toString());
			this.finishingCombinations.setText(sb.toString());
			this.finishingCombinations.setVisible(true);
			finishingCombinationsLabel.setVisible(true);
		}
	}

	@Override
	public void onTurnFinished(Player finishedPlayer, Score remainingScore) {
		logger.info("Player {} finished the turn. Remaining points: {}",
				finishedPlayer, remainingScore.getTotalScore());
		updateLabel(this.wrapper, statusBar, String.format(
				I18N.getString("playerFinishedTurn"),
				finishedPlayer.getName(), remainingScore.getTotalScore()));
	}

	@Override
	public void onRemainingScoreForPlayer(Player currentPlayer, Score remainingScore) {
		logger.info("Player {}'s remaining points: {}", currentPlayer,
				remainingScore.getTotalScore());
		updateLabel(this.wrapper, currentScore, Integer.toString(remainingScore.getTotalScore()));
		playerTable.setRemainingScore(currentPlayer, remainingScore);
	}

	@Override
	public void requestNextPlayerEvent() {
		logger.info("Please press 'Next Player'!");
		updateLabel(this.wrapper, statusBar, I18N.getString("requestNextPlayer"));
	}

	@Override
	public void onPlayerFinished(Player currentPlayer) {
		logger.info("Player {} finished!!!!!!! You are a Dart god!",
				currentPlayer);
		updateLabel(this.wrapper, statusBar, String.format(I18N.getString("playerFinished"), currentPlayer.getName()));
	}

	@Override
	public void onGameFinished(Map<Player, Score> playerScoreMap, List<Player> winner) {
		logger.info("The game has ended!");

		for (Player player : playerScoreMap.keySet()) {
			Score score = playerScoreMap.get(player);
			logger.info("{}: {}", player, score);
			
			if (score.getTotalScore() == 0) {
				try {
					Services.getImplementation(HighscorePersistence.class)
							.addHighscoreEntry(x01Game.getClass(), score);
				} catch (PersistencyException e) {
					logger.warn(e.getMessage(), e);
				}
			}
		}

		updateLabel(this.wrapper, statusBar, String.format(I18N.getString("gameHasEnded"), winner.toString()));
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
