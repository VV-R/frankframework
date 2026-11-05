package org.frankframework.support;

import org.apache.camel.spi.UuidGenerator;

import org.frankframework.util.MessageUtils;

/**
 * An implementation of the Apache Camel {@link UuidGenerator} SPI that can be used to
 * make a CamelContext generate the same kind of message/exchange id's as
 * Frank!Framework does.
 * <p>
 * This class may become the default UuidGenerator for the CamelContext's used in
 * Frank!Framework with Apache Camel integrations.
 */
public class FrankUuidGenerator implements UuidGenerator {

	@Override
	public String generateUuid() {
		return MessageUtils.generateDefaultMessageId();
	}
}
