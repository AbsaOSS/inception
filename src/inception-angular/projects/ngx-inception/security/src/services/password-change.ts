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

import {PasswordChangeReason} from './password-change-reason';

/**
 * The PasswordChange class holds the information for a password change.
 *
 * @author Marcus Portmann
 */
export class PasswordChange {

  /**
   * Expire the user's password when performing an administrative password change.
   */
  expirePassword: boolean | null = null;

  /**
   * Lock the user when performing an administrative password change.
   */
  lockUser: boolean | null = null;

  /**
   * The new password.
   */
  newPassword: string;

  /**
   * The password for the user that is used to authorise the operation when performing a user
   * password change.
   */
  password: string | null = null;

  /**
   * The reason for changing the password.
   */
  reason: PasswordChangeReason;

  /**
   * Reset the user's password history when performing an administrative password change.
   */
  resetPasswordHistory: boolean | null = null;

  /**
   * The security code when performing a forgotten password change.
   */
  securityCode: string | null = null;

  /**
   * Constructs a new PasswordChange.
   *
   * @param reason               The reason for changing the password.
   * @param newPassword          The new password.
   * @param password             The password for the user that is used to authorise the operation
   *                             when performing a user password change.
   * @param securityCode         The security code when performing a forgotten password change.
   * @param expirePassword       Expire the user's password when performing an administrative password change.
   * @param lockUser             Lock the user when performing an administrative password change.
   * @param resetPasswordHistory Reset the user's password history when performing an administrative password change.
   */
  constructor(reason: PasswordChangeReason, newPassword: string, password?: string, securityCode?: string,
              expirePassword?: boolean, lockUser?: boolean, resetPasswordHistory?: boolean) {
    this.reason = reason;
    this.newPassword = newPassword;
    this.password = !!password ? password : null;
    this.securityCode = !!securityCode ? securityCode : null;
    this.expirePassword = !!expirePassword ? expirePassword : null;
    this.lockUser = !!lockUser ? lockUser: null;
    this.resetPasswordHistory = !!resetPasswordHistory ? resetPasswordHistory : null;
  }
}
