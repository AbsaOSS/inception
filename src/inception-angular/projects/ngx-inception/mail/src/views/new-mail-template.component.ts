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
  AccessDeniedError, AdminContainerView, BackNavigation, Base64, DialogService, Error,
  FileValidator, InvalidArgumentError, ServiceUnavailableError, SpinnerService
} from '@absaoss/ngx-inception/core';
import {AfterViewInit, Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {finalize, first} from 'rxjs/operators';
import {MailTemplate} from '../services/mail-template';
import {MailTemplateContentType} from '../services/mail-template-content-type';
import {MailService} from '../services/mail.service';

/**
 * The NewMailTemplateComponent class implements the new mail template component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'new-mail-template.component.html',
  styleUrls: ['new-mail-template.component.css'],
})
export class NewMailTemplateComponent extends AdminContainerView implements AfterViewInit {

  MailTemplateContentType = MailTemplateContentType;

  contentTypeFormControl: FormControl;

  contentTypes: MailTemplateContentType[] = [MailTemplateContentType.Text,
    MailTemplateContentType.HTML];

  getMailTemplateContentTypeDescription = MailService.getMailTemplateContentTypeDescription;

  idFormControl: FormControl;

  mailTemplate: MailTemplate | null = null;

  nameFormControl: FormControl;

  newMailTemplateForm: FormGroup;

  templateFormControl: FormControl;

  constructor(private router: Router, private activatedRoute: ActivatedRoute, private mailService: MailService,
              private dialogService: DialogService, private spinnerService: SpinnerService) {
    super();

    // Initialise the form controls
    this.contentTypeFormControl = new FormControl('', [Validators.required]);
    this.idFormControl = new FormControl('', [Validators.required]);
    this.nameFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.templateFormControl = new FormControl('',
      [Validators.required, FileValidator.minSize(1), FileValidator.maxSize(MailService.MAX_TEMPLATE_SIZE)]);

    // Initialise the form
    this.newMailTemplateForm = new FormGroup({
      contentType: this.contentTypeFormControl,
      id: this.idFormControl,
      name: this.nameFormControl,
      template: this.templateFormControl
    });
  }

  get backNavigation(): BackNavigation {
    return new BackNavigation($localize`:@@mail_new_mail_template_back_navigation:Mail Templates`,
      ['..'], {relativeTo: this.activatedRoute});
  }

  get title(): string {
    return $localize`:@@mail_new_mail_template_title:New Mail Template`
  }

  cancel(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['..'], {relativeTo: this.activatedRoute});
  }

  ngAfterViewInit(): void {
    this.mailTemplate = new MailTemplate('', '', MailTemplateContentType.Text, '');
  }

  ok(): void {
    if (this.mailTemplate && this.newMailTemplateForm.valid) {

      const fileReader: FileReader = new FileReader();

      fileReader.onloadend = (ev: ProgressEvent) => {
        const template = fileReader.result;

        if (this.mailTemplate && (template instanceof ArrayBuffer)) {

          const base64: string = Base64.encode(template as ArrayBuffer);

          this.mailTemplate.id = this.idFormControl.value;
          this.mailTemplate.name = this.nameFormControl.value;
          this.mailTemplate.contentType = this.contentTypeFormControl.value;
          this.mailTemplate.template = base64;

          this.spinnerService.showSpinner();

          this.mailService.createMailTemplate(this.mailTemplate)
          .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
          .subscribe(() => {
            // noinspection JSIgnoredPromiseFromCall
            this.router.navigate(['..'], {relativeTo: this.activatedRoute});
          }, (error: Error) => {
            // noinspection SuspiciousTypeOfGuard
            if ((error instanceof AccessDeniedError) || (error instanceof InvalidArgumentError)
              || (error instanceof ServiceUnavailableError)) {
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
