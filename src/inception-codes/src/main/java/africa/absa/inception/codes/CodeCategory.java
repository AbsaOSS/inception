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

import africa.absa.inception.core.xml.LocalDateTimeAdapter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * The <b>CodeCategory</b> class holds the information for a code category.
 *
 * @author Marcus Portmann
 */
@Schema(description = "A collection of related codes")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "name", "data", "lastModified"})
@Entity
@Table(schema = "codes", name = "code_categories")
public class CodeCategory implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The optional code data for the code category. */
  @Schema(description = "The optional code data for the code category")
  @JsonProperty
  @Column(name = "data")
  private String data;

  /** The ID for the code category. */
  @Schema(description = "The ID for the code category", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  @Id
  @Column(name = "id", length = 100, nullable = false)
  private String id;

  /** The date and time the code category was last modified. */
  @Schema(description = "The date and time the code category was last modified")
  @JsonProperty
  @XmlElement(name = "LastModified")
  @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
  @XmlSchemaType(name = "dateTime")
  @Column(name = "last_modified")
  private LocalDateTime lastModified;

  /** The name of the code category. */
  @Schema(description = "The name of the code category", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  @Column(name = "name", length = 100, nullable = false)
  private String name;

  /** Constructs a new <b>CodeCategory</b>. */
  public CodeCategory() {}

  /**
   * Constructs a new <b>CodeCategory</b>.
   *
   * @param id the ID for the code category
   * @param name the name of the code category
   */
  public CodeCategory(String id, String name) {
    this.id = id;
    this.name = name;
  }

  /**
   * Constructs a new <b>CodeCategory</b>.
   *
   * @param id the ID for the code category
   * @param name the name of the code category
   * @param data the optional code data for the code category
   */
  public CodeCategory(String id, String name, String data) {
    this.id = id;
    this.name = name;
    this.data = data;
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

    CodeCategory other = (CodeCategory) object;

    return Objects.equals(id, other.id);
  }

  /**
   * Returns the optional code data for the code category.
   *
   * @return the optional code data for the code category
   */
  public String getData() {
    return data;
  }

  /**
   * Returns the ID for the code category.
   *
   * @return the ID for the code category
   */
  public String getId() {
    return id;
  }

  /**
   * Returns the date and time the code category was last modified.
   *
   * @return the date and time the code category was last modified
   */
  public LocalDateTime getLastModified() {
    return lastModified;
  }

  /**
   * Returns the name of the code category.
   *
   * @return the name of the code category
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
    return (id == null) ? 0 : id.hashCode();
  }

  /**
   * Set the code data for the code category.
   *
   * @param data the code data for the code category
   */
  public void setData(String data) {
    this.data = data;
  }

  /**
   * Set the ID for the code category.
   *
   * @param id the ID for the code category
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Set the name of the code category.
   *
   * @param name the name of the code category
   */
  public void setName(String name) {
    this.name = name;
  }

  /** The Java Persistence callback method invoked before the entity is created in the database. */
  @PrePersist
  protected void onCreate() {
    lastModified = LocalDateTime.now();
  }

  /** The Java Persistence callback method invoked before the entity is updated in the database. */
  @PreUpdate
  protected void onUpdate() {
    lastModified = LocalDateTime.now();
  }
}
