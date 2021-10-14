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

package africa.absa.inception.error;

import africa.absa.inception.core.service.InvalidArgumentException;
import africa.absa.inception.core.service.ServiceUnavailableException;
import africa.absa.inception.core.sorting.SortDirection;
import java.util.List;
import java.util.UUID;

/**
 * The <b>IErrorService</b> interface defines the functionality provided by an Error Service
 * implementation.
 *
 * @author Marcus Portmann
 */
public interface IErrorService {

  /**
   * Create the new entry for the error report in the database.
   *
   * @param errorReport the <b>ErrorReport</b> instance containing the information for the error
   *     report
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the error report could not be created
   */
  void createErrorReport(ErrorReport errorReport)
      throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Retrieve the error report.
   *
   * @param errorReportId the ID for the error report
   * @return the error report
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ErrorReportNotFoundException if the error report could not be found
   * @throws ServiceUnavailableException if the error report could not be retrieved
   */
  ErrorReport getErrorReport(UUID errorReportId)
      throws InvalidArgumentException, ErrorReportNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the error report summaries.
   *
   * @param filter the optional filter to apply to the error reports
   * @param sortBy the optional method used to sort the error reports e.g. by who submitted them
   * @param sortDirection the optional sort direction to apply to the error reports
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the error report summaries
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the error report summaries could not be retrieved
   */
  ErrorReportSummaries getErrorReportSummaries(
      String filter,
      ErrorReportSortBy sortBy,
      SortDirection sortDirection,
      Integer pageIndex,
      Integer pageSize)
      throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Retrieve the summary for the error report.
   *
   * @param errorReportId the ID for the error report
   * @return the summary for the error report
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ErrorReportNotFoundException if the error report could not be found
   * @throws ServiceUnavailableException if the error report summary could not be retrieved
   */
  ErrorReportSummary getErrorReportSummary(UUID errorReportId)
      throws InvalidArgumentException, ErrorReportNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the summaries for the most recent error reports.
   *
   * @param maximumNumberOfEntries the maximum number of summaries for the most recent error reports
   *     to retrieve
   * @return the summaries for the most recent error reports
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the most recent error report summaries could not be
   *     retrieved
   */
  List<ErrorReportSummary> getMostRecentErrorReportSummaries(int maximumNumberOfEntries)
      throws InvalidArgumentException, ServiceUnavailableException;
}
