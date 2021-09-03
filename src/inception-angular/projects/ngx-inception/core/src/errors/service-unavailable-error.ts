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
import {HttpError} from './http-error';
import {ProblemDetails} from './problem-details';

/**
 * The ServiceUnavailableError class holds the information for a service unavailable error.
 *
 * @author Marcus Portmann
 */
export class ServiceUnavailableError extends Error {

  /**
   * Constructs a new ServiceUnavailableError.
   *
   * @param message The error message.
   * @param cause   The optional cause of the error.
   */
  constructor(message: string, cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    //$localize`:@@core_service_unavailable_error:An error has occurred and your request could not be processed at this time.`
    super(message, cause);
  }
}
