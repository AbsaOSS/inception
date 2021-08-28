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

package africa.absa.inception.oauth2.server.authorization.controller;

import java.util.Map;
import org.springframework.util.StringUtils;

/**
 * The <b>ResourceOwnerPasswordCredentialsGrantRequest</b> class holds the information for a
 * Resource Owner Password Credentials Grant request.
 *
 * <p>See <a href="https://tools.ietf.org/html/rfc6749#section-4.3.2">Access Token Request</a>
 *
 * @author Marcus Portmann
 */
public class ResourceOwnerPasswordCredentialsGrantRequest extends GrantRequest {

  /** The grant type for the Resource Owner Password Credentials Grant. */
  public static final String GRANT_TYPE = "password";

  /**
   * The name of the resource owner password parameter.
   *
   * <p>This parameter is REQUIRED.
   */
  public static final String PASSWORD_PARAMETER = "password";

  /**
   * The name of the resource owner username parameter.
   *
   * <p>This parameter is REQUIRED.
   */
  public static final String USERNAME_PARAMETER = "username";

  /** The resource owner password. */
  private final String password;

  /** The resource owner username. */
  private final String username;

  /**
   * Constructs a new <b>ResourceOwnerPasswordCredentialsGrantRequest</b>.
   *
   * @param parameters the request parameters
   */
  public ResourceOwnerPasswordCredentialsGrantRequest(Map<String, String> parameters) {
    super(parameters);

    this.username = parameters.get(USERNAME_PARAMETER);
    this.password = parameters.get(PASSWORD_PARAMETER);
  }

  /**
   * Check whether the required information for the Resource Owner Password Credentials Grant is
   * provided as part of the HTTP servlet request.
   *
   * @param parameters the request parameters
   * @return <b>true</b> if the HTTP servlet request contains the required information for the
   *     Resource Owner Password Credentials Grant or <b>false</b> otherwise
   */
  public static boolean isValid(Map<String, String> parameters) {

    if (!StringUtils.hasText(parameters.get(GRANT_TYPE_PARAMETER))) {
      return false;
    }

    if (!GRANT_TYPE.equals(parameters.get(GRANT_TYPE_PARAMETER))) {
      return false;
    }

    if (!StringUtils.hasText(parameters.get(USERNAME_PARAMETER))) {
      return false;
    }

    return StringUtils.hasText(parameters.get(PASSWORD_PARAMETER));
  }

  /**
   * Returns the resource owner password.
   *
   * @return the resource owner password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Returns the resource owner username.
   *
   * @return the resource owner username
   */
  public String getUsername() {
    return username;
  }
}
