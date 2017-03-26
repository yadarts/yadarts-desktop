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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;

import spare.n52.yadarts.MainWindow;
import spare.n52.yadarts.layout.ImageControl;
import spare.n52.yadarts.layout.home.MenuImage;

public class CustomMenu extends Composite {

	public CustomMenu(Composite parent, int style, final MainWindow mw) {
		super(parent, style);
		
		GridLayout gl = new GridLayout(1, true);
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		this.setLayout(gl);

		Composite hLine = new Composite(this, SWT.NONE);
		hLine.setBackground(new Color(getDisplay(), new RGB(0, 0, 0)));
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.heightHint = 2;
		gd.horizontalAlignment = SWT.FILL;
		hLine.setLayoutData(gd);
		
		Composite wrapper = new Composite(this, SWT.NONE);
		GridData gd2 = new GridData();
		gd2.horizontalAlignment = SWT.FILL;
		gd2.grabExcessHorizontalSpace = true;
		
		RowLayout rl = new RowLayout();
		rl.marginWidth = 5;
		rl.spacing = 10;
		wrapper.setLayout(rl);

		ImageControl home = new ImageControl(wrapper, SWT.NONE);
		home.setImage(new MenuImage("home", getDisplay(), 25));
		home.setClickListener(new ImageControl.ClickListener() {
			
			public void onClickEvent() {
				mw.createWelcomePanel();
			}
		});
                
		ImageControl newGame = new ImageControl(wrapper, SWT.NONE);
		newGame.setImage(new MenuImage("new_game", getDisplay(), 25));
		newGame.setClickListener(new ImageControl.ClickListener() {
			
			public void onClickEvent() {
				mw.createNewGameView();
			}
		});
		
		ImageControl highscore = new ImageControl(wrapper, SWT.NONE);
		highscore.setImage(new MenuImage("highscore", getDisplay(), 25));
		highscore.setClickListener(new ImageControl.ClickListener() {
			
			public void onClickEvent() {
				mw.createHighscoreView();;
			}
		});
		
		ImageControl exit = new ImageControl(wrapper, SWT.NONE);
		exit.setImage(new MenuImage("exit", getDisplay(), 25));
		exit.setClickListener(new ImageControl.ClickListener() {
			
			public void onClickEvent() {
				mw.exit();
			}
		});
		
		this.layout();
		
	}
	
	
}
