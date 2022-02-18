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
import {Inject, Injectable} from '@angular/core';
import {JwtHelperService} from '@auth0/angular-jwt';
import {BehaviorSubject, Observable, of, Subject, throwError, timer} from 'rxjs';
import {catchError, map, mergeMap, switchMap} from 'rxjs/operators';
import {
  AccessDeniedError, CommunicationError, InvalidArgumentError, ServiceUnavailableError
} from '../../errors';
import {INCEPTION_CONFIG, InceptionConfig} from '../../inception-config';
import {Session} from './session';
import {LoginError, PasswordExpiredError, UserLockedError} from './session.service.errors';
import {TokenResponse} from './token-response';

/**
 * The Session Service implementation.
 *
 * @author Marcus Portmann
 */
@Injectable({
  providedIn: 'root'
})
export class SessionService {

  /**
   * The current active session.
   */
  session$: Subject<Session | null> = new BehaviorSubject<Session | null>(null);

  /**
   * Constructs a new SessionService.
   *
   * @param config     The Inception configuration.
   * @param httpClient The HTTP client.
   */
  constructor(@Inject(INCEPTION_CONFIG) private config: InceptionConfig, private httpClient: HttpClient) {
    console.log('Initializing the Session Service');

    // Start the session refresher
    timer(0, 10000).pipe(switchMap(() => this.refreshSession()))
    .subscribe((refreshedSession: Session | null) => {
      if (refreshedSession) {
        console.log('Successfully refreshed session: ', refreshedSession);
      }
    });
  }

  /**
   * Login.
   *
   * @param username The username.
   * @param password The password.
   *
   * @return The current active session.
   */
  login(username: string, password: string): Observable<Session | null> {

    // TODO: REMOVE HARD CODED SCOPE AND CLIENT ID -- MARCUS

    const body = new HttpParams()
    .set('grant_type', 'password')
    .set('username', username)
    .set('password', password);
    // .set('scope', 'demo')
    // .set('client_id', 'demo');

    const options = {headers: {'Content-Type': 'application/x-www-form-urlencoded'}};

    return this.httpClient.post<TokenResponse>(this.config.oauthTokenUrl, body.toString(), options)
    .pipe(mergeMap((tokenResponse: TokenResponse) => {
      this.session$.next(SessionService.createSessionFromAccessToken(tokenResponse.access_token, tokenResponse.refresh_token));

      return this.session$;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (httpErrorResponse.status === 400) {
        if (httpErrorResponse.error && (httpErrorResponse.error.error === 'invalid_grant') &&
          httpErrorResponse.error.error_description) {
          if (httpErrorResponse.error.error_description.includes('Bad credentials')) {
            return throwError(new LoginError(httpErrorResponse));
          } else if (httpErrorResponse.error.error_description.includes('User locked')) {
            return throwError(new UserLockedError(httpErrorResponse));
          } else if (httpErrorResponse.error.error_description.includes('Credentials expired')) {
            return throwError(new PasswordExpiredError(httpErrorResponse));
          }
        }

        return throwError(new LoginError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to login', httpErrorResponse));
    }));
  }

  /**
   * Logout.
   */
  logout(): void {
    this.session$.next(null);
  }

  private static createSessionFromAccessToken(accessToken: string, refreshToken: string | undefined): Session {
    const helper = new JwtHelperService();

    // eslint:disable-next-line
    const token: any = helper.decodeToken(accessToken);

    const accessTokenExpiry: Date | null = helper.getTokenExpirationDate(accessToken);

    return new Session((!!token.sub) ? token.sub : '',
      (!!token.user_directory_id) ? token.user_directory_id : '',
      (!!token.name) ? token.name : '',
      (!!token.scope) ? token.scope.split(' ') : [],
      (!!token.roles) ? token.roles : [],
      (!!token.functions) ? token.functions : [],
      (!!token.tenants) ? token.tenants : [],
      accessToken,
      (!!accessTokenExpiry) ? accessTokenExpiry : undefined, refreshToken);
  }

  private refreshSession(): Observable<Session | null> {
    return this.session$.pipe(mergeMap((currentSession: Session | null) => {
      if (currentSession) {
        const selectedTenantId = currentSession.tenantId;

        /*
         * If the access token will expire with 60 seconds then obtain a new one using the refresh
         * token if it exists. This will cause constant refreshes if the lifespan of the token
         * is less than 60 seconds.
         */
        if (currentSession.accessTokenExpiry && currentSession.refreshToken) {
          if (Date.now() > (currentSession.accessTokenExpiry.getTime() - 30000)) {
            const body = new HttpParams()
            .set('grant_type', 'refresh_token')
            .set('refresh_token', currentSession.refreshToken);

            const options = {headers: {'Content-Type': 'application/x-www-form-urlencoded'}};

            return this.httpClient.post<TokenResponse>(this.config.oauthTokenUrl, body.toString(), options)
            .pipe(map((tokenResponse: TokenResponse) => {
              const refreshedSession: Session = SessionService.createSessionFromAccessToken(
                tokenResponse.access_token,
                (!!tokenResponse.refresh_token) ? tokenResponse.refresh_token : currentSession.refreshToken);

              refreshedSession.tenantId = selectedTenantId;

              this.session$.next(refreshedSession);

              return refreshedSession;
            }), catchError((httpErrorResponse: HttpErrorResponse) => {
              console.log('Failed to refresh the user session.', httpErrorResponse);

              if ((httpErrorResponse.status === 400) || (httpErrorResponse.status === 401)) {
                this.session$.next(null);

                // // noinspection JSIgnoredPromiseFromCall
                // this.router.navigate(['/']);
              }

              return of(null);
            }));
          }
        }
      }

      return of(null);
    }));
  }
}
