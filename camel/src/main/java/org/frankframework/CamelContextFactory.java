package org.frankframework;

import org.apache.camel.spring.SpringCamelContext;

import org.frankframework.support.FrankComponent;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.Lifecycle;

public class CamelContextFactory implements FactoryBean<SpringCamelContext>, ApplicationContextAware, DisposableBean, Lifecycle {
	private ApplicationContext applicationContext;
	private SpringCamelContext camelContext;

	@Override
	public SpringCamelContext getObject() throws Exception {
		camelContext = new SpringCamelContext(applicationContext);

		// Set auto startup to false to prevent routes from starting automatically.
		// This is done because it is intended that Frank configurations start and stop the routes when needed.
		camelContext.setAutoStartup(false);
		camelContext.addComponent("frank", new FrankComponent());

		return camelContext;
	}

	@Override
	public Class<?> getObjectType() {
		return SpringCamelContext.class;
	}

	@Override
	public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void destroy() throws Exception {
		camelContext.close();
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
	public boolean isRunning() {
		return camelContext.isRunning();
	}
}
