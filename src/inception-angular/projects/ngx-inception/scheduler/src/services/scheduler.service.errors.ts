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

import {Error, HttpError, ProblemDetails} from '@absaoss/ngx-inception/core';
import {HttpErrorResponse} from '@angular/common/http';

/**
 * The DuplicateJobError class holds the information for a duplicate job error.
 *
 * @author Marcus Portmann
 */
export class DuplicateJobError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/scheduler/duplicate-job';

  /**
   * Constructs a new DuplicateJobError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@scheduler_duplicate_job_error:The job already exists.`, cause);
  }
}

/**
 * The JobNotFoundError class holds the information for a job not found error.
 *
 * @author Marcus Portmann
 */
export class JobNotFoundError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/scheduler/job-not-found';

  /**
   * Constructs a new JobNotFoundError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@scheduler_job_not_found_error:The job could not be found.`, cause);
  }
}

