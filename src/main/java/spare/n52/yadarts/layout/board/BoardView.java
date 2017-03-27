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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spare.n52.yadarts.entity.PointEvent;
import spare.n52.yadarts.themes.Theme;

public class BoardView extends Composite {
	
	private static final Logger logger = LoggerFactory.getLogger(BoardView.class);
	private static final int MAX_M_SIZE = 1000;
	private static final int MAX_LO_SIZE = 750;
	private final Color dartColor = new Color(getDisplay(), new RGB(255, 0, 255));
	
	private Image imageM;
	private Image imageHi;
	private Image imageLo;
	private Canvas theBoard;
	private List<DynamicPolarCoordinate> arrows = new ArrayList<>();
	private Point currentCenter;
	private int currentRadius;
	private int currentDartSize;
        private int currentWidthHeight;
        private Image currentImage;

	public BoardView(final Composite parent, int style) {
		super(parent, style);
		this.setLayout(new FillLayout());

		try {
			imageM = Theme.getCurrentTheme().getBoardM(getDisplay());
			imageHi = Theme.getCurrentTheme().getBoardHi(getDisplay());
			imageLo = Theme.getCurrentTheme().getBoardLo(getDisplay());
		} catch (FileNotFoundException e1) {
			logger.warn(e1.getMessage(), e1);
			throw new IllegalStateException("The theme is not correctly configured");
		}
		

		theBoard = new Canvas(this, SWT.INHERIT_FORCE);
		theBoard.addPaintListener(new PaintListener() {
                    public void paintControl(PaintEvent e) {
                        GC gc = e.gc;
                        gc.setAntialias(SWT.ON);
                        gc.setInterpolation(SWT.HIGH);
                        gc.drawImage(currentImage,
                                0, 0, currentImage.getBounds().width, currentImage.getBounds().height,
                                currentCenter.x - currentWidthHeight/2, currentCenter.y - currentWidthHeight/2, currentWidthHeight, currentWidthHeight);
                    }
                });

		theBoard.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(ControlEvent e) {
				super.controlResized(e);
				Rectangle newBounds = theBoard.getBounds();

				BoardView.this.currentWidthHeight = Math
						.min(newBounds.height, newBounds.width);

				BoardView.this.currentImage = resize(currentWidthHeight,
						currentWidthHeight);

				parent.layout();
			}

		});
		
		theBoard.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				logger.trace("current center is {}", currentCenter);
				
				for (DynamicPolarCoordinate c : arrows) {
					drawDartAt(c.calculatePoint(currentCenter, currentRadius), e.gc);					
				}
				
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
		
		Image scaled = new Image(image.getDevice(), width, height);
                updateScaleRatio(scaled);
                scaled.dispose();
                
		return image;
	}

	private void updateScaleRatio(Image image) {
		this.currentCenter = new Point(theBoard.getBounds().width / 2, theBoard.getBounds().height / 2);
		this.currentRadius = image.getBounds().width / 2;
		this.currentDartSize = this.currentRadius / 12;
	}

	public void removeLastHit() {
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (arrows != null && arrows.size() > 0) {
					arrows.remove(arrows.size()-1);
					theBoard.redraw();
				}				
			}
		});
	}

	public void onPointEvent(final PointEvent event) {
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				processNumberHit(event);				
			}
		});
	}


	private void processNumberHit(PointEvent event) {
		DynamicPolarCoordinate coordinate = new DynamicPolarCoordinate(event);
		coordinate.setDeviation(calculateDeviation(event));
		this.arrows.add(coordinate);
		theBoard.redraw();
	}

	private Deviation calculateDeviation(PointEvent event) {
		if (this.arrows.size() == 0) {
			return new Deviation();
		}
		
		List<DynamicPolarCoordinate> inSameField = new ArrayList<>(2);
		for (DynamicPolarCoordinate c : this.arrows) {
			if (sameField(event, c.getEvent())) {
				inSameField.add(c);
			}
		}
		
		if (inSameField.isEmpty()) {
			return new Deviation();
		}
		else {
			return calculateFieldDependentDeviation(event, inSameField);
		}
	}

	private Deviation calculateFieldDependentDeviation(PointEvent event,
			List<DynamicPolarCoordinate> inSameField) {
		
		if (event.getMultiplier() == 1) {
			if (event.getBaseNumber() == 25) {
				return HitAreaConstants.BULLSEYE_DEVIATION[inSameField.size()-1]; 
			}
			if (event.isOuterRing()) {
				return HitAreaConstants.OUTER_RING_DEVIATION[inSameField.size()-1];
			}
			else {
				return HitAreaConstants.INNER_RING_DEVIATION[inSameField.size()-1];
			}
		}
		else if (event.getMultiplier() == 2) {
			if (event.getBaseNumber() == 25) {
				return HitAreaConstants.DOUBLE_BULLSEYE_DEVIATION[inSameField.size()-1]; 
			}
			return HitAreaConstants.DOUBLE_DEVIATION[inSameField.size()-1];
		}
		else if (event.getMultiplier() == 3) {
			return HitAreaConstants.TRIPLE_DEVIATION[inSameField.size()-1];
		}
		
		return new Deviation();
	}

	private boolean sameField(PointEvent a, PointEvent b) {
		return a.getBaseNumber() == b.getBaseNumber()
				&& a.getMultiplier() == b.getMultiplier()
				&& a.isOuterRing() == b.isOuterRing();
	}

	private void drawDartAt(Point point, GC gc) {
		gc.setForeground(dartColor);
		gc.setLineWidth(3);
		
		gc.drawLine(point.x-currentDartSize, point.y-currentDartSize, point.x+currentDartSize, point.y+currentDartSize);
		gc.drawLine(point.x-currentDartSize, point.y+currentDartSize, point.x+currentDartSize, point.y-currentDartSize);
	}

	public void removeAllArrows() {
		getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				arrows.clear();
				theBoard.redraw();
			}
		});
		
	}


}
