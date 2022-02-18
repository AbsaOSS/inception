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

package africa.absa.inception.codes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The <b>Code</b> class holds the information for a code.
 *
 * @author Marcus Portmann
 */
@Schema(
    description =
        "Reference data in the form of a key-value pair that is used to classify or categorize other data")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "codeCategoryId", "name", "value"})
@Entity
@Table(schema = "codes", name = "codes")
@IdClass(CodeId.class)
public class Code implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The ID for the code category the code is associated with. */
  @Schema(description = "The ID for the code category the code is associated with", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  @Id
  @Column(name = "code_category_id", length = 100, nullable = false)
  private String codeCategoryId;

  /** The ID for the code. */
  @Schema(description = "The ID for the code", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  @Id
  @Column(name = "id", length = 100, nullable = false)
  private String id;

  /** The name of the code. */
  @Schema(description = "The name of the code", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  @Column(name = "name", length = 100, nullable = false)
  private String name;

  /** The value for the code. */
  @Schema(description = "The value for the code", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(max = 4000)
  @Column(name = "value", length = 4000, nullable = false)
  private String value;

  /** Constructs a new <b>Code</b>. */
  public Code() {}

  /**
   * Constructs a new <b>Code</b>.
   *
   * @param codeCategoryId the ID for the code category the code is associated with
   */
  public Code(String codeCategoryId) {
    this.codeCategoryId = codeCategoryId;
  }

  /**
   * Constructs a new <b>Code</b>.
   *
   * @param id the ID for the code
   * @param codeCategoryId the ID for the code category the code is associated with
   * @param name the name of the code
   * @param value the value for the code
   */
  public Code(String id, String codeCategoryId, String name, String value) {
    this.id = id;
    this.codeCategoryId = codeCategoryId;
    this.name = name;
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

    Code other = (Code) object;

    return Objects.equals(codeCategoryId, other.codeCategoryId) && Objects.equals(id, other.id);
  }

  /**
   * Returns the ID for the code category the code is associated with.
   *
   * @return the ID for the code category the code is associated with
   */
  public String getCodeCategoryId() {
    return codeCategoryId;
  }

  /**
   * Returns the ID for the code.
   *
   * @return the ID for the code
   */
  public String getId() {
    return id;
  }

  /**
   * Returns the name of the code.
   *
   * @return the name of the code
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the value for the code.
   *
   * @return the value for the code
   */
  public String getValue() {
    return this.value;
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for the object
   */
  @Override
  public int hashCode() {
    return ((codeCategoryId == null) ? 0 : codeCategoryId.hashCode())
        + ((id == null) ? 0 : id.hashCode());
  }

  /**
   * Set the ID for the code category the code is associated with.
   *
   * @param codeCategoryId the ID for the code category the code is associated with
   */
  public void setCodeCategoryId(String codeCategoryId) {
    this.codeCategoryId = codeCategoryId;
  }

  /**
   * Set the ID for the code.
   *
   * @param id the ID for the code
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Set the name of the code.
   *
   * @param name the name of the code
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Set the value for the code.
   *
   * @param value the value for the code
   */
  public void setValue(String value) {
    this.value = value;
  }
}
