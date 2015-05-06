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
import java.util.Collections;
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
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import spare.n52.yadarts.MainWindow;
import spare.n52.yadarts.i18n.I18N;
import spare.n52.yadarts.layout.GameParameter.Bounds;

/**
 * A sophisticated composite class that dynamically creates
 * UI input fields based on the required parameters of the
 * available games (see {@link GameView.AvailableGames})
 */
public class NewGameView extends Composite {

	private static final RGB LIGHT_GRAY = new RGB(220, 220, 220);
	private Combo comboDropDown;
	private List<GameView> availableGames;
	private Composite gameSpecificAreaStack;
	private StackLayout gameSpecificAreaStackLayout = new StackLayout();
	private List<GameLayout> gameLayouts = new ArrayList<>();
	protected GameLayout currentLayout;
	private MainWindow mainWindow;

	public NewGameView(Composite parent, int style, MainWindow mw) {
		super(parent, style);
		availableGames = GameView.AvailableGames.get();
		this.mainWindow = mw;
		createContents();
		this.pack();
		this.layout(true, true);
	}


	protected void createContents() {
		
		GridLayout rl = new GridLayout(1, true);
		this.setLayout(rl);

		/*
		 * which game?
		 */
		Composite comboDropDownRow = new Composite(this, SWT.NONE);
		comboDropDownRow.setLayout(new RowLayout(SWT.HORIZONTAL));
		comboDropDownRow.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		
		ColoredLabel.create(comboDropDownRow, SWT.NONE, LIGHT_GRAY).setText(I18N.getString("selectGame").concat(":"));
		
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
		
		gameSpecificAreaStack = new Composite(this, SWT.BORDER);
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
		Button okButton = new Button(this, SWT.PUSH);
		okButton.setText(I18N.getString("start"));
		okButton.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false));
		okButton.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				List<GameParameter<?>> params = currentLayout.computeFilledParameters();
				Map<GameView, List<GameParameter<?>>> theNewGame = Collections.singletonMap(currentLayout.game, params);
				
				if (theNewGame != null && !theNewGame.isEmpty()) {

					for (GameView gv : theNewGame.keySet()) {
						mainWindow.createGameView(gv, theNewGame.get(gv));
					}

				}	
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

	/**
	 * This class creates a UI for paramter input
	 * based on the defined {@link GameParameter}s of the
	 * {@link GameView}.
	 */
	private static class GameLayout extends Composite {

		private GameView game;
		private Map<GameParameter<?>, Stack<Text>> parameterInputs = new HashMap<>();
		private ScrolledComposite scrollView;

		public GameLayout(GameView gv, Composite parent) {
			super(parent, SWT.NONE);
			this.game = gv;
			
			this.setLayout(new FillLayout());

			/*
			 * this scrollView is updated when the UI changes
			 * dynamically
			 */
			scrollView = new ScrolledComposite(this, SWT.H_SCROLL
					| SWT.V_SCROLL | SWT.BORDER);
			scrollView.setExpandHorizontal(true);
			scrollView.setExpandVertical(true);
			
			/*
			 * the contentContainer is the root Composite of this game view.
			 * it is a child of scrollView to enable scrolling.
			 */
			final Composite contentContainer = new Composite(scrollView, SWT.NONE);
			scrollView.setContent(contentContainer);

			GridLayout layout = new GridLayout();
			layout.numColumns = 1;
			contentContainer.setLayout(layout);
			
			/*
			 * define a nice headline
			 */
			ColoredLabel.create(contentContainer, SWT.NONE, LIGHT_GRAY).setText(I18N.getString("newGame").concat(": ").concat(this.game.getGameName()));
			
			for (final GameParameter<?> param : this.game.getInputParameters()) {
				createParameterView(param, contentContainer);
			}
			
			/*
			 * initially compute the min size so the scroll bars are rendered
			 * when required
			 */
			scrollView.setMinSize(contentContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		}

		/**
		 * creates an input area based on the provided {@link GameParameter} and
		 * its {@link Bounds}.
		 * 
		 * @param param the input parameter
		 * @param contentContainer the parent Composite
		 */
		private void createParameterView(final GameParameter<?> param, final Composite contentContainer) {
			if (param.getBounds().getMin() != param.getBounds().getMax()) {
				createParameterViewWithSpinner(param, contentContainer);
			}
			else {
				createStaticParameterView(param, contentContainer);
			}
		}

		/**
		 * creates a parameter input view where the bounds are 
		 * static. No dynamic including of additional fields
		 * is required here. 
		 * 
		 * @param param the game parameter
		 * @param contentContainer the parent Composite
		 */
		private void createStaticParameterView(final GameParameter<?> param,
				final Composite contentContainer) {
			ColoredLabel.create(contentContainer, SWT.NONE, LIGHT_GRAY).setText(I18N.getString(param.getName()));
			
			parameterInputs.put(param, new Stack<Text>());

			final Composite parameterList = new Composite(contentContainer, SWT.NONE);
			parameterList.setLayout(new GridLayout(1, true));
			parameterList.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			
			for (int i = 0; i < param.getBounds().getMax(); i++) {
				addParameterInput(parameterList, param);	
			}
			
		}

		/**
		 * creates a parameter input view where the bounds are 
		 * dynamic. A spinner is used to define the amount
		 * of input fields. 
		 * 
		 * @param param the game parameter
		 * @param contentContainer the parent Composite
		 */
		private void createParameterViewWithSpinner(final GameParameter<?> param,
				final Composite contentContainer) {
			parameterInputs.put(param, new Stack<Text>());
			
			Composite spinnerContainer = new Composite(contentContainer, SWT.NONE);
			spinnerContainer.setLayout(new RowLayout());
			ColoredLabel.create(spinnerContainer, SWT.NONE, LIGHT_GRAY).setText(I18N.getString(param.getName()));
			
			final Spinner spinner = new Spinner(spinnerContainer, SWT.READ_ONLY);
			spinner.setMaximum(param.getBounds().getMax());
			spinner.setMinimum(param.getBounds().getMin());
			
			final Composite parameterList = new Composite(contentContainer, SWT.NONE);
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

					/*
					 * updated the size of the scrollView as the contents
					 * have changed!
					 */
					scrollView.setMinSize(contentContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
					contentContainer.layout();
				}
			});
			
			for (int i = 0; i < param.getBounds().getMin(); i++) {
				addParameterInput(parameterList, param);	
			}
		}

		/**
		 * add a single input field and store it in {@link #parameterInputs}
		 * 
		 * @param parameterListContainer the parent
		 * @param param the defining game parameter
		 */
		protected void addParameterInput(Composite parameterListContainer, GameParameter<?> param) {
			Text textInput = new Text(parameterListContainer, SWT.BORDER);
			textInput.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			textInput.setSize(120, 32);
			this.parameterInputs.get(param).push(textInput);
		}

		/**
		 * remove the last input field that has been added (both from the view
		 * and {@link #parameterInputs})
		 * 
		 * @param parameterListContainer the parent
		 * @param param the defining game parameter
		 */
		protected void removeLastParameterInput(Composite parameterListContainer, GameParameter<?> param) {
			Text toRemove = this.parameterInputs.get(param).pop();
			toRemove.dispose();
		}

		/**
		 * @return the filled list of {@link GameParameter}s. the contents
		 * are read from the UI input fields
		 */
		public List<GameParameter<?>> computeFilledParameters() {
			List<GameParameter<?>> list = new ArrayList<>();
			
			for (GameParameter<?> gameParameter : this.parameterInputs.keySet()) {
				list.add(computeParameterValues(gameParameter, this.parameterInputs.get(gameParameter)));
			}
			
			return list;
		}

		private GameParameter<?> computeParameterValues(
				GameParameter<?> gameParameter, Stack<Text> inputFields) {
			/*
			 * TODO implement other datatypes. currently only String
			 */
			
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
