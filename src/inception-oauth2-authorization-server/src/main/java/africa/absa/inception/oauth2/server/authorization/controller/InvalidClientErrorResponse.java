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
 * The <b>InvalidClientErrorResponse</b> class holds the information for an OAuth2 invalid client
 * error response.
 *
 * <p>See: <a href="https://tools.ietf.org/html/rfc6749#section-5.2">Error Response</a>
 *
 * @author Marcus Portmann
 */
public class InvalidClientErrorResponse extends ErrorResponse {

  /** The error code for the OAuth2 invalid client error response. */
  public static final String ERROR_CODE = "invalid_client";

  /**
   * Constructs a new <b>InvalidClientErrorResponse</b>.
   *
   * @param status the HTTP status that should be returned for the OAuth2 invalid client error
   *     response
   */
  public InvalidClientErrorResponse(HttpStatus status) {
    super(status, ERROR_CODE);
  }

  /**
   * Constructs a new <b>InvalidClientErrorResponse</b>.
   *
   * @param status the HTTP status that should be returned for the OAuth2 invalid client error
   *     response
   * @param errorDescription the optional human-readable ASCII text description of the error
   */
  public InvalidClientErrorResponse(HttpStatus status, String error, String errorDescription) {
    super(status, ERROR_CODE, errorDescription);
  }
}
