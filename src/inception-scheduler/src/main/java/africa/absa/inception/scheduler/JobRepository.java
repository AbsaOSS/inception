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
import java.util.List;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * The <b>JobRepository</b> interface declares the repository for the <b>Job</b> domain type.
 *
 * @author Marcus Portmann
 */
public interface JobRepository extends JpaRepository<Job, String> {

  /**
   * Delete the job.
   *
   * @param jobId the ID for the job
   */
  @Modifying
  @Query("delete from Job j where j.id = :jobId")
  void deleteById(@Param("jobId") String jobId);

  /**
   * Retrieve the filtered jobs.
   *
   * @param filter the filter to apply to the jobs
   * @return the filtered jobs
   */
  @Query(
      "select j from Job j where lower(j.name) like lower(:filter) or lower(j.jobClass) "
          + "like lower(:filter)")
  List<Job> findFiltered(String filter);

  /**
   * Retrieve the jobs scheduled for execution.
   *
   * @param lastExecutedBefore the date and time used to select failed jobs for reprocessing
   * @param currentTimestamp the current date and time
   * @param pageable the pagination information
   * @return the jobs scheduled for execution
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      "select j from Job j where j.enabled = true and j.status = 2 and "
          + "(j.lastExecuted < :lastExecutedBefore or j.executionAttempts = 0) "
          + "and j.nextExecution <= :currentTimestamp")
  List<Job> findJobsScheduledForExecutionForWrite(
      @Param("lastExecutedBefore") LocalDateTime lastExecutedBefore,
      @Param("currentTimestamp") LocalDateTime currentTimestamp,
      Pageable pageable);

  /**
   * Retrieve the unscheduled jobs.
   *
   * @return the unscheduled jobs
   */
  @Query("select j from Job j where j.enabled = true and j.status = 1")
  List<Job> findUnscheduledJobs();

  /**
   * Retrieve and lock the unscheduled jobs.
   *
   * @param pageable the pagination information
   * @return the unscheduled jobs
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select j from Job j where j.enabled = true and j.status = 1")
  List<Job> findUnscheduledJobsForWrite(Pageable pageable);

  /**
   * Retrieve the name for the job.
   *
   * @param jobId the ID for the job
   * @return an Optional containing the name for the job or an empty Optional if the job could not
   *     be found
   */
  @Query("select j.name from Job j where j.id = :jobId")
  Optional<String> getNameById(@Param("jobId") String jobId);

  /**
   * Lock the job for execution.
   *
   * @param jobId the ID for the job
   * @param lockName the name of the lock
   * @param when the date and time the job is locked for execution
   */
  @Modifying
  @Query(
      "update Job j set j.lockName = :lockName, j.status = 3, "
          + "j.executionAttempts = j.executionAttempts + 1, j.lastExecuted = :when "
          + "where j.id = :jobId")
  void lockJobForExecution(
      @Param("jobId") String jobId,
      @Param("lockName") String lockName,
      @Param("when") LocalDateTime when);

  /**
   * Reset the job locks with the specified status.
   *
   * @param status the status
   * @param newStatus the new status for the jobs
   * @param lockName the lock name
   */
  @Modifying
  @Query(
      "update Job j set j.status = :newStatus, j.lockName = null "
          + "where j.lockName = :lockName and j.status = :status")
  void resetJobLocks(
      @Param("status") JobStatus status,
      @Param("newStatus") JobStatus newStatus,
      @Param("lockName") String lockName);

  /**
   * Schedule the job.
   *
   * @param jobId the ID for the job
   * @param nextExecution the date and time the job is scheduled for execution
   */
  @Modifying
  @Query(
      "update Job j set j.status = 2, j.executionAttempts = 0, "
          + "j.nextExecution = :nextExecution where j.id = :jobId")
  void scheduleJob(
      @Param("jobId") String jobId, @Param("nextExecution") LocalDateTime nextExecution);

  /**
   * Set the job status.
   *
   * @param jobId the ID for the job
   * @param status the status for the job
   */
  @Modifying
  @Query("update Job j set j.status = :status where j.id = :jobId")
  void setJobStatus(@Param("jobId") String jobId, @Param("status") JobStatus status);

  /**
   * Unlock the job.
   *
   * @param jobId the ID for the job
   * @param status the status for the job
   */
  @Modifying
  @Query("update Job j set j.status = :status, j.lockName = null where j.id = :jobId")
  void unlockJob(@Param("jobId") String jobId, @Param("status") JobStatus status);
}
