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

import {SortDirection} from '@absaoss/ngx-inception/core';
import {CollectionViewer, DataSource} from '@angular/cdk/collections';
import {Observable, ReplaySubject, Subject} from 'rxjs';
import {first} from 'rxjs/operators';
import {SecurityService} from './security.service';
import {Tenant} from './tenant';
import {Tenants} from './tenants';

/**
 * The TenantDatasource class implements the tenant data source.
 *
 * @author Marcus Portmann
 */
export class TenantDatasource implements DataSource<Tenant> {

  private dataSubject$: Subject<Tenant[]> = new ReplaySubject<Tenant[]>();

  private loadingSubject$: Subject<boolean> = new ReplaySubject<boolean>();

  loading$ = this.loadingSubject$.asObservable();

  private totalSubject$: Subject<number> = new ReplaySubject<number>();

  total$ = this.totalSubject$.asObservable();

  constructor(private securityService: SecurityService) {
  }

  /**
   * Clear the data source.
   */
  clear(): void {
    this.totalSubject$.next(0);
    this.dataSubject$.next([]);
  }

  connect(collectionViewer: CollectionViewer): Observable<Tenant[] | ReadonlyArray<Tenant>> {
    return this.dataSubject$.asObservable();
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.dataSubject$.complete();
    this.loadingSubject$.complete();
  }

  /**
   * Load the tenants.
   *
   * @param filter        The optional filter to apply to the tenants.
   * @param sortDirection The optional sort direction to apply to the tenants.
   * @param pageIndex     The optional page index.
   * @param pageSize      The optional page size.
   */
  load(filter?: string, sortDirection?: SortDirection, pageIndex?: number, pageSize?: number): void {
    this.loadingSubject$.next(true);

    this.securityService.getTenants(filter, sortDirection, pageIndex, pageSize)
    .pipe(first())
    .subscribe((tenants: Tenants) => {
      this.loadingSubject$.next(false);

      this.totalSubject$.next(tenants.total);

      this.dataSubject$.next(tenants.tenants);
    }, (error: Error) => {
      this.loadingSubject$.next(false);

      this.totalSubject$.next(0);

      this.loadingSubject$.error(error);
    });
  }
}
