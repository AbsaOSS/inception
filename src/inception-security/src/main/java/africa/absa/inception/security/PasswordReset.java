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
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The <b>PasswordReset</b> class holds the information for a password rest.
 *
 * @author Marcus Portmann
 */
@Schema(description = "A password reset")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"username", "requested", "completed", "expired", "status", "securityCodeHash"})
@SuppressWarnings({"unused"})
@Entity
@Table(schema = "security", name = "password_resets")
@IdClass(PasswordResetId.class)
public class PasswordReset implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The date and time the password reset was completed. */
  @Schema(description = "The date and time the password reset was completed")
  @JsonProperty
  @Column(name = "completed")
  private LocalDateTime completed;

  /** The date and time the password reset expired. */
  @Schema(description = "The date and time the password reset expired")
  @JsonProperty
  @Column(name = "expired")
  private LocalDateTime expired;

  /** The date and time the password reset was requested. */
  @Schema(description = "The date and time the password reset was requested")
  @JsonProperty
  @Id
  @Column(name = "requested", nullable = false)
  private LocalDateTime requested;

  /** The security code hash. */
  @Schema(description = "The security code hash", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  @Column(name = "security_code_hash", length = 100, nullable = false)
  private String securityCodeHash;

  /** The status of the password reset. */
  @Schema(description = "The status of the password reset", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Column(name = "status", nullable = false)
  private PasswordResetStatus status;

  /** The username for the user associated with the password reset. */
  @Schema(
      description = "The username for the user associated with the password reset",
      required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  @Id
  @Column(name = "username", length = 100, nullable = false)
  private String username;

  /** Constructs a new <b>PasswordReset</b>. */
  public PasswordReset() {}

  /**
   * Constructs a new <b>PasswordReset</b>.
   *
   * @param username the username for the user associated with the password reset
   * @param securityCodeHash the security code hash
   */
  public PasswordReset(String username, String securityCodeHash) {
    this.username = username;
    this.securityCodeHash = securityCodeHash;
    this.requested = LocalDateTime.now();
    this.status = PasswordResetStatus.REQUESTED;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param object the reference object with which to compare
   * @return <b>true</b> if this object is the same as the object argument otherwise <b> false</b>
   */
  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }

    if (object == null) {
      return false;
    }

    if (getClass() != object.getClass()) {
      return false;
    }

    PasswordReset other = (PasswordReset) object;

    return Objects.equals(username, other.username) && Objects.equals(requested, other.requested);
  }

  /**
   * Returns the date and time the password reset was completed.
   *
   * @return the date and time the password reset was completed
   */
  public LocalDateTime getCompleted() {
    return completed;
  }

  /**
   * Returns the date and time the password reset expired.
   *
   * @return the date and time the password reset expired
   */
  public LocalDateTime getExpired() {
    return expired;
  }

  /**
   * Returns the date and time the password reset was requested.
   *
   * @return the date and time the password reset was requested
   */
  public LocalDateTime getRequested() {
    return requested;
  }

  /**
   * Returns the security code hash.
   *
   * @return the security code hash
   */
  public String getSecurityCodeHash() {
    return securityCodeHash;
  }

  /**
   * Returns the status of the password reset.
   *
   * @return the status of the password reset
   */
  public PasswordResetStatus getStatus() {
    return status;
  }

  /**
   * Returns the username for the user associated with the password reset.
   *
   * @return the username for the user associated with the password reset
   */
  public String getUsername() {
    return username;
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for the object
   */
  @Override
  public int hashCode() {
    return ((username == null) ? 0 : username.hashCode())
        + ((requested == null) ? 0 : requested.hashCode());
  }

  /**
   * Set the date and time the password reset was completed.
   *
   * @param completed the date and time the password reset was completed
   */
  public void setCompleted(LocalDateTime completed) {
    this.completed = completed;
  }

  /**
   * Set the date and time the password reset expired.
   *
   * @param expired the date and time the password reset expired
   */
  public void setExpired(LocalDateTime expired) {
    this.expired = expired;
  }

  /**
   * Set the date and time the password reset was requested.
   *
   * @param requested the date and time the password reset was requested
   */
  public void setRequested(LocalDateTime requested) {
    this.requested = requested;
  }

  /**
   * Set the security code hash.
   *
   * @param securityCodeHash the security code hash
   */
  public void setSecurityCodeHash(String securityCodeHash) {
    this.securityCodeHash = securityCodeHash;
  }

  /**
   * Set the status of the password reset.
   *
   * @param status the status of the password reset
   */
  public void setStatus(PasswordResetStatus status) {
    this.status = status;
  }

  /**
   * Set the username for the user associated with the password reset.
   *
   * @param username the username for the user associated with the password reset
   */
  public void setUsername(String username) {
    this.username = username;
  }
}
