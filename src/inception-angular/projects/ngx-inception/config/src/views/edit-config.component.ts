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
} from 'ngx-inception/core';
import {finalize, first} from 'rxjs/operators';
import {Config} from '../services/config';
import {ConfigService} from '../services/config.service';

/**
 * The EditConfigComponent class implements the edit config component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'edit-config.component.html',
  styleUrls: ['edit-config.component.css'],
})
export class EditConfigComponent extends AdminContainerView implements AfterViewInit {

  config?: Config;

  descriptionFormControl: FormControl;

  editConfigForm: FormGroup;

  key: string;

  keyFormControl: FormControl;

  valueFormControl: FormControl;

  constructor(private router: Router, private activatedRoute: ActivatedRoute,
              private configService: ConfigService, private dialogService: DialogService,
              private spinnerService: SpinnerService) {
    super();

    // Retrieve the route parameters
    const key = this.activatedRoute.snapshot.paramMap.get('key');

    if (key == null) {
      throw(new Error('No key route parameter found'));
    }

    this.key = decodeURIComponent(key);

    // Initialise the form controls
    this.descriptionFormControl = new FormControl('', [Validators.maxLength(100)]);
    this.keyFormControl = new FormControl({
      value: '',
      disabled: true
    }, [Validators.required, Validators.maxLength(100)]);
    this.valueFormControl = new FormControl('', [Validators.maxLength(4000)]);

    // Initialise the form
    this.editConfigForm = new FormGroup({
      description: this.descriptionFormControl,
      key: this.keyFormControl,
      value: this.valueFormControl
    });
  }

  get backNavigation(): BackNavigation {
    return new BackNavigation($localize`:@@config_edit_config_back_navigation:Config`,
      ['../..'], {relativeTo: this.activatedRoute});
  }

  get title(): string {
    return $localize`:@@config_edit_config_title:Edit Config`
  }

  cancel(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['../..'], {relativeTo: this.activatedRoute});
  }

  ngAfterViewInit(): void {
    // Retrieve the existing config and initialise the form controls
    this.spinnerService.showSpinner();

    this.configService.getConfig(this.key)
    .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
    .subscribe((config: Config) => {
      this.config = config;

      this.keyFormControl.setValue(config.key);
      this.valueFormControl.setValue(config.value);
      this.descriptionFormControl.setValue(config.description);
    }, (error: Error) => {
      this.spinnerService.hideSpinner();
      // noinspection SuspiciousTypeOfGuard
      if ((error instanceof AccessDeniedError) || (error instanceof ServiceUnavailableError)) {
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
    if (this.config && this.editConfigForm.valid) {
      this.config.description = this.descriptionFormControl.value;
      this.config.value = this.valueFormControl.value;

      this.spinnerService.showSpinner();

      this.configService.saveConfig(this.config)
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
