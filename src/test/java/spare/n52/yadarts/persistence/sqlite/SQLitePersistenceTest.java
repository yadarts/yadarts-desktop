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
package spare.n52.yadarts.persistence.sqlite;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import spare.n52.yadarts.entity.impl.PlayerImpl;
import spare.n52.yadarts.games.Score;
import spare.n52.yadarts.games.x01.Three01Game;
import spare.n52.yadarts.persistence.PersistedScore;
import spare.n52.yadarts.persistence.PersistencyException;

public class SQLitePersistenceTest {

	private File theFile = new File("./test.db");
	private Date now = new Date();
	
	@Test
	public void testPersistency() throws PersistencyException {
		if (theFile.exists()) {
			theFile.delete();
		}
		
		storeScore();
		
		storeScore();
		
		DummySQLitePersistence sql = new DummySQLitePersistence();
		
		List<Score> result = sql.getHighscore(Three01Game.class);
		
		Assert.assertTrue(result.size() == 2);
		
		Score first = result.get(0);
		
		Assert.assertTrue(first.getPlayer().getName().equals("test0r"));
		Assert.assertTrue(first.getDateTime().equals(now));
		Assert.assertTrue(first.getThrownDarts() == 123);
		Assert.assertTrue(first.getTotalTime() == 42);		
	}
	
	private void storeScore() throws PersistencyException {
		DummySQLitePersistence sql = new DummySQLitePersistence();
		
		PersistedScore score = new PersistedScore();
		
		score.setPlayer(new PlayerImpl("test0r"));
		score.setThrownDarts(123);
		
		score.setTime(now);
		score.setTotalTime(42);
		
		sql.addHighscoreEntry(Three01Game.class, score);
	}

	public class DummySQLitePersistence extends SQLitePersistence {

		public DummySQLitePersistence() throws PersistencyException {
			super();
		}
		
		@Override
		protected File resolveFile() throws IOException {
			return theFile;
		}
	}
	
}
