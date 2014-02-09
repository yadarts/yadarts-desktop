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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import spare.n52.yadarts.layout.board.BoardView;
import spare.n52.yadarts.themes.BorderedControlContainer;
import spare.n52.yadarts.themes.Theme;

public class BasicX01GameView extends Composite implements
		GameStatusUpdateListener {

	private static final Logger logger = LoggerFactory
			.getLogger(BasicX01GameView.class);
	private Label currentScore;
	private BoardView theBoard;
	private Label turnSummary;
	private Composite leftBar;
	private Composite rightBar;
	private static List<Player> thePlayers = Arrays.asList(new Player[] {
			new PlayerImpl("Jan"), new PlayerImpl("Benjamin"),
			new PlayerImpl("Eike"), new PlayerImpl("Matthes") });
	private Composite bottomBar;
	private Label statusBar;
	private GenericX01Game x01Game;
	private List<Player> players;
	private PlayerTableView playerTable;
	private int targetScore;
	private Label roundLabel;
	private Image background;
	
	public BasicX01GameView(Composite parent, int style, int targetScore) {
		this(parent, style, thePlayers, targetScore);
	}

	public BasicX01GameView(Composite parent, int style, List<Player> playerList, int targetScore) {
		super(parent, style);
		
		try {
			this.background = Theme.getCurrentTheme().getBackground(getDisplay());
		} catch (FileNotFoundException e1) {
			logger.warn(e1.getMessage(), e1);
			throw new IllegalStateException("The theme does not provide a valid background resource");
		}
		
		this.setBackgroundImage(background);
		
		this.players = playerList;
		this.targetScore = targetScore;
		
		FormLayout formLayout = new FormLayout();
		formLayout.marginHeight = 0;
		formLayout.marginWidth = 0;
		formLayout.spacing = 0;
		this.setLayout(formLayout);


		initFirstRow(this);

		initSecondRow(this);

		this.pack();

		try {
			startGame();
		} catch (InitializationException | AlreadyRunningException e) {
			logger.warn(e.getMessage(), e);
		}
	}

	private void startGame() throws InitializationException,
			AlreadyRunningException {
		EventEngine engine = EventEngine.instance();
		x01Game = new GenericX01Game(players, 301);
		x01Game.registerGameListener(this);
		engine.registerListener(x01Game);
		engine.start();
	}

	private void initFirstRow(final Composite container) {
		createLeftBar(container);

		theBoard = new BoardView(container, SWT.NONE);
		FormData theBoardData = new FormData();
		theBoardData.top = new FormAttachment(0);
		theBoardData.left = new FormAttachment(leftBar);
		theBoardData.right = new FormAttachment(80);
		theBoardData.bottom = new FormAttachment(80);
		theBoard.setLayoutData(theBoardData);

		createRightBar(container);
	}


	private void createRightBar(Composite container) {
		rightBar = new BorderedControlContainer(container, SWT.NONE) {
			
			@Override
			protected Control createContents(Composite parent) {
				Composite rightBarContainer = new Composite(parent, SWT.NONE);
				rightBarContainer.setBackgroundMode(SWT.INHERIT_FORCE);
				
				RowLayout leftBarLayout = new RowLayout(SWT.VERTICAL);
				leftBarLayout.spacing = 5;
				rightBarContainer.setLayout(leftBarLayout);
				
				new Label(rightBarContainer, SWT.UNDERLINE_SINGLE).setText(I18N.getString("currentRound").concat(":"));
				roundLabel = new Label(rightBarContainer, SWT.NONE);
				roundLabel.setText("1");
				roundLabel.setFont(new Font(getDisplay(), new FontData("Arial", 16,
						SWT.NONE)));

				new Label(rightBarContainer, SWT.UNDERLINE_SINGLE).setText(I18N.getString("turnThrows").concat(":"));
				turnSummary = new Label(rightBarContainer, SWT.NONE);
				turnSummary.setFont(new Font(getDisplay(), new FontData("Arial", 16,
						SWT.NONE)));
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
				leftBarContainer.setBackgroundMode(SWT.INHERIT_FORCE);
				
				GridLayout leftBarLayout = new GridLayout(1, true);
				leftBarContainer.setLayout(leftBarLayout);
				
				new Label(leftBarContainer, SWT.UNDERLINE_SINGLE).setText(I18N.getString("theTurnIsOn").concat(":"));
				playerTable = new PlayerTableView(leftBarContainer, SWT.NONE, players, targetScore);
				GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
				playerTable.setLayoutData(data);

				new Label(leftBarContainer, SWT.UNDERLINE_SINGLE).setText(I18N.getString("remainingScore").concat(":"));
				currentScore = new Label(leftBarContainer, SWT.NONE);
				currentScore.setFont(new Font(getDisplay(), new FontData("Arial", 24,
						SWT.NONE)));
				currentScore.setText("301");
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
				statusBar.setFont(new Font(getDisplay(), new FontData("Arial", 14,
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

	private void updateLabel(final Label theLabel, final String value) {
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				theLabel.setText(value);
				theLabel.pack();
			}
		});
	}

	@Override
	public void onCurrentPlayerChanged(final Player p, Score remaining) {
		playerTable.setCurrentPlayer(p, remaining);
		updateLabel(turnSummary, "");
		onRemainingScoreForPlayer(p, remaining);
	}

	@Override
	public void onBust(final Player p, Score remaining) {
		String s = String.format(I18N.getString("playerBusted"),
				p.getName(), remaining.getTotalScore());
		logger.info(s);
		updateLabel(statusBar, s);
		playerTable.setRemainingScore(p, remaining);
	}

	@Override
	public void onPointEvent(final PointEvent event) {
		processPointEvent(Integer.toString(event.getScoreValue()));
		theBoard.onPointEvent(event);
	}

	private void processPointEvent(final String value) {
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				String current = turnSummary.getText();

				String result;
				if (current != null && !current.isEmpty()) {
					result = String.format("%s, %s", current, value);
				} else {
					result = value;
				}

				updateLabel(turnSummary, result);
			}
		});
	}

	@Override
	public void onRoundStarted(int rounds) {
		logger.info("+++++++++++++++++++");
		logger.info("Round {} started!", rounds);
		logger.info("+++++++++++++++++++");
		updateLabel(statusBar, String.format(I18N.getString("roundStarted"), rounds));
		updateLabel(roundLabel, Integer.toString(rounds));
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
		}
	}

	@Override
	public void onTurnFinished(Player finishedPlayer, Score remainingScore) {
		logger.info("Player {} finished the turn. Remaining points: {}",
				finishedPlayer, remainingScore.getTotalScore());
		updateLabel(statusBar, String.format(
				I18N.getString("playerFinishedTurn"),
				finishedPlayer.getName(), remainingScore.getTotalScore()));
	}

	@Override
	public void onRemainingScoreForPlayer(Player currentPlayer, Score remainingScore) {
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
	public void onPlayerFinished(Player currentPlayer) {
		logger.info("Player {} finished!!!!!!! You are a Dart god!",
				currentPlayer);
		updateLabel(statusBar, String.format(I18N.getString("playerFinished"), currentPlayer.getName()));
	}

	@Override
	public void onGameFinished(Map<Player, Score> playerScoreMap, List<Player> winner) {
		logger.info("The game has ended!");

		for (Player player : playerScoreMap.keySet()) {
			logger.info("{}: {}", player, playerScoreMap.get(player));
		}

		updateLabel(statusBar, String.format(I18N.getString("gameHasEnded"), winner.toString()));
		
		try {
			EventEngine.instance().shutdown();
		} catch (InitializationException e) {
			logger.warn(e.getMessage(), e);
		}
	}

	@Override
	public void onNextPlayerPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBounceOutPressed() {
		processPointEvent("bounce out!");
		theBoard.removeLastHit();
	}

	@Override
	public void onDartMissedPressed() {
		processPointEvent("missed!");
	}

}
