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
package spare.n52.yadarts.layout.board;

import java.io.FileNotFoundException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spare.n52.yadarts.entity.PointEvent;
import spare.n52.yadarts.themes.Theme;

public class BoardView extends Composite {
	
	private static final Logger logger = LoggerFactory.getLogger(BoardView.class);
	private static final int MAX_M_SIZE = 1000;
	private static final int MAX_LO_SIZE = 750;
	
	private Image imageM;
	private Image imageHi;
	private Image imageLo;
	private Label theBoard;

	public BoardView(final Composite parent, int style) {
		super(parent, style);
		this.setLayout(new FillLayout());

		try {
			imageM = Theme.getCurrentTheme().getBoardM(getDisplay());
			imageHi = Theme.getCurrentTheme().getBoardHi(getDisplay());
			imageLo = Theme.getCurrentTheme().getBoardLo(getDisplay());
		} catch (FileNotFoundException e1) {
			logger.warn(e1.getMessage(), e1);
			try {
				imageM = Theme.getDefault().getBoardM(getDisplay());
				imageHi = Theme.getDefault().getBoardHi(getDisplay());
				imageLo = Theme.getDefault().getBoardLo(getDisplay());
			} catch (FileNotFoundException e2) {
				logger.warn("The default theme is not available! {}", e2.getMessage());
				throw new IllegalStateException("The default theme is not available");
			}
			
		}
		
		theBoard = new Label(this, SWT.NONE);
		theBoard.setImage(imageM);
		theBoard.setAlignment(SWT.CENTER);

		theBoard.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(ControlEvent e) {
				super.controlResized(e);
				Rectangle newBounds = theBoard.getBounds();

				int newWidthHeight = Math
						.min(newBounds.height, newBounds.width);

				theBoard.setImage(resize(newWidthHeight,
						newWidthHeight));

				parent.layout();
			}

		});
	}
	
	private Image resize(int width, int height) {
		Image image;
		if (width > MAX_M_SIZE) {
			image = imageHi;
		}
		else if (width > MAX_LO_SIZE) {
			image = imageM;
		}
		else {
			image = imageLo;
		}
		
		Image scaled = new Image(getDisplay(), width, height);
		
		GC gc = new GC(scaled);
		try {
			gc.setAntialias(SWT.ON);
			gc.setInterpolation(SWT.HIGH);	
		}
		catch (SWTException e) {
			logger.warn(e.getMessage());
			logger.debug(e.getMessage(), e);
		}
		
		gc.drawImage(image, 0, 0, image.getBounds().width,
				image.getBounds().height, 0, 0, width, height);
		gc.dispose();
		
		return scaled;
	}

	public void removeLastHit() {
		// TODO Auto-generated method stub
		
	}

	public void onPointEvent(final PointEvent event) {
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				Rectangle currentBounds = theBoard.getBounds();
				Point center = new Point(currentBounds.x + currentBounds.width / 2,
						currentBounds.y + currentBounds.height / 2);

				processNumberHit(event.getBaseNumber(), event.getMultiplier(),
						event.isOuterRing(), center, currentBounds.height / 2);				
			}
		});
	}


	private void processNumberHit(int baseNumber, int multiplier,
			boolean outerRing, Point center, int radius) {
		DynamicPolarCoordinate coordinate = new DynamicPolarCoordinate(baseNumber, multiplier, outerRing);
		
		logger.info("current center is {}", center);
		
		drawDartAt(coordinate.calculatePoint(center, radius));
	}

	private void drawDartAt(Point point) {
		logger.info("Dart would draw at {}", point);
	}


}
