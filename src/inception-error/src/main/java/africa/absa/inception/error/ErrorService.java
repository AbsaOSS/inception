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
import africa.absa.inception.core.service.ValidationError;
import africa.absa.inception.core.sorting.SortDirection;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * The <b>ErrorService</b> class provides the Error Service implementation.
 *
 * @author Marcus Portmann
 */
@Service
@SuppressWarnings({"unused"})
public class ErrorService implements IErrorService {

  /** The maximum number of filtered error report summaries. */
  private static final int MAX_FILTERED_ERROR_REPORT_SUMMARIES = 100;

  /* Logger */
  private static final Logger logger = LoggerFactory.getLogger(ErrorService.class);

  /** The Spring application context. */
  private final ApplicationContext applicationContext;

  /** The Error Report Repository. */
  private final ErrorReportRepository errorReportRepository;

  /** The Error Report Summary Repository. */
  private final ErrorReportSummaryRepository errorReportSummaryRepository;

  /** The JSR-303 validator. */
  private final Validator validator;

  /** Is debugging enabled for the Inception Framework? */
  @Value("${inception.debug.enabled:#{false}}")
  private boolean inDebugMode;

  /**
   * Constructs a new <b>ErrorService</b>.
   *
   * @param applicationContext the Spring application context
   * @param validator the JSR-303 validator
   * @param errorReportRepository the Error Report Repository
   * @param errorReportSummaryRepository the Error Report Summary Repository
   */
  public ErrorService(
      ApplicationContext applicationContext,
      Validator validator,
      ErrorReportRepository errorReportRepository,
      ErrorReportSummaryRepository errorReportSummaryRepository) {
    this.applicationContext = applicationContext;
    this.validator = validator;
    this.errorReportRepository = errorReportRepository;
    this.errorReportSummaryRepository = errorReportSummaryRepository;
  }

  /**
   * Create the new error report.
   *
   * @param errorReport the <b>ErrorReport</b> instance containing the information for the error
   *     report
   */
  @Override
  @Transactional
  public void createErrorReport(ErrorReport errorReport)
      throws InvalidArgumentException, ServiceUnavailableException {
    if (errorReport == null) {
      throw new InvalidArgumentException("errorReport");
    }

    // Truncate the detail if required
    if ((errorReport.getDetail() != null)
        && (errorReport.getDetail().length() > ErrorReport.MAX_DETAIL_SIZE)) {
      errorReport.setDetail(
          errorReport.getDetail().substring(0, ErrorReport.MAX_DETAIL_SIZE - 3) + "...");
    }

    Set<ConstraintViolation<ErrorReport>> constraintViolations = validator.validate(errorReport);

    if (!constraintViolations.isEmpty()) {
      throw new InvalidArgumentException(
          "errorReport", ValidationError.toValidationErrors(constraintViolations));
    }

    try {
      String description = errorReport.getDescription();

      if (description.length() > 4000) {
        description = description.substring(0, 4000);
      }

      errorReport.setDescription(description);

      String detail = StringUtils.hasText(errorReport.getDetail()) ? errorReport.getDetail() : "";

      if (detail.length() > 4000) {
        detail = detail.substring(0, 4000);
      }

      errorReport.setDetail(detail);

      String who = errorReport.getWho();

      if ((who != null) && (who.length() > 1000)) {
        who = who.substring(0, 1000);
      }

      errorReport.setWho(who);

      String feedback = errorReport.getFeedback();

      if ((feedback != null) && (feedback.length() > 4000)) {
        feedback = feedback.substring(0, 4000);
      }

      errorReport.setFeedback(feedback);

      errorReportRepository.saveAndFlush(errorReport);

      if (inDebugMode) {
        ObjectMapper objectMapper = applicationContext.getBean(ObjectMapper.class);

        if (objectMapper != null) {
          logger.info(
              "Error Report: "
                  + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorReport));
        }
      }
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to create the error report (" + errorReport.getId() + ")", e);
    }
  }

  /**
   * Retrieve the error report.
   *
   * @param errorReportId the Universally Unique Identifier (UUID) for the error report
   * @return the error report
   */
  @Override
  public ErrorReport getErrorReport(UUID errorReportId)
      throws InvalidArgumentException, ErrorReportNotFoundException, ServiceUnavailableException {
    if (errorReportId == null) {
      throw new InvalidArgumentException("errorReportId");
    }

    try {
      Optional<ErrorReport> errorReportOptional = errorReportRepository.findById(errorReportId);

      if (errorReportOptional.isPresent()) {
        return errorReportOptional.get();
      } else {
        throw new ErrorReportNotFoundException(errorReportId);
      }
    } catch (ErrorReportNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the error report (" + errorReportId + ")", e);
    }
  }

  /**
   * Retrieve the error report summaries.
   *
   * @param filter the optional filter to apply to the error reports
   * @param sortBy the optional method used to sort the error reports e.g. by who submitted them
   * @param sortDirection the optional sort direction to apply to the error reports
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the error report summaries
   */
  @Override
  public ErrorReportSummaries getErrorReportSummaries(
      String filter,
      ErrorReportSortBy sortBy,
      SortDirection sortDirection,
      Integer pageIndex,
      Integer pageSize)
      throws InvalidArgumentException, ServiceUnavailableException {
    if ((pageIndex != null) && (pageIndex < 0)) {
      throw new InvalidArgumentException("pageIndex");
    }

    if ((pageSize != null) && (pageSize <= 0)) {
      throw new InvalidArgumentException("pageSize");
    }

    if (sortBy == null) {
      sortBy = ErrorReportSortBy.CREATED;
    }

    if (sortDirection == null) {
      sortDirection = SortDirection.ASCENDING;
    }

    try {
      PageRequest pageRequest;

      if (pageIndex == null) {
        pageIndex = 0;
      }

      if (pageSize == null) {
        pageSize = MAX_FILTERED_ERROR_REPORT_SUMMARIES;
      }

      if (sortBy == ErrorReportSortBy.WHO) {
        pageRequest =
            PageRequest.of(
                pageIndex,
                Math.min(pageSize, MAX_FILTERED_ERROR_REPORT_SUMMARIES),
                (sortDirection == SortDirection.ASCENDING)
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC,
                "who");
      } else {
        pageRequest =
            PageRequest.of(
                pageIndex,
                Math.min(pageSize, MAX_FILTERED_ERROR_REPORT_SUMMARIES),
                (sortDirection == SortDirection.ASCENDING)
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC,
                "created");
      }

      Page<ErrorReportSummary> errorReportSummaryPage;
      if (StringUtils.hasText(filter)) {
        errorReportSummaryPage =
            errorReportSummaryRepository.findFiltered("%" + filter + "%", pageRequest);
      } else {
        errorReportSummaryPage = errorReportSummaryRepository.findAll(pageRequest);
      }

      return new ErrorReportSummaries(
          errorReportSummaryPage.toList(),
          errorReportSummaryPage.getTotalElements(),
          filter,
          sortBy,
          sortDirection,
          pageIndex,
          pageSize);
    } catch (Throwable e) {

      logger.error("Failed to retrieve the filtered error report summaries", e);

      throw new ServiceUnavailableException(
          "Failed to retrieve the filtered error report summaries", e);
    }
  }

  /**
   * Retrieve the summary for the error report.
   *
   * @param errorReportId the Universally Unique Identifier (UUID) for the error report
   * @return the summary for the error report
   */
  @Override
  public ErrorReportSummary getErrorReportSummary(UUID errorReportId)
      throws InvalidArgumentException, ErrorReportNotFoundException, ServiceUnavailableException {
    if (errorReportId == null) {
      throw new InvalidArgumentException("errorReportId");
    }

    try {
      Optional<ErrorReportSummary> errorReportSummaryOptional =
          errorReportSummaryRepository.findById(errorReportId);

      if (errorReportSummaryOptional.isPresent()) {
        return errorReportSummaryOptional.get();
      } else {
        throw new ErrorReportNotFoundException(errorReportId);
      }
    } catch (ErrorReportNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the summary for the error report (" + errorReportId + ")", e);
    }
  }

  /**
   * Retrieve the summaries for the most recent error reports.
   *
   * @param maximumNumberOfEntries the maximum number of summaries for the most recent error reports
   *     to retrieve
   * @return the summaries for the most recent error reports
   */
  @Override
  public List<ErrorReportSummary> getMostRecentErrorReportSummaries(int maximumNumberOfEntries)
      throws InvalidArgumentException, ServiceUnavailableException {
    if (maximumNumberOfEntries < 0) {
      throw new InvalidArgumentException("maximumNumberOfEntries");
    }

    try {
      PageRequest pageRequest =
          PageRequest.of(0, maximumNumberOfEntries, Sort.Direction.DESC, "created");

      Page<ErrorReportSummary> errorReportSummaryPage =
          errorReportSummaryRepository.findAll(pageRequest);

      return errorReportSummaryPage.getContent();
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the summaries for the most recent error reports", e);
    }
  }
}
