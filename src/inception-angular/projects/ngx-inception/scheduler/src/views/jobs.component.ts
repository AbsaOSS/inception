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

import {AfterViewInit, Component, HostBinding, ViewChild} from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {ActivatedRoute, Router} from '@angular/router';
import {
  AccessDeniedError, AdminContainerView, ConfirmationDialogComponent, DialogService, Error,
  InvalidArgumentError, ServiceUnavailableError, SpinnerService
} from 'ngx-inception/core';
import {finalize, first} from 'rxjs/operators';
import {Job} from '../services/job';
import {JobStatus} from '../services/job-status';
import {SchedulerService} from '../services/scheduler.service';

/**
 * The JobsComponent class implements the jobs component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'jobs.component.html',
  styleUrls: ['jobs.component.css']
})
export class JobsComponent extends AdminContainerView implements AfterViewInit {

  JobStatus = JobStatus;
  dataSource: MatTableDataSource<Job> = new MatTableDataSource<Job>();
  displayedColumns = ['name', 'status', 'executionAttempts', 'nextExecution', 'actions'];
  getJobStatusDescription = SchedulerService.getJobStatusDescription;
  @HostBinding('class') hostClass = 'flex flex-column flex-fill';
  @ViewChild(MatPaginator, {static: true}) paginator!: MatPaginator;

  @ViewChild(MatSort, {static: true}) sort!: MatSort;

  constructor(private router: Router, private activatedRoute: ActivatedRoute,
              private schedulerService: SchedulerService, private dialogService: DialogService,
              private spinnerService: SpinnerService) {
    super();

    // Set the data source filter
    this.dataSource.filterPredicate = (data, filter): boolean => data.name.toLowerCase().includes(filter);
  }

  get title(): string {
    return $localize`:@@scheduler_jobs_title:Jobs`
  }

  applyFilter(filterValue: string): void {
    filterValue = filterValue.trim();
    filterValue = filterValue.toLowerCase();
    this.dataSource.filter = filterValue;
  }

  deleteJob(jobId: string): void {
    const dialogRef: MatDialogRef<ConfirmationDialogComponent, boolean> = this.dialogService.showConfirmationDialog({
      message: $localize`:@@scheduler_jobs_confirm_delete_job:Are you sure you want to delete the job?`
    });

    dialogRef.afterClosed()
    .pipe(first())
    .subscribe((confirmation: boolean | undefined) => {
      if (confirmation === true) {
        this.spinnerService.showSpinner();

        this.schedulerService.deleteJob(jobId)
        .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
        .subscribe(() => {
          this.loadJobs();
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
    });
  }

  editJob(jobId: string): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate([encodeURIComponent(jobId) + '/edit'], {relativeTo: this.activatedRoute});
  }

  loadJobs(): void {
    this.spinnerService.showSpinner();

    this.schedulerService.getJobs()
    .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
    .subscribe((jobs: Job[]) => {
      this.dataSource.data = jobs;
    }, (error: Error) => {
      // noinspection SuspiciousTypeOfGuard
      if ((error instanceof SchedulerService) || (error instanceof AccessDeniedError) ||
        (error instanceof ServiceUnavailableError)) {
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigateByUrl('/error/send-error-report', {state: {error}});
      } else {
        this.dialogService.showErrorDialog(error);
      }
    });
  }

  newJob(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['new'], {relativeTo: this.activatedRoute});
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    this.loadJobs();
  }
}

