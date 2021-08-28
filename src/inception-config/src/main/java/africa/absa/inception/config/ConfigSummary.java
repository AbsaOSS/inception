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

package africa.absa.inception.config;

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
 * The <b>Config</b> class stores the summary for a config.
 *
 * @author Marcus Portmann
 */
@Schema(description = "A config summary")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"key", "description"})
@Entity
@Table(schema = "config", name = "config")
public class ConfigSummary implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The description for the config. */
  @Schema(description = "The description for the config", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(max = 100)
  @Column(name = "description", length = 100, nullable = false)
  private String description;

  /** The key for the config. */
  @Schema(description = "The key for the config", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  @Id
  @Column(name = "key", length = 100, nullable = false)
  private String key;

  /** Constructs a new <b>ConfigSummary</b>. */
  public ConfigSummary() {}

  /**
   * Constructs a new <b>ConfigSummary</b>.
   *
   * @param key the key for the config
   * @param description the description for the config
   */
  ConfigSummary(String key, String description) {
    this.key = key;
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

    ConfigSummary other = (ConfigSummary) object;

    return Objects.equals(key, other.key);
  }

  /**
   * Returns the description for the config.
   *
   * @return the description for the config
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the key for the config.
   *
   * @return the key for the config
   */
  public String getKey() {
    return key;
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for the object
   */
  @Override
  public int hashCode() {
    return (key == null) ? 0 : key.hashCode();
  }
}
