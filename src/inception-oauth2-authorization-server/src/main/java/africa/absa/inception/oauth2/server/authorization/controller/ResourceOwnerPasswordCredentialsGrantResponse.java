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
 * The <b>ResourceOwnerPasswordCredentialsGrantResponse</b> class holds the information for a
 * Resource Owner Password Credentials Grant response.
 *
 * @author Marcus Portmann
 */
public class ResourceOwnerPasswordCredentialsGrantResponse extends Response {

  /** The access token. */
  private final String accessToken;

  /** The lifetime in seconds of the access token. */
  private final long expiresIn;

  /** The refresh token. */
  private final String refreshToken;

  /** The access token scope. */
  private final String scope;

  /**
   * Constructs a new <b>ResourceOwnerPasswordCredentialsGrantResponse</b>.
   *
   * @param accessToken the access token
   * @param expiresIn the access token validity in seconds
   * @param scope the access token scope
   * @param refreshToken the refresh token
   */
  public ResourceOwnerPasswordCredentialsGrantResponse(
      String accessToken, int expiresIn, String scope, String refreshToken) {
    super(HttpStatus.OK);

    this.accessToken = accessToken;
    this.expiresIn = expiresIn;
    this.scope = scope;
    this.refreshToken = refreshToken;
  }

  /**
   * Constructs a new <b>ResourceOwnerPasswordCredentialsGrantResponse</b>.
   *
   * @param accessToken the access token
   * @param expiresIn the access token validity in seconds
   * @param refreshToken the refresh token
   */
  public ResourceOwnerPasswordCredentialsGrantResponse(
      String accessToken, long expiresIn, String refreshToken) {
    super(HttpStatus.OK);

    this.accessToken = accessToken;
    this.expiresIn = expiresIn;
    this.scope = null;
    this.refreshToken = refreshToken;
  }

  /**
   * Constructs a new <b>ResourceOwnerPasswordCredentialsGrantResponse</b>.
   *
   * @param accessToken the access token
   * @param expiresIn the lifetime in seconds of the access token
   */
  public ResourceOwnerPasswordCredentialsGrantResponse(String accessToken, long expiresIn) {
    super(HttpStatus.OK);

    this.accessToken = accessToken;
    this.expiresIn = expiresIn;
    this.scope = null;
    this.refreshToken = null;
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
      jsonGenerator.writeStringField("access_token", accessToken);
      jsonGenerator.writeStringField("token_type", "bearer");
      jsonGenerator.writeNumberField("expires_in", expiresIn);

      if (StringUtils.hasText(refreshToken)) {
        jsonGenerator.writeStringField("refresh_token", refreshToken);
      }

      if (StringUtils.hasText(scope)) {
        jsonGenerator.writeStringField("scope", scope);
      }

      jsonGenerator.writeEndObject();
      jsonGenerator.close();

      return stringWriter.getBuffer().toString();
    } catch (Throwable e) {
      throw new RuntimeException(
          "Failed to construct the body for the Resource Owner Password Credentials Grant response",
          e);
    }
  }
}
