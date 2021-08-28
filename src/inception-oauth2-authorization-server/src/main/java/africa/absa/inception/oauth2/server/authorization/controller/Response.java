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

import org.springframework.http.HttpStatus;

/**
 * The <b>Response</b> class provides the base class that all OAuth2 response classes should be
 * derived from.
 *
 * @author Marcus Portmann
 */
public abstract class Response {

  /** The HTTP status that should be returned for the OAuth2 response. */
  private final HttpStatus status;

  /**
   * Constructs a new <b>Response</b>.
   *
   * @param status the HTTP status that should be returned for the OAuth2 response
   */
  public Response(HttpStatus status) {
    this.status = status;
  }

  /**
   * Returns the body for the OAuth2 response.
   *
   * @return the body for the OAuth2 response
   */
  public abstract String getBody();

  /**
   * Returns the HTTP status that should be returned for the OAuth2 response.
   *
   * @return the HTTP status that should be returned for the OAuth2 response
   */
  public HttpStatus getStatus() {
    return status;
  }
}
