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
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The <b>Function</b> class holds the information for a discrete unit of functionality for an
 * application that can be assigned to <b>Role</b>s.
 *
 * @author Marcus Portmann
 */
@Schema(
    description =
        "A discrete unit of functionality for an application that can be assigned to a role")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"code", "name", "description"})
@Entity
@Table(schema = "security", name = "functions")
@SuppressWarnings({"unused", "WeakerAccess"})
public class Function implements java.io.Serializable {

  private static final long serialVersionUID = 1000000;

  /** The code for the function. */
  @Schema(description = "The code for the function", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  @Id
  @Column(name = "code", length = 100, nullable = false)
  private String code;

  /** The description for the function. */
  @Schema(description = "The description for the function")
  @JsonProperty
  @Size(max = 100)
  @Column(name = "description", length = 100)
  private String description;

  /** The name of the function. */
  @Schema(description = "The name of the function", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  @Column(name = "name", length = 100, nullable = false)
  private String name;

  /** The roles the user is associated with. */
  @JsonIgnore
  @ManyToMany(mappedBy = "functions")
  private Set<Role> roles = new HashSet<>();

  /** Constructs a new <b>Function</b>. */
  public Function() {}

  /**
   * Constructs a new <b>Function</b>.
   *
   * @param code the code for the function
   * @param name the name of the function
   * @param description the description for the function
   */
  public Function(String code, String name, String description) {
    this.code = code;
    this.name = name;
    this.description = description;
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

    Function other = (Function) object;

    return Objects.equals(code, other.code);
  }

  /**
   * Returns the code for the function.
   *
   * @return the code for the function
   */
  public String getCode() {
    return code;
  }

  /**
   * Returns the description for the function.
   *
   * @return the description for the function
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the name of the function.
   *
   * @return the name of the function
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the roles the user is associated with.
   *
   * @return the roles the user is associated with
   */
  public Set<Role> getRoles() {
    return roles;
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
   * Set the code for the function.
   *
   * @param code the code for the function
   */
  public void setCode(String code) {
    this.code = code;
  }

  /**
   * Set the description for the function.
   *
   * @param description the description for the function
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Set the name of the function.
   *
   * @param name the name of the function
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Set the roles the user is associated with.
   *
   * @param roles the roles the user is associated with
   */
  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }
}
