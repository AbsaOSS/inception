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

package africa.absa.inception.reference;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The <b>TimeZone</b> class holds the information for a time zone.
 *
 * <p>A time zone is a region of the globe that observes a uniform standard time for legal,
 * commercial, and social purposes. Time zones tend to follow the boundaries of countries and their
 * subdivisions instead of strictly following longitude, because it is convenient for areas in close
 * commercial or other communication to keep the same time.
 *
 * @author Marcus Portmann
 */
@Schema(description = "A time zone")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "localeId", "sortIndex", "name", "description"})
public class TimeZone implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The description for the time zone. */
  @Schema(description = "The description for the time zone", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(max = 200)
  private String description;

  /** The ID for the time zone. */
  @Schema(description = "The ID for the time zone", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 50)
  private String id;

  /** The Unicode locale identifier for the time zone. */
  @Schema(description = "The Unicode locale identifier for the time zone", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 2, max = 10)
  private String localeId;

  /** The name of the time zone. */
  @Schema(description = "The name of the time zone", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  private String name;

  /** The sort index for the time zone. */
  @Schema(description = "The sort index for the time zone", required = true)
  @JsonProperty(required = true)
  @NotNull
  private Integer sortIndex;

  /** Constructs a new <b>TimeZone</b>. */
  public TimeZone() {}

  /**
   * Constructs a new <b>TimeZone</b>.
   *
   * @param id the ID for the time zone
   * @param localeId the Unicode locale identifier for the time zone
   * @param name the name of the time zone
   * @param description the description for the time zone
   * @param sortIndex the sort index for the time zone
   */
  public TimeZone(String id, String localeId, String name, String description, Integer sortIndex) {
    this.id = id;
    this.localeId = localeId;
    this.name = name;
    this.description = description;
    this.sortIndex = sortIndex;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param object the reference object with which to compare
   * @return <b>true</b> if this object is the same as the object argument otherwise <b>false</b>
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

    TimeZone other = (TimeZone) object;

    return Objects.equals(id, other.id) && Objects.equals(localeId, other.localeId);
  }

  /**
   * Returns the description for the time zone.
   *
   * @return the description for the time zone
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the ID for the time zone.
   *
   * @return the ID for the time zone
   */
  public String getId() {
    return id;
  }

  /**
   * Returns the Unicode locale identifier for the time zone.
   *
   * @return the Unicode locale identifier for the time zone
   */
  public String getLocaleId() {
    return localeId;
  }

  /**
   * Returns the name of the time zone.
   *
   * @return the name of the time zone
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the sort index for the time zone.
   *
   * @return the sort index for the time zone
   */
  public Integer getSortIndex() {
    return sortIndex;
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for the object
   */
  @Override
  public int hashCode() {
    return ((id == null) ? 0 : id.hashCode()) + ((localeId == null) ? 0 : localeId.hashCode());
  }

  /**
   * Set the description for the time zone.
   *
   * @param description the description for the time zone
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Set the ID for the time zone.
   *
   * @param id the ID for the time zone
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Set the Unicode locale identifier for the time zone.
   *
   * @param localeId the Unicode locale identifier for the time zone
   */
  public void setLocaleId(String localeId) {
    this.localeId = localeId;
  }

  /**
   * Set the name of the time zone.
   *
   * @param name the name of the time zone
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Set the sort index for the time zone.
   *
   * @param sortIndex the sort index for the time zone
   */
  public void setSortIndex(Integer sortIndex) {
    this.sortIndex = sortIndex;
  }
}
