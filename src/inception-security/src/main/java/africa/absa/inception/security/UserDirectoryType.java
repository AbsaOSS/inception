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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The <b>UserDirectoryType</b> class holds the information for a user directory type.
 *
 * @author Marcus Portmann
 */
@Schema(description = "A user directory type")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"code", "name", "userDirectoryClassName"})
@Entity
@Table(schema = "security", name = "user_directory_types")
@SuppressWarnings({"unused"})
public class UserDirectoryType implements java.io.Serializable {

  private static final long serialVersionUID = 1000000;

  /** The code for the user directory type. */
  @Schema(description = "The code for the user directory type", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  @Id
  @Column(name = "code", length = 100, nullable = false)
  private String code;

  /** The name of the user directory type. */
  @Schema(description = "The name of the user directory type", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  @Column(name = "name", length = 100, nullable = false)
  private String name;

  /** The Java class that implements the user directory type. */
  @JsonIgnore @Transient private transient Class userDirectoryClass;

  /** The fully qualified name of the Java class that implements the user directory type. */
  @Schema(
      description =
          "The fully qualified name of the Java class that implements the user directory type",
      required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 1000)
  @Column(name = "user_directory_class", length = 1000, nullable = false)
  private String userDirectoryClassName;

  /** Constructs a new <b>UserDirectoryType</b>. */
  public UserDirectoryType() {}

  /**
   * Constructs a new <b>UserDirectoryType</b>.
   *
   * @param code the code for the user directory type
   * @param name the name of the user directory type
   * @param userDirectoryClassName the fully qualified name of the Java class that implements the
   *     user directory type
   */
  UserDirectoryType(String code, String name, String userDirectoryClassName) {
    this.code = code;
    this.name = name;
    this.userDirectoryClassName = userDirectoryClassName;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param object the reference object with which to compare
   * @return <b>true</b> if this object is the same as the object argument otherwise <b> false</b>
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

    UserDirectoryType other = (UserDirectoryType) object;

    return Objects.equals(code, other.code);
  }

  /**
   * Returns the code for the user directory type.
   *
   * @return the code for the user directory type
   */
  public String getCode() {
    return code;
  }

  /**
   * Returns the name of the user directory type.
   *
   * @return the name of the user directory type
   */
  public String getName() {
    return name;
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for the object
   */
  @Override
  public int hashCode() {
    return (code == null) ? 0 : code.hashCode();
  }

  /**
   * Returns the fully qualified name of the Java class that implements the user directory type.
   *
   * @return the fully qualified name of the Java class that implements the user directory type
   */
  String getUserDirectoryClassName() {
    return userDirectoryClassName;
  }
}
