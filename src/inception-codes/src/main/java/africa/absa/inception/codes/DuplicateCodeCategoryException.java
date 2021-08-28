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
 * The <b>DuplicateCodeCategoryException</b> exception is thrown to indicate an error condition as a
 * result of an attempt to create a duplicate code category i.e a code category with the specified
 * ID already exists.
 *
 * <p>This is a checked exception to prevent the automatic rollback of the current transaction.
 *
 * @author Marcus Portmann
 */
@Problem(
    type = "http://inception.absa.africa/problems/codes/duplicate-code-category",
    title = "A code category with the specified ID already exists.",
    status = 409)
public class DuplicateCodeCategoryException extends ServiceException {

  private static final long serialVersionUID = 1000000;

  /**
   * Constructs a new <b>DuplicateCodeCategoryException</b>.
   *
   * @param codeCategoryId the ID for the code category
   */
  public DuplicateCodeCategoryException(String codeCategoryId) {
    super("The code category with ID (" + codeCategoryId + ") already exists");
  }
}
