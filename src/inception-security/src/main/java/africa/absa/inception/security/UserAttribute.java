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
 * The <b>UserAttribute</b> class stores a user attribute as a name-value pair.
 *
 * @author Marcus Portmann
 */
@Schema(description = "A user attribute in the form of a name-value pair")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"name", "value"})
@SuppressWarnings({"unused", "WeakerAccess"})
public class UserAttribute implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The name of the user attribute. */
  @Schema(description = "The name of the user attribute", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  private String name;

  /** The value for the user attribute. */
  @Schema(description = "The value for the user attribute", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(max = 4000)
  private String value;

  /** Constructs a new <b>UserAttribute</b>. */
  public UserAttribute() {}

  /**
   * Constructs a new <b>UserAttribute</b>.
   *
   * @param name the name of the user attribute
   * @param value the <b>BigDecimal</b> value for the user attribute
   */
  public UserAttribute(String name, BigDecimal value) {
    this.name = name;
    this.value = String.valueOf(value);
  }

  /**
   * Constructs a new <b>UserAttribute</b>.
   *
   * @param name the name of the user attribute
   * @param value the binary value for the user attribute
   */
  public UserAttribute(String name, BinaryBuffer value) {
    this.name = name;
    this.value = Base64Util.encodeBytes(value.getData());
  }

  /**
   * Constructs a new <b>UserAttribute</b>.
   *
   * @param name the name of the user attribute
   * @param value the binary value for the user attribute
   */
  public UserAttribute(String name, byte[] value) {
    this.name = name;
    this.value = Base64Util.encodeBytes(value);
  }

  /**
   * Constructs a new <b>UserAttribute</b>.
   *
   * @param name the name of the user attribute
   * @param value the <b>double</b> value for the user attribute
   */
  public UserAttribute(String name, double value) {
    this.name = name;
    this.value = String.valueOf(value);
  }

  /**
   * Constructs a new <b>UserAttribute</b>.
   *
   * @param name the name of the user attribute
   * @param value the <b>long</b> value for the user attribute
   */
  public UserAttribute(String name, long value) {
    this.name = name;
    this.value = String.valueOf(value);
  }

  /**
   * Constructs a new <b>UserAttribute</b>.
   *
   * @param name the name of the user attribute
   * @param value the <b>String</b> value for the user attribute
   */
  public UserAttribute(String name, String value) {
    this.name = name;
    this.value = value;
  }

  /**
   * Returns whether the list of user attributes contains a user attribute whose name matches the
   * specified name.
   *
   * @param list the user attributes to search
   * @param name the name of the attribute
   * @return <b>true</b> if the list of user attributes contains a user attribute whose name matches
   *     the specified name or <b>false</b> otherwise
   */
  public static boolean contains(List<UserAttribute> list, String name) {
    for (UserAttribute userAttribute : list) {
      if (userAttribute.name.equalsIgnoreCase(name)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Returns the binary value for the user attribute with the specified name in the specified list.
   *
   * @param list the user attributes to search
   * @param name the name of the attribute
   * @return the binary value for the user attribute with the specified name in the specified list
   * @throws UserAttributeException if the binary value could not be retrieved for the user
   *     attribute
   */
  public static byte[] getBinaryValue(List<UserAttribute> list, String name)
      throws UserAttributeException {
    for (UserAttribute userAttribute : list) {
      if (userAttribute.name.equalsIgnoreCase(name)) {
        try {
          return Base64Util.decode(userAttribute.value);
        } catch (Throwable e) {
          throw new UserAttributeException(
              String.format(
                  "Failed to retrieve the binary value for the user attribute (%s)",
                  userAttribute.name));
        }
      }
    }

    throw new UserAttributeException(
        String.format(
            "Failed to retrieve the binary value for the user attribute (%s): "
                + "The attribute could not be found",
            name));
  }

  /**
   * Returns the <b>BigDecimal</b> value for the user attribute with the specified name in the
   * specified list.
   *
   * @param list the user attributes to search
   * @param name the name of the attribute
   * @return the <b>BigDecimal</b> value for the user attribute with the specified name in the
   *     specified list
   * @throws UserAttributeException if the decimal value could not be retrieved for the user
   *     attribute
   */
  public static BigDecimal getDecimalValue(List<UserAttribute> list, String name)
      throws UserAttributeException {
    for (UserAttribute userAttribute : list) {
      if (userAttribute.name.equalsIgnoreCase(name)) {
        try {
          return new BigDecimal(userAttribute.value);
        } catch (Throwable e) {
          throw new UserAttributeException(
              String.format(
                  "Failed to retrieve the decimal value for the user attribute (%s)",
                  userAttribute.name));
        }
      }
    }

    throw new UserAttributeException(
        String.format(
            "Failed to retrieve the decimal value for the user attribute (%s): "
                + "The attribute could not be found",
            name));
  }

  /**
   * Returns the <b>double</b> value for the user attribute with the specified name in the specified
   * list.
   *
   * @param list the user attributes to search
   * @param name the name of the attribute
   * @return the <b>double</b> value for the user attribute with the specified name in the specified
   *     list
   * @throws UserAttributeException if the double value could not be retrieved for the user
   *     attribute
   */
  public static double getDoubleValue(List<UserAttribute> list, String name)
      throws UserAttributeException {
    for (UserAttribute userAttribute : list) {
      if (userAttribute.name.equalsIgnoreCase(name)) {
        try {
          return Double.parseDouble(userAttribute.value);
        } catch (Throwable e) {
          throw new UserAttributeException(
              String.format(
                  "Failed to retrieve the double value for the user attribute (%s)",
                  userAttribute.name));
        }
      }
    }

    throw new UserAttributeException(
        String.format(
            "Failed to retrieve the double value for the user attribute (%s): "
                + "The attribute could not be found",
            name));
  }

  /**
   * Returns the <b>long</b> value for the user attribute with the specified name in the specified
   * list.
   *
   * @param list the user attributes to search
   * @param name the name of the attribute
   * @return the <b>int</b> value for the user attribute with the specified name in the specified
   *     list
   * @throws UserAttributeException if the integer value could not be retrieved for the user
   *     attribute
   */
  public static int getIntegerValue(List<UserAttribute> list, String name)
      throws UserAttributeException {
    for (UserAttribute userAttribute : list) {
      if (userAttribute.name.equalsIgnoreCase(name)) {
        try {
          return Integer.parseInt(userAttribute.value);
        } catch (Throwable e) {
          throw new UserAttributeException(
              String.format(
                  "Failed to retrieve the integer value for the user attribute (%s)",
                  userAttribute.name));
        }
      }
    }

    throw new UserAttributeException(
        String.format(
            "Failed to retrieve the integer value for the user attribute (%s): "
                + "The attribute could not be found",
            name));
  }

  /**
   * Returns the <b>long</b> value for the user attribute with the specified name in the specified
   * list.
   *
   * @param list the user attributes to search
   * @param name the name of the attribute
   * @return the <b>long</b> value for the user attribute with the specified name in the specified
   *     list
   * @throws UserAttributeException if the long value could not be retrieved for the user attribute
   */
  public static long getLongValue(List<UserAttribute> list, String name)
      throws UserAttributeException {
    for (UserAttribute userAttribute : list) {
      if (userAttribute.name.equalsIgnoreCase(name)) {
        try {
          return Long.parseLong(userAttribute.value);
        } catch (Throwable e) {
          throw new UserAttributeException(
              String.format(
                  "Failed to retrieve the long value for the user attribute (%s)",
                  userAttribute.name));
        }
      }
    }

    throw new UserAttributeException(
        String.format(
            "Failed to retrieve the long value for the user attribute (%s): "
                + "The attribute could not be found",
            name));
  }

  /**
   * Returns the <b>String</b> value for the user attribute with the specified name in the specified
   * list.
   *
   * @param list the user attributes to search
   * @param name the name of the attribute
   * @return the <b>String</b> value for the user attribute with the specified name in the specified
   *     list
   * @throws UserAttributeException if the string value could not be retrieved for the user
   *     attribute
   */
  public static String getStringValue(List<UserAttribute> list, String name)
      throws UserAttributeException {
    for (UserAttribute userAttribute : list) {
      if (userAttribute.name.equalsIgnoreCase(name)) {
        return userAttribute.value;
      }
    }

    throw new UserAttributeException(
        String.format(
            "Failed to retrieve the string value for the user attribute (%s): "
                + "The attribute could not be found",
            name));
  }

  /**
   * Set the binary value for the user attribute with the specified name in the specified list.
   *
   * @param list the user attributes to search
   * @param name the name of the attribute
   * @param value the binary value for the user attribute
   * @throws UserAttributeException if the binary value could not be set for the user attribute
   */
  public static void setBinaryValue(List<UserAttribute> list, String name, BinaryBuffer value)
      throws UserAttributeException {
    setBinaryValue(list, name, value.getData());
  }

  /**
   * Set the binary value for the user attribute with the specified name in the specified list.
   *
   * @param list the user attributes to search
   * @param name the name of the attribute
   * @param value the binary value for the user attribute
   * @throws UserAttributeException if the binary value could not be set for the user attribute
   */
  public static void setBinaryValue(List<UserAttribute> list, String name, byte[] value)
      throws UserAttributeException {
    for (UserAttribute userAttribute : list) {
      if (userAttribute.name.equalsIgnoreCase(name)) {
        userAttribute.setBinaryValue(value);

        return;
      }
    }

    throw new UserAttributeException(
        String.format(
            "Failed to set the binary value for the user attribute (%s): "
                + "The attribute could not be found",
            name));
  }

  /**
   * Set the <b>BigDecimal</b> value for the user attribute with the specified name in the specified
   * list.
   *
   * @param list the user attributes to search
   * @param name the name of the attribute
   * @param value the <b>BigDecimal</b> value for the user attribute
   * @throws UserAttributeException if the decimal value could not be set for the user attribute
   */
  public static void setDecimalValue(List<UserAttribute> list, String name, BigDecimal value)
      throws UserAttributeException {
    for (UserAttribute userAttribute : list) {
      if (userAttribute.name.equalsIgnoreCase(name)) {
        userAttribute.setDecimalValue(value);

        return;
      }
    }

    throw new UserAttributeException(
        String.format(
            "Failed to set the decimal value for the user attribute (%s): "
                + "The attribute could not be found",
            name));
  }

  /**
   * Set the <b>double</b> value for the user attribute with the specified name in the specified
   * list.
   *
   * @param list the user attributes to search
   * @param name the name of the attribute
   * @param value the <b>double</b> value for the user attribute
   * @throws UserAttributeException if the double value could not be set for the user attribute
   */
  public static void setDoubleValue(List<UserAttribute> list, String name, double value)
      throws UserAttributeException {
    for (UserAttribute userAttribute : list) {
      if (userAttribute.name.equalsIgnoreCase(name)) {
        userAttribute.setDoubleValue(value);

        return;
      }
    }

    throw new UserAttributeException(
        String.format(
            "Failed to set the double value for the user attribute (%s): "
                + "The attribute could not be found",
            name));
  }

  /**
   * Set the <b>int</b> value for the user attribute with the specified name in the specified list.
   *
   * @param list the user attributes to search
   * @param name the name of the attribute
   * @param value the <b>int</b> value for the user attribute
   * @throws UserAttributeException if the integer value could not be set for the user attribute
   */
  public static void setIntegerValue(List<UserAttribute> list, String name, int value)
      throws UserAttributeException {
    for (UserAttribute userAttribute : list) {
      if (userAttribute.name.equalsIgnoreCase(name)) {
        userAttribute.setIntegerValue(value);

        return;
      }
    }

    throw new UserAttributeException(
        String.format(
            "Failed to set the integer value for the user attribute (%s): "
                + "The attribute could not be found",
            name));
  }

  /**
   * Set the <b>long</b> value for the user attribute with the specified name in the specified list.
   *
   * @param list the user attributes to search
   * @param name the name of the attribute
   * @param value the <b>long</b> value for the user attribute
   * @throws UserAttributeException if the long value could not be set for the user attribute
   */
  public static void setLongValue(List<UserAttribute> list, String name, long value)
      throws UserAttributeException {
    for (UserAttribute userAttribute : list) {
      if (userAttribute.name.equalsIgnoreCase(name)) {
        userAttribute.setLongValue(value);

        return;
      }
    }

    throw new UserAttributeException(
        String.format(
            "Failed to set the long value for the user attribute (%s): "
                + "The attribute could not be found",
            name));
  }

  /**
   * Set the <b>String</b> value for the user attribute with the specified name in the specified
   * list.
   *
   * @param list the user attributes to search
   * @param name the name of the attribute
   * @param value the <b>String</b> value for the user attribute
   * @throws UserAttributeException if the string value could not be set for the user attribute
   */
  public static void setStringValue(List<UserAttribute> list, String name, String value)
      throws UserAttributeException {
    for (UserAttribute userAttribute : list) {
      if (userAttribute.name.equalsIgnoreCase(name)) {
        userAttribute.setStringValue(value);

        return;
      }
    }

    throw new UserAttributeException(
        String.format(
            "Failed to set the string value for the user attribute (%s): "
                + "The attribute could not be found",
            name));
  }

  /**
   * Returns the binary value for the user attribute.
   *
   * @return the binary value for the user attribute
   * @throws UserAttributeException if the binary value could not be retrieved for the user
   *     attribute
   */
  public byte[] getBinaryValue() throws UserAttributeException {
    try {
      return Base64Util.decode(value);
    } catch (Throwable e) {
      throw new UserAttributeException(
          String.format("Failed to retrieve the binary value for the user attribute (%s)", name));
    }
  }

  /**
   * Returns the <b>BigDecimal</b> value for the user attribute.
   *
   * @return the <b>BigDecimal</b> value for the user attribute
   * @throws UserAttributeException if the decimal value could not be retrieved for the user
   *     attribute
   */
  public BigDecimal getDecimalValue() throws UserAttributeException {
    try {
      return new BigDecimal(value);
    } catch (Throwable e) {
      throw new UserAttributeException(
          String.format("Failed to retrieve the decimal value for the user attribute (%s)", name));
    }
  }

  /**
   * Returns the <b>double</b> value for the user attribute.
   *
   * @return the <b>double</b> value for the user attribute
   * @throws UserAttributeException if the double value could not be retrieved for the user
   *     attribute
   */
  public double getDoubleValue() throws UserAttributeException {
    try {
      return Double.parseDouble(value);
    } catch (Throwable e) {
      throw new UserAttributeException(
          String.format("Failed to retrieve the double value for the user attribute (%s)", name));
    }
  }

  /**
   * Returns the <b>int</b> value for the user attribute.
   *
   * @return the <b>int</b> value for the user attribute
   * @throws UserAttributeException if the integer value could not be retrieved for the user
   *     attribute
   */
  public int getIntegerValue() throws UserAttributeException {
    try {
      return Integer.parseInt(value);
    } catch (Throwable e) {
      throw new UserAttributeException(
          String.format("Failed to retrieve the integer value for the user attribute (%s)", name));
    }
  }

  /**
   * Returns the <b>long</b> value for the user attribute.
   *
   * @return the <b>long</b> value for the user attribute
   * @throws UserAttributeException if the long value could not be retrieved for the user attribute
   */
  public long getLongValue() throws UserAttributeException {
    try {
      return Long.parseLong(value);
    } catch (Throwable e) {
      throw new UserAttributeException(
          String.format("Failed to retrieve the long value for the user attribute (%s)", name));
    }
  }

  /**
   * Returns the name of the user attribute.
   *
   * @return the name of the user attribute
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the <b>String</b> value for the user attribute.
   *
   * @return the <b>String</b> value for the user attribute
   */
  public String getStringValue() {
    return value;
  }

  /**
   * Returns the <b>String</b> value for the user attribute.
   *
   * @return the <b>String</b> value for the user attribute
   */
  public String getValue() {
    return value;
  }

  /**
   * Set the binary value for the user attribute.
   *
   * @param value the binary value for the user attribute
   */
  public void setBinaryValue(BinaryBuffer value) {
    this.value = Base64Util.encodeBytes(value.getData());
  }

  /**
   * Set the binary value for the user attribute.
   *
   * @param value the binary value for the user attribute
   */
  public void setBinaryValue(byte[] value) {
    this.value = Base64Util.encodeBytes(value);
  }

  /**
   * Set the <b>BigDecimal</b> value for the user attribute.
   *
   * @param value the <b>BigDecimal</b> value for the user attribute
   */
  public void setDecimalValue(BigDecimal value) {
    this.value = String.valueOf(value);
  }

  /**
   * Set the <b>double</b> value for the user attribute.
   *
   * @param value the <b>double</b> value for the user attribute
   */
  public void setDoubleValue(double value) {
    this.value = String.valueOf(value);
  }

  /**
   * Set the <b>int</b> value for the user attribute.
   *
   * @param value the <b>int</b> value for the user attribute
   */
  public void setIntegerValue(int value) {
    this.value = String.valueOf(value);
  }

  /**
   * Set the <b>long</b> value for the user attribute.
   *
   * @param value the <b>long</b> value for the user attribute
   */
  public void setLongValue(long value) {
    this.value = String.valueOf(value);
  }

  /**
   * Set the name of the user attribute.
   *
   * @param name the name of the user attribute
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Set the <b>String</b> value for the user attribute.
   *
   * @param value the <b>String</b> value for the user attribute
   */
  public void setStringValue(String value) {
    this.value = value;
  }
}
