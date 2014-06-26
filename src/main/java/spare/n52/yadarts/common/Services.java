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
package spare.n52.yadarts.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convenience class and Cache for {@link ServiceLoader} enabled interfaces.
 */
public class Services {

	private static final Logger logger = LoggerFactory.getLogger(Services.class);
	private static Map<Class<?>, List<Object>> interfaceImplementations = new HashMap<>();

	/**
	 * @param theClazz the interface class
	 * @return the list of all available interface impls
	 */
	@SuppressWarnings("unchecked")
	public static synchronized <T> List<T> getImplementations(Class<? extends T> theClazz) {
		if (interfaceImplementations.containsKey(theClazz)) {
			return (List<T>) interfaceImplementations.get(theClazz);
		}
		
		ServiceLoader<? extends T> l = ServiceLoader.load(theClazz);
		
		List<T> result = new ArrayList<>();
		for (T t : l) {
			result.add(t);
		}
		
		interfaceImplementations.put(theClazz, (List<Object>) result);
		logger.info(String.format("Implementations for %s: %s",theClazz, result));
		logger.info(String.format("Default Implementations for %s: %s",theClazz, result.get(0)));
		
		return result;
	}
	
	/**
	 * @param theClazz the interface class
	 * @return the first found interface impl
	 */
	public static synchronized <T> T getImplementation(Class<? extends T> theClazz) {
		List<T> result = getImplementations(theClazz);
		
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		
		return null;
	}

	/**
	 * goes through all interface impls created with
	 * {@link #getImplementation(Class)} or
	 * {@link #getImplementations(Class)}, checks
	 * if they extends {@link Disposable}. If so, the instances
	 * are closed via {@link Disposable#shutdown()}.
	 */
	public static synchronized void shutdownDisposables() {
		for (Class<?> c : interfaceImplementations.keySet()) {
			if (Disposable.class.isAssignableFrom(c)) {
				List<Object> list = interfaceImplementations.get(c);
				
				for (Object object : list) {
					try {
						((Disposable) object).shutdown();
					}
					catch (RuntimeException e) {
						logger.warn(e.getMessage(), e);
					}
				}
				
			}
		}
	}
	
}
