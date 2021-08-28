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

package africa.absa.inception.test;

import africa.absa.inception.jpa.JpaUtil;
import africa.absa.inception.json.DateTimeModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.agroal.api.AgroalDataSource;
import io.agroal.api.configuration.supplier.AgroalDataSourceConfigurationSupplier;
import io.agroal.api.configuration.supplier.AgroalPropertiesReader;
import io.agroal.api.transaction.TransactionIntegration;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import javax.sql.DataSource;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.configuration.HubConfiguration;
import liquibase.configuration.LiquibaseConfiguration;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * The <b>TestConfiguration</b> class provides the base Spring configuration for the JUnit test
 * classes that test the capabilities provided by the <b>Inception</b> framework.
 *
 * <p>This configuration class disables the default application data source and Camunda Process
 * Engine bootstrapping using the component scan filters.
 *
 * @author Marcus Portmann
 */
@Configuration
@EnableAsync
@EnableCaching
@EnableConfigurationProperties
@EnableScheduling
@EnableTransactionManagement
@ComponentScan(
    basePackages = {"africa.absa.inception"},
    lazyInit = true,
    excludeFilters = {
      @ComponentScan.Filter(value = SpringBootApplication.class, type = FilterType.ANNOTATION),
      @ComponentScan.Filter(
          pattern = "digital\\.inception\\.bmi\\.BMIConfiguration",
          type = FilterType.REGEX)
    })
@SuppressWarnings("WeakerAccess")
public class TestConfiguration {

  private static final Object dataSourceLock = new Object();

  /* Logger */
  private static final Logger logger = LoggerFactory.getLogger(TestConfiguration.class);

  private static DataSource dataSource;

  private final ApplicationContext applicationContext;

  /** Execute the Liquibase changelogs using the data context. */
  @Value("${inception.application.data-source.liquibase.apply-data-context:#{true}}")
  private boolean liquibaseApplyDataContext;

  /**
   * The Liquibase changelog resources on the classpath used to initialize the application database.
   */
  @Value("classpath*:**/db/*.changelog.xml")
  private Resource[] liquibaseChangelogResources;

  /**
   * Constructs a new <b>TestConfiguration</b>.
   *
   * @param applicationContext the Spring application context
   */
  public TestConfiguration(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  /**
   * Initialize the in-memory application database and return a data source that can be used to
   * interact with the database.
   *
   * <p>This data source returned by this method must be closed after use with the <b>close()</b>
   * method.
   *
   * @return the data source that can be used to interact with the in-memory database
   */
  @Bean
  @Primary
  public DataSource applicationDataSource() {
    synchronized (dataSourceLock) {
      if (dataSource == null) {
        try {
          Properties agroalProperties = new Properties();
          agroalProperties.setProperty(
              AgroalPropertiesReader.JDBC_URL,
              "jdbc:h2:mem:"
                  + Thread.currentThread().getName()
                  + ";AUTOCOMMIT=OFF;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE");

          agroalProperties.setProperty(
              AgroalPropertiesReader.PROVIDER_CLASS_NAME, "org.h2.jdbcx.JdbcDataSource");
          agroalProperties.setProperty(AgroalPropertiesReader.MAX_SIZE, "5");

          AgroalPropertiesReader agroalReaderProperties2 =
              new AgroalPropertiesReader().readProperties(agroalProperties);
          AgroalDataSourceConfigurationSupplier agroalDataSourceConfigurationSupplier =
              agroalReaderProperties2.modify();

          TransactionIntegration transactionIntegration =
              applicationContext.getBean(TransactionIntegration.class);

          if (transactionIntegration != null) {
            agroalDataSourceConfigurationSupplier
                .connectionPoolConfiguration()
                .transactionIntegration(transactionIntegration);
          }

          dataSource =
              new DataSourceProxy(AgroalDataSource.from(agroalDataSourceConfigurationSupplier));

          // Initialize the in-memory database using Liquibase changeSets
          LiquibaseConfiguration.getInstance()
              .getConfiguration(HubConfiguration.class)
              .setLiquibaseHubMode("OFF");

          try (Connection connection = dataSource.getConnection()) {
            liquibase.database.Database database =
                DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            for (Resource changelogResource : liquibaseChangelogResources) {
              if (!changelogResource.getFilename().toLowerCase().endsWith("-data.changelog.xml")) {
                String changelogFile = "db/" + changelogResource.getFilename();

                logger.info("Applying Liquibase changelog: " + changelogResource.getFilename());

                Liquibase liquibase =
                    new Liquibase(changelogFile, new ClassLoaderResourceAccessor(), database);

                liquibase.update(new Contexts(), new LabelExpression());
              }
            }

            for (Resource changelogResource : liquibaseChangelogResources) {
              if (changelogResource.getFilename().toLowerCase().endsWith("-data.changelog.xml")) {
                String changelogFile = "db/" + changelogResource.getFilename();

                logger.info(
                    "Applying Liquibase data changelog: " + changelogResource.getFilename());

                Liquibase liquibase =
                    new Liquibase(changelogFile, new ClassLoaderResourceAccessor(), database);

                liquibase.update(new Contexts(), new LabelExpression());
              }
            }
          }
        } catch (Throwable e) {
          throw new RuntimeException("Failed to initialize the in-memory application database", e);
        }
      }

      return dataSource;
    }
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
   * Returns the cache manager.
   *
   * @return the cache manager
   */
  @Bean
  public CacheManager cacheManager() {
    return new ConcurrentMapCacheManager();
  }

  /**
   * Returns the Jackson2 object mapper.
   *
   * @return the Jackson2 object mapper
   */
  @Bean
  public ObjectMapper objectMapper() {
    return jackson2ObjectMapperBuilder().build().disable(SerializationFeature.INDENT_OUTPUT);
  }

  /**
   * Returns the Spring task executor to use for @Async method invocations.
   *
   * @return the Spring task executor to use for @Async method invocations
   */
  @Bean
  public Executor taskExecutor() {
    return new SimpleAsyncTaskExecutor();
  }

  /**
   * Returns the Spring task scheduler.
   *
   * @return the Spring task scheduler
   */
  @Bean
  public TaskScheduler taskScheduler() {
    return new ConcurrentTaskScheduler();
  }

  /**
   * Returns the WebClient.Builder.
   *
   * @return the default WebClient.Builder
   */
  @Bean
  public WebClient.Builder webClientBuilder() {

    // TODO: Configure with security if available -- MARCUS

    return WebClient.builder();
  }

  /**
   * Returns the <b>Jackson2ObjectMapperBuilder</b> bean, which configures the Jackson JSON
   * processor package.
   *
   * @return the <b>Jackson2ObjectMapperBuilder</b> bean, which configures the Jackson JSON
   *     processor package
   */
  protected Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
    Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder = new Jackson2ObjectMapperBuilder();
    jackson2ObjectMapperBuilder.indentOutput(true);

    /*
     * Install the custom Jackson module that supports serializing and de-serializing ISO 8601 date
     * and date/time values. The jackson-datatype-jsr310 module provided by Jackson was not used as
     * it does not handle timezones correctly for LocalDateTime objects.
     */
    jackson2ObjectMapperBuilder.modulesToInstall(new DateTimeModule());

    return jackson2ObjectMapperBuilder;
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
