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
import {finalize, first} from 'rxjs/operators';
import {Code} from '../services/code';
import {CodesService} from '../services/codes.service';

/**
 * The NewCodeComponent class implements the new code component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'new-code.component.html',
  styleUrls: ['new-code.component.css'],
})
export class NewCodeComponent extends AdminContainerView implements AfterViewInit {

  code: Code | null = null;

  codeCategoryId: string;

  idFormControl: FormControl;

  nameFormControl: FormControl;

  newCodeForm: FormGroup;

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

    // Initialise the form controls
    this.idFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.nameFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.valueFormControl = new FormControl('', [Validators.required]);

    // Initialise the form
    this.newCodeForm = new FormGroup({
      id: this.idFormControl,
      name: this.nameFormControl,
      value: this.valueFormControl,
    });
  }

  get backNavigation(): BackNavigation {
    return new BackNavigation($localize`:@@codes_new_code_back_navigation:Codes`,
      ['..'], {relativeTo: this.activatedRoute});
  }

  get title(): string {
    return $localize`:@@codes_new_code_title:New Code`
  }

  cancel(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['..'], {relativeTo: this.activatedRoute});
  }

  ngAfterViewInit(): void {
    // Create the new code
    this.code = new Code('', this.codeCategoryId, '', '');
  }

  ok(): void {
    if (this.code && this.newCodeForm.valid) {
      this.code.id = this.idFormControl.value;
      this.code.name = this.nameFormControl.value;
      this.code.value = this.valueFormControl.value;

      this.spinnerService.showSpinner();

      this.codesService.createCode(this.code)
      .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
      .subscribe(() => {
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigate(['..'], {relativeTo: this.activatedRoute});
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
