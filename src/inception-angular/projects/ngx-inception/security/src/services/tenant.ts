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

import {TenantStatus} from './tenant-status';

/**
 * The Tenant class holds the information for a tenant.
 *
 * @author Marcus Portmann
 */
export class Tenant {

  /**
   * The Universally Unique Identifier (UUID) for the tenant.
   */
  id: string;

  /**
   * The name of the tenant.
   */
  name: string;

  /**
   * The status for the tenant.
   */
  status: TenantStatus;

  /**
   * Constructs a new Tenant.
   *
   * @param id     The Universally Unique Identifier (UUID) for the tenant.
   * @param name   The name of the tenant.
   * @param status The status for the tenant.
   */
  constructor(id: string, name: string, status: TenantStatus) {
    this.id = id;
    this.name = name;
    this.status = status;
  }
}
