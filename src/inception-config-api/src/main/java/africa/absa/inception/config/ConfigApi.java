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

package africa.absa.inception.config;

import africa.absa.inception.api.ApiUtil;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * The <b>ConfigApi</b> class.
 *
 * @author Marcus Portmann
 */
@Tag(name = "Config")
@RestController
@RequestMapping(value = "/api/config")
@CrossOrigin
@SuppressWarnings({"unused"})
// @el (isSecurityDisabled: africa.absa.inception.api.ApiSecurityExpressionRoot.isSecurityEnabled)
public class ConfigApi extends SecureApi {

  /** The Config Service. */
  private final IConfigService configService;

  /**
   * Constructs a new <b>ConfigApi</b>.
   *
   * @param applicationContext the Spring application context
   * @param configService the Config Service
   */
  public ConfigApi(ApplicationContext applicationContext, IConfigService configService) {
    super(applicationContext);

    this.configService = configService;
  }

  /**
   * Delete the config.
   *
   * @param key the key for the config
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ConfigNotFoundException if the config could not be found
   * @throws ServiceUnavailableException if the config could not be deleted
   */
  @Operation(summary = "Delete the config", description = "Delete the config")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "The config was deleted successfully"),
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
            description = "The config could not be found",
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
      value = "/configs/{key}",
      method = RequestMethod.DELETE,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Config.ConfigAdministration')")
  public void deleteConfig(
      @Parameter(name = "key", description = "The key for the config", required = true)
          @PathVariable
          String key)
      throws InvalidArgumentException, ConfigNotFoundException, ServiceUnavailableException {
    configService.deleteConfig(key);
  }

  /**
   * Retrieve the config.
   *
   * @param key the key for the config
   * @return the config
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ConfigNotFoundException if the config could not be found
   * @throws ServiceUnavailableException if the config could not be retrieved
   */
  @Operation(summary = "Retrieve the config", description = "Retrieve the config")
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
            description = "The config could not be found",
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
      value = "/configs/{key}",
      method = RequestMethod.GET,
      produces = "application/json")
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Config.ConfigAdministration')")
  public Config getConfig(
      @Parameter(name = "key", description = "The key for the config", required = true)
          @PathVariable
          String key)
      throws InvalidArgumentException, ConfigNotFoundException, ServiceUnavailableException {
    return configService.getConfig(key);
  }

  /**
   * Retrieve the config value.
   *
   * @param key the key for the config
   * @return the config value
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ConfigNotFoundException if the config could not be found
   * @throws ServiceUnavailableException if the string config value could not be retrieved
   */
  @Operation(summary = "Retrieve the config value", description = "Retrieve the config value")
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
            description = "The config could not be found",
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
      value = "/configs/{key}/value",
      method = RequestMethod.GET,
      produces = "application/json")
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Config.ConfigAdministration')")
  public String getConfigValue(
      @Parameter(name = "key", description = "The key for the config", required = true)
          @PathVariable
          String key)
      throws InvalidArgumentException, ConfigNotFoundException, ServiceUnavailableException {
    return ApiUtil.quote(configService.getString(key));
  }

  /**
   * Retrieve all the configs.
   *
   * @return the configs
   * @throws ServiceUnavailableException if the configs could not be retrieved
   */
  @Operation(summary = "Retrieve all the configs", description = "Retrieve all the configs")
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
  @RequestMapping(value = "/configs", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Config.ConfigAdministration')")
  public List<Config> getConfigs() throws ServiceUnavailableException {
    return configService.getConfigs();
  }

  /**
   * Set the config.
   *
   * @param config the config
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the config could not be set
   */
  @Operation(summary = "Set the config", description = "Set the config")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "The config was set successfully"),
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
  @RequestMapping(value = "/configs", method = RequestMethod.POST, produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Config.ConfigAdministration')")
  public void setConfig(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The config",
              required = true)
          @RequestBody
          Config config)
      throws InvalidArgumentException, ServiceUnavailableException {
    configService.setConfig(config);
  }
}
