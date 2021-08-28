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

import javax.naming.InitialContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The <b>ServiceUtil</b> class provides utility methods that are useful when creating internal
 * application services.
 */
public class ServiceUtil {

  /* Logger */
  private static final Logger logger = LoggerFactory.getLogger(ServiceUtil.class);

  /**
   * Retrieve the service instance name for the service with the specified name.
   *
   * @param serviceName the service name
   * @return the service instance name for the service with the specified name
   */
  public static String getServiceInstanceName(String serviceName) {
    String applicationName = null;

    try {
      applicationName = InitialContext.doLookup("java:app/AppName");
    } catch (Throwable ignored) {
    }

    if (applicationName == null) {
      try {
        applicationName = InitialContext.doLookup("java:comp/env/ApplicationName");
      } catch (Throwable ignored) {
      }
    }

    String instanceName = (applicationName == null) ? "" : applicationName + "::";

    try {
      java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();

      instanceName += localMachine.getHostName().toLowerCase();
    } catch (Throwable e) {
      logger.error(
          String.format(
              "Failed to retrieve the server hostname while constructing the %s instance name",
              serviceName),
          e);
      instanceName = "Unknown";
    }

    // Check if we are running under JBoss and if so retrieve the server name
    if (System.getProperty("jboss.server.name") != null) {
      instanceName = instanceName + "::" + System.getProperty("jboss.server.name");
    }

    // Check if we are running under Glassfish and if so retrieve the server name
    else if (System.getProperty("glassfish.version") != null) {
      instanceName = instanceName + "::" + System.getProperty("com.sun.aas.instanceName");
    }

    // Check if we are running under WebSphere Application Server Community Edition (Geronimo)
    else if (System.getProperty("org.apache.geronimo.server.dir") != null) {
      instanceName = instanceName + "::Geronimo";
    }

    // Check if we are running under WebSphere Application Server Liberty Profile
    else if (System.getProperty("wlp.user.dir") != null) {
      instanceName = instanceName + "::WLP";
    }

    /*
     * Check if we are running under WebSphere and if so execute the code below to retrieve the
     * server name.
     */
    else {
      Class<?> clazz = null;

      try {
        clazz =
            Thread.currentThread()
                .getContextClassLoader()
                .loadClass("com.ibm.websphere.management.configservice.ConfigurationService");
      } catch (Throwable ignored) {
      }

      if (clazz != null) {
        try {
          instanceName = instanceName + "::" + InitialContext.doLookup("servername").toString();
        } catch (Throwable e) {
          logger.error(
              String.format(
                  "Failed to retrieve the name of the WebSphere server instance from JNDI while "
                      + "constructing the %s instance name",
                  serviceName),
              e);
          instanceName = instanceName + "::Unknown";
        }
      }
    }

    return instanceName;
  }
}
