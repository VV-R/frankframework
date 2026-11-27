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

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.support.DefaultEndpoint;

import org.apache.camel.util.StringHelper;
import org.frankframework.listeners.CamelListener;

import org.jspecify.annotations.NonNull;

/**
 * Send messages to a Frank!Framework configuration.
 */
public class FrankEndpoint extends DefaultEndpoint {
	private final String key;

	/**
	 * Example input: {@literal "frank://abc?key=value"};
	 * Example output: {@literal "abc"};
	 */
	private String extractKey(String endpointUri) {
		String intermediate = StringHelper.before(endpointUri, "?", endpointUri);
		return StringHelper.after(intermediate, "frank://", intermediate);
	}

	public FrankEndpoint(String endpointUri, FrankComponent component) {
		super(endpointUri, component);
		this.key = extractKey(endpointUri);
	}

	@Override
	public @NonNull Producer createProducer() throws Exception {
		CamelListener listener = getCamelContext().getRegistry().lookupByNameAndType(key, CamelListener.class);
		if (listener == null) {
			throw new IllegalStateException("Can not find CamelListener with name [" + key + "]");
		}

		return new FrankProducer(listener, this);
	}

	@Override
	public @NonNull Consumer createConsumer(@NonNull Processor processor) throws Exception {
		return new FrankConsumer(this, processor, key);
	}
}
