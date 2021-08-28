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

import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {first, flatMap} from 'rxjs/operators';
import {SessionService} from './session.service';

/**
 * The SessionInterceptor class implements an Angular HTTP interceptor, which injects the OAuth2
 * access token associated with the current active user session into HTTP requests as an
 * Authorization header.
 *
 * @author Marcus Portmann
 */
@Injectable()
export class SessionInterceptor implements HttpInterceptor {

  /**
   * Constructs a new SessionInterceptor.
   *
   * @param sessionService The session service.
   */
  constructor(private sessionService: SessionService) {
  }

  // tslint:disable-next-line
  intercept(httpRequest: HttpRequest<any>, // tslint:disable-next-line
            nextHttpHandler: HttpHandler): Observable<HttpEvent<any>> {
    if (!httpRequest.url.endsWith('/oauth/token')) {
      return this.sessionService.session$.pipe(first(), flatMap(session => {
        if (session) {
          httpRequest = httpRequest.clone({
            headers: httpRequest.headers.set('Authorization', `Bearer ${session.accessToken}`)
            .set('X-TENANT-ID', (!!session.tenantId) ? session.tenantId : '00000000-0000-0000-0000-000000000000')
          });
        }

        return nextHttpHandler.handle(httpRequest);
      }));
    } else {
      return nextHttpHandler.handle(httpRequest);
    }
  }
}
