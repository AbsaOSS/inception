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

import {HttpClient, HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {Inject, Injectable} from '@angular/core';
import {
  AccessDeniedError, CommunicationError, INCEPTION_CONFIG, InceptionConfig, InvalidArgumentError,
  ProblemDetails, ServiceUnavailableError
} from 'ngx-inception/core';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {MailTemplate} from './mail-template';
import {MailTemplateContentType} from './mail-template-content-type';
import {MailTemplateSummary} from './mail-template-summary';
import {DuplicateMailTemplateError, MailTemplateNotFoundError} from './mail.service.errors';

/**
 * The Mail Service implementation.
 *
 * @author Marcus Portmann
 */
@Injectable({
  providedIn: 'root'
})
export class MailService {

  static readonly MAX_TEMPLATE_SIZE: number = 10485760;

  /**
   * Constructs a new MailService.
   *
   * @param config     The Inception configuration.
   * @param httpClient The HTTP client.
   */
  constructor(@Inject(INCEPTION_CONFIG) private config: InceptionConfig, private httpClient: HttpClient) {
    console.log('Initializing the Mail Service');
  }

  static getMailTemplateContentTypeDescription(mailTemplateContentType: MailTemplateContentType): string {
    switch (mailTemplateContentType) {
      case MailTemplateContentType.HTML:
        return $localize`:@@mail_mail_template_content_type_text:HTML`;
      case MailTemplateContentType.Text:
        return $localize`:@@mail_mail_template_content_type_text:Text`;
      default:
        return $localize`:@@mail_mail_template_content_type_unknown:Unknown`;
    }
  }

  /**
   * Create a mail template.
   *
   * @param mailTemplate The mail template to create.
   *
   * @return True if the mail template was created successfully or false otherwise.
   */
  createMailTemplate(mailTemplate: MailTemplate): Observable<boolean> {
    return this.httpClient.post<boolean>(this.config.mailApiUrlPrefix + '/mail-templates', mailTemplate,
      {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, DuplicateMailTemplateError.TYPE)) {
        return throwError(new MailTemplateNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to create the mail template.', httpErrorResponse));
    }));
  }

  /**
   * Delete the mail template.
   *
   * @param mailTemplateId The ID for the mail template.
   *
   * @return True if the mail template was deleted or false otherwise.
   */
  deleteMailTemplate(mailTemplateId: string): Observable<boolean> {
    return this.httpClient.delete<boolean>(
      this.config.mailApiUrlPrefix + '/mail-templates/' + encodeURIComponent(mailTemplateId), {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, MailTemplateNotFoundError.TYPE)) {
        return throwError(new MailTemplateNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to delete the mail template.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the mail template.
   *
   * @param mailTemplateId The ID for the template.
   *
   * @return The mail template.
   */
  getMailTemplate(mailTemplateId: string): Observable<MailTemplate> {
    return this.httpClient.get<MailTemplate>(
      this.config.mailApiUrlPrefix + '/mail-templates/' + encodeURIComponent(mailTemplateId), {reportProgress: true})
    .pipe(map((mailTemplate: MailTemplate) => {
      return mailTemplate;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, MailTemplateNotFoundError.TYPE)) {
        return throwError(new MailTemplateNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the mail template.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the name of the mail template.
   *
   * @param mailTemplateId The ID for the mail template.
   *
   * @return The name of the mail template.
   */
  getMailTemplateName(mailTemplateId: string): Observable<string> {
    return this.httpClient.get<string>(
      this.config.mailApiUrlPrefix + '/mail-templates/' + encodeURIComponent(mailTemplateId) + '/name', {
        reportProgress: true,
      }).pipe(map((mailTemplateName: string) => {
      return mailTemplateName;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, MailTemplateNotFoundError.TYPE)) {
        return throwError(new MailTemplateNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the mail template name.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the summaries for all the mail templates.
   *
   * @return The summaries for all the mail templates.
   */
  getMailTemplateSummaries(): Observable<MailTemplateSummary[]> {
    return this.httpClient.get<MailTemplateSummary[]>(this.config.mailApiUrlPrefix + '/mail-template-summaries',
      {reportProgress: true})
    .pipe(map((mailTemplateSummaries: MailTemplateSummary[]) => {
      return mailTemplateSummaries;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the summaries for the mail templates.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve all the mail templates.
   *
   * @return The mail templates.
   */
  getMailTemplates(): Observable<MailTemplate[]> {
    return this.httpClient.get<MailTemplate[]>(this.config.mailApiUrlPrefix + '/mail-templates', {reportProgress: true})
    .pipe(map((mailTemplates: MailTemplate[]) => {
      return mailTemplates;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the mail templates.', httpErrorResponse));
    }));
  }

  /**
   * Update the mail template.
   *
   * @param mailTemplate The mail template
   *
   * @return True if the mail template was updated successfully or false otherwise.
   */
  updateMailTemplate(mailTemplate: MailTemplate): Observable<boolean> {
    return this.httpClient.put<boolean>(
      this.config.mailApiUrlPrefix + '/mail-templates/' + encodeURIComponent(mailTemplate.id), mailTemplate,
      {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, MailTemplateNotFoundError.TYPE)) {
        return throwError(new MailTemplateNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to update the mail template.', httpErrorResponse));
    }));
  }
}
