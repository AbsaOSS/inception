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

import {Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable, of} from 'rxjs';

/**
 * The GroupTitleResolver class provides the route data resolver that resolves the
 * title for the "Group" route in the navigation hierarchy.
 *
 * @author Marcus Portmann
 */
@Injectable()
export class GroupTitleResolver implements Resolve<string> {

  /**
   * Constructs a new GroupTitleResolver.
   */
  constructor() {
  }

  /**
   * Resolve the title.
   *
   * @param activatedRouteSnapshot The activate route snapshot.
   * @param routerStateSnapshot    The router state snapshot.
   */
  resolve(activatedRouteSnapshot: ActivatedRouteSnapshot,
          routerStateSnapshot: RouterStateSnapshot): Observable<string> {
    let groupName = activatedRouteSnapshot.paramMap.get('groupName');

    if (!groupName) {
      throw(new Error('No groupName route parameter found'));
    }

    groupName = decodeURIComponent(groupName);

    return of(groupName);
  }
}
