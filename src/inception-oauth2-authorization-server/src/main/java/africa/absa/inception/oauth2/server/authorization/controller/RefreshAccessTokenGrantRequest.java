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
 * The <b>RefreshAccessTokenGrantRequest</b> class holds the information for a Refresh Access Token
 * Grant request.
 *
 * <p>See <a href="https://tools.ietf.org/html/rfc6749#section-6">Refreshing an Access Token</a>
 *
 * @author Marcus Portmann
 */
public class RefreshAccessTokenGrantRequest extends GrantRequest {

  /** The grant type for the Refresh Access Token Grant. */
  public static final String GRANT_TYPE = "refresh_token";

  /**
   * The name of the refresh token parameter.
   *
   * <p>This parameter is REQUIRED.
   */
  public static final String REFRESH_TOKEN_PARAMETER = "refresh_token";

  /** The refresh token. */
  private final String refreshToken;

  /**
   * Constructs a new <b>RefreshAccessTokenGrantRequest</b>.
   *
   * @param parameters the request parameters
   */
  public RefreshAccessTokenGrantRequest(Map<String, String> parameters) {
    super(parameters);

    this.refreshToken = parameters.get(REFRESH_TOKEN_PARAMETER);
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

    return StringUtils.hasText(parameters.get(REFRESH_TOKEN_PARAMETER));
  }

  /**
   * Returns the refresh token.
   *
   * @return the refresh token
   */
  public String getRefreshToken() {
    return refreshToken;
  }
}
