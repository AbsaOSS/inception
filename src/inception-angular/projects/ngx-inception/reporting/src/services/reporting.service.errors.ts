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

import {HttpErrorResponse} from '@angular/common/http';
import {Error, HttpError, ProblemDetails} from 'ngx-inception/core';

/**
 * The DuplicateReportDefinitionError class holds the information for a duplicate report definition
 * error.
 *
 * @author Marcus Portmann
 */
export class DuplicateReportDefinitionError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/reporting/duplicate-report-definition';

  /**
   * Constructs a new DuplicateReportDefinitionError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@reporting_duplicate_report_definition_error:The report definition already exists.`, cause);
  }
}

/**
 * The ReportDefinitionNotFoundError class holds the information for a report definition not found
 * error.
 *
 * @author Marcus Portmann
 */
export class ReportDefinitionNotFoundError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/reporting/report-definition-not-found';

  /**
   * Constructs a new ReportDefinitionNotFoundError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@reporting_report_definition_not_found_error:The report definition could not be found.`, cause);
  }
}
