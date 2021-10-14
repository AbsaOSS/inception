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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * The <b>Car</b> class.
 *
 * <p>This class exposes the JSON and XML properties using a property-based approach rather than a
 * field-based approach to support the JPA inheritance model.
 *
 * @author Marcus Portmann
 */
@Schema(description = "A car")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "name", "numberOfDoors"})
@Entity
@Table(schema = "demo", name = "cars")
public class Car extends VehicleBase implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The number of doors for the car. */
  @NotNull
  @Column(table = "cars", name = "number_of_doors", nullable = false)
  private int numberOfDoors;

  /** Constructs a new <b>Car</b>. */
  public Car() {
    super(VehicleType.CAR);
  }

  /**
   * Constructs a new <b>Car</b>.
   *
   * @param name the name of the car
   * @param numberOfDoors the number of doors
   */
  public Car(String name, int numberOfDoors) {
    super(VehicleType.CAR, name);

    this.numberOfDoors = numberOfDoors;
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

    Car other = (Car) object;

    return Objects.equals(getId(), other.getId());
  }

  /**
   * Returns the date and time the car was created.
   *
   * @return the date and time the car was created
   */
  @JsonIgnore
  @Override
  public LocalDateTime getCreated() {
    return super.getCreated();
  }

  /**
   * Returns the ID for the car.
   *
   * @return the ID for the car
   */
  @Schema(description = "The ID for the car", required = true)
  @JsonProperty(required = true)
  @Override
  public UUID getId() {
    return super.getId();
  }

  /**
   * Returns the name of the car.
   *
   * @return the name of the car
   */
  @Schema(description = "The name of the car", required = true)
  @JsonProperty(required = true)
  @Override
  public String getName() {
    return super.getName();
  }

  /**
   * Returns the number of doors for the car.
   *
   * @return the number of doors for the car
   */
  @Schema(description = "The number of doors for the car", required = true)
  @JsonProperty(required = true)
  public int getNumberOfDoors() {
    return numberOfDoors;
  }

  /**
   * Returns the vehicle type for the car.
   *
   * @return the vehicle type for the car
   */
  @JsonIgnore
  @Override
  public VehicleType getType() {
    return super.getType();
  }

  /**
   * Returns the date and time the car was last updated.
   *
   * @return the date and time the car was last updated
   */
  @JsonIgnore
  @Override
  public LocalDateTime getUpdated() {
    return super.getUpdated();
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for the object
   */
  @Override
  public int hashCode() {
    return ((super.getId() == null) ? 0 : super.getId().hashCode());
  }

  /**
   * Set the ID for the car.
   *
   * @param id the ID for the car
   */
  @Override
  public void setId(UUID id) {
    super.setId(id);
  }

  /**
   * Set the name of the car.
   *
   * @param name the name of the car
   */
  @Override
  public void setName(String name) {
    super.setName(name);
  }

  /**
   * Set the number of doors for the car.
   *
   * @param numberOfDoors the number of doors for the car
   */
  public void setNumberOfDoors(int numberOfDoors) {
    this.numberOfDoors = numberOfDoors;
  }
}
