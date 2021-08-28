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

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

/**
 * The <b>ApiMethodSecurityExpressionHandler</b> class.
 *
 * @author Marcus Portmann
 */
public class ApiMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

  /** The Spring application context. */
  private final ApplicationContext applicationContext;

  /**
   * Constructs a new <b>ApiMethodSecurityExpressionHandler</b>.
   *
   * @param applicationContext the Spring application context
   */
  public ApiMethodSecurityExpressionHandler(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  /**
   * Creates the root object for expression evaluation.
   *
   * @param authentication the authentication
   * @param invocation the invocation
   * @return the root object for expression evaluation
   */
  @Override
  protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
      Authentication authentication, MethodInvocation invocation) {
    ApiSecurityExpressionRoot apiSecurityExpressionRoot =
        new ApiSecurityExpressionRoot(authentication, applicationContext);

    apiSecurityExpressionRoot.setPermissionEvaluator(getPermissionEvaluator());
    apiSecurityExpressionRoot.setTrustResolver(this.getTrustResolver());
    apiSecurityExpressionRoot.setRoleHierarchy(getRoleHierarchy());

    return apiSecurityExpressionRoot;
  }
}
