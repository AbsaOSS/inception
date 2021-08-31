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

package demo.api;

import africa.absa.inception.api.ProblemDetails;
import africa.absa.inception.core.service.InvalidArgumentException;
import africa.absa.inception.core.service.ServiceUnavailableException;
import demo.model.Data;
import demo.model.IDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * The <b>DataRestController</b> class.
 *
 * @author Marcus Portmann
 */
@Tag(name = "Data")
@RestController
@RequestMapping(value = "/api/data")
@CrossOrigin
@SuppressWarnings({"unused"})
public class DataApi {

  private final IDataService dataService;

  /**
   * Constructs a new <b>DataRestController</b>.
   *
   * @param dataService the Data Service
   */
  public DataApi(IDataService dataService) {
    this.dataService = dataService;
  }

  /**
   * Returns all the data.
   *
   * @return the data
   * @throws ServiceUnavailableException if the data could not be retrieved
   */
  @RequestMapping(value = "/all-data", method = RequestMethod.GET, produces = "application/json")
  public List<Data> getAllData() throws ServiceUnavailableException {
    return dataService.getAllData();
  }

  /**
   * Retrieve the data.
   *
   * @return the data
   * @throws ServiceUnavailableException if the data could not be retrieved
   */
  @RequestMapping(value = "/data", method = RequestMethod.GET, produces = "application/json")
  public Data getData() throws ServiceUnavailableException {
    long id = System.currentTimeMillis();

    Data data = new Data(id, 777, "Test Value " + id, LocalDate.now(), LocalDateTime.now());

    dataService.createData(data);

    data = dataService.getData(data.getId());

    return data;
  }

  /**
   * Validate the data.
   *
   * @param data the data to validate
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the data could not be validated
   */
  @Operation(summary = "Validate the data", description = "Validate the data")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "The data was validated successfully"),
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
  @RequestMapping(value = "/validate", method = RequestMethod.POST, produces = "application/json")
  public void validate(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The data to validate",
              required = true)
          @RequestBody
          Data data)
      throws InvalidArgumentException, ServiceUnavailableException {
    dataService.validateData(data);
  }
}
