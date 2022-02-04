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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The enumeration giving the possible statuses for a job.
 *
 * @author Marcus Portmann
 */
@Schema(description = "The job status")
public enum JobStatus {
  /** Unscheduled. */
  UNSCHEDULED("unscheduled", "Unscheduled"),

  /** Scheduled. */
  SCHEDULED("scheduled", "Scheduled"),

  /** Executing. */
  EXECUTING("executing", "Executing"),

  /** Executed. */
  EXECUTED("executed", "Executed"),

  /** Aborted. */
  ABORTED("aborted", "Aborted"),

  /** Failed. */
  FAILED("failed", "Failed"),

  /** Once-off. */
  ONCE_OFF("once_off", "Once-Off");

  private final String code;

  private final String description;

  JobStatus(String code, String description) {
    this.code = code;
    this.description = description;
  }

  /**
   * Returns the job status given by the specified code value.
   *
   * @param code the code for the job status
   * @return the job status given by the specified code value
   */
  @JsonCreator
  public static JobStatus fromCode(String code) {
    switch (code) {
      case "unscheduled":
        return JobStatus.UNSCHEDULED;
      case "scheduled":
        return JobStatus.SCHEDULED;
      case "executing":
        return JobStatus.EXECUTING;
      case "executed":
        return JobStatus.EXECUTED;
      case "aborted":
        return JobStatus.ABORTED;
      case "failed":
        return JobStatus.FAILED;
      case "once_off":
        return JobStatus.ONCE_OFF;
      default:
        throw new RuntimeException(
            "Failed to determine the job status with the invalid code (" + code + ")");
    }
  }

  /**
   * Returns the job status for the specified numeric code.
   *
   * @param numericCode the numeric code for the job status
   * @return the job status given by the specified numeric code value
   */
  public static JobStatus fromNumericCode(int numericCode) {
    switch (numericCode) {
      case 1:
        return JobStatus.UNSCHEDULED;
      case 2:
        return JobStatus.SCHEDULED;
      case 3:
        return JobStatus.EXECUTING;
      case 4:
        return JobStatus.EXECUTED;
      case 5:
        return JobStatus.ABORTED;
      case 6:
        return JobStatus.FAILED;
      case 7:
        return JobStatus.ONCE_OFF;
      default:
        throw new RuntimeException(
            "Failed to determine the job status for the numeric code (" + numericCode + ")");
    }
  }

  /**
   * Returns the numeric code for the job status.
   *
   * @param jobStatus the job status
   * @return the numeric code for the job status
   */
  public static int toNumericCode(JobStatus jobStatus) {
    switch (jobStatus) {
      case UNSCHEDULED:
        return 1;
      case SCHEDULED:
        return 2;
      case EXECUTING:
        return 3;
      case EXECUTED:
        return 4;
      case ABORTED:
        return 5;
      case FAILED:
        return 6;
      case ONCE_OFF:
        return 7;
      default:
        throw new RuntimeException(
            "Failed to determine the numeric code for the job status (" + jobStatus.code() + ")");
    }
  }

  /**
   * Returns the code for the job status.
   *
   * @return the code for the job status
   */
  @JsonValue
  public String code() {
    return code;
  }

  /**
   * Returns the description for the job status.
   *
   * @return the description for the job status
   */
  public String description() {
    return description;
  }

  /**
   * Return the string representation of the job status enumeration value.
   *
   * @return the string representation of the job status enumeration value
   */
  public String toString() {
    return description;
  }
}
