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

import africa.absa.inception.core.util.MutualSSLSocketFactory;
import africa.absa.inception.core.util.NoTrustSSLSocketFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.KeyStore;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.handler.HandlerResolver;

/**
 * The <b>WebServiceClientSecurityHelper</b> class is a utility class that provides support for
 * configuring secure web service clients.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings("unused")
public class WebServiceClientSecurityHelper {

  /**
   * The name of the internal JAX-WS property that allows the SSL socket factory to be configured.
   */
  public static final String JAX_WS_INTERNAL_PROPERTIES_SSL_SOCKET_FACTORY =
      "com.sun.xml.internal.ws.transport.https.client.SSLSocketFactory";

  /** The name of the JAX-WS property that allows the SSL socket factory to be configured. */
  public static final String JAX_WS_PROPERTIES_SSL_SOCKET_FACTORY =
      "com.sun.xml.ws.transport.https.client.SSLSocketFactory";

  /* The web service client cache. */
  private static final ConcurrentMap<String, WebServiceClient> webServiceClientCache =
      new ConcurrentHashMap<>();

  private static boolean apacheCxfCheckFailed;

  private static Class apacheCxfClientClass;

  private static Method apacheCxfClientGetConduitMethod;

  private static Class apacheCxfClientProxyClass;

  private static Method apacheCxfClientProxyGetClientMethod;

  private static Method apacheCxfHttpConduitSetTlsClientParametersMethod;

  private static Class apacheCxfTlsClientParametersClass;

  private static Method apacheCxfTlsClientParametersSetDisableCNCheckMethod;

  private static Method apacheCxfTlsParametersBaseSetKeyManagersMethod;

  private static Method apacheCxfTlsParametersBaseSetTrustManagersMethod;

  /**
   * The socket factory used to connect to a web service using SSL without validating the server
   * certificate.
   */
  private static NoTrustSSLSocketFactory noTrustSSLSocketFactory;

  /**
   * Returns the secure web service proxy for the web service that has been secured with digest
   * authentication.
   *
   * @param serviceClass the Java web service client class
   * @param serviceInterface the Java interface for the web service
   * @param wsdlResourcePath the resource path to the WSDL for the web service on the classpath
   * @param serviceEndpoint the URL giving the web service endpoint
   * @param username the username
   * @param password the password
   * @param <T> the Java interface for the web service
   * @return the secure web service proxy for the web service that has been secured with digest
   *     authentication
   * @throws WebServiceClientSecurityException if the web service proxy could not be retrieved
   */
  public static <T> T getDigestAuthenticationServiceProxy(
      Class<?> serviceClass,
      Class<T> serviceInterface,
      String wsdlResourcePath,
      String serviceEndpoint,
      String username,
      String password)
      throws WebServiceClientSecurityException {
    try {
      // First attempt to retrieve the web service client from the cache
      WebServiceClient webServiceClient =
          webServiceClientCache.get(
              serviceClass.getName() + "-" + WebServiceSecurityType.DIGEST_AUTHENTICATION.code());

      if (webServiceClient == null) {
        webServiceClient = getWebServiceClient(serviceClass, wsdlResourcePath);

        webServiceClientCache.put(
            serviceClass.getName() + "-" + WebServiceSecurityType.DIGEST_AUTHENTICATION.code(),
            webServiceClient);
      }

      T proxy =
          webServiceClient.getService().getPort(webServiceClient.getPortQName(), serviceInterface);

      // Set the endpoint for the web service
      BindingProvider bindingProvider = ((BindingProvider) proxy);

      bindingProvider
          .getRequestContext()
          .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceEndpoint);

      CXFDigestSecurityProxyConfigurator.configureProxy(proxy, username, password);

      return proxy;
    } catch (Throwable e) {
      throw new WebServiceClientSecurityException(
          "Failed to retrieve the web service proxy for the web service that has been secured "
              + "using digest authentication",
          e);
    }
  }

  /**
   * Returns the secure web service proxy for the web service that has been secured with basic HTTP
   * authentication.
   *
   * @param serviceClass the Java web service client class
   * @param serviceInterface the Java interface for the web service
   * @param wsdlResourcePath the resource path to the WSDL for the web service on the classpath
   * @param serviceEndpoint the URL giving the web service endpoint
   * @param username the username
   * @param password the password
   * @param <T> the Java interface for the web service
   * @return the secure web service proxy for the web service that has been secured with basic HTTP
   *     authentication
   * @throws WebServiceClientSecurityException if the web service proxy could not be retrieved
   */
  public static <T> T getHTTPAuthenticationServiceProxy(
      Class<?> serviceClass,
      Class<T> serviceInterface,
      String wsdlResourcePath,
      String serviceEndpoint,
      String username,
      String password)
      throws WebServiceClientSecurityException {
    try {
      // First attempt to retrieve the web service client from the cache
      WebServiceClient webServiceClient = webServiceClientCache.get(serviceClass.getName());

      if (webServiceClient == null) {
        webServiceClient = getWebServiceClient(serviceClass, wsdlResourcePath);

        webServiceClientCache.put(serviceClass.getName(), webServiceClient);
      }

      T proxy =
          webServiceClient.getService().getPort(webServiceClient.getPortQName(), serviceInterface);

      // Set the endpoint for the web service
      BindingProvider bindingProvider = ((BindingProvider) proxy);

      bindingProvider
          .getRequestContext()
          .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceEndpoint);

      bindingProvider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
      bindingProvider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);

      CXFBasicSecurityProxyConfigurator.configureProxy(proxy, username, password);

      return proxy;
    } catch (Throwable e) {
      throw new WebServiceClientSecurityException(
          "Failed to retrieve the web service proxy for the web service ("
              + serviceClass.getName()
              + ") that has been secured using basic HTTP authentication",
          e);
    }
  }

  /**
   * Returns the secure web service proxy for the web service that has been secured with transport
   * level security using SSL client authentication.
   *
   * @param serviceClass the Java web service client class
   * @param serviceInterface the Java interface for the web service
   * @param wsdlResourcePath the resource path to the WSDL for the web service on the classpath
   * @param serviceEndpoint the URL giving the web service endpoint
   * @param keyStore the key store containing the private key and certificate (public key) that will
   *     be used to perform the mutual SSL authentication when invoking the web service
   * @param keyStorePassword the password for the key store that will be used to perform the mutual
   *     SSL authentication when invoking the web service
   * @param trustStore the key store containing the certificates that will be used to verify the
   *     remote server when performing mutual SSL authentication
   * @param disableServerTrustChecking disable trust checking of the remote server certificate
   * @param <T> the Java interface for the web service
   * @return the secure web service proxy for the web service that has been secured with transport
   *     level security using mutual SSL authentication
   * @throws WebServiceClientSecurityException if the web service proxy could not be retrieved
   */
  @SuppressWarnings("unchecked")
  public static <T> T getMutualSSLServiceProxy(
      Class<?> serviceClass,
      Class<T> serviceInterface,
      String wsdlResourcePath,
      String serviceEndpoint,
      KeyStore keyStore,
      String keyStorePassword,
      KeyStore trustStore,
      boolean disableServerTrustChecking)
      throws WebServiceClientSecurityException {
    try {
      // First attempt to retrieve the web service client from the cache
      WebServiceClient webServiceClient =
          webServiceClientCache.get(
              serviceClass.getName() + "-" + WebServiceSecurityType.MUTUAL_SSL.code());

      if (webServiceClient == null) {
        webServiceClient = getWebServiceClient(serviceClass, wsdlResourcePath);

        webServiceClientCache.put(
            serviceClass.getName() + "-" + WebServiceSecurityType.MUTUAL_SSL.code(),
            webServiceClient);
      }

      T proxy =
          webServiceClient.getService().getPort(webServiceClient.getPortQName(), serviceInterface);

      // Set the endpoint for the web service
      BindingProvider bindingProvider = ((BindingProvider) proxy);

      bindingProvider
          .getRequestContext()
          .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceEndpoint);

      // Retrieve the Mutual SSL socket factory
      SSLSocketFactory mutualSSLSocketFactory =
          new MutualSSLSocketFactory(
              keyStore, keyStorePassword, trustStore, disableServerTrustChecking);

      /*
       * NOTE: We use two different properties here because different JDKs seem to use the
       *       different property values.
       */
      bindingProvider
          .getRequestContext()
          .put(JAX_WS_PROPERTIES_SSL_SOCKET_FACTORY, mutualSSLSocketFactory);
      bindingProvider
          .getRequestContext()
          .put(JAX_WS_INTERNAL_PROPERTIES_SSL_SOCKET_FACTORY, mutualSSLSocketFactory);

      CXFMutualSSLSecurityProxyConfigurator.configureProxy(
          proxy, keyStore, keyStorePassword, trustStore, disableServerTrustChecking);

      return proxy;
    } catch (Throwable e) {
      throw new WebServiceClientSecurityException(
          "Failed to retrieve the web service proxy for the web service ("
              + serviceClass.getName()
              + ") that has been secured using mutual SSL authentication",
          e);
    }
  }

  /**
   * Returns the web service proxy for the web service that supports SSL without validating the
   * server certificate.
   *
   * @param serviceClass the Java web service client class
   * @param serviceInterface the Java interface for the web service
   * @param wsdlResourcePath the resource path to the WSDL for the web service on the classpath
   * @param serviceEndpoint the URL giving the web service endpoint
   * @param <T> the Java interface for the web service
   * @return the web service proxy for the web service that supports SSL without validating the
   *     server certificate
   * @throws WebServiceClientSecurityException if the web service proxy could not be retrieved
   */
  @SuppressWarnings("unchecked")
  public static <T> T getNoTrustServiceProxy(
      Class<?> serviceClass,
      Class<T> serviceInterface,
      String wsdlResourcePath,
      String serviceEndpoint)
      throws WebServiceClientSecurityException {
    try {
      // First attempt to retrieve the web service client from the cache
      WebServiceClient webServiceClient =
          webServiceClientCache.get(
              serviceClass.getName() + "-" + WebServiceSecurityType.MUTUAL_SSL.code());

      if (webServiceClient == null) {
        webServiceClient = getWebServiceClient(serviceClass, wsdlResourcePath);

        webServiceClientCache.put(
            serviceClass.getName() + "-" + WebServiceSecurityType.MUTUAL_SSL.code(),
            webServiceClient);
      }

      T proxy =
          webServiceClient.getService().getPort(webServiceClient.getPortQName(), serviceInterface);

      // Set the endpoint for the web service
      BindingProvider bindingProvider = ((BindingProvider) proxy);

      bindingProvider
          .getRequestContext()
          .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceEndpoint);

      /*
       * NOTE: We use two different properties here because different JDKs seem to use the
       *       different property values.
       */
      bindingProvider
          .getRequestContext()
          .put(JAX_WS_PROPERTIES_SSL_SOCKET_FACTORY, getNoTrustSSLSocketFactory());
      bindingProvider
          .getRequestContext()
          .put(JAX_WS_INTERNAL_PROPERTIES_SSL_SOCKET_FACTORY, getNoTrustSSLSocketFactory());

      CXFNoTrustSecurityProxyConfigurator.configureProxy(proxy);

      return proxy;
    } catch (Throwable e) {
      throw new WebServiceClientSecurityException(
          "Failed to retrieve the web service proxy for the web service ("
              + serviceClass.getName()
              + ") that has server certificate verification disabled",
          e);
    }
  }

  /**
   * Returns the web service proxy for the unsecured web service.
   *
   * @param serviceClass the Java web service client class
   * @param serviceInterface the Java interface for the web service
   * @param wsdlResourcePath the resource path to the WSDL for the web service on the classpath
   * @param serviceEndpoint the URL giving the web service endpoint
   * @param <T> the Java interface for the web service
   * @return the web service proxy for the unsecured web service
   * @throws WebServiceClientSecurityException if the web service proxy could not be retrieved
   */
  public static <T> T getServiceProxy(
      Class<?> serviceClass,
      Class<T> serviceInterface,
      String wsdlResourcePath,
      String serviceEndpoint)
      throws WebServiceClientSecurityException {
    return getServiceProxy(serviceClass, serviceInterface, wsdlResourcePath, serviceEndpoint, null);
  }

  /**
   * Returns the web service proxy for the unsecured web service.
   *
   * @param serviceClass the Java web service client class
   * @param serviceInterface the Java interface for the web service
   * @param wsdlResourcePath the resource path to the WSDL for the web service on the classpath
   * @param serviceEndpoint the URL giving the web service endpoint
   * @param handlerResolver the web service handler resolver
   * @param <T> the Java interface for the web service
   * @return the web service proxy for the unsecured web service
   * @throws WebServiceClientSecurityException if the web service proxy could not be retrieved
   */
  public static <T> T getServiceProxy(
      Class<?> serviceClass,
      Class<T> serviceInterface,
      String wsdlResourcePath,
      String serviceEndpoint,
      HandlerResolver handlerResolver)
      throws WebServiceClientSecurityException {
    return getServiceProxy(
        serviceClass, serviceInterface, wsdlResourcePath, serviceEndpoint, handlerResolver, false);
  }

  /**
   * Returns the web service proxy for the unsecured web service.
   *
   * @param serviceClass the Java web service client class
   * @param serviceInterface the Java interface for the web service
   * @param wsdlResourcePath the resource path to the WSDL for the web service on the classpath
   * @param serviceEndpoint the URL giving the web service endpoint
   * @param handlerResolver the web service handler resolver
   * @param useClientCache should the web service client cached be used
   * @param <T> the Java interface for the web service
   * @return the web service proxy for the unsecured web service
   * @throws WebServiceClientSecurityException if the web service proxy could not be retrieved
   */
  public static <T> T getServiceProxy(
      Class<?> serviceClass,
      Class<T> serviceInterface,
      String wsdlResourcePath,
      String serviceEndpoint,
      HandlerResolver handlerResolver,
      boolean useClientCache)
      throws WebServiceClientSecurityException {
    try {
      // First attempt to retrieve the web service client from the cache if required
      WebServiceClient webServiceClient = null;

      if (useClientCache) {
        webServiceClient = webServiceClientCache.get(serviceClass.getName());
      }

      if (webServiceClient == null) {
        webServiceClient = getWebServiceClient(serviceClass, wsdlResourcePath);

        if (handlerResolver != null) {
          webServiceClient.getService().setHandlerResolver(handlerResolver);
        }

        if (useClientCache) {
          webServiceClientCache.put(serviceClass.getName(), webServiceClient);
        }
      }

      T proxy =
          webServiceClient.getService().getPort(webServiceClient.getPortQName(), serviceInterface);

      // Set the endpoint for the web service
      BindingProvider bindingProvider = ((BindingProvider) proxy);

      bindingProvider
          .getRequestContext()
          .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceEndpoint);

      return proxy;
    } catch (Throwable e) {
      throw new WebServiceClientSecurityException(
          "Failed to retrieve the web service proxy for the unsecured web service ("
              + serviceClass.getName()
              + ") ",
          e);
    }
  }

  /**
   * Returns the secure web service proxy for the web service that has been secured with message
   * level security using the Web Services Security Username Token profile.
   *
   * @param serviceClass the Java web service client class
   * @param serviceInterface the Java interface for the web service
   * @param wsdlResourcePath the resource path to the WSDL for the web service on the classpath
   * @param serviceEndpoint the URL giving the web service endpoint
   * @param username the username
   * @param password the password
   * @param <T> the Java interface for the web service
   * @return the secure web service proxy for the web service that has been secured with message
   *     level security using the Web Services Security Username Token profile
   * @throws WebServiceClientSecurityException if the web service proxy could not be retrieved
   */
  public static <T> T getWSSecurityUsernameTokenServiceProxy(
      Class<?> serviceClass,
      Class<T> serviceInterface,
      String wsdlResourcePath,
      String serviceEndpoint,
      String username,
      String password)
      throws WebServiceClientSecurityException {
    return getWSSecurityUsernameTokenServiceProxy(
        serviceClass,
        serviceInterface,
        wsdlResourcePath,
        serviceEndpoint,
        username,
        password,
        false);
  }

  /**
   * Returns the secure web service proxy for the web service that has been secured with message
   * level security using the Web Services Security Username Token profile.
   *
   * @param serviceClass the Java web service client class
   * @param serviceInterface the Java interface for the web service
   * @param wsdlResourcePath the resource path to the WSDL for the web service on the classpath
   * @param serviceEndpoint the URL giving the web service endpoint
   * @param username the username
   * @param password the password
   * @param usePlainTextPassword should a plain text password be used
   * @param <T> the Java interface for the web service
   * @return the secure web service proxy for the web service that has been secured with message
   *     level security using the Web Services Security Username Token profile
   * @throws WebServiceClientSecurityException if the web service proxy could not be retrieved
   */
  public static <T> T getWSSecurityUsernameTokenServiceProxy(
      Class<?> serviceClass,
      Class<T> serviceInterface,
      String wsdlResourcePath,
      String serviceEndpoint,
      String username,
      String password,
      boolean usePlainTextPassword)
      throws WebServiceClientSecurityException {
    try {
      // First attempt to retrieve the web service client from the cache
      WebServiceClient webServiceClient =
          webServiceClientCache.get(
              serviceClass.getName()
                  + "-"
                  + WebServiceSecurityType.WSS_SECURITY_USERNAME_TOKEN.code());

      if (webServiceClient == null) {
        webServiceClient = getWebServiceClient(serviceClass, wsdlResourcePath);

        webServiceClientCache.put(
            serviceClass.getName()
                + "-"
                + WebServiceSecurityType.WSS_SECURITY_USERNAME_TOKEN.code(),
            webServiceClient);
      }

      T proxy =
          webServiceClient.getService().getPort(webServiceClient.getPortQName(), serviceInterface);

      // Set the endpoint for the web service
      BindingProvider bindingProvider = ((BindingProvider) proxy);

      bindingProvider
          .getRequestContext()
          .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceEndpoint);

      CXFWSSUsernameTokenProfileProxyConfigurator.configureProxy(
          proxy, username, password, usePlainTextPassword);

      return proxy;
    } catch (Throwable e) {
      throw new WebServiceClientSecurityException(
          "Failed to retrieve the web service proxy for the web service ("
              + serviceClass.getName()
              + ") that has been secured using the Web Services Security Username Token profile",
          e);
    }
  }

  /**
   * Returns the secure web service proxy for the web service that has been secured with message
   * level security using the Web Services Security X.509 Certificate Token profile.
   *
   * @param serviceClass the Java web service client class
   * @param serviceInterface the Java interface for the web service
   * @param wsdlResourcePath the resource path to the WSDL for the web service on the classpath
   * @param serviceEndpoint the URL giving the web service endpoint
   * @param keyStore the key store containing the private key and certificate (public key)
   * @param keyStorePassword the password for the key store
   * @param keyStoreAlias the alias of the key-pair in the key store
   * @param <T> the Java interface for the web service
   * @return the secure web service proxy for the web service that has been secured with message
   *     level security using the Web Services Security X.509 Certificate Token profile
   * @throws WebServiceClientSecurityException if the web service proxy could not be retrieved
   */
  public static <T> T getWSSecurityX509CertificateServiceProxy(
      Class<?> serviceClass,
      Class<T> serviceInterface,
      String wsdlResourcePath,
      String serviceEndpoint,
      KeyStore keyStore,
      String keyStorePassword,
      String keyStoreAlias)
      throws WebServiceClientSecurityException {
    return getWSSecurityX509CertificateServiceProxy(
        serviceClass,
        serviceInterface,
        wsdlResourcePath,
        serviceEndpoint,
        keyStore,
        keyStorePassword,
        keyStoreAlias,
        keyStore);
  }

  /**
   * Returns the secure web service proxy for the web service that has been secured with message
   * level security using the Web Services Security X.509 Certificate Token profile.
   *
   * @param serviceClass the Java web service client class
   * @param serviceInterface the Java interface for the web service
   * @param wsdlResourcePath the resource path to the WSDL for the web service on the classpath
   * @param serviceEndpoint the URL giving the web service endpoint
   * @param keyStore the key store containing the private key and certificate (public key)
   * @param keyStorePassword the password for the key store
   * @param keyStoreAlias the alias of the key-pair in the key store
   * @param trustStore the key store containing the certificates that will be used to verify the
   *     remote web service
   * @param <T> the Java interface for the web service
   * @return the secure web service proxy for the web service that has been secured with message
   *     level security using the Web Services Security X.509 Certificate Token profile
   * @throws WebServiceClientSecurityException if the web service proxy could not be retrieved
   */
  public static <T> T getWSSecurityX509CertificateServiceProxy(
      Class<?> serviceClass,
      Class<T> serviceInterface,
      String wsdlResourcePath,
      String serviceEndpoint,
      KeyStore keyStore,
      String keyStorePassword,
      String keyStoreAlias,
      KeyStore trustStore)
      throws WebServiceClientSecurityException {
    try {
      // First attempt to retrieve the web service client from the cache
      WebServiceClient webServiceClient =
          webServiceClientCache.get(
              serviceClass.getName() + "-" + WebServiceSecurityType.WSS_SECURITY_X509_TOKEN.code());

      if (webServiceClient == null) {
        webServiceClient = getWebServiceClient(serviceClass, wsdlResourcePath);

        webServiceClientCache.put(
            serviceClass.getName() + "-" + WebServiceSecurityType.WSS_SECURITY_X509_TOKEN.code(),
            webServiceClient);
      }

      T proxy =
          webServiceClient.getService().getPort(webServiceClient.getPortQName(), serviceInterface);

      // Set the endpoint for the web service
      BindingProvider bindingProvider = ((BindingProvider) proxy);

      bindingProvider
          .getRequestContext()
          .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceEndpoint);

      CXFWSSX509CertificateTokenProfileProxyConfigurator.configureProxy(
          proxy, keyStore, keyStorePassword, keyStoreAlias, trustStore);

      return proxy;
    } catch (Throwable e) {
      throw new WebServiceClientSecurityException(
          "Failed to retrieve the web service proxy for the web service ("
              + serviceClass.getName()
              + ") that has been secured using the Web Services Security X.509 Certificate Token profile",
          e);
    }
  }

  /**
   * Returns the socket factory used to connect to a web service using SSL without validating the
   * server certificate.
   *
   * @return the socket factory used to connect to a web service using SSL without validating the
   *     server certificate
   */
  private static SSLSocketFactory getNoTrustSSLSocketFactory() {
    try {
      if (noTrustSSLSocketFactory == null) {
        noTrustSSLSocketFactory = new NoTrustSSLSocketFactory();
      }

      return noTrustSSLSocketFactory;
    } catch (Throwable e) {
      throw new RuntimeException("Failed to initialize the no trust SSL socket factory", e);
    }
  }

  /**
   * Returns the web service client.
   *
   * @param serviceClass the Java web service client class
   * @param wsdlResourcePath the resource path to the WSDL for the web service on the classpath
   * @return the web service client
   * @throws WebServiceClientSecurityException if the web service client could not be retrieved
   */
  private static WebServiceClient getWebServiceClient(
      Class<?> serviceClass, String wsdlResourcePath) throws WebServiceClientSecurityException {
    try {
      if (!Service.class.isAssignableFrom(serviceClass)) {
        throw new WebServiceClientSecurityException(
            "The web service client class ("
                + serviceClass.getName()
                + ") does not extend the javax.xml.ws.Service class");
      }

      /*
       * Retrieve the @WebServiceClient annotation from the service class to determine the qname
       * for the web service.
       */
      javax.xml.ws.WebServiceClient webServiceClientAnnotation =
          serviceClass.getAnnotation(javax.xml.ws.WebServiceClient.class);

      if (webServiceClientAnnotation == null) {
        throw new WebServiceClientSecurityException(
            "Failed to retrieve the @WebServiceClient annotation from the web service client "
                + "class ("
                + serviceClass.getName()
                + ")");
      }

      ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

      // Retrieve the WSDL for the web service as a resource from the classpath
      URL wsdlLocation = contextClassLoader.getResource(wsdlResourcePath);

      if (wsdlLocation == null) {
        throw new WebServiceClientSecurityException(
            "Failed to retrieve the WSDL for the web service ("
                + wsdlResourcePath
                + ") as a resource from the classpath");
      }

      // Create a new instance of the web service client
      Constructor<?> constructor = serviceClass.getConstructor(URL.class, QName.class);

      QName serviceQName =
          new QName(
              webServiceClientAnnotation.targetNamespace(), webServiceClientAnnotation.name());

      Object serviceObject = constructor.newInstance(wsdlLocation, serviceQName);

      Service service = (Service) serviceObject;

      /*
       * Find the method with no parameters annotated with @WebEndpoint.
       *
       * Then use the value of the "name" parameter on this annotation to determine the port name.
       */
      String portName = null;

      for (Method method : serviceClass.getMethods()) {
        WebEndpoint webEndpointAnnotation = method.getAnnotation(WebEndpoint.class);

        if (webEndpointAnnotation != null) {
          portName = webEndpointAnnotation.name();

          break;
        }
      }

      if (portName == null) {
        throw new WebServiceClientSecurityException(
            "Failed to determine the port name using the"
                + " @WebEndpoint annotation on one of the methods on the web service client class ("
                + serviceClass.getName()
                + ")");
      }

      QName portQName = new QName(webServiceClientAnnotation.targetNamespace(), portName);

      return new WebServiceClient(portQName, service);
    } catch (Throwable e) {
      throw new WebServiceClientSecurityException(
          "Failed to retrieve the web service client for the web service ("
              + serviceClass.getName()
              + ")",
          e);
    }
  }

  /**
   * The <b>WebServiceClient</b> class holds the information for a web service client.
   *
   * @author Marcus Portmann
   */
  public static class WebServiceClient {

    /** The QName for the port. */
    private final QName portQName;

    /** The web service client. */
    private final Service service;

    /**
     * Constructs a new <b>CachedWebServiceClient</b>.
     *
     * @param portQName the QName for the port
     * @param service the web service client
     */
    public WebServiceClient(QName portQName, Service service) {
      this.portQName = portQName;
      this.service = service;
    }

    /**
     * Returns the QName for the port.
     *
     * @return the QName for the port
     */
    public QName getPortQName() {
      return portQName;
    }

    /**
     * Returns the web service client.
     *
     * @return the web service client
     */
    public Service getService() {
      return service;
    }
  }
}
