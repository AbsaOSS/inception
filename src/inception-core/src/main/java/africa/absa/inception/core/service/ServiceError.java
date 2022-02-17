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

import africa.absa.inception.core.xml.LocalDateTimeAdapter;
import java.time.LocalDateTime;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * The <b>ServiceError</b> class holds the service error information.
 *
 * @author Marcus Portmann
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ServiceError", namespace = "http://inception.absa.africa/core")
@XmlType(
    name = "ServiceError",
    namespace = "http://inception.absa.africa/core",
    propOrder = {"timestamp", "message"})
@SuppressWarnings({"unused", "WeakerAccess"})
public class ServiceError {

  /** The message for the service error. */
  @XmlElement(name = "Message", required = true)
  private String message;

  /** The date and time the service error occurred. */
  @XmlElement(name = "Timestamp", required = true)
  @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
  @XmlSchemaType(name = "dateTime")
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
