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

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spare.n52.yadarts.config.Configuration;
import spare.n52.yadarts.i18n.I18N;
import spare.n52.yadarts.layout.BasicX01GameView;
import spare.n52.yadarts.layout.GameParameter;
import spare.n52.yadarts.layout.NewGameDialog;
import spare.n52.yadarts.layout.Three01GameView;
import spare.n52.yadarts.layout.GameParameter.Bounds;
import spare.n52.yadarts.themes.Theme;

public class MainWindow {

	private static final Logger logger = LoggerFactory
			.getLogger(MainWindow.class);
	
	private static List<String> thePlayers = Arrays.asList(new String[] {
			"Simon", "Conny", "Daniel"
	});

	private static GameParameter<String> gp;
	
	static {
		gp = new GameParameter<String>(String.class, BasicX01GameView.PLAYERS_PARAMETER, Bounds.unbound(2));
		gp.setValue(thePlayers);
	}
	
	private Shell shell;
	private boolean fullscreen;

	public MainWindow(Display display, MainWindowOpenedListener l) {
		shell = new Shell(display);
		this.fullscreen = Configuration.Instance.instance().isAutoFullScreen();
		
		shell.setMinimumSize(800, 600);
		shell.setText("yadarts desktop edition");
		
		initLayout();

//		Object result = NewGameDialog.create(shell).open();
		
		appendKeyListeners();

		shell.open();
		
		if (this.fullscreen) {
			shell.setFullScreen(this.fullscreen);
		}
		else {
			resolvePriorWindowState();
		}

		l.onMainWindowOpened();

		logger.info("bootstrapping finished!");

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
		
		try {
			EventEngine.instance().shutdown();
		} catch (InitializationException e) {
			logger.warn(e.getMessage(), e);
		}
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
		try {
			shell.setBackgroundImage(Theme.getCurrentTheme().getBackground(shell.getDisplay()));
		} catch (FileNotFoundException e1) {
			logger.warn(e1.getMessage(), e1);
		}
		
		List<GameParameter<?>> gpList = new ArrayList<>();
		gpList.add(gp);
		new Three01GameView().initialize(shell, SWT.NONE, gpList);
		
		FillLayout layout = new FillLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		shell.setLayout(layout);

        Menu menuBar = new Menu(shell, SWT.BAR);
        MenuItem cascadeFileMenu = new MenuItem(menuBar, SWT.CASCADE);
        cascadeFileMenu.setText(I18N.getString("File"));
        
        Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
        cascadeFileMenu.setMenu(fileMenu);

        MenuItem exitItem = new MenuItem(fileMenu, SWT.PUSH);
        exitItem.setText(I18N.getString("Exit"));

        exitItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shell.getDisplay().dispose();
                System.exit(0);
            }
        });
        
        MenuItem newGame = new MenuItem(fileMenu, SWT.PUSH);
        newGame.setText(I18N.getString("newGame"));
        
        newGame.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                NewGameDialog.create(shell);
            }
        });
        
        shell.setMenuBar(menuBar);
        
        shell.pack();
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
	}

	public static interface MainWindowOpenedListener {

		void onMainWindowOpened();

	}
}
