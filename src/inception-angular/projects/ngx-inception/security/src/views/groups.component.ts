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

import {AfterViewInit, Component, HostBinding, OnDestroy, ViewChild} from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';
import {MatPaginator} from '@angular/material/paginator';
import {MatSelect, MatSelectChange} from '@angular/material/select';
import {MatSort} from '@angular/material/sort';
import {ActivatedRoute, Router} from '@angular/router';
import {
  AccessDeniedError, AdminContainerView, ConfirmationDialogComponent, DialogService, Error,
  InvalidArgumentError, ServiceUnavailableError, Session, SessionService, SortDirection,
  SpinnerService, TableFilterComponent
} from '@absaoss/ngx-inception/core';
import {BehaviorSubject, merge, Observable, ReplaySubject, Subject, Subscription} from 'rxjs';
import {finalize, first, map, tap} from 'rxjs/operators';
import {GroupDatasource} from '../services/group.datasource';
import {SecurityService} from '../services/security.service';
import {UserDirectoryCapabilities} from '../services/user-directory-capabilities';
import {UserDirectorySummary} from '../services/user-directory-summary';

/**
 * The GroupsComponent class implements the groups component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'groups.component.html',
  styleUrls: ['groups.component.css']
})
export class GroupsComponent extends AdminContainerView implements AfterViewInit, OnDestroy {

  dataSource: GroupDatasource;
  displayedColumns = ['name', 'actions'];
  @HostBinding('class') hostClass = 'flex flex-column flex-fill';
  @ViewChild(MatPaginator, {static: true}) paginator!: MatPaginator;
  @ViewChild(MatSort, {static: true}) sort!: MatSort;
  @ViewChild(TableFilterComponent, {static: true}) tableFilter!: TableFilterComponent;
  userDirectories: UserDirectorySummary[] = [];
  userDirectoryCapabilities$: Subject<UserDirectoryCapabilities> = new ReplaySubject<UserDirectoryCapabilities>();
  userDirectoryId$: BehaviorSubject<string> = new BehaviorSubject<string>('');
  @ViewChild('userDirectorySelect', {static: true}) userDirectorySelect!: MatSelect;
  private subscriptions: Subscription = new Subscription();

  constructor(private router: Router, private activatedRoute: ActivatedRoute,
              private securityService: SecurityService, private sessionService: SessionService,
              private dialogService: DialogService, private spinnerService: SpinnerService) {
    super();

    this.dataSource = new GroupDatasource(this.securityService);
  }

  get enableActionsMenu$(): Observable<boolean> {
    return this.userDirectoryCapabilities$.pipe(map((userDirectoryCapabilities: UserDirectoryCapabilities) => {
      return userDirectoryCapabilities.supportsGroupAdministration;
    }));
  }

  get enableNewButton$(): Observable<boolean> {
    return this.userDirectoryCapabilities$.pipe(map((userDirectoryCapabilities: UserDirectoryCapabilities) => {
      return userDirectoryCapabilities.supportsGroupAdministration;
    }));
  }

  get title(): string {
    return $localize`:@@security_groups_title:Groups`
  }

  // noinspection JSUnusedLocalSymbols
  deleteGroup(groupName: string): void {
    // noinspection JSUnusedLocalSymbols
    const dialogRef: MatDialogRef<ConfirmationDialogComponent, boolean> = this.dialogService.showConfirmationDialog({
      message: $localize`:@@security_groups_confirm_delete_group:Are you sure you want to delete the group?`
    });

    dialogRef.afterClosed()
    .pipe(first())
    .subscribe((confirmation: boolean | undefined) => {
      if (confirmation === true) {
        this.spinnerService.showSpinner();

        this.securityService.deleteGroup(this.userDirectoryId$.value, groupName)
        .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
        .subscribe(() => {
          this.tableFilter.reset(false);

          this.paginator.pageIndex = 0;

          this.sort.active = '';
          this.sort.direction = 'asc' as SortDirection;

          this.loadGroups();
        }, (error: Error) => {
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

  editGroup(groupName: string): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate([this.userDirectoryId$.value + '/' + encodeURIComponent(groupName) + '/edit'],
      {relativeTo: this.activatedRoute});
  }

  groupMembers(groupName: string): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate([this.userDirectoryId$.value + '/' + encodeURIComponent(groupName) + '/members'],
      {relativeTo: this.activatedRoute});
  }

  groupRoles(groupName: string): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate([this.userDirectoryId$.value + '/' + encodeURIComponent(groupName) + '/roles'],
      {relativeTo: this.activatedRoute});
  }

  loadGroups(): void {
    let filter = '';

    if (!!this.tableFilter.filter) {
      filter = this.tableFilter.filter;
      filter = filter.trim();
      filter = filter.toLowerCase();
    }

    const sortDirection = this.sort.direction === 'asc' ? SortDirection.Ascending : SortDirection.Descending;

    if (!!this.userDirectoryId$.value) {
      this.dataSource.load(this.userDirectoryId$.value, filter, sortDirection, this.paginator.pageIndex,
        this.paginator.pageSize);
    } else {
      this.dataSource.clear();
    }
  }

  newGroup(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate([this.userDirectoryId$.value + '/new'], {relativeTo: this.activatedRoute});
  }

  ngAfterViewInit(): void {
    this.subscriptions.add(this.userDirectoryId$.subscribe((userDirectoryId: string) => {
      if (!!userDirectoryId) {
        this.tableFilter.reset(false);

        this.paginator.pageIndex = 0;

        this.sort.active = 'name';
        this.sort.direction = 'asc' as SortDirection;

        this.spinnerService.showSpinner();

        this.securityService.getUserDirectoryCapabilities(userDirectoryId)
        .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
        .subscribe((userDirectoryCapabilities: UserDirectoryCapabilities) => {
          this.userDirectoryCapabilities$.next(userDirectoryCapabilities);

          this.loadGroups();
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
    }));

    this.subscriptions.add(this.dataSource.loading$.subscribe((next: boolean) => {
      if (next) {
        this.spinnerService.showSpinner();
      } else {
        this.spinnerService.hideSpinner();
      }
    }, (error: Error) => {
      // noinspection SuspiciousTypeOfGuard
      if ((error instanceof AccessDeniedError) || (error instanceof InvalidArgumentError) ||
        (error instanceof ServiceUnavailableError)) {
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigateByUrl('/error/send-error-report', {state: {error}});
      } else {
        this.dialogService.showErrorDialog(error);
      }
    }));

    this.subscriptions.add(this.userDirectorySelect.selectionChange.subscribe((matSelectChange: MatSelectChange) => {
      this.userDirectoryId$.next(matSelectChange.value);
    }));

    this.subscriptions.add(this.sort.sortChange.subscribe(() => {
      if (this.paginator) {
        this.paginator.pageIndex = 0;
      }
    }));

    this.subscriptions.add(this.tableFilter.changed.subscribe(() => {
      if (this.paginator) {
        this.paginator.pageIndex = 0;
      }
    }));

    this.subscriptions.add(merge(this.sort.sortChange, this.tableFilter.changed, this.paginator.page)
    .pipe(tap(() => {
      this.loadGroups();
    })).subscribe());

    this.sessionService.session$.pipe(first()).subscribe((session: Session | null) => {
      if (session && session.tenantId) {
        this.spinnerService.showSpinner();

        this.securityService.getUserDirectorySummariesForTenant(session.tenantId)
        .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
        .subscribe((userDirectories: UserDirectorySummary[]) => {
          this.userDirectories = userDirectories;

          // If we only have one user directory then select it
          if (this.userDirectories.length === 1) {
            this.userDirectoryId$.next(this.userDirectories[0].id);
          } else {
            /*
             * If a user directory ID has been passed in then select the appropriate user
             * directory if it exists.
             */
            this.activatedRoute.paramMap
            .pipe(first(), map(() => window.history.state))
            .subscribe((state) => {
              if (state.userDirectoryId) {
                for (const userDirectory of userDirectories) {
                  if (userDirectory.id === state.userDirectoryId) {
                    this.userDirectorySelect.value = userDirectory.id;
                    this.userDirectoryId$.next(userDirectory.id);
                    break;
                  }
                }
              }
            });
          }
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

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
