/*
   Copyright 2025-2026 WeAreFrank!

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
package org.frankframework.pipes;


import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import lombok.Setter;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dsl.xml.io.CamelXmlRoutesBuilderLoader;
import org.apache.camel.spi.Resource;
import org.apache.camel.support.ResourceSupport;

import org.frankframework.CamelUtils;
import org.frankframework.configuration.ConfigurationException;
import org.frankframework.core.PipeLineSession;
import org.frankframework.core.PipeRunException;
import org.frankframework.stream.Message;


/**
 * Pipe that used to test reading a resource from the local (plugin) classpath.
 */
public abstract class AbstractCamelPipe extends FixedForwardPipe {
	protected CamelContext camelContext;
	protected ProducerTemplate producerTemplate;
	protected CamelXmlRoutesBuilderLoader camelXmlRoutesBuilderLoader;
	@Setter
	private String routesSource;
	protected Endpoint endpoint;

	@Override
	public void configure() throws ConfigurationException {
		super.configure();

		camelContext = CamelUtils.createCamelContext(getApplicationContext());

		producerTemplate = this.camelContext.createProducerTemplate();
		camelXmlRoutesBuilderLoader = new CamelXmlRoutesBuilderLoader();

		try {
			configureRoutes();
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}

	private Resource getRouteBuilderResource() throws Exception {
		URL url = org.frankframework.util.ClassLoaderUtils.getResourceURL(routesSource);

		return new ResourceSupport("jar", url.getFile()) {
			@Override
			public boolean exists() {
				return true;
			}

			@Override
			public InputStream getInputStream() throws IOException {
				return url.openStream();
			}
		};
	}

	private RouteBuilder getRouteBuilder() throws Exception {
		Resource resource = getRouteBuilderResource();
		return camelXmlRoutesBuilderLoader.doLoadRouteBuilder(resource);
	}

	private void configureRoutes() throws Exception {
		RouteBuilder routeBuilder;
		routeBuilder = getRouteBuilder();
		// TODO: maybe add null check

		camelContext.addRoutes(routeBuilder);
	}

	private Map<String, Object> serializeMap(Map<String, Serializable> input) {
		return new HashMap<>(input);
	}

	protected Exchange createExchangeFromSession(PipeLineSession session) throws PipeRunException {
		Exchange exchange = endpoint.createExchange(ExchangePattern.InOut);

		String messageId = session.getMessageId();
		if (messageId == null)
			throw new PipeRunException(this, "Pipe line session message id should not be null");
		exchange.setExchangeId(messageId);

		return exchange;
	}

	protected void setupExchangeInMessage(Exchange exchange, Message message) throws IOException {
		CamelUtils.convertFrankMessageToCamelMessage(message, exchange.getIn());
	}

	protected Message convertResponseExchangeToFrankMessage(Exchange exchange) {
//		ExchangeHelper.extractResultBody may be useful
		Object resultBody = exchange.getOut().getBody();
		return Message.asMessage(resultBody);
	}

	@Override
	public void start() {
		super.start();
		camelContext.start();
	}

	@Override
	public void stop() {
		super.stop();
		camelContext.stop();
	}
}
