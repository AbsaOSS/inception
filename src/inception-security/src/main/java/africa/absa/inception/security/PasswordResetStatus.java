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
 * The <b>PasswordResetStatus</b> enumeration defines the possible statuses for a password reset.
 *
 * @author Marcus Portmann
 */
@Schema(description = "The password reset status")
public enum PasswordResetStatus {
  /** Requested. */
  REQUESTED("requested", "Requested"),

  /** Completed. */
  COMPLETED("completed", "Completed"),

  /** Expired. */
  EXPIRED("expired", "Expired");

  private final String code;

  private final String description;

  PasswordResetStatus(String code, String description) {
    this.code = code;
    this.description = description;
  }

  /**
   * Returns the password reset status given by the specified code value.
   *
   * @param code the code for the password reset status
   * @return the password reset status given by the specified code value
   */
  @JsonCreator
  public static PasswordResetStatus fromCode(String code) {
    switch (code) {
      case "requested":
        return PasswordResetStatus.REQUESTED;
      case "completed":
        return PasswordResetStatus.COMPLETED;
      case "expired":
        return PasswordResetStatus.EXPIRED;
      default:
        throw new RuntimeException(
            "Failed to determine the password reset status with the invalid code (" + code + ")");
    }
  }

  /**
   * Returns the numeric code for the password reset status.
   *
   * @param passwordResetStatus the password reset status
   * @return the numeric code for the password reset status
   */
  public static int toNumericCode(PasswordResetStatus passwordResetStatus) {
    switch (passwordResetStatus) {
      case REQUESTED:
        return 1;
      case COMPLETED:
        return 2;
      case EXPIRED:
        return 3;
      default:
        throw new RuntimeException(
            "Failed to determine the numeric code for the password reset status ("
                + passwordResetStatus.code()
                + ")");
    }
  }

  /**
   * Returns the password reset status for the specified numeric code.
   *
   * @param numericCode the numeric code for the password reset status
   * @return the password reset status given by the specified numeric code value
   */
  public static PasswordResetStatus fromNumericCode(int numericCode) {
    switch (numericCode) {
      case 1:
        return PasswordResetStatus.REQUESTED;
      case 2:
        return PasswordResetStatus.COMPLETED;
      case 3:
        return PasswordResetStatus.EXPIRED;
      default:
        throw new RuntimeException(
            "Failed to determine the password reset status for the numeric code ("
                + numericCode
                + ")");
    }
  }

  /**
   * Returns the code value for the password reset status.
   *
   * @return the code value for the password reset status
   */
  @JsonValue
  public String code() {
    return code;
  }

  /**
   * Returns the description for the password reset status.
   *
   * @return the description for the password reset status
   */
  public String description() {
    return description;
  }
}
