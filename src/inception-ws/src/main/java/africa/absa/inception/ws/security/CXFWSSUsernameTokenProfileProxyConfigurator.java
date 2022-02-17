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
import java.util.HashMap;
import java.util.Map;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;

/**
 * The <b>CXFWSSUsernameTokenProfileProxyConfigurator</b> class provides the capability to configure
 * a CXF web service proxy to support authentication using the Web Services Security Username Token
 * profile.
 *
 * @author Marcus Portmann
 */
public class CXFWSSUsernameTokenProfileProxyConfigurator {

  /**
   * Configure the CXF web service proxy to support authentication using the the Web Services
   * Security Username Token profile.
   *
   * @param proxy the web service proxy to configure
   * @param username the username to use when authenticating
   * @param password the password to use when authenticating
   * @param usePlainTextPassword should a plain text password be used
   */
  public static void configureProxy(
      Object proxy, String username, String password, boolean usePlainTextPassword) {
    InvocationHandler invocationHandler = Proxy.getInvocationHandler(proxy);

    if (invocationHandler instanceof ClientProxy) {
      ClientProxy clientProxy = (ClientProxy) invocationHandler;

      Client client = clientProxy.getClient();

      Map<String, Object> outProperties = new HashMap<>();
      outProperties.put(
          WSHandlerConstants.ACTION,
          WSHandlerConstants.TIMESTAMP + " " + WSHandlerConstants.USERNAME_TOKEN);

      if (usePlainTextPassword) {
        outProperties.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
      } else {
        outProperties.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_DIGEST);
      }

      outProperties.put(WSHandlerConstants.USER, username);
      outProperties.put(
          WSHandlerConstants.PW_CALLBACK_REF, new PasswordCallbackHandler(username, password));

      client.getOutInterceptors().add(new WSS4JOutInterceptor(outProperties));
    }
  }
}
