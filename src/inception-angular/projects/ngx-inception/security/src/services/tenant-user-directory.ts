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

/**
 * The TenantUserDirectory class holds the information for a tenant user directory.
 *
 * @author Marcus Portmann
 */
export class TenantUserDirectory {

  /**
   * The ID for the user directory.
   */
  tenantId: string;

  /**
   * The ID for the tenant.
   */
  userDirectoryId: string;

  /**
   * Constructs a new TenantUserDirectory.
   *
   * @param tenantId  The ID for the tenant.
   * @param userDirectoryId The ID for the user directory.
   */
  constructor(tenantId: string, userDirectoryId: string) {
    this.tenantId = tenantId;
    this.userDirectoryId = userDirectoryId;
  }
}
