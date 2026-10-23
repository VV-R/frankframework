package org.frankframework.listeners;

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

public class CamelListener implements IPushingListener<Message>, ReceiverAware<Message> {
	private String name;
	private ApplicationContext applicationContext;
	private Receiver<Message> receiver;
	private String uri;
	private IMessageHandler<Message> handler;
	private IbisExceptionListener listener;
	private String routesSource;
	private CamelContext camelContext;

	public void setRoutesSource(String routesSource) {
		this.routesSource = routesSource;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}

	public void setReceiver(Receiver<Message> receiver) {
		this.receiver = receiver;
	}

	public Receiver<Message> getReceiver() {
		return receiver;
	}

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

	}

	@Override
	public void configure() throws ConfigurationException {
//		getApplicationContext().getAutowireCapableBeanFactory().autowireBean(this);

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

	@Override
	public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public void processMessage(Message message, PipeLineSession session) throws ListenerException {
		receiver.processRequest(this, new MessageWrapper<Message>(message, session.getMessageId(), null), session);
	}

	@Override
	public void setHandler(@NonNull IMessageHandler<Message> handler) {
		this.handler = handler;
	}

	@Override
	public void setExceptionListener(@NonNull IbisExceptionListener listener) {
		this.listener = listener;
	}

	@Override
	public RawMessageWrapper<Message> wrapRawMessage(@NonNull Message rawMessage, @NonNull PipeLineSession session) throws ListenerException {
		return new RawMessageWrapper<Message>(rawMessage);
	}
}
