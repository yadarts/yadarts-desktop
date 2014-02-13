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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import spare.n52.yadarts.persistence.PersistencyException;

public class SQLitePersistence extends AbstractJDBCPersistence {

	public SQLitePersistence() throws PersistencyException {
		super();
	}

	@Override
	protected Connection createConnection() throws SQLException, IOException {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			throw new SQLException("Could not find the SQLite driver.");
		}
		
		File f = resolveFile();
		return DriverManager.getConnection("jdbc:sqlite:".concat(f
				.getAbsolutePath()));
	}

	protected File resolveFile() throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(".");
		sb.append(File.separator);
		sb.append(SQLiteConstants.DATA_BASE_DIRECTORY);
		sb.append(File.separator);
		sb.append(SQLiteConstants.HIGHSORE_FILE);

		File result = new File(sb.toString());

		if (result.exists() && !result.canWrite()) {
			throw new IOException("Could not open the DB file: "
					+ result.getAbsoluteFile());
		} else if (!result.exists()) {
			if (!result.createNewFile()) {
				throw new IOException("Could create the DB file: "
						+ result.getAbsoluteFile());
			}

		}

		return result;
	}
	
}
