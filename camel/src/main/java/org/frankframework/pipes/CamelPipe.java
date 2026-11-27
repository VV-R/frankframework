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

import lombok.Setter;

import org.apache.camel.Exchange;

import org.frankframework.configuration.ConfigurationException;
import org.frankframework.core.ParameterException;
import org.frankframework.core.PipeLineSession;
import org.frankframework.core.PipeRunException;
import org.frankframework.core.PipeRunResult;
import org.frankframework.parameters.ParameterList;
import org.frankframework.parameters.ParameterValue;
import org.frankframework.stream.Message;
import org.frankframework.support.FrankComponent;
import org.frankframework.support.FrankConsumer;


public class CamelPipe extends AbstractCamelPipe {
	@Setter private String endpointName;

	@Override
	public void configure() throws ConfigurationException {
		if (endpointName == null) {
			throw new ConfigurationException("endpointName must be set");
		}

		super.configure();

		endpoint = camelContext.getEndpoint("frank:" + endpointName);
	}

	private void copyParamsToExchange(Message message, PipeLineSession session, Exchange exchange) throws ParameterException {
		ParameterList parameterList = getParameterList();

		for (ParameterValue param : parameterList.getValues(message, session)) {
			exchange.setVariable(param.getName(), param.getValue());
		}
	}

	@Override
	public PipeRunResult doPipe(Message message, PipeLineSession session) throws PipeRunException {
		try {

			Exchange exchange = createExchangeFromSession(session);

			setupExchangeInMessage(exchange, message);

			copyParamsToExchange(message, session, exchange);

//			Exchange responseExchange = producerTemplate.send(endpoint, exchange);

			FrankComponent component = camelContext.getComponent("frank", FrankComponent.class);
			FrankConsumer consumer = component.getConsumer(endpointName);
			consumer.getProcessor().process(exchange);

			return new PipeRunResult(
				getSuccessForward(), convertResponseExchangeToFrankMessage(exchange)
			);
		} catch (Exception e) {
			throw new PipeRunException(this, "something went wrong relaying message to Camel", e);
		}
	}
}
