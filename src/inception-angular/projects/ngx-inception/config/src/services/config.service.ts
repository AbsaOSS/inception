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

import {
  AccessDeniedError, CommunicationError, INCEPTION_CONFIG, InceptionConfig, InvalidArgumentError,
  ProblemDetails, ServiceUnavailableError
} from '@absaoss/ngx-inception/core';
import {HttpClient, HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {Inject, Injectable} from '@angular/core';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {Config} from './config';
import {ConfigNotFoundError} from './config.service.errors';

/**
 * The Config Service implementation.
 *
 * @author Marcus Portmann
 */
@Injectable({
  providedIn: 'root'
})
export class ConfigService {

  /**
   * Constructs a new ConfigService.
   *
   * @param config     The Inception config.
   * @param httpClient The HTTP client.
   */
  constructor(@Inject(INCEPTION_CONFIG) private config: InceptionConfig, private httpClient: HttpClient) {
    console.log('Initializing the Config Service');
  }

  /**
   * Delete the config.
   *
   * @param key The key for the config.
   *
   * @return True if the config was deleted or false otherwise.
   */
  deleteConfig(key: string): Observable<boolean> {
    return this.httpClient.delete<boolean>(
      this.config.configApiUrlPrefix + '/configs/' + encodeURIComponent(key), {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, ConfigNotFoundError.TYPE)) {
        return throwError(new ConfigNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to delete the config.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the config.
   *
   * @param key The key for the config.
   *
   * @return The config.
   */
  getConfig(key: string): Observable<Config> {
    return this.httpClient.get<Config>(
      this.config.configApiUrlPrefix + '/configs/' + encodeURIComponent(key), {reportProgress: true})
    .pipe(map((config: Config) => {
      return config;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, ConfigNotFoundError.TYPE)) {
        return throwError(new ConfigNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the config.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the config value.
   *
   * @param key The key for the config.
   *
   * @return The config value.
   */
  getConfigValue(key: string): Observable<string> {
    return this.httpClient.get<string>(
      this.config.configApiUrlPrefix + '/configs/' + encodeURIComponent(key) + '/value', {
        reportProgress: true
      }).pipe(map((value: string) => {
      return value;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, ConfigNotFoundError.TYPE)) {
        return throwError(new ConfigNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the config value.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve all the configs.
   *
   * @return The configs.
   */
  getConfigs(): Observable<Config[]> {
    return this.httpClient.get<Config[]>(this.config.configApiUrlPrefix + '/configs',
      {reportProgress: true})
    .pipe(map((configs: Config[]) => {
      return configs;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the configs.', httpErrorResponse));
    }));
  }

  /**
   * Save the config.
   *
   * @param config The config.
   *
   * @return True if the config was saved successfully or false otherwise.
   */
  saveConfig(config: Config): Observable<boolean> {
    return this.httpClient.post<boolean>(this.config.configApiUrlPrefix + '/configs', config,
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

      return throwError(new ServiceUnavailableError('Failed to save the config.', httpErrorResponse));
    }));
  }
}
