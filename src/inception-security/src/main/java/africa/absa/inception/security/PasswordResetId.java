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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * The <b>PasswordResetId</b> class implements the ID class for the <b>PasswordReset</b> class.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings({"UnusedDeclaration"})
public class PasswordResetId implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The date and time the password reset was requested. */
  private LocalDateTime requested;

  /** The username for the user associated with the password reset. */
  private String username;

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

    PasswordResetId other = (PasswordResetId) object;

    return Objects.equals(username, other.username) && Objects.equals(requested, other.requested);
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
   * Set the date and time the password reset was requested.
   *
   * @param requested the date and time the password reset was requested
   */
  public void setRequested(LocalDateTime requested) {
    this.requested = requested;
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
