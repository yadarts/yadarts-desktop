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
package spare.n52.yadarts.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spare.n52.yadarts.sound.BasicSoundService;

public class FileBasedConfiguration implements Configuration {

	private static final Logger logger = LoggerFactory.getLogger(FileBasedConfiguration.class);
	private static final String FILE_PATH = "yadarts.cfg";
	private static final String AUTO_FULL_SCREEN = "AUTO_FULL_SCREEN";
	private static final String CALLER_TIMEOUT = "CALLER_TIMEOUT";
	private static final String GRAPHIC_THEME = "GRAPHIC_THEME";
	private Properties properties;
	
	public FileBasedConfiguration() {
		try {
			loadConfiguration();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	private void loadConfiguration() throws IOException {
		this.properties = new Properties();
		
		File f = new File(FILE_PATH);
		logger.info(String.format("Trying to load config from file %s", f.getAbsolutePath()));
		
		InputStream is;
		if (f != null && f.exists() && f.length() > 0) {
			is = new FileInputStream(f);
		}
		else {
			is = getClass().getResourceAsStream("/".concat(FILE_PATH));
			logger.info("Loading properties from resource: "+ "/".concat(FILE_PATH));
		}
		
		if (is == null) {
			throw new IOException(String.format("Could not open inputstream of file %s", "/".concat(FILE_PATH)));
		}
		
		this.properties.load(is);
		logger.info(String.format("Properties loaded: %s", this.properties));
	}

	@Override
	public boolean isAutoFullScreen() {
		return getBooleanKey(AUTO_FULL_SCREEN);
	}
	
	@Override
	public int getCallerTimeout() {
		return getIntegerKey(CALLER_TIMEOUT);
	}
	
	@Override
	public void setAutoFullScreen(boolean b) {
		storeBooleanProperty(AUTO_FULL_SCREEN, b);
	}
	
	@Override
	public String getUITheme() {
		return properties.getProperty(GRAPHIC_THEME, "plain");
	}


	@Override
	public void storeCallerTimeout(int to) {
		storeIntegerProperty(CALLER_TIMEOUT, to);
	}
	
	private void storeIntegerProperty(String val, int to) {
		storeProperty(val, Integer.toString(to));
	}

	private void storeBooleanProperty(String val, boolean b) {
		storeProperty(val, Boolean.toString(b));
	}

	private synchronized void storeProperty(String val, String key) {
		this.properties.setProperty(val, key);
		String cfg = resolveConfigFile();
		try {
			this.properties.store(new FileWriter(cfg), null);
		} catch (IOException e) {
			logger.warn(e.getMessage(), e);
		}
	}

	private String resolveConfigFile() {
		URL resource = getClass().getResource(FILE_PATH);
		return resource.getFile();
	}

	private boolean getBooleanKey(String key) {
		String value = this.properties.getProperty(key);
		
		if (value != null && !value.isEmpty()) {
			return Boolean.parseBoolean(value);
		}
		
		return false;
	}
	
	private int getIntegerKey(String key) {
		String value = this.properties.getProperty(key);
		
		if (value != null && !value.isEmpty()) {
			try {
				int result = Integer.parseInt(value);
				return result;
			}
			catch (NumberFormatException e) {
				logger.warn("Could not parse value '{}' of key '{}': {}", value, key,
						e.getMessage());
			}
		}
		
		return 0;
	}

	@Override
	public String getSoundPackage() {
		return properties.getProperty(BasicSoundService.SOUND_THEME, "robot");
	}


}
