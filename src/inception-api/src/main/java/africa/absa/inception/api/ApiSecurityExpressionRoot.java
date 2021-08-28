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

package africa.absa.inception.api;

import org.springframework.context.ApplicationContext;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

/**
 * The <b>ApiSecurityExpressionRoot</b> class implements the custom methods, which can be used in
 * conjunction with the <b>@PreAuthorize</b> annotation.
 *
 * @author Marcus Portmann
 */
public class ApiSecurityExpressionRoot extends SecurityExpressionRoot
    implements MethodSecurityExpressionOperations {

  /** The filter object. */
  private Object filterObject;

  /** Is debugging enabled for the Inception Framework? */
  private boolean inDebugMode;

  /** Is API security enabled for the Inception Framework? */
  private boolean isSecurityEnabled;

  /** The return object. */
  private Object returnObject;

  /**
   * Constructs a new <ApiSecurityExpressionRoot></b>.
   *
   * @param authentication the authentication
   * @param applicationContext the Spring application context
   */
  public ApiSecurityExpressionRoot(
      Authentication authentication, ApplicationContext applicationContext) {
    super(authentication);

    // Check if debugging is enabled for the Inception Framework
    try {
      if (StringUtils.hasText(
          applicationContext.getEnvironment().getProperty("inception.debug.enabled"))) {
        this.inDebugMode =
            Boolean.parseBoolean(
                applicationContext.getEnvironment().getProperty("inception.debug.enabled"));
      }
    } catch (Throwable e) {
      this.inDebugMode = false;
    }

    // Check if security is enabled for the Inception Framework
    try {
      if (StringUtils.hasText(
          applicationContext.getEnvironment().getProperty("inception.api.security.enabled"))) {
        this.isSecurityEnabled =
            Boolean.parseBoolean(
                applicationContext.getEnvironment().getProperty("inception.api.security.enabled"));
      } else {
        this.isSecurityEnabled = (!this.inDebugMode);
      }
    } catch (Throwable e) {
      this.isSecurityEnabled = (!this.inDebugMode);
    }
  }

  @Override
  public Object getFilterObject() {
    return filterObject;
  }

  @Override
  public Object getReturnObject() {
    return returnObject;
  }

  @Override
  public Object getThis() {
    return this;
  }

  /**
   * Returns whether security is disabled.
   *
   * @return <b>true</b> if security is disabled or <b>false</b> otherwise
   */
  public boolean isSecurityDisabled() {
    return (!isSecurityEnabled);
  }

  /**
   * Returns whether security is enabled.
   *
   * @return <b>true</b> if security is enabled or <b>false</b> otherwise
   */
  public boolean isSecurityEnabled() {
    return isSecurityEnabled;
  }

  @Override
  public void setFilterObject(Object filterObject) {
    this.filterObject = filterObject;
  }

  @Override
  public void setReturnObject(Object returnObject) {
    this.returnObject = returnObject;
  }
}
