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
 * The AuthenticationFailedError class holds the information for an authentication failed error.
 *
 * @author Marcus Portmann
 */
export class AuthenticationFailedError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/security/authentication-failed';

  /**
   * Constructs a new AuthenticationFailedError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@security_authentication_failed_error:Authentication failed.`, cause);
  }
}

/**
 * The DuplicateGroupError class holds the information for a duplicate group error.
 *
 * @author Marcus Portmann
 */
export class DuplicateGroupError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/security/duplicate-group';

  /**
   * Constructs a new DuplicateGroupError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@security_duplicate_group_error:A group with the specified name already exists.`, cause);
  }
}

/**
 * The DuplicateTenantError class holds the information for a duplicate tenant error.
 *
 * @author Marcus Portmann
 */
export class DuplicateTenantError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/security/duplicate-tenant';

  /**
   * Constructs a new DuplicateTenantError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@security_duplicate_tenant_error:An tenant with the specified ID or name already exists.`, cause);
  }
}

/**
 * The DuplicateUserDirectoryError class holds the information for a duplicate user directory error.
 *
 * @author Marcus Portmann
 */
export class DuplicateUserDirectoryError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/security/duplicate-user-directory';

  /**
   * Constructs a new DuplicateUserDirectoryError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@security_duplicate_user_directory_error:A user directory with the specified ID or name already exists.`, cause);
  }
}

/**
 * The DuplicateUserError class holds the information for a duplicate user error.
 *
 * @author Marcus Portmann
 */
export class DuplicateUserError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/security/duplicate-user';

  /**
   * Constructs a new DuplicateUserError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@security_duplicate_user_error:A user with the specified username already exists.`, cause);
  }
}

/**
 * The ExistingGroupMembersError class holds the information for an existing group members error.
 *
 * @author Marcus Portmann
 */
export class ExistingGroupMembersError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/security/existing-group-members';

  /**
   * Constructs a new ExistingGroupMembersError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@security_existing_group_members_error:The group has existing members.`, cause);
  }
}

/**
 * The ExistingPasswordError class holds the information for an existing password error.
 *
 * @author Marcus Portmann
 */
export class ExistingPasswordError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/security/existing-password';

  /**
   * Constructs a new ExistingPasswordError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@security_existing_password_error:The new password has been used recently and is not valid.`, cause);
  }
}

/**
 * The GroupMemberNotFoundError class holds the information for a group member not found error.
 *
 * @author Marcus Portmann
 */
export class GroupMemberNotFoundError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/security/group-member-not-found';

  /**
   * Constructs a new GroupMemberNotFoundError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@security_group_member_not_found_error:The group member could not be found.`, cause);
  }
}

/**
 * The GroupNotFoundError class holds the information for a group not found error.
 *
 * @author Marcus Portmann
 */
export class GroupNotFoundError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/security/group-not-found';

  /**
   * Constructs a new GroupNotFoundError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@security_group_not_found_error:The group could not be found.`, cause);
  }
}

/**
 * The GroupRoleNotFoundError class holds the information for a group role not found error.
 *
 * @author Marcus Portmann
 */
export class GroupRoleNotFoundError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/security/group-role-not-found';

  /**
   * Constructs a new GroupRoleNotFoundError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@security_group_role_not_found_error:The group role could not be found.`, cause);
  }
}

/**
 * The InvalidSecurityCodeError class holds the information for an invalid security code error.
 *
 * @author Marcus Portmann
 */
export class InvalidSecurityCodeError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/security/invalid-security-code';

  /**
   * Constructs a new InvalidSecurityCodeError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@security_invalid_security_code_error:The security code is invalid.`, cause);
  }
}

/**
 * The RoleNotFoundError class holds the information for a role not found error.
 *
 * @author Marcus Portmann
 */
export class RoleNotFoundError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/security/role-not-found';

  /**
   * Constructs a new RoleNotFoundError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@security_role_not_found_error:The role could not be found.`, cause);
  }
}

/**
 * The SessionError class holds the information for a session error.
 *
 * @author Marcus Portmann
 */
export class SessionError extends Error {

  /**
   * Constructs a new SessionError.
   *
   * @param message The error message.
   * @param cause   The optional cause of the error.
   */
  constructor(message: string, cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super(message, cause);
  }
}

/**
 * The TenantNotFoundError class holds the information for a tenant not found error.
 *
 * @author Marcus Portmann
 */
export class TenantNotFoundError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/security/tenant-not-found';

  /**
   * Constructs a new TenantNotFoundError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@security_tenant_not_found_error:The tenant could not be found.`, cause);
  }
}

/**
 * The TenantUserDirectoryNotFoundError class holds the information for a tenant user
 * directory not found error.
 *
 * @author Marcus Portmann
 */
export class TenantUserDirectoryNotFoundError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/security/tenant-user-directory-not-found';

  /**
   * Constructs a new TenantUserDirectoryNotFoundError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@security_tenant_user_directory_not_found_error:The tenant user directory could not be found.`, cause);
  }
}

/**
 * The UserDirectoryNotFoundError class holds the information for a user directory not found error.
 *
 * @author Marcus Portmann
 */
export class UserDirectoryNotFoundError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/security/user-directory-not-found';

  /**
   * Constructs a new UserDirectoryNotFoundError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@security_user_directory_not_found_error:The user directory could not be found.`, cause);
  }
}

/**
 * The UserLockedError class holds the information for a user locked error.
 *
 * @author Marcus Portmann
 */
export class UserLockedError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/security/user-locked';

  /**
   * Constructs a new UserLockedError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@security_user_locked_error:The user has exceeded the maximum number of failed password attempts and has been locked.`, cause);
  }
}

/**
 * The UserNotFoundError class holds the information for a user not found error.
 *
 * @author Marcus Portmann
 */
export class UserNotFoundError extends Error {

  static readonly TYPE = 'http://inception.digital/problems/security/user-not-found';

  /**
   * Constructs a new UserNotFoundError.
   *
   * @param cause The optional cause of the error.
   */
  constructor(cause?: ProblemDetails | HttpErrorResponse | HttpError) {
    super($localize`:@@security_user_not_found_error:The user could not be found.`, cause);
  }
}


