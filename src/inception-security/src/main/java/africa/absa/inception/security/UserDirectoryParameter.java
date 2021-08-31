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

package africa.absa.inception.security;

import africa.absa.inception.core.util.Base64Util;
import africa.absa.inception.core.util.BinaryBuffer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The <b>UserDirectoryParameter</b> class stores a parameter for a user directory as a name-value
 * pair.
 *
 * @author Marcus Portmann
 */
@Schema(description = "A name-value pair parameter for a user directory")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"name", "value"})
@SuppressWarnings({"unused", "WeakerAccess"})
public class UserDirectoryParameter implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The name of the user directory parameter. */
  @Schema(description = "The name of the user directory parameter", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  private String name;

  /** The value for the user directory parameter. */
  @Schema(description = "The value for the user directory parameter", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(max = 4000)
  private String value;

  /** Constructs a new <b>UserDirectoryParameter</b>. */
  public UserDirectoryParameter() {}

  /**
   * Constructs a new <b>UserDirectoryParameter</b>.
   *
   * @param name the name of the user directory parameter
   * @param value the <b>BigDecimal</b> value for the user directory parameter
   */
  public UserDirectoryParameter(String name, BigDecimal value) {
    this.name = name;
    this.value = String.valueOf(value);
  }

  /**
   * Constructs a new <b>UserDirectoryParameter</b>.
   *
   * @param name the name of the user directory parameter
   * @param value the binary value for the user directory parameter
   */
  public UserDirectoryParameter(String name, BinaryBuffer value) {
    this.name = name;
    this.value = Base64Util.encodeBytes(value.getData());
  }

  /**
   * Constructs a new <b>UserDirectoryParameter</b>.
   *
   * @param name the name of the user directory parameter
   * @param value the binary value for the user directory parameter
   */
  public UserDirectoryParameter(String name, byte[] value) {
    this.name = name;
    this.value = Base64Util.encodeBytes(value);
  }

  /**
   * Constructs a new <b>UserDirectoryParameter</b>.
   *
   * @param name the name of the user directory parameter
   * @param value the <b>double</b> value for the user directory parameter
   */
  public UserDirectoryParameter(String name, double value) {
    this.name = name;
    this.value = String.valueOf(value);
  }

  /**
   * Constructs a new <b>UserDirectoryParameter</b>.
   *
   * @param name the name of the user directory parameter
   * @param value the <b>long</b> value for the user directory parameter
   */
  public UserDirectoryParameter(String name, long value) {
    this.name = name;
    this.value = String.valueOf(value);
  }

  /**
   * Constructs a new <b>UserDirectoryParameter</b>.
   *
   * @param name the name of the user directory parameter
   * @param value the <b>String</b> value for the user directory parameter
   */
  public UserDirectoryParameter(String name, String value) {
    this.name = name;
    this.value = value;
  }

  /**
   * Returns whether the list of user directory parameters contains a user directory parameter whose
   * name matches the specified name.
   *
   * @param parameters the user directory parameters to search
   * @param name the name of the user directory parameter to search for
   * @return <b>true</b> if the list of user directory parameters contains a user directory
   *     parameter whose name matches the specified name or <b>false</b> otherwise
   */
  public static boolean contains(List<UserDirectoryParameter> parameters, String name) {
    for (UserDirectoryParameter parameter : parameters) {
      if (parameter.name.equalsIgnoreCase(name)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Returns the binary value for the user directory parameter with the specified name in the
   * specified list.
   *
   * @param parameters the user directory parameters to search
   * @param name the name of the user directory parameter to search for
   * @return the binary value for the user directory parameter with the specified name in the
   *     specified list
   * @throws UserDirectoryParameterException if the binary value could not be retrieved for the user
   *     directory parameter
   */
  public static byte[] getBinaryValue(List<UserDirectoryParameter> parameters, String name)
      throws UserDirectoryParameterException {
    for (UserDirectoryParameter parameter : parameters) {
      if (parameter.name.equalsIgnoreCase(name)) {
        try {
          return Base64Util.decode(parameter.value);
        } catch (Throwable e) {
          throw new UserDirectoryParameterException(
              String.format(
                  "Failed to retrieve the binary value for the user directory parameter (%s)",
                  parameter.name));
        }
      }
    }

    throw new UserDirectoryParameterException(
        String.format(
            "Failed to retrieve the binary value for the user directory parameter (%s): "
                + "The user directory parameter could not be found",
            name));
  }

  /**
   * Returns the <b>boolean</b> value for the user directory parameter with the specified name in
   * the specified list.
   *
   * @param parameters the user directory parameters to search
   * @param name the name of the user directory parameter to search for
   * @return the <b>boolean</b> value for the user directory parameter with the specified name in
   *     the specified list
   * @throws UserDirectoryParameterException if the boolean value could not be retrieved for the
   *     user directory parameter
   */
  public static boolean getBooleanValue(List<UserDirectoryParameter> parameters, String name)
      throws UserDirectoryParameterException {
    for (UserDirectoryParameter parameter : parameters) {
      if (parameter.name.equalsIgnoreCase(name)) {
        try {
          return Boolean.parseBoolean(parameter.value);
        } catch (Throwable e) {
          throw new UserDirectoryParameterException(
              String.format(
                  "Failed to retrieve the boolean value for the user directory parameter (%s)",
                  parameter.name));
        }
      }
    }

    throw new UserDirectoryParameterException(
        String.format(
            "Failed to retrieve the boolean value for the user directory parameter (%s): "
                + "The user directory parameter could not be found",
            name));
  }

  /**
   * Returns the <b>BigDecimal</b> value for the user directory parameter with the specified name in
   * the specified list.
   *
   * @param parameters the user directory parameters to search
   * @param name the name of the user directory parameter to search for
   * @return the <b>BigDecimal</b> value for the user directory parameter with the specified name in
   *     the specified list
   * @throws UserDirectoryParameterException if the decimal value could not be retrieved for the
   *     user directory parameter
   */
  public static BigDecimal getDecimalValue(List<UserDirectoryParameter> parameters, String name)
      throws UserDirectoryParameterException {
    for (UserDirectoryParameter parameter : parameters) {
      if (parameter.name.equalsIgnoreCase(name)) {
        try {
          return new BigDecimal(parameter.value);
        } catch (Throwable e) {
          throw new UserDirectoryParameterException(
              String.format(
                  "Failed to retrieve the decimal value for the user directory parameter (%s)",
                  parameter.name));
        }
      }
    }

    throw new UserDirectoryParameterException(
        String.format(
            "Failed to retrieve the decimal value for the user directory parameter (%s): "
                + "The user directory parameter could not be found",
            name));
  }

  /**
   * Returns the <b>double</b> value for the user directory parameter with the specified name in the
   * specified list.
   *
   * @param parameters the user directory parameters to search
   * @param name the name of the user directory parameter to search for
   * @return the <b>double</b> value for the user directory parameter with the specified name in the
   *     specified list
   * @throws UserDirectoryParameterException if the double value could not be retrieved for the user
   *     directory parameter
   */
  public static double getDoubleValue(List<UserDirectoryParameter> parameters, String name)
      throws UserDirectoryParameterException {
    for (UserDirectoryParameter parameter : parameters) {
      if (parameter.name.equalsIgnoreCase(name)) {
        try {
          return Double.parseDouble(parameter.value);
        } catch (Throwable e) {
          throw new UserDirectoryParameterException(
              String.format(
                  "Failed to retrieve the double value for the user directory parameter (%s)",
                  parameter.name));
        }
      }
    }

    throw new UserDirectoryParameterException(
        String.format(
            "Failed to retrieve the double value for the user directory parameter (%s): "
                + "The user directory parameter could not be found",
            name));
  }

  /**
   * Returns the <b>int</b> value for the user directory parameter with the specified name in the
   * specified list.
   *
   * @param parameters the user directory parameters to search
   * @param name the name of the user directory parameter to search for
   * @return the <b>int</b> value for the user directory parameter with the specified name in the
   *     specified list
   * @throws UserDirectoryParameterException if the integer value could not be retrieved for the
   *     user directory parameter
   */
  public static int getIntegerValue(List<UserDirectoryParameter> parameters, String name)
      throws UserDirectoryParameterException {
    for (UserDirectoryParameter parameter : parameters) {
      if (parameter.name.equalsIgnoreCase(name)) {
        try {
          return Integer.parseInt(parameter.value);
        } catch (Throwable e) {
          throw new UserDirectoryParameterException(
              String.format(
                  "Failed to retrieve the integer value for the user directory parameter (%s)",
                  parameter.name));
        }
      }
    }

    throw new UserDirectoryParameterException(
        String.format(
            "Failed to retrieve the integer value for the user directory parameter (%s): "
                + "The user directory parameter could not be found",
            name));
  }

  /**
   * Returns the <b>long</b> value for the user directory parameter with the specified name in the
   * specified list.
   *
   * @param parameters the user directory parameters to search
   * @param name the name of the user directory parameter to search for
   * @return the <b>long</b> value for the user directory parameter with the specified name in the
   *     specified list
   * @throws UserDirectoryParameterException if the long value could not be retrieved for the user
   *     directory parameter
   */
  public static long getLongValue(List<UserDirectoryParameter> parameters, String name)
      throws UserDirectoryParameterException {
    for (UserDirectoryParameter parameter : parameters) {
      if (parameter.name.equalsIgnoreCase(name)) {
        try {
          return Long.parseLong(parameter.value);
        } catch (Throwable e) {
          throw new UserDirectoryParameterException(
              String.format(
                  "Failed to retrieve the long value for the user directory parameter (%s)",
                  parameter.name));
        }
      }
    }

    throw new UserDirectoryParameterException(
        String.format(
            "Failed to retrieve the long value for the user directory parameter (%s): "
                + "The user directory parameter could not be found",
            name));
  }

  /**
   * Returns the <b>String</b> value for the user directory parameter with the specified name in the
   * specified list.
   *
   * @param parameters the user directory parameters to search
   * @param name the name of the user directory parameter to search for
   * @return the <b>String</b> value for the user directory parameter with the specified name in the
   *     specified list
   * @throws UserDirectoryParameterException if the string value could not be retrieved for the user
   *     directory parameter
   */
  public static String getStringValue(List<UserDirectoryParameter> parameters, String name)
      throws UserDirectoryParameterException {
    for (UserDirectoryParameter parameter : parameters) {
      if (parameter.name.equalsIgnoreCase(name)) {
        return parameter.value;
      }
    }

    throw new UserDirectoryParameterException(
        String.format(
            "Failed to retrieve the string value for the user directory parameter (%s): "
                + "The user directory parameter could not be found",
            name));
  }

  /**
   * Set the binary value for the user directory parameter with the specified name in the specified
   * list.
   *
   * @param parameters the user directory parameters to search
   * @param name the name of the user directory parameter to search for
   * @param value the binary value for the user directory parameter
   * @throws UserDirectoryParameterException if the binary value could not be set for the user
   *     directory parameter
   */
  public static void setBinaryValue(
      List<UserDirectoryParameter> parameters, String name, BinaryBuffer value)
      throws UserDirectoryParameterException {
    setBinaryValue(parameters, name, value.getData());
  }

  /**
   * Set the binary value for the user directory parameter with the specified name in the specified
   * list.
   *
   * @param parameters the user directory parameters to search
   * @param name the name of the user directory parameter to search for
   * @param value the binary value for the user directory parameter
   * @throws UserDirectoryParameterException if the binary value could not be set for the user
   *     directory parameter
   */
  public static void setBinaryValue(
      List<UserDirectoryParameter> parameters, String name, byte[] value)
      throws UserDirectoryParameterException {
    for (UserDirectoryParameter parameter : parameters) {
      if (parameter.name.equalsIgnoreCase(name)) {
        parameter.setBinaryValue(value);

        return;
      }
    }

    throw new UserDirectoryParameterException(
        String.format(
            "Failed to set the binary value for the user directory parameter (%s): "
                + "The user directory parameter could not be found",
            name));
  }

  /**
   * Set the <b>BigDecimal</b> value for the user directory parameter with the specified name in the
   * specified list.
   *
   * @param parameters the user directory parameters to search
   * @param name the name of the user directory parameter to search for
   * @param value the <b>BigDecimal</b> value for the user directory parameter
   * @throws UserDirectoryParameterException if the decimal value could not be set for the user
   *     directory parameter
   */
  public static void setDecimalValue(
      List<UserDirectoryParameter> parameters, String name, BigDecimal value)
      throws UserDirectoryParameterException {
    for (UserDirectoryParameter parameter : parameters) {
      if (parameter.name.equalsIgnoreCase(name)) {
        parameter.setDecimalValue(value);

        return;
      }
    }

    throw new UserDirectoryParameterException(
        String.format(
            "Failed to set the decimal value for the user directory parameter (%s): "
                + "The user directory parameter could not be found",
            name));
  }

  /**
   * Set the <b>double</b> value for the user directory parameter with the specified name in the
   * specified list.
   *
   * @param parameters the user directory parameters to search
   * @param name the name of the user directory parameter to search for
   * @param value the <b>double</b> value for the user directory parameter
   * @throws UserDirectoryParameterException if the double value could not be set for the user
   *     directory parameter
   */
  public static void setDoubleValue(
      List<UserDirectoryParameter> parameters, String name, double value)
      throws UserDirectoryParameterException {
    for (UserDirectoryParameter parameter : parameters) {
      if (parameter.name.equalsIgnoreCase(name)) {
        parameter.setDoubleValue(value);

        return;
      }
    }

    throw new UserDirectoryParameterException(
        String.format(
            "Failed to set the double value for the user directory parameter (%s): "
                + "The user directory parameter could not be found",
            name));
  }

  /**
   * Set the <b>int</b> value for the user directory parameter with the specified name in the
   * specified list.
   *
   * @param parameters the user directory parameters to search
   * @param name the name of the user directory parameter to search for
   * @param value the <b>int</b> value for the user directory parameter
   * @throws UserDirectoryParameterException if the integer value could not be set for the user
   *     directory parameter
   */
  public static void setIntegerValue(
      List<UserDirectoryParameter> parameters, String name, int value)
      throws UserDirectoryParameterException {
    for (UserDirectoryParameter parameter : parameters) {
      if (parameter.name.equalsIgnoreCase(name)) {
        parameter.setIntegerValue(value);

        return;
      }
    }

    throw new UserDirectoryParameterException(
        String.format(
            "Failed to set the long value for the user directory parameter (%s): "
                + "The user directory parameter could not be found",
            name));
  }

  /**
   * Set the <b>long</b> value for the user directory parameter with the specified name in the
   * specified list.
   *
   * @param parameters the user directory parameters to search
   * @param name the name of the user directory parameter to search for
   * @param value the <b>long</b> value for the user directory parameter
   * @throws UserDirectoryParameterException if the long value could not be set for the user
   *     directory parameter
   */
  public static void setLongValue(List<UserDirectoryParameter> parameters, String name, long value)
      throws UserDirectoryParameterException {
    for (UserDirectoryParameter parameter : parameters) {
      if (parameter.name.equalsIgnoreCase(name)) {
        parameter.setLongValue(value);

        return;
      }
    }

    throw new UserDirectoryParameterException(
        String.format(
            "Failed to set the long value for the user directory parameter (%s): "
                + "The user directory parameter could not be found",
            name));
  }

  /**
   * Set the <b>String</b> value for the user directory parameter with the specified name in the
   * specified list.
   *
   * @param parameters the user directory parameters to search
   * @param name the name of the user directory parameter to search for
   * @param value the <b>String</b> value for the user directory parameter
   * @throws UserDirectoryParameterException if the string value could not be set for the user
   *     directory parameter
   */
  public static void setStringValue(
      List<UserDirectoryParameter> parameters, String name, String value)
      throws UserDirectoryParameterException {
    for (UserDirectoryParameter parameter : parameters) {
      if (parameter.name.equalsIgnoreCase(name)) {
        parameter.setStringValue(value);

        return;
      }
    }

    throw new UserDirectoryParameterException(
        String.format(
            "Failed to set the string value for the user directory parameter (%s): "
                + "The user directory parameter could not be found",
            name));
  }

  /**
   * Returns the binary value for the user directory parameter.
   *
   * @return the binary value for the user directory parameter
   * @throws UserDirectoryParameterException if the binary value could not be retrieved for the user
   *     directory parameter
   */
  @JsonIgnore
  public byte[] getBinaryValue() throws UserDirectoryParameterException {
    try {
      return Base64Util.decode(value);
    } catch (Throwable e) {
      throw new UserDirectoryParameterException(
          String.format(
              "Failed to retrieve the binary value for the user directory parameter (%s)", name));
    }
  }

  /**
   * Returns the <b>BigDecimal</b> value for the user directory parameter.
   *
   * @return the <b>BigDecimal</b> value for the user directory parameter
   * @throws UserDirectoryParameterException if the decimal value could not be retrieved for the
   *     user directory parameter
   */
  @JsonIgnore
  public BigDecimal getDecimalValue() throws UserDirectoryParameterException {
    try {
      return new BigDecimal(value);
    } catch (Throwable e) {
      throw new UserDirectoryParameterException(
          String.format(
              "Failed to retrieve the decimal value for the user directory parameter (%s)", name));
    }
  }

  /**
   * Returns the <b>double</b> value for the user directory parameter.
   *
   * @return the <b>double</b> value for the user directory parameter
   * @throws UserDirectoryParameterException if the double value could not be retrieved for the user
   *     directory parameter
   */
  @JsonIgnore
  public double getDoubleValue() throws UserDirectoryParameterException {
    try {
      return Double.parseDouble(value);
    } catch (Throwable e) {
      throw new UserDirectoryParameterException(
          String.format(
              "Failed to retrieve the double value for the user directory parameter (%s)", name));
    }
  }

  /**
   * Returns the <b>int</b> value for the user directory parameter.
   *
   * @return the <b>int</b> value for the user directory parameter
   * @throws UserDirectoryParameterException if the integer value could not be retrieved for the
   *     user directory parameter
   */
  @JsonIgnore
  public int getIntegerValue() throws UserDirectoryParameterException {
    try {
      return Integer.parseInt(value);
    } catch (Throwable e) {
      throw new UserDirectoryParameterException(
          String.format(
              "Failed to retrieve the integer value for the user directory parameter (%s)", name));
    }
  }

  /**
   * Returns the <b>long</b> value for the user directory parameter.
   *
   * @return the <b>long</b> value for the user directory parameter
   * @throws UserDirectoryParameterException if the long value could not be retrieved for the user
   *     directory parameter
   */
  @JsonIgnore
  public long getLongValue() throws UserDirectoryParameterException {
    try {
      return Long.parseLong(value);
    } catch (Throwable e) {
      throw new UserDirectoryParameterException(
          String.format(
              "Failed to retrieve the long value for the user directory parameter (%s)", name));
    }
  }

  /**
   * Returns the name of the user directory parameter.
   *
   * @return the name of the user directory parameter
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the <b>String</b> value for the user directory parameter.
   *
   * @return the <b>String</b> value for the user directory parameter
   */
  @JsonIgnore
  public String getStringValue() {
    return value;
  }

  /**
   * Returns the <b>String</b> value for the user directory parameter.
   *
   * @return the <b>String</b> value for the user directory parameter
   */
  public String getValue() {
    return value;
  }

  /**
   * Set the binary value for the user directory parameter.
   *
   * @param value the binary value for the user directory parameter
   */
  @JsonIgnore
  public void setBinaryValue(BinaryBuffer value) {
    this.value = Base64Util.encodeBytes(value.getData());
  }

  /**
   * Set the binary value for the user directory parameter.
   *
   * @param value the binary value for the user directory parameter
   */
  @JsonIgnore
  public void setBinaryValue(byte[] value) {
    this.value = Base64Util.encodeBytes(value);
  }

  /**
   * Set the <b>boolean</b> value for the user directory parameter.
   *
   * @param value the <b>boolean</b> value for the user directory parameter
   */
  @JsonIgnore
  public void setBooleanValue(boolean value) {
    this.value = String.valueOf(value);
  }

  /**
   * Set the <b>BigDecimal</b> value for the user directory parameter.
   *
   * @param value the <b>BigDecimal</b> value for the user directory parameter
   */
  @JsonIgnore
  public void setDecimalValue(BigDecimal value) {
    this.value = String.valueOf(value);
  }

  /**
   * Set the <b>double</b> value for the user directory parameter.
   *
   * @param value the <b>double</b> value for the user directory parameter
   */
  @JsonIgnore
  public void setDoubleValue(double value) {
    this.value = String.valueOf(value);
  }

  /**
   * Set the <b>int</b> value for the user directory parameter.
   *
   * @param value the <b>int</b> value for the user directory parameter
   */
  @JsonIgnore
  public void setIntegerValue(int value) {
    this.value = String.valueOf(value);
  }

  /**
   * Set the <b>long</b> value for the user directory parameter.
   *
   * @param value the <b>long</b> value for the user directory parameter
   */
  @JsonIgnore
  public void setLongValue(long value) {
    this.value = String.valueOf(value);
  }

  /**
   * Set the name of the user directory parameter.
   *
   * @param name the name of the user directory parameter
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Set the <b>String</b> value for the user directory parameter.
   *
   * @param value the <b>String</b> value for the user directory parameter
   */
  @JsonIgnore
  public void setStringValue(String value) {
    this.value = value;
  }

  /**
   * Set the <b>String</b> value for the user directory parameter.
   *
   * @param value the <b>String</b> value for the user directory parameter
   */
  public void setValue(String value) {
    this.value = value;
  }
}
