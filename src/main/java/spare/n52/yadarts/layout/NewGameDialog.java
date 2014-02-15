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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
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
	private Composite gameSpecificAreaStack;
	private StackLayout gameSpecificAreaStackLayout = new StackLayout();
	private List<GameParameter<?>> result;
	private List<GameLayout> gameLayouts = new ArrayList<>();
	protected GameLayout currentLayout;

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
		
		GridLayout rl = new GridLayout(1, true);
		shell.setLayout(rl);

		/*
		 * which game?
		 */
		Composite comboDropDownRow = new Composite(shell, SWT.NONE);
		comboDropDownRow.setLayout(new RowLayout(SWT.HORIZONTAL));
		comboDropDownRow.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		
		new Label(comboDropDownRow, SWT.NONE).setText(I18N.getString("selectGame").concat(":"));
		
		comboDropDown = new Combo(comboDropDownRow, SWT.READ_ONLY);
		comboDropDown.setItems(createItems());
		comboDropDown.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				currentLayout = gameLayouts.get(comboDropDown.getSelectionIndex());
				gameSpecificAreaStackLayout.topControl = currentLayout;
				gameSpecificAreaStack.layout();
			}
			
		});
		
		/*
		 * stack layout for each game area
		 */
		
		gameSpecificAreaStack = new Composite(shell, SWT.BORDER);
		gameSpecificAreaStack.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		gameSpecificAreaStack.setLayout(gameSpecificAreaStackLayout);
		
		Composite empty = new Composite(gameSpecificAreaStack, SWT.NONE);
		empty.setLayout(new FillLayout());
		
		/*
		 * create an area for each available game
		 */
		for (GameView gv : availableGames) {
			gameLayouts.add(new GameLayout(gv, gameSpecificAreaStack));
		}
		
		gameSpecificAreaStackLayout.topControl = empty;
		
		/*
		 * ok button
		 */
		Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText(I18N.getString("start"));
		okButton.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false));
		okButton.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				result = currentLayout.computeFilledParameters();
			}
			
		});
	}


	private String[] createItems() {
		String[] array = new String[availableGames.size()];
		
		int i = 0;
		for (GameView gv : availableGames) {
			array[i++] = gv.getGameName();
		}
		
		return array;
	}

	public static class GameLayout extends Composite {

		private GameView game;
		private Map<GameParameter<?>, Stack<Text>> parameterInputs = new HashMap<>();

		public GameLayout(GameView gv, Composite parent) {
			super(parent, SWT.NONE);
			this.game = gv;
			
			this.setLayout(new FillLayout());

			// set the minimum width and height of the scrolled content - method 2
			final ScrolledComposite sc2 = new ScrolledComposite(this, SWT.H_SCROLL
					| SWT.V_SCROLL | SWT.BORDER);
			sc2.setExpandHorizontal(true);
			sc2.setExpandVertical(true);
			final Composite c2 = new Composite(sc2, SWT.NONE);
			sc2.setContent(c2);

			GridLayout layout = new GridLayout();
			layout.numColumns = 1;
			c2.setLayout(layout);
			
			new Label(c2, SWT.NONE).setText(I18N.getString("newGame").concat(": ").concat(this.game.getGameName()));
			
			for (final GameParameter<?> param : this.game.getInputParameters()) {
				parameterInputs.put(param, new Stack<Text>());
				
				final Spinner spinner = new Spinner(c2, SWT.READ_ONLY);
				spinner.setMaximum(param.getBounds().getMax());
				spinner.setMinimum(param.getBounds().getMin());
				
				final Composite parameterList = new Composite(c2, SWT.NONE);
				parameterList.setLayout(new GridLayout(1, true));
				parameterList.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
				
				spinner.addModifyListener(new ModifyListener() {
					@Override
					public void modifyText(ModifyEvent e) {
						int count = Integer.parseInt(spinner.getText());
						
						if (count > parameterInputs.get(param).size()) {
							/*
							 * change the stack top child. call 
							 * layout() on the stack's component
							 */
							addParameterInput(parameterList, param);	
						}
						else {
							removeLastParameterInput(parameterList, param);
						}
						

						// reset the minimum width and height so children can be seen -
						sc2.setMinSize(c2.computeSize(SWT.DEFAULT, SWT.DEFAULT));
						c2.layout();
					}
				});
			}
			
			// set the minimum width and height so children can be seen
			sc2.setMinSize(c2.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		}

		protected void addParameterInput(Composite parameterListContainer, GameParameter<?> param) {
			Text button = new Text(parameterListContainer, SWT.NONE);
			this.parameterInputs.get(param).push(button);
		}

		protected void removeLastParameterInput(Composite parameterListContainer, GameParameter<?> param) {
			Text toRemove = this.parameterInputs.get(param).pop();
			toRemove.dispose();
		}

		public List<GameParameter<?>> computeFilledParameters() {
			List<GameParameter<?>> list = new ArrayList<>();
			
			for (GameParameter<?> gameParameter : this.parameterInputs.keySet()) {
				list.add(computeParameterValues(gameParameter, this.parameterInputs.get(gameParameter)));
			}
			
			return list;
		}

		private GameParameter<?> computeParameterValues(
				GameParameter<?> gameParameter, Stack<Text> inputFields) {
			//TODO implement other datatypes. currently only String
			
			if (gameParameter.getType().isAssignableFrom(String.class)) {
				computeStringParameterValues(gameParameter, inputFields);	
			}
			
			return gameParameter;
		}

		protected void computeStringParameterValues(
				GameParameter<?> gameParameter, Stack<Text> inputFields) {
			GameParameter<String> sp = (GameParameter<String>) gameParameter;
			List<String> valueList = new ArrayList<>(inputFields.size());
			for (Text text : inputFields) {
				valueList.add(text.getText().trim());
			}
			
			sp.setValue(valueList);
		}
		
	}

}
