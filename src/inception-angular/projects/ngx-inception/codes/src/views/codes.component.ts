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
  AccessDeniedError, AdminContainerView, BackNavigation, ConfirmationDialogComponent, DialogService,
  Error, InvalidArgumentError, ServiceUnavailableError, SpinnerService
} from '@absaoss/ngx-inception/core';
import {finalize, first} from 'rxjs/operators';
import {Code} from '../services/code';
import {CodesService} from '../services/codes.service';

/**
 * The CodesComponent class implements the codes component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'codes.component.html',
  styleUrls: ['codes.component.css']
})
export class CodesComponent extends AdminContainerView implements AfterViewInit {

  codeCategoryId: string;
  dataSource: MatTableDataSource<Code> = new MatTableDataSource<Code>();
  displayedColumns = ['id', 'name', 'actions'];
  @HostBinding('class') hostClass = 'flex flex-column flex-fill';
  @ViewChild(MatPaginator, {static: true}) paginator!: MatPaginator;

  @ViewChild(MatSort, {static: true}) sort!: MatSort;

  constructor(private router: Router, private activatedRoute: ActivatedRoute, private codesService: CodesService,
              private dialogService: DialogService, private spinnerService: SpinnerService) {
    super();

    // Retrieve the route parameters
    const codeCategoryId = this.activatedRoute.snapshot.paramMap.get('codeCategoryId');

    if (!codeCategoryId) {
      throw(new Error('No codeCategoryId route parameter found'));
    }

    this.codeCategoryId = decodeURIComponent(codeCategoryId);

    // Set the data source filter
    this.dataSource.filterPredicate =
      (data, filter): boolean => data.id.toLowerCase().includes(filter) || data.name.toLowerCase().includes(filter);
  }

  get backNavigation(): BackNavigation {
    return new BackNavigation($localize`:@@codes_codes_back_navigation:Code Categories`,
      ['../../../../../..'], {relativeTo: this.activatedRoute});
  }

  get title(): string {
    return $localize`:@@codes_codes_title:Codes`
  }

  applyFilter(filterValue: string): void {
    filterValue = filterValue.trim();
    filterValue = filterValue.toLowerCase();
    this.dataSource.filter = filterValue;
  }

  deleteCode(codeId: string): void {
    const dialogRef: MatDialogRef<ConfirmationDialogComponent, boolean> = this.dialogService.showConfirmationDialog({
      message: $localize`:@@codes_codes_confirm_delete_code:Are you sure you want to delete the code?`
    });

    dialogRef.afterClosed()
    .pipe(first())
    .subscribe((confirmation: boolean | undefined) => {
      if (confirmation === true) {
        this.spinnerService.showSpinner();

        this.codesService.deleteCode(this.codeCategoryId, codeId)
        .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
        .subscribe(() => {
          this.loadCodes();
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

  editCode(codeId: string): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate([encodeURIComponent(codeId) + '/edit'], {relativeTo: this.activatedRoute});
  }

  loadCodes(): void {
    this.spinnerService.showSpinner();

    this.codesService.getCodesForCodeCategory(this.codeCategoryId)
    .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
    .subscribe((codes: Code[]) => {
      this.dataSource.data = codes;
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

  newCode(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['new'], {relativeTo: this.activatedRoute});
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    this.loadCodes();
  }
}

