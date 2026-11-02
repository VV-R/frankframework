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

import org.apache.camel.spi.Resource;
import org.apache.camel.spring.SpringCamelContext;

import org.apache.camel.support.ResourceSupport;

import org.frankframework.util.SpringUtils;

import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@UtilityClass
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
