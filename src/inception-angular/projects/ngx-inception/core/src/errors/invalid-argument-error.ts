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
import {HttpError} from "./http-error";
import {ProblemDetails} from "./problem-details";

/**
 * The InvalidArgumentError class holds the information for an invalid argument error.
 *
 * @author Marcus Portmann
 */
export class InvalidArgumentError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/invalid-argument';

  /**
   * Constructs a new InvalidArgumentError.
   *
   * @param cause   The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@core_invalid_argument_error:An invalid argument error occurred.`, cause);
  }

  /**
   * Returns whether the specified HTTP error response is as a result of an invalid argument error.
   *
   * @param httpErrorResponse The HTTP error response.
   *
   * @return True if the HTTP error response is as a result of an invalid argument error or false
   *         otherwise.
   */
  static isInvalidArgumentError(httpErrorResponse: HttpErrorResponse): boolean {
    return ProblemDetails.isProblemDetails(httpErrorResponse, InvalidArgumentError.TYPE);
  }
}
