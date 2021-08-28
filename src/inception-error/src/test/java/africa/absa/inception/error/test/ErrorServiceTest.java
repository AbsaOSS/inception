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

package africa.absa.inception.error.test;

import static africa.absa.inception.test.Assert.assertEqualsToMillisecond;
import static org.junit.jupiter.api.Assertions.assertEquals;

import africa.absa.inception.core.sorting.SortDirection;
import africa.absa.inception.core.util.Base64Util;
import africa.absa.inception.error.ErrorReport;
import africa.absa.inception.error.ErrorReportSortBy;
import africa.absa.inception.error.ErrorReportSummaries;
import africa.absa.inception.error.ErrorReportSummary;
import africa.absa.inception.error.IErrorService;
import africa.absa.inception.test.InceptionExtension;
import africa.absa.inception.test.TestConfiguration;
import com.github.f4b6a3.uuid.UuidCreator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

/**
 * The <b>ErrorServiceTest</b> class contains the implementation of the JUnit tests for the
 * <b>ErrorService</b> class.
 *
 * @author Marcus Portmann
 */
@ExtendWith(SpringExtension.class)
@ExtendWith(InceptionExtension.class)
@ContextConfiguration(
    classes = {TestConfiguration.class},
    initializers = {ConfigDataApplicationContextInitializer.class})
@TestExecutionListeners(
    listeners = {
      DependencyInjectionTestExecutionListener.class,
      DirtiesContextTestExecutionListener.class,
      TransactionalTestExecutionListener.class
    })
public class ErrorServiceTest {

  /** The Error Service. */
  @Autowired private IErrorService errorService;

  private static synchronized ErrorReport getTestErrorReport() {
    return new ErrorReport(
        UuidCreator.getShortPrefixComb(),
        "ApplicationId",
        "ApplicationVersion",
        "Description",
        "Detail",
        LocalDateTime.now(),
        "Who",
        UuidCreator.getShortPrefixComb(),
        "Feedback",
        Base64Util.encodeBytes("Data".getBytes()));
  }

  /** Test the error report functionality. */
  @Test
  public void errorReportTest() throws Exception {
    ErrorReport errorReport = getTestErrorReport();

    errorService.createErrorReport(errorReport);

    ErrorReport retrievedErrorReport = errorService.getErrorReport(errorReport.getId());

    compareErrorReports(errorReport, retrievedErrorReport);

    ErrorReportSummary retrievedErrorReportSummary =
        errorService.getErrorReportSummary(errorReport.getId());

    compareErrorReportAndErrorReportSummary(errorReport, retrievedErrorReportSummary);

    List<ErrorReportSummary> errorReportSummaries =
        errorService.getMostRecentErrorReportSummaries(1000);

    assertEquals(
        1, errorReportSummaries.size(), "The number of error report summaries is incorrect");

    compareErrorReportAndErrorReportSummary(errorReport, errorReportSummaries.get(0));

    ErrorReportSummaries filteredErrorReportSummaries =
        errorService.getErrorReportSummaries(
            errorReport.getWho(), ErrorReportSortBy.WHO, SortDirection.ASCENDING, 0, 100);

    assertEquals(
        1,
        (long) filteredErrorReportSummaries.getTotal(),
        "The number of filtered error report summaries is incorrect");

    compareErrorReportAndErrorReportSummary(
        errorReport, filteredErrorReportSummaries.getErrorReportSummaries().get(0));
  }

  /** Test the functionality to retrieve the most recent error report summaries. */
  @Test
  public void getMostRecentErrorReportSummariesTest() throws Exception {
    List<ErrorReport> errorReports = new ArrayList<>();

    for (int i = 0; i < 20; i++) {
      ErrorReport errorReport = getTestErrorReport();

      errorService.createErrorReport(errorReport);

      errorReports.add(errorReport);

      Thread.sleep(10L);
    }

    List<ErrorReportSummary> errorReportSummaries =
        errorService.getMostRecentErrorReportSummaries(10);

    assertEquals(
        10, errorReportSummaries.size(), "The number of error report summaries is incorrect");

    for (int i = 0; i < 10; i++) {
      assertEquals(
          errorReportSummaries.get(i).getId(),
          errorReports.get((errorReports.size() - 1) - i).getId(),
          "The error report summary does not match the error report");
    }
  }

  private void compareErrorReportAndErrorReportSummary(
      ErrorReport errorReport, ErrorReportSummary errorReportSummary) {
    assertEquals(
        errorReport.getId(),
        errorReportSummary.getId(),
        "The ID values for the error reports do not match");
    assertEquals(
        errorReport.getApplicationId(),
        errorReportSummary.getApplicationId(),
        "The application ID values for the error reports do not match");
    assertEquals(
        errorReport.getApplicationVersion(),
        errorReportSummary.getApplicationVersion(),
        "The application version values for the error reports do not match");
    assertEquals(
        errorReport.getDescription(),
        errorReportSummary.getDescription(),
        "The description values for the error reports do not match");
    assertEqualsToMillisecond(
        errorReport.getCreated(),
        errorReportSummary.getCreated(),
        "The created values for the error reports do not match");
    assertEquals(
        errorReport.getWho(),
        errorReportSummary.getWho(),
        "The who values for the error reports do not match");
    assertEquals(
        errorReport.getDeviceId(),
        errorReportSummary.getDeviceId(),
        "The device ID values for the error reports do not match");
  }

  private void compareErrorReports(ErrorReport errorReport1, ErrorReport errorReport2) {
    assertEquals(
        errorReport1.getId(),
        errorReport2.getId(),
        "The ID values for the error reports do not match");
    assertEquals(
        errorReport1.getApplicationId(),
        errorReport2.getApplicationId(),
        "The application ID values for the error reports do not match");
    assertEquals(
        errorReport1.getApplicationVersion(),
        errorReport2.getApplicationVersion(),
        "The application version values for the error reports do not match");
    assertEquals(
        errorReport1.getDescription(),
        errorReport2.getDescription(),
        "The description values for the error reports do not match");
    assertEquals(
        errorReport1.getDetail(),
        errorReport2.getDetail(),
        "The detail values for the error reports do not match");
    assertEqualsToMillisecond(
        errorReport1.getCreated(),
        errorReport2.getCreated(),
        "The created values for the error reports do not match");
    assertEquals(
        errorReport1.getWho(),
        errorReport2.getWho(),
        "The who values for the error reports do not match");
    assertEquals(
        errorReport1.getDeviceId(),
        errorReport2.getDeviceId(),
        "The device ID values for the error reports do not match");
    assertEquals(
        errorReport1.getFeedback(),
        errorReport2.getFeedback(),
        "The feedback values for the error reports do not match");
    assertEquals(
        errorReport1.getData(),
        errorReport2.getData(),
        "The data values for the error reports do not match");
  }
}
