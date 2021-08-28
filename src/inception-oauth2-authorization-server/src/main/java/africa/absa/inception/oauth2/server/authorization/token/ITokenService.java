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

import africa.absa.inception.core.service.ServiceUnavailableException;
import java.util.Set;

/**
 * The <b>ITokenService</b> interface defines the functionality provided by a Token Service
 * implementation, which manages the OAuth2 tokens for an application.
 *
 * @author Marcus Portmann
 */
public interface ITokenService {

  /**
   * Issue an OAuth2 access token for the specified user.
   *
   * @param username the username for the user
   * @param scopes the optional scope(s) for the access token
   * @return the OAuth2 access token
   */
  OAuth2AccessToken issueOAuth2AccessToken(String username, Set<String> scopes)
      throws ServiceUnavailableException;

  /**
   * Issue an OAuth2 refresh token for the specified user.
   *
   * @param username the username for the user
   * @param scopes the optional scope(s) for the refresh token
   * @return the OAuth2 refresh token
   */
  OAuth2RefreshToken issueOAuth2RefreshToken(String username, Set<String> scopes)
      throws ServiceUnavailableException;

  /**
   * Refresh an OAuth2 access token and if required the OAuth2 refresh token.
   *
   * @param encodedOAuth2RefreshToken the encoded OAuth2 refresh token
   * @return the refreshed tokens
   */
  RefreshedOAuth2Tokens refreshOAuth2Tokens(String encodedOAuth2RefreshToken)
      throws InvalidOAuth2RefreshTokenException, ServiceUnavailableException;
}
