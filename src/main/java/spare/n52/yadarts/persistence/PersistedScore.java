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

import java.util.Date;

import spare.n52.yadarts.entity.Player;
import spare.n52.yadarts.games.Score;
import spare.n52.yadarts.games.Turn;

public class PersistedScore implements Score, Comparable<PersistedScore> {

	private int totalScore;
	private int thrownDarts;
	private Date time;
	private int totalTime;
	private Player player;
	
	public int getTotalScore() {
		return totalScore;
	}
	
	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}
	
	public int getThrownDarts() {
		return thrownDarts;
	}
	
	public void setThrownDarts(int thrownDarts) {
		this.thrownDarts = thrownDarts;
	}

	public void setTime(Date parseDateTime) {
		this.time = parseDateTime;
	}

	@Override
	public Date getDateTime() {
		return time;
	}

	public void setTotalTime(int parseInt) {
		this.totalTime = parseInt;
	}

	@Override
	public int getTotalTime() {
		return totalTime;
	}

	@Override
	public int compareTo(PersistedScore o) {
		if (this.thrownDarts < o.thrownDarts) {
			return -1;
		}
		
		if (this.thrownDarts > o.thrownDarts) {
			return 1;
		}
		
		if (this.thrownDarts == o.thrownDarts) {
			if (this.time.before(o.time)) {
				return -1;
			}
			if (this.time.equals(o.time)) {
				return 0;
			}
			return 1;
		}
		
		return 0;
	}
	
	public Player getPlayer() {
		return this.player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	@Override
	public boolean turnHasEvents() {
		return true;
	}

	@Override
	public void terminateLastTurn() {
	}

	@Override
	public boolean lastTurnTerminatedCorrect() {
		return true;
	}

        @Override
        public Turn getLastTurn() {
            return new Turn() {
                @Override
                public boolean isBusted() {
                    return false;
                }
            };
        }
	
}
