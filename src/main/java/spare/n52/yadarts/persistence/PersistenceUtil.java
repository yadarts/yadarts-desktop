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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import spare.n52.yadarts.games.Score;

public class PersistenceUtil {

	public static List<Score> sort(List<PersistedScore> list) {
		Collections.sort(list);
		List<Score> result = new ArrayList<>();
		result.addAll(list);
		return result;
	}
	
}
