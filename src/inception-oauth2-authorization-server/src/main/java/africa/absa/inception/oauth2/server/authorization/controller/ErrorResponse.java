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

package africa.absa.inception.oauth2.server.authorization.controller;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.StringWriter;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

/**
 * The <b>ErrorGrantResponse</b> class holds the information for an OAuth2 error response and
 * provides the base class that all OAuth2 error response classes should be derived from.
 *
 * <p>See: <a href="https://tools.ietf.org/html/rfc6749#section-5.2">Error Response</a>
 *
 * @author Marcus Portmann
 */
public abstract class ErrorResponse extends Response {

  /** The single ASCII error code. */
  private final String error;

  /** The optional human-readable ASCII text description of the error. */
  private final String errorDescription;

  /**
   * Constructs a new <b>ErrorGrantResponse</b>.
   *
   * @param status the HTTP status that should be returned for the OAuth2 error response
   * @param error the single ASCII error code
   */
  public ErrorResponse(HttpStatus status, String error) {
    super(status);

    this.error = error;
    this.errorDescription = null;
  }

  /**
   * Constructs a new <b>ErrorResponse</b>.
   *
   * @param status the HTTP status that should be returned for the OAuth2 error response
   * @param error the single ASCII error code
   * @param errorDescription the optional human-readable ASCII text description of the error
   */
  public ErrorResponse(HttpStatus status, String error, String errorDescription) {
    super(status);

    this.error = error;
    this.errorDescription = errorDescription;
  }

  /**
   * Returns the body for the OAuth2 response.
   *
   * @return the body for the OAuth2 response
   */
  @Override
  public String getBody() {
    try {
      JsonFactory jsonFactory = new JsonFactory();

      StringWriter stringWriter = new StringWriter();

      JsonGenerator jsonGenerator = jsonFactory.createGenerator(stringWriter);

      jsonGenerator.writeStartObject();
      jsonGenerator.writeStringField("error", error);
      if (StringUtils.hasText(errorDescription)) {
        jsonGenerator.writeStringField("error_description", errorDescription);
      }
      jsonGenerator.writeEndObject();

      jsonGenerator.close();

      return stringWriter.getBuffer().toString();
    } catch (Throwable e) {
      throw new RuntimeException("Failed to construct the body for the error response", e);
    }
  }

  /**
   * Returns the single ASCII error code.
   *
   * @return the single ASCII error code
   */
  public String getError() {
    return error;
  }

  /**
   * Returns the optional human-readable ASCII text description of the error.
   *
   * @return the optional human-readable ASCII text description of the error
   */
  public String getErrorDescription() {
    return errorDescription;
  }
}
