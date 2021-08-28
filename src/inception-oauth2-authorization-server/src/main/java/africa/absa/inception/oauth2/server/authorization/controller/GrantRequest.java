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

/**
 * The <b>GrantRequest</b> class provides the base class that all OAuth2 grant request classes
 * should be derived from.
 *
 * @author Marcus Portmann
 */
public abstract class GrantRequest {

  /**
   * The name of the client ID parameter.
   *
   * <p>This parameter is REQUIRED if client authentication is enabled.
   */
  public static final String CLIENT_ID_PARAMETER = "client_id";

  /**
   * The name of the client secret parameter.
   *
   * <p>This parameter is REQUIRED if client authentication is enabled.
   */
  public static final String CLIENT_SECRET_PARAMETER = "client_secret";

  /**
   * The name of the grant type parameter.
   *
   * <p>This parameter is REQUIRED.
   */
  public static final String GRANT_TYPE_PARAMETER = "grant_type";

  /**
   * The name of the parameter providing the requested access token scope.
   *
   * <p>See <a href="https://tools.ietf.org/html/rfc6749#section-3.3">Access Token Scope</a>
   *
   * <p>This parameter is OPTIONAL.
   */
  public static final String SCOPE_PARAMETER = "scope";

  /**
   * The name of the state parameter.
   *
   * <p>This parameter is OPTIONAL.
   */
  public static final String STATE_PARAMETER = "state";

  /** The client identifier issued to the client. */
  private final String clientId;

  /** The client secret. */
  private final String clientSecret;

  /** The grant type. */
  private final String grantType;

  /** The requested scope(s) for the access request. */
  private final String scope;

  /** The state received from the client. */
  private final String state;

  /**
   * Constructs a new <b>GrantRequest</b>.
   *
   * @param parameters the request parameters
   */
  public GrantRequest(Map<String, String> parameters) {
    this.grantType = parameters.get(GRANT_TYPE_PARAMETER);
    this.clientId = parameters.get(CLIENT_ID_PARAMETER);
    this.clientSecret = parameters.get(CLIENT_SECRET_PARAMETER);
    this.scope = parameters.get(SCOPE_PARAMETER);
    this.state = parameters.get(STATE_PARAMETER);
  }

  /**
   * Returns the client identifier issued to the client.
   *
   * @return the client identifier issued to the client
   */
  public String getClientId() {
    return clientId;
  }

  /**
   * Returns the client secret.
   *
   * @return the client secret
   */
  public String getClientSecret() {
    return clientSecret;
  }

  /**
   * Returns the grant type.
   *
   * @return the grant type
   */
  public String getGrantType() {
    return grantType;
  }

  /**
   * Returns the requested scope(s) for the access request.
   *
   * @return the requested scope(s) for the access request
   */
  public String getScope() {
    return scope;
  }

  /**
   * Returns the state received from the client.
   *
   * @return the state received from the client
   */
  public String getState() {
    return state;
  }
}
