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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The <b>Region</b> class holds the information for a region.
 *
 * @author Marcus Portmann
 */
@Schema(
    description =
        "An area, especially part of a country or the world having definable characteristics but not always fixed boundaries")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"country", "code", "localeId", "sortIndex", "name", "description"})
@Entity
@Table(schema = "reference", name = "regions")
@IdClass(RegionId.class)
public class Region implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The ISO 3166-2 subdivision code for the region. */
  @Schema(description = "The ISO 3166-2 subdivision code for the region", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 30)
  @Id
  @Column(name = "code", length = 30, nullable = false)
  private String code;

  /** The ISO 3166-1 alpha-2 code for the country the region is associated with. */
  @Schema(
      description = "The ISO 3166-1 alpha-2 code for the country the region is associated with",
      required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 2, max = 2)
  @Id
  @Column(name = "country", length = 2, nullable = false)
  private String country;

  /** The description for the region. */
  @Schema(description = "The description for the region", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(max = 200)
  @Column(name = "description", length = 200, nullable = false)
  private String description;

  /** The Unicode locale identifier for the region. */
  @Schema(description = "The Unicode locale identifier for the region", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 2, max = 10)
  @Id
  @Column(name = "locale_id", length = 10, nullable = false)
  private String localeId;

  /** The name of the region. */
  @Schema(description = "The name of the region", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 50)
  @Column(name = "name", length = 50, nullable = false)
  private String name;

  /** The sort index for the region. */
  @Schema(description = "The sort index for the region")
  @JsonProperty
  @Column(name = "sort_index")
  private Integer sortIndex;

  /** Constructs a new <b>Region</b>. */
  public Region() {}

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

    Region other = (Region) object;

    return Objects.equals(country, other.country)
        && Objects.equals(code, other.code)
        && Objects.equals(localeId, other.localeId);
  }

  /**
   * Returns the ISO 3166-2 subdivision code for the region.
   *
   * @return the ISO 3166-2 subdivision code for the region
   */
  public String getCode() {
    return code;
  }

  /**
   * Returns the ISO 3166-1 alpha-2 code for the country the region is associated with.
   *
   * @return the ISO 3166-1 alpha-2 code for the country the region is associated with
   */
  public String getCountry() {
    return country;
  }

  /**
   * Returns the description for the region.
   *
   * @return the description for the region
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the Unicode locale identifier for the region.
   *
   * @return the Unicode locale identifier for the region
   */
  public String getLocaleId() {
    return localeId;
  }

  /**
   * Returns the name of the region.
   *
   * @return the name of the region
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the sort index for the region.
   *
   * @return the sort index for the region
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
    return ((country == null) ? 0 : country.hashCode())
        + ((code == null) ? 0 : code.hashCode())
        + ((localeId == null) ? 0 : localeId.hashCode());
  }

  /**
   * Set the ISO 3166-2 subdivision code for the region.
   *
   * @param code the ISO 3166-2 subdivision code for the region
   */
  public void setCode(String code) {
    this.code = code;
  }

  /**
   * Set the ISO 3166-1 alpha-2 code for the country the region is associated with.
   *
   * @param country the ISO 3166-1 alpha-2 code for the country the region is associated with
   */
  public void setCountry(String country) {
    this.country = country;
  }

  /**
   * Set the description for the region.
   *
   * @param description the description for the region
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Set the Unicode locale identifier for the region.
   *
   * @param localeId the Unicode locale identifier for the region
   */
  public void setLocaleId(String localeId) {
    this.localeId = localeId;
  }

  /**
   * Set the name of the region.
   *
   * @param name the name of the region
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Set the sort index for the region.
   *
   * @param sortIndex the sort index for the region
   */
  public void setSortIndex(Integer sortIndex) {
    this.sortIndex = sortIndex;
  }
}
