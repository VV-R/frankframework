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

package org.frankframework.listeners;

import lombok.Getter;

import org.apache.camel.CamelContext;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dsl.xml.io.CamelXmlRoutesBuilderLoader;

import org.frankframework.CamelUtils;
import org.frankframework.configuration.ConfigurationException;
import org.frankframework.core.IMessageHandler;
import org.frankframework.core.IPushingListener;
import org.frankframework.core.IbisExceptionListener;
import org.frankframework.core.ListenerException;
import org.frankframework.core.PipeLineResult;
import org.frankframework.core.PipeLineSession;
import org.frankframework.receivers.MessageWrapper;
import org.frankframework.receivers.RawMessageWrapper;
import org.frankframework.receivers.Receiver;
import org.frankframework.receivers.ReceiverAware;
import org.frankframework.stream.Message;

import org.frankframework.support.FrankComponent;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.Map;

/**
 * Listener that allows a {@link Receiver} to receive messages from a Camel route.
 * The receiver can be targeted in a Camel route by the following URI: <code>frank:{@link CamelListener#name}</code>
 *
 * @author Ruben van Voorden
 */
public class CamelListener implements IPushingListener<Message>, ReceiverAware<Message> {
	@Getter
	private String name;
	private ApplicationContext applicationContext;
	private Receiver<Message> receiver;
	private IMessageHandler<Message> handler;
	private IbisExceptionListener listener;
	private String routesSource;
	private CamelContext camelContext;

	@Override
	public void start() {
		camelContext.start();
	}

	@Override
	public void stop() {
		camelContext.stop();
	}

	@Override
	public Message extractMessage(@NonNull RawMessageWrapper<Message> rawMessage, @NonNull Map<String,Object> context) throws ListenerException {
		return rawMessage.getRawMessage();
	}

	@Override
	public void afterMessageProcessed(PipeLineResult processResult, RawMessageWrapper<Message> rawMessage, PipeLineSession pipeLineSession) throws ListenerException {
		// Do nothing
	}

	@Override
	public void configure() throws ConfigurationException {
		RouteBuilder routesBuilder;
		try (CamelXmlRoutesBuilderLoader camelXmlRoutesBuilderLoader = new CamelXmlRoutesBuilderLoader()) {
			routesBuilder = camelXmlRoutesBuilderLoader.doLoadRouteBuilder(CamelUtils.getResource(routesSource));
		} catch (Exception e) { // could IOException
			throw new ConfigurationException(e);
		}

		camelContext = CamelUtils.createCamelContext(getApplicationContext());
		camelContext.addComponent("frank", new FrankComponent());
		camelContext.getRegistry().bind(name, this);
		try {
			camelContext.addRoutes(routesBuilder);
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}

	public void processMessage(Message message, PipeLineSession session) throws ListenerException {
		receiver.processRequest(this, new MessageWrapper<Message>(message, session.getMessageId(), null), session);
	}

	@Override
	public RawMessageWrapper<Message> wrapRawMessage(@NonNull Message rawMessage, @NonNull PipeLineSession session) throws ListenerException {
		return new RawMessageWrapper<Message>(rawMessage);
	}

	/**
	 * The path to the Camel routes configuration XML in configuration directory.
	 */
	public void setRoutesSource(String routesSource) {
		this.routesSource = routesSource;
	}

	/**
	 * Sets the listener name used in a Camel configuration to refer to the listener. e.g: <code>to("frank:{name}")</code>.
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the message handler. {@link CamelListener#handler} is currently not being used, but its setter and getter are required by IPushingListener.
	 */
	@Override
	public void setHandler(@NonNull IMessageHandler<Message> handler) {
		this.handler = handler;
	}

	@Override
	public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public void setExceptionListener(@NonNull IbisExceptionListener listener) {
		this.listener = listener;
	}

	public void setReceiver(Receiver<Message> receiver) {
		this.receiver = receiver;
	}

	public Receiver<Message> getReceiver() {
		return receiver;
	}
}
