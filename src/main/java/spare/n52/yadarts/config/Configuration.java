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
package spare.n52.yadarts.config;

import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Configuration {

	/**
	 * @return true if the application should start in full screen mode
	 */
	public boolean isAutoFullScreen();
	
	/**
	 * @see #isAutoFullScreen()
	 * @param b the auto fullscreen state
	 */
	public void setAutoFullScreen(boolean b);
	
	/**
	 * @return the time in seconds within the next throw should be taken 
	 */
	public int getCallerTimeout();
	
	/**
	 * @see #getCallerTimeout()
	 * @param storing the caller timeout
	 */
	public void storeCallerTimeout(int to);
	
	/**
	 * The static class for getting the Configuration instance
	 */
	public static class Instance {
		
		private static final Logger logger = LoggerFactory.getLogger(Instance.class);
		private static Configuration instance;

		static {
			try {
				ServiceLoader<Configuration> l = ServiceLoader.load(Configuration.class);
				
				for (Configuration configuration : l) {
					instance = configuration;
					break;
				}
			}
			catch (RuntimeException e) {
				logger.warn(e.getMessage(), e);
			}
		}
		
		public static Configuration instance() {
			if (instance == null) {
				throw new IllegalStateException("No configuration available!");
			}
			
			return instance;
		}
		
	}

	/**
	 * @return the name of the sound package. Should match the name of the sub folder in "sounds".
	 */
	public String getSoundPackage();
}
