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

import java.sql.Date;

public class ColumnWithType {

	private String name;
	private Class<?> type;

	public ColumnWithType(String name, Class<?> type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return type;
	}

	public static ColumnWithType string(String string) {
		return new ColumnWithType(string, String.class);
	}
	
	public static ColumnWithType dateTime(String string) {
		return new ColumnWithType(string, Date.class);
	}
	
	public static ColumnWithType integer(String string) {
		return new ColumnWithType(string, Integer.class);
	}

	public String getSQLType() {
		return getType().getSimpleName().toLowerCase();
	}
	
}
