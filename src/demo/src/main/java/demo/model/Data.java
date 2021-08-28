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

package demo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The <b>Data</b> class.
 *
 * @author Marcus Portmann
 */
@Schema(description = "Data")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "stringValue", "integerValue", "dateValue", "timestampValue"})
@Entity
@Table(schema = "demo", name = "data")
@SuppressWarnings({"unused", "WeakerAccess"})
public class Data implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The timestamp value for the data. */
  @Schema(description = "The timestamp value for the data", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Column(name = "timestamp_value")
  public LocalDateTime timestampValue;

  /** The date value for the data. */
  @Schema(description = "The date value for the data", required = true)
  @JsonProperty(required = true)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  @NotNull
  @Column(name = "date_value")
  private LocalDate dateValue;

  /** The ID for the data. */
  @Schema(description = "The ID for the data", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Id
  @Column(name = "id", nullable = false)
  private long id;

  /** The integer value for the data. */
  @Schema(description = "The integer value for the data", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Column(name = "integer_value")
  private Integer integerValue;

  /** The string string value for the data. */
  @Schema(description = "The string string value for the data", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 4000)
  @Column(name = "string_value")
  private String stringValue;

  /** Constructs a new <b>Data</b>. */
  public Data() {}

  /**
   * Constructs a new <b>Data</b>.
   *
   * @param id the ID for the data
   * @param integerValue the integer value
   * @param stringValue the string value for the data
   * @param dateValue the date value for the data
   * @param timestampValue the timestamp value for the data
   */
  public Data(
      long id,
      Integer integerValue,
      String stringValue,
      LocalDate dateValue,
      LocalDateTime timestampValue) {
    this.id = id;
    this.integerValue = integerValue;
    this.stringValue = stringValue;
    this.dateValue = dateValue;
    this.timestampValue = timestampValue;
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

    Data other = (Data) object;

    return Objects.equals(id, other.id);
  }

  /**
   * Returns the date value for the data.
   *
   * @return the date value for the data
   */
  public LocalDate getDateValue() {
    return dateValue;
  }

  /**
   * Returns the ID for the data.
   *
   * @return the ID for the data
   */
  public long getId() {
    return id;
  }

  /**
   * Returns the integer value for the data.
   *
   * @return the integer value for the data
   */
  public Integer getIntegerValue() {
    return integerValue;
  }

  /**
   * Returns the string value for the data.
   *
   * @return the string value for the data
   */
  public String getStringValue() {
    return stringValue;
  }

  /**
   * Returns the timestamp value for the data.
   *
   * @return the timestamp value for the data
   */
  public LocalDateTime getTimestampValue() {
    return timestampValue;
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for the object
   */
  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  /**
   * Set the date value for the data.
   *
   * @param dateValue the date value for the data
   */
  public void setDateValue(LocalDate dateValue) {
    this.dateValue = dateValue;
  }

  /**
   * Set the ID for the data.
   *
   * @param id the ID for the data
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * Set the integer value for the data.
   *
   * @param integerValue the integer value for the data
   */
  public void setIntegerValue(Integer integerValue) {
    this.integerValue = integerValue;
  }

  /**
   * Set the string value for the data.
   *
   * @param stringValue the string value for the data
   */
  public void setStringValue(String stringValue) {
    this.stringValue = stringValue;
  }

  /**
   * Set the timestamp value for the data.
   *
   * @param timestampValue the timestamp value for the data
   */
  public void setTimestampValue(LocalDateTime timestampValue) {
    this.timestampValue = timestampValue;
  }

  /**
   * Returns a string representation of the data.
   *
   * @return a string representation of the data
   */
  @Override
  public String toString() {
    return "Data {id=\""
        + id
        + "\", integerValue=\""
        + integerValue
        + "\", stringValue=\""
        + stringValue
        + "\", dateValue=\""
        + dateValue
        + "\", timestampValue=\""
        + timestampValue
        + "\"}";
  }
}
