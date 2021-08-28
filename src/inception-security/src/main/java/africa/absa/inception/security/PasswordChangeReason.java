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

package africa.absa.inception.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The <b>PasswordChangeReason</b> enumeration defines the possible reasons for why a user's
 * password was changed.
 *
 * @author Marcus Portmann
 */
@Schema(description = "The password change reason")
public enum PasswordChangeReason {
  USER("user", "User"),
  ADMINISTRATIVE("administrative", "Administrative"),
  RESET("reset", "Reset");

  private final String code;

  private final String description;

  PasswordChangeReason(String code, String description) {
    this.code = code;
    this.description = description;
  }

  /**
   * Returns the password change reason given by the specified code value.
   *
   * @param code the code for the password change reason
   * @return the password change reason given by the specified code value
   */
  @JsonCreator
  public static PasswordChangeReason fromCode(String code) {
    switch (code) {
      case "user":
        return PasswordChangeReason.USER;
      case "administrative":
        return PasswordChangeReason.ADMINISTRATIVE;
      case "reset":
        return PasswordChangeReason.RESET;
      default:
        throw new RuntimeException(
            "Failed to determine the password change reason with the invalid code (" + code + ")");
    }
  }

  /**
   * Returns the code for the password change reason.
   *
   * @return the code for the password change reason
   */
  @JsonValue
  public String code() {
    return code;
  }

  /**
   * Returns the description for the password change reason.
   *
   * @return the description for the password change reason
   */
  public String description() {
    return description;
  }
}
