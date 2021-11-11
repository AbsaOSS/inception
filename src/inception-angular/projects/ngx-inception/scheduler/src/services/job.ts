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

import {JobParameter} from './job-parameter';
import {JobStatus} from './job-status';

/**
 * The Job class holds the information for a job.
 *
 * @author Marcus Portmann
 */
export class Job {

  /**
   * Is the job enabled for execution?
   */
  enabled: boolean;

  /**
   * The number of times the current execution of the job has been attempted.
   */
  executionAttempts: number | null = null;

  /**
   * The ID for the job.
   */
  id: string;

  /**
   * The fully qualified name of the Java class that implements the job.
   */
  jobClass: string;

  /**
   * The date and time the job was last executed.
   */
  lastExecuted: Date | null = null;

  /**
   * The name of the entity that has locked the job for execution.
   */
  lockName: string | null = null;

  /**
   * The name of the job.
   */
  name: string;

  /**
   * The date and time when the job will next be executed.
   */
  nextExecution: Date | null = null;

  /**
   * The parameters for the job.
   */
  parameters: JobParameter[];

  /**
   * The cron-style scheduling pattern for the job.
   */
  schedulingPattern: string;

  /**
   * The status of the job.
   */
  status: JobStatus;

  /**
   * Constructs a new Job.
   *
   * @param id                The ID for the job.
   * @param name              The name of the job.
   * @param schedulingPattern The cron-style scheduling pattern for the job.
   * @param jobClass          The fully qualified name of the Java class that implements the job.
   * @param enabled           Is the job enabled for execution?
   * @param parameters        The parameters for the job.
   */
  constructor(id: string, name: string, schedulingPattern: string, jobClass: string, enabled: boolean,
              parameters: JobParameter[]) {
    this.id = id;
    this.name = name;
    this.schedulingPattern = schedulingPattern;
    this.jobClass = jobClass;
    this.enabled = enabled;
    this.status = JobStatus.Unscheduled;
    this.parameters = parameters;
  }
}
