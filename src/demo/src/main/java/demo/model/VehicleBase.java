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

import com.github.f4b6a3.uuid.UuidCreator;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The <b>VehicleBase</b> class.
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
 * <p>This class and its subclasses expose the JSON and XML properties using a property-based
 * approach rather than a field-based approach to support the JPA inheritance model.
 *
 * @author Marcus Portmann
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlTransient
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(schema = "demo", name = "vehicles")
public class VehicleBase implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The ID for the vehicle. */
  @NotNull
  @Id
  @Column(table = "vehicles", name = "id", nullable = false)
  private UUID id;

  /** The name of the vehicle. */
  @NotNull
  @Size(min = 1, max = 100)
  @Column(table = "vehicles", name = "name", length = 100, nullable = false)
  private String name;

  /** The vehicle type. */
  @NotNull
  @Column(table = "vehicles", name = "type", length = 30, nullable = false)
  private VehicleType type;

  /** Constructs a new <b>VehicleBase</b>. */
  public VehicleBase() {}

  /**
   * Constructs a new <b>VehicleBase</b>.
   *
   * @param type the vehicle type
   */
  protected VehicleBase(VehicleType type) {
    this.id = UuidCreator.getShortPrefixComb();
    this.type = type;
  }

  /**
   * Constructs a new <b>VehicleBase</b>.
   *
   * @param type the vehicle type
   * @param name the name of the vehicle
   */
  public VehicleBase(VehicleType type, String name) {
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

    VehicleBase other = (VehicleBase) object;

    return Objects.equals(id, other.id);
  }

  /**
   * Returns the ID for the vehicle.
   *
   * @return the ID for the vehicle
   */
  @XmlTransient
  public UUID getId() {
    return id;
  }

  /**
   * Returns the name of the vehicle.
   *
   * @return the name of the vehicle
   */
  @XmlTransient
  public String getName() {
    return name;
  }

  /**
   * Returns the vehicle type.
   *
   * @return the vehicle type
   */
  @XmlTransient
  public VehicleType getType() {
    return type;
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
   * Set the ID for the vehicle.
   *
   * @param id the ID for the vehicle
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
