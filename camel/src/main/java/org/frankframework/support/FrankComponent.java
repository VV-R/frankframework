package org.frankframework.support;
import org.apache.camel.Endpoint;
import org.apache.camel.support.DefaultComponent;

import org.frankframework.listeners.CamelListener;

import java.util.Map;

public class FrankComponent extends DefaultComponent {
	@Override
	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
//		CamelListener listener = getCamelContext().getRegistry().findSingleByType(CamelListener.class);
		CamelListener listener = getCamelContext().getRegistry().lookupByNameAndType("listener", CamelListener.class);
		if (listener == null)
			throw new Exception("Can not find CamelListener");
		return new FrankEndpoint(uri, this, listener);
	}
}
