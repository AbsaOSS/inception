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
import {Tenant} from './tenant';

/**
 * The Tenants class holds the results of a request to retrieve a list of tenants.
 *
 * @author Marcus Portmann
 */
export class Tenants {

  /**
   * The optional filter that was applied to the tenants.
   */
  filter: string | null = null;

  /**
   * The optional page index.
   */
  pageIndex: number | null = null;

  /**
   * The optional page size.
   */
  pageSize: number | null = null;

  /**
   * The optional sort direction that was applied to the tenants.
   */
  sortDirection: SortDirection | null = null;

  /**
   * The tenants.
   */
  tenants: Tenant[];

  /**
   * The total number of tenants.
   */
  total: number;

  /**
   * Constructs a new Tenants.
   *
   * @param tenants       The tenants.
   * @param total         The total number of tenants.
   * @param filter        The optional filter that was applied to the tenants.
   * @param sortDirection The optional sort direction that was applied to the tenants.
   * @param pageIndex     The optional page index.
   * @param pageSize      The optional page size.
   */
  constructor(tenants: Tenant[], total: number, filter?: string, sortDirection?: SortDirection,
              pageIndex?: number, pageSize?: number) {
    this.tenants = tenants;
    this.total = total;
    this.filter = !!filter ? filter : null;
    this.sortDirection = !!sortDirection ? sortDirection : null;
    this.pageIndex = !!pageIndex ? pageIndex : null;
    this.pageSize = !!pageSize ? pageSize : null;
  }
}
