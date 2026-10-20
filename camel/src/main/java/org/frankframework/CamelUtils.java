package org.frankframework;

import org.apache.camel.spi.Resource;
import org.apache.camel.spring.SpringCamelContext;

import org.apache.camel.support.ResourceSupport;

import org.frankframework.util.SpringUtils;

import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class CamelUtils {
	public static SpringCamelContext createCamelContext(ApplicationContext applicationContext) {
		return SpringUtils.createBean(applicationContext, SpringCamelContext.class);
	}

	public static Resource getResource(String source) throws Exception {
		URL url = org.frankframework.util.ClassLoaderUtils.getResourceURL(source);

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
}
