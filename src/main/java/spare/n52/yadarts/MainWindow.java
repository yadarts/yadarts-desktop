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
package spare.n52.yadarts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spare.n52.yadarts.common.Services;
import spare.n52.yadarts.config.Configuration;
import spare.n52.yadarts.games.GameEventBus;
import spare.n52.yadarts.i18n.I18N;
import spare.n52.yadarts.layout.GameParameter;
import spare.n52.yadarts.layout.GameView;
import spare.n52.yadarts.layout.HighscoreView;
import spare.n52.yadarts.layout.NewGameView;
import spare.n52.yadarts.layout.home.WelcomeView;
import spare.n52.yadarts.layout.menu.CustomMenu;
import spare.n52.yadarts.themes.Theme;

public class MainWindow {
    

	private static final Logger logger = LoggerFactory
			.getLogger(MainWindow.class);
	
	private Shell shell;
	private boolean fullscreen;

	private Composite rootPanel;
	private Composite currentContentView;

	/**
	 * single map storing the current game and its defined parameters
	 */
	private Map<String, List<GameParameter<?>>> currentGame = new HashMap<>(1);

	private MenuItem restartGame;

	private Menu menuBar;

	private Composite rootWrapper;

	private Composite customMenu;

	public MainWindow(Display display, MainWindowOpenedListener l) {
		Configuration config = Services.getImplementation(Configuration.class);
		Theme.setCurrentTheme(config.getUITheme());
		
		shell = new Shell(display);
		shell.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		        event.doit = false;
		        shutdown();
		      }
		    });
		this.fullscreen = config.isAutoFullScreen();
		
		shell.setMinimumSize(800, 600);
		shell.setText("yadarts desktop edition");
		
		initLayout();

		appendKeyListeners();

		shell.open();
		
		resolvePriorWindowState();

		if (this.fullscreen) {
			this.fullscreen = false;
			switchFullscreenState();
		}

		l.onMainWindowOpened();

		logger.info("bootstrapping finished!");

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		shutdown();
	}

	private void shutdown() {
		try {
			shell.getDisplay().dispose();
		}
		catch (SWTException e) {
		}
		
		try {
			/*
			 * just to be sure: shutdown
			 */
			EventEngine.instance().shutdown();
			
			if (currentContentView != null) {
				currentContentView.dispose();
			}
			
			GameEventBus.instance().shutdown();
		} catch (InitializationException | RuntimeException e) {
			logger.warn(e.getMessage(), e);
		}
		
		Services.shutdownDisposables();
	}

	private void resolvePriorWindowState() {
		shell.setSize(1280, 720);
		
		Rectangle splashBounds = shell.getBounds();
		Rectangle displayBounds = shell.getDisplay().getPrimaryMonitor().getBounds();
		int x = (displayBounds.width - splashBounds.width) / 2;
		int y = (displayBounds.height - splashBounds.height) / 2;
		shell.setLocation(x, y);
	}

	protected void initLayout() {
//			shell.setBackgroundImage(Theme.getCurrentTheme().getBackground(shell.getDisplay()));
		shell.setBackgroundImage(new Image(shell.getDisplay(), getClass().getResourceAsStream("/images/background.jpg")));
		shell.setBackgroundMode(SWT.INHERIT_FORCE);
		
		FillLayout layout = new FillLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		shell.setLayout(layout);
		
		rootWrapper = new Composite(shell, SWT.INHERIT_FORCE);
		GridLayout wrapperLayout = new GridLayout();
		wrapperLayout.numColumns = 1;
		wrapperLayout.verticalSpacing = 0;
		wrapperLayout.horizontalSpacing = 0;
		wrapperLayout.marginHeight = 0;
		wrapperLayout.marginWidth = 0;
		rootWrapper.setLayout(wrapperLayout);
		
//		createCustomMenu();
		
		createRootPanel();
		
		createWelcomePanel();

        menuBar = new Menu(shell, SWT.BAR);
        MenuItem cascadeFileMenu = new MenuItem(menuBar, SWT.CASCADE);
        cascadeFileMenu.setText(I18N.getString("File"));
        
        Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
        cascadeFileMenu.setMenu(fileMenu);

        /*
         * new game menu item
         */
        MenuItem newGame = new MenuItem(fileMenu, SWT.PUSH);
        newGame.setText(I18N.getString("newGame"));
        
        newGame.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	createNewGameView();
            	
            }
        });
        
        new MenuItem(fileMenu, SWT.SEPARATOR);
        
        /*
         * restart game item
         */
        restartGame = new MenuItem(fileMenu, SWT.PUSH);
        restartGame.setText(I18N.getString("restartGame"));
        
        restartGame.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	restartLastGame();
            }

        });
        restartGame.setEnabled(false);
        
        new MenuItem(fileMenu, SWT.SEPARATOR);
        
        /*
         * highscore menu item
         */
        MenuItem highscore = new MenuItem(fileMenu, SWT.PUSH);
        highscore.setText(I18N.getString("highscore"));
        
        highscore.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	createHighscoreView();
            }
        });
        
        new MenuItem(fileMenu, SWT.SEPARATOR);
        
        /*
         * undo button
         */
        MenuItem undo = new MenuItem(fileMenu, SWT.PUSH);
        undo.setText(I18N.getString("undo"));
        
        undo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	GameEventBus.instance().undoEvent();
            }
        });
        
        new MenuItem(fileMenu, SWT.SEPARATOR);
        
        /*
         * exit menu item
         */
        MenuItem exitItem = new MenuItem(fileMenu, SWT.PUSH);
        exitItem.setText(I18N.getString("Exit"));

        exitItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                exit();
            }

        });
        
        shell.setMenuBar(menuBar);
        
        shell.pack();
	}

	public void createRootPanel() {
		rootPanel = new Composite(rootWrapper, SWT.INHERIT_FORCE);
		rootPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		FillLayout glayout = new FillLayout();
		rootPanel.setLayout(glayout);
	}
	
	private void createCustomMenu() {
		if (customMenu != null) {
			customMenu.dispose();
		}
		
		customMenu = new CustomMenu(rootWrapper, SWT.INHERIT_FORCE, this);
		customMenu.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
	}
	
	private void enableCustomMenu() {
		customMenu.pack();
	}

	public void exit() {
		shell.getDisplay().dispose();
        System.exit(0);
	}
	
	protected void restartLastGame() {
		String currentName;
		List<GameParameter<?>> params;
		
		synchronized (this) {
    		Iterator<String> it = currentGame.keySet().iterator();
        	if (!it.hasNext()) {
        		return;
        	}
        	currentName = it.next();
        	params = currentGame.get(currentName);
		}
    	
    	/*
    	 * check existing impls and compare against the name
    	 */
    	ServiceLoader<GameView> l = ServiceLoader.load(GameView.class);
    	
    	GameView newView = null;
		for (GameView gameView : l) {
			if (gameView.getGameName().equals(currentName)) {
				newView = gameView;
			}
		}
    	
		if (newView != null) {
			createGameView(newView, params);
		}
	}

	public void createGameView(GameView gv, List<GameParameter<?>> list) {
		clearRootPanel();
		
		createCustomMenu();
		currentContentView = gv.initialize(rootPanel, SWT.INHERIT_FORCE, list);
		enableCustomMenu();
		shell.layout(true, true);
		
		synchronized (this) {
			this.currentGame.clear();
			this.currentGame.put(gv.getGameName(), list);
			this.restartGame.setEnabled(true);
		}
		
	}
	
	public void createHighscoreView() {
		clearRootPanel();

		createCustomMenu();
		currentContentView = new HighscoreView(rootPanel, SWT.NONE);
		shell.layout(true, true);
	}

	private void createWelcomePanel() {
		clearRootPanel();
		
		currentContentView = new WelcomeView(rootPanel, SWT.INHERIT_FORCE, this);
		
		rootWrapper.layout();
	}

	private void clearRootPanel() {
		if (currentContentView != null) {
			currentContentView.dispose();
		}
		if (customMenu != null) {
			customMenu.dispose();
		}
		
		rootPanel.dispose();
		
		createRootPanel();
		rootWrapper.layout();
	}

	private void appendKeyListeners() {
		shell.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.F11) {
					switchFullscreenState();
				}
			}

		});
	}

	protected void switchFullscreenState() {
		this.fullscreen = !this.fullscreen;
		shell.setFullScreen(fullscreen);
		menuBar.setVisible(!this.fullscreen);
		shell.layout(true, true);
	}

	public static interface MainWindowOpenedListener {

		void onMainWindowOpened();

	}

	public void createNewGameView() {
		clearRootPanel();
		
		createCustomMenu();
		currentContentView = new NewGameView(rootPanel, SWT.INHERIT_FORCE, this);
		
		rootWrapper.layout(true, true);
	}
}
