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
import com.github.f4b6a3.uuid.UuidCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * The <b>Vehicle</b> class.
 *
 * <p>The <b>Vehicle</b> and <b>VehicleBase</b> classes are both JPA entity classes mapped to the
 * same <b>demo.vehicles</b> table. The <b>VehicleBase</b> class provides the common base class for
 * all JPA entity classes that form part of the vehicle inheritance model, e.g. <b>Car</b>,
 * <b>Motorbike</b>, etc. This inheritance model is required to allow the same child classes to be
 * mapped to the different parent classes for the different vehicle types, e.g. to support the
 * one-to-many mappings for both the <b>Car</b> and <b>Motorbike</b> classes to the
 * <b>VehicleAttribute</b> class. The <b>Vehicle</b> class provides a mechanism to retrieve the
 * minimum amount of vehicle information without executing the polymorphic query that would result
 * from retrieving the same entities using a query that specifies the <b>VehicleBase</b> class as
 * the result type.
 *
 * @author Marcus Portmann
 */
@Schema(description = "A vehicle")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "name", "type"})
@Entity
@Table(schema = "demo", name = "vehicles")
public class Vehicle implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The date and time the vehicle was created. */
  @JsonIgnore
  @CreationTimestamp
  @Column(table = "vehicles", name = "created", nullable = false, updatable = false)
  private LocalDateTime created;

  /** The Universally Unique Identifier (UUID) for the vehicle. */
  @Schema(description = "The Universally Unique Identifier (UUID) for the vehicle", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Id
  @Column(table = "vehicles", name = "id", nullable = false)
  private UUID id;

  /** The name of the vehicle. */
  @Schema(description = "The name of the vehicle", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  @Column(table = "vehicles", name = "name", length = 100, nullable = false)
  private String name;

  /** The vehicle type. */
  @Schema(description = "The vehicle type", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Column(table = "vehicles", name = "type", length = 30, nullable = false)
  private VehicleType type;

  /** The date and time the vehicle was last updated. */
  @JsonIgnore
  @UpdateTimestamp
  @Column(table = "vehicles", name = "updated", insertable = false)
  private LocalDateTime updated;

  /** Constructs a new <b>Vehicle</b>. */
  public Vehicle() {}

  /**
   * Constructs a new <b>Vehicle</b>.
   *
   * @param type the vehicle type
   * @param name the name of the vehicle
   */
  public Vehicle(VehicleType type, String name) {
    this.id = UuidCreator.getShortPrefixComb();
    this.type = type;
    this.name = name;
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

    Vehicle other = (Vehicle) object;

    return Objects.equals(id, other.id);
  }

  /**
   * Returns the date and time the vehicle was created.
   *
   * @return the date and time the vehicle was created
   */
  public LocalDateTime getCreated() {
    return created;
  }

  /**
   * Returns the Universally Unique Identifier (UUID) for the vehicle.
   *
   * @return the Universally Unique Identifier (UUID) for the vehicle
   */
  public UUID getId() {
    return id;
  }

  /**
   * Returns the name of the vehicle.
   *
   * @return the name of the vehicle
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the vehicle type.
   *
   * @return the vehicle type
   */
  public VehicleType getType() {
    return type;
  }

  /**
   * Returns the date and time the vehicle was last updated.
   *
   * @return the date and time the vehicle was last updated
   */
  public LocalDateTime getUpdated() {
    return updated;
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for the object
   */
  @Override
  public int hashCode() {
    return ((id == null) ? 0 : id.hashCode());
  }

  /**
   * Set the Universally Unique Identifier (UUID) for the vehicle.
   *
   * @param id the Universally Unique Identifier (UUID) for the vehicle
   */
  public void setId(UUID id) {
    this.id = id;
  }

  /**
   * Set the name of the vehicle.
   *
   * @param name the name of the vehicle
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Set the vehicle type.
   *
   * @param type the vehicle type
   */
  public void setType(VehicleType type) {
    this.type = type;
  }
}
