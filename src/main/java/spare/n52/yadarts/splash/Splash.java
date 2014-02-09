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
package spare.n52.yadarts.splash;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The splash class
 */
public class Splash {
	
	private static final Logger logger = LoggerFactory.getLogger(Splash.class);

	private int splashPos = 0;
	private final int SPLASH_MAX = 5;
	protected SplashListener finishedListener;
	private boolean running = true;
	private ProgressBar bar;

	private Image image;

	private Shell theShell;

	public Splash(Display display, SplashListener l) {
		finishedListener = l;
		image = new Image(display, getClass().getResourceAsStream(
				"/images/splash.jpg"));

		theShell = new Shell(SWT.ON_TOP);
		initLayout(image, theShell);
		
		initShell(display, theShell);

		executeLoading(display);

		while (running) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	protected void executeLoading(Display display) {
		display.asyncExec(new Runnable() {
			public void run() {

				for (splashPos = 0; splashPos <= SPLASH_MAX; splashPos++) {
					try {

						Thread.sleep(100);
					} catch (InterruptedException e) {
						logger.warn(e.getMessage(), e);
					}
					bar.setSelection(splashPos);
				}
				
				running = false;
				finishedListener.onSplashFinished(Splash.this);
			}
		});
	}

	protected void initShell(Display display, final Shell splash) {
		splash.pack();

		Rectangle splashBounds = splash.getBounds();
		Rectangle displayBounds = display.getPrimaryMonitor().getBounds();
		int x = (displayBounds.width - splashBounds.width) / 2;
		int y = (displayBounds.height - splashBounds.height) / 2;
		splash.setLocation(x, y);
//		splash.open();
	}

	protected void initLayout(final Image image, final Shell shell) {
		bar = new ProgressBar(shell, SWT.NONE);
		bar.setMaximum(SPLASH_MAX);

		Label label = new Label(shell, SWT.NONE);
		label.setImage(image);

		FormLayout layout = new FormLayout();
		shell.setLayout(layout);

		FormData labelData = new FormData();
		labelData.right = new FormAttachment(100, 0);
		labelData.bottom = new FormAttachment(100, 0);
		label.setLayoutData(labelData);

		FormData progressData = new FormData();
		progressData.left = new FormAttachment(0, -5);
		progressData.right = new FormAttachment(100, 0);
		progressData.bottom = new FormAttachment(100, 0);
		bar.setLayoutData(progressData);
	}
	
	public void closeSelf() {
		theShell.close();
		image.dispose();
		theShell.dispose();		
	}

	public static interface SplashListener {

		public void onSplashFinished(Splash s);

	}


}