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
import {ActivatedRoute, Router} from '@angular/router';
import {combineLatest} from 'rxjs';
import {finalize, first} from 'rxjs/operators';
import {SecurityService} from '../services/security.service';
import {User} from '../services/user';
import {UserDirectoryCapabilities} from '../services/user-directory-capabilities';

/**
 * The ResetUserPasswordComponent class implements the reset user password component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'reset-user-password.component.html',
  styleUrls: ['reset-user-password.component.css'],
})
export class ResetUserPasswordComponent extends AdminContainerView implements AfterViewInit {

  confirmPasswordFormControl: FormControl;

  expirePasswordFormControl: FormControl;

  lockUserFormControl: FormControl;

  nameFormControl: FormControl;

  passwordFormControl: FormControl;

  resetPasswordHistoryFormControl: FormControl;

  resetUserPasswordForm: FormGroup;

  userDirectoryCapabilities?: UserDirectoryCapabilities;

  userDirectoryId: string;

  username: string;

  usernameFormControl: FormControl;

  constructor(private router: Router, private activatedRoute: ActivatedRoute, private securityService: SecurityService,
              private dialogService: DialogService, private spinnerService: SpinnerService) {
    super();

    // Retrieve the route parameters
    const userDirectoryId = this.activatedRoute.snapshot.paramMap.get('userDirectoryId');

    if (!userDirectoryId) {
      throw(new Error('No userDirectoryId route parameter found'));
    }

    this.userDirectoryId = decodeURIComponent(userDirectoryId);

    const username = this.activatedRoute.snapshot.paramMap.get('username');

    if (!username) {
      throw(new Error('No username route parameter found'));
    }

    this.username = decodeURIComponent(username);

    // Initialise the form controls
    this.confirmPasswordFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.expirePasswordFormControl = new FormControl(false);
    this.nameFormControl = new FormControl({
      value: '',
      disabled: true
    });
    this.lockUserFormControl = new FormControl(false);
    this.passwordFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.resetPasswordHistoryFormControl = new FormControl(false);
    this.usernameFormControl = new FormControl({
      value: '',
      disabled: true
    });

    // Initialise the form
    this.resetUserPasswordForm = new FormGroup({
      confirmPassword: this.confirmPasswordFormControl,
      name: this.nameFormControl,
      password: this.passwordFormControl,
      username: this.usernameFormControl
    });
  }

  get backNavigation(): BackNavigation {
    return new BackNavigation($localize`:@@security_reset_user_password_back_navigation:Users`,
      ['../../..'], {
        relativeTo: this.activatedRoute,
        state: {userDirectoryId: this.userDirectoryId}
      });
  }

  get title(): string {
    return $localize`:@@security_reset_user_password_title:Reset User Password`
  }

  cancel(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['../../..'], {
      relativeTo: this.activatedRoute,
      state: {userDirectoryId: this.userDirectoryId}
    });
  }

  ngAfterViewInit(): void {
    // Retrieve the existing user and initialise the form fields
    this.spinnerService.showSpinner();

    combineLatest([this.securityService.getUserDirectoryCapabilities(this.userDirectoryId),
      this.securityService.getUser(this.userDirectoryId, this.username)
    ])
    .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
    .subscribe((results: [UserDirectoryCapabilities, User]) => {
      this.userDirectoryCapabilities = results[0];

      this.nameFormControl.setValue(results[1].name);
      this.usernameFormControl.setValue(results[1].username);

      if (this.userDirectoryCapabilities.supportsPasswordExpiry) {
        this.resetUserPasswordForm.addControl('expirePassword', this.expirePasswordFormControl);
      }

      if (this.userDirectoryCapabilities.supportsUserLocks) {
        this.resetUserPasswordForm.addControl('lockUser', this.lockUserFormControl);
      }

      if (this.userDirectoryCapabilities.supportsPasswordHistory) {
        this.resetUserPasswordForm.addControl('resetPasswordHistory', this.resetPasswordHistoryFormControl);
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

  ok(): void {
    if (this.resetUserPasswordForm.valid) {
      // Check that the password and confirmation password match
      if (this.passwordFormControl.value !== this.confirmPasswordFormControl.value) {
        this.dialogService.showErrorDialog(new Error('The passwords do not match.'));

        return;
      }

      const password = this.passwordFormControl.value;

      this.spinnerService.showSpinner();

      this.securityService.adminChangePassword(this.userDirectoryId, this.username, password,
        this.resetUserPasswordForm.contains('expirePassword') ? this.expirePasswordFormControl.value : false,
        this.resetUserPasswordForm.contains('lockUser') ? this.lockUserFormControl.value : false,
        this.resetUserPasswordForm.contains('resetPasswordHistory') ? this.resetPasswordHistoryFormControl.value :
          false)
      .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
      .subscribe(() => {
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigate(['../../..'], {
          relativeTo: this.activatedRoute,
          state: {userDirectoryId: this.userDirectoryId}
        });
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
  }
}
