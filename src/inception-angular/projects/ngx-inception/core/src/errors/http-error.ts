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
 * The HttpError class holds the information for a HTTP error.
 *
 * NOTE: This data structure is required because the standard HttpErrorResponse data structure
 *       cannot be successfully serialized when passed as a "state" parameter to the navigateByUrl
 *       method on the Angular router.
 *
 * @author Marcus Portmann
 */
export class HttpError {

  /**
   * The error.
   */
  error: string;

  /**
   * The error description.
   */
  errorDescription: string;

  /**
   * The error message.
   */
  message: string;

  /**
   * The status code.
   */
  status: number;

  /**
   * The status text.
   */
  statusText: string;

  /**
   * The URL associated with the error.
   */
  url: string;

  /**
   * Constructs a new HttpError.
   *
   * @param error            The error.
   * @param errorDescription The error description.
   * @param message          The error message.
   * @param status           The status code.
   * @param statusText       The status text.
   * @param url              The URL associated with the error.
   */
  constructor(error: string, errorDescription: string, message: string, status: number, statusText: string,
              url: string) {
    this.error = error;
    this.errorDescription = errorDescription;
    this.message = message;
    this.status = status;
    this.statusText = statusText;
    this.url = url;
  }
}


