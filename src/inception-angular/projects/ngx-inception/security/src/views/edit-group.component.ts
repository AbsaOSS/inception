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

import {AfterViewInit, Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {
  AccessDeniedError, AdminContainerView, BackNavigation, DialogService, Error, InvalidArgumentError,
  ServiceUnavailableError, SpinnerService
} from '@absaoss/ngx-inception/core';
import {combineLatest} from 'rxjs';
import {finalize, first} from 'rxjs/operators';
import {Group} from '../services/group';
import {SecurityService} from '../services/security.service';
import {UserDirectoryCapabilities} from '../services/user-directory-capabilities';

/**
 * The EditGroupComponent class implements the edit group component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'edit-group.component.html',
  styleUrls: ['edit-group.component.css'],
})
export class EditGroupComponent extends AdminContainerView implements AfterViewInit {

  descriptionFormControl: FormControl;

  editGroupForm: FormGroup;

  group?: Group;

  groupName: string;

  nameFormControl: FormControl;

  userDirectoryCapabilities?: UserDirectoryCapabilities;

  userDirectoryId: string;

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

    // Initialise the form controls
    this.descriptionFormControl = new FormControl('', [Validators.maxLength(100)]);
    this.nameFormControl = new FormControl({
      value: '',
      disabled: true
    });

    // Initialise the form
    this.editGroupForm = new FormGroup({
      description: this.descriptionFormControl,
      name: this.nameFormControl
    });
  }

  get backNavigation(): BackNavigation {
    return new BackNavigation($localize`:@@security_edit_group_back_navigation:Groups`,
      ['../../..'], {
        relativeTo: this.activatedRoute,
        state: {userDirectoryId: this.userDirectoryId}
      });
  }

  get title(): string {
    return $localize`:@@security_edit_group_title:Edit Group`
  }

  cancel(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['../../..'], {
      relativeTo: this.activatedRoute,
      state: {userDirectoryId: this.userDirectoryId}
    });
  }

  ngAfterViewInit(): void {
    // Retrieve the existing group and initialise the form fields
    this.spinnerService.showSpinner();

    combineLatest([this.securityService.getUserDirectoryCapabilities(this.userDirectoryId),
      this.securityService.getGroup(this.userDirectoryId, this.groupName)
    ])
    .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
    .subscribe((results: [UserDirectoryCapabilities, Group]) => {
      this.userDirectoryCapabilities = results[0];

      this.group = results[1];

      this.descriptionFormControl.setValue(results[1].description);
      this.nameFormControl.setValue(results[1].name);
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

  ok(): void {
    if (this.group && this.editGroupForm.valid) {
      this.group.description = this.descriptionFormControl.value;

      this.spinnerService.showSpinner();

      this.securityService.updateGroup(this.group)
      .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
      .subscribe(() => {
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigate(['../../..'], {
          relativeTo: this.activatedRoute,
          state: {userDirectoryId: this.userDirectoryId}
        });
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
