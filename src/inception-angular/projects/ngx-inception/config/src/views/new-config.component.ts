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
import {first} from 'rxjs/operators';
import {Config} from '../services/config';
import {ConfigService} from '../services/config.service';

/**
 * The NewConfigComponent class implements the new config component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'new-config.component.html',
  styleUrls: ['new-config.component.css'],
})
export class NewConfigComponent extends AdminContainerView implements AfterViewInit {

  config?: Config;

  descriptionFormControl: FormControl;

  keyFormControl: FormControl;

  newConfigForm: FormGroup;

  valueFormControl: FormControl;

  constructor(private router: Router, private activatedRoute: ActivatedRoute,
              private configService: ConfigService, private dialogService: DialogService,
              private spinnerService: SpinnerService) {
    super();

    // Initialise the form controls
    this.descriptionFormControl = new FormControl('', [Validators.maxLength(100)]);
    this.keyFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.valueFormControl = new FormControl('', [Validators.maxLength(4000)]);

    // Initialise the form
    this.newConfigForm = new FormGroup({
      description: this.descriptionFormControl,
      key: this.keyFormControl,
      value: this.valueFormControl
    });
  }

  get backNavigation(): BackNavigation {
    return new BackNavigation($localize`:@@config_new_config_back_navigation:Config`,
      ['..'], {relativeTo: this.activatedRoute});
  }

  get title(): string {
    return $localize`:@@config_new_config_title:New Config`
  }

  cancel(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['..'], {relativeTo: this.activatedRoute});
  }

  ngAfterViewInit(): void {
    this.config = new Config('', '', '');
  }

  ok(): void {
    if (this.config && this.newConfigForm.valid) {
      this.config.description = this.descriptionFormControl.value;
      this.config.key = this.keyFormControl.value;
      this.config.value = this.valueFormControl.value;

      this.spinnerService.showSpinner();

      this.configService.saveConfig(this.config)
      .pipe(first())
      .subscribe(() => {
        this.spinnerService.hideSpinner();

        // noinspection JSIgnoredPromiseFromCall
        this.router.navigate(['..'], {relativeTo: this.activatedRoute});
      }, (error: Error) => {
        this.spinnerService.hideSpinner();
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
