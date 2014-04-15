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
package spare.n52.yadarts.sound;

public enum SoundId {
	
	Triple,Double,Single,BullsEye,y1,y2,y3,y4,y5,y6,y7,y8,y9,y10,y11,y12,y13,y14,y15,y16,y17,y18,y19,y20,y25,
	Bust, Missed, RemoveDarts, Hit, Praise_low, Praise_high, BounceOut, PleasePressNextPlayer, None, Lower_Classic, Upper_Classic, DefaultPlayer, PleaseThrowDarts, IsTheWinner, 
	Eike, Benjamin, Jan, Matthes, Dustin, Simon, Christian, Albert, Andreas, Conny, Ann, Henning, Daniel, Carsten, Holger;
	
	static SoundId get(final int i) {
		switch (i) {
		case 1:
			return y1;
		case 2:
			return y2;
		case 3:
			return y3;
		case 4:
			return y4;
		case 5:
			return y5;
		case 6:
			return y6;
		case 7:
			return y7;
		case 8:
			return y8;
		case 9:
			return y9;
		case 10:
			return y10;
		case 11:
			return y11;
		case 12:
			return y12;
		case 13:
			return y13;
		case 14:
			return y14;
		case 15:
			return y15;
		case 16:
			return y16;
		case 17:
			return y17;
		case 18:
			return y18;
		case 19:
			return y19;
		case 20:
			return y20;
		case 25:
			return BullsEye;
		default:
			return None;
		}
	}
	
	static SoundId get(final String name) {
		
		switch(name){
		case "Eike":
			return Eike;
		case "Benjamin":
			return Benjamin;
		case "Matthes":
			return Matthes;
		case "Jan":
			return Jan;
		case "Dustin":
			return Dustin;
		default:
			return DefaultPlayer;
		}
	}

}
