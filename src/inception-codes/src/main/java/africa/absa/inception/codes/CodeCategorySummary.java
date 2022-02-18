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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * The <b>CodeCategorySummary</b> class holds the summary information for a code category.
 *
 * @author Marcus Portmann
 */
@Schema(description = "A code category summary")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "name", "lastModified"})
@Entity
@Table(schema = "codes", name = "code_categories")
public class CodeCategorySummary implements Serializable {

  private static final long serialVersionUID = 1000000;

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

  /** Constructs a new <b>CodeCategorySummary</b>. */
  public CodeCategorySummary() {}

  /**
   * Constructs a new <b>CodeCategorySummary</b>.
   *
   * @param id the ID for the code category
   * @param name the name of the code category
   * @param lastModified the date and time the code category was last modified
   */
  public CodeCategorySummary(String id, String name, LocalDateTime lastModified) {
    this.id = id;
    this.name = name;
    this.lastModified = lastModified;
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

    CodeCategorySummary other = (CodeCategorySummary) object;

    return Objects.equals(id, other.id);
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
}
