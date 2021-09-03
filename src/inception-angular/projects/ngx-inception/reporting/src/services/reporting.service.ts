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
  AccessDeniedError, CommunicationError, INCEPTION_CONFIG, InceptionConfig, InvalidArgumentError,
  ProblemDetails, ServiceUnavailableError
} from '@absaoss/ngx-inception/core';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {ReportDefinition} from './report-definition';
import {ReportDefinitionSummary} from './report-definition-summary';
import {
  DuplicateReportDefinitionError, ReportDefinitionNotFoundError
} from './reporting.service.errors';

/**
 * The Reporting Service implementation.
 *
 * @author Marcus Portmann
 */
@Injectable({
  providedIn: 'root'
})
export class ReportingService {

  static readonly MAX_TEMPLATE_SIZE: number = 10485760;

  /**
   * Constructs a new ReportingService.
   *
   * @param config     The Inception configuration.
   * @param httpClient The HTTP client.
   */
  constructor(@Inject(INCEPTION_CONFIG) private config: InceptionConfig, private httpClient: HttpClient) {
    console.log('Initializing the Reporting Service');
  }

  /**
   * Create a report definition.
   *
   * @param reportDefinition The report definition to create.
   *
   * @return True if the report definition was created successfully or false otherwise.
   */
  createReportDefinition(reportDefinition: ReportDefinition): Observable<boolean> {
    return this.httpClient.post<boolean>(this.config.reportingApiUrlPrefix + '/report-definitions', reportDefinition,
      {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, DuplicateReportDefinitionError.TYPE)) {
        return throwError(new DuplicateReportDefinitionError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to create the report definition.', httpErrorResponse));
    }));
  }

  /**
   * Delete the report definition.
   *
   * @param reportDefinitionId The ID for the report definition.
   *
   * @return True if the report definition was deleted or false otherwise.
   */
  deleteReportDefinition(reportDefinitionId: string): Observable<boolean> {
    return this.httpClient.delete<boolean>(
      this.config.reportingApiUrlPrefix + '/report-definitions/' + encodeURIComponent(reportDefinitionId),
      {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, ReportDefinitionNotFoundError.TYPE)) {
        return throwError(new ReportDefinitionNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to delete the report definition.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the report definition.
   *
   * @param reportDefinitionId The ID for the report definition.
   *
   * @return The report definition.
   */
  getReportDefinition(reportDefinitionId: string): Observable<ReportDefinition> {
    return this.httpClient.get<ReportDefinition>(
      this.config.reportingApiUrlPrefix + '/report-definitions/' + encodeURIComponent(reportDefinitionId),
      {reportProgress: true})
    .pipe(map((reportDefinition: ReportDefinition) => {
      return reportDefinition;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, ReportDefinitionNotFoundError.TYPE)) {
        return throwError(new ReportDefinitionNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the report definition.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the name of the report definition.
   *
   * @param reportDefinitionId The ID for the report definition.
   *
   * @return The name of the report definition.
   */
  getReportDefinitionName(reportDefinitionId: string): Observable<string> {
    return this.httpClient.get<string>(
      this.config.reportingApiUrlPrefix + '/report-definitions/' + encodeURIComponent(reportDefinitionId) + '/name', {
        reportProgress: true,
      }).pipe(map((reportDefinitionName: string) => {
      return reportDefinitionName;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, ReportDefinitionNotFoundError.TYPE)) {
        return throwError(new ReportDefinitionNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the report definition name.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the summaries for all the report definitions.
   *
   * @return The summaries for all the report definitions.
   */
  getReportDefinitionSummaries(): Observable<ReportDefinitionSummary[]> {
    return this.httpClient.get<ReportDefinitionSummary[]>(this.config.reportingApiUrlPrefix + '/report-definitions',
      {reportProgress: true})
    .pipe(map((reportDefinitionSummaries: ReportDefinitionSummary[]) => {
      return reportDefinitionSummaries;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the summaries for the report definitions.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve all the report definitions.
   *
   * @return The report definitions.
   */
  getReportDefinitions(): Observable<ReportDefinition[]> {
    return this.httpClient.get<ReportDefinition[]>(this.config.reportingApiUrlPrefix + '/report-definitions',
      {reportProgress: true})
    .pipe(map((reportDefinitions: ReportDefinition[]) => {
      return reportDefinitions;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the report definitions.', httpErrorResponse));
    }));
  }

  /**
   * Update the report definition.
   *
   * @param reportDefinition The report definition
   *
   * @return True if the report definition was updated successfully or false otherwise.
   */
  updateReportDefinition(reportDefinition: ReportDefinition): Observable<boolean> {
    return this.httpClient.put<boolean>(
      this.config.reportingApiUrlPrefix + '/report-definitions/' + encodeURIComponent(reportDefinition.id),
      reportDefinition, {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, ReportDefinitionNotFoundError.TYPE)) {
        return throwError(new ReportDefinitionNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to update the report definition.', httpErrorResponse));
    }));
  }
}
