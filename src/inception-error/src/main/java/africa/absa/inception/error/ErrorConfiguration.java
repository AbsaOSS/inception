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

package africa.absa.inception.error;

import africa.absa.inception.jpa.JpaUtil;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * The <b>ErrorConfiguration</b> class provides the Spring configuration for the Error module.
 *
 * @author Marcus Portmann
 */
@Configuration
@EnableJpaRepositories(
    entityManagerFactoryRef = "errorEntityManagerFactory",
    basePackages = {"africa.absa.inception.error"})
public class ErrorConfiguration {

  /** The Spring application context. */
  private final ApplicationContext applicationContext;

  /**
   * Constructs a new <b>ErrorConfiguration</b>.
   *
   * @param applicationContext the Spring application context
   */
  public ErrorConfiguration(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  /**
   * Returns the error entity manager factory bean associated with the application data source.
   *
   * @param dataSource the application data source
   * @param platformTransactionManager the platform transaction manager
   * @return the error entity manager factory bean associated with the application data source
   */
  @Bean
  public LocalContainerEntityManagerFactoryBean errorEntityManagerFactory(
      @Qualifier("applicationDataSource") DataSource dataSource,
      PlatformTransactionManager platformTransactionManager) {
    return JpaUtil.createEntityManager(
        "error", dataSource, platformTransactionManager, "africa.absa.inception.error");
  }
}
