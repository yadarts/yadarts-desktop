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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class NewGameDialog extends Dialog {

	private Shell shell;
	private Object result;
	private Combo comboDropDown;
	private List<GameView> availableGames;
	private Composite gameSpecificArea;
	private StackLayout gameSpecificAreaLayout = new StackLayout();
	private List<Composite> gameLayouts = new ArrayList<>();

	public static NewGameDialog create(Shell shell) {
		return new NewGameDialog(shell);
	}

	public NewGameDialog(Shell parent) {
		super(parent);
		availableGames = GameView.AvailableGames.get();
	}

	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	protected void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText("New Game");
		
	    RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
	    rowLayout.spacing = 10;
	    rowLayout.marginWidth = 5;
	    rowLayout.marginHeight = 5;
	    
	    shell.setLayout(rowLayout);

		comboDropDown = new Combo(shell, SWT.READ_ONLY);
		comboDropDown.setItems(createItems());
		comboDropDown.addSelectionListener(new DropdownSelectionListener());
		
		gameSpecificArea = new Composite(shell, SWT.BORDER);
		gameSpecificArea.setLayout(gameSpecificAreaLayout);
		
		Composite empty = new Composite(gameSpecificArea, SWT.NONE);
		empty.setSize(640, 480);
		
		for (GameView gv : availableGames) {
			gameLayouts.add(createGameLayout(gv));
		}
		
		gameSpecificAreaLayout.topControl = empty;
		
		shell.open();
	}

	private Composite createGameLayout(GameView gv) {
		Composite container = new Composite(gameSpecificArea, SWT.NONE);
		
		new Label(container, SWT.NONE).setText(gv.getGameName());
		
		container.pack();
		return container;
	}

	private String[] createItems() {
		String[] array = new String[availableGames.size()];
		
		int i = 0;
		for (GameView gv : availableGames) {
			array[i++] = gv.getGameName();
		}
		
		return array;
	}

	public void updateGameSpecificArea(int selectionIndex) {
		gameSpecificAreaLayout.topControl = gameLayouts.get(selectionIndex);
		gameSpecificArea.layout();
	}
	
	class DropdownSelectionListener extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			super.widgetSelected(e);
			updateGameSpecificArea(comboDropDown.getSelectionIndex());
		}
		
	}


}
