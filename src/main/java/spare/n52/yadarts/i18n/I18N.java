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
package spare.n52.yadarts.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * basic access for i18n stuff
 */
public class I18N {

	private static final String YADARTS_BUNDLE = "i18n.yadarts-bundle";
	private static Locale currentLocale;

	static {
		currentLocale = Locale.getDefault();
	}
	
	/**
	 * sets the application wide locale
	 * 
	 * @param l the locale
	 */
	public static void setApplicationLocale(Locale l) {
		currentLocale = l;
	}
	
	/**
	 * this returns the localized string using the current
	 * {@link Locale} (see {@link #setApplicationLocale(Locale)}).
	 * 
	 * if the provided string is null, null will be returned.
	 * if no localized string can be found, the provided string is returned.
	 * 
	 * @param string the key for the localized target
	 * @return the localized string
	 */
	public static String getString(String string) {
		if (string == null) {
			return null;
		}
		try {
			String result = defaultBundle().getString(string);
			return result;
		}
		catch (ClassCastException | MissingResourceException e) {
		}
		
		return string;
	}

	private static ResourceBundle defaultBundle() {
		ResourceBundle result = ResourceBundle.getBundle(YADARTS_BUNDLE, currentLocale);
		
		if (result == null) {
			result = ResourceBundle.getBundle(YADARTS_BUNDLE, Locale.ENGLISH);
		}
		
		return result;
	}

}
