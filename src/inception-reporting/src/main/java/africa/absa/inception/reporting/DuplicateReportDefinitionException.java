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

package africa.absa.inception.reporting;

import africa.absa.inception.core.service.Problem;
import africa.absa.inception.core.service.ServiceException;

/**
 * The <b>DuplicateReportDefinitionException</b> exception is thrown to indicate an error condition
 * as a result of an attempt to create a duplicate report definition i.e a report definition with
 * the specified ID already exists.
 *
 * <p>This is a checked exception to prevent the automatic rollback of the current transaction.
 *
 * @author Marcus Portmann
 */
@Problem(
    type = "http://inception.absa.africa/problems/reporting/duplicate-report-definition",
    title = "A report definition with the specified ID already exists.",
    status = 409)
public class DuplicateReportDefinitionException extends ServiceException {

  private static final long serialVersionUID = 1000000;

  /**
   * Constructs a new <b>DuplicateReportDefinitionException</b>.
   *
   * @param reportDefinitionId the ID for the report definition
   */
  public DuplicateReportDefinitionException(String reportDefinitionId) {
    super("The report definition with ID (" + reportDefinitionId + ") already exists");
  }
}
