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
import {Code} from '../services/code';
import {CodesService} from '../services/codes.service';

/**
 * The EditCodeComponent class implements the edit code category component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'edit-code.component.html',
  styleUrls: ['edit-code.component.css'],
})
export class EditCodeComponent extends AdminContainerView implements AfterViewInit {

  code?: Code;

  codeCategoryId: string;

  codeId: string;

  editCodeForm: FormGroup;

  idFormControl: FormControl;

  nameFormControl: FormControl;

  valueFormControl: FormControl;

  constructor(private router: Router, private activatedRoute: ActivatedRoute, private codesService: CodesService,
              private dialogService: DialogService, private spinnerService: SpinnerService) {
    super();

    // Retrieve the route parameters
    const codeCategoryId = this.activatedRoute.snapshot.paramMap.get('codeCategoryId');

    if (!codeCategoryId) {
      throw(new Error('No codeCategoryId route parameter found'));
    }

    this.codeCategoryId = decodeURIComponent(codeCategoryId);

    const codeId = this.activatedRoute.snapshot.paramMap.get('codeId');

    if (!codeId) {
      throw(new Error('No codeId route parameter found'));
    }

    this.codeId = decodeURIComponent(codeId);

    // Initialise the form controls
    this.idFormControl = new FormControl({
      value: '',
      disabled: true
    }, [Validators.required, Validators.maxLength(100)]);
    this.nameFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.valueFormControl = new FormControl('', [Validators.required]);

    // Initialise the form
    this.editCodeForm = new FormGroup({
      id: this.idFormControl,
      name: this.nameFormControl,
      value: this.valueFormControl
    });
  }

  get backNavigation(): BackNavigation {
    return new BackNavigation($localize`:@@codes_edit_code_back_navigation:Codes`,
      ['../..'], {relativeTo: this.activatedRoute});
  }

  get title(): string {
    return $localize`:@@codes_edit_code_title:Edit Code`
  }

  cancel(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['../..'], {relativeTo: this.activatedRoute});
  }

  ngAfterViewInit(): void {
    // Retrieve the existing code and initialise the form controls
    this.spinnerService.showSpinner();

    this.codesService.getCode(this.codeCategoryId, this.codeId)
    .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
    .subscribe((code: Code) => {
      this.code = code;
      this.idFormControl.setValue(code.id);
      this.nameFormControl.setValue(code.name);
      this.valueFormControl.setValue(code.value);
    }, (error: Error) => {
      // noinspection SuspiciousTypeOfGuard
      if ((error instanceof AccessDeniedError) || (error instanceof InvalidArgumentError) ||
        (error instanceof ServiceUnavailableError)) {
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigateByUrl('/error/send-error-report', {state: {error}});
      } else {
        this.dialogService.showErrorDialog(error).afterClosed()
        .pipe(first())
        .subscribe(() => {
          this.router.navigate(['../..'], {relativeTo: this.activatedRoute});
        });
      }
    });
  }

  ok(): void {
    if (this.code && this.editCodeForm.valid) {
      this.code.name = this.nameFormControl.value;
      this.code.value = this.valueFormControl.value;

      this.spinnerService.showSpinner();

      this.codesService.updateCode(this.code)
      .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
      .subscribe(() => {
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigate(['../..'], {relativeTo: this.activatedRoute});
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
