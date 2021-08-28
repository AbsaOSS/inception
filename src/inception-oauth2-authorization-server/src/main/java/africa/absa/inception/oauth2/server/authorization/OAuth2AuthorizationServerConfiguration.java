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

package africa.absa.inception.oauth2.server.authorization;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * The <b>OAuth2AuthorizationServerConfiguration</b> class provides the Spring configuration for the
 * OAuth2 Authorization Server module.
 *
 * @author Marcus Portmann
 */
@Configuration
public class OAuth2AuthorizationServerConfiguration {

  /* The RSA private key used to sign the JWTs. */
  @Value("${inception.oauth2.authorization-server.jwt.rsa-private-key-location}")
  private RSAPrivateKey rsaPrivateKey;

  /* The RSA public key used to verify the JWTs. */
  @Value("${inception.oauth2.authorization-server.jwt.rsa-public-key-location}")
  private RSAPublicKey rsaPublicKey;
}
