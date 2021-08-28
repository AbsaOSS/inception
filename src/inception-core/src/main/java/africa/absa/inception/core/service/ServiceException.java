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
 * The <b>ServiceException</b> exception is the base class that all service exceptions should be
 * derived from.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings("unused")
public abstract class ServiceException extends Exception {

  private static final long serialVersionUID = 1000000;

  /** The service error information. */
  private final ServiceError serviceError;

  /**
   * Constructs a new <b>ServiceException</b> with the specified message.
   *
   * @param message The message saved for later retrieval by the <b>getMessage()</b> method.
   */
  public ServiceException(String message) {
    this(message, null);
  }

  /**
   * Constructs a new <b>ServiceException</b> with the specified message and cause.
   *
   * @param message The message saved for later retrieval by the <b>getMessage()</b> method.
   * @param cause The cause saved for later retrieval by the <b>getCause()</b> method. (A
   *     <b>null</b> value is permitted if the cause is nonexistent or unknown)
   */
  public ServiceException(String message, Throwable cause) {
    super(message, cause);

    this.serviceError = new ServiceError(message);
  }

  /**
   * Returns the fault info.
   *
   * @return the fault info
   */
  public ServiceError getFaultInfo() {
    return serviceError;
  }

  /**
   * Returns the date and time the service exception occurred.
   *
   * @return the date and time the service exception occurred
   */
  public LocalDateTime getTimestamp() {
    return serviceError.getTimestamp();
  }
}
