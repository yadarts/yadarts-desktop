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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ServicesTest {

	@Test
	public void testInterfaceCreation() {
		List<DummyServiceLoadedInterface> dslis = 
				Services.getInterfaceImplementations(DummyServiceLoadedInterface.class);
		
		Assert.assertTrue(dslis.size() == 2);
		
		DummyServiceLoadedInterface dsli = 
				Services.getInterfaceImplementation(DummyServiceLoadedInterface.class);
		
		Assert.assertTrue(dslis.get(0) == dsli);
		
		Services.shutdownDisposables();
		
		Assert.assertTrue(dsli.isShutdown());
	}
	
	public static interface DummyServiceLoadedInterface extends Disposable {
		
		public boolean isShutdown();
		
	}
	
	public static class DummyServiceLoadedInterfaceImpl implements DummyServiceLoadedInterface {

		private boolean shutdown;

		@Override
		public void shutdown() {
			this.shutdown = true;
		}

		public boolean isShutdown() {
			return shutdown;
		}
		
	}
	
	public static class AnotherDummyServiceLoadedInterfaceImpl implements DummyServiceLoadedInterface {

		@Override
		public void shutdown() {
		}

		@Override
		public boolean isShutdown() {
			return false;
		}

	}
}
