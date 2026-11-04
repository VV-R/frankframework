/*
   Copyright 2026 WeAreFrank!

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package org.frankframework.support;
import org.apache.camel.Endpoint;
import org.apache.camel.support.DefaultComponent;

import org.frankframework.listeners.CamelListener;

import java.util.Map;

/**
 * The FrankComponent is for binding {@link CamelListener}s to Camel message exchanges based on {@link CamelListener#name} attribute.
 */
public class FrankComponent extends DefaultComponent {
	@Override
	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
		CamelListener listener = getCamelContext().getRegistry().lookupByNameAndType(remaining, CamelListener.class);
		if (listener == null) {
			throw new IllegalStateException("Can not find CamelListener with name [" + remaining + "]");
		}
		return new FrankEndpoint(uri, this, listener);
	}
}
