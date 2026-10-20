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

		if (exchange.getMessage().getBody() == null) {
			throw new Exception("Message body should not be null");
		}

		Message frankMsg = new Message(
			exchange.getMessage().getBody().toString(),
			new MessageContext((Map<String, Serializable>)(Map)exchange.getMessage().getHeaders())
		);
//		exchange.getMessage().getMessageId();
		camelListener.processMessage(frankMsg, session);
	}
}
