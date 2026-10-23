package org.frankframework.support;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.support.DefaultProducer;

import org.frankframework.core.PipeLineSession;
import org.frankframework.listeners.CamelListener;
import org.frankframework.stream.Message;
import org.frankframework.stream.MessageContext;

import java.io.Serializable;
import java.util.Map;

public class FrankProducer extends DefaultProducer {
	private CamelListener camelListener;

	protected FrankProducer(CamelListener listener, Endpoint endpoint) {
		super(endpoint);
		camelListener = listener;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		PipeLineSession session = new PipeLineSession();
		session.put(PipeLineSession.MESSAGE_ID_KEY, exchange.getMessage().getMessageId());

		Message frankMsg = Message.asMessage(exchange.getMessage().getBody());
		// This is the only way to set the message context at the moment.
		frankMsg.getContext().putAll((Map<String, Serializable>)(Map)exchange.getMessage().getHeaders());

		camelListener.processMessage(frankMsg, session);
	}
}
