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

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The <b>InvalidArgumentError</b> class holds the invalid argument error information.
 *
 * @author Marcus Portmann
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "InvalidArgumentError", namespace = "http://inception.absa.africa/core")
@XmlType(
    name = "InvalidArgumentError",
    namespace = "http://inception.absa.africa/core",
    propOrder = {"parameter", "validationErrors"})
@SuppressWarnings({"unused"})
public class InvalidArgumentError extends ServiceError implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The name of the parameter associated with the invalid argument error. */
  @XmlElement(name = "Parameter", required = true)
  private String parameter;

  /** The optional validation errors associated with the invalid argument error. */
  @XmlElementWrapper(name = "ValidationErrors", required = true)
  @XmlElement(name = "ValidationError")
  private List<ValidationError> validationErrors;

  /** Constructs a new <b>InvalidArgumentError</b>. */
  public InvalidArgumentError() {}

  /**
   * Constructs a new <b>InvalidArgumentError</b>.
   *
   * @param message the message for the invalid argument error
   * @param parameter the name of the parameter associated with the invalid argument error
   * @param validationErrors the validation errors associated with the invalid argument
   */
  public InvalidArgumentError(
      String message, String parameter, List<ValidationError> validationErrors) {
    super(message);
    this.parameter = parameter;
    this.validationErrors = validationErrors;
  }

  /**
   * Returns the name of the parameter associated with the invalid argument error.
   *
   * @return the name of the parameter associated with the invalid argument error
   */
  public String getParameter() {
    return parameter;
  }

  /**
   * Returns the optional validation errors associated with the invalid argument error.
   *
   * @return the optional validation errors associated with the invalid argument error
   */
  public List<ValidationError> getValidationErrors() {
    return validationErrors;
  }
}
