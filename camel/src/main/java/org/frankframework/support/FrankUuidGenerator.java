package org.frankframework.support;

import org.apache.camel.spi.UuidGenerator;

import org.frankframework.util.MessageUtils;

public class FrankUuidGenerator implements UuidGenerator {

	@Override
	public String generateUuid() {
		return MessageUtils.generateDefaultMessageId();
	}
}
