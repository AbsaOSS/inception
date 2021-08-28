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

import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router} from '@angular/router';
import {Observable} from 'rxjs';
import {first, map} from 'rxjs/operators';
import {Session} from '../session/services/session';
import {SessionService} from '../session/services/session.service';

/**
 * The CanActivateFunctionGuard class implements the routing guard that restricts access to a route
 * based on the function codes assigned to a user that are stored as part of the user's session.
 *
 * @author Marcus Portmann
 */
@Injectable()
export class CanActivateFunctionGuard implements CanActivate {

  /**
   * Constructs a new CanActivateFunctionGuard.
   *
   * @param router         The router.
   * @param sessionService The session service.
   */
  constructor(private router: Router, private sessionService: SessionService) {
  }

  canActivate(activatedRouteSnapshot: ActivatedRouteSnapshot): Observable<boolean> {
    return this.sessionService.session$.pipe(first(), map((session: Session | null) => {
      if (activatedRouteSnapshot) {
        if (activatedRouteSnapshot.data) {
          if (activatedRouteSnapshot.data.authorities) {
            if (session) {
              for (const authority of activatedRouteSnapshot.data.authorities) {
                if (session.hasAuthority(authority)) {
                  return true;
                }
              }

              // noinspection JSIgnoredPromiseFromCall
              this.router.navigate(['/login']);

              return false;
            } else {
              // noinspection JSIgnoredPromiseFromCall
              this.router.navigate(['/login']);

              return false;
            }
          } else {
            return true;
          }
        } else {
          return true;
        }
      } else {
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigate(['/login']);

        return false;
      }
    }));
  }
}
