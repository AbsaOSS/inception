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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotNull;
import org.springframework.util.StringUtils;

/**
 * The <b>ValidationError</b> class represents a validation error.
 *
 * @author Marcus Portmann
 */
@Schema(description = "A validation error")
@JsonPropertyOrder({"property", "message"})
@SuppressWarnings({"unused", "WeakerAccess"})
public class ValidationError implements Serializable, Cloneable {

  private static final long serialVersionUID = 1000000;

  /** The error message for the validation error. */
  @Schema(description = "The error message for the validation error", required = true)
  @JsonProperty(required = true)
  @NotNull
  private String message;

  /** The path for the property that resulted in the validation error. */
  @Schema(
      description = "The path for the property that resulted in the validation error",
      required = true)
  @JsonProperty(required = true)
  @NotNull
  private String property;

  /** Constructs a new <b>ValidationError</b>. */
  public ValidationError() {}

  /**
   * Constructs a new <b>ValidationError</b>.
   *
   * @param constraintViolation the constraint violation
   */
  public ValidationError(ConstraintViolation<?> constraintViolation) {
    this.property = constraintViolation.getPropertyPath().toString();
    this.message = constraintViolation.getMessage();

    Map<String, Object> attributes = constraintViolation.getConstraintDescriptor().getAttributes();
  }

  /**
   * Constructs a new <b>ValidationError</b>.
   *
   * @param property the path for the property that resulted in the validation error
   * @param message the error message for the validation error
   */
  public ValidationError(String property, String message) {
    this.property = property;
    this.message = message;
  }

  /**
   * Capitalize each word in the string.
   *
   * @param str the string
   * @return the capitalized string
   */
  public static String capitalizePropertyName(String str) {
    if (!StringUtils.hasText(str)) {
      return str;
    }

    StringReader reader = new StringReader(str);

    try {
      StringBuilder buffer = new StringBuilder();

      boolean capitilizeNextCharacter = true;

      int c;
      while ((c = reader.read()) != -1) {
        char character = (char) c;

        if (capitilizeNextCharacter) {
          buffer.append(Character.toUpperCase(character));
        } else {
          buffer.append(character);
        }

        capitilizeNextCharacter = (character == '.') || (character == ' ');
      }

      return buffer.toString();
    } catch (Throwable e) {
      throw new RuntimeException("Failed to capitalizePropertyName the string (" + str + ")", e);
    }
  }

  /**
   * Capitalize the property names for the validation errors.
   *
   * @param validationErrors the validation errors
   */
  public static void capitalizePropertyNames(List<ValidationError> validationErrors) {
    if (validationErrors == null) {
      return;
    }

    for (ValidationError validationError : validationErrors) {
      validationError.setProperty(capitalizePropertyName(validationError.getProperty()));
    }
  }

  /**
   * Helper method to convert a set of JSR 303 constraint violations to a set of validation errors.
   *
   * @param <T> the type the constraint violations are associated with
   * @param constraintViolations the JSR 303 constraint violations to convert
   * @return the validation errors
   */
  public static <T> List<ValidationError> toValidationErrors(
      Set<ConstraintViolation<T>> constraintViolations) {
    return toValidationErrors(constraintViolations, false);
  }

  /**
   * Helper method to convert a set of JSR 303 constraint violations to a set of validation errors.
   *
   * @param <T> the type the constraint violations are associated with
   * @param constraintViolations the JSR 303 constraint violations to convert
   * @param capitalizePropertyNames should the property names be capitilized
   * @return the validation errors
   */
  public static <T> List<ValidationError> toValidationErrors(
      Set<ConstraintViolation<T>> constraintViolations, boolean capitalizePropertyNames) {
    List<ValidationError> validationErrors = new ArrayList<>();

    for (ConstraintViolation<T> constraintViolation : constraintViolations) {
      ValidationError validationError = new ValidationError(constraintViolation);

      if (capitalizePropertyNames) {
        validationError.setProperty(capitalizePropertyName(validationError.getProperty()));
      }

      validationErrors.add(validationError);
    }

    return validationErrors;
  }

  /**
   * Returns the error message for the validation error.
   *
   * @return the error message for the validation error
   */
  public String getMessage() {
    return message;
  }

  /**
   * Returns the path for the property that resulted in the validation error.
   *
   * @return the path for the property that resulted in the validation error
   */
  public String getProperty() {
    return property;
  }

  /**
   * Set the error message for the validation error.
   *
   * @param message the error message for the validation error
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Set the path for the property that resulted in the validation error.
   *
   * @param property the path for the property that resulted in the validation error
   */
  public void setProperty(String property) {
    this.property = property;
  }
}
