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
import {Error, HttpError, ProblemDetails} from '@absaoss/ngx-inception/core';

/**
 * The CodeCategoryNotFoundError class holds the information for a code category not found error.
 *
 * @author Marcus Portmann
 */
export class CodeCategoryNotFoundError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/codes/code-category-not-found';

  /**
   * Constructs a new CodeCategoryNotFoundError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@codes_code_category_not_found_error:The code category could not be found.`, cause);
  }
}

/**
 * The CodeNotFoundError class holds the information for a code not found error.
 *
 * @author Marcus Portmann
 */
export class CodeNotFoundError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/codes/code-not-found';

  /**
   * Constructs a new CodeNotFoundError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@codes_code_not_found_error:The code could not be found.`, cause);
  }
}

/**
 * The DuplicateCodeCategoryError class holds the information for a duplicate code category error.
 *
 * @author Marcus Portmann
 */
export class DuplicateCodeCategoryError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/codes/duplicate-code-category';

  /**
   * Constructs a new DuplicateCodeCategoryError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@codes_duplicate_code_category_error:The code category already exists.`, cause);
  }
}

/**
 * The DuplicateCodeError class holds the information for a duplicate code error.
 *
 * @author Marcus Portmann
 */
export class DuplicateCodeError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/codes/duplicate-code';

  /**
   * Constructs a new DuplicateCodeError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@codes_duplicate_code_error:The code already exists.`, cause);
  }
}
