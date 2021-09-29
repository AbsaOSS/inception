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

import {
  AccessDeniedError, AdminContainerView, BackNavigation, DialogService, Error, InvalidArgumentError,
  ServiceUnavailableError, SpinnerService
} from '@absaoss/ngx-inception/core';
import {AfterViewInit, Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {ActivatedRoute, Router} from '@angular/router';
import {finalize, first} from 'rxjs/operators';
import {Job} from '../services/job';
import {JobParameter} from '../services/job-parameter';
import {JobStatus} from '../services/job-status';
import {SchedulerService} from '../services/scheduler.service';
import {
  JobParameterDialogComponent, JobParameterDialogData
} from './job-parameter-dialog.component';

/**
 * The EditJobComponent class implements the edit job component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'edit-job.component.html',
  styleUrls: ['edit-job.component.css'],
})
export class EditJobComponent extends AdminContainerView implements AfterViewInit {

  JobStatus = JobStatus;

  editJobForm: FormGroup;

  enabledFormControl: FormControl;

  getJobStatusDescription = SchedulerService.getJobStatusDescription;

  idFormControl: FormControl;

  job?: Job;

  jobClassFormControl: FormControl;

  jobId: string;

  jobParameters: JobParameter[] = [];

  jobStatuses: JobStatus[] = [JobStatus.Unscheduled, JobStatus.Scheduled, JobStatus.Executing,
    JobStatus.Executed, JobStatus.Aborted, JobStatus.Failed, JobStatus.OnceOff];

  nameFormControl: FormControl;

  schedulingPatternFormControl: FormControl;

  statusFormControl: FormControl;

  constructor(private router: Router, private activatedRoute: ActivatedRoute,
              private schedulerService: SchedulerService, private dialogService: DialogService,
              private spinnerService: SpinnerService, private matDialog: MatDialog) {
    super();

    // Retrieve the route parameters
    const jobId = this.activatedRoute.snapshot.paramMap.get('jobId');

    if (!jobId) {
      throw(new Error('No jobId route parameter found'));
    }

    this.jobId = decodeURIComponent(jobId);

    // Initialise the form controls
    this.enabledFormControl = new FormControl(true, [Validators.required]);
    this.idFormControl = new FormControl({
      value: '',
      disabled: true
    }, [Validators.required]);
    this.jobClassFormControl = new FormControl('', [Validators.required, Validators.maxLength(1000)]);
    this.nameFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.schedulingPatternFormControl = // tslint:disable-next-line:max-line-length
      new FormControl('0 * * * *', [Validators.required, Validators.maxLength(100), Validators.pattern(// tslint:disable-next-line:max-line-length
        '(((([*])|(((([0-5])?[0-9])((-(([0-5])?[0-9])))?)))((/(([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?[0-9])))?))(,(((([*])|(((([0-5])?[0-9])((-(([0-5])?[0-9])))?)))((/(([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?[0-9])))?)))* (((([*])|(((((([0-1])?[0-9]))|(([2][0-3])))((-(((([0-1])?[0-9]))|(([2][0-3])))))?)))((/(([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?[0-9])))?))(,(((([*])|(((((([0-1])?[0-9]))|(([2][0-3])))((-(((([0-1])?[0-9]))|(([2][0-3])))))?)))((/(([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?[0-9])))?)))* (((((((([*])|(((((([1-2])?[0-9]))|(([3][0-1]))|(([1-9])))((-(((([1-2])?[0-9]))|(([3][0-1]))|(([1-9])))))?)))((/(([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?[0-9])))?))|(L)|(((((([1-2])?[0-9]))|(([3][0-1]))|(([1-9])))W))))(,(((((([*])|(((((([1-2])?[0-9]))|(([3][0-1]))|(([1-9])))((-(((([1-2])?[0-9]))|(([3][0-1]))|(([1-9])))))?)))((/(([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?[0-9])))?))|(L)|(((((([1-2])?[0-9]))|(([3][0-1]))|(([1-9])))W)))))*)|([?])) (((([*])|((((([1-9]))|(([1][0-2])))((-((([1-9]))|(([1][0-2])))))?))|((((JAN)|(FEB)|(MAR)|(APR)|(MAY)|(JUN)|(JUL)|(AUG)|(SEP)|(OKT)|(NOV)|(DEC))((-((JAN)|(FEB)|(MAR)|(APR)|(MAY)|(JUN)|(JUL)|(AUG)|(SEP)|(OKT)|(NOV)|(DEC))))?)))((/(([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?[0-9])))?))(,(((([*])|((((([1-9]))|(([1][0-2])))((-((([1-9]))|(([1][0-2])))))?))|((((JAN)|(FEB)|(MAR)|(APR)|(MAY)|(JUN)|(JUL)|(AUG)|(SEP)|(OKT)|(NOV)|(DEC))((-((JAN)|(FEB)|(MAR)|(APR)|(MAY)|(JUN)|(JUL)|(AUG)|(SEP)|(OKT)|(NOV)|(DEC))))?)))((/(([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?[0-9])))?)))* (((((((([*])|((([0-6])((-([0-6])))?))|((((SUN)|(MON)|(TUE)|(WED)|(THU)|(FRI)|(SAT))((-((SUN)|(MON)|(TUE)|(WED)|(THU)|(FRI)|(SAT))))?)))((/(([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?[0-9])))?))|((([0-6])L))|(W)|(([#][1-5]))))(,(((((([*])|((([0-6])((-([0-6])))?))|((((SUN)|(MON)|(TUE)|(WED)|(THU)|(FRI)|(SAT))((-((SUN)|(MON)|(TUE)|(WED)|(THU)|(FRI)|(SAT))))?)))((/(([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?[0-9])))?))|((([0-6])L))|(W)|(([#][1-5])))))*)|([?]))((( (((([*])|((([1-2][0-9][0-9][0-9])((-([1-2][0-9][0-9][0-9])))?)))((/(([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?[0-9])))?))(,(((([*])|((([1-2][0-9][0-9][0-9])((-([1-2][0-9][0-9][0-9])))?)))((/(([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?([0-9])?[0-9])))?)))*))?)')
      ]);
    this.statusFormControl = new FormControl(JobStatus.Unscheduled, [Validators.required]);

    // Initialise the form
    this.editJobForm = new FormGroup({
      enabled: this.enabledFormControl,
      id: this.idFormControl,
      jobClass: this.jobClassFormControl,
      name: this.nameFormControl,
      schedulingPattern: this.schedulingPatternFormControl,
      status: this.statusFormControl
    });
  }

  get backNavigation(): BackNavigation {
    return new BackNavigation($localize`:@@scheduler_edit_job_back_navigation:Jobs`,
      ['../..'], {relativeTo: this.activatedRoute});
  }

  get title(): string {
    return $localize`:@@scheduler_edit_job_title:Edit Job`
  }

  cancel(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['../..'], {relativeTo: this.activatedRoute});
  }

  deleteJobParameter(existingJobParameter: JobParameter): void {
    this.jobParameters.forEach((jobParameter, index) => {
      if (jobParameter.name === existingJobParameter.name) {
        this.jobParameters.splice(index, 1);
      }
    });
  }

  editJobParameter(existingJobParameter: JobParameter): void {
    const data: JobParameterDialogData = {
      name: existingJobParameter.name,
      readonlyName: true,
      value: existingJobParameter.value
    };

    const dialogRef: MatDialogRef<JobParameterDialogComponent, JobParameter> = this.matDialog.open(
      JobParameterDialogComponent, {
        restoreFocus: false,
        data
      });

    dialogRef.afterClosed()
    .pipe(first())
    .subscribe((jobParameter: JobParameter | undefined) => {
      if (jobParameter) {
        for (const aJobParameter of this.jobParameters) {
          if (aJobParameter.name === jobParameter.name) {
            aJobParameter.value = jobParameter.value;
            return;
          }
        }
      }
    });
  }

  newJobParameter(): void {
    const data: JobParameterDialogData = {
      name: '',
      value: ''
    };

    const dialogRef: MatDialogRef<JobParameterDialogComponent, JobParameter> = this.matDialog.open(
      JobParameterDialogComponent, {
        restoreFocus: false,
        data
      });

    dialogRef.afterClosed()
    .pipe(first())
    .subscribe((jobParameter: JobParameter | undefined) => {
      if (jobParameter) {
        for (const aJobParameter of this.jobParameters) {
          if (aJobParameter.name === jobParameter.name) {
            this.dialogService.showErrorDialog(new Error('The job parameter already exists.'));

            return;
          }
        }

        this.jobParameters.push(jobParameter);

        this.jobParameters.sort((a: JobParameter, b: JobParameter) => {
          if ((a.name ? a.name.toLowerCase() : '') < (b.name ? b.name.toLowerCase() : '')) {
            return -1;
          }
          if ((a.name ? a.name.toLowerCase() : '') > (b.name ? b.name.toLowerCase() : '')) {
            return 1;
          }
          return 0;
        });
      }
    });
  }

  ngAfterViewInit(): void {
    // Retrieve the existing job and initialise the form controls
    this.spinnerService.showSpinner();

    this.schedulerService.getJob(this.jobId)
    .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
    .subscribe((job: Job) => {
      this.job = job;
      this.enabledFormControl.setValue(job.enabled);
      this.idFormControl.setValue(job.id);
      this.jobClassFormControl.setValue(job.jobClass);
      this.nameFormControl.setValue(job.name);
      this.schedulingPatternFormControl.setValue(job.schedulingPattern);
      this.statusFormControl.setValue(job.status);
      this.jobParameters = job.parameters;
    }, (error: Error) => {
      // noinspection SuspiciousTypeOfGuard
      if ((error instanceof AccessDeniedError) || (error instanceof InvalidArgumentError) ||
        (error instanceof ServiceUnavailableError)) {
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigateByUrl('/error/send-error-report', {state: {error}});
      } else {
        this.dialogService.showErrorDialog(error).afterClosed()
        .pipe(first())
        .subscribe(() => {
          this.router.navigate(['../..'], {relativeTo: this.activatedRoute});
        });
      }
    });
  }

  ok(): void {
    if (this.job && this.editJobForm.valid) {
      this.job.enabled = this.enabledFormControl.value;
      this.job.id = this.idFormControl.value;
      this.job.jobClass = this.jobClassFormControl.value;
      this.job.name = this.nameFormControl.value;
      this.job.parameters = this.jobParameters;
      this.job.schedulingPattern = this.schedulingPatternFormControl.value;
      this.job.status = this.statusFormControl.value;

      this.spinnerService.showSpinner();

      this.schedulerService.updateJob(this.job)
      .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
      .subscribe(() => {
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigate(['../..'], {relativeTo: this.activatedRoute});
      }, (error: Error) => {
        // noinspection SuspiciousTypeOfGuard
        if ((error instanceof AccessDeniedError) || (error instanceof InvalidArgumentError) ||
          (error instanceof ServiceUnavailableError)) {
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigateByUrl('/error/send-error-report', {state: {error}});
        } else {
          this.dialogService.showErrorDialog(error);
        }
      });
    }
  }
}
