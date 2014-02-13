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
package spare.n52.yadarts.persistence;

import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spare.n52.yadarts.games.Game;
import spare.n52.yadarts.games.Score;

public interface HighscorePersistence {

	public void addHighscoreEntry(Class<? extends Game> theGame, Score score) throws PersistencyException;

	public List<Score> getHighscore(Class<? extends Game> theGame) throws PersistencyException;
	
	/**
	 * The static class for getting the HighscorePersistence instance
	 */
	public static class Instance {
		
		private static final Logger logger = LoggerFactory.getLogger(Instance.class);
		private static HighscorePersistence instance;

		static {
			try {
				ServiceLoader<HighscorePersistence> l = ServiceLoader.load(HighscorePersistence.class);
				
				for (HighscorePersistence configuration : l) {
					instance = configuration;
					break;
				}
			}
			catch (RuntimeException e) {
				logger.warn(e.getMessage(), e);
			}
		}
		
		public static HighscorePersistence instance() {
			if (instance == null) {
				throw new IllegalStateException("No persistence available!");
			}
			
			return instance;
		}
		
	}


}
