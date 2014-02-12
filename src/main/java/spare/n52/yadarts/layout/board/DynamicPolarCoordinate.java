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

import org.eclipse.swt.graphics.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spare.n52.yadarts.entity.PointEvent;

/**
 * Polar coordinates are used to calculate the points
 * on the board where a dart should be drawn.
 */
public class DynamicPolarCoordinate {
	
	private static final Logger logger = LoggerFactory.getLogger(DynamicPolarCoordinate.class);
	
	private int baseNumber;
	private int multiplier;
	private boolean outerRing;

	private Deviation deviation;

	private PointEvent event;

	public DynamicPolarCoordinate(int base, int multi, boolean outer) {
		this.baseNumber = base;
		this.multiplier = multi;
		this.outerRing = outer;
	}

	public DynamicPolarCoordinate(PointEvent event) {
		this(event.getBaseNumber(), event.getMultiplier(),
						event.isOuterRing());
		this.event = event;
	}

	/**
	 * @param center the current center of the board
	 * @param radius the current radius of the board
	 * @return the calculated point
	 */
	public Point calculatePoint(Point center, int radius) {
		int angle;
		
		/*
		 * bullseye special treatment
		 */
		if (baseNumber == 25) {
			angle = 45;
		}
		else {
			angle = HitAreaConstants.getAngle(baseNumber);
		}
		
		double targetDistance;
		switch (multiplier) {
		case 3:
			targetDistance = radius * 0.5;
			break;
		case 2:
			targetDistance = radius * 0.82;
			break;
		default:
			if (outerRing) {
				targetDistance = radius * 0.65;
			}
			else {
				targetDistance = radius * 0.35;
			}
			break;
		}
		
		/*
		 * bullseye special treatment
		 */
		if (baseNumber == 25) {
			if (multiplier == 2) {
				targetDistance = radius * 0.01;
			}
			else {
				targetDistance = radius * 0.05;
			}
		}
		
		/*
		 * apply deviation
		 */
		if (deviation != null) {
			targetDistance = targetDistance * deviation.getDistanceFactor();
			angle += deviation.getAngleDelta();	
		}
		
		double deltaX = targetDistance * Math.cos(Math.toRadians(angle));
		double deltaY = targetDistance * Math.sin(Math.toRadians(angle));
		
		logger.info("Delta x: {}, Delta y: {}", deltaX, deltaY);
		
		double x = center.x + deltaX;
		double y = center.y - deltaY;
		
		return new Point((int) x, (int) y);
	}

	public void setDeviation(Deviation d) {
		this.deviation = d;
	}

	public Deviation getDeviation() {
		return deviation;
	}

	public PointEvent getEvent() {
		return event;
	}
	
	
}
