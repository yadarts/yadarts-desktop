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
package spare.n52.yadarts.layout.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import spare.n52.yadarts.MainWindow;
import spare.n52.yadarts.layout.ImageControl;
import spare.n52.yadarts.layout.home.MenuImage;

public class CustomMenu extends Composite {

	public CustomMenu(Composite parent, int style, final MainWindow mw) {
		super(parent, style);
		
		RowLayout rl = new RowLayout();
		rl.marginWidth = 5;
		rl.spacing = 10;
		this.setLayout(rl);
		
		ImageControl newGame = new ImageControl(this, SWT.NONE);
		newGame.setImage(new MenuImage("new_game", getDisplay(), 25));
		newGame.setClickListener(new ImageControl.ClickListener() {
			
			public void onClickEvent() {
				mw.createNewGameView();
			}
		});
		
		ImageControl highscore = new ImageControl(this, SWT.NONE);
		highscore.setImage(new MenuImage("highscore", getDisplay(), 25));
		highscore.setClickListener(new ImageControl.ClickListener() {
			
			public void onClickEvent() {
				mw.createHighscoreView();;
			}
		});
		
		ImageControl exit = new ImageControl(this, SWT.NONE);
		exit.setImage(new MenuImage("exit", getDisplay(), 25));
		exit.setClickListener(new ImageControl.ClickListener() {
			
			public void onClickEvent() {
				mw.exit();
			}
		});
		
		this.layout();
		
	}
	
	
}
