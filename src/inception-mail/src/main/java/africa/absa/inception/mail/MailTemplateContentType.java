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

package africa.absa.inception.mail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The <b>MailTemplateContentType</b> enumeration defines the possible content types for mail
 * templates.
 *
 * @author Marcus Portmann
 */
@Schema(description = "The mail template content type")
public enum MailTemplateContentType {
  /** Text. */
  TEXT("text", "Text"),

  /** HTML. */
  HTML("html", "HTML");

  private final String code;

  private final String description;

  MailTemplateContentType(String code, String description) {
    this.code = code;
    this.description = description;
  }

  /**
   * Returns the mail template content type given by the specified code value.
   *
   * @param code the code for the mail template content type
   * @return the mail template content type given by the specified code value
   */
  @JsonCreator
  public static MailTemplateContentType fromCode(String code) {
    switch (code) {
      case "text":
        return MailTemplateContentType.TEXT;

      case "html":
        return MailTemplateContentType.HTML;

      default:
        throw new RuntimeException(
            "Failed to determine the mail template content type with the invalid code ("
                + code
                + ")");
    }
  }

  /**
   * Returns the mail template content type for the specified numeric code.
   *
   * @param numericCode the numeric code for the mail template content type
   * @return the mail template content type given by the specified numeric code value
   */
  public static MailTemplateContentType fromNumericCode(int numericCode) {
    switch (numericCode) {
      case 1:
        return MailTemplateContentType.TEXT;
      case 2:
        return MailTemplateContentType.HTML;
      default:
        throw new RuntimeException(
            "Failed to determine the mail template content type for the numeric code ("
                + numericCode
                + ")");
    }
  }

  /**
   * Returns the numeric code for the mail template content type.
   *
   * @param mailTemplateContentType the mail template content type
   * @return the numeric code for the mail template content type
   */
  public static int toNumericCode(MailTemplateContentType mailTemplateContentType) {
    switch (mailTemplateContentType) {
      case TEXT:
        return 1;
      case HTML:
        return 2;
      default:
        throw new RuntimeException(
            "Failed to determine the numeric code for the mail template content type ("
                + mailTemplateContentType.code()
                + ")");
    }
  }

  /**
   * Returns the code for the mail template content type.
   *
   * @return the code for the mail template content type
   */
  @JsonValue
  public String code() {
    return code;
  }

  /**
   * Returns the description for the mail template content type.
   *
   * @return the description for the mail template content type
   */
  public String description() {
    return description;
  }
}
