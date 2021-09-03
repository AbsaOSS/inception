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
import {finalize, first} from 'rxjs/operators';
import {Group} from '../services/group';
import {SecurityService} from '../services/security.service';
import {UserDirectoryCapabilities} from '../services/user-directory-capabilities';

/**
 * The NewGroupComponent class implements the new group component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'new-group.component.html',
  styleUrls: ['new-group.component.css'],
})
export class NewGroupComponent extends AdminContainerView implements AfterViewInit {

  descriptionFormControl: FormControl;

  group?: Group;

  nameFormControl: FormControl;

  newGroupForm: FormGroup;

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

    // Initialise the form controls
    this.descriptionFormControl = new FormControl('', [Validators.maxLength(100)]);
    this.nameFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);

    // Initialise the form
    this.newGroupForm = new FormGroup({
      description: this.descriptionFormControl,
      name: this.nameFormControl,
    });
  }

  get backNavigation(): BackNavigation {
    return new BackNavigation($localize`:@@security_new_group_back_navigation:Groups`,
      ['../..'], {
        relativeTo: this.activatedRoute,
        state: {userDirectoryId: this.userDirectoryId}
      });
  }

  get title(): string {
    return $localize`:@@security_new_group_title:New Group`
  }

  cancel(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['../..'], {
      relativeTo: this.activatedRoute,
      state: {userDirectoryId: this.userDirectoryId}
    });
  }

  ngAfterViewInit(): void {
    // Retrieve the existing user and initialise the form fields
    this.spinnerService.showSpinner();

    this.securityService.getUserDirectoryCapabilities(this.userDirectoryId)
    .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
    .subscribe((userDirectoryCapabilities: UserDirectoryCapabilities) => {
      this.userDirectoryCapabilities = userDirectoryCapabilities;

      this.group = new Group(this.userDirectoryId, '', '');
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
    if (this.group && this.newGroupForm.valid) {

      this.group.name = this.nameFormControl.value;
      this.group.description = this.descriptionFormControl.value;

      this.spinnerService.showSpinner();

      this.securityService.createGroup(this.group)
      .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
      .subscribe(() => {
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigate(['../..'], {
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
