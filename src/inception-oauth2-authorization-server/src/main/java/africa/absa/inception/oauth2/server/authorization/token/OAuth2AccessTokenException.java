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
 * The <b>OAuth2AccessTokenException</b> exception is thrown to indicate an error condition when
 * working with an OAuth2 access token.
 *
 * @author Marcus Portmann
 */
public class OAuth2AccessTokenException extends RuntimeException {

  private static final long serialVersionUID = 1000000;

  /**
   * Constructs a new <b>OAuth2AccessTokenException</b> with the specified message and cause.
   *
   * @param message The message saved for later retrieval by the <b>getMessage()</b> method.
   * @param cause The cause saved for later retrieval by the <b>getCause()</b> method. (A
   *     <b>null</b> value is permitted if the cause is nonexistent or unknown)
   */
  OAuth2AccessTokenException(String message, Throwable cause) {
    super(message, cause);
  }
}
