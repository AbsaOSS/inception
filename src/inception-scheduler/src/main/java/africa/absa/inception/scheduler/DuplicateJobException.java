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

import africa.absa.inception.core.service.Problem;
import africa.absa.inception.core.service.ServiceException;

/**
 * A <b>DuplicateJobException</b> is thrown to indicate that a scheduler operation failed as a
 * result of a duplicate job.
 *
 * <p>This is a checked exception to prevent the automatic rollback of the current transaction.
 *
 * @author Marcus Portmann
 */
@Problem(
    type = "http://inception.absa.africa/problems/scheduler/duplicate-job",
    title = "A job with the specified ID already exists.",
    status = 409)
public class DuplicateJobException extends ServiceException {

  private static final long serialVersionUID = 1000000;

  /**
   * Constructs a new <b>DuplicateJobException</b>.
   *
   * @param jobId the ID for the job
   */
  public DuplicateJobException(String jobId) {
    super("A job with the ID (" + jobId + ") already exists");
  }
}
