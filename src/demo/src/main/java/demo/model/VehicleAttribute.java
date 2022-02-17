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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * The <b>VehicleAttribute</b> class holds the information for a vehicle attribute for a vehicle.
 *
 * @author Marcus Portmann
 */
@Schema(description = "A vehicle attribute for a vehicle")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"type", "value"})
@XmlRootElement(name = "VehicleAttribute", namespace = "http://demo")
@XmlType(
    name = "VehicleAttribute",
    namespace = "http://demo",
    propOrder = {"type", "value"})
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(schema = "demo", name = "vehicle_attributes")
@IdClass(VehicleAttributeId.class)
public class VehicleAttribute implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The code for the vehicle attribute type. */
  @Schema(description = "The code for the vehicle attribute type", required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "Type", required = true)
  @NotNull
  @Size(min = 1, max = 30)
  @Id
  @Column(name = "type", length = 30, nullable = false)
  private String type;

  /** The value for the vehicle attribute. */
  @Schema(description = "The value for the vehicle attribute", required = true)
  @JsonProperty(required = true)
  @XmlElement(name = "Value", required = true)
  @NotNull
  @Size(min = 1, max = 200)
  @Column(name = "value", length = 200, nullable = false)
  private String value;

  /** The vehicle the vehicle attribute is associated with. */
  @Schema(hidden = true)
  @JsonBackReference("vehicleAttributeReference")
  @XmlTransient
  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vehicle_id")
  private VehicleBase vehicle;

  /** Constructs a new <b>VehicleAttribute</b>. */
  public VehicleAttribute() {}

  /**
   * Constructs a new <b>VehicleAttribute</b>.
   *
   * @param type the vehicle attribute type
   * @param value the value for the vehicle attribute
   */
  public VehicleAttribute(String type, String value) {
    this.type = type;
    this.value = value;
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

    VehicleAttribute other = (VehicleAttribute) object;

    return Objects.equals(vehicle, other.vehicle) && Objects.equals(type, other.type);
  }

  /**
   * Returns the code for the vehicle attribute type.
   *
   * @return the code for the vehicle attribute type
   */
  public String getType() {
    return type;
  }

  /**
   * Returns the value for the vehicle attribute.
   *
   * @return the value for the vehicle attribute
   */
  public String getValue() {
    return value;
  }

  /**
   * Returns the vehicle the vehicle attribute is associated with.
   *
   * @return the vehicle the vehicle attribute is associated with
   */
  @Schema(hidden = true)
  public VehicleBase getVehicle() {
    return vehicle;
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for the object
   */
  @Override
  public int hashCode() {
    return (((vehicle == null) || (vehicle.getId() == null)) ? 0 : vehicle.getId().hashCode())
        + ((type == null) ? 0 : type.hashCode());
  }

  /**
   * Set the code for the vehicle attribute type.
   *
   * @param type the code for the vehicle attribute type
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Set the value for the vehicle attribute.
   *
   * @param value the value for the vehicle attribute
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Set the vehicle the vehicle attribute is associated with.
   *
   * @param vehicle the vehicle the vehicle attribute is associated with
   */
  @Schema(hidden = true)
  public void setVehicle(VehicleBase vehicle) {
    this.vehicle = vehicle;
  }
}
