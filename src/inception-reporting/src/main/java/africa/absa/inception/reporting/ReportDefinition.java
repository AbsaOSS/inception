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

package africa.absa.inception.reporting;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The <b>ReportDefinition</b> class holds the information for a report definition.
 *
 * @author Marcus Portmann
 */
@Schema(description = "A report definition")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "name", "template"})
@Entity
@Table(schema = "reporting", name = "report_definitions")
@SuppressWarnings({"unused", "WeakerAccess"})
public class ReportDefinition implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The ID for the report definition. */
  @Schema(description = "The ID for the report definition", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  @Id
  @Column(name = "id", length = 100, nullable = false)
  private String id;

  /** The name of the report definition. */
  @Schema(description = "The name of the report definition", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  @Column(name = "name", length = 100, nullable = false)
  private String name;

  /** The JasperReports template for the report definition. */
  @Schema(description = "The JasperReports template for the report definition", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 10485760)
  @Column(name = "template", nullable = false)
  private byte[] template;

  /** Constructs a new <b>ReportDefinition</b>. */
  public ReportDefinition() {}

  /**
   * Constructs a new <b>ReportDefinition</b>.
   *
   * @param id the ID for the report definition
   * @param name the name of the report definition
   * @param template the JasperReports template for the report definition
   */
  public ReportDefinition(String id, String name, byte[] template) {
    this.id = id;
    this.name = name;
    this.template = template;
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

    ReportDefinition other = (ReportDefinition) object;

    return Objects.equals(id, other.id);
  }

  /**
   * Returns the ID for the report definition.
   *
   * @return the ID for the report definition
   */
  public String getId() {
    return id;
  }

  /**
   * Returns the name of the report definition.
   *
   * @return the name of the report definition
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the JasperReports template for the report definition.
   *
   * @return the JasperReports template for the report definition
   */
  public byte[] getTemplate() {
    return template;
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
   * Set the ID for the report definition.
   *
   * @param id the ID for the report definition
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Set the name of the report definition.
   *
   * @param name the name of the report definition
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Set the JasperReports template for the report definition.
   *
   * @param template the JasperReports template for the report definition
   */
  public void setTemplate(byte[] template) {
    this.template = template;
  }

  /**
   * Returns a string representation of the object.
   *
   * @return a string representation of the object
   */
  @Override
  public String toString() {
    return "ReportDefinition {id=\"" + getId() + "\", name=\"" + getName() + "\"}";
  }
}
