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

import africa.absa.inception.api.ApiUtil;
import africa.absa.inception.api.ProblemDetails;
import africa.absa.inception.api.SecureApi;
import africa.absa.inception.core.service.InvalidArgumentException;
import africa.absa.inception.core.service.ServiceUnavailableException;
import africa.absa.inception.core.service.ValidationError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * The <b>ReportingApi</b> class.
 *
 * @author Marcus Portmann
 */
@Tag(name = "Reporting")
@RestController
@RequestMapping(value = "/api/reporting")
@CrossOrigin
@SuppressWarnings({"unused"})
// @el (isSecurityDisabled: digital.inception.api.ApiSecurityExpressionRoot.isSecurityEnabled)
public class ReportingApi extends SecureApi {

  /** The data source used to provide connections to the application database. */
  private final DataSource dataSource;

  /** The Reporting Service. */
  private final IReportingService reportingService;

  /** The JSR-303 validator. */
  private final Validator validator;

  /**
   * Constructs a new <b>ReportingRestController</b>.
   *
   * @param applicationContext the Spring application context
   * @param dataSource the data source used to provide connections to the application database
   * @param reportingService the Reporting Service
   * @param validator the JSR-303 validator
   */
  public ReportingApi(
      ApplicationContext applicationContext,
      @Qualifier("applicationDataSource") DataSource dataSource,
      IReportingService reportingService,
      Validator validator) {
    super(applicationContext);

    this.dataSource = dataSource;
    this.reportingService = reportingService;
    this.validator = validator;
  }

  /**
   * Create the new report definition.
   *
   * @param reportDefinition the report definition to create
   */
  @Operation(summary = "Create the report definition", description = "Create the report definition")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "The report definition was created successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "409",
            description = "A report definition with the specified ID already exists",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/report-definitions",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Reporting.ReportingAdministration') or hasAuthority('FUNCTION_Reporting.ReportDefinitionAdministration')")
  public void createReportDefinition(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The report definition to create",
              required = true)
          @RequestBody
          ReportDefinition reportDefinition)
      throws InvalidArgumentException, DuplicateReportDefinitionException,
          ServiceUnavailableException {
    reportingService.createReportDefinition(reportDefinition);
  }

  /**
   * Delete the report definition.
   *
   * @param reportDefinitionId the ID for the report definition
   */
  @Operation(summary = "Delete the report definition", description = "Delete the report definition")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "The report definition was deleted successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The report definition could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/report-definitions/{reportDefinitionId}",
      method = RequestMethod.DELETE,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Reporting.ReportingAdministration') or hasAuthority('FUNCTION_Reporting.ReportDefinitionAdministration')")
  public void deleteReportDefinition(
      @Parameter(
              name = "reportDefinitionId",
              description = "The ID for the report definition",
              required = true)
          @PathVariable
          String reportDefinitionId)
      throws InvalidArgumentException, ReportDefinitionNotFoundException,
          ServiceUnavailableException {
    reportingService.deleteReportDefinition(reportDefinitionId);
  }

  /**
   * Generate the PDF report.
   *
   * @param generateReportRequest the request to generate the PDF report
   * @return the PDF report
   */
  @Operation(summary = "Generate the PDF report", description = "Generate the PDF report")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "The PDF report was generated successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The report definition could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/generate-report",
      method = RequestMethod.POST,
      produces = "application/pdf")
  public ResponseEntity<byte[]> generateReport(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The request to generate a report",
              required = true)
          @RequestBody
          GenerateReportRequest generateReportRequest)
      throws InvalidArgumentException, ReportDefinitionNotFoundException,
          ServiceUnavailableException {
    if (generateReportRequest == null) {
      throw new InvalidArgumentException("generateReportRequest");
    }

    Set<ConstraintViolation<GenerateReportRequest>> constraintViolations =
        validator.validate(generateReportRequest);

    if (!constraintViolations.isEmpty()) {
      throw new InvalidArgumentException(
          "generateReportRequest", ValidationError.toValidationErrors(constraintViolations));
    }

    Map<String, Object> parameters = new HashMap<>();

    for (ReportParameter reportParameter : generateReportRequest.getReportParameters()) {
      parameters.put(reportParameter.getName(), reportParameter.getValue());
    }

    ReportDefinitionSummary reportDefinitionSummary =
        reportingService.getReportDefinitionSummary(generateReportRequest.getReportDefinitionId());

    try (Connection connection = dataSource.getConnection()) {
      byte[] reportPDF =
          reportingService.createReportPDF(
              generateReportRequest.getReportDefinitionId(), parameters, connection);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.parseMediaType("application/pdf"));
      headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
      headers.add("Pragma", "no-cache");
      headers.add("Expires", "0");

      String filename = reportDefinitionSummary.getName().replaceAll(" ", "") + ".pdf";
      headers.setContentDispositionFormData(filename, filename);
      headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

      return new ResponseEntity<>(reportPDF, headers, HttpStatus.OK);
    } catch (ReportDefinitionNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to generate the PDF report", e);
    }
  }

  /**
   * Retrieve the report definition.
   *
   * @param reportDefinitionId the ID for the report definition
   * @return the report definition
   */
  @Operation(
      summary = "Retrieve the report definition",
      description = "Retrieve the report definition")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The report definition could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/report-definitions/{reportDefinitionId}",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Reporting.ReportingAdministration') or hasAuthority('FUNCTION_Reporting.ReportDefinitionAdministration')")
  public ReportDefinition getReportDefinition(
      @Parameter(
              name = "reportDefinition",
              description = "The ID for the report definition",
              required = true)
          @PathVariable
          String reportDefinitionId)
      throws InvalidArgumentException, ReportDefinitionNotFoundException,
          ServiceUnavailableException {
    return reportingService.getReportDefinition(reportDefinitionId);
  }

  /**
   * Retrieve the name of the report definition.
   *
   * @param reportDefinitionId the ID for the report definition
   * @return the name of the report definition
   */
  @Operation(
      summary = "Retrieve the name of the report definition",
      description = "Retrieve the name of the report definition")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The report definition could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/report-definitions/{reportDefinitionId}/name",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public String getReportDefinitionName(
      @Parameter(
              name = "reportDefinitionId",
              description = "The ID for the report definition",
              required = true)
          @PathVariable
          String reportDefinitionId)
      throws InvalidArgumentException, ReportDefinitionNotFoundException,
          ServiceUnavailableException {
    return ApiUtil.quote(reportingService.getReportDefinitionName(reportDefinitionId));
  }

  /**
   * Retrieve the report definition summaries.
   *
   * @return the report definition summaries
   */
  @Operation(
      summary = "Retrieve the report definition summaries",
      description = "Retrieve the report definition summaries")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/report-definition-summaries",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Reporting.ReportingAdministration') or hasAuthority('FUNCTION_Reporting.ReportDefinitionAdministration')")
  public List<ReportDefinitionSummary> getReportDefinitionSummaries()
      throws ServiceUnavailableException {
    return reportingService.getReportDefinitionSummaries();
  }

  /**
   * Retrieve the report definitions.
   *
   * @return the report definitions
   */
  @Operation(
      summary = "Retrieve the report definitions",
      description = "Retrieve the report definitions")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/report-definitions",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Reporting.ReportingAdministration') or hasAuthority('FUNCTION_Reporting.ReportDefinitionAdministration')")
  public List<ReportDefinition> getReportDefinitions() throws ServiceUnavailableException {
    return reportingService.getReportDefinitions();
  }

  /**
   * Update the report definition.
   *
   * @param reportDefinitionId the ID for the report definition
   * @param reportDefinition the report definition
   */
  @Operation(summary = "Update the report definition", description = "Update the report definition")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "The report definition was updated successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The report definition could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/report-definitions/{reportDefinitionId}",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Reporting.ReportingAdministration') or hasAuthority('FUNCTION_Reporting.ReportDefinitionAdministration')")
  public void updateReportDefinition(
      @Parameter(
              name = "reportDefinitionId",
              description = "The ID for the report definition",
              required = true)
          @PathVariable
          String reportDefinitionId,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The report definition to update",
              required = true)
          @RequestBody
          ReportDefinition reportDefinition)
      throws InvalidArgumentException, ReportDefinitionNotFoundException,
          ServiceUnavailableException {
    if (!StringUtils.hasText(reportDefinitionId)) {
      throw new InvalidArgumentException("reportDefinitionId");
    }

    if (reportDefinition == null) {
      throw new InvalidArgumentException("reportDefinition");
    }

    if (!reportDefinitionId.equals(reportDefinition.getId())) {
      throw new InvalidArgumentException("reportDefinition");
    }

    reportingService.updateReportDefinition(reportDefinition);
  }
}
