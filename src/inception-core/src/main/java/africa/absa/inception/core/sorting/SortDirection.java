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

package africa.absa.inception.core.sorting;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The <b>SortDirection</b> enumeration defines the possible sort directions when retrieving
 * security related entities.
 *
 * @author Marcus Portmann
 */
@Schema(description = "The sort direction")
public enum SortDirection {
  /** Sort ascending. */
  ASCENDING("asc", "Ascending"),

  /** Sort descending. */
  DESCENDING("desc", "Descending");

  private final String code;

  private final String description;

  SortDirection(String code, String description) {
    this.code = code;
    this.description = description;
  }

  /**
   * Returns the sort direction given by the specified code value.
   *
   * @param code the code for the sort direction
   * @return the sort direction given by the specified code value
   */
  @JsonCreator
  public static SortDirection fromCode(String code) {
    switch (code) {
      case "asc":
        return SortDirection.ASCENDING;

      case "desc":
        return SortDirection.DESCENDING;

      default:
        throw new RuntimeException(
            "Failed to determine the sort direction with the invalid code (" + code + ")");
    }
  }

  /**
   * Returns the code for the sort direction.
   *
   * @return the code for the sort direction
   */
  @JsonValue
  public String code() {
    return code;
  }

  /**
   * Returns the description for the sort direction.
   *
   * @return the description for the sort direction
   */
  public String description() {
    return description;
  }

  /**
   * Return the string representation of the sort direction enumeration value.
   *
   * @return the string representation of the sort direction enumeration value
   */
  public String toString() {
    return description;
  }
}
