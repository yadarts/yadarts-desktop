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

public class DynamicPolarCoordinate {
	
	private static final Logger logger = LoggerFactory.getLogger(DynamicPolarCoordinate.class);
	
	private int baseNumber;
	private int multiplier;
	private boolean outerRing;

	public DynamicPolarCoordinate(int base, int multi, boolean outer) {
		this.baseNumber = base;
		this.multiplier = multi;
		this.outerRing = outer;
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
			targetDistance = (radius * 0.4);
			break;
		case 2:
			targetDistance = (radius * 0.78);
			break;
		default:
			if (outerRing) {
				targetDistance = (radius * 0.6);
			}
			else {
				targetDistance = (radius * 0.2);
			}
			break;
		}
		
		/*
		 * bullseye special treatment
		 */
		if (baseNumber == 25) {
			if (multiplier == 2) {
				targetDistance = 0;
			}
			else {
				targetDistance = radius * 0.05;
			}
		}
		
		double deltaX = targetDistance * Math.cos(Math.toRadians(angle));
		double deltaY = targetDistance * Math.sin(Math.toRadians(angle));
		
		logger.info("Delta x: {}, Delta y: {}", deltaX, deltaY);
		
		double x = center.x + deltaX;
		double y = center.y - deltaY;
		
		return new Point((int) x, (int) y);
	}
	
}
