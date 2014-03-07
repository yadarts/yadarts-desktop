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


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.GuiceFilter;

import spare.n52.yadarts.common.Disposable;
import spare.n52.yadarts.common.Services;
import spare.n52.yadarts.splash.Splash;
import yadarts.server.servlet.BaseServletListener;

public class YadartsDesktopMain {
	
	private static final Logger logger = LoggerFactory.getLogger(YadartsDesktopMain.class);

	public static void main(String[] args) throws Exception {
		logger.info("bootstrapping yadarts desktop...");
		final Display display = Display.getDefault();
		new Splash(display, new Splash.SplashListener() {

			@Override
			public void onSplashFinished(Splash s) {
				startMainApp(display, s);
			}
		});
	}

	protected static void startMainApp(Display display, final Splash splash) {
		new MainWindow(display, new MainWindow.MainWindowOpenedListener() {
			
			@Override
			public void onMainWindowOpened() {
				splash.closeSelf();
				
				/*
				 * init those classes which are discoverable as Disposable
				 */
				Services.getImplementation(Disposable.class);
			}
		});
	}
	
	public static class JettyDisposable implements Disposable {
		
		private static Server server;

		public JettyDisposable() {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						startJetty();
					} catch (Exception e) {
						e.printStackTrace();
					}						
				}
			}).start();
		}
		

		private static void startJetty() throws Exception {
		      server = new Server(8081);
		       
		      ServletContextHandler sch = new ServletContextHandler(server, "/");
		       
		      sch.addEventListener(new BaseServletListener());
		      
		      sch.addFilter(GuiceFilter.class, "/*", null);
		      // Must add DefaultServlet for embedded Jetty.
		      // Failing to do this will cause 404 errors.
		      // This is not needed if web.xml is used instead.
		      sch.addServlet(DefaultServlet.class, "/");
		       
		      sch.addFilter(GuiceFilter.class, "/*", null);
		      // Start the server
		      server.start();
		      server.join();		
		}

		@Override
		public void shutdown() {
			try {
				server.stop();
			} catch (Exception e) {
				logger.warn(e.getMessage(), e);
			}
		}
		
	}

}
