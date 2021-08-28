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

package africa.absa.inception.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The <b>ErrorReportSortBy</b> enumeration defines the possible methods used to sort a list of
 * error reports.
 *
 * @author Marcus Portmann
 */
@Schema(description = "The method used to sort the list of error reports")
public enum ErrorReportSortBy {
  /** Sort by created. */
  CREATED("created", "Sort By Created"),

  /** Sort by who. */
  WHO("who", "Sort By Who");

  private final String code;

  private final String description;

  ErrorReportSortBy(String code, String description) {
    this.code = code;
    this.description = description;
  }

  /**
   * Returns the method used to sort a list of error reports given by the specified code value.
   *
   * @param code the code for the method used to sort a list of error reports
   * @return the method used to sort a list of error reports given by the specified code value
   */
  @JsonCreator
  public static ErrorReportSortBy fromCode(String code) {
    switch (code) {
      case "created":
        return ErrorReportSortBy.CREATED;

      case "who":
        return ErrorReportSortBy.WHO;

      default:
        throw new RuntimeException(
            "Failed to determine the error report sort by with the invalid code (" + code + ")");
    }
  }

  /**
   * Returns the code for the method used to sort a list of error reports.
   *
   * @return the code for the method used to sort a list of error reports
   */
  @JsonValue
  public String code() {
    return code;
  }

  /**
   * Returns the description for the method used to sort a list of error reports.
   *
   * @return the description for the method used to sort a list of error reports
   */
  public String description() {
    return description;
  }

  /**
   * Return the string representation of the method used to sort a list of error reports enumeration
   * value.
   *
   * @return the string representation of the method used to sort a list of error reports
   *     enumeration value
   */
  public String toString() {
    return description;
  }
}
