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

import africa.absa.inception.core.service.ServiceUnavailableException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * The <b>TestRestController</b> class.
 *
 * @author Marcus Portmann
 */
@Tag(name = "Test")
@RestController
@RequestMapping(value = "/api/test")
@CrossOrigin
@SuppressWarnings({"unused"})
public class TestApi {

  private final WebClient.Builder webClientBuilder;

  /**
   * Constructs a new <b>TestRestController</b>.
   *
   * @param webClientBuilder the web client builder
   */
  public TestApi(WebClient.Builder webClientBuilder) {
    this.webClientBuilder = webClientBuilder;
  }

  /** Test the exception handling. */
  @RequestMapping(value = "/test-exception-handling", method = RequestMethod.GET)
  public void testExceptionHandling() throws ServiceUnavailableException {
    throw new ServiceUnavailableException("Testing 1.. 2.. 3..");
  }

  /**
   * Test the local date time mapping.
   *
   * @param localDateTime the local date time
   */
  @Operation(
      summary = "Test the local date time serialization",
      description = "Test the local date time serialization")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  @RequestMapping(
      value = "/test-local-date-time",
      method = RequestMethod.GET,
      produces = "application/json")
  public LocalDateTime testLocalDateTime(
      @Parameter(name = "localDateTime", description = "The local date time", required = true)
          @RequestParam("localDateTime")
          LocalDateTime localDateTime)
      throws ServiceUnavailableException {
    System.out.println("localDateTime = " + localDateTime);

    return localDateTime;
  }

  /**
   * Test the zoned date time mapping.
   *
   * @param zonedDateTime the zoned date time
   */
  @Operation(
      summary = "Test the zoned date time serialization",
      description = "Test the zoned date time serialization")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  @RequestMapping(
      value = "/test-zoned-date-time",
      method = RequestMethod.GET,
      produces = "application/json")
  public ZonedDateTime testZonedDateTime(
      @Parameter(name = "zonedDateTime", description = "The zoned date time", required = true)
          @RequestParam("zonedDateTime")
          ZonedDateTime zonedDateTime)
      throws ServiceUnavailableException {
    System.out.println("zonedDateTime = " + zonedDateTime);

    return zonedDateTime;
  }
}
