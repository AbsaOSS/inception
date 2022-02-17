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

package africa.absa.inception.ws.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** The enumeration giving the supported types of web service security. */
public enum WebServiceSecurityType {
  /** The value indicating that the web service does not implement a security service. */
  NONE("none", "None"),

  /**
   * The value indicating that the web service implements the WS-Security X509 token authentication
   * security service.
   */
  WSS_SECURITY_X509_TOKEN("wss_security_x509_token", "WS-Security X509 Token"),

  /**
   * The value indicating that the web service implements the WS-Security username token security
   * service.
   */
  WSS_SECURITY_USERNAME_TOKEN("wss_security_username_token", "WS-Security Username Token"),

  /** The value indicating that the web service implements the Mutual SSL security service. */
  MUTUAL_SSL("mutual_ssl", "Mutual SSL"),

  /**
   * The value indicating that the web service implements the basic HTTP authentication security
   * service.
   */
  HTTP_AUTHENTICATION("http_authentication", "HTTP Authentication"),

  /**
   * The value indicating that the web service implements the digest authentication security
   * service.
   */
  DIGEST_AUTHENTICATION("digest_authentication", "Digest Authentication");

  private final String code;

  private final String description;

  WebServiceSecurityType(String code, String description) {
    this.code = code;
    this.description = description;
  }

  /**
   * Returns the web service security type given by the specified code value.
   *
   * @param code the code for the web service security type
   * @return the web service security type given by the specified code value
   */
  @JsonCreator
  public static WebServiceSecurityType fromCode(String code) {
    switch (code) {
      case "none":
        return WebServiceSecurityType.NONE;
      case "wss_security_x509_token":
        return WebServiceSecurityType.WSS_SECURITY_X509_TOKEN;
      case "wss_security_username_token":
        return WebServiceSecurityType.WSS_SECURITY_USERNAME_TOKEN;
      case "mutual_ssl":
        return WebServiceSecurityType.MUTUAL_SSL;
      case "http_authentication":
        return WebServiceSecurityType.HTTP_AUTHENTICATION;
      case "digest_authentication":
        return WebServiceSecurityType.DIGEST_AUTHENTICATION;
      default:
        throw new RuntimeException(
            "Failed to determine the web service security type with the invalid code ("
                + code
                + ")");
    }
  }

  /**
   * Returns the code for the web service security type.
   *
   * @return the code for the web service security type
   */
  @JsonValue
  public String code() {
    return code;
  }

  /**
   * Returns the description for the web service security type.
   *
   * @return the description for the web service security type
   */
  public String description() {
    return description;
  }

  /**
   * Return the string representation of the <b>WebServiceSecurityType</b> enumeration value.
   *
   * @return the string representation of the <b>WebServiceSecurityType</b> enumeration value
   */
  public String toString() {
    return description;
  }
}
