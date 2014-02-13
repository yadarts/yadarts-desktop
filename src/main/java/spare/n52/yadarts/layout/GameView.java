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

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.eclipse.swt.widgets.Composite;

import spare.n52.yadarts.entity.Player;

public interface GameView {

	public List<GameParameter> getInputParameters();
	
	public String getGameName();
	
	void initialize(Composite parent, int style, List<Player> playerList,
			int targetScore);
	
	public static class AvailableGames {
		
		private static List<GameView> list;

		public static synchronized List<GameView> get() {
			if (list == null) {
				list = new ArrayList<>();
				
				ServiceLoader<GameView> l = ServiceLoader.load(GameView.class);
				
				for (GameView gameView : l) {
					list.add(gameView);
				}
			}
			
			return list;
		}
		
	}
	
}
