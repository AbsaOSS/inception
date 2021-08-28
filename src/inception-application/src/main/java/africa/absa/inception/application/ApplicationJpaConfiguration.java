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

package africa.absa.inception.application;

import africa.absa.inception.jpa.JpaUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

/**
 * The <b>ApplicationJpaConfiguration</b> class initialises the JPA EntityManagerFactory for the
 * application.
 *
 * @author Marcus Portmann
 */
@Configuration
@ConditionalOnClass(
    name = {
      "org.springframework.orm.jpa.JpaVendorAdapter",
      "org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean",
      "org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter",
      "org.springframework.transaction.PlatformTransactionManager"
    })
public class ApplicationJpaConfiguration {

  /* Logger */
  private static final Logger logger = LoggerFactory.getLogger(ApplicationJpaConfiguration.class);

  private final ApplicationContext applicationContext;

  /**
   * Constructs a new <b>ApplicationJpaConfiguration</b>.
   *
   * @param applicationContext the Spring application context
   */
  ApplicationJpaConfiguration(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  /**
   * Returns the application entity manager factory bean associated with the application data
   * source.
   *
   * @param dataSource the application data source
   * @param platformTransactionManager the platform transaction manager
   * @return the application entity manager factory bean associated with the application data source
   */
  @Bean
  @Primary
  @ConditionalOnBean(
      name = {"applicationDataSource"},
      value = {DataSource.class})
  public LocalContainerEntityManagerFactoryBean applicationEntityManagerFactory(
      @Qualifier("applicationDataSource") DataSource dataSource,
      PlatformTransactionManager platformTransactionManager) {
    return JpaUtil.createEntityManager(
        "application",
        dataSource,
        platformTransactionManager,
        StringUtils.toStringArray(packagesToScanForEntities()));
  }

  /**
   * Returns the names of the packages to scan for JPA entity classes.
   *
   * @return the names of the packages to scan for JPA entity classes
   */
  protected List<String> packagesToScanForEntities() {
    List<String> packagesToScan = new ArrayList<>();

    // Add the base packages specified using the EnableJpaRepositories annotation
    Map<String, Object> annotatedBeans =
        applicationContext.getBeansWithAnnotation(EnableJpaRepositories.class);

    for (String beanName : annotatedBeans.keySet()) {
      Class<?> beanClass = annotatedBeans.get(beanName).getClass();

      EnableJpaRepositories enableJpaRepositories =
          beanClass.getAnnotation(EnableJpaRepositories.class);

      if (enableJpaRepositories != null) {
        for (String basePackage : enableJpaRepositories.basePackages()) {
          if (!basePackage.startsWith("africa.absa.inception")) {
            // Replace any existing packages to scan with the higher level package
            packagesToScan.removeIf(packageToScan -> packageToScan.startsWith(basePackage));

            // Check if there is a higher level package already being scanned
            if (packagesToScan.stream().noneMatch(basePackage::startsWith)) {
              packagesToScan.add(basePackage);
            }
          }
        }
      }
    }

    logger.info(
        "Scanning the following packages for JPA entities: "
            + StringUtils.collectionToDelimitedString(packagesToScan, ","));

    return packagesToScan;
  }
}
