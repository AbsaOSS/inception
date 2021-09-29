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
  AccessDeniedError, AdminContainerView, ConfirmationDialogComponent, DialogService, Error,
  InvalidArgumentError, ServiceUnavailableError, SpinnerService
} from '@absaoss/ngx-inception/core';
import {AfterViewInit, Component, HostBinding, ViewChild} from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {ActivatedRoute, Router} from '@angular/router';
import {finalize, first} from 'rxjs/operators';
import {CodeCategorySummary} from '../services/code-category-summary';
import {CodesService} from '../services/codes.service';

/**
 * The CodeCategoriesComponent class implements the code categories component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'code-categories.component.html',
  styleUrls: ['code-categories.component.css']
})
export class CodeCategoriesComponent extends AdminContainerView implements AfterViewInit {

  dataSource: MatTableDataSource<CodeCategorySummary> = new MatTableDataSource<CodeCategorySummary>();

  displayedColumns = ['id', 'name', 'actions'];

  @HostBinding('class') hostClass = 'flex flex-column flex-fill';

  @ViewChild(MatPaginator, {static: true}) paginator!: MatPaginator;

  @ViewChild(MatSort, {static: true}) sort!: MatSort;

  constructor(private router: Router, private activatedRoute: ActivatedRoute, private codesService: CodesService,
              private dialogService: DialogService, private spinnerService: SpinnerService) {
    super();

    // Set the data source filter
    this.dataSource.filterPredicate =
      (data, filter): boolean => data.id.toLowerCase().includes(filter) || data.name.toLowerCase().includes(filter);
  }

  get title(): string {
    return $localize`:@@codes_code_categories_title:Code Categories`
  }

  applyFilter(filterValue: string): void {
    filterValue = filterValue.trim();
    filterValue = filterValue.toLowerCase();
    this.dataSource.filter = filterValue;
  }

  codesAdministration(codeCategoryId: string): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate([encodeURIComponent(codeCategoryId) + '/codes'], {relativeTo: this.activatedRoute});
  }

  deleteCodeCategory(codeCategoryId: string): void {
    const dialogRef: MatDialogRef<ConfirmationDialogComponent, boolean> = this.dialogService.showConfirmationDialog({
      message: $localize`:@@codes_code_categories_confirm_delete_code_category:Are you sure you want to delete the code category?`
    });

    dialogRef.afterClosed()
    .pipe(first())
    .subscribe((confirmation: boolean | undefined) => {
      if (confirmation === true) {
        this.spinnerService.showSpinner();

        this.codesService.deleteCodeCategory(codeCategoryId)
        .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
        .subscribe(() => {
          this.loadCodeCategorySummaries();
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

  editCodeCategory(codeCategoryId: string): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate([encodeURIComponent(codeCategoryId) + '/edit'], {relativeTo: this.activatedRoute});
  }

  loadCodeCategorySummaries(): void {
    this.spinnerService.showSpinner();

    this.codesService.getCodeCategorySummaries()
    .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
    .subscribe((codeCategorySummaries: CodeCategorySummary[]) => {
      this.dataSource.data = codeCategorySummaries;
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

  newCodeCategory(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['new'], {relativeTo: this.activatedRoute});
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    this.loadCodeCategorySummaries();
  }
}

