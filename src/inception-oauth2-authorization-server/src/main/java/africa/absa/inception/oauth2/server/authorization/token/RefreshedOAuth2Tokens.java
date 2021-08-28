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

package africa.absa.inception.oauth2.server.authorization.token;

/**
 * The <b>RefreshedOAuth2Tokens</b> class holds a refreshed OAuth2 access token and optionally a
 * refreshed OAuth2 refresh token.
 *
 * @author Marcus Portmann
 */
public class RefreshedOAuth2Tokens {

  /** The OAuth2 access token. */
  private final OAuth2AccessToken accessToken;

  /** The OAuth2 refresh token. */
  private final OAuth2RefreshToken refreshToken;

  /**
   * Constructs a new <b>RefreshedOAuth2Tokens</b>.
   *
   * @param accessToken the OAuth2 access token
   * @param refreshToken the OAuth2 refresh token
   */
  public RefreshedOAuth2Tokens(OAuth2AccessToken accessToken, OAuth2RefreshToken refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }

  /**
   * Returns the OAuth2 access token.
   *
   * @return the OAuth2 access token
   */
  public OAuth2AccessToken getAccessToken() {
    return accessToken;
  }

  /**
   * Returns the OAuth2 refresh token.
   *
   * @return the OAuth2 refresh token
   */
  public OAuth2RefreshToken getRefreshToken() {
    return refreshToken;
  }
}
