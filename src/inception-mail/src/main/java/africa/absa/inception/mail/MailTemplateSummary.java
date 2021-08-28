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

package africa.absa.inception.mail;

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
 * The <b>MailTemplateSummary</b> class holds the information for a mail template.
 *
 * @author Marcus Portmann
 */
@Schema(description = "A mail template summary")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "name", "contentType"})
@Entity
@Table(schema = "mail", name = "mail_templates")
@SuppressWarnings({"unused"})
public class MailTemplateSummary implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The content type for the mail template. */
  @Schema(description = "The content type for the mail template", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Column(name = "content_type", nullable = false)
  private MailTemplateContentType contentType;

  /** The ID for the mail template. */
  @Schema(description = "The ID for the mail template", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  @Id
  @Column(name = "id", length = 100, nullable = false)
  private String id;

  /** The name of the mail template. */
  @Schema(description = "The name of the mail template", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  @Column(name = "name", length = 100, nullable = false)
  private String name;

  /** Constructs a new <b>MailTemplateSummary</b>. */
  public MailTemplateSummary() {}

  /**
   * Constructs a new <b>MailTemplateSummary</b>.
   *
   * @param id the ID for the mail template
   * @param name the name of the mail template
   * @param contentType the content type for the mail template
   */
  public MailTemplateSummary(String id, String name, MailTemplateContentType contentType) {
    this.id = id;
    this.name = name;
    this.contentType = contentType;
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

    MailTemplateSummary other = (MailTemplateSummary) object;

    return Objects.equals(id, other.id);
  }

  /**
   * Returns the content type for the mail template.
   *
   * @return the content type for the mail template
   */
  public MailTemplateContentType getContentType() {
    return contentType;
  }

  /**
   * Returns the ID for the mail template.
   *
   * @return the ID for the mail template
   */
  public String getId() {
    return id;
  }

  /**
   * Returns the name of the mail template.
   *
   * @return the name of the mail template
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
   * Returns a string representation of the object.
   *
   * @return a string representation of the object
   */
  @Override
  public String toString() {
    return "MailTemplateSummary {id=\""
        + getId()
        + "\", name=\""
        + getName()
        + "\", contentType=\""
        + getContentType()
        + "\"}";
  }
}
