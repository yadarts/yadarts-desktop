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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spare.n52.yadarts.MainWindow;

public class WelcomeView extends Composite {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WelcomeView.class);

	private Label imageHolder;
	private Image image;

	private Cursor cursorHand;
	private Cursor cursorArrow;
	
	//new game
	//225, 175 -> 600, 275
	private int[] newGameOrigBbox = new int[] {225, 175, 600, 275};

	//highscore
	//225, 320 -> 625, 420
	private int[] highScoreOrigBbox = new int[] {225, 320, 625, 420};

	private int[] newGameScaleBounds;

	private int[] highScoreScaleBounds;

	private MainWindow mainWindow;
	
	public WelcomeView(Composite parent, int style, MainWindow mw) {
		super(parent, style);
		
		this.mainWindow = mw;

		image = new Image(getDisplay(), getClass().getResourceAsStream(
				"/images/mm_bg.jpg"));

		this.setLayout(new FillLayout());
		this.setBackgroundMode(SWT.INHERIT_FORCE);
		imageHolder = new Label(this, SWT.BORDER);
		imageHolder.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event e) {
				calculateBounds();
			}
		});
		imageHolder.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				paintScaledImage(e.gc);
			}
		});
		imageHolder.addMouseMoveListener(new MouseMoveListener() {
			
			@Override
			public void mouseMove(MouseEvent e) {
				if (isOverButton(e)) {
					imageHolder.setCursor(cursorHand);
				}
				else {
					imageHolder.setCursor(cursorArrow);
				}
			}
		});
		imageHolder.addMouseListener(new MouseListener() {
			private long startClick;

			@Override
			public void mouseUp(MouseEvent e) {
				if (System.currentTimeMillis() - startClick <= 200) {
					handleButtonClick(e);					
				}
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				startClick = System.currentTimeMillis();
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
		cursorHand = new Cursor(imageHolder.getDisplay(), SWT.CURSOR_HAND);
		cursorArrow = new Cursor(imageHolder.getDisplay(), SWT.CURSOR_ARROW);
	}

	protected void handleButtonClick(MouseEvent e) {
		if (newGameScaleBounds == null || highScoreScaleBounds == null) {
			return;
		}
		
		if (cursorWithin(newGameScaleBounds, e)) {
        	mainWindow.createNewGameDialog();
		}
		
		if (cursorWithin(highScoreScaleBounds, e)) {
			
		}
	}

	protected boolean isOverButton(MouseEvent e) {
		if (newGameScaleBounds == null || highScoreScaleBounds == null) {
			return false;
		}
		
		if (cursorWithin(newGameScaleBounds, e) || cursorWithin(highScoreScaleBounds, e)) {
			return true;
		}
		else {
			LOGGER.info(e.x +" "+ e.y);
		}
		
		return false;
	}

	private boolean cursorWithin(int[] b, MouseEvent e) {
		if (e.x > b[0] && e.x < b[2] && e.y > b[1] && e.y < b[3]) {
			return true;
		}
		return false;
	}

	protected void calculateBounds() {
		Rectangle targetBounds = imageHolder.getBounds();
		
		if (targetBounds.width == 0 ||  targetBounds.height == 0) {
			return;
		}
		
		Rectangle imageBounds = image.getBounds();
		double xScale = (double) imageBounds.width / (double) targetBounds.width;
		double yScale = (double) imageBounds.height / (double) targetBounds.height;
		
		newGameScaleBounds = calculateScaledBounds(xScale, yScale, newGameOrigBbox);
		LOGGER.info("NEWGAME: " +Arrays.toString(newGameScaleBounds));
		highScoreScaleBounds = calculateScaledBounds(xScale, yScale, highScoreOrigBbox);
		LOGGER.info("hi: " +Arrays.toString(highScoreScaleBounds));
		
		imageHolder.redraw();
	}

	private int[] calculateScaledBounds(double xScale, double yScale,
			int[] src) {
		int[] result = new int[4];
		result[0] = (int) (src[0] / xScale);
		result[2] = (int) (src[2] / xScale);
		result[1] = (int) (src[1] / yScale);
		result[3] = (int) (src[3] / yScale);
		return result;
	}

	protected void paintScaledImage(GC gc) {
		int width = image.getBounds().width;
		int height = image.getBounds().height;
		
		Rectangle targetBounds = imageHolder.getBounds();
		
		gc.drawImage(image, 0, 0, width, height, 0, 0, targetBounds.width, targetBounds.height);
		gc.dispose();
	}

}
