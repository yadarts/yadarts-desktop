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

import spare.n52.yadarts.common.Disposable;
import spare.n52.yadarts.games.Game;
import spare.n52.yadarts.games.Score;

/**
 * Interface for the highscore peristency mechanism
 */
public interface HighscorePersistence extends Disposable {

	/**
	 * add a {@link Score} to the highscore for a specific {@link Game}
	 * class.
	 * 
	 * @param theGame the specific class
	 * @param score the score
	 * @throws PersistencyException
	 */
	public void addHighscoreEntry(Class<? extends Game> theGame, Score score) throws PersistencyException;

	/**
	 * get the list of highscore entries for a specific {@link Game}
	 * class.
	 * 
	 * @param theGame the specific class
	 * @return the ordered list of scores (low first)
	 * @throws PersistencyException
	 */
	public List<Score> getHighscore(Class<? extends Game> theGame) throws PersistencyException;
	

}
