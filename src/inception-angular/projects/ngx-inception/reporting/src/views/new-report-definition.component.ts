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
  AccessDeniedError, AdminContainerView, BackNavigation, Base64, DialogService, Error,
  FileValidator, InvalidArgumentError, ServiceUnavailableError, SpinnerService
} from '@absaoss/ngx-inception/core';
import {finalize, first} from 'rxjs/operators';
import {ReportDefinition} from '../services/report-definition';
import {ReportingService} from '../services/reporting.service';

/**
 * The NewReportDefinitionComponent class implements the new report definition component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'new-report-definition.component.html',
  styleUrls: ['new-report-definition.component.css'],
})
export class NewReportDefinitionComponent extends AdminContainerView implements AfterViewInit {

  idFormControl: FormControl;

  nameFormControl: FormControl;

  newReportDefinitionForm: FormGroup;

  reportDefinition?: ReportDefinition;

  templateFormControl: FormControl;

  constructor(private router: Router, private activatedRoute: ActivatedRoute,
              private reportingService: ReportingService, private dialogService: DialogService,
              private spinnerService: SpinnerService) {
    super();

    // Initialise the form controls
    this.idFormControl = new FormControl('', [Validators.required]);
    this.nameFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.templateFormControl = new FormControl('',
      [Validators.required, FileValidator.minSize(1), FileValidator.maxSize(ReportingService.MAX_TEMPLATE_SIZE)
      ]);

    // Initialise the form
    this.newReportDefinitionForm = new FormGroup({
      id: this.idFormControl,
      name: this.nameFormControl,
      template: this.templateFormControl
    });
  }

  get backNavigation(): BackNavigation {
    return new BackNavigation($localize`:@@reporting_new_report_definition_back_navigation:Report Definitions`,
      ['..'], {relativeTo: this.activatedRoute});
  }

  get title(): string {
    return $localize`:@@reporting_new_report_definition_title:New Report Definition`
  }

  cancel(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['..'], {relativeTo: this.activatedRoute});
  }

  ngAfterViewInit(): void {
    this.reportDefinition = new ReportDefinition('', '', '');
  }

  ok(): void {
    if (this.reportDefinition && this.newReportDefinitionForm.valid) {

      const fileReader: FileReader = new FileReader();

      fileReader.onloadend = (ev: ProgressEvent) => {
        const template = fileReader.result;

        if (this.reportDefinition && (template instanceof ArrayBuffer)) {

          const base64: string = Base64.encode(template as ArrayBuffer);

          this.reportDefinition.id = this.idFormControl.value;
          this.reportDefinition.name = this.nameFormControl.value;
          this.reportDefinition.template = base64;

          this.spinnerService.showSpinner();

          this.reportingService.createReportDefinition(this.reportDefinition)
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
        } else {
          console.log('Failed to read the template file for the report definition (' + fileReader.result + ')');
        }
      };

      fileReader.readAsArrayBuffer(this.templateFormControl.value[0]);
    }
  }
}
