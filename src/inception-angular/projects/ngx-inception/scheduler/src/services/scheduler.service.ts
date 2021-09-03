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

import {HttpClient, HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {Inject, Injectable} from '@angular/core';
import {
  AccessDeniedError, CommunicationError, INCEPTION_CONFIG, InceptionConfig, InvalidArgumentError,
  ProblemDetails, ServiceUnavailableError
} from '@absaoss/ngx-inception/core';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {Job} from './job';
import {JobStatus} from './job-status';
import {DuplicateJobError, JobNotFoundError} from './scheduler.service.errors';

/**
 * The Scheduler Service implementation.
 *
 * @author Marcus Portmann
 */
@Injectable({
  providedIn: 'root'
})
export class SchedulerService {

  /**
   * Constructs a new SchedulerService.
   *
   * @param config     The Inception configuration.
   * @param httpClient The HTTP client.
   */
  constructor(@Inject(INCEPTION_CONFIG) private config: InceptionConfig, private httpClient: HttpClient) {
    console.log('Initializing the Scheduler Service');
  }

  /**
   * Retrieve the description for the job status.
   *
   * @param jobStatus the job status
   */
  static getJobStatusDescription(jobStatus: JobStatus): string {
    switch (jobStatus) {
      case JobStatus.Unscheduled:
        return $localize`:@@scheduler_job_status_unscheduled:Unscheduled`;
      case JobStatus.Scheduled:
        return $localize`:@@scheduler_job_status_scheduled:Scheduled`;
      case JobStatus.Executing:
        return $localize`:@@scheduler_job_status_executing:Executing`;
      case JobStatus.Executed:
        return $localize`:@@scheduler_job_status_executed:Executed`;
      case JobStatus.Aborted:
        return $localize`:@@scheduler_job_status_aborted:Aborted`;
      case JobStatus.Failed:
        return $localize`:@@scheduler_job_status_failed:Failed`;
      case JobStatus.OnceOff:
        return $localize`:@@scheduler_job_status_once_off:Once Off`;
      default:
        return $localize`:@@scheduler_job_status_unknown:Unknown`;
    }
  }

  /**
   * Create a job.
   *
   * @param job The job to create.
   *
   * @return True if the job was created successfully or false otherwise.
   */
  createJob(job: Job): Observable<boolean> {
    return this.httpClient.post<boolean>(this.config.schedulerApiUrlPrefix + '/jobs', job, {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, DuplicateJobError.TYPE)) {
        return throwError(new DuplicateJobError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to create the job.', httpErrorResponse));
    }));
  }

  /**
   * Delete the job.
   *
   * @param jobId The ID for the job.
   *
   * @return True if the job was deleted or false otherwise.
   */
  deleteJob(jobId: string): Observable<boolean> {
    return this.httpClient.delete<boolean>(this.config.schedulerApiUrlPrefix + '/jobs/' + encodeURIComponent(jobId),
      {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, JobNotFoundError.TYPE)) {
        return throwError(new JobNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to delete the job.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the job.
   *
   * @param jobId The ID for the job.
   *
   * @return The job.
   */
  getJob(jobId: string): Observable<Job> {
    return this.httpClient.get<Job>(this.config.schedulerApiUrlPrefix + '/jobs/' + encodeURIComponent(jobId),
      {reportProgress: true})
    .pipe(map((job: Job) => {
      return job;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, JobNotFoundError.TYPE)) {
        return throwError(new JobNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the job.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the name of the job.
   *
   * @param jobId The ID for the job.
   *
   * @return The name of the job.
   */
  getJobName(jobId: string): Observable<string> {
    return this.httpClient.get<string>(
      this.config.schedulerApiUrlPrefix + '/jobs/' + encodeURIComponent(jobId) + '/name', {
        reportProgress: true,
      }).pipe(map((jobName: string) => {
      return jobName;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, JobNotFoundError.TYPE)) {
        return throwError(new JobNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the job name.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve all the jobs.
   *
   * @return The jobs.
   */
  getJobs(): Observable<Job[]> {
    return this.httpClient.get<Job[]>(this.config.schedulerApiUrlPrefix + '/jobs', {reportProgress: true})
    .pipe(map((jobs: Job[]) => {
      return jobs;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the jobs.', httpErrorResponse));
    }));
  }

  /**
   * Update the job.
   *
   * @param job The job.
   *
   * @return True if the job was updated successfully or false otherwise.
   */
  updateJob(job: Job): Observable<boolean> {
    return this.httpClient.put<boolean>(this.config.schedulerApiUrlPrefix + '/jobs/' + encodeURIComponent(job.id), job,
      {observe: 'response'}).pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, JobNotFoundError.TYPE)) {
        return throwError(new JobNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to update the job.', httpErrorResponse));
    }));
  }
}
