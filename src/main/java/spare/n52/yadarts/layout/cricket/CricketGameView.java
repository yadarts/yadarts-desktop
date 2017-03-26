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
package spare.n52.yadarts.layout.cricket;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
import spare.n52.yadarts.games.GameAlreadyActiveException;
import spare.n52.yadarts.games.GameEventBus;
import spare.n52.yadarts.games.NoGameActiveException;
import spare.n52.yadarts.games.Score;
import spare.n52.yadarts.games.Turn;
import spare.n52.yadarts.games.cricket.CricketGame;
import spare.n52.yadarts.i18n.I18N;
import spare.n52.yadarts.layout.AbstractGameView;
import spare.n52.yadarts.layout.GameParameter;
import spare.n52.yadarts.layout.board.BoardView;
import spare.n52.yadarts.persistence.HighscorePersistence;
import spare.n52.yadarts.persistence.PersistencyException;
import spare.n52.yadarts.sound.SoundService;
import spare.n52.yadarts.themes.BorderedControlContainer;
import spare.n52.yadarts.themes.Theme;

public class CricketGameView extends AbstractGameView {
	
	private static final Logger logger = LoggerFactory
			.getLogger(CricketGameView.class);
	
	public static final String PLAYERS_PARAMETER = "playersInput";
	private List<Player> players;
	private Composite wrapper;
	protected CricketGame gameInstance;

	private Image background;
	private BoardView theBoard;
	private BorderedControlContainer leftBar;
	private BorderedControlContainer bottomBar;

	protected Label turnThrows;
	protected Label roundLabel;
	protected Label statusBar;
	protected CricketPlayerTableView playerTable;


	@Override
	public String getGameName() {
		return "Cricket";
	}

	@Override
	public Composite initialize(Composite parent, int style,
			List<GameParameter<?>> inputValues) {
		this.players = resolvePlayers(inputValues);
		if (this.players == null) {
			throw new IllegalStateException("No players found!");
		}
		
		this.wrapper = new Composite(parent, style);
		
		this.wrapper.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (gameInstance != null) {
					try {
						GameEventBus.instance().endGame(gameInstance);
					} catch (NoGameActiveException e1) {
						logger.warn(e1.getMessage(), e1);
					}
					
				}
			}
		});
		
		try {
			background = Theme.getCurrentTheme().getBackground(wrapper.getDisplay());
		} catch (final FileNotFoundException e1) {
			logger.warn(e1.getMessage(), e1);
			throw new IllegalStateException("The theme does not provide a valid background resource");
		}
		
		wrapper.setBackgroundImage(background);
		
		FormLayout formLayout = new FormLayout();
		formLayout.marginHeight = 0;
		formLayout.marginWidth = 0;
		formLayout.spacing = 0;
		wrapper.setLayout(formLayout);

		initFirstRow(wrapper);

		initSecondRow(wrapper);

//		wrapper.pack();
		wrapper.setSize(parent.getSize());
//		wrapper.layout(true, true);

		try {
			startGame();
		} catch (InitializationException | AlreadyRunningException e) {
			logger.warn(e.getMessage(), e);
		}
		
		return this.wrapper;
	}
	
	private void startGame() throws InitializationException,
	AlreadyRunningException {
		gameInstance = new CricketGame(players);
		gameInstance.registerGameListener(this);
		
		/*
		 * TODO: check Configuration for existing SoundService 
		 */
		gameInstance.registerGameListener(Services.getImplementation(SoundService.class));
		
		try {
			GameEventBus.instance().startGame(gameInstance);
		} catch (GameAlreadyActiveException e) {
			logger.warn(e.getMessage(), e);
		}

	}

	private void initFirstRow(final Composite container) {
		createLeftBar(container);

		theBoard = new BoardView(container, SWT.NONE);
		FormData theBoardData = new FormData();
		theBoardData.top = new FormAttachment(0);
		theBoardData.left = new FormAttachment(leftBar);
		theBoardData.right = new FormAttachment(100);
		theBoardData.bottom = new FormAttachment(80);
		theBoard.setLayoutData(theBoardData);

	}



	private void createLeftBar(Composite container) {
		leftBar = new BorderedControlContainer(container, SWT.NONE) {

			@Override
			protected Control createContents(Composite parent) {
				Composite leftBarContainer = new Composite(parent, SWT.NONE);
				leftBarContainer.setBackgroundMode(SWT.INHERIT_FORCE);
				
				GridLayout leftBarLayout = new GridLayout(1, true);
				leftBarContainer.setLayout(leftBarLayout);
				
				new Label(leftBarContainer, SWT.UNDERLINE_SINGLE).setText(I18N.getString("currentRound").concat(":"));
				roundLabel = new Label(leftBarContainer, SWT.NONE);
				roundLabel.setText("1");
				roundLabel.setFont(new Font(getDisplay(), new FontData(FONT, NORMAL_FONT,
						SWT.NONE)));
				
				new Label(leftBarContainer, SWT.UNDERLINE_SINGLE).setText(I18N.getString("theTurnIsOn").concat(":"));
				playerTable = new CricketPlayerTableView(leftBarContainer, SWT.NONE, players, 0);
				final GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
				playerTable.setLayoutData(data);

				new Label(leftBarContainer, SWT.UNDERLINE_SINGLE).setText(I18N.getString("turnThrows").concat(":"));
				turnThrows = new Label(leftBarContainer, SWT.NONE);
				turnThrows.setFont(new Font(getDisplay(), new FontData(FONT, NORMAL_FONT,
						SWT.NONE)));
				
				return leftBarContainer;
			}
		};
		FormData leftBarData = new FormData();
		leftBarData.top = new FormAttachment(0);
		leftBarData.left = new FormAttachment(0);
		leftBarData.right = new FormAttachment(40);
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
		playerTable.setCurrentPlayer(p);
		playerTable.setRemainingScore(p, remaining);
		updateLabel(this.wrapper, turnThrows, "");
		onRemainingScoreForPlayer(p, remaining);
		theBoard.removeAllArrows();
	}

	@Override
	public void onBust(final Player p, final Score remaining) {
	}

	@Override
	public void onPointEvent(final PointEvent event, Turn turn) {
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
				updateLabel(wrapper, turnThrows, result);
			}

		});
	}

	@Override
	public void onRoundStarted(int rounds) {
		logger.info("+++++++++++++++++++");
		logger.info("Round {} started!", rounds);
		logger.info("+++++++++++++++++++");
		updateLabel(wrapper, statusBar, String.format(I18N.getString("roundStarted"), rounds));
		updateLabel(wrapper, roundLabel, Integer.toString(rounds));
	}

	@Override
	public void onFinishingCombination(
			List<List<PointEvent>> finishingCombinations) {
	}

	@Override
	public void onTurnFinished(Player finishedPlayer, Score remainingScore) {
		logger.info("Player {} finished the turn. Remaining points: {}",
				finishedPlayer, remainingScore.getTotalScore());
		playerTable.setRemainingScore(finishedPlayer, remainingScore);
//		updateLabel(wrapper, statusBar, String.format(
//				I18N.getString("playerFinishedTurn"),
//				finishedPlayer.getName(), remainingScore.getTotalScore()));
	}

	@Override
	public void onRemainingScoreForPlayer(Player currentPlayer, Score remainingScore) {
		logger.info("Player {}'s remaining points: {}", currentPlayer,
				remainingScore.getTotalScore());
		updateLabel(wrapper, statusBar, remainingScore.toString());
		playerTable.setRemainingScore(currentPlayer, remainingScore);
	}

	@Override
	public void requestNextPlayerEvent() {
		logger.info("Please press 'Next Player'!");
//		updateLabel(wrapper, statusBar, I18N.getString("requestNextPlayer"));
	}

	@Override
	public void onPlayerFinished(Player currentPlayer) {
		logger.info("Player {} finished!!!!!!! You are a Dart god!",
				currentPlayer);
//		updateLabel(wrapper, statusBar, String.format(I18N.getString("playerFinished"), currentPlayer.getName()));
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
							.addHighscoreEntry(gameInstance.getClass(), score);
				} catch (PersistencyException e) {
					logger.warn(e.getMessage(), e);
				}
			}
		}

		updateLabel(wrapper, statusBar, String.format(I18N.getString("gameHasEnded"), winner.toString()));
	}

	@Override
	public void onNextPlayerPressed() {
		theBoard.removeAllArrows();
	}

	@Override
	public void onBounceOutPressed() {
		processPointEvent(BOUNCE_OUT);
		theBoard.removeLastHit();
	}

	@Override
	public void onDartMissedPressed() {
		processPointEvent(MISSED);
	}

}
