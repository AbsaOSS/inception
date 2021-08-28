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

package africa.absa.inception.security;

import africa.absa.inception.core.service.Problem;
import africa.absa.inception.core.service.ServiceException;
import java.util.UUID;

/**
 * A <b>TenantUserDirectoryNotFoundException</b> is thrown to indicate that a security operation
 * failed as a result of a tenant user directory that could not be found.
 *
 * <p>This is a checked exception to prevent the automatic rollback of the current transaction.
 *
 * @author Marcus Portmann
 */
@Problem(
    type = "http://inception.absa.africa/problems/security/tenant-user-directory-not-found",
    title = "The tenant user directory could not be found.",
    status = 404)
public class TenantUserDirectoryNotFoundException extends ServiceException {

  private static final long serialVersionUID = 1000000;

  /**
   * Constructs a new <b>TenantUserDirectoryNotFoundException</b>.
   *
   * @param tenantId the Universally Unique Identifier (UUID) for the tenant
   * @param userDirectoryId the Universally Unique Identifier (UUID) for the user directory
   */
  public TenantUserDirectoryNotFoundException(UUID tenantId, UUID userDirectoryId) {
    super(
        "The tenant user directory for the tenant ("
            + tenantId
            + ") and user directory ("
            + userDirectoryId
            + ") could not be found");
  }
}
