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

import java.util.Collections;
import java.util.List;

/**
 * a {@link GameView} defines its required input params via this
 * method.
 * 
 * @param <T> the type of the param
 */
public class GameParameter<T> {

	private String name;
	private Bounds majority;
	private List<T> value;
	private Class<? extends T> clazz;
	
	public GameParameter(Class<? extends T> clazz, String name, Bounds majority) {
		this.clazz = clazz;
		this.name = name;
		this.majority = majority;
	}
	
	/**
	 * @param value the value (e.g. as received from a user input). if only one, use {@link Collections#singletonList(Object)}
	 */
	public void setValue(List<T> value) {
		this.value = value;
	}
	
	/**
	 * @return the filled value
	 */
	public List<T> getValue() {
		return value;
	}
	
	public Class<? extends T> getType() {
		return clazz;
	}

	/**
	 * @return the name of the param, shall be unique in the owning {@link GameView} 
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the (min, max) majority of the param
	 */
	public Bounds getBounds() {
		return majority;
	}
	
	public static class Bounds {
		
		private int min;
		private int max;
		
		public static Bounds unbound(int min) {
			return new Bounds(min, Integer.MAX_VALUE);
		}
		
		public Bounds(int min, int max) {
			super();
			this.min = min;
			this.max = max;
		}
		
		public int getMin() {
			return min;
		}
		
		public int getMax() {
			return max;
		}		
		
		public boolean isUnbound() {
			return max == Integer.MAX_VALUE;
		}
		
	}

	
}
