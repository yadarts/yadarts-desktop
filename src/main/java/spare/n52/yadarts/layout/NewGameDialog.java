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
import java.util.Stack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
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
	private ScrolledComposite sc;

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
		shell.setSize(400, 300);
		
		FormLayout gl = new FormLayout();
		gl.spacing = 5;
		shell.setLayout(gl);

		/*
		 * which game?
		 */
		Composite comboDropDownRow = new Composite(shell, SWT.NONE);
		comboDropDownRow.setLayout(new RowLayout(SWT.HORIZONTAL));
		FormData fd = new FormData();
		fd.top = new FormAttachment(0);
		fd.left = new FormAttachment(0);
		comboDropDownRow.setLayoutData(fd);
		
		new Label(comboDropDownRow, SWT.NONE).setText(I18N.getString("selectGame").concat(":"));
		
		comboDropDown = new Combo(comboDropDownRow, SWT.READ_ONLY);
		comboDropDown.setItems(createItems());
		comboDropDown.addSelectionListener(new DropdownSelectionListener());
		
		/*
		 * stack layout for each game area
		 */
		
		gameSpecificArea = new Composite(shell, SWT.BORDER);
		fd = new FormData();
		fd.top = new FormAttachment(comboDropDownRow);
		fd.left = new FormAttachment(0);
		fd.right = new FormAttachment(100);
		fd.bottom = new FormAttachment(90);
		
		gameSpecificArea.setLayoutData(fd);
		gameSpecificArea.setLayout(gameSpecificAreaLayout);
		
		Composite empty = new Composite(gameSpecificArea, SWT.NONE);
		empty.setLayout(new FillLayout());
		empty.pack();
		
		/*
		 * create an area for each available game
		 */
		for (GameView gv : availableGames) {
			gameLayouts.add(createGameLayout(gv));
		}
		
		gameSpecificAreaLayout.topControl = empty;
		
		/*
		 * ok button
		 */
		Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText(I18N.getString("start"));
		fd = new FormData();
		fd.top = new FormAttachment(gameSpecificArea);
		fd.left = new FormAttachment(60);
		fd.right = new FormAttachment(100);
		fd.bottom = new FormAttachment(100);
		okButton.setLayoutData(fd);
		okButton.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				System.out.println("ok");
			}
			
		});
		
		shell.open();
	}

	private Composite createGameLayout(GameView gv) {
		Composite container = new Composite(gameSpecificArea, SWT.NONE);
		container.setBackground(new Color(getParent().getDisplay(), new RGB(255, 0, 0)));
		container.setLayout(new FormLayout());
		
		Label label = new Label(container, SWT.NONE);
		label.setText(I18N.getString("newGame") +": "+ gv.getGameName());
		
		this.result = gv.getInputParameters();
		createInputFields(container, this.result, label);
		
		container.pack();
		
		return container;
	}

	private void createInputFields(final Composite parent,
			List<GameParameter<?>> inputParameters, Label topAligningComponent) {
		sc = new ScrolledComposite(parent, SWT.V_SCROLL);
		sc.setBackground(new Color(getParent().getDisplay(), new RGB(0, 0, 255)));
		FormData fd = new FormData();
		fd.top = new FormAttachment(topAligningComponent);
		fd.left = new FormAttachment(0);
		fd.right = new FormAttachment(100);
		fd.bottom = new FormAttachment(100);
		sc.setLayoutData(fd);
		sc.setLayout(new GridLayout(1, true));
		
		final Composite container = new Composite(sc, SWT.NONE);
		container.setLayout(new RowLayout(SWT.VERTICAL));
		container.setBackground(new Color(getParent().getDisplay(), new RGB(0, 255, 0)));
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		container.setLayoutData(gd);
		
		sc.setContent(container);

		for (GameParameter<?> gameParameter : inputParameters) {
			if (gameParameter.getBounds().getMax() > 1) {
				
				final Stack<Text> currentParameterValue = new Stack<>();
				
				/*
				 * the bounds spinner for this parameter
				 */
				final Composite inputField = new Composite(container, SWT.NONE);
				inputField.setLayout(new RowLayout());
				new Label(inputField, SWT.NONE).setText(I18N.getString("numberOf")+ " "+
							I18N.getString(gameParameter.getName()));
				final Spinner boundsSpinner = new Spinner(inputField, SWT.READ_ONLY);
				boundsSpinner.setMinimum(gameParameter.getBounds().getMin());
				boundsSpinner.setMaximum(gameParameter.getBounds().getMax());

				new Label(container, SWT.NONE).setText(I18N.getString(gameParameter.getName()).concat(":"));
				
				final Composite parameterListContainer = new Composite(container, SWT.NONE);
				parameterListContainer.setLayout(new GridLayout(1, true));
				
				/*
				 * if the value of the spinner changes, add/remove entry fields
				 */
				boundsSpinner.addModifyListener(new ModifyListener() {
					
					@Override
					public void modifyText(ModifyEvent e) {
						int newCount = Integer.parseInt(boundsSpinner.getText());
						
						if (currentParameterValue.size() < newCount) {
							addNewInputRow(parameterListContainer, currentParameterValue);
						}
						else {
							removeLastInputRow(parameterListContainer, currentParameterValue);
						}
					}

				});
				
				for (int i = 0; i < gameParameter.getBounds().getMin(); i++) {
					addNewInputRow(parameterListContainer, currentParameterValue);
				}
				
			}
		}
		
		sc.setMinSize(container.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		container.pack();
	}
	
	private void removeLastInputRow(Composite parent, Stack<Text> currentParameterValue) {
		currentParameterValue.pop().dispose();
		parent.pack();
	}

	private void addNewInputRow(Composite parent, Stack<Text> currentParameterValue) {
		Text in = new Text(parent, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData(280, SWT.DEFAULT);

		in.setLayoutData(gd);
		in.setSize(280, 32);
		currentParameterValue.add(in);
		parent.pack();
		sc.pack();
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
