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

/**
 * The ValidationErrorAttribute class holds the information for an attribute associated with the
 * validation error.
 *
 * @author Marcus Portmann
 */
export class ValidationErrorAttribute {

  /**
   * The name for the validation error attribute.
   */
  name: string;

  /**
   * The value for the validation error attribute.
   */
  value: string;

  /**
   * Constructs a new ValidationErrorAttribute.
   *
   * @param name  The name for the validation error attribute.
   * @param value The value for the validation error attribute.
   */
  constructor(name: string, value: string) {
    this.name = name;
    this.value = value;
  }
}

/**
 * The ValidationError class holds the information for a validation error.
 *
 * @author Marcus Portmann
 */
export class ValidationError {

  /**
   * The attributes associated with the validation error.
   */
  attributes: ValidationErrorAttribute[];
  /**
   * The error message for the validation error.
   */
  message: string;
  /**
   * The path for the property that resulted in the validation error.
   */
  property: string;

  /**
   * Constructs a new ValidationError.
   *
   * @param property   The path for the property that resulted in the validation error.
   * @param message    The error message for the validation error.
   * @param attributes The attributes associated with the validation error.
   */
  constructor(property: string, message: string, attributes?: ValidationErrorAttribute[]) {
    this.property = property;
    this.message = message;

    if (attributes == null) {
      this.attributes = [];
    } else {
      this.attributes = attributes;
    }
  }
}
