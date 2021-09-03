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

import {HttpClient, HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {Inject, Injectable} from '@angular/core';
import {
  AccessDeniedError, CommunicationError, Error, INCEPTION_CONFIG, InceptionConfig,
  InvalidArgumentError, ServiceUnavailableError
} from '@absaoss/ngx-inception/core';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {v4 as uuid} from 'uuid';
import {ErrorReport} from './error-report';

/**
 * The Error Service implementation that provides the capability to capture and process application
 * and back-end errors and submit error reports.
 *
 * @author Marcus Portmann
 */
@Injectable({
  providedIn: 'root'
})
export class ErrorService {

  /**
   * Constructs a new ErrorService.
   *
   * @param config     The Inception configuration.
   * @param httpClient The HTTP client.
   */
  constructor(@Inject(INCEPTION_CONFIG) private config: InceptionConfig, private httpClient: HttpClient) {
    console.log('Initializing the Error Service');
  }

  /**
   * Send the error report for the error.
   *
   * @param error    The error.
   * @param email    The optional e-mail address of the user submitting the error report.
   * @param feedback The optional feedback from the user submitting the error report.
   */
  sendErrorReport(error: Error, email?: string, feedback?: string): Observable<boolean> {
    const errorReport: ErrorReport = new ErrorReport(uuid(), this.config.applicationId, this.config.applicationVersion,
      error.message, error.cause ? JSON.stringify(error.cause) : '', error.timestamp, email, feedback);

    return this.httpClient.post<boolean>(this.config.errorApiUrlPrefix + '/error-reports', errorReport,
      {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to send the error report.', httpErrorResponse));
    }));
  }
}
