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

/**
 * Multiple hits in the same field require some sort of
 * drawing deviation. This class is used by {@link DynamicPolarCoordinate}
 * to apply an offset so darts are not rendered at the exact same position.
 */
public class Deviation {

	
	private int angleDelta;
	private double distanceFactor;
	
	/**
	 * @param i the angle deviation in degree (see {@link #getAngleDelta()})
	 * @param d the distance deviation as a factor (see {@link #getDistanceFactor()})
	 */
	public Deviation(int i, double d) {
		angleDelta = i;
		distanceFactor = d;
	}

	public Deviation() {
		this(0, 1);
	}

	/**
	 * @return the delta angle in degree
	 */
	public int getAngleDelta() {
		return angleDelta;
	}
	
	public void setAngleDelta(int angleDelta) {
		this.angleDelta = angleDelta;
	}
	
	/**
	 * @return the distance factor (e.g. > 1 defines greater distance from center)
	 */
	public double getDistanceFactor() {
		return distanceFactor;
	}
	
	public void setDistanceFactor(double distanceDelta) {
		this.distanceFactor = distanceDelta;
	}
	
	
	
}
