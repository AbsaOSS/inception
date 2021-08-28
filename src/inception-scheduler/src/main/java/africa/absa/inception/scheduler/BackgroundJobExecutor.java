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

import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * The <b>BackgroundJobExecutor</b> class implements the Background Job Executor.
 *
 * @author Marcus Portmann
 */
@Service
@SuppressWarnings("unused")
public class BackgroundJobExecutor implements InitializingBean {

  /** The default number of minutes an idle processing thread should be kept alive. */
  private static final int DEFAULT_IDLE_PROCESSING_THREADS_KEEP_ALIVE_TIME = 5;

  /** The default number of threads to start initially to process jobs. */
  private static final int DEFAULT_INITIAL_PROCESSING_THREADS = 1;

  /**
   * The default maximum number of jobs to queue for processing if no processing threads are
   * available.
   */
  private static final int DEFAULT_MAXIMUM_PROCESSING_QUEUE_LENGTH = 100;

  /** The default maximum number of threads to create to process jobs. */
  private static final int DEFAULT_MAXIMUM_PROCESSING_THREADS = 10;

  /* Logger */
  private static final Logger logger = LoggerFactory.getLogger(BackgroundJobExecutor.class);

  /** The Scheduler Service. */
  private final ISchedulerService schedulerService;

  /** The executor responsible for processing jobs. */
  private Executor jobProcessor;

  /**
   * Constructs a new <b>BackgroundJobExecutor</b>.
   *
   * @param schedulerService the Scheduler Service
   */
  public BackgroundJobExecutor(ISchedulerService schedulerService) {
    this.schedulerService = schedulerService;
  }

  /** Initialize the Background Job Executor. */
  @Override
  public void afterPropertiesSet() {
    logger.info("Initializing the Background Job Executor");

    if (schedulerService != null) {
      // Initialize the job processor
      this.jobProcessor =
          new ThreadPoolExecutor(
              DEFAULT_INITIAL_PROCESSING_THREADS,
              DEFAULT_MAXIMUM_PROCESSING_THREADS,
              DEFAULT_IDLE_PROCESSING_THREADS_KEEP_ALIVE_TIME,
              TimeUnit.MINUTES,
              new LinkedBlockingQueue<>(DEFAULT_MAXIMUM_PROCESSING_QUEUE_LENGTH));

      // Reset any locks for jobs that were previously being executed
      try {
        logger.info("Resetting the locks for the jobs being executed");

        schedulerService.resetJobLocks(JobStatus.EXECUTING, JobStatus.SCHEDULED);
      } catch (Throwable e) {
        logger.error("Failed to reset the locks for the jobs being executed", e);
      }

      // Schedule any unscheduled jobs
      try {
        while (schedulerService.scheduleNextUnscheduledJobForExecution()) {}
      } catch (Throwable e) {
        logger.error("Failed to schedule the unscheduled jobs for execution");
      }
    } else {
      logger.error(
          "Failed to initialize the Background Job Executor: "
              + "The Scheduler Service was NOT injected");
    }
  }

  /** Execute the jobs. */
  @SuppressWarnings("StatementWithEmptyBody")
  @Scheduled(cron = "0 * * * * *")
  public void executeJobs() {
    Optional<Job> jobOptional;

    if (schedulerService == null) {
      return;
    }

    while (true) {
      // Retrieve the next job scheduled for execution
      try {
        jobOptional = schedulerService.getNextJobScheduledForExecution();

        if (jobOptional.isEmpty()) {
          if (logger.isDebugEnabled()) {
            logger.debug("No jobs scheduled for execution");
          }

          // Schedule any unscheduled jobs
          while (schedulerService.scheduleNextUnscheduledJobForExecution()) {}

          return;
        }
      } catch (Throwable e) {
        logger.error("Failed to retrieve the next job scheduled for execution", e);

        return;
      }

      jobProcessor.execute(new JobExecutor(schedulerService, jobOptional.get()));
    }
  }
}
