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

package africa.absa.inception.scheduler;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The <b>JobParameter</b> class holds the information for a job parameter.
 *
 * @author Marcus Portmann
 */
@Schema(description = "A parameter for a scheduled job")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"name", "value"})
@Entity
@Table(schema = "scheduler", name = "job_parameters")
@IdClass(JobParameterId.class)
@SuppressWarnings({"unused"})
public class JobParameter implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The date and time the job parameter was created. */
  @JsonIgnore
  @Column(name = "created", nullable = false, updatable = false)
  private LocalDateTime created;

  /** The job the job parameter is associated with. */
  @JsonBackReference
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_id")
  private Job job;

  /** The name of the job parameter. */
  @Schema(description = "The name of the job parameter", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  @Id
  @Column(name = "name", length = 100, nullable = false)
  private String name;

  /** The date and time the job parameter was last updated. */
  @JsonIgnore
  @Column(name = "updated", insertable = false)
  private LocalDateTime updated;

  /** The value of the job parameter. */
  @Schema(description = "The value of the job parameter", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(max = 4000)
  @Column(name = "value", length = 4000, nullable = false)
  private String value;

  /** Constructs a new <b>JobParameter</b>. */
  public JobParameter() {}

  /**
   * Constructs a new <b>JobParameter</b>.
   *
   * @param name the name of the job parameter
   * @param value the value of the job parameter
   */
  public JobParameter(String name, String value) {
    this.name = name;
    this.value = value;
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

    JobParameter other = (JobParameter) object;

    return Objects.equals(job, other.job) && Objects.equals(name, other.name);
  }

  /**
   * Returns the date and time the job parameter was created.
   *
   * @return the date and time the job parameter was created
   */
  public LocalDateTime getCreated() {
    return created;
  }

  /**
   * Returns the job the job parameter is associated with.
   *
   * @return the job the job parameter is associated with
   */
  public Job getJob() {
    return job;
  }

  /**
   * Returns the name of the job parameter.
   *
   * @return the name of the job parameter
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the date and time the job parameter was last updated.
   *
   * @return the date and time the job parameter was last updated
   */
  public LocalDateTime getUpdated() {
    return updated;
  }

  /**
   * Returns the value of the job parameter.
   *
   * @return the value of the job parameter
   */
  public String getValue() {
    return value;
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for the object
   */
  @Override
  public int hashCode() {
    return (((job == null) || (job.getId() == null)) ? 0 : job.getId().hashCode())
        + ((name == null) ? 0 : name.hashCode());
  }

  /**
   * Set the job the job parameter is associated with.
   *
   * @param job the job the job parameter is associated with
   */
  public void setJob(Job job) {
    this.job = job;
  }

  /**
   * Set the name of the job parameter.
   *
   * @param name the name of the job parameter
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Set the value of the job parameter.
   *
   * @param value the value of the job parameter
   */
  public void setValue(String value) {
    this.value = value;
  }

  /** The Java Persistence callback method invoked before the entity is created in the database. */
  @PrePersist
  protected void onCreate() {
    created = LocalDateTime.now();
  }

  /** The Java Persistence callback method invoked before the entity is updated in the database. */
  @PreUpdate
  protected void onUpdate() {
    updated = LocalDateTime.now();
  }
}
