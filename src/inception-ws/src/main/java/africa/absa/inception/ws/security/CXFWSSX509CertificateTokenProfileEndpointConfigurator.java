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

package africa.absa.inception.ws.security;

import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import javax.xml.ws.Endpoint;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.handler.RequestData;
import org.apache.wss4j.dom.handler.WSHandlerConstants;

/**
 * The <b>CXFWSSX509CertificateTokenProfileEndpointConfigurator</b> class provides the capability to
 * configure a CXF web service endpoint to support authentication using the Web Services Security
 * X.509 Certificate Token profile.
 *
 * @author Marcus Portmann
 */
public class CXFWSSX509CertificateTokenProfileEndpointConfigurator {

  /**
   * Configure the CXF web service endpoint to support authentication using the the Web Services
   * Security X.509 Certificate Token profile.
   *
   * @param endpoint the web service endpoint to configure
   * @param keyStore the key store containing the private key and certificate (public key) for the
   *     web service
   * @param keyStorePassword the password for the key store containing the private key and
   *     certificate (public key) for the web service
   * @param keyStoreAlias the alias of the key-pair in the key store for the web service
   * @param trustStore the key store containing the certificates that will be used to verify the web
   *     service clients
   */
  public static void configureEndpoint(
      Endpoint endpoint,
      KeyStore keyStore,
      String keyStorePassword,
      String keyStoreAlias,
      KeyStore trustStore) {
    if (endpoint instanceof EndpointImpl) {
      EndpointImpl endpointImpl = (EndpointImpl) endpoint;

      Crypto crypto = new Crypto(keyStore, keyStorePassword, trustStore);

      // Enable timestamp and signature verification for incoming web service requests
      Map<String, Object> inProperties = new HashMap<>();

      inProperties.put(
          WSHandlerConstants.ACTION,
          WSHandlerConstants.TIMESTAMP + " " + WSHandlerConstants.SIGNATURE);
      inProperties.put(WSHandlerConstants.USER, keyStoreAlias);
      inProperties.put(
          WSHandlerConstants.PW_CALLBACK_REF,
          new PasswordCallbackHandler(keyStoreAlias, keyStorePassword));
      inProperties.put(WSHandlerConstants.SIG_PROP_FILE, "INTERNAL");

      WSS4JInInterceptor wss4JInInterceptor =
          new WSS4JInInterceptor(inProperties) {
            @Override
            protected org.apache.wss4j.common.crypto.Crypto loadCryptoFromPropertiesFile(
                String propFilename, RequestData reqData) {
              return crypto;
            }
          };

      endpointImpl.getInInterceptors().add(wss4JInInterceptor);

      // Enable timestamp and signature verification for outgoing web service responses
      Map<String, Object> outProperties = new HashMap<>();

      outProperties.put(
          WSHandlerConstants.ACTION,
          WSHandlerConstants.TIMESTAMP + " " + WSHandlerConstants.SIGNATURE);
      outProperties.put(WSHandlerConstants.USER, keyStoreAlias);
      outProperties.put(
          WSHandlerConstants.PW_CALLBACK_REF,
          new PasswordCallbackHandler(keyStoreAlias, keyStorePassword));
      outProperties.put(WSHandlerConstants.SIG_PROP_FILE, "INTERNAL");

      WSS4JOutInterceptor wss4JOutInterceptor =
          new WSS4JOutInterceptor(inProperties) {
            @Override
            protected org.apache.wss4j.common.crypto.Crypto loadCryptoFromPropertiesFile(
                String propFilename, RequestData reqData) throws WSSecurityException {
              return crypto;
            }
          };

      endpointImpl.getOutInterceptors().add(wss4JOutInterceptor);
    }
  }
}
