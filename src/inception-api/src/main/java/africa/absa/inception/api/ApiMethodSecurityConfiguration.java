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

package africa.absa.inception.api;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/**
 * The <b>ApiMethodSecurityConfiguration</b> class applies the API-specific method security
 * configuration to the global method security configuration.
 *
 * @author Marcus Portmann
 */
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApiMethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

  /** The Spring application context. */
  private final ApplicationContext applicationContext;

  /**
   * Constructs a new <b>ApiMethodSecurityConfiguration</b>.
   *
   * @param applicationContext the Spring application context
   */
  public ApiMethodSecurityConfiguration(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  /**
   * Returns the method security expression handler.
   *
   * @return the method security expression handler
   */
  @Bean
  public MethodSecurityExpressionHandler expressionHandler() {
    return new ApiMethodSecurityExpressionHandler(applicationContext);
  }

  @Override
  protected MethodSecurityExpressionHandler createExpressionHandler() {
    return new ApiMethodSecurityExpressionHandler(applicationContext);
  }
}
