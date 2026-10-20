package org.frankframework.support;

import jakarta.ws.rs.NotSupportedException;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.support.DefaultEndpoint;

import org.frankframework.listeners.CamelListener;


public class FrankEndpoint extends DefaultEndpoint {
	private CamelListener listener;

	public FrankEndpoint(String endpointUri, Component component, CamelListener listener) {
		super(endpointUri, component);
		this.listener = listener;
	}

	@Override
	public Producer createProducer() throws Exception {
		return new FrankProducer(listener, this);
	}

	@Override
	public Consumer createConsumer(Processor processor) throws Exception {
		throw new NotSupportedException();
	}
}
