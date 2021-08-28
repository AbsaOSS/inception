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

import africa.absa.inception.core.service.InvalidArgumentException;
import africa.absa.inception.core.service.ServiceUnavailableException;
import java.util.List;
import java.util.Optional;

/**
 * The <b>ISchedulerService</b> interface defines the functionality provided by a Scheduler Service
 * implementation.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings("unused")
public interface ISchedulerService {

  /**
   * Create the new job.
   *
   * @param job the <b>Job</b> instance containing the information for the job
   */
  void createJob(Job job)
      throws InvalidArgumentException, DuplicateJobException, ServiceUnavailableException;

  /**
   * Delete the job
   *
   * @param jobId the ID for the job
   */
  void deleteJob(String jobId)
      throws InvalidArgumentException, JobNotFoundException, ServiceUnavailableException;

  /**
   * Execute the job.
   *
   * @param job the job
   */
  void executeJob(Job job) throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Retrieve the filtered jobs.
   *
   * @param filter the filter to apply to the jobs
   * @return the jobs
   */
  List<Job> getFilteredJobs(String filter) throws ServiceUnavailableException;

  /**
   * Retrieve the job.
   *
   * @param jobId the ID for the job
   * @return the job
   */
  Job getJob(String jobId)
      throws InvalidArgumentException, JobNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the name of the job.
   *
   * @param jobId the ID for the job
   * @return the name of the job
   */
  String getJobName(String jobId)
      throws InvalidArgumentException, JobNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the jobs.
   *
   * @return the jobs
   */
  List<Job> getJobs() throws ServiceUnavailableException;

  /**
   * Returns the maximum number of times execution will be attempted for a job.
   *
   * @return the maximum number of times execution will be attempted for a job
   */
  int getMaximumJobExecutionAttempts();

  /**
   * Retrieve the next job that is scheduled for execution.
   *
   * <p>The job will be locked to prevent duplicate processing.
   *
   * @return an Optional containing the next job that is scheduled for execution or an empty
   *     Optional if no jobs are currently scheduled for execution
   */
  Optional<Job> getNextJobScheduledForExecution() throws ServiceUnavailableException;

  /**
   * Retrieve the unscheduled jobs.
   *
   * @return the unscheduled jobs
   */
  List<Job> getUnscheduledJobs() throws ServiceUnavailableException;

  /**
   * Reschedule the job for execution.
   *
   * @param jobId the ID for the job
   * @param schedulingPattern the cron-style scheduling pattern for the job used to determine the
   *     next execution time
   */
  void rescheduleJob(String jobId, String schedulingPattern)
      throws InvalidArgumentException, JobNotFoundException, ServiceUnavailableException;

  /**
   * Reset the job locks.
   *
   * @param status the current status of the jobs that have been locked
   * @param newStatus the new status for the jobs that have been unlocked
   */
  void resetJobLocks(JobStatus status, JobStatus newStatus) throws ServiceUnavailableException;

  /**
   * Schedule the next unscheduled job for execution.
   *
   * @return <b>true</b> if a job was successfully scheduled for execution or <b>false</b> otherwise
   */
  boolean scheduleNextUnscheduledJobForExecution() throws ServiceUnavailableException;

  /**
   * Set the status for the job.
   *
   * @param jobId the ID for the job
   * @param status the new status for the job
   */
  void setJobStatus(String jobId, JobStatus status)
      throws InvalidArgumentException, JobNotFoundException, ServiceUnavailableException;

  /**
   * Unlock a locked job.
   *
   * @param jobId the ID for the job
   * @param status the new status for the unlocked job
   */
  void unlockJob(String jobId, JobStatus status)
      throws InvalidArgumentException, JobNotFoundException, ServiceUnavailableException;

  /**
   * Update the job.
   *
   * @param job the <b>Job</b> instance containing the updated information for the job
   */
  void updateJob(Job job)
      throws InvalidArgumentException, JobNotFoundException, ServiceUnavailableException;
}
