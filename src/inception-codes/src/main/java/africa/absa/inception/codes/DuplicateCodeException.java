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

package africa.absa.inception.codes;

import africa.absa.inception.core.service.Problem;
import africa.absa.inception.core.service.ServiceException;

/**
 * The <b>DuplicateCodeException</b> exception is thrown to indicate an error condition as a result
 * of an attempt to create a duplicate code i.e a code with the specified ID already exists for the
 * code category.
 *
 * <p>This is a checked exception to prevent the automatic rollback of the current transaction.
 *
 * @author Marcus Portmann
 */
@Problem(
    type = "http://inception.absa.africa/problems/codes/duplicate-code",
    title = "A code with the specified ID already exists for the code category.",
    status = 409)
public class DuplicateCodeException extends ServiceException {

  private static final long serialVersionUID = 1000000;

  /**
   * Constructs a new <b>DuplicateCodeException</b>.
   *
   * @param codeCategoryId the ID for the code category the code is associated with
   * @param codeId the ID for the code
   */
  public DuplicateCodeException(String codeCategoryId, String codeId) {
    super(
        "The code with ID ("
            + codeId
            + ") already exists for the code category with ID ("
            + codeCategoryId
            + ")");
  }
}
