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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.dom.handler.RequestData;
import org.apache.wss4j.dom.handler.WSHandlerConstants;

/**
 * The <b>CXFWSSX509CertificateTokenProfileProxyConfigurator</b> class provides the capability to
 * configure a CXF web service proxy to support authentication using the Web Services Security X.509
 * Certificate Token profile.
 *
 * @author Marcus Portmann
 */
public class CXFWSSX509CertificateTokenProfileProxyConfigurator {

  /**
   * Configure the CXF web service proxy to support authentication using the the Web Services
   * Security X.509 Certificate Token profile.
   *
   * @param proxy the web service proxy to configure
   * @param keyStore the key store containing the private key and certificate (public key) that will
   *     be used to authenticate
   * @param keyStorePassword the password for the key store that will be used to authenticate
   * @param keyStoreAlias the alias of the key-pair in the key store that will be used to
   *     authenticate
   * @param trustStore the key store containing the certificates that will be used to verify the
   *     remote web service
   */
  public static void configureProxy(
      Object proxy,
      KeyStore keyStore,
      String keyStorePassword,
      String keyStoreAlias,
      KeyStore trustStore) {
    InvocationHandler invocationHandler = Proxy.getInvocationHandler(proxy);

    if (invocationHandler instanceof ClientProxy) {
      ClientProxy clientProxy = (ClientProxy) invocationHandler;

      Client client = clientProxy.getClient();

      Crypto crypto = new Crypto(keyStore, keyStorePassword, trustStore);

      // Enable timestamp and signature verification for the web service responses
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

      client.getInInterceptors().add(wss4JInInterceptor);

      // Enable timestamp and signature verification for the web service requests
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
          new WSS4JOutInterceptor(outProperties) {
            @Override
            protected org.apache.wss4j.common.crypto.Crypto loadCryptoFromPropertiesFile(
                String propFilename, RequestData reqData) {
              return crypto;
            }
          };

      client.getOutInterceptors().add(wss4JOutInterceptor);
    }
  }
}
