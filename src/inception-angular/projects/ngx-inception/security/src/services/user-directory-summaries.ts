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
import {UserDirectorySummary} from './user-directory-summary';

/**
 * The UserDirectorySummaries class holds the results of a request to retrieve a list of user
 * directory summaries.
 *
 * @author Marcus Portmann
 */
export class UserDirectorySummaries {

  /**
   * The optional filter that was applied to the user directory summaries.
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
   * The optional sort direction that was applied to the user directory summaries.
   */
  sortDirection: SortDirection | null = null;

  /**
   * The total number of user directory summaries.
   */
  total: number;

  /**
   * The user directory summaries.
   */
  userDirectorySummaries: UserDirectorySummary[];

  /**
   * Constructs a new UserDirectorySummaries.
   *
   * @param userDirectorySummaries The user directory summaries.
   * @param total                  The total number of user directory summaries.
   * @param filter                 The optional filter that was applied to the user directory
   *                               summaries.
   * @param sortDirection          The optional sort direction that was applied to the user
   *                               directory summaries.
   * @param pageIndex              The optional page index.
   * @param pageSize               The optional page size.
   */
  constructor(userDirectorySummaries: UserDirectorySummary[], total: number, filter?: string,
              sortDirection?: SortDirection, pageIndex?: number, pageSize?: number) {
    this.userDirectorySummaries = userDirectorySummaries;
    this.total = total;
    this.filter = !!filter ? filter : null;
    this.sortDirection = !!sortDirection ? sortDirection : null;
    this.pageIndex = !!pageIndex ? pageIndex : null;
    this.pageSize = !!pageSize ? pageSize : null;
  }
}
