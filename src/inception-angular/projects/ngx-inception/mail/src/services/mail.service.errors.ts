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

import {Error, HttpError, ProblemDetails} from '@absaoss/ngx-inception/core';
import {HttpErrorResponse} from '@angular/common/http';

/**
 * The DuplicateMailTemplateError class holds the information for a duplicate mail template
 * error.
 *
 * @author Marcus Portmann
 */
export class DuplicateMailTemplateError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/mail/duplicate-mail-template';

  /**
   * Constructs a new DuplicateMailTemplateError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@mail_duplicate_mail_template_error:The mail template already exists.`, cause);
  }
}

/**
 * The MailTemplateNotFoundError class holds the information for a mail template not found
 * error.
 *
 * @author Marcus Portmann
 */
export class MailTemplateNotFoundError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/mail/mail-template-not-found';

  /**
   * Constructs a new MailTemplateNotFoundError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@mail_mail_template_not_found_error:The mail template could not be found.`, cause);
  }
}

