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
} from '@absaoss/ngx-inception/core';

import {finalize, first} from 'rxjs/operators';
import {Config} from '../services/config';
import {ConfigService} from '../services/config.service';

/**
 * The ConfigsComponent class implements the configs component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'configs.component.html',
  styleUrls: ['configs.component.css']
})
export class ConfigsComponent extends AdminContainerView implements AfterViewInit {

  dataSource = new MatTableDataSource<Config>();
  displayedColumns = ['key', 'value', 'actions'];
  @HostBinding('class') hostClass = 'flex flex-column flex-fill';
  @ViewChild(MatPaginator, {static: true}) paginator!: MatPaginator;

  @ViewChild(MatSort, {static: true}) sort!: MatSort;

  constructor(private router: Router, private activatedRoute: ActivatedRoute,
              private configService: ConfigService, private dialogService: DialogService,
              private spinnerService: SpinnerService) {
    super();

    // Set the data source filter
    this.dataSource.filterPredicate = (data, filter): boolean => data.key.toLowerCase().includes(filter);
  }

  get title(): string {
    return $localize`:@@config_configs_title:Configs`
  }

  applyFilter(filterValue: string): void {
    filterValue = filterValue.trim();
    filterValue = filterValue.toLowerCase();
    this.dataSource.filter = filterValue;
  }

  deleteConfig(key: string): void {
    const dialogRef: MatDialogRef<ConfirmationDialogComponent, boolean> = this.dialogService.showConfirmationDialog({
      message: $localize`:@@config_configs_confirm_delete_config:Are you sure you want to delete the config?`
    });

    dialogRef.afterClosed()
    .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
    .subscribe((confirmation: boolean | undefined) => {
      if (confirmation === true) {
        this.spinnerService.showSpinner();

        this.configService.deleteConfig(key)
        .pipe(first())
        .subscribe(() => {
          this.loadConfigs();
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

  editConfig(key: string): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate([encodeURIComponent(key) + '/edit'], {relativeTo: this.activatedRoute});
  }

  loadConfigs(): void {
    this.spinnerService.showSpinner();

    this.configService.getConfigs()
    .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
    .subscribe((configs: Config[]) => {
      this.dataSource.data = configs;
    }, (error: Error) => {
      // noinspection SuspiciousTypeOfGuard
      if ((error instanceof AccessDeniedError) || (error instanceof InvalidArgumentError) || (error instanceof ServiceUnavailableError)) {
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigateByUrl('/error/send-error-report', {state: {error}});
      } else {
        this.dialogService.showErrorDialog(error);
      }
    });
  }

  newConfig(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['new'], {relativeTo: this.activatedRoute});
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    this.loadConfigs();
  }
}

