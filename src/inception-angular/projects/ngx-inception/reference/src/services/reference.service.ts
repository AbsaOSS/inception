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

import {HttpClient, HttpErrorResponse, HttpParams} from '@angular/common/http';
import {Inject, Injectable, LOCALE_ID} from '@angular/core';
import {
  AccessDeniedError, CommunicationError, INCEPTION_CONFIG, InceptionConfig, ServiceUnavailableError
} from 'ngx-inception/core';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {Country} from './country';
import {Language} from './language';
import {Region} from './region';

/**
 * The Reference Service implementation.
 *
 * @author Marcus Portmann
 */
@Injectable({
  providedIn: 'root'
})
export class ReferenceService {

  /**
   * Constructs a new ReferenceService.
   *
   * @param config     The Inception configuration.
   * @param localeId   The Unicode locale identifier for the locale for the application.
   * @param httpClient The HTTP client.
   */
  constructor(@Inject(INCEPTION_CONFIG) private config: InceptionConfig,
              @Inject(LOCALE_ID) private localeId: string, private httpClient: HttpClient) {
    console.log('Initializing the Reference Service (' + localeId + ')');
  }

  /**
   * Retrieve the countries.
   *
   * @return The countries.
   */
  getCountries(): Observable<Country[]> {
    let params = new HttpParams();

    params = params.append('localeId', this.localeId);

    return this.httpClient.get<Country[]>(this.config.referenceApiUrlPrefix + '/countries',
      {params, reportProgress: true})
    .pipe(map((countries: Country[]) => {
      return countries;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the countries.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the languages.
   *
   * @return The languages.
   */
  getLanguages(): Observable<Language[]> {
    let params = new HttpParams();

    params = params.append('localeId', this.localeId);

    return this.httpClient.get<Language[]>(this.config.referenceApiUrlPrefix + '/languages',
      {params, reportProgress: true})
    .pipe(map((languages: Language[]) => {
      return languages;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the languages.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the regions.
   *
   * @return The regions.
   */
  getRegions(): Observable<Region[]> {
    let params = new HttpParams();

    params = params.append('localeId', this.localeId);

    return this.httpClient.get<Region[]>(this.config.referenceApiUrlPrefix + '/regions',
      {params, reportProgress: true})
    .pipe(map((regions: Region[]) => {
      return regions;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the regions.', httpErrorResponse));
    }));
  }
}
