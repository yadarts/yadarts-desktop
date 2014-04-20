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

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import spare.n52.yadarts.common.Services;
import spare.n52.yadarts.games.AnnotatedGame;
import spare.n52.yadarts.games.Game;
import spare.n52.yadarts.games.Score;
import spare.n52.yadarts.i18n.I18N;
import spare.n52.yadarts.persistence.HighscorePersistence;
import spare.n52.yadarts.persistence.PersistencyException;

/**
 * very basic highscore view using a classic {@link Table}
 * view.
 */
public class HighscoreView extends Composite {

	private Map<String, Class<? extends Game>> supportedGames = new HashMap<>();

	public HighscoreView(Composite parent, int style) {
		super(parent, style);
		GridLayout gl = new GridLayout(1, true);
		gl.verticalSpacing = 10;
		gl.marginLeft = 10;
		gl.marginRight = 10;
		this.setLayout(gl);
		
		List<Class<? extends Game>> persistedGames = Services.getImplementation(HighscorePersistence.class).getSupportedGameTypes();
		
		for (Class<? extends Game> c : persistedGames) {
			AnnotatedGame anno = c.getAnnotation(AnnotatedGame.class);
			
			if (anno != null) {
				supportedGames.put(anno.displayName(), c);
			}
			else {
				supportedGames.put(c.getSimpleName(), c);
			}
		}
		
		createLayout();
		this.pack();
		this.setSize(parent.getSize());
		this.layout();
	}

	private void createLayout() {
		Composite titleBar = new Composite(this, SWT.NONE);
		titleBar.setLayout(new RowLayout(SWT.HORIZONTAL));
		new Label(titleBar, SWT.NONE).setText(I18N.getString("chooseHighscore").concat(":"));
		
		final Combo gameChoosers = new Combo(titleBar, SWT.READ_ONLY);
		titleBar.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		
		final String[] strings = new String[supportedGames.size()];
		gameChoosers.setItems((String[]) supportedGames.keySet().toArray(strings));
		
		
		final Composite highscoreContainer = new Composite(this, SWT.NONE);
		highscoreContainer.setLayout(new FillLayout());
		highscoreContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		gameChoosers.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				int index = gameChoosers.getSelectionIndex();
				Class<? extends Game> c = supportedGames.get(strings[index]);
				
				createHighscoreTable(highscoreContainer, c);
			}
			
		});
		
	}

	protected void createHighscoreTable(Composite highscoreContainer,
			Class<? extends Game> c) {
		for (Control control : highscoreContainer.getChildren()) {
			control.dispose();
		}
		
		List<Score> scores;
		try {
			scores = Services.getImplementation(HighscorePersistence.class).getHighscore(c);
		} catch (PersistencyException e) {
			return;
		}
		
		Table table = new Table (highscoreContainer, SWT.NONE | SWT.BORDER);
		table.setLinesVisible (true);
		table.setHeaderVisible (true);
		String[] titles = {I18N.getString("name"),
				I18N.getString("thrownDarts"),
				I18N.getString("date"),
				I18N.getString("timeInSeconds")};
		for (int i=0; i<titles.length; i++) {
			TableColumn column = new TableColumn (table, SWT.NONE);
			column.setText (titles [i]);
		}	
		
		SimpleDateFormat sdf = new SimpleDateFormat();
		for (Score sc : scores) {
			TableItem item = new TableItem (table, SWT.NONE);
			item.setText(0, sc.getPlayer().getName());
			item.setText(1, Integer.toString(sc.getThrownDarts()));
			item.setText(2, sdf.format(sc.getDateTime()));
			item.setText(3, Integer.toString(sc.getTotalTime()));
		}
		
		for (int i=0; i<titles.length; i++) {
			table.getColumn (i).pack ();
		}
		
		this.pack();
		
		this.setSize(getParent().getSize());
		this.layout();
	}

}
