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
import africa.absa.inception.core.service.ValidationError;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.sql.DataSource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;

/**
 * The <b>ReportingService</b> class provides the Reporting Service implementation.
 *
 * @author Marcus Portmann
 */
@Service
@SuppressWarnings("unused")
public class ReportingService implements IReportingService {

  /* Logger */
  private static final Logger logger = LoggerFactory.getLogger(ReportingService.class);

  /** The data source used to provide connections to the application database. */
  private final DataSource dataSource;

  /** The Report Definition Repository. */
  private final ReportDefinitionRepository reportDefinitionRepository;

  /** The Report Definition Summary Repository. */
  private final ReportDefinitionSummaryRepository reportDefinitionSummaryRepository;

  /** The JSR-303 validator. */
  private final Validator validator;

  /* The real path to the folder where the local Jasper reports are stored. */
  private String localReportFolderPath;

  /**
   * Constructs a new <b>ReportingService</b>.
   *
   * @param validator the JSR-303 validator
   * @param dataSource the data source used to provide connections to the application database
   * @param reportDefinitionRepository the Report Definition Repository
   * @param reportDefinitionSummaryRepository the Report Definition Summary Repository
   */
  public ReportingService(
      Validator validator,
      @Qualifier("applicationDataSource") DataSource dataSource,
      ReportDefinitionRepository reportDefinitionRepository,
      ReportDefinitionSummaryRepository reportDefinitionSummaryRepository) {
    this.validator = validator;
    this.dataSource = dataSource;
    this.reportDefinitionRepository = reportDefinitionRepository;
    this.reportDefinitionSummaryRepository = reportDefinitionSummaryRepository;
  }

  @Override
  @Transactional
  public void createReportDefinition(ReportDefinition reportDefinition)
      throws InvalidArgumentException, DuplicateReportDefinitionException,
          ServiceUnavailableException {
    validateReportDefinition(reportDefinition);

    try {
      if (reportDefinitionRepository.existsById(reportDefinition.getId())) {
        throw new DuplicateReportDefinitionException(reportDefinition.getId());
      }

      reportDefinitionRepository.saveAndFlush(reportDefinition);
    } catch (DuplicateReportDefinitionException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to create the report definition (" + reportDefinition.getId() + ")", e);
    }
  }

  @Override
  public byte[] createReportPDF(String reportDefinitionId, Map<String, Object> parameters)
      throws InvalidArgumentException, ReportDefinitionNotFoundException,
          ServiceUnavailableException {
    if (!StringUtils.hasText(reportDefinitionId)) {
      throw new InvalidArgumentException("reportDefinitionId");
    }

    if (parameters == null) {
      throw new InvalidArgumentException("parameters");
    }

    try (Connection connection = dataSource.getConnection()) {
      return createReportPDF(reportDefinitionId, parameters, connection);
    } catch (ReportDefinitionNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to create the PDF for the report using the report definition ("
              + reportDefinitionId
              + ")",
          e);
    }
  }

  @Override
  public byte[] createReportPDF(
      String reportDefinitionId, Map<String, Object> parameters, Connection connection)
      throws InvalidArgumentException, ReportDefinitionNotFoundException,
          ServiceUnavailableException {
    if (!StringUtils.hasText(reportDefinitionId)) {
      throw new InvalidArgumentException("reportDefinitionId");
    }

    if (parameters == null) {
      throw new InvalidArgumentException("parameters");
    }

    if (connection == null) {
      throw new InvalidArgumentException("connection");
    }

    try {
      Optional<ReportDefinition> reportDefinitionOptional =
          reportDefinitionRepository.findById(reportDefinitionId);

      if (reportDefinitionOptional.isEmpty()) {
        throw new ReportDefinitionNotFoundException(reportDefinitionId);
      }

      Map<String, Object> localParameters = new HashMap<>();

      if (!StringUtils.hasText(getLocalReportFolderPath())) {
        localParameters.put("SUBREPORT_DIR", getLocalReportFolderPath());
      }

      for (String name : parameters.keySet()) {
        localParameters.put(name, parameters.get(name));
      }

      JasperPrint jasperPrint =
          JasperFillManager.fillReport(
              new ByteArrayInputStream(reportDefinitionOptional.get().getTemplate()),
              localParameters,
              connection);

      return JasperExportManager.exportReportToPdf(jasperPrint);
    } catch (ReportDefinitionNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to create the PDF for the report using the report definition ("
              + reportDefinitionId
              + ")",
          e);
    }
  }

  @Override
  public byte[] createReportPDF(
      String reportDefinitionId, Map<String, Object> parameters, Document document)
      throws InvalidArgumentException, ReportDefinitionNotFoundException,
          ServiceUnavailableException {
    if (!StringUtils.hasText(reportDefinitionId)) {
      throw new InvalidArgumentException("reportDefinitionId");
    }

    if (parameters == null) {
      throw new InvalidArgumentException("parameters");
    }

    if (document == null) {
      throw new InvalidArgumentException("document");
    }

    try {
      ReportDefinition reportDefinition = getReportDefinition(reportDefinitionId);

      Map<String, Object> localParameters = new HashMap<>();

      localParameters.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, document);
      localParameters.put(JRXPathQueryExecuterFactory.XML_DATE_PATTERN, "yyyy-MM-dd");
      localParameters.put(JRXPathQueryExecuterFactory.XML_NUMBER_PATTERN, "#,##0.##");
      localParameters.put(JRXPathQueryExecuterFactory.XML_LOCALE, Locale.ENGLISH);
      localParameters.put(JRParameter.REPORT_LOCALE, Locale.US);

      if (!StringUtils.hasText(getLocalReportFolderPath())) {
        localParameters.put("SUBREPORT_DIR", getLocalReportFolderPath());
      }

      for (String name : parameters.keySet()) {
        localParameters.put(name, parameters.get(name));
      }

      JasperPrint jasperPrint =
          JasperFillManager.fillReport(
              new ByteArrayInputStream(reportDefinition.getTemplate()), localParameters);

      return JasperExportManager.exportReportToPdf(jasperPrint);
    } catch (ReportDefinitionNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to create the PDF for the report using the report definintion ("
              + reportDefinitionId
              + ")",
          e);
    }
  }

  @Override
  @Transactional
  public void deleteReportDefinition(String reportDefinitionId)
      throws InvalidArgumentException, ReportDefinitionNotFoundException,
          ServiceUnavailableException {
    if (!StringUtils.hasText(reportDefinitionId)) {
      throw new InvalidArgumentException("reportDefinitionId");
    }

    try {
      if (!reportDefinitionRepository.existsById(reportDefinitionId)) {
        throw new ReportDefinitionNotFoundException(reportDefinitionId);
      }

      reportDefinitionRepository.deleteById(reportDefinitionId);
    } catch (ReportDefinitionNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to delete the report definition (" + reportDefinitionId + ")", e);
    }
  }

  @Override
  public String getLocalReportFolderPath() {
    return localReportFolderPath;
  }

  @Override
  public ReportDefinition getReportDefinition(String reportDefinitionId)
      throws InvalidArgumentException, ReportDefinitionNotFoundException,
          ServiceUnavailableException {
    if (!StringUtils.hasText(reportDefinitionId)) {
      throw new InvalidArgumentException("reportDefinitionId");
    }

    try {
      Optional<ReportDefinition> reportDefinitionOptional =
          reportDefinitionRepository.findById(reportDefinitionId);

      if (reportDefinitionOptional.isPresent()) {
        return reportDefinitionOptional.get();
      } else {
        throw new ReportDefinitionNotFoundException(reportDefinitionId);
      }
    } catch (ReportDefinitionNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the report definition (" + reportDefinitionId + ")", e);
    }
  }

  @Override
  public String getReportDefinitionName(String reportDefinitionId)
      throws InvalidArgumentException, ReportDefinitionNotFoundException,
          ServiceUnavailableException {
    if (!StringUtils.hasText(reportDefinitionId)) {
      throw new InvalidArgumentException("reportDefinitionId");
    }

    try {
      Optional<String> nameOptional = reportDefinitionRepository.getNameById(reportDefinitionId);

      if (nameOptional.isPresent()) {
        return nameOptional.get();
      }

      throw new ReportDefinitionNotFoundException(reportDefinitionId);
    } catch (ReportDefinitionNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the name of the report definition (" + reportDefinitionId + ")", e);
    }
  }

  @Override
  public List<ReportDefinitionSummary> getReportDefinitionSummaries()
      throws ServiceUnavailableException {
    try {
      return reportDefinitionSummaryRepository.findAll();
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the summaries for the report definitions", e);
    }
  }

  @Override
  public ReportDefinitionSummary getReportDefinitionSummary(String reportDefinitionId)
      throws InvalidArgumentException, ReportDefinitionNotFoundException,
          ServiceUnavailableException {
    if (!StringUtils.hasText(reportDefinitionId)) {
      throw new InvalidArgumentException("reportDefinitionId");
    }

    try {
      Optional<ReportDefinitionSummary> reportDefinitionSummaryOptional =
          reportDefinitionSummaryRepository.findById(reportDefinitionId);

      if (reportDefinitionSummaryOptional.isPresent()) {
        return reportDefinitionSummaryOptional.get();
      } else {
        throw new ReportDefinitionNotFoundException(reportDefinitionId);
      }
    } catch (ReportDefinitionNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the summary for the report definition (" + reportDefinitionId + ")",
          e);
    }
  }

  @Override
  public List<ReportDefinition> getReportDefinitions() throws ServiceUnavailableException {
    try {
      return reportDefinitionRepository.findAll();
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to retrieve the report definitions", e);
    }
  }

  @Override
  public boolean reportDefinitionExists(String reportDefinitionId)
      throws InvalidArgumentException, ServiceUnavailableException {
    if (!StringUtils.hasText(reportDefinitionId)) {
      throw new InvalidArgumentException("reportDefinitionId");
    }

    try {
      return reportDefinitionRepository.existsById(reportDefinitionId);
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to check whether the report definition (" + reportDefinitionId + ") exists", e);
    }
  }

  @Override
  public void setLocalReportFolderPath(String localReportFolderPath) {
    this.localReportFolderPath = localReportFolderPath;
  }

  @Override
  @Transactional
  public void updateReportDefinition(ReportDefinition reportDefinition)
      throws InvalidArgumentException, ReportDefinitionNotFoundException,
          ServiceUnavailableException {
    validateReportDefinition(reportDefinition);

    try {
      if (!reportDefinitionRepository.existsById(reportDefinition.getId())) {
        throw new ReportDefinitionNotFoundException(reportDefinition.getId());
      }

      reportDefinitionRepository.saveAndFlush(reportDefinition);
    } catch (ReportDefinitionNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to update the report definition (" + reportDefinition.getId() + ")", e);
    }
  }

  private void validateReportDefinition(ReportDefinition reportDefinition)
      throws InvalidArgumentException {
    if (reportDefinition == null) {
      throw new InvalidArgumentException("reportDefinition");
    }

    Set<ConstraintViolation<ReportDefinition>> constraintViolations =
        validator.validate(reportDefinition);

    if (!constraintViolations.isEmpty()) {
      throw new InvalidArgumentException(
          "reportDefinition", ValidationError.toValidationErrors(constraintViolations));
    }
  }
}
