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

/**
 * The OAuthError interface defines the structure for an OAuth error.
 *
 * @author Marcus Portmann
 */
export interface OAuthError {

  /**
   * The error type e.g. invalid_request, invalid_client, invalid_grant, etc.
   */
  error: string;

  /**
   * The optional error description.
   */
  error_description?: string;

  /**
   * The URI containing more information about the error.
   */
  error_uri?: string;
}
