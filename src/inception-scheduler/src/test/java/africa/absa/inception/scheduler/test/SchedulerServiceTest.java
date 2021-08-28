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

package africa.absa.inception.scheduler.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import africa.absa.inception.scheduler.ISchedulerService;
import africa.absa.inception.scheduler.Job;
import africa.absa.inception.scheduler.JobNotFoundException;
import africa.absa.inception.scheduler.JobParameter;
import africa.absa.inception.scheduler.JobStatus;
import africa.absa.inception.test.InceptionExtension;
import africa.absa.inception.test.TestConfiguration;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

/**
 * The <b>SchedulerServiceTest</b> class contains the implementation of the JUnit tests for the
 * Scheduler Service.
 *
 * @author Marcus Portmann
 */
@ExtendWith(SpringExtension.class)
@ExtendWith(InceptionExtension.class)
@ContextConfiguration(
    classes = {TestConfiguration.class},
    initializers = {ConfigDataApplicationContextInitializer.class})
@TestExecutionListeners(
    listeners = {
      DependencyInjectionTestExecutionListener.class,
      DirtiesContextTestExecutionListener.class,
      TransactionalTestExecutionListener.class
    })
public class SchedulerServiceTest {

  private static int jobCount;

  /** The Scheduler Service. */
  @Autowired private ISchedulerService schedulerService;

  private static synchronized Job getTestJobDetails() {
    jobCount++;

    Job job = new Job();
    job.setId("TestJob" + jobCount);
    job.setName("Test Job Name " + jobCount);
    job.setSchedulingPattern("5 * * * *");
    job.setJobClass("africa.absa.inception.scheduler.TestJob");
    job.setEnabled(true);
    job.setStatus(JobStatus.UNSCHEDULED);

    for (int i = 1; i <= 10; i++) {
      JobParameter parameter =
          new JobParameter("Job Parameter Name " + i, "Job Parameter Value " + i);

      job.addParameter(parameter);
    }

    return job;
  }

  /** Test the execute job functionality. */
  @Test
  public void executeJobTest() throws Exception {
    Job job = getTestJobDetails();

    schedulerService.executeJob(job);
  }

  /** Test the job parameters functionality. */
  @Test
  public void jobParametersTest() throws Exception {
    Job job = getTestJobDetails();
    job.setEnabled(false);

    for (int i = 0; i < 10; i++) {
      job.addParameter(
          new JobParameter(job.getName() + " Parameter " + i, job.getName() + " Value " + i));
    }

    schedulerService.createJob(job);

    Job retrievedJob = schedulerService.getJob(job.getId());

    compareJobs(job, retrievedJob);
  }

  /** Test the job functionality. */
  @Test
  public void jobTest() throws Exception {
    Job disabledJob = getTestJobDetails();
    disabledJob.setEnabled(false);

    schedulerService.createJob(disabledJob);

    List<Job> unscheduledJobs = schedulerService.getUnscheduledJobs();

    for (Job unscheduledJob : unscheduledJobs) {
      if (unscheduledJob.getId().equals(disabledJob.getId())) {
        fail(
            String.format(
                "The disabled job (%s) was retrieved incorrectly as an unscheduled job",
                disabledJob.getId()));
      }
    }

    Job job = getTestJobDetails();

    List<Job> beforeRetrievedJobs = schedulerService.getJobs();

    schedulerService.createJob(job);

    Job retrievedJob = schedulerService.getJob(job.getId());

    compareJobs(job, retrievedJob);

    String retrievedJobName = schedulerService.getJobName(job.getId());

    assertEquals(retrievedJobName, job.getName(), "The correct job name was not retrieved");

    List<Job> afterRetrievedJobs = schedulerService.getJobs();

    assertEquals(
        beforeRetrievedJobs.size() + 1,
        afterRetrievedJobs.size(),
        "The correct number of jobs was not retrieved");

    boolean foundJob = false;

    for (Job afterRetrievedJob : afterRetrievedJobs) {
      if (afterRetrievedJob.getId().equals(job.getId())) {
        compareJobs(job, afterRetrievedJob);

        foundJob = true;

        break;
      }
    }

    if (!foundJob) {
      fail(String.format("Failed to find the job (%s) in the list of jobs", job.getId()));
    }

    boolean foundUnscheduledJob = false;

    unscheduledJobs = schedulerService.getUnscheduledJobs();

    for (Job unscheduledJob : unscheduledJobs) {
      if (unscheduledJob.getId().equals(job.getId())) {
        foundUnscheduledJob = true;

        break;
      }
    }

    if (!foundUnscheduledJob) {
      fail(
          String.format(
              "Failed to find the job (%s) in the list of unscheduled jobs", job.getId()));
    }

    // noinspection StatementWithEmptyBody
    while (schedulerService.scheduleNextUnscheduledJobForExecution()) {}

    unscheduledJobs = schedulerService.getUnscheduledJobs();

    for (Job unscheduledJob : unscheduledJobs) {
      if (unscheduledJob.getId().equals(job.getId())) {
        fail(
            String.format(
                "The job (%s) was retrieved incorrectly as an unscheduled job", job.getId()));

        break;
      }
    }

    retrievedJob = schedulerService.getJob(job.getId());

    assertEquals(
        JobStatus.SCHEDULED,
        retrievedJob.getStatus(),
        "The status for the job (" + job.getId() + ") is incorrect");

    job.removeParameter(job.getParameters().iterator().next().getName());

    JobParameter jobParameter = job.getParameters().iterator().next();

    jobParameter.setName(jobParameter.getName() + " Updated");

    schedulerService.updateJob(job);

    Job updatedJob = schedulerService.getJob(job.getId());

    compareJobs(job, updatedJob);

    schedulerService.deleteJob(job.getId());

    try {
      schedulerService.getJob(job.getId());

      fail("Retrieved the job (" + job.getId() + ") that should have been deleted");
    } catch (JobNotFoundException ignore) {
    }
  }

  private void compareJobs(Job job1, Job job2) {
    assertEquals(job1.getId(), job2.getId(), "The ID values for the jobs do not match");
    assertEquals(job1.getName(), job2.getName(), "The name values for the jobs do not match");
    assertEquals(
        job1.getSchedulingPattern(),
        job2.getSchedulingPattern(),
        "The scheduling pattern values for the jobs do not match");
    assertEquals(
        job1.getJobClass(), job2.getJobClass(), "The job class values for the jobs do not match");
    assertEquals(
        job1.isEnabled(), job2.isEnabled(), "The is enabled values for the jobs do not match");
    assertEquals(job1.getStatus(), job2.getStatus(), "The status values for the jobs do not match");
    assertEquals(
        job1.getExecutionAttempts(),
        job2.getExecutionAttempts(),
        "The execution attempts values for the jobs do not match");
    assertEquals(
        job1.getLockName(), job2.getLockName(), "The lock name values for the jobs do not match");
    assertEquals(
        job1.getLastExecuted(),
        job2.getLastExecuted(),
        "The last executed values for the jobs do not match");
    assertEquals(
        job1.getParameters().size(),
        job2.getParameters().size(),
        "The number of parameters for the jobs do not match");

    for (JobParameter job1Parameter : job1.getParameters()) {
      boolean foundParameter = false;

      for (JobParameter job2Parameter : job2.getParameters()) {
        if (job1Parameter.getName().equalsIgnoreCase(job2Parameter.getName())) {
          assertEquals(
              job1Parameter.getValue(),
              job2Parameter.getValue(),
              "The values for the job parameters (" + job1Parameter.getName() + ") do not match");

          foundParameter = true;
        }
      }

      if (!foundParameter) {
        fail("Failed to find the job parameter (" + job1Parameter.getName() + ")");
      }
    }
  }
}
