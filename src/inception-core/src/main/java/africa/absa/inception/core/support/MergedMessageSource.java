/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package africa.absa.inception.core.support;

import java.io.IOException;
import java.util.Properties;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * The <b>MergedMessageSource</b> class implements the merged message source that provides support
 * for merging multiple message property files with the .properties suffix.
 *
 * @author Marcus Portmann
 */
public class MergedMessageSource extends ReloadableResourceBundleMessageSource {

  private static final String PROPERTIES_SUFFIX = ".properties";

  private final PathMatchingResourcePatternResolver resolver =
      new PathMatchingResourcePatternResolver();

  @Override
  protected PropertiesHolder refreshProperties(String filename, PropertiesHolder propertiesHolder) {
    if (filename.startsWith(PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX)) {
      return refreshClassPathProperties(filename, propertiesHolder);
    } else {
      return super.refreshProperties(filename, propertiesHolder);
    }
  }

  private PropertiesHolder refreshClassPathProperties(
      String filename, PropertiesHolder propertiesHolder) {
    Properties properties = new Properties();
    long lastModified = -1;
    try {
      Resource[] resources = resolver.getResources(filename + PROPERTIES_SUFFIX);
      for (Resource resource : resources) {
        String resourcePath = resource.getURI().toString().replace(PROPERTIES_SUFFIX, "");
        PropertiesHolder resourcePropertiesHolder =
            super.refreshProperties(resourcePath, propertiesHolder);

        Properties resourceProperties = resourcePropertiesHolder.getProperties();

        if (resourceProperties != null) {
          properties.putAll(resourceProperties);
        }

        if (lastModified < resource.lastModified()) {
          lastModified = resource.lastModified();
        }
      }
    } catch (IOException ignored) {
    }

    return new PropertiesHolder(properties, lastModified);
  }
}
