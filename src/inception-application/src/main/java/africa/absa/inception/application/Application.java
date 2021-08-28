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

import africa.absa.inception.core.support.MergedMessageSource;
import africa.absa.inception.json.DateTimeModule;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * The <b>Application</b> class provides the class that all application-specific application classes
 * should be derived from.
 *
 * @author Marcus Portmann
 */
@Component
@ComponentScan(
    basePackages = {"africa.absa.inception"},
    lazyInit = true)
@EnableAsync
@EnableScheduling
@SuppressWarnings({"unused"})
public abstract class Application implements WebApplicationInitializer {

  /* Logger */
  private static final Logger logger = LoggerFactory.getLogger(Application.class);

  /** The Spring application context. */
  private final ApplicationContext applicationContext;

  /** The distributed in-memory caches. */
  Map<String, Map<?, ?>> caches = new ConcurrentHashMap<>();

  /**
   * Constructs a new <b>Application</b>.
   *
   * @param applicationContext the Spring application context
   */
  protected Application(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  /**
   * Returns the Spring application context.
   *
   * @return the Spring application context
   */
  public ApplicationContext getApplicationContext() {
    return applicationContext;
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

  @Override
  public void onStartup(ServletContext container) {
    // Create the 'root' Spring application context
    AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();

    container.addListener(new ContextLoaderListener(rootContext));
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
   * Returns the application message source.
   *
   * @return the application message source
   */
  @Bean
  protected MessageSource messageSource() {
    MergedMessageSource messageSource = new MergedMessageSource();
    messageSource.setBasename("classpath*:messages");

    return messageSource;
  }

  /**
   * Returns the metric registry.
   *
   * @return the metric registry
   */
  @Bean
  protected MetricRegistry metricRegistry() {
    return new MetricRegistry();
  }

  /**
   * Returns the Spring task executor to use for @Async method invocations.
   *
   * @return the Spring task executor to use for @Async method invocations
   */
  @Bean
  protected Executor taskExecutor() {
    return new SimpleAsyncTaskExecutor();
  }

  /**
   * Returns the Spring task scheduler.
   *
   * @return the Spring task scheduler
   */
  @Bean
  protected TaskScheduler taskScheduler() {
    return new ConcurrentTaskScheduler();
  }
}
