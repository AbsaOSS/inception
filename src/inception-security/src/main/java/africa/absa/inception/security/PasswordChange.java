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
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The <b>PasswordChange</b> class holds the information for a password change.
 *
 * @author Marcus Portmann
 */
@Schema(description = "A password change")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "expirePassword",
  "lockUser",
  "newPassword",
  "password",
  "reason",
  "resetPasswordHistory",
  "securityCode"
})
@SuppressWarnings({"unused"})
public class PasswordChange implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** Expire the user's password when performing an administrative password change. */
  @Schema(
      description = "Expire the user's password when performing an administrative password change")
  @JsonProperty
  private Boolean expirePassword;

  /** Lock the user when performing an administrative password change. */
  @Schema(description = "Lock the user when performing an administrative password change")
  @JsonProperty
  private Boolean lockUser;

  /** The new password. */
  @Schema(description = "The new password", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  private String newPassword;

  /**
   * The password for the user that is used to authorise the operation when performing a user
   * password change.
   */
  @Schema(
      description =
          "The password for the user that is used to authorise the operation when performing a user password change")
  @JsonProperty
  @Size(min = 1, max = 100)
  private String password;

  /** The reason for changing the password. */
  @Schema(description = "The reason for changing the password", required = true)
  @JsonProperty(required = true)
  @NotNull
  private PasswordChangeReason reason;

  /** Reset the user's password history when performing an administrative password change. */
  @Schema(
      description =
          "Reset the user's password history when performing an administrative password change")
  @JsonProperty
  private Boolean resetPasswordHistory;

  /** The security code when performing a forgotten password change. */
  @Schema(description = "The security code when performing a forgotten password change.")
  @JsonProperty
  private String securityCode;

  /** Constructs a new <b>PasswordChange</b>. */
  public PasswordChange() {}

  /**
   * Constructs a new <b>PasswordChange</b> for a USER password change.
   *
   * @param newPassword the new password
   * @param password the password for the user that is used to authorise the operation when
   *     performing a user password change
   */
  public PasswordChange(String newPassword, String password) {
    this.newPassword = newPassword;
    this.password = password;
    this.reason = PasswordChangeReason.USER;
  }

  /**
   * Constructs a new <b>PasswordChange</b> for an ADMINISTRATIVE password change.
   *
   * @param newPassword the new password
   * @param expirePassword expire the user's password when performing the administrative password
   *     change
   * @param lockUser lock the user when performing the administrative password change
   * @param resetPasswordHistory reset the user's password history when performing the
   *     administrative password change
   */
  public PasswordChange(
      String newPassword, Boolean expirePassword, Boolean lockUser, Boolean resetPasswordHistory) {
    this.newPassword = newPassword;
    this.expirePassword = expirePassword;
    this.lockUser = lockUser;
    this.resetPasswordHistory = resetPasswordHistory;
    this.reason = PasswordChangeReason.ADMINISTRATIVE;
  }

  /**
   * Returns whether the user's password should be expired when performing an administrative
   * password change.
   *
   * @return <b>true</b> if the user's password should be expired when performing an administrative
   *     password changeor <b>false</b> otherwise
   */
  public Boolean getExpirePassword() {
    return expirePassword;
  }

  /**
   * Returns whether the user should be locked when performing an administrative password change.
   *
   * @return <b>true</b> if the user should be locked when performing an administrative password
   *     change or <b>false</b> otherwise
   */
  public Boolean getLockUser() {
    return lockUser;
  }

  /**
   * Returns the new password.
   *
   * @return the new password
   */
  public String getNewPassword() {
    return newPassword;
  }

  /**
   * Returns the password for the user that is used to authorise the operation when performing a
   * user password change.
   *
   * @return the password for the user that is used to authorise the operation when performing a
   *     user password change
   */
  public String getPassword() {
    return password;
  }

  /**
   * Returns the reason for changing the password.
   *
   * @return the reason for changing the password
   */
  public PasswordChangeReason getReason() {
    return reason;
  }

  /**
   * Returns whether the user's password history should be reset when performing an administrative
   * password change.
   *
   * @return <b>true</b> if the user's password history should be reset when performing an
   *     administrative password changeor <b>false</b> otherwise
   */
  public Boolean getResetPasswordHistory() {
    return resetPasswordHistory;
  }

  /**
   * Returns the security code when performing a forgotten password change.
   *
   * @return the security code when performing a forgotten password change
   */
  public String getSecurityCode() {
    return securityCode;
  }

  /**
   * Set whether the user's password should be expired when performing an administrative password
   * change.
   *
   * @param expirePassword expire the user's password when performing an administrative password
   *     change
   */
  public void setExpirePassword(Boolean expirePassword) {
    this.expirePassword = expirePassword;
  }

  /**
   * Set whether the user should be locked when performing an administrative password change.
   *
   * @param lockUser lock the user when performing an administrative password change
   */
  public void setLockUser(Boolean lockUser) {
    this.lockUser = lockUser;
  }

  /**
   * Set the new password when performing a user password change.
   *
   * @param newPassword the new password when performing a new password change
   */
  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  /**
   * Set the password for the user that is used to authorise the operation when performing a user
   * password change.
   *
   * @param password the password for the user that is used to authorise the operation when
   *     performing a user password change
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Set the reason for changing the password.
   *
   * @param reason the reason for changing the password
   */
  public void setReason(PasswordChangeReason reason) {
    this.reason = reason;
  }

  /**
   * Set whether the user's password history should be reset when performing an administrative
   * password change.
   *
   * @param resetPasswordHistory reset the user's password history when performing an administrative
   *     password change
   */
  public void setResetPasswordHistory(Boolean resetPasswordHistory) {
    this.resetPasswordHistory = resetPasswordHistory;
  }

  /**
   * Set the security code when performing a forgotten password change.
   *
   * @param securityCode the security code when performing a forgotten password change
   */
  public void setSecurityCode(String securityCode) {
    this.securityCode = securityCode;
  }
}
