package org.frankframework.components;

import java.util.Collections;
import java.util.List;

public class CamelModule implements Module {
	@Override
	public List<String> getSpringConfigurationFiles() {
		return List.of("springCamel.xml");
	}
}
