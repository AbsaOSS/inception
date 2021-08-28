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

import {Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {MatDialogRef} from '@angular/material/dialog';
import {ActivatedRoute, Router} from '@angular/router';
import {
  AccessDeniedError, DialogService, Error, InformationDialogComponent, InvalidArgumentError,
  ServiceUnavailableError, SpinnerService
} from 'ngx-inception/core';
import {SecurityService} from "ngx-inception/security";
import {finalize, first} from 'rxjs/operators';

/**
 * The ForgottenPasswordComponent class implements the forgotten password component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'forgotten-password.component.html'
})
export class ForgottenPasswordComponent {

  forgottenPasswordForm: FormGroup;

  usernameFormControl: FormControl;

  /**
   * Constructs a new ForgottenPasswordComponent.
   *
   * @param router          The router.
   * @param activatedRoute  The activated route.
   * @param dialogService   The dialog service.
   * @param securityService The security service.
   * @param spinnerService  The spinner service.
   */
  constructor(private router: Router, private activatedRoute: ActivatedRoute,
              private dialogService: DialogService, private securityService: SecurityService,
              private spinnerService: SpinnerService) {

    // Initialise the form controls
    this.usernameFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);

    // Initialise the form
    this.forgottenPasswordForm = new FormGroup({
      username: this.usernameFormControl,
    });
  }

  cancel(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['..'], {
      relativeTo: this.activatedRoute
    });
  }

  resetPassword(): void {
    if (this.forgottenPasswordForm.valid) {
      const username = this.usernameFormControl.value;

      const resetPasswordUrl = window.location.href.substr(0, window.location.href.length - this.router.url.length) +
        '/login/reset-password';

      this.spinnerService.showSpinner();

      this.securityService.initiatePasswordReset(username, resetPasswordUrl)
      .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
      .subscribe(() => {
        const dialogRef: MatDialogRef<InformationDialogComponent, boolean> = this.dialogService.showInformationDialog(
          {
            message: 'The password reset process was initiated. Please check your e-mail to proceed.'
          });

        dialogRef.afterClosed()
        .pipe(first())
        .subscribe((confirmation: boolean | undefined) => {
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigate(['..'], {
            relativeTo: this.activatedRoute
          });
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
