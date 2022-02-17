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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * The <b>VehicleType</b> enumeration defines the possible vehicle types.
 *
 * @author Marcus Portmann
 */
@Schema(description = "The vehicle type")
@XmlEnum
@XmlType(name = "VehicleType", namespace = "http://demo")
public enum VehicleType {
  /** Car. */
  @XmlEnumValue("Car")
  CAR("car", "Car"),

  /** Motorbike. */
  @XmlEnumValue("Motorbike")
  MOTORBIKE("motorbike", "Motorbike"),

  /** Unknown */
  @XmlEnumValue("Unknown")
  UNKNOWN("unknown", "Unknown");

  private final String code;

  private final String description;

  VehicleType(String code, String description) {
    this.code = code;
    this.description = description;
  }

  /**
   * Returns the vehicle type given by the specified code value.
   *
   * @param code the code for the vehicle type
   * @return the vehicle type given by the specified code value
   */
  @JsonCreator
  public static VehicleType fromCode(String code) {
    switch (code) {
      case "car":
        return VehicleType.CAR;

      case "motorbike":
        return VehicleType.MOTORBIKE;

      case "unknown":
        return VehicleType.UNKNOWN;

      default:
        throw new RuntimeException(
            "Failed to determine the vehicle type with the invalid code (" + code + ")");
    }
  }

  /**
   * Returns the code for the vehicle type.
   *
   * @return the code for the vehicle type
   */
  @JsonValue
  public String code() {
    return code;
  }

  /**
   * Returns the description for the vehicle type.
   *
   * @return the description for the vehicle type
   */
  public String description() {
    return description;
  }
}
