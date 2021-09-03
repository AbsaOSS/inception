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
import {MatSort} from '@angular/material/sort';
import {ActivatedRoute, Router} from '@angular/router';
import {
  AccessDeniedError, AdminContainerView, BackNavigation, ConfirmationDialogComponent, DialogService,
  Error, InvalidArgumentError, ServiceUnavailableError, SortDirection, SpinnerService,
  TableFilterComponent
} from '@absaoss/ngx-inception/core';
import {merge, Subscription} from 'rxjs';
import {finalize, first, tap} from 'rxjs/operators';
import {GroupMember} from '../services/group-member';
import {GroupMemberType} from '../services/group-member-type';
import {GroupMemberDatasource} from '../services/group-member.datasource';
import {SecurityService} from '../services/security.service';

/**
 * The GroupMembersComponent class implements the group members component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'group-members.component.html',
  styleUrls: ['group-members.component.css']
})
export class GroupMembersComponent extends AdminContainerView implements AfterViewInit, OnDestroy {

  dataSource: GroupMemberDatasource;
  displayedColumns = ['memberName', 'memberType', 'actions'];
  groupName: string;
  @HostBinding('class') hostClass = 'flex flex-column flex-fill';
  @ViewChild(MatPaginator, {static: true}) paginator!: MatPaginator;
  @ViewChild(MatSort, {static: true}) sort!: MatSort;
  @ViewChild(TableFilterComponent, {static: true}) tableFilter!: TableFilterComponent;
  userDirectoryId: string;
  private subscriptions: Subscription = new Subscription();

  constructor(private router: Router, private activatedRoute: ActivatedRoute, private securityService: SecurityService,
              private dialogService: DialogService, private spinnerService: SpinnerService) {
    super();

    // Retrieve the route parameters
    const userDirectoryId = this.activatedRoute.snapshot.paramMap.get('userDirectoryId');

    if (!userDirectoryId) {
      throw(new Error('No userDirectoryId route parameter found'));
    }

    this.userDirectoryId = decodeURIComponent(userDirectoryId);

    const groupName = this.activatedRoute.snapshot.paramMap.get('groupName');

    if (!groupName) {
      throw(new Error('No groupName route parameter found'));
    }

    this.groupName = decodeURIComponent(groupName);

    this.dataSource = new GroupMemberDatasource(this.securityService);
  }

  get backNavigation(): BackNavigation {
    return new BackNavigation($localize`:@@security_group_members_back_navigation:Back`,
      ['../../..'], {
        relativeTo: this.activatedRoute,
        state: {userDirectoryId: this.userDirectoryId}
      });
  }

  get title(): string {
    return $localize`:@@security_group_members_title:Group Members`
  }

  addMemberToGroup(): void {
    // // noinspection JSIgnoredPromiseFromCall
    // this.router.navigate(['new'], {relativeTo: this.activatedRoute});
  }

  groupMemberTypeName(groupMemberType: GroupMemberType): string {
    if (groupMemberType === GroupMemberType.User) {
      return 'User';
    } else if (groupMemberType === GroupMemberType.Group) {
      return 'Group';
    } else {
      return 'Unknown';
    }
  }

  loadGroupMembers(): void {
    let filter = '';

    if (!!this.tableFilter.filter) {
      filter = this.tableFilter.filter;
      filter = filter.trim();
      filter = filter.toLowerCase();
    }

    const sortDirection = this.sort.direction === 'asc' ? SortDirection.Ascending : SortDirection.Descending;

    this.dataSource.load(this.userDirectoryId, this.groupName, filter, sortDirection, this.paginator.pageIndex,
      this.paginator.pageSize);
  }

  ngAfterViewInit(): void {
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
      this.loadGroupMembers();
    })).subscribe());

    this.loadGroupMembers();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  removeMemberFromGroup(groupMember: GroupMember): void {
    const dialogRef: MatDialogRef<ConfirmationDialogComponent, boolean> = this.dialogService.showConfirmationDialog({
      message: $localize`:@@security_group_members_confirm_remove_member_from_group:Are you sure you want to remove the group member from the group?`
    });

    dialogRef.afterClosed()
    .pipe(first())
    .subscribe((confirmation: boolean | undefined) => {
      if (confirmation === true) {
        this.spinnerService.showSpinner();

        this.securityService.removeMemberFromGroup(this.userDirectoryId, this.groupName, groupMember.memberType,
          groupMember.memberName)
        .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
        .subscribe(() => {
          this.loadGroupMembers();
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
}

