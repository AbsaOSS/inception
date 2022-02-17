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

package africa.absa.inception.ws;

import africa.absa.inception.core.configuration.ConfigurationException;
import africa.absa.inception.core.util.CryptoUtil;
import africa.absa.inception.ws.security.CXFWSSX509CertificateTokenProfileEndpointConfigurator;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * The <b>InceptionWebServiceConfiguration</b> class provides the configuration for the
 * <b>inception-ws</b> library.
 *
 * @author Marcus Portmann
 */
@Configuration
public class InceptionWebServiceConfiguration {

  /* Logger */
  private static final Logger logger =
      LoggerFactory.getLogger(InceptionWebServiceConfiguration.class);

  /** The Spring application context */
  private final ApplicationContext applicationContext;

  /** Is the Web Services Security X.509 Certificate Token Profile enabled? */
  @Value("${inception.ws.security.x509-certificate-token-profile.enabled:#{false}}")
  private boolean isWSSX509CertificateTokenProfileEnabled;

  /** The Web Services Security X.509 Certificate Token Profile key store alias. */
  @Value("${inception.ws.security.x509-certificate-token-profile.key-alias:#{null}}")
  private String wssX509CertificateTokenProfileKeyStoreAlias;

  /** The Web Services Security X.509 Certificate Token Profile key store password. */
  @Value("${inception.ws.security.x509-certificate-token-profile.key-store-password:#{null}}")
  private String wssX509CertificateTokenProfileKeyStorePassword;

  /** The Web Services Security X.509 Certificate Token Profile key store path. */
  @Value("${inception.ws.security.x509-certificate-token-profile.key-store:#{null}}")
  private String wssX509CertificateTokenProfileKeyStorePath;

  /** The Web Services Security X.509 Certificate Token Profile key store type. */
  @Value("${inception.ws.security.x509-certificate-token-profile.key-store-type:#{null}}")
  private String wssX509CertificateTokenProfileKeyStoreType;

  /** The optional Web Services Security X.509 Certificate Token Profile trust store password. */
  @Value("${inception.ws.security.x509-certificate-token-profile.trust-store-password:#{null}}")
  private String wssX509CertificateTokenProfileTrustStorePassword;

  /** The optional Web Services Security X.509 Certificate Token Profile trust store path. */
  @Value("${inception.ws.security.x509-certificate-token-profile.trust-store:#{null}}")
  private String wssX509CertificateTokenProfileTrustStorePath;

  /** The optional Web Services Security X.509 Certificate Token Profile trust store type. */
  @Value("${inception.ws.security.x509-certificate-token-profile.trust-store-type:#{null}}")
  private String wssX509CertificateTokenProfileTrustStoreType;

  /**
   * Constructs a new <b>InceptionWSConfiguration</b>.
   *
   * @param applicationContext the Spring application context
   */
  public InceptionWebServiceConfiguration(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  /**
   * Returns the web services bean factory post processor.
   *
   * @return web services bean factory post processor
   */
  @Bean
  protected static BeanFactoryPostProcessor webServicesBeanFactoryPostProcessor() {
    return beanFactory -> beanFactory.registerSingleton("cxf", new SpringBus());
  }

  /**
   * Returns the Apache CXF servlet registration bean.
   *
   * @return the Apache CXF servlet registration bean
   */
  @Bean
  public ServletRegistrationBean<CXFServlet> cxfServletRegistrationBean() {
    CXFServlet cxfServlet = new CXFServlet();

    return new ServletRegistrationBean<>(cxfServlet, "/service/*");
  }

  /**
   * Create the web service endpoint.
   *
   * @param name the web service name
   * @param implementation the web service implementation
   */
  protected void createWebServiceEndpoint(String name, Object implementation) {
    try {
      SpringBus springBus = applicationContext.getBean(SpringBus.class);

      Endpoint endpoint = new EndpointImpl(springBus, implementation);

      endpoint.publish("/" + name);

      if (isWSSX509CertificateTokenProfileEnabled) {
        if (!StringUtils.hasText(wssX509CertificateTokenProfileKeyStoreType)) {
          throw new ConfigurationException(
              "The type was not specified for the server SSL key store");
        }

        if (!StringUtils.hasText(wssX509CertificateTokenProfileKeyStorePath)) {
          throw new ConfigurationException(
              "The path was not specified for the server SSL key store");
        }

        if (!StringUtils.hasText(wssX509CertificateTokenProfileKeyStorePassword)) {
          throw new ConfigurationException(
              "The password was not specified for the server SSL key store");
        }

        if (!StringUtils.hasText(wssX509CertificateTokenProfileKeyStoreAlias)) {
          throw new ConfigurationException(
              "The alias was not specified for the server SSL key store");
        }

        KeyStore keyStore;

        try {
          keyStore =
              CryptoUtil.loadKeyStore(
                  wssX509CertificateTokenProfileKeyStoreType,
                  wssX509CertificateTokenProfileKeyStorePath,
                  wssX509CertificateTokenProfileKeyStorePassword,
                  wssX509CertificateTokenProfileKeyStoreAlias);
        } catch (Throwable e) {
          throw new WebServiceInitializationException(
              "Failed to initialize the Web Services Security X.509 Certificate Token Profile key store",
              e);
        }

        KeyStore trustStore = keyStore;

        if ((StringUtils.hasText(wssX509CertificateTokenProfileTrustStoreType))
            || (StringUtils.hasText(wssX509CertificateTokenProfileTrustStorePath))
            || (StringUtils.hasText(wssX509CertificateTokenProfileTrustStorePassword))) {
          if (!StringUtils.hasText(wssX509CertificateTokenProfileTrustStoreType)) {
            throw new ConfigurationException(
                "The type was not specified for the Web Services Security X.509 Certificate Token Profile trust store");
          }

          if (!StringUtils.hasText(wssX509CertificateTokenProfileTrustStorePath)) {
            throw new ConfigurationException(
                "The path was not specified for the Web Services Security X.509 Certificate Token Profile trust store");
          }

          if (!StringUtils.hasText(wssX509CertificateTokenProfileTrustStorePassword)) {
            throw new ConfigurationException(
                "The password was not specified for the Web Services Security X.509 Certificate Token Profile trust store");
          }

          try {
            trustStore =
                CryptoUtil.loadTrustStore(
                    wssX509CertificateTokenProfileTrustStoreType,
                    wssX509CertificateTokenProfileTrustStorePath,
                    wssX509CertificateTokenProfileTrustStorePassword);
          } catch (Throwable e) {
            throw new WebServiceInitializationException(
                "Failed to initialize the Web Services Security X.509 Certificate Token Profile key store",
                e);
          }
        }

        CXFWSSX509CertificateTokenProfileEndpointConfigurator.configureEndpoint(
            endpoint,
            keyStore,
            wssX509CertificateTokenProfileKeyStorePassword,
            wssX509CertificateTokenProfileKeyStoreAlias,
            trustStore);
      }
    } catch (Throwable e) {
      throw new WebServiceInitializationException(
          "Failed to create the endpoint for the web service (" + name + ")", e);
    }
  }

  /** Initialize the web services. */
  @PostConstruct
  private void initializeWebServices() {
    List<String> packagesToScanForWebServices = new ArrayList<>();

    packagesToScanForWebServices.add("africa.absa.inception");

    for (Object annotated :
        applicationContext.getBeansWithAnnotation(ComponentScan.class).values()) {
      Class clazz = ClassUtils.getUserClass(annotated);

      ComponentScan componentScan =
          AnnotatedElementUtils.getMergedAnnotation(clazz, ComponentScan.class);
      if (componentScan != null) { // For some reasons, this might still be null.
        for (String basePackage : componentScan.basePackages()) {
          // Replace any existing packages to scan with the higher level package
          packagesToScanForWebServices.removeIf(
              packageToScanForWebServices -> packageToScanForWebServices.startsWith(basePackage));

          // Check if there is a higher level package already being scanned
          if (packagesToScanForWebServices.stream().noneMatch(basePackage::startsWith)) {
            packagesToScanForWebServices.add(basePackage);
          }
        }
      }
    }

    ClassPathScanningCandidateComponentProvider scanner =
        new ClassPathScanningCandidateComponentProvider(false);

    scanner.addIncludeFilter(new AnnotationTypeFilter(WebService.class));

    logger.info(
        "Scanning the following packages for web services: "
            + StringUtils.collectionToDelimitedString(packagesToScanForWebServices, ","));

    for (String packageToScanForWebServices : packagesToScanForWebServices) {
      for (BeanDefinition beanDefinition :
          scanner.findCandidateComponents(packageToScanForWebServices)) {
        try {
          if (beanDefinition.getBeanClassName() != null) {
            Class<?> webServiceClass = ClassUtils.forName(beanDefinition.getBeanClassName(), null);

            WebService webServiceAnnotation = webServiceClass.getAnnotation(WebService.class);

            Object webServiceImplementation =
                applicationContext.getAutowireCapableBeanFactory().createBean(webServiceClass);

            createWebServiceEndpoint(webServiceAnnotation.serviceName(), webServiceImplementation);
          }
        } catch (Throwable e) {
          throw new WebServiceInitializationException(
              "Failed to initialize the web service (" + beanDefinition.getBeanClassName() + ")",
              e);
        }
      }
    }
  }
}
