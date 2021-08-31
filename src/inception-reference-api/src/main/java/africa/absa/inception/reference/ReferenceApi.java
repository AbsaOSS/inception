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

package africa.absa.inception.reference;

import africa.absa.inception.api.ProblemDetails;
import africa.absa.inception.api.SecureApi;
import africa.absa.inception.core.service.InvalidArgumentException;
import africa.absa.inception.core.service.ServiceUnavailableException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * The <b>ReferenceApi</b> class.
 *
 * @author Marcus Portmann
 */
@Tag(name = "Reference")
@RestController
@RequestMapping(value = "/api/reference")
@CrossOrigin
@SuppressWarnings({"unused", "WeakerAccess"})
// @el (isSecurityDisabled: africa.absa.inception.api.ApiSecurityExpressionRoot.isSecurityEnabled)
public class ReferenceApi extends SecureApi {

  /** The Reference Service. */
  private final IReferenceService referenceService;

  /**
   * Constructs a new <b>ReferenceRestController</b>.
   *
   * @param applicationContext the Spring application context
   * @param referenceService the Reference Service
   */
  public ReferenceApi(ApplicationContext applicationContext, IReferenceService referenceService) {
    super(applicationContext);

    this.referenceService = referenceService;
  }

  /**
   * Retrieve the country reference data for a specific locale.
   *
   * @param localeId the Unicode locale identifier for the locale to retrieve the country reference
   *     data for
   * @return the country reference data
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the country reference data could not be retrieved
   */
  @Operation(
      summary = "Retrieve the country reference data for a specific locale",
      description = "Retrieve the country reference data for a specific locale")
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
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(value = "/countries", method = RequestMethod.GET, produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("isSecurityDisabled() or isAuthenticated()")
  public List<Country> getCountries(
      @Parameter(
              name = "localeId",
              description =
                  "The Unicode locale identifier for the locale to retrieve the country reference data for",
              example = "en-US")
          @RequestParam(
              value = "localeId",
              required = false,
              defaultValue = IReferenceService.DEFAULT_LOCALE_ID)
          String localeId)
      throws InvalidArgumentException, ServiceUnavailableException {
    return referenceService.getCountries(localeId);
  }

  /**
   * Retrieve the language reference data for a specific locale.
   *
   * @param localeId the Unicode locale identifier for the locale to retrieve the language reference
   *     data for
   * @return the language reference data
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the language reference data could not be retrieved
   */
  @Operation(
      summary = "Retrieve the language reference data for a specific locale",
      description = "Retrieve the language reference data for a specific locale")
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
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(value = "/languages", method = RequestMethod.GET, produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("isSecurityDisabled() or isAuthenticated()")
  public List<Language> getLanguages(
      @Parameter(
              name = "localeId",
              description =
                  "The Unicode locale identifier for the locale to retrieve the language reference data for",
              example = "en-US")
          @RequestParam(
              value = "localeId",
              required = false,
              defaultValue = IReferenceService.DEFAULT_LOCALE_ID)
          String localeId)
      throws InvalidArgumentException, ServiceUnavailableException {
    return referenceService.getLanguages(localeId);
  }

  /**
   * Retrieve the measurement system reference data for a specific locale.
   *
   * @param localeId the Unicode locale identifier for the locale to retrieve the measurement system
   *     reference data for
   * @return the measurement system reference data
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the measurement system reference data could not be
   *     retrieved
   */
  @Operation(
      summary = "Retrieve the measurement system reference data for a specific locale",
      description = "Retrieve the measurement system reference data for a specific locale")
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
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/measurement-systems",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("isSecurityDisabled() or isAuthenticated()")
  public List<MeasurementSystem> getMeasurementSystems(
      @Parameter(
              name = "localeId",
              description =
                  "The Unicode locale identifier for the locale to retrieve the measurement system reference data for",
              example = "en-US")
          @RequestParam(
              value = "localeId",
              required = false,
              defaultValue = IReferenceService.DEFAULT_LOCALE_ID)
          String localeId)
      throws InvalidArgumentException, ServiceUnavailableException {
    return referenceService.getMeasurementSystems(localeId);
  }

  /**
   * Retrieve the measurement unit type reference data for a specific locale.
   *
   * @param localeId the Unicode locale identifier for the locale to retrieve the measurement unit
   *     type reference data for
   * @return the measurement unit type reference data
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the measurement unit type reference data could not be
   *     retrieved
   */
  @Operation(
      summary = "Retrieve the measurement unit type reference data for a specific locale",
      description = "Retrieve the measurement unit type reference data for a specific locale")
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
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/measurement-unit-types",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("isSecurityDisabled() or isAuthenticated()")
  public List<MeasurementUnitType> getMeasurementUnitTypes(
      @Parameter(
              name = "localeId",
              description =
                  "The Unicode locale identifier for the locale to retrieve the measurement unit type reference data for",
              example = "en-US")
          @RequestParam(
              value = "localeId",
              required = false,
              defaultValue = IReferenceService.DEFAULT_LOCALE_ID)
          String localeId)
      throws InvalidArgumentException, ServiceUnavailableException {
    return referenceService.getMeasurementUnitTypes(localeId);
  }

  /**
   * Retrieve the measurement unit reference data for a specific locale.
   *
   * @param localeId the Unicode locale identifier for the locale to retrieve the measurement unit
   *     reference data
   * @return the measurement unit reference data
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the measurement unit reference data could not be
   *     retrieved
   */
  @Operation(
      summary = "Retrieve the measurement unit reference data for a specific locale",
      description = "Retrieve the measurement unit reference data for a specific locale")
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
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/measurement-units",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("isSecurityDisabled() or isAuthenticated()")
  public List<MeasurementUnit> getMeasurementUnits(
      @Parameter(
              name = "localeId",
              description =
                  "The Unicode locale identifier for the locale to retrieve the measurement unit reference data for",
              example = "en-US")
          @RequestParam(
              value = "localeId",
              required = false,
              defaultValue = IReferenceService.DEFAULT_LOCALE_ID)
          String localeId)
      throws InvalidArgumentException, ServiceUnavailableException {
    return referenceService.getMeasurementUnits(localeId);
  }

  /**
   * Retrieve the region reference data for a specific locale.
   *
   * @param localeId the Unicode locale identifier for the locale to retrieve the region reference
   *     data for
   * @return the region reference data
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the region reference data could not be retrieved
   */
  @Operation(
      summary = "Retrieve the region reference data for a specific locale",
      description = "Retrieve the region reference data for a specific locale")
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
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(value = "/regions", method = RequestMethod.GET, produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("isSecurityDisabled() or isAuthenticated()")
  public List<Region> getRegions(
      @Parameter(
              name = "localeId",
              description =
                  "The Unicode locale identifier for the locale to retrieve the region reference data for",
              example = "en-US")
          @RequestParam(
              value = "localeId",
              required = false,
              defaultValue = IReferenceService.DEFAULT_LOCALE_ID)
          String localeId)
      throws InvalidArgumentException, ServiceUnavailableException {
    return referenceService.getRegions(localeId);
  }

  /**
   * Retrieve the time zone reference data for a specific locale.
   *
   * @param localeId the Unicode locale identifier for the locale to retrieve the time zone
   *     reference data for
   * @return the time zone reference data
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the time zones reference data could not be retrieved
   */
  @Operation(
      summary = "Retrieve the time zone reference data for a specific locale",
      description = "Retrieve the time zone reference data for a specific locale")
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
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(value = "/time-zones", method = RequestMethod.GET, produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("isSecurityDisabled() or isAuthenticated()")
  public List<TimeZone> getTimeZones(
      @Parameter(
              name = "localeId",
              description =
                  "The Unicode locale identifier for the locale to retrieve the time zone reference data for",
              example = "en-US")
          @RequestParam(
              value = "localeId",
              required = false,
              defaultValue = IReferenceService.DEFAULT_LOCALE_ID)
          String localeId)
      throws InvalidArgumentException, ServiceUnavailableException {
    return referenceService.getTimeZones(localeId);
  }
}
