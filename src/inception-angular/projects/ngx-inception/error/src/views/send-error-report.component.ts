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
  DialogService, Error, InformationDialogComponent, ProblemDetails, SpinnerService
} from '@absaoss/ngx-inception/core';
import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {MatDialogRef} from '@angular/material/dialog';
import {ActivatedRoute, Router} from '@angular/router';
import {first, map} from 'rxjs/operators';
import {ErrorService} from '../services/error.service';

/**
 * The SendErrorReportComponent class implements the send error report component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'send-error-report.component.html'
})
export class SendErrorReportComponent implements OnInit {

  emailFormControl: FormControl;

  error: Error | null = null;

  feedbackFormControl: FormControl;

  messageFormControl: FormControl;

  sendErrorReportForm: FormGroup;

  constructor(private router: Router, private activatedRoute: ActivatedRoute, private dialogService: DialogService,
              private errorService: ErrorService, private spinnerService: SpinnerService) {

    // Initialise the form controls
    this.emailFormControl = new FormControl('', Validators.email);
    this.feedbackFormControl = new FormControl('');
    this.messageFormControl = new FormControl('');

    // Initialise the form
    this.sendErrorReportForm = new FormGroup({
      message: this.messageFormControl,
      email: this.emailFormControl,
      feedback: this.feedbackFormControl
    });
  }

  ngOnInit(): void {
    this.activatedRoute.paramMap
    .pipe(first(), map(() => window.history.state))
    .subscribe((state) => {
      if (state.error) {
        this.error = state.error;

        this.messageFormControl.setValue(state.error.message);

        if (this.error) {
          console.log('Error: ', this.error);

          if (this.error.cause) {
            console.log('Cause: ', this.error.cause);

            if ((this.error.cause as ProblemDetails).stackTrace) {
              console.log('StackTrace: ', (this.error.cause as ProblemDetails).stackTrace);
            }
          }
        }
      } else {
        console.log('No error found, redirecting to the application root');

        // noinspection JSIgnoredPromiseFromCall
        this.router.navigate(['/']);
      }
    });
  }

  sendErrorReport(): void {
    if (this.sendErrorReportForm.valid && this.error) {
      this.spinnerService.showSpinner();

      this.errorService.sendErrorReport(this.error, this.emailFormControl.value, this.feedbackFormControl.value)
      .pipe(first())
      .subscribe(() => {
        this.spinnerService.hideSpinner();

        const dialogRef: MatDialogRef<InformationDialogComponent, boolean> = this.dialogService.showInformationDialog(
          {
            message: 'Your error report was submitted.'
          });

        dialogRef.afterClosed()
        .pipe(first())
        .subscribe(() => {
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigate(['/']);
        });
      }, (error: Error) => {
        this.spinnerService.hideSpinner();

        this.dialogService.showErrorDialog(error);
      });
    }
  }
}
