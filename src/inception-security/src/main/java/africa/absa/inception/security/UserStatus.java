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
 * The <b>UserStatus</b> enumeration defines the possible statuses for a user.
 *
 * @author Marcus Portmann
 */
@Schema(description = "The user status")
public enum UserStatus {
  /** Inactive. */
  INACTIVE("inactive", "Inactive"),

  /** Active. */
  ACTIVE("active", "Active"),

  /** Locked. */
  LOCKED("locked", "Locked"),

  /** Expired. */
  EXPIRED("expired", "Expired");

  private final String code;

  private final String description;

  UserStatus(String code, String description) {
    this.code = code;
    this.description = description;
  }

  /**
   * Returns the numeric code for the user status.
   *
   * @param userStatus the user status
   * @return the numeric code for the user status
   */
  public static int toNumericCode(UserStatus userStatus) {
    switch (userStatus) {
      case ACTIVE:
        return 1;
      case INACTIVE:
        return 2;
      case LOCKED:
        return 3;
      case EXPIRED:
        return 4;
      default:
        throw new RuntimeException(
            "Failed to determine the numeric code for the user status (" + userStatus.code() + ")");
    }
  }

  /**
   * Returns the user status for the specified numeric code.
   *
   * @param numericCode the numeric code for the user status
   * @return the user status given by the specified numeric code value
   */
  public static UserStatus fromNumericCode(int numericCode) {
    switch (numericCode) {
      case 1:
        return UserStatus.ACTIVE;
      case 2:
        return UserStatus.INACTIVE;
      case 3:
        return UserStatus.LOCKED;
      case 4:
        return UserStatus.EXPIRED;
      default:
        throw new RuntimeException(
            "Failed to determine the user status for the numeric database code ("
                + numericCode
                + ")");
    }
  }

  /**
   * Returns the user status given by the specified code value.
   *
   * @param code the code for the user status
   * @return the user status given by the specified code value
   */
  @JsonCreator
  public static UserStatus fromCode(String code) {
    switch (code) {
      case "inactive":
        return UserStatus.INACTIVE;

      case "active":
        return UserStatus.ACTIVE;

      case "locked":
        return UserStatus.LOCKED;

      case "expired":
        return UserStatus.EXPIRED;

      default:
        throw new RuntimeException(
            "Failed to determine the user status with the invalid code (" + code + ")");
    }
  }

  /**
   * Returns the code value for the user status.
   *
   * @return the code value for the user status
   */
  @JsonValue
  public String code() {
    return code;
  }

  /**
   * Returns the description for the user status.
   *
   * @return the description for the user status
   */
  public String description() {
    return description;
  }
}
