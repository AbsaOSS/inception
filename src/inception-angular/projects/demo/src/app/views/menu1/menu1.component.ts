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
  AccessDeniedError, CommunicationError, INCEPTION_CONFIG, InceptionConfig, ServiceUnavailableError
} from "@absaoss/ngx-inception/core";
import {Country, ReferenceService} from "@absaoss/ngx-inception/reference";
import {HttpClient, HttpErrorResponse, HttpParams} from "@angular/common/http";
import {Component, Inject, LOCALE_ID} from '@angular/core';
import {Observable, throwError} from "rxjs";
import {catchError, map} from "rxjs/operators";


/**
 * The Menu1Component class implements the menu 1 component.
 *
 * @author Marcus Portmann
 */
@Component({
  template: `
    <mat-card class="flex-grow-1">
      <mat-card-content>
        Menu 1
      </mat-card-content>
    </mat-card>
  `
})
export class Menu1Component {

  constructor(@Inject(INCEPTION_CONFIG) private config: InceptionConfig,
              @Inject(LOCALE_ID) private localeId: string, private httpClient: HttpClient,
              private referenceService: ReferenceService) {

    this.doIt().subscribe((countries) => {
      console.log('countries = ', countries);
    });


    // referenceService.getCountries().pipe(first()).subscribe((countries: Country[]) => {
    //   console.log('countries = ', countries);
    // });

  }

  doIt(): Observable<Country[]> {
    {
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
  }
}
