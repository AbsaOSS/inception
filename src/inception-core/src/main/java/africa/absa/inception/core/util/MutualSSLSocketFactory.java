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

package africa.absa.inception.core.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyStore;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * The <b>MutualSSLSocketFactory</b> class implements the custom SSL socket factory that supports
 * Mutual SSL authentication.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class MutualSSLSocketFactory extends SSLSocketFactory {

  private final SSLSocketFactory socketFactory;

  /**
   * Constructs a new <b>MutualSSLSocketFactory</b>
   *
   * @param keyStore the key store containing the client certificate and private key
   * @param keyStorePassword the password required to access the key store
   * @param trustStore the trust store
   * @param disableServerTrustChecking disable server trust checking
   */
  public MutualSSLSocketFactory(
      KeyStore keyStore,
      String keyStorePassword,
      KeyStore trustStore,
      boolean disableServerTrustChecking) {
    try {
      // Create a trust manager that does not validate certificate chains
      TrustManager[] trustAllCerts =
          new TrustManager[] {
            new X509TrustManager() {
              public void checkClientTrusted(X509Certificate[] chain, String authType)
                  throws CertificateException {
                // Skip client verification step
              }

              public void checkServerTrusted(X509Certificate[] chain, String authType)
                  throws CertificateException {
                // Skip server verification step
              }

              public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
              }
            }
          };

      // Setup the key manager for the mutual SSL socket factory
      KeyManagerFactory keyManagerFactory =
          KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

      keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

      // Setup the trust manager for the mutual SSL socket factory
      TrustManagerFactory trustManagerFactory =
          TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

      trustManagerFactory.init(trustStore);

      // Setup the SSL contxt
      SSLContext sslContext = SSLContext.getInstance("TLS");

      if (disableServerTrustChecking) {
        sslContext.init(keyManagerFactory.getKeyManagers(), trustAllCerts, new SecureRandom());
      } else {
        sslContext.init(
            keyManagerFactory.getKeyManagers(),
            trustManagerFactory.getTrustManagers(),
            new SecureRandom());
      }

      HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

      /*
       * Retrieve the socket factory from the SSL context that will be used to create the MutualSSL
       * connections.
       */
      socketFactory = sslContext.getSocketFactory();
    } catch (Throwable e) {
      throw new MutualSSLSocketFactoryException(
          "Failed to initialize the Mutual SSL socket factory", e);
    }
  }

  /**
   * Returns <b>true</b> if we are running under the IBM JDK or <b>false</b> otherwise.
   *
   * @return <b>true</b> if we are running under the IBM JDK or <b>false</b> otherwise
   */
  public static boolean isIBMJDK() {
    for (Provider provider : Security.getProviders()) {
      if (provider.getName().startsWith("IBMJSSE")) {
        return true;
      }
    }

    return false;
  }

  /**
   * Creates a socket and connects it to the specified port number at the specified address.
   *
   * <p>This socket is configured using the socket options established for this factory. If there is
   * a security manager, its <b>checkConnect</b> method is called with the host address and port as
   * its arguments. This could result in a <b>SecurityException</b>.
   *
   * @param host the address of the server host
   * @param port the server port
   * @return a socket connected to the specified host and port
   */
  @Override
  public Socket createSocket(InetAddress host, int port) throws IOException {
    return socketFactory.createSocket(host, port);
  }

  /**
   * Creates a socket and connects it to the specified remote host at the specified remote port.
   *
   * <p>This socket is configured using the socket options established for this factory. If there is
   * a security manager, its <b>checkConnect</b> method is called with the host address and port as
   * its arguments. This could result in a <b>SecurityException</b>.
   *
   * @param host the server host
   * @param port the server port
   * @return a socket connected to the specified host and port
   */
  @Override
  public Socket createSocket(String host, int port) throws IOException {
    return socketFactory.createSocket(host, port);
  }

  /**
   * Creates a socket and connects it to the specified port number at the specified address. The
   * socket will also be bound to the local address and port supplied.
   *
   * <p>This socket is configured using the socket options established for this factory. If there is
   * a security manager, its <b>checkConnect</b> method is called with the host address and port as
   * its arguments. This could result in a <b>SecurityException</b>.
   *
   * @param host the address of the server host
   * @param port the server port
   * @param localHost the local address
   * @param localPort the local port
   * @return a socket connected to the specified host and port
   */
  @Override
  public Socket createSocket(InetAddress host, int port, InetAddress localHost, int localPort)
      throws IOException {
    return socketFactory.createSocket(host, port, localHost, localPort);
  }

  /**
   * Returns a socket layered over an existing socket connected to the named host, at the given
   * port.
   *
   * <p>This constructor can be used when tunneling SSL through a proxy or when negotiating the use
   * of SSL over an existing socket. The host and port refer to the logical peer destination. This
   * socket is configured using the socket options established for this factory.
   *
   * @param s the existing socket
   * @param host the server host
   * @param port the server port
   * @param autoClose close the underlying socket when this socket is closed
   * @return a socket connected to the specified host and port
   */
  @Override
  public Socket createSocket(Socket s, String host, int port, boolean autoClose)
      throws IOException {
    return socketFactory.createSocket(s, host, port, autoClose);
  }

  /**
   * Creates a socket and connects it to the specified remote host at the specified remote port. The
   * socket will also be bound to the local address and port supplied.
   *
   * <p>This socket is configured using the socket options established for this factory. If there is
   * a security manager, its <b>checkConnect</b> method is called with the host address and port as
   * its arguments. This could result in a <b>SecurityException</b>.
   *
   * @param host the server host
   * @param port the server port
   * @param localHost the local address
   * @param localPort the local port
   * @return a socket connected to the specified host and port
   */
  @Override
  public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
      throws IOException {
    return socketFactory.createSocket(host, port, localHost, localPort);
  }

  /**
   * Returns the cipher suites which are enabled by default.
   *
   * <p>Unless a different list is enabled, handshaking on an SSL connection will use one of these
   * cipher suites. The minimum quality of service for these defaults requires confidentiality
   * protection and server authentication (that is, no anonymous cipher suites).
   *
   * @return the cipher suites which are enabled by default
   */
  @Override
  public String[] getDefaultCipherSuites() {
    return socketFactory.getDefaultCipherSuites();
  }

  /**
   * Returns the names of the cipher suites which could be enabled for use on an SSL connection.
   *
   * <p>Normally, only a subset of these will actually be enabled by default, since this list may
   * include cipher suites which do not meet quality of service requirements for those defaults.
   * Such cipher suites are useful in specialized applications.
   *
   * @return the names of the cipher suites which could be enabled for use on an SSL connection
   */
  @Override
  public String[] getSupportedCipherSuites() {
    return socketFactory.getSupportedCipherSuites();
  }
}
