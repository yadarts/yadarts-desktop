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

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import spare.n52.yadarts.entity.Player;
import spare.n52.yadarts.games.Score;
import spare.n52.yadarts.i18n.I18N;

public class PlayerTableView extends Composite {

	private List<Player> players;
	private Table table;

	private Font defaultFont;
	private Font highlightFont;
	private Font highlightFontBigger;

	public PlayerTableView(Composite parent, int style, List<Player> players,
			int targetScore) {
		super(parent, style);

		this.players = players;

		this.setLayout(new GridLayout());
		this.setBackgroundMode(SWT.INHERIT_FORCE);
		this.setBackground(parent.getBackground());

		this.defaultFont = new Font(getDisplay(), "Arial", 14, SWT.NONE);
		this.highlightFont = new Font(getDisplay(), "Arial", 14, SWT.BOLD);
		this.highlightFontBigger = new Font(getDisplay(), "Arial", 16, SWT.BOLD);

		initTable(Integer.toString(targetScore));
	}

	private void initTable(String targetScore) {
        table = new Table(this, SWT.NO_FOCUS | SWT.HIDE_SELECTION );
        table.setBackgroundMode(SWT.INHERIT_FORCE);
        table.setBackground(this.getBackground());
        table.setLinesVisible(false);
        table.setHeaderVisible(false);
        table.addSelectionListener(new SelectionAdapter() {
        	
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		table.deselectAll();
        	}
        	
		});
        
        GridData data = new GridData(SWT.LEFT, SWT.FILL, true, false);
        table.setLayoutData(data);

		TableColumn nameRow = new TableColumn(table, SWT.LEFT);
		nameRow.setAlignment(SWT.LEFT);
		nameRow.setMoveable(false);
		nameRow.setResizable(false);

		TableColumn scoreRow = new TableColumn(table, SWT.RIGHT);
		scoreRow.setAlignment(SWT.RIGHT);
		scoreRow.setMoveable(false);
		scoreRow.setResizable(false);
		
		TableColumn dartsRow = new TableColumn(table, SWT.CENTER);
		dartsRow.setAlignment(SWT.RIGHT);
		dartsRow.setMoveable(false);
		dartsRow.setResizable(false);

		new TableItem(table, SWT.BOLD).setText(new String[]{
				"", I18N.getString("currentScore"), I18N.getString("thrownDarts")
		});
		
		TableItem ti;
		for (Player p : players) {
			ti = new TableItem(table, SWT.LEFT | SWT.NONE);
			ti.setFont(highlightFontBigger);
			ti.setText(new String[] { p.getName(), targetScore });
		}

		table.getColumn(0).pack();
		table.getColumn(1).pack();
		table.getColumn(2).pack();		
		table.pack();
	}

	public void setCurrentPlayer(final Player p, final Score remaining) {
		getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				int index = players.indexOf(p);

				for (int i = 0; i < players.size(); i++) {
					TableItem item = table.getItem(i+1);

					if (i == index) {
						item.setFont(highlightFont);
						item.setText(new String[] {p.getName(), Integer.toString(remaining.getTotalScore()),
								Integer.toString(remaining.getThrownDarts())});
					} else {
						item.setFont(defaultFont);
					}
					
				}

//				table.layout();
//				table.pack();
			}
		});
	}

	public void setRemainingScore(final Player p, final Score remaining) {
		getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				int index = players.indexOf(p);

				TableItem item = table.getItem(index+1);

				item.setText(new String[] {p.getName(), Integer.toString(remaining.getTotalScore()),
						Integer.toString(remaining.getThrownDarts())});
				
//				table.layout();
//				table.pack();
			}
		});
	}

}
