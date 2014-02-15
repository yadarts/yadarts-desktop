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

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spare.n52.yadarts.entity.impl.PlayerImpl;
import spare.n52.yadarts.games.AnnotatedGame;
import spare.n52.yadarts.games.Game;
import spare.n52.yadarts.games.Score;
import spare.n52.yadarts.games.x01.Five01Game;
import spare.n52.yadarts.games.x01.GenericX01Game;
import spare.n52.yadarts.games.x01.Seven01Game;
import spare.n52.yadarts.games.x01.Three01Game;
import spare.n52.yadarts.persistence.HighscorePersistence;
import spare.n52.yadarts.persistence.PersistedScore;
import spare.n52.yadarts.persistence.PersistenceUtil;
import spare.n52.yadarts.persistence.PersistencyException;

public abstract class AbstractJDBCPersistence implements HighscorePersistence {
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractJDBCPersistence.class);
	private static final DateTimeFormatter isoDate = ISODateTimeFormat.dateTime(); 
	
	private static final String TOTAL_TIME = "totalTimeSeconds";
	private static final String TIME = "time";
	private static final String DART_COUNT = "dartCount";
	private static final String PLAYER = "player";
	
	
	private static final ColumnWithType[] X01_COLUMNS = new ColumnWithType[] {
		ColumnWithType.string(PLAYER),
		ColumnWithType.integer(DART_COUNT),
		ColumnWithType.string(TIME), 
		ColumnWithType.integer(TOTAL_TIME)
	};
	private static final String PREFIX = "highscore_";
	
	private Connection connection;
	private static List<Class<? extends Game>> gameList;
	
	static {
		gameList = new ArrayList<>();
		gameList.add(Three01Game.class);
		gameList.add(Five01Game.class);
		gameList.add(Seven01Game.class);
		gameList.add(GenericX01Game.class);
	}

	public AbstractJDBCPersistence() throws PersistencyException {
		try {
			this.connection = createConnection();
		} catch (SQLException | IOException e1) {
			throw new PersistencyException(e1);
		}
		
		try {
			ensureValidDatabaseState();
		} catch (SQLException e) {
			throw new PersistencyException(e);
		}
	}
	
	private void ensureValidDatabaseState() throws SQLException {
		for (Class<? extends Game> g: gameList ) {
			String gameName = resolveTableName(g);
			try {
				ensureTableExistsWithColumns(gameName, X01_COLUMNS);
			} catch (InvalidTableStateException e) {
				logger.warn("Table {} is inconsistent (or maybe empty). Recreating.", gameName);
				dropAndRecreateTable(gameName, X01_COLUMNS);
			}
		}
		
	}

	private void dropAndRecreateTable(String tableName,
			ColumnWithType... columns) throws SQLException {
		Statement statement = connection.createStatement();
		statement.executeUpdate("drop table if exists ".concat(tableName));
		statement.close();
		
		createTableWithColumns(tableName, columns);
	}

	private void ensureTableExistsWithColumns(String tableName, ColumnWithType... columns) throws SQLException, InvalidTableStateException {
		try (Statement stmt = connection.createStatement()) {
			ResultSet rs = stmt.executeQuery(String.format("SELECT name FROM sqlite_master WHERE type='table' AND name='%s'",
					tableName));
			
			/*
			 * throws a SQLException if not there
			 */
			rs.getString("name");
			
			ResultSet result = stmt.executeQuery(String.format("select %s from %s",
					concatColumns(columns), tableName));
			
			try {
				ensureAllColumnsExistWithCorrectTypes(result, tableName, columns);
			}
			catch (SQLException e) {
				/*
				 * invalid state. drop it an recreate it
				 */
				throw new InvalidTableStateException(e);
			}
		}
		catch (SQLException e) {
			/*
			 * its not there, just silently create it
			 */
			createTableWithColumns(tableName, columns);
			return;
		}
		
	}

	private void ensureAllColumnsExistWithCorrectTypes(ResultSet result, String tableName,
			ColumnWithType... columns) throws SQLException {
		for (ColumnWithType tableWithType : columns) {
			int index = result.findColumn(tableWithType.getName());
			Object obj = result.getObject(index);
			if (!obj.getClass().isAssignableFrom(tableWithType.getType())) {
				throw new SQLException(String.format("Invalid type for column %s: Expected %s, got %s", tableWithType.getName(),
						tableWithType.getType().getName(), obj.getClass().getName()));
			}
		}
	}

	private void createTableWithColumns(String tableName, ColumnWithType... columns) throws SQLException {
		Statement statement = connection.createStatement();
		statement.executeUpdate("drop table if exists '".concat(tableName).concat("';"));
		statement.executeUpdate(String.format("create table %s (id integer primary key autoincrement, %s)",
				tableName, createCreateColumns(columns)));
	}

	private String createCreateColumns(ColumnWithType... columns) {
		StringBuilder sb = new StringBuilder();
		
		for (ColumnWithType tableWithType : columns) {
			sb.append(tableWithType.getName());
			sb.append(" ");
			sb.append(tableWithType.getSQLType());
			sb.append(", ");
		}
		
		sb.delete(sb.length()-2, sb.length());
		
		return sb.toString();
	}

	private String concatColumns(ColumnWithType[] columns) {
		StringBuilder sb = new StringBuilder();
		
		for (ColumnWithType string : columns) {
			sb.append(string.getName());
			sb.append(", ");
		}
		
		sb.delete(sb.length()-2, sb.length());
		
		return sb.toString();
	}

	protected abstract Connection createConnection() throws SQLException, IOException;
	
	@Override
	public void addHighscoreEntry(Class<? extends Game> theGame, Score score) throws PersistencyException {
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate(String.format("insert into %s values(null, %s)",
					resolveTableName(theGame), encodeData(score)));
		}
		catch (SQLException e) {
			throw new PersistencyException(e);
		}
		
	}
	
	@Override
	public List<Score> getHighscore(Class<? extends Game> theGame) throws PersistencyException {
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("select * from "+resolveTableName(theGame));
			
			return PersistenceUtil.sort(decodeData(rs));
		} catch (SQLException e) {
			throw new PersistencyException(e);
		}
	}
	
	@Override
	public List<Class<? extends Game>> getSupportedGameTypes() {
		return gameList;
	}

	private String resolveTableName(Class<? extends Game> theGame) {
		AnnotatedGame anno = theGame.getAnnotation(AnnotatedGame.class);
		
		if (anno != null) {
			return PREFIX.concat(anno.highscorePersistentName());
		}
		else {
			return PREFIX.concat(theGame.getClass().getSimpleName());
		}
	}

	private String encodeData(Score score) {
		StringBuilder sb = new StringBuilder("'");
		sb.append(score.getPlayer().getName());
		sb.append("', ");
		sb.append(score.getThrownDarts());
		sb.append(", '");
		sb.append(new DateTime(score.getDateTime()).toString(isoDate));
		sb.append("', ");
		sb.append(score.getTotalTime());
		return sb.toString();
	}
	
	private List<PersistedScore> decodeData(ResultSet rs) throws SQLException {
		List<PersistedScore> map = new ArrayList<>();
		
		while (rs.next()) {
			PersistedScore score = new PersistedScore();
			for (ColumnWithType twt : X01_COLUMNS) {
				Object value = rs.getObject(twt.getName());
				
				switch (twt.getName()) {
				case PLAYER:
					score.setPlayer(new PlayerImpl(value.toString()));
					break;
				case DART_COUNT:
					score.setThrownDarts(Integer.parseInt(value.toString()));
					break;
				case TIME:
					score.setTime(isoDate.parseDateTime(value.toString()).toDate());
					break;
				case TOTAL_TIME:
					score.setTotalTime(Integer.parseInt(value.toString()));
				default:
					break;
				}
			}
			
			map.add(score);
		}
		
		return map;
	}
	
	@Override
	public void shutdown() {
		if (this.connection != null) {
			try {
				this.connection.close();
			} catch (SQLException e) {
				logger.warn(e.getMessage(), e);
			}
		}
	}

}
