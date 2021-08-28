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

package africa.absa.inception.core.service;

import java.time.LocalDateTime;

/**
 * The <b>ServiceError</b> class holds the service error information.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ServiceError {

  /** The message for the service error. */
  private String message;

  /** The date and time the service error occurred. */
  private LocalDateTime timestamp;

  /** Constructs a new <b>ServiceError</b>. */
  public ServiceError() {}

  /**
   * Constructs a new <b>ServiceError</b>.
   *
   * @param message the message for the service error
   */
  public ServiceError(String message) {
    this.timestamp = LocalDateTime.now();
    this.message = message;
  }

  /**
   * Returns the message for the service error.
   *
   * @return the message for the service error
   */
  public String getMessage() {
    return message;
  }

  /**
   * Returns the date and time the service error occurred.
   *
   * @return the date and time the service error occurred
   */
  public LocalDateTime getTimestamp() {
    return timestamp;
  }
}
