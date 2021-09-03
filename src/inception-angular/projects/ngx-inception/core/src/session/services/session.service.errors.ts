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
import {Error, HttpError, ProblemDetails} from '../../errors';

/**
 * The LoginError class holds the information for a login error.
 *
 * @author Marcus Portmann
 */
export class LoginError extends Error {

  /**
   * Constructs a new LoginError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@session_login_error:Incorrect username or password.`, cause);
  }
}

/**
 * The PasswordExpiredError class holds the information for a password expired error.
 *
 * @author Marcus Portmann
 */
export class PasswordExpiredError extends Error {

  /**
   * Constructs a new PasswordExpiredError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@session_password_expired_error:The password has expired.`, cause);
  }
}

/**
 * The UserLockedError class holds the information for a user locked error.
 *
 * @author Marcus Portmann
 */
export class UserLockedError extends Error {

  /**
   * Constructs a new UserLockedError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@security_user_locked_error:The user has exceeded the maximum number of failed password attempts and has been locked.`, cause);
  }
}
