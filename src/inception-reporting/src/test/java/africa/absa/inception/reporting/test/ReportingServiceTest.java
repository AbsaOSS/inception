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

package africa.absa.inception.reporting.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import africa.absa.inception.core.util.ResourceUtil;
import africa.absa.inception.reporting.IReportingService;
import africa.absa.inception.reporting.ReportDefinition;
import africa.absa.inception.reporting.ReportDefinitionNotFoundException;
import africa.absa.inception.reporting.ReportDefinitionSummary;
import africa.absa.inception.test.InceptionExtension;
import africa.absa.inception.test.TestConfiguration;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
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
 * The <b>ReportingServiceTest</b> class contains the implementation of the JUnit tests for the
 * <b>ReportingService</b> class.
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
public class ReportingServiceTest {

  private static int reportDefinitionCount;

  /** The data source used to provide connections to the application database. */
  @Autowired private DataSource dataSource;

  /** The Reporting Service. */
  @Autowired private IReportingService reportingService;

  private static synchronized ReportDefinition getTestReportDefinitionDetails() {
    reportDefinitionCount++;

    byte[] testReportTemplate =
        ResourceUtil.getClasspathResource("africa/absa/inception/reporting/test/TestReport.jasper");

    ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setId("TestReport" + reportDefinitionCount);
    reportDefinition.setName("Test Report Definition " + reportDefinitionCount);
    reportDefinition.setTemplate(testReportTemplate);

    return reportDefinition;
  }

  /** Test the create report PDF functionality. */
  @Test
  public void createReportPDFTest() throws Exception {
    ReportDefinition reportDefinition = getTestReportDefinitionDetails();

    reportingService.createReportDefinition(reportDefinition);

    Map<String, Object> parameters = new HashMap<>();

    try (Connection connection = dataSource.getConnection()) {
      reportingService.createReportPDF(reportDefinition.getId(), parameters, connection);
    }

    reportingService.deleteReportDefinition(reportDefinition.getId());
  }

  /** Test the report definition functionality. */
  @Test
  public void reportDefinitionTest() throws Exception {
    ReportDefinition reportDefinition = getTestReportDefinitionDetails();

    reportingService.createReportDefinition(reportDefinition);

    ReportDefinition retrievedReportDefinition =
        reportingService.getReportDefinition(reportDefinition.getId());

    compareReportDefinitions(reportDefinition, retrievedReportDefinition);

    boolean reportDefinitionExists =
        reportingService.reportDefinitionExists(reportDefinition.getId());

    assertTrue(reportDefinitionExists, "The report definition does not exist");

    String retrievedReportDefinitionName =
        reportingService.getReportDefinitionName(reportDefinition.getId());

    assertEquals(
        reportDefinition.getName(),
        retrievedReportDefinitionName,
        "The correct report definition name was not retrieved");

    reportDefinition.setName("Updated " + reportDefinition.getName());

    reportingService.updateReportDefinition(reportDefinition);

    retrievedReportDefinition = reportingService.getReportDefinition(reportDefinition.getId());

    compareReportDefinitions(reportDefinition, retrievedReportDefinition);

    reportDefinitionExists = reportingService.reportDefinitionExists(reportDefinition.getId());

    assertTrue(reportDefinitionExists, "The updated report definition does not exist");

    List<ReportDefinition> reportDefinitions = reportingService.getReportDefinitions();

    assertEquals(
        1, reportDefinitions.size(), "The correct number of report definitions was not retrieved");

    compareReportDefinitions(reportDefinition, reportDefinitions.get(0));

    ReportDefinitionSummary retrievedReportDefinitionSummary =
        reportingService.getReportDefinitionSummary(reportDefinition.getId());

    compareReportDefinitionToReportDefinitionSummary(
        reportDefinition, retrievedReportDefinitionSummary);

    List<ReportDefinitionSummary> reportDefinitionSummaries =
        reportingService.getReportDefinitionSummaries();

    assertEquals(
        1,
        reportDefinitionSummaries.size(),
        "The correct number of report definition summaries was not retrieved");

    compareReportDefinitionToReportDefinitionSummary(
        reportDefinition, reportDefinitionSummaries.get(0));

    reportingService.deleteReportDefinition(reportDefinition.getId());

    try {
      reportingService.getReportDefinition(reportDefinition.getId());

      fail("The report definition that should have been deleted was retrieved successfully");
    } catch (ReportDefinitionNotFoundException ignored) {
    }
  }

  private void compareReportDefinitionToReportDefinitionSummary(
      ReportDefinition reportDefinition, ReportDefinitionSummary reportDefinitionSummary) {
    assertEquals(
        reportDefinition.getId(),
        reportDefinitionSummary.getId(),
        "The ID values for the report definition summaries do not match");
    assertEquals(
        reportDefinition.getName(),
        reportDefinitionSummary.getName(),
        "The name values for the report definition summaries do not match");
  }

  private void compareReportDefinitions(
      ReportDefinition reportDefinition1, ReportDefinition reportDefinition2) {
    assertEquals(
        reportDefinition1.getId(),
        reportDefinition2.getId(),
        "The ID values for the report definitions do not match");
    assertEquals(
        reportDefinition1.getName(),
        reportDefinition2.getName(),
        "The name values for the report definitions do not match");
    assertArrayEquals(
        reportDefinition1.getTemplate(),
        reportDefinition2.getTemplate(),
        "The template values for the report definitions do not match");
  }
}
