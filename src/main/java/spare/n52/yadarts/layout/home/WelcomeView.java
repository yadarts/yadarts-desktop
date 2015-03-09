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
package spare.n52.yadarts.layout.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private Image background;
	private MenuImage logo;
	
	private Cursor cursorHand;
	private Cursor cursorArrow;
	
	private MainWindow mainWindow;

	private double targetScale;

	private int xStart;

	private int yStart;

	private MenuImage activeImage;

	private List<MenuImage> menuImages = new ArrayList<>();;
	private Map<MenuImage, Listener> menuImagesHandlers = new HashMap<>();

	
	public WelcomeView(Composite parent, int style, MainWindow mw) {
		super(parent, style);
		
		this.mainWindow = mw;
		
		background = new Image(getDisplay(), getClass().getResourceAsStream(
				"/images/background.jpg"));

		image = new Image(getDisplay(), getClass().getResourceAsStream(
				"/images/mm_bg.png"));
		
		MenuImage newGame = new MenuImage("new_game", getDisplay(), 300, 425);
		
		MenuImage highscore = new MenuImage("highscore", getDisplay(), 265, 526);
		
		MenuImage exit = new MenuImage("exit", getDisplay(), 1540, 945);

		this.menuImages.add(newGame);
		this.menuImages.add(highscore);
		this.menuImages.add(exit);
		
		this.menuImagesHandlers.put(newGame, new Listener() {
			@Override
			public void handleEvent(Event event) {
				mainWindow.createNewGameDialog();				
			}
		});
		this.menuImagesHandlers.put(highscore, new Listener() {
			@Override
			public void handleEvent(Event event) {
				mainWindow.createHighscoreView();
			}
		});
		this.menuImagesHandlers.put(exit, new Listener() {
			@Override
			public void handleEvent(Event event) {
				mainWindow.exit();
			}
		});
		
		logo = new MenuImage(new Image(getDisplay(), getClass().getResourceAsStream(
				"/images/logo.png")),
				null, 50, 50);
		

		
		this.setLayout(new FillLayout());
		this.setBackgroundMode(SWT.INHERIT_FORCE);
		this.setBackgroundImage(background);
		imageHolder = new Label(this, SWT.NONE);
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
			
			private MenuImage previousActiveImage;

			@Override
			public void mouseMove(MouseEvent e) {
				if (isOverButton(e)) {
					checkRedraw();
				}
				else {
					activeImage = null;
					checkRedraw();
				}
			}

			private void checkRedraw() {
				if (previousActiveImage != activeImage) {
					previousActiveImage = activeImage;
					if (activeImage != null) {
						imageHolder.setCursor(cursorHand);
					}
					else {
						imageHolder.setCursor(cursorArrow);
					}
					imageHolder.redraw();
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
		
		LOGGER.info("initialized WelcomeView");
	}

	protected void handleButtonClick(MouseEvent e) {
		Listener handler = this.menuImagesHandlers.get(activeImage);
		
		if (handler != null) {
			Event event = new Event();
			event.display = this.getDisplay();
			handler.handleEvent(event);
		}
		
	}

	protected boolean isOverButton(MouseEvent e) {
		for (MenuImage menuImage : menuImages) {
			if (cursorWithin(menuImage.getScaledBounds(), e)) {
				activeImage = menuImage;
				return true;	
			}
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
		
		
		/*
		 * choose the smaller axis
		 */
		if (xScale < yScale) {
			targetScale = yScale;
			yStart = 0;
			int tmp = (int) (imageBounds.width / targetScale);
			xStart = (int) (targetBounds.width - tmp) / 2;
		}
		else {
			targetScale = xScale;
			xStart = 0;
			int tmp = (int) (imageBounds.height / targetScale);
			yStart = (int) (targetBounds.height - tmp) / 2;
		}
		
		/*
		 * place the menu items
		 */
		for (MenuImage menuImage : menuImages) {
			menuImage.calculateScaledBounds(targetScale, xStart, yStart);
		}

		logo.calculateScaledBounds(targetScale, xStart, yStart);
		
		imageHolder.redraw();
	}


	protected void paintScaledImage(GC gc) {
		int width = image.getBounds().width;
		int height = image.getBounds().height;
		
		gc.drawImage(image, 0, 0, width, height,
				xStart, yStart, (int) (width/targetScale), (int) (height/targetScale));
		
		for (MenuImage menuImage : menuImages) {
			drawMenuImage(gc, menuImage, menuImage == activeImage);	
		}
		
		drawMenuImage(gc, logo, false);
		
		gc.dispose();
	}

	private void drawMenuImage(GC gc, MenuImage mi, boolean active) {
		int[] bounds = mi.getScaledBounds();
		
		Image img;
		if (active) {
			img = mi.getActiveImage();
		}
		else {
			img = mi.getImage();
		}
		
		int targetWidth = bounds[2] - bounds[0];
		int targetHeight = bounds[3] - bounds[1];
		gc.drawImage(img, 0, 0, img.getBounds().width, img.getBounds().height, 
				bounds[0], bounds[1], targetWidth, targetHeight);
	}

}
