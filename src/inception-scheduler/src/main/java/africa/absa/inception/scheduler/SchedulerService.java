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
import africa.absa.inception.core.service.ValidationError;
import africa.absa.inception.core.util.ServiceUtil;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * The <b>SchedulerService</b> class provides the Scheduler Service implementation.
 *
 * @author Marcus Portmann
 */
@Service
public class SchedulerService implements ISchedulerService, InitializingBean {

  /* Logger */
  private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);

  /** The Spring application context. */
  private final ApplicationContext applicationContext;

  /* The name of the Scheduler Service instance. */
  private final String instanceName = ServiceUtil.getServiceInstanceName("SchedulerService");

  /** The Job Repository. */
  private final JobRepository jobRepository;

  /** The JSR-303 validator. */
  private final Validator validator;

  /* Entity Manager */
  @PersistenceContext(unitName = "scheduler")
  private EntityManager entityManager;

  /*
   * The delay in milliseconds between successive attempts to execute a job.
   */
  @Value("${application.scheduler.jobExecutionRetryDelay:60000}")
  private int jobExecutionRetryDelay;

  /*
   * The maximum number of times execution will be attempted for a job.
   */
  @Value("${application.scheduler.maximumJobExecutionAttempts:144}")
  private int maximumJobExecutionAttempts;

  /**
   * Constructs a new <b>SchedulerService</b>.
   *
   * @param applicationContext the Spring application context
   * @param validator the JSR-303 validator
   * @param jobRepository the Job Repository
   */
  public SchedulerService(
      ApplicationContext applicationContext, Validator validator, JobRepository jobRepository) {
    this.validator = validator;
    this.applicationContext = applicationContext;
    this.jobRepository = jobRepository;
  }

  @Override
  public void afterPropertiesSet() {
    logger.info("Initializing the Scheduler Service (" + instanceName + ")");
  }

  @Override
  @Transactional
  public void createJob(Job job)
      throws InvalidArgumentException, DuplicateJobException, ServiceUnavailableException {
    validateJob(job);

    try {
      if (jobRepository.existsById(job.getId())) {
        throw new DuplicateJobException(job.getId());
      }

      job.setExecutionAttempts(0);

      jobRepository.saveAndFlush(job);
    } catch (DuplicateJobException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to create the job (" + job.getName() + ")", e);
    }
  }

  @Override
  @Transactional
  public void deleteJob(String jobId)
      throws InvalidArgumentException, JobNotFoundException, ServiceUnavailableException {
    if (!StringUtils.hasText(jobId)) {
      throw new InvalidArgumentException("jobId");
    }

    try {
      if (!jobRepository.existsById(jobId)) {
        throw new JobNotFoundException(jobId);
      }

      jobRepository.deleteById(jobId);
    } catch (JobNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to delete the job (" + jobId + ")", e);
    }
  }

  @Override
  public void executeJob(Job job) throws InvalidArgumentException, ServiceUnavailableException {
    validateJob(job);

    Class<?> jobClass;

    // Load the job class.
    try {
      jobClass = Thread.currentThread().getContextClassLoader().loadClass(job.getJobClass());
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to execute the job ("
              + job.getName()
              + ") with ID ("
              + job.getId()
              + "): Failed to load the job class ("
              + job.getJobClass()
              + ")",
          e);
    }

    // Initialize the job
    IJob jobImplementation;

    try {
      // Create a new instance of the job
      Object jobObject = jobClass.getConstructor().newInstance();

      // Check if the job is a valid job
      if (!(jobObject instanceof IJob)) {
        throw new ServiceUnavailableException(
            "The job class ("
                + job.getJobClass()
                + ") does not implement the africa.absa.inception.scheduler.IJob interface");
      }

      jobImplementation = (IJob) jobObject;

      // Perform dependency injection for the job implementation
      applicationContext.getAutowireCapableBeanFactory().autowireBean(jobImplementation);
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to initialize the job (" + job.getName() + ") with ID (" + job.getId() + ")", e);
    }

    // Execute the job
    try {
      // Retrieve the parameters for the job
      Map<String, String> parameters = new HashMap<>();

      for (JobParameter jobParameter : job.getParameters()) {
        parameters.put(jobParameter.getName(), jobParameter.getValue());
      }

      // Initialize the job execution context
      JobExecutionContext context = new JobExecutionContext(job.getNextExecution(), parameters);

      // Execute the job
      jobImplementation.execute(context);
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to execute the job (" + job.getName() + ") with ID (" + job.getId() + ")", e);
    }
  }

  @Override
  public List<Job> getFilteredJobs(String filter) throws ServiceUnavailableException {
    try {
      if (StringUtils.hasText(filter)) {
        return jobRepository.findFiltered("%" + filter + "%");
      } else {
        return jobRepository.findAll();
      }
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the jobs matching the filter (" + filter + ")", e);
    }
  }

  @Override
  public Job getJob(String jobId)
      throws InvalidArgumentException, JobNotFoundException, ServiceUnavailableException {
    if (!StringUtils.hasText(jobId)) {
      throw new InvalidArgumentException("jobId");
    }

    try {
      Optional<Job> jobOptional = jobRepository.findById(jobId);

      if (jobOptional.isPresent()) {
        return jobOptional.get();
      } else {
        throw new JobNotFoundException(jobId);
      }
    } catch (JobNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to retrieve the job (" + jobId + ")", e);
    }
  }

  @Override
  public String getJobName(String jobId)
      throws InvalidArgumentException, JobNotFoundException, ServiceUnavailableException {
    if (!StringUtils.hasText(jobId)) {
      throw new InvalidArgumentException("jobId");
    }

    try {
      Optional<String> nameOptional = jobRepository.getNameById(jobId);

      if (nameOptional.isPresent()) {
        return nameOptional.get();
      }

      throw new JobNotFoundException(jobId);
    } catch (JobNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the name of the job (" + jobId + ")", e);
    }
  }

  @Override
  public List<Job> getJobs() throws ServiceUnavailableException {
    try {
      return jobRepository.findAll();
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to retrieve the jobs", e);
    }
  }

  @Override
  public int getMaximumJobExecutionAttempts() {
    return maximumJobExecutionAttempts;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Optional<Job> getNextJobScheduledForExecution() throws ServiceUnavailableException {
    try {
      LocalDateTime lastExecutedBefore = LocalDateTime.now();

      lastExecutedBefore = lastExecutedBefore.minus(jobExecutionRetryDelay, ChronoUnit.MILLIS);

      PageRequest pageRequest = PageRequest.of(0, 1);

      List<Job> jobs =
          jobRepository.findJobsScheduledForExecutionForWrite(
              lastExecutedBefore, LocalDateTime.now(), pageRequest);

      if (jobs.size() > 0) {
        Job job = jobs.get(0);

        LocalDateTime when = LocalDateTime.now();

        jobRepository.lockJobForExecution(job.getId(), instanceName, when);

        entityManager.detach(job);

        job.setStatus(JobStatus.EXECUTING);
        job.setLockName(instanceName);
        job.incrementExecutionAttempts();
        job.setLastExecuted(when);

        return Optional.of(job);
      } else {
        return Optional.empty();
      }
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the next job that has been scheduled for execution", e);
    }
  }

  @Override
  public List<Job> getUnscheduledJobs() throws ServiceUnavailableException {
    try {
      return jobRepository.findUnscheduledJobs();
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to retrieve the unscheduled jobs", e);
    }
  }

  @Override
  @Transactional
  public void rescheduleJob(String jobId, String schedulingPattern)
      throws InvalidArgumentException, ServiceUnavailableException {
    if (!StringUtils.hasText(jobId)) {
      throw new InvalidArgumentException("jobId");
    }

    if (!StringUtils.hasText(schedulingPattern)) {
      throw new InvalidArgumentException("schedulingPattern");
    }

    try {
      Predictor predictor = new Predictor(schedulingPattern, System.currentTimeMillis());

      jobRepository.scheduleJob(jobId, predictor.nextMatchingLocalDateTime());
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to reschedule the job (" + jobId + ") for execution", e);
    }
  }

  @Override
  @Transactional
  public void resetJobLocks(JobStatus status, JobStatus newStatus)
      throws InvalidArgumentException, ServiceUnavailableException {
    if (status == null) {
      throw new InvalidArgumentException("status");
    }

    if (newStatus == null) {
      throw new InvalidArgumentException("newStatus");
    }

    try {
      jobRepository.resetJobLocks(status, newStatus, instanceName);
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to reset the locks for the jobs with status ("
              + status
              + ") that have been locked using the lock name ("
              + instanceName
              + ")",
          e);
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public boolean scheduleNextUnscheduledJobForExecution() throws ServiceUnavailableException {
    try {
      PageRequest pageRequest = PageRequest.of(0, 1);

      List<Job> jobs = jobRepository.findUnscheduledJobsForWrite(pageRequest);

      if (jobs.size() > 0) {
        Job job = jobs.get(0);

        LocalDateTime nextExecution = null;

        try {
          Predictor predictor =
              new Predictor(job.getSchedulingPattern(), System.currentTimeMillis());

          nextExecution = predictor.nextMatchingLocalDateTime();
        } catch (Throwable e) {
          logger.error(
              "The next execution date could not be determined for the unscheduled job ("
                  + job.getId()
                  + ") with the scheduling pattern ("
                  + job.getSchedulingPattern()
                  + "): The job will be marked as FAILED",
              e);
        }

        if (nextExecution == null) {
          jobRepository.setJobStatus(job.getId(), JobStatus.FAILED);
        } else {
          logger.info(
              "Scheduling the unscheduled job ("
                  + job.getId()
                  + ") for execution at ("
                  + nextExecution
                  + ")");

          jobRepository.scheduleJob(job.getId(), nextExecution);
        }

        return true;
      } else {
        return false;
      }
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to schedule the next unscheduled job", e);
    }
  }

  @Override
  @Transactional
  public void setJobStatus(String jobId, JobStatus status)
      throws InvalidArgumentException, JobNotFoundException, ServiceUnavailableException {
    if (!StringUtils.hasText(jobId)) {
      throw new InvalidArgumentException("jobId");
    }

    try {
      if (!jobRepository.existsById(jobId)) {
        throw new JobNotFoundException(jobId);
      }

      jobRepository.setJobStatus(jobId, status);
    } catch (JobNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to set the status (" + status + ") for the job (" + jobId + ")", e);
    }
  }

  @Override
  @Transactional
  public void unlockJob(String jobId, JobStatus status)
      throws InvalidArgumentException, JobNotFoundException, ServiceUnavailableException {
    if (!StringUtils.hasText(jobId)) {
      throw new InvalidArgumentException("jobId");
    }

    try {
      if (!jobRepository.existsById(jobId)) {
        throw new JobNotFoundException(jobId);
      }

      jobRepository.unlockJob(jobId, status);
    } catch (JobNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to unlock and set the status for the job (" + jobId + ") to (" + status + ")", e);
    }
  }

  @Override
  public void updateJob(Job job)
      throws InvalidArgumentException, JobNotFoundException, ServiceUnavailableException {
    validateJob(job);

    try {
      Optional<Job> jobOptional = jobRepository.findById(job.getId());

      if (jobOptional.isEmpty()) {
        throw new JobNotFoundException(job.getId());
      }

      Job existingJob = jobOptional.get();

      existingJob.setEnabled(job.isEnabled());
      existingJob.setJobClass(job.getJobClass());
      existingJob.setName(job.getName());
      existingJob.setParameters(job.getParameters());
      existingJob.setSchedulingPattern(job.getSchedulingPattern());
      existingJob.setStatus(job.getStatus());

      if (!job.isEnabled()) {
        job.setStatus(JobStatus.UNSCHEDULED);
        job.setNextExecution(null);
      }

      jobRepository.saveAndFlush(job);
    } catch (JobNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to update the job (" + job.getId() + ")", e);
    }
  }

  private void validateJob(Job job) throws InvalidArgumentException {
    if (job == null) {
      throw new InvalidArgumentException("job");
    }

    Set<ConstraintViolation<Job>> constraintViolations = validator.validate(job);

    if (!constraintViolations.isEmpty()) {
      throw new InvalidArgumentException(
          "job", ValidationError.toValidationErrors(constraintViolations));
    }
  }
}
