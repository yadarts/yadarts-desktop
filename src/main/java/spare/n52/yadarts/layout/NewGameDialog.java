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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import spare.n52.yadarts.i18n.I18N;

public class NewGameDialog extends Dialog {

	private Shell shell;
	private Combo comboDropDown;
	private List<GameView> availableGames;
	private Composite gameSpecificArea;
	private StackLayout gameSpecificAreaLayout = new StackLayout();
	private List<Composite> gameLayouts = new ArrayList<>();
	private List<GameParameter<?>> result;

	public static NewGameDialog create(Shell shell) {
		return new NewGameDialog(shell);
	}

	public NewGameDialog(Shell parent) {
		super(parent);
		availableGames = GameView.AvailableGames.get();
	}

	public List<GameParameter<?>> open() {
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
		shell.setText(I18N.getString("newGame"));
		
	    shell.setLayout(new FillLayout());

		comboDropDown = new Combo(shell, SWT.READ_ONLY);
		comboDropDown.setItems(createItems());
		comboDropDown.addSelectionListener(new DropdownSelectionListener());
		
		gameSpecificArea = new Composite(shell, SWT.BORDER);
		gameSpecificArea.setLayout(gameSpecificAreaLayout);
		
		Composite empty = new Composite(gameSpecificArea, SWT.NONE);
		empty.setLayout(new FillLayout());
		empty.pack();
		
		for (GameView gv : availableGames) {
			gameLayouts.add(createGameLayout(gv));
		}
		
		gameSpecificAreaLayout.topControl = empty;
		
		shell.open();
	}

	private Composite createGameLayout(GameView gv) {
		Composite container = new Composite(gameSpecificArea, SWT.NONE);
		container.setLayout(new RowLayout(SWT.VERTICAL));
		
		new Label(container, SWT.NONE).setText(I18N.getString("newGame") +": "+ gv.getGameName());
		
		this.result = gv.getInputParameters();
		createInputFields(container, this.result);
		
		container.pack();
		return container;
	}

	private void createInputFields(final Composite container,
			List<GameParameter<?>> inputParameters) {
		for (GameParameter<?> gameParameter : inputParameters) {
			if (gameParameter.getBounds().getMax() > 1) {
				
				final List<Object> currentParameterValue = new ArrayList<>();
				
				final Composite inputField = new Composite(container, SWT.NONE);
				inputField.setLayout(new RowLayout(SWT.HORIZONTAL));
				new Label(inputField, SWT.NONE).setText(I18N.getString("numberOf")+ " "+
							I18N.getString(gameParameter.getName()));
				final Spinner boundsSpinner = new Spinner(inputField, SWT.NONE);
				boundsSpinner.setMinimum(gameParameter.getBounds().getMin());
				boundsSpinner.setMaximum(gameParameter.getBounds().getMax());
				boundsSpinner.addModifyListener(new ModifyListener() {
					
					@Override
					public void modifyText(ModifyEvent e) {
						int newCount = Integer.parseInt(boundsSpinner.getText());
						
						if (currentParameterValue.size() < newCount) {
							addNewInputRow(container);
						}
						else {
							removeLastInputRow();
						}
					}

					private void removeLastInputRow() {
						// TODO Auto-generated method stub
						
					}

					private void addNewInputRow(Composite parent) {
						Text in = new Text(parent, SWT.SINGLE);
						currentParameterValue.add(in);
						parent.layout();
						parent.pack();
						parent.redraw();
					}
					
				});
			}
			new Label(container, SWT.NONE).setText(I18N.getString(gameParameter.getName()));
		}
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
