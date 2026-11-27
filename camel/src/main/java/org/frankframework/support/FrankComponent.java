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

import org.apache.camel.support.service.ServiceHelper;

import org.frankframework.listeners.CamelListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The FrankComponent is for binding {@link CamelListener}s to Camel message exchanges based on {@link CamelListener#name} attribute.
 */
public class FrankComponent extends DefaultComponent {
	private final ConcurrentMap<String, FrankConsumer> consumers = new ConcurrentHashMap<>();

	@Override
	protected Endpoint createEndpoint(
		String uri,
		// the part after the scheme and before the query parameters (if any), e.g. "scheme:{remaining}", like the path
		String remaining,
		Map<String, Object> parameters
	) {
		return new FrankEndpoint(uri, this);
	}

	@Override
	protected void doShutdown() throws Exception {
		ServiceHelper.stopAndShutdownService(consumers);
		consumers.clear();
		super.doShutdown();
	}

	public void addConsumer(String key, FrankConsumer consumer) {
		if (consumers.putIfAbsent(key, consumer) != null) {
			throw new IllegalArgumentException(String.format("Cannot create a second consumer with endpoint %s", key));
		}
	}

	public void removeConsumer(String key, FrankConsumer consumer) {
		consumers.remove(key, consumer);
	}

	public FrankConsumer getConsumer(String key) {
		return consumers.get(key);
	}
}
