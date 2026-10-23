package org.frankframework.support;

import jakarta.ws.rs.NotSupportedException;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.support.DefaultEndpoint;

import org.frankframework.listeners.CamelListener;

import org.jspecify.annotations.NonNull;


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
	public @NonNull Consumer createConsumer(@NonNull Processor processor) throws Exception {
		throw new NotSupportedException();
	}
}
