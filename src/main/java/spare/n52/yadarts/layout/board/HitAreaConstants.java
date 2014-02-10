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

import java.util.HashMap;
import java.util.Map;

public class HitAreaConstants {

	private static Map<Integer, Integer> angleMap = new HashMap<>();

	public static final Deviation[] OUTER_RING_DEVIATION = new Deviation[] {
		new Deviation(3, 1.18), new Deviation(1, 0.85)
	};
	public static final Deviation[] INNER_RING_DEVIATION = new Deviation[] {
		new Deviation(1, 1.35), new Deviation(-3, 0.5)
	};
	public static final Deviation[] DOUBLE_DEVIATION = new Deviation[] {
		new Deviation(4, 0.995), new Deviation(-5, 0.99)
	};
	public static final Deviation[] TRIPLE_DEVIATION = new Deviation[] {
		new Deviation(-4, 1.025), new Deviation(3, 0.978)
	};
	public static final Deviation[] BULLSEYE_DEVIATION = new Deviation[] {
		new Deviation(180, 1.2), new Deviation(67, 1.1)
	};
	public static final Deviation[] DOUBLE_BULLSEYE_DEVIATION = new Deviation[] {
		new Deviation(140, 1.4), new Deviation(-67, 1.6)
	};
	
	static {
		angleMap.put(6, 0);
		angleMap.put(13, 1*18);
		angleMap.put(4, 2*18);
		angleMap.put(18, 3*18);
		angleMap.put(1, 4*18);
		angleMap.put(20, 90);
		angleMap.put(5, 90+1*18);
		angleMap.put(12, 90+2*18);
		angleMap.put(9, 90+3*18);
		angleMap.put(14, 90+4*18);
		angleMap.put(11, 180);
		angleMap.put(8, 180+1*18);
		angleMap.put(16, 180+2*18);
		angleMap.put(7, 180+3*18);
		angleMap.put(19, 180+4*18);
		angleMap.put(3, 270);
		angleMap.put(17, 270+1*18);
		angleMap.put(2, 270+2*18);
		angleMap.put(15, 270+3*18);
		angleMap.put(10, 270+4*18);
	}
	
	/**
	 * @param base the hit number 
	 * @return the angle for the polar coordinate of the hit number
	 */
	public static int getAngle(int base) {
		return angleMap.get(base);
	}
	
}
