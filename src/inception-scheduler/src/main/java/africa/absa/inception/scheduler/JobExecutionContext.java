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

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * The <b>JobExecutionContext</b> class provides access to context information associated with the
 * execution of a job.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class JobExecutionContext {

  /** The date and time that the job was scheduled to be executed. */
  private final LocalDateTime executionDate;

  /** The parameters for the job. */
  private final Map<String, String> parameters;

  /**
   * Constructs a new <b>JobExecutionContext</b>.
   *
   * @param executionDate the date and time that the job was scheduled to be executed
   * @param parameters the parameters for the job
   */
  public JobExecutionContext(LocalDateTime executionDate, Map<String, String> parameters) {
    this.executionDate = executionDate;
    this.parameters = parameters;
  }

  /**
   * Returns the date and time that the job was scheduled to be executed.
   *
   * @return the date and time that the job was scheduled to be executed
   */
  public LocalDateTime getExecutionDate() {
    return executionDate;
  }

  /**
   * Returns the parameter with the specified name for the job.
   *
   * @param name the name of the parameter
   * @return an Optional containing the value of the parameter with the specified name or an empty
   *     Optional if the parameter cannot be found
   */
  public Optional<String> getParameter(String name) {
    return Optional.ofNullable(parameters.get(name));
  }
}
