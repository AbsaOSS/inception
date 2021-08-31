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

import africa.absa.inception.api.ProblemDetails;
import africa.absa.inception.api.SecureApi;
import africa.absa.inception.core.service.InvalidArgumentException;
import africa.absa.inception.core.service.ServiceUnavailableException;
import africa.absa.inception.core.sorting.SortDirection;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * The <b>ErrorApi</b> class.
 *
 * @author Marcus Portmann
 */
@Tag(name = "Error")
@RestController
@RequestMapping(value = "/api/error")
@CrossOrigin
@SuppressWarnings({"unused"})
// @el (isSecurityDisabled: africa.absa.inception.api.ApiSecurityExpressionRoot.isSecurityEnabled)
public class ErrorApi extends SecureApi {

  /** The Error Service. */
  private final IErrorService errorService;

  /**
   * Constructs a new <b>ErrorRestController</b>.
   *
   * @param applicationContext the Spring application context
   * @param errorService the Error Service
   */
  public ErrorApi(ApplicationContext applicationContext, IErrorService errorService) {
    super(applicationContext);

    this.errorService = errorService;
  }

  /**
   * Create the new error report.
   *
   * @param errorReport the error report
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the error report could not be created
   */
  @Operation(summary = "Create the error report", description = "Create the error report")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "The error report was created successfully"),
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
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/error-reports",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void createErrorReport(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The error report to create",
              required = true)
          @RequestBody
          ErrorReport errorReport)
      throws InvalidArgumentException, ServiceUnavailableException {
    if (errorReport == null) {
      throw new InvalidArgumentException("errorReport");
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null) {
      errorReport.setWho(authentication.getName());
    }

    errorService.createErrorReport(errorReport);
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
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the error report summaries could not be retrieved
   */
  @Operation(
      summary = "Retrieve the error report summaries",
      description = "Retrieve the error report summaries")
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
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/error-report-summaries",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Error.ErrorAdministration')")
  public ErrorReportSummaries getErrorReportSummaries(
      @Parameter(name = "filter", description = "The optional filter to apply to the error reports")
          @RequestParam(value = "filter", required = false)
          String filter,
      @Parameter(
              name = "sortBy",
              description =
                  "The optional method used to sort the error reports e.g. by who submitted them")
          @RequestParam(value = "sortBy", required = false)
          ErrorReportSortBy sortBy,
      @Parameter(
              name = "sortDirection",
              description = "The optional sort direction to apply to the error reports")
          @RequestParam(value = "sortDirection", required = false)
          SortDirection sortDirection,
      @Parameter(name = "pageIndex", description = "The optional page index", example = "0")
          @RequestParam(value = "pageIndex", required = false, defaultValue = "0")
          Integer pageIndex,
      @Parameter(name = "pageSize", description = "The optional page size", example = "10")
          @RequestParam(value = "pageSize", required = false, defaultValue = "10")
          Integer pageSize)
      throws InvalidArgumentException, ServiceUnavailableException {
    if (pageIndex == null) {
      pageIndex = 0;
    }
    if (pageSize == null) {
      pageSize = 10;
    }

    return errorService.getErrorReportSummaries(filter, sortBy, sortDirection, pageIndex, pageSize);
  }
}
