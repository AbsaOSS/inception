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

package africa.absa.inception.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The <b>ErrorReportSummary</b> class holds the summary information for an error report.
 *
 * @author Marcus Portmann
 */
@Schema(description = "An error report summary")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "id",
  "applicationId",
  "applicationVersion",
  "description",
  "created",
  "who",
  "deviceId"
})
@Entity
@Table(schema = "error", name = "error_reports")
@SuppressWarnings({"unused", "WeakerAccess"})
public class ErrorReportSummary implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The ID for the application that generated the error report. */
  @Schema(description = "ID for the application that generated the error report", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 200)
  @Column(name = "application_id", length = 200, nullable = false)
  private String applicationId;

  /** The version of the application that generated the error report. */
  @Schema(
      description = "The version of the application that generated the error report",
      required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(max = 50)
  @Column(name = "application_version", nullable = false)
  private String applicationVersion;

  /** The date and time the error report was created. */
  @Schema(description = "The date and time the error report was created", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Column(name = "created", nullable = false)
  private LocalDateTime created;

  /** The description of the error. */
  @Schema(description = "The description of the error", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(max = 4000)
  @Column(name = "description", length = 4000, nullable = false)
  private String description;

  /** The optional ID for the device the error report originated from. */
  @Schema(description = "The optional ID for the device the error report originated from")
  @JsonProperty
  @Size(max = 50)
  @Column(name = "device_id")
  private UUID deviceId;

  /** The ID for the error report. */
  @Schema(description = "The ID for the error report", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  /** The optional username for the user associated with the error report. */
  @Schema(description = "The optional username for the user associated with the error report")
  @JsonProperty
  @Size(max = 100)
  @Column(name = "who", length = 100)
  private String who;

  /** Constructs a new <b>ErrorReportSummary</b>. */
  public ErrorReportSummary() {}

  /**
   * Constructs a new <b>ErrorReportSummary</b>.
   *
   * @param id the ID for the error report
   * @param applicationId the ID for the application that generated the error report
   * @param applicationVersion the version of the application that generated the error report
   * @param description the description of the error
   * @param created the date and time the error report was created
   * @param who the optional username for the user associated with the error report
   * @param deviceId the optional ID for the device the error report originated from
   */
  public ErrorReportSummary(
      UUID id,
      String applicationId,
      String applicationVersion,
      String description,
      LocalDateTime created,
      String who,
      UUID deviceId) {
    this.id = id;
    this.applicationId = applicationId;
    this.applicationVersion = applicationVersion;
    this.description = description;
    this.created = created;
    this.who = who;
    this.deviceId = deviceId;
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

    ErrorReportSummary other = (ErrorReportSummary) object;

    return Objects.equals(id, other.id);
  }

  /**
   * Returns the ID for the application that generated the error report.
   *
   * @return the ID for the application that generated the error report
   */
  public String getApplicationId() {
    return applicationId;
  }

  /**
   * Returns the version of the application that generated the error report.
   *
   * @return the version of the application that generated the error report
   */
  public String getApplicationVersion() {
    return applicationVersion;
  }

  /**
   * Returns the date and time the error report was created.
   *
   * @return the date and time the error report was created
   */
  public LocalDateTime getCreated() {
    return created;
  }

  /**
   * Returns the description of the error.
   *
   * @return the description of the error
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the optional ID for the device the error report originated from.
   *
   * @return the optional ID for the device the error report originated from
   */
  public UUID getDeviceId() {
    return deviceId;
  }

  /**
   * Returns the ID for the error report.
   *
   * @return the ID for the error report
   */
  public UUID getId() {
    return id;
  }

  /**
   * Returns the username for the user associated with the error report.
   *
   * @return the username for the user associated with the error report
   */
  public String getWho() {
    return who;
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
