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

package africa.absa.inception.core.service;

import java.util.List;

/**
 * The <b>InvalidArgumentException</b> exception is thrown to indicate an error condition as a
 * result of an invalid argument.
 *
 * <p>This is a checked exception to prevent the automatic rollback of the current transaction.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings("unused")
public class InvalidArgumentException extends ServiceException {

  private static final long serialVersionUID = 1000000;

  /** The invalid argument error information. */
  private final InvalidArgumentError invalidArgumentError;

  /**
   * Constructs a new <b>InvalidArgumentException</b>.
   *
   * @param parameter the name of the parameter associated with the invalid argument
   */
  public InvalidArgumentException(String parameter) {
    this(parameter, null);
  }

  /**
   * Constructs a new <b>InvalidArgumentException</b>.
   *
   * @param parameter the name of the parameter associated with the invalid argument
   * @param validationErrors the validation errors associated with the invalid argument
   */
  public InvalidArgumentException(String parameter, List<ValidationError> validationErrors) {
    super("Invalid argument (" + parameter + ")");

    this.invalidArgumentError =
        new InvalidArgumentError(
            "Invalid argument (" + parameter + ")", parameter, validationErrors);
  }

  /**
   * Returns the fault info.
   *
   * @return the fault info
   */
  public InvalidArgumentError getFaultInfo() {
    return invalidArgumentError;
  }

  /**
   * Returns the invalid argument error info.
   *
   * @return the invalid argument error info
   */
  public InvalidArgumentError getInvalidArgumentError() {
    return invalidArgumentError;
  }

  /**
   * Returns the name of the parameter associated with the invalid argument.
   *
   * @return the name of the parameter associated with the invalid argument
   */
  public String getParameter() {
    return invalidArgumentError.getParameter();
  }

  /**
   * Returns the optional validation errors associated with the invalid argument.
   *
   * @return the optional validation errors associated with the invalid argument
   */
  public List<ValidationError> getValidationErrors() {
    return invalidArgumentError.getValidationErrors();
  }
}
