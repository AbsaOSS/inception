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
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.http.HTTPConduit;

/**
 * The <b>CXFMutualSSLSecurityProxyConfigurator</b> class provides the capability to configure a CXF
 * web service proxy for mutual SSL authentication.
 *
 * @author Marcus Portmann
 */
public class CXFMutualSSLSecurityProxyConfigurator {

  /**
   * Configure the CXF web service proxy to support mutual SSL authentication.
   *
   * @param proxy the web service proxy to configure
   * @param keyStore the key store containing the private key and certificate (public key) to use
   *     when authenticating using mutual SSL authentication
   * @param keyStorePassword the password for the key store that will be used when authenticating
   *     using mutual SSL authentication
   * @param trustStore the key store containing the certificates that will be used to verify the
   *     remote server when authenticating using mutual SSL authentication
   * @param disableServerTrustChecking disable trust checking of the remote server certificate
   * @throws Exception if the proxy could not be configured
   */
  public static void configureProxy(
      Object proxy,
      KeyStore keyStore,
      String keyStorePassword,
      KeyStore trustStore,
      boolean disableServerTrustChecking)
      throws Exception {
    InvocationHandler invocationHandler = Proxy.getInvocationHandler(proxy);

    if (invocationHandler instanceof ClientProxy) {
      ClientProxy clientProxy = (ClientProxy) invocationHandler;

      Conduit conduit = clientProxy.getClient().getConduit();

      if (conduit instanceof HTTPConduit) {
        HTTPConduit httpConduit = (HTTPConduit) conduit;

        TLSClientParameters tlsClientParameters = new TLSClientParameters();

        // Setup the key manager for the client SSL socket factory
        KeyManagerFactory keyManagerFactory =
            KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

        keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

        tlsClientParameters.setKeyManagers(keyManagerFactory.getKeyManagers());

        if (disableServerTrustChecking) {
          // Create a trust manager that does not validate certificate chains
          TrustManager[] trustAllCerts =
              new TrustManager[] {
                new X509TrustManager() {
                  public void checkClientTrusted(X509Certificate[] chain, String authType)
                      throws CertificateException {
                    // Skip client verification
                  }

                  public void checkServerTrusted(X509Certificate[] chain, String authType)
                      throws CertificateException {
                    // Skip server verification
                  }

                  public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                  }
                }
              };

          tlsClientParameters.setTrustManagers(trustAllCerts);
        } else {
          TrustManagerFactory trustManagerFactory =
              TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

          trustManagerFactory.init(trustStore);

          tlsClientParameters.setTrustManagers(trustManagerFactory.getTrustManagers());
        }

        httpConduit.setTlsClientParameters(tlsClientParameters);
      }
    }
  }
}
