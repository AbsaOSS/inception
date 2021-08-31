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

/**
 * An <b>InvalidSecurityCodeException</b> is thrown to indicate that a security operation failed as
 * a result of an invalid security code.
 *
 * <p>This is a checked exception to prevent the automatic rollback of the current transaction.
 *
 * @author Marcus Portmann
 */
@Problem(
    type = "http://inception.absa.africa/problems/security/invalid-security-code",
    title = "Invalid security code.",
    status = 401)
public class InvalidSecurityCodeException extends ServiceException {

  private static final long serialVersionUID = 1000000;

  /**
   * Constructs a new <b>InvalidSecurityCodeException</b>.
   *
   * @param username the username for the user
   */
  public InvalidSecurityCodeException(String username) {
    super("Invalid security code for the user (" + username + ")");
  }
}
