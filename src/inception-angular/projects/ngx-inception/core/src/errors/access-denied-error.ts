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

import {HttpErrorResponse} from '@angular/common/http';
import {Error} from './error';

/**
 * The AccessDeniedError class holds the information for an access denied error.
 *
 * @author Marcus Portmann
 */
export class AccessDeniedError extends Error {

  /**
   *  The HTTP status-code for the error.
   */
  status: number;

  /**
   * The HTTP reason-phrase for the HTTP status-code for the error.
   */
  statusText: string;

  /**
   * The optional URL for the HTTP request that resulted in the error.
   */
  url?: string;

  /**
   * Constructs a new AccessDeniedError.
   *
   * @param httpErrorResponse The HTTP error response containing the error information.
   */
  constructor(httpErrorResponse: HttpErrorResponse) {
    super($localize`:@@core_access_denied_error:Access is denied. You do not have sufficient privileges to perform the requested operation.`, httpErrorResponse);

    this.status = httpErrorResponse.status;
    this.statusText = httpErrorResponse.statusText;

    if (httpErrorResponse.url) {
      this.url = httpErrorResponse.url;
    }
  }

  /**
   * Returns whether the specified HTTP error response is as a result of an HTTP 403 forbidden
   * response, which indicates that access to the requested resource has been denied.
   *
   * @param httpErrorResponse The HTTP error response.
   *
   * @return True if the HTTP error response is as a result of an HTTP 403 forbidden response,
   *         which indicates that access to the requested resource has been denied.
   */
  static isAccessDeniedError(httpErrorResponse: HttpErrorResponse): boolean {
    return (httpErrorResponse.name === 'HttpErrorResponse') && (httpErrorResponse.status === 403);
  }
}
