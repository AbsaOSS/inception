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

package africa.absa.inception.mail;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The <b>MailApiConfiguration</b> class provides the Spring configuration for the Mail API module.
 *
 * @author Marcus Portmann
 */
@Configuration
public class MailApiConfiguration {

  /**
   * Returns the grouped Open API for the Mail API.
   *
   * @return the grouped Open API for the Mail API
   */
  @Bean
  public GroupedOpenApi mailOpenApi() {
    return GroupedOpenApi.builder()
        .group("mail")
        .packagesToScan("africa.absa.inception.mail")
        .addOpenApiCustomiser(
            openApi -> openApi.info(new Info().title("Mail API").version(Version.PROJECT_VERSION)))
        .build();
  }
}
