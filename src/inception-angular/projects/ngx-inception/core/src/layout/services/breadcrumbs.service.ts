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
import {ActivatedRoute, NavigationEnd, Router} from '@angular/router';
import {render} from 'es6-template-string';
import {ReplaySubject, Subject} from 'rxjs';
import {filter} from 'rxjs/operators';
import {Breadcrumb} from './breadcrumb';

/**
 * The Breadcrumbs Service implementation.
 *
 * @author Marcus Portmann
 */
@Injectable({
  providedIn: 'root'
})
export class BreadcrumbsService {

  breadcrumbs$: Subject<Breadcrumb[]> = new ReplaySubject<Breadcrumb[]>();

  /**
   * Constructs a new BreadcrumbsService.
   *
   * @param router         The router.
   * @param activatedRoute The activated route.
   */
  constructor(private router: Router, private activatedRoute: ActivatedRoute) {
    console.log('Initializing the Breadcrumbs Service');

    this.router.events.pipe(filter(event => event instanceof NavigationEnd))
    .subscribe(() => {
      const breadcrumbs: Breadcrumb[] = [];
      let currentRoute: ActivatedRoute | null = this.activatedRoute.root;
      let url = '';

      do {
        const childrenRoutes = currentRoute.children;
        currentRoute = null;
        // eslint:disable-next-line:no-shadowed-variable
        childrenRoutes.forEach(route => {
          if (route.outlet === 'primary') {
            const routeSnapshot = route.snapshot;

            if (routeSnapshot.url.length > 0) {
              url += '/' + routeSnapshot.url.map(segment => segment.path).join('/');

              if (routeSnapshot.data.title) {
                breadcrumbs.push(new Breadcrumb(render(routeSnapshot.data.title, routeSnapshot.params), url));
              }
            }
            currentRoute = route;
          }
        });
      } while (currentRoute);

      this.breadcrumbs$.next(breadcrumbs);

      return breadcrumbs;
    });
  }
}



