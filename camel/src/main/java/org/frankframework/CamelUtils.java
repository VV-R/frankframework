/*
   Copyright 2026 WeAreFrank!

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

package org.frankframework;

import lombok.experimental.UtilityClass;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spi.Resource;
import org.apache.camel.spring.SpringCamelContext;

import org.apache.camel.support.ResourceSupport;

import org.frankframework.support.FrankComponent;
import org.frankframework.util.SpringUtils;

import org.jspecify.annotations.NonNull;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.Lifecycle;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.stream.Stream;

@UtilityClass
public class CamelUtils {
	public static SpringCamelContext createCamelContext(ApplicationContext applicationContext) {
		boolean setup = false;

		try {
			applicationContext.getAutowireCapableBeanFactory().getBean(SpringCamelContext.class);
		} catch (NoSuchBeanDefinitionException e) {
			setup = true;
		}

		var camelContext = SpringUtils.createBean(applicationContext, SpringCamelContext.class);

		if (setup) {
			// Set auto startup to false to prevent routes from starting automatically.
			// This is done because it is intended that Frank configurations start and stop the routes when needed.
			camelContext.setAutoStartup(false);
			camelContext.addComponent("frank", new FrankComponent());

			if (applicationContext instanceof Lifecycle lifecycle) {
				lifecycle.isRunning();
				camelContext.start();
			}
		}

		return camelContext;
	}

	public static Stream<String> getRouteIdsFromRouteBuilder(RouteBuilder routeBuilder) {
		return routeBuilder.getRoutes().getRoutes().stream().map(RouteDefinition::getRouteId);
	}

	public static void startRoutes(CamelContext context, RouteBuilder routeBuilder) {
		getRouteIdsFromRouteBuilder(routeBuilder).forEach((String rId) -> {
			try {
				context.getRouteController().startRoute(rId);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	public static void stopRoutes(CamelContext context, RouteBuilder routeBuilder) {
		getRouteIdsFromRouteBuilder(routeBuilder).forEach((String rId) -> {
			try {
				context.getRouteController().stopRoute(rId);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	public static Resource getResource(String source) throws Exception {
		URL url = org.frankframework.util.ClassLoaderUtils.getResourceURL(source);

		if (url == null) {
			throw new IllegalArgumentException("\"source\" does not point to a resource");
		}

		return new ResourceSupport("jar", url.getFile()) {
			@Override
			public boolean exists() {
				return true;
			}

			@Override
			public @NonNull InputStream getInputStream() throws IOException {
				return url.openStream();
			}
		};
	}
}
