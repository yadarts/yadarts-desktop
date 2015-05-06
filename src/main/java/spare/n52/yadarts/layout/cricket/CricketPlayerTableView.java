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
package spare.n52.yadarts.layout.cricket;

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
import spare.n52.yadarts.games.cricket.CricketGame;
import spare.n52.yadarts.games.cricket.CricketScore;

public class CricketPlayerTableView extends Composite {

	private List<Player> players;
	private Table table;

	private Font defaultFont;
	private Font highlightFont;
	private Font highlightFontBigger;
	private TableItem headerItem;
	private TableItem scoreItem;

	public CricketPlayerTableView(Composite parent, int style, List<Player> players,
			int targetScore) {
		super(parent, style);

		this.players = players;

		this.setLayout(new GridLayout());
//		this.setBackgroundMode(SWT.INHERIT_FORCE);
//		
//		if (parent.getBackgroundImage() != null) {
//			this.setBackgroundImage(parent.getBackgroundImage());
//		}

		this.defaultFont = new Font(getDisplay(), "Arial", 14, SWT.NONE);
		this.highlightFont = new Font(getDisplay(), "Arial", 14, SWT.BOLD);
		this.highlightFontBigger = new Font(getDisplay(), "Arial", 16, SWT.BOLD);

		initTable();
//		this.pack();
//		this.layout(true, true);
		
		setCurrentPlayer(players.get(0));
		
		this.layout(true);
	}

	private void initTable() {
        table = new Table(this, SWT.NO_FOCUS | SWT.HIDE_SELECTION );
//        table.setBackgroundMode(SWT.INHERIT_FORCE);
//        table.setBackgroundImage(this.getBackgroundImage());
        table.setLinesVisible(false);
        table.setHeaderVisible(false);
        table.addSelectionListener(new SelectionAdapter() {
        	
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		table.deselectAll();
        	}
        	
		});
        
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
        table.setLayoutData(data);

		TableColumn numberCol = new TableColumn(table, SWT.LEFT);
		numberCol.setAlignment(SWT.LEFT);
		numberCol.setMoveable(false);
		numberCol.setResizable(false);
		
		int playerCount = players.size();
		for (int i = 0; i < playerCount; i++) {
			TableColumn pCol = new TableColumn(table, SWT.LEFT);
			pCol.setAlignment(SWT.LEFT);
			pCol.setMoveable(false);
			pCol.setResizable(false);
		}

		headerItem = new TableItem(table, SWT.BOLD);
		headerItem.setText(createTableHeader());
		headerItem.setFont(highlightFontBigger);
		
		for (int p : CricketGame.VALID_NUMBERS) {
			TableItem ti = new TableItem(table, SWT.LEFT | SWT.NONE);
			ti.setFont(defaultFont);
			ti.setFont(0, highlightFont);
			ti.setText(createRow(p, playerCount));
		}
		
		scoreItem = new TableItem(table, SWT.BOLD);
		scoreItem.setText(createScoreRow(1000));
		scoreItem.setFont(highlightFont);

		table.getColumn(0).pack();
		
		for (int i = 0; i < players.size(); i++) {
			table.getColumn(i+1).pack();
		}
		
		
//		table.pack();
//		table.layout();
		
		headerItem.setFont(defaultFont);
		scoreItem.setText(createScoreRow(0));
	}


	private String[] createScoreRow(int n) {
		int playerCount = players.size();
		String[] result = new String[playerCount+1];
		result[0] = "Total";
		
		for (int i = 0; i < playerCount; i++) {
			result[1+i] = Integer.toString(n);
		}
		
		return result;
	}

	private String[] createTableHeader() {
		int playerCount = players.size();
		String[] result = new String[playerCount+1];
		result[0] = "";
		
		for (int i = 0; i < playerCount; i++) {
			result[1+i] = createName(players.get(i));
		}
		
		return result;
	}

	private String[] createRow(int p, int playerCount) {
		String[] result = new String[playerCount+1];
		result[0] = Integer.toString(p);
		
		for (int i = 0; i < playerCount; i++) {
			result[1+i] = "0";
		}
		
		return result;
	}

	private String createName(Player p) {
		StringBuilder sb = new StringBuilder();
		String n = p.getName();
		for (char c : n.toCharArray()) {
			sb.append(c);
			sb.append(System.getProperty("line.separator"));
		}
		return sb.toString();
	}

	public void setCurrentPlayer(final Player p) {
		getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				int index = players.indexOf(p);

				for (int i = 0; i < players.size(); i++) {
					
					if (i == index) {
						headerItem.setFont(i+1, highlightFont);
					} else {
						headerItem.setFont(i+1, defaultFont);
					}
					
				}

				table.update();
				table.redraw();
			}
		});
	}

	public void setRemainingScore(final Player p, final Score remaining) {
		final int index = players.indexOf(p);
		getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (remaining instanceof CricketScore) {
					CricketScore cs = (CricketScore) remaining;
					
					int currentRow = 1;
					for (int number : CricketGame.VALID_NUMBERS) {
						int rem = 3 - cs.getRemainingClosedSlots(number);
						TableItem item = table.getItem(currentRow);
						item.setText(index+1, Integer.toString(rem));
						
						currentRow++;
					}
					
					TableItem item = table.getItems()[table.getItemCount()-1];
					item.setText(index+1, Integer.toString(cs.getTotalScore()));
					
					table.update();
					table.redraw();					
				}

			}
		});
	}

}
