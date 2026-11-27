package org.frankframework.support;

import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.ShutdownRunningTask;
import org.apache.camel.Suspendable;
import org.apache.camel.spi.ShutdownAware;
import org.apache.camel.support.DefaultConsumer;

public class FrankConsumer extends DefaultConsumer implements ShutdownAware, Suspendable {
	private final String key;

	public FrankConsumer(Endpoint endpoint, Processor processor, String key) {
		super(endpoint, processor);
		this.key = key;
	}

	private FrankComponent getComponent() {
		return (FrankComponent)(getEndpoint().getComponent());
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
		getComponent().addConsumer(key, this);
	}

	@Override
	protected void doStop() throws Exception {
		getComponent().removeConsumer(key, this);
	}

	@Override
	protected void doSuspend() throws Exception {
		getComponent().removeConsumer(key, this);
	}

	@Override
	protected void doResume() throws Exception {
		// resume by using the start logic
		getComponent().addConsumer(key, this);
	}

	@Override
	public boolean deferShutdown(ShutdownRunningTask shutdownRunningTask) {
		return true;
	}

	@Override
	public int getPendingExchangesSize() {
		return 0;
	}

	@Override
	public void prepareShutdown(boolean suspendOnly, boolean forced) {
		// do nothing
	}
}
