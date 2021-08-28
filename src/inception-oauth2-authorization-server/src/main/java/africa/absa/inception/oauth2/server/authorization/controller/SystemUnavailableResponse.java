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

import africa.absa.inception.core.util.ISO8601Util;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

/**
 * The <b>SystemUnavailableResponse</b> class holds the information for a response returned to
 * indicated that the system encountered an error and is unable to process the OAuth2 request.
 *
 * @author Marcus Portmann
 */
public class SystemUnavailableResponse extends Response {

  /** The message. */
  private final String message;

  /** The date and time the error occurred. */
  private final LocalDateTime timestamp;

  /** The optional detail. */
  private String detail;

  /** The optional fully qualified name of the exception associated with the error. */
  private String exception;

  /** The optional stack trace associated with the error. */
  private String stackTrace;

  /** The URI for the HTTP request that resulted in the error. */
  @JsonProperty private String uri;

  /**
   * Constructs a new <b>SystemUnavailableResponse</b>.
   *
   * @param message the message
   */
  public SystemUnavailableResponse(String message) {
    this(message, null);
  }

  /**
   * Constructs a new <b>SystemUnavailableResponse</b>.
   *
   * @param message the message
   * @param cause the cause
   */
  public SystemUnavailableResponse(String message, Throwable cause) {
    super(HttpStatus.INTERNAL_SERVER_ERROR);

    this.timestamp = LocalDateTime.now();

    this.message = message;

    if (cause != null) {
      this.detail = cause.getMessage();

      this.exception = cause.getClass().getName();

      try {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);

        pw.println(cause.getMessage());
        pw.println();

        cause.printStackTrace(pw);

        pw.flush();

        this.stackTrace = baos.toString();
      } catch (Throwable ignored) {
      }
    }
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
      jsonGenerator.writeStringField("timestamp", ISO8601Util.fromLocalDateTime(timestamp));
      jsonGenerator.writeStringField("message", message);
      if (StringUtils.hasText(detail)) {
        jsonGenerator.writeStringField("detail", detail);
      }
      if (StringUtils.hasText(exception)) {
        jsonGenerator.writeStringField("exception", exception);
      }
      if (StringUtils.hasText(stackTrace)) {
        jsonGenerator.writeStringField("exception", stackTrace);
      }

      jsonGenerator.writeEndObject();

      jsonGenerator.close();

      return stringWriter.getBuffer().toString();
    } catch (Throwable e) {
      throw new RuntimeException(
          "Failed to construct the body for the system unavailable response", e);
    }
  }
}
