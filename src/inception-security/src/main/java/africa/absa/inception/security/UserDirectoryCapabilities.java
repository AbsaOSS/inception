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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;

/**
 * The <b>UserDirectoryCapabilities</b> class holds the information that describes the capabilities
 * supported by a user directory.
 *
 * @author Marcus Portmann
 */
@Schema(description = "The capabilities supported by a user directory")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "supportsAdminChangePassword",
  "supportsChangePassword",
  "supportsGroupAdministration",
  "supportsGroupMemberAdministration",
  "supportsPasswordExpiry",
  "supportsPasswordHistory",
  "supportsUserAdministration",
  "supportsUserLocks"
})
@SuppressWarnings({"unused"})
public class UserDirectoryCapabilities {

  /** The user directory supports the admin change password capability. */
  @Schema(
      description = "The the user directory supports the admin change password capability",
      required = true)
  @JsonProperty(required = true)
  @NotNull
  private boolean supportsAdminChangePassword;

  /** The user directory supports the change password capability. */
  @Schema(
      description = "The user directory supports the change password capability",
      required = true)
  @JsonProperty(required = true)
  @NotNull
  private boolean supportsChangePassword;

  /** The user directory supports the group administration capability. */
  @Schema(
      description = "The user directory supports the group administration capability",
      required = true)
  @JsonProperty(required = true)
  @NotNull
  private boolean supportsGroupAdministration;

  /** The user directory supports the group member administration capability. */
  @Schema(
      description = "The user directory supports the group member administration capability",
      required = true)
  @JsonProperty(required = true)
  @NotNull
  private boolean supportsGroupMemberAdministration;

  /** The user directory supports the password expiry capability. */
  @Schema(
      description = "The user directory supports the password expiry capability",
      required = true)
  @JsonProperty(required = true)
  @NotNull
  private boolean supportsPasswordExpiry;

  /** The user directory supports the password history capability. */
  @Schema(
      description = "The user directory supports the password history capability",
      required = true)
  @JsonProperty(required = true)
  @NotNull
  private boolean supportsPasswordHistory;

  /** The user directory supports the user administration capability. */
  @Schema(
      description = "The user directory supports the user administration capability",
      required = true)
  @JsonProperty(required = true)
  @NotNull
  private boolean supportsUserAdministration;

  /** The user directory supports the user locks capability. */
  @Schema(description = "The user directory supports the user locks capability", required = true)
  @JsonProperty(required = true)
  @NotNull
  private boolean supportsUserLocks;

  /** Constructs a new <b>UserDirectoryCapabilities</b>. */
  public UserDirectoryCapabilities() {}

  /**
   * Constructs a new <b>UserDirectoryCapabilities</b>.
   *
   * @param supportsAdminChangePassword the user directory supports the admin change password
   *     capability
   * @param supportsChangePassword the user directory supports the change password capability
   * @param supportsGroupAdministration the user directory supports the group administration
   *     capability
   * @param supportsGroupMemberAdministration the user directory supports the group member
   *     administration capability
   * @param supportsPasswordExpiry the user directory supports the password expiry capability
   * @param supportsPasswordHistory the user directory supports the password history capability
   * @param supportsUserAdministration the user directory supports the user administration
   *     capability
   * @param supportsUserLocks the user directory supports the user locks capability
   */
  public UserDirectoryCapabilities(
      boolean supportsAdminChangePassword,
      boolean supportsChangePassword,
      boolean supportsGroupAdministration,
      boolean supportsGroupMemberAdministration,
      boolean supportsPasswordExpiry,
      boolean supportsPasswordHistory,
      boolean supportsUserAdministration,
      boolean supportsUserLocks) {
    this.supportsAdminChangePassword = supportsAdminChangePassword;
    this.supportsChangePassword = supportsChangePassword;
    this.supportsGroupAdministration = supportsGroupAdministration;
    this.supportsGroupMemberAdministration = supportsGroupMemberAdministration;
    this.supportsPasswordExpiry = supportsPasswordExpiry;
    this.supportsPasswordHistory = supportsPasswordHistory;
    this.supportsUserAdministration = supportsUserAdministration;
    this.supportsUserLocks = supportsUserLocks;
  }

  /**
   * Returns whether the user directory supports the admin change password capability.
   *
   * @return <b>true</b> if the user directory supports the admin change password capability or
   *     <b>false</b> otherwise
   */
  public boolean getSupportsAdminChangePassword() {
    return supportsAdminChangePassword;
  }

  /**
   * Returns whether the user directory supports the change password capability.
   *
   * @return <b>true</b> if the user directory supports the change password capability or
   *     <b>false</b> otherwise
   */
  public boolean getSupportsChangePassword() {
    return supportsChangePassword;
  }

  /**
   * Returns whether the user directory supports the group administration capability.
   *
   * @return <b>true</b> if the user directory supports the group administration capability or
   *     <b>false</b> otherwise
   */
  public boolean getSupportsGroupAdministration() {
    return supportsGroupAdministration;
  }

  /**
   * Returns whether the user directory supports the group member administration capability.
   *
   * @return <b>true</b> if the user directory supports the group member administration capability
   *     or <b>false</b> otherwise
   */
  public boolean getSupportsGroupMemberAdministration() {
    return supportsGroupMemberAdministration;
  }

  /**
   * Returns whether the user directory supports the password expiry capability.
   *
   * @return <b>true</b> if the user directory supports the password expiry capability or
   *     <b>false</b> otherwise
   */
  public boolean getSupportsPasswordExpiry() {
    return supportsPasswordExpiry;
  }

  /**
   * Returns whether the user directory supports the password history capability.
   *
   * @return <b>true</b> if the user directory supports the password history capability or
   *     <b>false</b> otherwise
   */
  public boolean getSupportsPasswordHistory() {
    return supportsPasswordHistory;
  }

  /**
   * Returns whether the user directory supports the user administration capability.
   *
   * @return <b>true</b> if the user directory supports the user administration capability or
   *     <b>false</b> otherwise
   */
  public boolean getSupportsUserAdministration() {
    return supportsUserAdministration;
  }

  /**
   * Returns whether the user directory supports the user locks capability.
   *
   * @return <b>true</b> if the user directory supports the user locks capability or <b> false</b>
   */
  public boolean getSupportsUserLocks() {
    return supportsUserLocks;
  }
}
