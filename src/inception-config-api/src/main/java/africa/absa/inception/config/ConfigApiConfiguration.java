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

package africa.absa.inception.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The <b>ConfigApiConfiguration</b> class provides the Spring configuration for the Config API
 * module.
 *
 * @author Marcus Portmann
 */
@Configuration
public class ConfigApiConfiguration {

  /**
   * Returns the grouped Open API for the Config API.
   *
   * @return the grouped Open API for the Config API
   */
  @Bean
  public GroupedOpenApi configOpenApi() {
    return GroupedOpenApi.builder()
        .group("config")
        .packagesToScan("africa.absa.inception.config")
        .addOpenApiCustomiser(
            openApi ->
                openApi.info(new Info().title("Config API").version(Version.PROJECT_VERSION)))
        .build();
  }
}
