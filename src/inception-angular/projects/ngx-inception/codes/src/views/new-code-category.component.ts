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
import {CodeCategory} from '../services/code-category';
import {CodesService} from '../services/codes.service';

/**
 * The NewCodeCategoryComponent class implements the new code category component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'new-code-category.component.html',
  styleUrls: ['new-code-category.component.css'],
})
export class NewCodeCategoryComponent extends AdminContainerView implements AfterViewInit {

  codeCategory?: CodeCategory;

  dataFormControl: FormControl;

  idFormControl: FormControl;

  nameFormControl: FormControl;

  newCodeCategoryForm: FormGroup;

  constructor(private router: Router, private activatedRoute: ActivatedRoute, private codesService: CodesService,
              private dialogService: DialogService, private spinnerService: SpinnerService) {
    super();

    // Initialise the form controls
    this.dataFormControl = new FormControl('');
    this.idFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.nameFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);

    // Initialise the form
    this.newCodeCategoryForm = new FormGroup({
      data: this.dataFormControl,
      id: this.idFormControl,
      name: this.nameFormControl
    });
  }

  get backNavigation(): BackNavigation {
    return new BackNavigation($localize`:@@codes_new_code_category_back_navigation:Code Categories`,
      ['..'], {relativeTo: this.activatedRoute});
  }

  get title(): string {
    return $localize`:@@codes_new_code_category_title:New Code Category`
  }

  cancel(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['..'], {relativeTo: this.activatedRoute});
  }

  ngAfterViewInit(): void {
    this.codeCategory = new CodeCategory('', '');
  }

  ok(): void {
    if (this.codeCategory && this.newCodeCategoryForm.valid) {
      const data = this.dataFormControl.value;

      this.codeCategory.id = this.idFormControl.value;
      this.codeCategory.name = this.nameFormControl.value;
      this.codeCategory.data = (!!data) ? data : null;

      this.spinnerService.showSpinner();

      this.codesService.createCodeCategory(this.codeCategory)
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
