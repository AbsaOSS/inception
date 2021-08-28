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

import africa.absa.inception.core.service.InvalidArgumentException;
import africa.absa.inception.core.service.ServiceUnavailableException;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;

/**
 * The <b>IReportingService</b> interface defines the functionality provided by a Reporting Service
 * implementation.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings("unused")
public interface IReportingService {

  /** The username used to identify operations performed by the system. */
  String SYSTEM_USERNAME = "SYSTEM";

  /**
   * Create the new report definition.
   *
   * @param reportDefinition the <b>ReportDefinition</b> instance containing the information for the
   *     new report definition
   */
  void createReportDefinition(ReportDefinition reportDefinition)
      throws InvalidArgumentException, DuplicateReportDefinitionException,
          ServiceUnavailableException;

  /**
   * Create the PDF for the report using a connection retrieved from the application data source.
   *
   * @param reportDefinitionId the ID for the report definition
   * @param parameters the parameters for the report
   * @return the PDF data for the report
   */
  byte[] createReportPDF(String reportDefinitionId, Map<String, Object> parameters)
      throws InvalidArgumentException, ReportDefinitionNotFoundException,
          ServiceUnavailableException;

  /**
   * Create the PDF for the report.
   *
   * @param reportDefinitionId the ID for the report definition
   * @param parameters the parameters for the report
   * @param connection the database connection used to retrieve the report data
   * @return the PDF data for the report
   */
  byte[] createReportPDF(
      String reportDefinitionId, Map<String, Object> parameters, Connection connection)
      throws InvalidArgumentException, ReportDefinitionNotFoundException,
          ServiceUnavailableException;

  /**
   * Create the PDF for the report.
   *
   * @param reportDefinitionId the ID for the report definition
   * @param parameters the parameters for the report
   * @param document the XML document containing the report data
   * @return the PDF data for the report
   */
  byte[] createReportPDF(
      String reportDefinitionId, Map<String, Object> parameters, Document document)
      throws InvalidArgumentException, ReportDefinitionNotFoundException,
          ServiceUnavailableException;

  /**
   * Delete the existing report definition.
   *
   * @param reportDefinitionId the ID for the report definition
   */
  void deleteReportDefinition(String reportDefinitionId)
      throws InvalidArgumentException, ReportDefinitionNotFoundException,
          ServiceUnavailableException;

  /**
   * Returns the real path to the folder where the local Jasper reports are stored.
   *
   * @return the real path to the folder where the local Jasper reports are stored
   */
  String getLocalReportFolderPath();

  /**
   * Retrieve the report definition.
   *
   * @param reportDefinitionId the ID for the report definition
   * @return the report definition
   */
  ReportDefinition getReportDefinition(String reportDefinitionId)
      throws InvalidArgumentException, ReportDefinitionNotFoundException,
          ServiceUnavailableException;

  /**
   * Retrieve the name of the report definition.
   *
   * @param reportDefinitionId the ID for the report definition
   * @return the name of the report definition
   */
  String getReportDefinitionName(String reportDefinitionId)
      throws InvalidArgumentException, ReportDefinitionNotFoundException,
          ServiceUnavailableException;

  /**
   * Returns the summaries for all the report definitions.
   *
   * @return the summaries for all the report definitions
   */
  List<ReportDefinitionSummary> getReportDefinitionSummaries() throws ServiceUnavailableException;

  /**
   * Retrieve the summary for the report definition.
   *
   * @param reportDefinitionId the ID for the report definition
   * @return the summary for the report definition
   */
  ReportDefinitionSummary getReportDefinitionSummary(String reportDefinitionId)
      throws InvalidArgumentException, ReportDefinitionNotFoundException,
          ServiceUnavailableException;

  /**
   * Returns all the report definitions.
   *
   * @return all the report definitions
   */
  List<ReportDefinition> getReportDefinitions() throws ServiceUnavailableException;

  /**
   * Check whether the report definition exists.
   *
   * @param reportDefinitionId the ID for the report definition
   * @return <b>true</b> if the report definition exists or <b>false</b> otherwise
   */
  boolean reportDefinitionExists(String reportDefinitionId)
      throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Set the real path to the folder where the local Jasper reports are stored.
   *
   * @param localReportFolderPath the real path to the folder where the local Jasper reports are
   *     stored
   */
  void setLocalReportFolderPath(String localReportFolderPath);

  /**
   * Update the report definition.
   *
   * @param reportDefinition the <b>ReportDefinition</b> instance containing the updated information
   *     for the report definition
   */
  void updateReportDefinition(ReportDefinition reportDefinition)
      throws InvalidArgumentException, ReportDefinitionNotFoundException,
          ServiceUnavailableException;
}
