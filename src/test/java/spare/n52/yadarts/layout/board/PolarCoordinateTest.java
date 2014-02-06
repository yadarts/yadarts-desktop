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
import org.junit.Assert;
import org.junit.Test;

public class PolarCoordinateTest {

	@Test
	public void testTransformation() {
		/*
		 * 20
		 */
		DynamicPolarCoordinate coord = new DynamicPolarCoordinate(20, 3, false);
		
		Point result = coord.calculatePoint(new Point(500, 500), 500);
		
		Assert.assertTrue(result.x == 500);
		Assert.assertTrue(result.y == 300);
		
		coord = new DynamicPolarCoordinate(20, 1, true);
		
		result = coord.calculatePoint(new Point(500, 500), 500);
		
		Assert.assertTrue(result.x == 500);
		Assert.assertTrue(result.y == 200);
		
		/*
		 * 11
		 */
		coord = new DynamicPolarCoordinate(11, 1, true);
		
		result = coord.calculatePoint(new Point(500, 500), 500);
		
		/*
		 * some tolerance required..
		 */
		Assert.assertTrue(result.x == 200);
		Assert.assertTrue(result.y - 500 <= 2);
	
		/*
		 * 3
		 */
		coord = new DynamicPolarCoordinate(3, 1, true);
		
		result = coord.calculatePoint(new Point(500, 500), 500);
		
		/*
		 * some tolerance required..
		 */
		Assert.assertTrue(result.x - 500 <= 2);
		Assert.assertTrue(result.y - 800 <= 2);
		
		/*
		 * 6
		 */
		coord = new DynamicPolarCoordinate(6, 1, true);
		
		result = coord.calculatePoint(new Point(500, 500), 500);
		
		/*
		 * some tolerance required..
		 */
		Assert.assertTrue(result.x - 800 <= 2);
		Assert.assertTrue(result.y - 800 <= 2);
	}
	
}
