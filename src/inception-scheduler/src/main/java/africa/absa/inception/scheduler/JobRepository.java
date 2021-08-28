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

  @Modifying
  @Query("delete from Job j where j.id = :jobId")
  void deleteById(@Param("jobId") String jobId);

  @Query(
      "select j from Job j where lower(j.name) like lower(:filter) or lower(j.jobClass) "
          + "like lower(:filter)")
  List<Job> findFiltered(String filter);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      "select j from Job j where j.enabled = true and j.status = 2 and "
          + "(j.lastExecuted < :lastExecutedBefore or j.executionAttempts = 0) "
          + "and j.nextExecution <= :currentTimestamp")
  List<Job> findJobsScheduledForExecutionForWrite(
      @Param("lastExecutedBefore") LocalDateTime lastExecutedBefore,
      @Param("currentTimestamp") LocalDateTime currentTimestamp,
      Pageable pageable);

  @Query("select j from Job j where j.enabled = true and j.status = 1")
  List<Job> findUnscheduledJobs();

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select j from Job j where j.enabled = true and j.status = 1")
  List<Job> findUnscheduledJobsForWrite(Pageable pageable);

  @Query("select j.name from Job j where j.id = :jobId")
  Optional<String> getNameById(@Param("jobId") String jobId);

  @Modifying
  @Query(
      "update Job j set j.lockName = :lockName, j.status = 3, "
          + "j.executionAttempts = j.executionAttempts + 1, j.lastExecuted = :when "
          + "where j.id = :jobId")
  void lockJobForExecution(
      @Param("jobId") String jobId,
      @Param("lockName") String lockName,
      @Param("when") LocalDateTime when);

  @Modifying
  @Query(
      "update Job j set j.status = :newStatus, j.lockName = null "
          + "where j.lockName = :lockName and j.status = :status")
  void resetJobLocks(
      @Param("status") JobStatus status,
      @Param("newStatus") JobStatus newStatus,
      @Param("lockName") String lockName);

  @Modifying
  @Query(
      "update Job j set j.status = 2, j.executionAttempts = 0, "
          + "j.nextExecution = :nextExecution where j.id = :jobId")
  void scheduleJob(
      @Param("jobId") String jobId, @Param("nextExecution") LocalDateTime nextExecution);

  @Modifying
  @Query("update Job j set j.status = :status where j.id = :jobId")
  void setJobStatus(@Param("jobId") String jobId, @Param("status") JobStatus status);

  @Modifying
  @Query("update Job j set j.status = :status, j.lockName = null where j.id = :jobId")
  void unlockJob(@Param("jobId") String jobId, @Param("status") JobStatus status);
}
