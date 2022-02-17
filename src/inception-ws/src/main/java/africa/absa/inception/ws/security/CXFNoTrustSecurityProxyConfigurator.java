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
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.http.HTTPConduit;

/**
 * The <b>CXFNoTrustSecurityProxyConfigurator</b> class provides the capability to configure a CXF
 * web service proxy to disable verification of the remote server's certificate when using a HTTPS
 * connection.
 *
 * @author Marcus Portmann
 */
public class CXFNoTrustSecurityProxyConfigurator {

  /**
   * Configure the CXF web service proxy to disable verification of the remote server's certificate
   * when using a HTTPS connection.
   *
   * @param proxy the web service proxy to configure
   */
  public static void configureProxy(Object proxy) {
    InvocationHandler invocationHandler = Proxy.getInvocationHandler(proxy);

    if (invocationHandler instanceof ClientProxy) {
      ClientProxy clientProxy = (ClientProxy) invocationHandler;

      Conduit conduit = clientProxy.getClient().getConduit();

      if (conduit instanceof HTTPConduit) {
        HTTPConduit httpConduit = (HTTPConduit) conduit;

        TLSClientParameters tlsClientParameters = new TLSClientParameters();

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

        httpConduit.setTlsClientParameters(tlsClientParameters);
      }
    }
  }
}
