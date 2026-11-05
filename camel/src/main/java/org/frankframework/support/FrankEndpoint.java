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

import jakarta.ws.rs.NotSupportedException;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.support.DefaultEndpoint;

import org.frankframework.listeners.CamelListener;

import org.jspecify.annotations.NonNull;

/**
 * Send messages to a Frank!Framework configuration.
 */
public class FrankEndpoint extends DefaultEndpoint {
	private CamelListener listener;

	public FrankEndpoint(String endpointUri, Component component, CamelListener listener) {
		super(endpointUri, component);
		this.listener = listener;
	}

	@Override
	public @NonNull Producer createProducer() throws Exception {
		return new FrankProducer(listener, this);
	}

	@Override
	public @NonNull Consumer createConsumer(@NonNull Processor processor) throws UnsupportedOperationException {
		// inspired from: https://github.com/apache/camel/blob/30afcab269af9841d34bb6d8058bc0b0016a1c05/components/camel-tika/src/main/java/org/apache/camel/component/tika/TikaEndpoint.java#L69
		throw new UnsupportedOperationException("Consumer is not supported for Frank endpoint");
	}
}
