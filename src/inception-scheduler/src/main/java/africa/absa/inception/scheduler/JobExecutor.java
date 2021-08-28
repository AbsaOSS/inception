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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The <b>JobExecutorThread</b> class.
 *
 * @author Marcus Portmann
 */
public class JobExecutor implements Runnable {

  /* Logger */
  private static final Logger logger = LoggerFactory.getLogger(JobExecutor.class);

  private final Job job;

  private final ISchedulerService schedulerService;

  /**
   * Constructs a new <b>JobExecutorThread</b>.
   *
   * @param schedulerService the Scheduler Service
   * @param job the job
   */
  public JobExecutor(ISchedulerService schedulerService, Job job) {
    this.schedulerService = schedulerService;
    this.job = job;
  }

  @Override
  public void run() {
    try {
      if (logger.isDebugEnabled()) {
        logger.debug(String.format("Executing the job (%s)", job.getId()));
      }

      schedulerService.executeJob(job);

      // Reschedule the job
      try {
        schedulerService.rescheduleJob(job.getId(), job.getSchedulingPattern());

        try {
          schedulerService.unlockJob(job.getId(), JobStatus.SCHEDULED);
        } catch (Throwable f) {
          logger.error(
              String.format(
                  "Failed to unlock and set the status for the job (%s) to \"Scheduled\"",
                  job.getId()),
              f);
        }
      } catch (Throwable e) {
        logger.warn(
            String.format(
                "The job (%s) could not be rescheduled and will be marked as \"Failed\"",
                job.getId()));

        try {
          schedulerService.unlockJob(job.getId(), JobStatus.FAILED);
        } catch (Throwable f) {
          logger.error(
              String.format(
                  "Failed to unlock and set the status for the job (%s) to \"Failed\"",
                  job.getId()),
              f);
        }
      }
    } catch (Throwable e) {
      logger.error(String.format("Failed to execute the job (%s)", job.getId()), e);

      try {
        /*
         * If the job has exceeded the maximum number of execution attempts then
         * unlock it and set its status to "Failed" otherwise unlock it and set its status to
         * "Scheduled".
         */
        if (job.getExecutionAttempts() >= schedulerService.getMaximumJobExecutionAttempts()) {
          logger.warn(
              String.format(
                  "The job (%s) has exceeded the maximum  number of execution attempts and will be "
                      + "marked as \"Failed\"",
                  job.getId()));

          schedulerService.unlockJob(job.getId(), JobStatus.FAILED);
        } else {
          schedulerService.unlockJob(job.getId(), JobStatus.SCHEDULED);
        }
      } catch (Throwable f) {
        logger.error(
            String.format("Failed to unlock and set the status for the job (%s)", job.getId()), f);
      }
    }
  }
}
