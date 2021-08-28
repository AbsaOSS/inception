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

package africa.absa.inception.oauth2.server.resource;

import java.security.interfaces.RSAPublicKey;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/**
 * The <b>OAuth2ResourceServerConfiguration</b> class provides the Spring configuration for the
 * OAuth2 Resource Server module.
 *
 * @author Marcus Portmann
 */
@Configuration
public class OAuth2ResourceServerConfiguration {

  /* The RSA public key used to verify the JWTs. */
  @Value("${inception.oauth2.resource-server.jwt.rsa-public-key-location}")
  private RSAPublicKey rsaPublicKey;

  @Bean
  JwtDecoder jwtDecoderByRSAPublicKeyValue() throws Exception {
    if (rsaPublicKey == null) {
      throw new BeanInitializationException(
          "Failed to retrieve the RSA public key using the "
              + "inception.oauth2.resource-server.jwt.rsa-public-key-location property "
              + "to initialize the JwtDecoder bean");
    }

    try {
      return NimbusJwtDecoder.withPublicKey(rsaPublicKey)
          .signatureAlgorithm(SignatureAlgorithm.from("RS256"))
          .build();
    } catch (Throwable e) {
      throw new BeanInitializationException(
          "Failed to initialize the JwtDecoder bean using the RSA public key ("
              + rsaPublicKey
              + ")",
          e);
    }
  }
}
