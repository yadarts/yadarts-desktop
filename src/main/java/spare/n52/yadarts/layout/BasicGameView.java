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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
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

public class BasicGameView extends Composite implements
		GameStatusUpdateListener {

	private static final Logger logger = LoggerFactory
			.getLogger(BasicGameView.class);
	private Label currentScore;
	private Label currentPlayer;
	private BoardView theBoard;
	private Label turnSummary;
	private Composite leftBar;
	private Composite rightBar;
	private List<Player> thePlayers = Arrays.asList(new Player[] {
			new PlayerImpl("Jan"), new PlayerImpl("Benjamin"),
			new PlayerImpl("Eike"), new PlayerImpl("Matthes") });
	private Composite bottomBar;
	private Label statusBar;

	public BasicGameView(Composite parent, int style) {
		super(parent, style);
		FormLayout formLayout = new FormLayout();
		formLayout.marginHeight = 5;
		formLayout.marginWidth = 5;
		formLayout.spacing = 5;
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
		GenericX01Game x01Game = new GenericX01Game(thePlayers, 301, this);
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
		rightBar = new Composite(container, SWT.BORDER);
		FormData rightBarData = new FormData();
		rightBarData.top = new FormAttachment(0);
		rightBarData.left = new FormAttachment(theBoard);
		rightBarData.right = new FormAttachment(100);
		rightBarData.bottom = new FormAttachment(80);
		rightBar.setLayoutData(rightBarData);

		RowLayout leftBarLayout = new RowLayout(SWT.VERTICAL);
		leftBarLayout.spacing = 5;
		rightBar.setLayout(leftBarLayout);

		new Label(rightBar, SWT.UNDERLINE_SINGLE).setText(I18N.getString("turnThrows").concat(":"));
		turnSummary = new Label(rightBar, SWT.NONE);
		turnSummary.setFont(new Font(getDisplay(), new FontData("Arial", 16,
				SWT.NONE)));
	}

	private void createLeftBar(Composite container) {
		leftBar = new Composite(container, SWT.BORDER);
		FormData leftBarData = new FormData();
		leftBarData.top = new FormAttachment(0);
		leftBarData.left = new FormAttachment(0);
		leftBarData.right = new FormAttachment(20);
		leftBarData.bottom = new FormAttachment(80);
		leftBar.setLayoutData(leftBarData);

		RowLayout leftBarLayout = new RowLayout(SWT.VERTICAL);
		leftBarLayout.spacing = 5;
		leftBar.setLayout(leftBarLayout);

		new Label(leftBar, SWT.UNDERLINE_SINGLE).setText(I18N.getString("theTurnIsOn").concat(":"));
		currentPlayer = new Label(leftBar, SWT.NONE);
		currentPlayer.setFont(new Font(getDisplay(), new FontData("Arial", 24,
				SWT.BOLD)));

		new Label(leftBar, SWT.UNDERLINE_SINGLE).setText(I18N.getString("remainingScore").concat(":"));
		currentScore = new Label(leftBar, SWT.NONE);
		currentScore.setFont(new Font(getDisplay(), new FontData("Arial", 24,
				SWT.NONE)));
		currentScore.setText("301");

	}

	private void initSecondRow(Composite container) {
		bottomBar = new Composite(container, SWT.BORDER);
		bottomBar.setLayout(new FillLayout());
		FormData leftBarData = new FormData();
		leftBarData.top = new FormAttachment(leftBar);
		leftBarData.left = new FormAttachment(0);
		leftBarData.right = new FormAttachment(100);
		leftBarData.bottom = new FormAttachment(100);
		bottomBar.setLayoutData(leftBarData);

		statusBar = new Label(bottomBar, SWT.NONE);
		statusBar.setFont(new Font(getDisplay(), new FontData("Arial", 14,
				SWT.NONE)));
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
	public void onCurrentPlayerChanged(final Player p, int remaining) {
		updateLabel(currentPlayer, p.getName());
		updateLabel(turnSummary, "");
		remainingScoreForPlayer(p, remaining);
	}

	@Override
	public void onBust(final Player p, int remaining) {
		String s = String.format(I18N.getString("playerBusted"),
				p.getName(), remaining);
		logger.info(s);
		updateLabel(statusBar, s);
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
	public void roundStarted(int rounds) {
		logger.info("+++++++++++++++++++");
		logger.info("Round {} started!", rounds);
		logger.info("+++++++++++++++++++");
		updateLabel(statusBar, String.format("Round %d started!", rounds));
	}

	@Override
	public void provideFinishingCombination(
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
	public void onTurnFinished(Player finishedPlayer, int remainingScore) {
		logger.info("Player {} finished the turn. Remaining points: {}",
				finishedPlayer, remainingScore);
		updateLabel(statusBar, String.format(
				"Player %s finished the turn. Remaining points: %d",
				finishedPlayer.getName(), remainingScore));
	}

	@Override
	public void remainingScoreForPlayer(Player currentPlayer, int remainingScore) {
		logger.info("Player {}'s remaining points: {}", currentPlayer,
				remainingScore);
		updateLabel(currentScore, Integer.toString(remainingScore));
	}

	@Override
	public void requestNextPlayerEvent() {
		logger.info("Please press 'Next Player'!");
		updateLabel(statusBar, "Please press 'Next Player'!");
	}

	@Override
	public void playerFinished(Player currentPlayer) {
		logger.info("Player {} finished!!!!!!! You are a Dart god!",
				currentPlayer);
		updateLabel(statusBar, "Player {} finished!!!!!!! You are a Dart god!");
	}

	@Override
	public void onGameFinished(Map<Player, Score> playerScoreMap) {
		logger.info("The game has ended!");

		for (Player player : playerScoreMap.keySet()) {
			logger.info("{}: {}", player, playerScoreMap.get(player));
		}

		updateLabel(statusBar, "The game has ended!");
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
