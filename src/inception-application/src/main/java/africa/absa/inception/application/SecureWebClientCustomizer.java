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

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import reactor.netty.http.client.HttpClient;

/**
 * The <b>SecureWebClientCustomizer</b> class implements the callback interface that can be used to
 * customize the {@link org.springframework.web.reactive.function.client.WebClient.Builder
 * WebClient.Builder} to apply the security configuration.
 *
 * @author Marcus Portmann
 */
@Component
@ConditionalOnBean(ApplicationSecurityConfiguration.class)
public class SecureWebClientCustomizer implements WebClientCustomizer {

  /** The application security configuration. */
  ApplicationSecurityConfiguration applicationSecurityConfiguration;

  /**
   * Constructs a new <b>SecureWebClientCustomizer</b>.
   *
   * @param applicationSecurityConfiguration the application security configuration
   */
  public SecureWebClientCustomizer(
      ApplicationSecurityConfiguration applicationSecurityConfiguration) {
    this.applicationSecurityConfiguration = applicationSecurityConfiguration;
  }

  @Override
  public void customize(Builder webClientBuilder) {
    try {
      KeyManagerFactory keyManagerFactory = null;

      if (applicationSecurityConfiguration.hasKeyStoreConfiguration()) {
        keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

        keyManagerFactory.init(
            applicationSecurityConfiguration.keyStore(),
            applicationSecurityConfiguration.keyStorePassword().toCharArray());
      }

      TrustManagerFactory trustManagerFactory = null;

      if (applicationSecurityConfiguration.hasTrustStoreConfiguration()) {
        trustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

        trustManagerFactory.init(applicationSecurityConfiguration.trustStore());
      }

      SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();

      if (keyManagerFactory != null) {
        sslContextBuilder.keyManager(keyManagerFactory);
      }

      if (trustManagerFactory != null) {
        sslContextBuilder.trustManager(trustManagerFactory);
      }

      SslContext sslContext = sslContextBuilder.build();

      HttpClient httpClient =
          HttpClient.create().secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));

      ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
      webClientBuilder.clientConnector(connector);

    } catch (Throwable e) {
      throw new RuntimeException(
          "Failed to apply the security configuration to the web client builder", e);
    }
  }
}
