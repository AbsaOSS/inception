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

import {HttpClient, HttpErrorResponse, HttpParams, HttpResponse} from '@angular/common/http';
import {Inject, Injectable} from '@angular/core';
import {
  AccessDeniedError, CommunicationError, INCEPTION_CONFIG, InceptionConfig, InvalidArgumentError,
  ProblemDetails, ServiceUnavailableError, SessionService, SortDirection
} from '@absaoss/ngx-inception/core';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {Group} from './group';
import {GroupMember} from './group-member';
import {GroupMemberType} from './group-member-type';
import {GroupMembers} from './group-members';
import {GroupRole} from './group-role';
import {Groups} from './groups';
import {PasswordChange} from './password-change';
import {PasswordChangeReason} from './password-change-reason';
import {Role} from './role';
import {
  AuthenticationFailedError, DuplicateGroupError, DuplicateTenantError, DuplicateUserDirectoryError,
  DuplicateUserError, ExistingGroupMembersError, ExistingPasswordError,  GroupMemberNotFoundError,
  GroupNotFoundError, GroupRoleNotFoundError, InvalidSecurityCodeError, RoleNotFoundError,
  TenantNotFoundError, TenantUserDirectoryNotFoundError, UserDirectoryNotFoundError,
  UserLockedError, UserNotFoundError
} from './security.service.errors';
import {Tenant} from './tenant';
import {TenantUserDirectory} from './tenant-user-directory';
import {Tenants} from './tenants';
import {User} from './user';
import {UserDirectory} from './user-directory';
import {UserDirectoryCapabilities} from './user-directory-capabilities';
import {UserDirectorySummaries} from './user-directory-summaries';
import {UserDirectorySummary} from './user-directory-summary';
import {UserDirectoryType} from './user-directory-type';
import {UserSortBy} from './user-sort-by';
import {Users} from './users';

/**
 * The Security Service implementation.
 *
 * @author Marcus Portmann
 */
@Injectable({
  providedIn: 'root'
})
export class SecurityService {

  /**
   * Constructs a new SecurityService.
   *
   * @param config         The Inception configuration.
   * @param httpClient     The HTTP client.
   * @param sessionService The session service.
   */
  constructor(@Inject(INCEPTION_CONFIG) private config: InceptionConfig,
              private httpClient: HttpClient, private sessionService: SessionService) {
    console.log('Initializing the Security Service');
  }

  /**
   * Add the group member to the group.
   *
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory.
   * @param groupName       The name of the group.
   * @param memberType      The group member type.
   * @param memberName      The name of the group member.
   *
   * @return True if the group member was successfully added to the group or false otherwise.
   */
  addMemberToGroup(userDirectoryId: string, groupName: string, memberType: GroupMemberType,
                   memberName: string): Observable<boolean> {
    const groupMember = new GroupMember(userDirectoryId, groupName, memberType, memberName);

    return this.httpClient.post<boolean>(
      this.config.securityApiUrlPrefix + '/user-directories/' + userDirectoryId + '/groups/' +
      encodeURIComponent(groupName) + '/members', groupMember, {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, GroupNotFoundError.TYPE)) {
        return throwError(new GroupNotFoundError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, UserNotFoundError.TYPE)) {
        return throwError(new UserNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to add the group member to the group.', httpErrorResponse));
    }));
  }

  /**
   * Add the role to the group.
   *
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory.
   * @param groupName       The name of the group.
   * @param roleCode        The code for the role.
   *
   * @return True if the role was successfully added to the group or false otherwise.
   */
  addRoleToGroup(userDirectoryId: string, groupName: string, roleCode: string): Observable<boolean> {
    const groupRole = new GroupRole(userDirectoryId, groupName, roleCode);

    return this.httpClient.post<boolean>(
      this.config.securityApiUrlPrefix + '/user-directories/' + userDirectoryId + '/groups/' +
      encodeURIComponent(groupName) + '/roles', groupRole, {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, GroupNotFoundError.TYPE)) {
        return throwError(new GroupNotFoundError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, RoleNotFoundError.TYPE)) {
        return throwError(new RoleNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to add the role to the group.', httpErrorResponse));
    }));
  }

  /**
   * Add the user directory to the tenant.
   *
   * @param tenantId        The Universally Unique Identifier (UUID) for the tenant.
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory.
   *
   * @return True if the user directory was successfully added to the tenant or false
   *         otherwise.
   */
  addUserDirectoryToTenant(tenantId: string, userDirectoryId: string): Observable<boolean> {
    const tenantUserDirectory = new TenantUserDirectory(tenantId, userDirectoryId);

    return this.httpClient.post<boolean>(
      this.config.securityApiUrlPrefix + '/tenants/' + tenantId + '/user-directories',
      tenantUserDirectory, {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, TenantNotFoundError.TYPE)) {
        return throwError(new TenantNotFoundError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to add the user directory to the tenant.', httpErrorResponse));
    }));
  }

  /**
   * Administratively change the password for the user.
   *
   * @param userDirectoryId      The Universally Unique Identifier (UUID) for the user directory.
   * @param username             The username for the user.
   * @param newPassword          The new password.
   * @param expirePassword       Expire the user's password?
   * @param lockUser             Lock the user?
   * @param resetPasswordHistory Reset the user's password history?
   *
   * @return True if the user's password was changed successfully or false otherwise.
   */
  adminChangePassword(userDirectoryId: string, username: string, newPassword: string, expirePassword: boolean,
                      lockUser: boolean, resetPasswordHistory: boolean): Observable<boolean> {
    const passwordChange = new PasswordChange(PasswordChangeReason.Administrative, newPassword, undefined, undefined,
      expirePassword, lockUser, resetPasswordHistory);

    return this.httpClient.put<boolean>(
      this.config.securityApiUrlPrefix + '/user-directories/' + userDirectoryId + '/users/' +
      encodeURIComponent(username) + '/password', passwordChange, {
        observe: 'response'
      }).pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, UserNotFoundError.TYPE)) {
        return throwError(new UserNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to administratively change the password for the user.', httpErrorResponse));
    }));
  }

  /**
   * Change the password for the user.
   *
   * @param username    The username for the user.
   * @param password    The password for the user that is used to authorise the operation.
   * @param newPassword The new password.
   *
   * @return True if the user's password was changed successfully or false otherwise.
   */
  changePassword(username: string, password: string, newPassword: string): Observable<boolean> {
    const passwordChange = new PasswordChange(PasswordChangeReason.User, newPassword, password);

    return this.httpClient.put<boolean>(
      this.config.securityApiUrlPrefix + '/users/' + encodeURIComponent(username) + '/password', passwordChange, {
        observe: 'response'
      }).pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, AuthenticationFailedError.TYPE)) {
        return throwError(new AuthenticationFailedError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, ExistingPasswordError.TYPE)) {
        return throwError(new ExistingPasswordError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, UserLockedError.TYPE)) {
        return throwError(new UserLockedError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to change the password for the user.', httpErrorResponse));
    }));
  }

  /**
   * Create the new group.
   *
   * @param group The group to create.
   *
   * @return True if the group was created successfully or false otherwise.
   */
  createGroup(group: Group): Observable<boolean> {
    return this.httpClient.post<boolean>(
      this.config.securityApiUrlPrefix + '/user-directories/' + group.userDirectoryId + '/groups', group, {
        observe: 'response'
      }).pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, DuplicateGroupError.TYPE)) {
        return throwError(new DuplicateGroupError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to create the group.', httpErrorResponse));
    }));
  }

  /**
   * Create the new tenant.
   *
   * @param tenant        The tenant to create.
   * @param createUserDirectory Should a new internal user directory be created for the
   *                            tenant?
   *
   * @return True if the tenant was created successfully or false otherwise.
   */
  createTenant(tenant: Tenant, createUserDirectory?: boolean): Observable<boolean> {
    let httpParams = new HttpParams();
    httpParams = httpParams.append('createUserDirectory',
      createUserDirectory === undefined ? 'false' : (createUserDirectory ? 'true' : 'false'));

    return this.httpClient.post<boolean>(this.config.securityApiUrlPrefix + '/tenants', tenant, {
      params: httpParams,
      observe: 'response'
    }).pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, DuplicateTenantError.TYPE)) {
        return throwError(new DuplicateTenantError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to create the tenant.', httpErrorResponse));
    }));
  }

  /**
   * Create the new user.
   *
   * @param user            The user to create.
   * @param expiredPassword Create the user with its password expired?
   * @param userLocked      Create the user locked?
   *
   * @return True if the user was created successfully or false otherwise.
   */
  createUser(user: User, expiredPassword?: boolean, userLocked?: boolean): Observable<boolean> {
    let httpParams = new HttpParams();
    httpParams = httpParams.append('expiredPassword',
      expiredPassword === undefined ? 'false' : (expiredPassword ? 'true' : 'false'));
    httpParams = httpParams.append('userLocked', userLocked === undefined ? 'false' : (userLocked ? 'true' : 'false'));

    return this.httpClient.post<boolean>(
      this.config.securityApiUrlPrefix + '/user-directories/' + user.userDirectoryId + '/users', user, {
        params: httpParams,
        observe: 'response'
      }).pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, DuplicateUserError.TYPE)) {
        return throwError(new DuplicateUserError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to create the user.', httpErrorResponse));
    }));
  }

  /**
   * Create the new user directory.
   *
   * @param userDirectory The user directory to create.
   *
   * @return True if the user directory was created successfully or false otherwise.
   */
  createUserDirectory(userDirectory: UserDirectory): Observable<boolean> {
    return this.httpClient.post<boolean>(this.config.securityApiUrlPrefix + '/user-directories', userDirectory,
      {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, DuplicateUserDirectoryError.TYPE)) {
        return throwError(new DuplicateUserDirectoryError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to create the user directory.', httpErrorResponse));
    }));
  }

  /**
   * Delete the group.
   *
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory.
   * @param groupName       The name of the group.
   *
   * @return True if the group was deleted or false otherwise.
   */
  deleteGroup(userDirectoryId: string, groupName: string): Observable<boolean> {
    return this.httpClient.delete<boolean>(
      this.config.securityApiUrlPrefix + '/user-directories/' + userDirectoryId + '/groups/' +
      encodeURIComponent(groupName), {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, GroupNotFoundError.TYPE)) {
        return throwError(new GroupNotFoundError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, ExistingGroupMembersError.TYPE)) {
        return throwError(new ExistingGroupMembersError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to delete the group.', httpErrorResponse));
    }));
  }

  /**
   * Delete the tenant.
   *
   * @param tenantId The Universally Unique Identifier (UUID) for the tenant.
   *
   * @return True if the tenant was deleted or false otherwise.
   */
  deleteTenant(tenantId: string): Observable<boolean> {
    return this.httpClient.delete<boolean>(this.config.securityApiUrlPrefix + '/tenants/' + tenantId,
      {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, TenantNotFoundError.TYPE)) {
        return throwError(new TenantNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to delete the tenant.', httpErrorResponse));
    }));
  }

  /**
   * Delete the user.
   *
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory.
   * @param username        The username for the user.
   *
   * @return True if the user was deleted or false otherwise.
   */
  deleteUser(userDirectoryId: string, username: string): Observable<boolean> {
    return this.httpClient.delete<boolean>(
      this.config.securityApiUrlPrefix + '/user-directories/' + userDirectoryId + '/users/' +
      encodeURIComponent(username), {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, UserNotFoundError.TYPE)) {
        return throwError(new UserNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to delete the user.', httpErrorResponse));
    }));
  }

  /**
   * Delete the user directory.
   *
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory.
   *
   * @return True if the user directory was deleted or false otherwise.
   */
  deleteUserDirectory(userDirectoryId: string): Observable<boolean> {
    return this.httpClient.delete<boolean>(this.config.securityApiUrlPrefix + '/user-directories/' + userDirectoryId,
      {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to delete the user directory.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the group.
   *
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory.
   * @param groupName       The name of the group.
   *
   * @return The group.
   */
  getGroup(userDirectoryId: string, groupName: string): Observable<Group> {
    return this.httpClient.get<Group>(
      this.config.securityApiUrlPrefix + '/user-directories/' + userDirectoryId + '/groups/' +
      encodeURIComponent(groupName), {reportProgress: true}).pipe(map((group: Group) => {
      return group;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, GroupNotFoundError.TYPE)) {
        return throwError(new GroupNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the group.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve all the group names.
   *
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory.
   *
   * @return The group names.
   */
  getGroupNames(userDirectoryId: string): Observable<string[]> {
    return this.httpClient.get<string[]>(
      this.config.securityApiUrlPrefix + '/user-directories/' + userDirectoryId + '/group-names', {
        reportProgress: true,
      }).pipe(map((groupNames: string[]) => {
      return groupNames;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the group names.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the names of the groups the user is a member of.
   *
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory.
   * @param username        The username for the user.
   *
   * @return The names of the groups the user is a member of.
   */
  getGroupNamesForUser(userDirectoryId: string, username: string): Observable<string[]> {
    return this.httpClient.get<string[]>(
      this.config.securityApiUrlPrefix + '/user-directories/' + userDirectoryId + '/users/' +
      encodeURIComponent(username) + '/group-names', {reportProgress: true})
    .pipe(map((groupNames: string[]) => {
      return groupNames;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, UserNotFoundError.TYPE)) {
        return throwError(new UserNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the group names for the user.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the groups.
   *
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory.
   * @param filter          The optional filter to apply to the groups.
   * @param sortDirection   The optional sort direction to apply to the groups.
   * @param pageIndex       The optional page index.
   * @param pageSize        The optional page size.
   *
   * @return The groups.
   */
  getGroups(userDirectoryId: string, filter?: string, sortDirection?: SortDirection, pageIndex?: number,
            pageSize?: number): Observable<Groups> {

    let params = new HttpParams();

    if (filter != null) {
      params = params.append('filter', filter);
    }

    if (sortDirection != null) {
      params = params.append('sortDirection', sortDirection);
    }

    if (pageIndex != null) {
      params = params.append('pageIndex', String(pageIndex));
    }

    if (pageSize != null) {
      params = params.append('pageSize', String(pageSize));
    }

    return this.httpClient.get<Groups>(
      this.config.securityApiUrlPrefix + '/user-directories/' + userDirectoryId + '/groups', {
        params,
        reportProgress: true,
      }).pipe(map((groups: Groups) => {
      return groups;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the groups.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the members for the group.
   *
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory.
   * @param groupName       The name of the group.
   * @param filter          The optional filter to apply to the group members.
   * @param sortDirection   The optional sort direction to apply to the group members.
   * @param pageIndex       The optional page index.
   * @param pageSize        The optional page size.
   *
   * @return The members for the group.
   */
  getMembersForGroup(userDirectoryId: string, groupName: string, filter?: string, sortDirection?: SortDirection,
                     pageIndex?: number, pageSize?: number): Observable<GroupMembers> {

    let params = new HttpParams();

    if (filter != null) {
      params = params.append('filter', filter);
    }

    if (sortDirection != null) {
      params = params.append('sortDirection', sortDirection);
    }

    if (pageIndex != null) {
      params = params.append('pageIndex', String(pageIndex));
    }

    if (pageSize != null) {
      params = params.append('pageSize', String(pageSize));
    }

    return this.httpClient.get<GroupMembers>(
      this.config.securityApiUrlPrefix + '/user-directories/' + userDirectoryId + '/groups/' +
      encodeURIComponent(groupName) + '/members', {
        params,
        reportProgress: true,
      }).pipe(map((groupMembers: GroupMembers) => {
      return groupMembers;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, GroupNotFoundError.TYPE)) {
        return throwError(new GroupNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the members for the group.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the codes for the roles that have been assigned to the group.
   *
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory.
   * @param groupName       The name of the group.
   *
   * @return The codes for the roles that have been assigned to the group.
   */
  getRoleCodesForGroup(userDirectoryId: string, groupName: string): Observable<string[]> {
    return this.httpClient.get<string[]>(
      this.config.securityApiUrlPrefix + '/user-directories/' + userDirectoryId + '/groups/' +
      encodeURIComponent(groupName) + '/role-codes', {
        reportProgress: true,
      }).pipe(map((roleCodes: string[]) => {
      return roleCodes;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, GroupNotFoundError.TYPE)) {
        return throwError(new GroupNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the role codes for the group.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve all the roles.
   *
   * @return The roles.
   */
  getRoles(): Observable<Role[]> {
    return this.httpClient.get<Role[]>(this.config.securityApiUrlPrefix + '/roles', {
      reportProgress: true,
    }).pipe(map((roles: Role[]) => {
      return roles;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the roles.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the roles that have been assigned to the group.
   *
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory.
   * @param groupName       The name of the group.
   *
   * @return The roles that have been assigned to the group.
   */
  getRolesForGroup(userDirectoryId: string, groupName: string): Observable<GroupRole[]> {
    return this.httpClient.get<GroupRole[]>(
      this.config.securityApiUrlPrefix + '/user-directories/' + userDirectoryId + '/groups/' +
      encodeURIComponent(groupName) + '/roles', {
        reportProgress: true,
      }).pipe(map((groupRoles: GroupRole[]) => {
      return groupRoles;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, GroupNotFoundError.TYPE)) {
        return throwError(new GroupNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the roles for the group.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the tenant.
   *
   * @param tenantId The Universally Unique Identifier (UUID) for the tenant.
   *
   * @return The tenant.
   */
  getTenant(tenantId: string): Observable<Tenant> {
    return this.httpClient.get<Tenant>(this.config.securityApiUrlPrefix + '/tenants/' + tenantId,
      {reportProgress: true})
    .pipe(map((tenant: Tenant) => {
      return tenant;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, TenantNotFoundError.TYPE)) {
        return throwError(new TenantNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the tenant.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the name of the tenant.
   *
   * @param tenantId The Universally Unique Identifier (UUID) for the tenant.
   *
   * @return The name of the tenant.
   */
  getTenantName(tenantId: string): Observable<string> {
    return this.httpClient.get<string>(this.config.securityApiUrlPrefix + '/tenants/' + tenantId + '/name',
      {reportProgress: true}).pipe(map((tenantName: string) => {
      return tenantName;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, TenantNotFoundError.TYPE)) {
        return throwError(new TenantNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the tenant name.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the tenants.
   *
   * @param filter        The optional filter to apply to the tenants.
   * @param sortDirection The optional sort direction to apply to the tenants.
   * @param pageIndex     The optional page index.
   * @param pageSize      The optional page size.
   *
   * @return The tenants.
   */
  getTenants(filter?: string, sortDirection?: SortDirection, pageIndex?: number,
             pageSize?: number): Observable<Tenants> {

    let params = new HttpParams();

    if (filter != null) {
      params = params.append('filter', filter);
    }

    if (sortDirection != null) {
      params = params.append('sortDirection', sortDirection);
    }

    if (pageIndex != null) {
      params = params.append('pageIndex', String(pageIndex));
    }

    if (pageSize != null) {
      params = params.append('pageSize', String(pageSize));
    }

    return this.httpClient.get<Tenants>(this.config.securityApiUrlPrefix + '/tenants', {
      params,
      reportProgress: true,
    }).pipe(map((tenants: Tenants) => {
      return tenants;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the tenants.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the tenants the user directory is associated with.
   *
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory.
   *
   * @return The tenants the user directory is associated with.
   */
  getTenantsForUserDirectory(userDirectoryId: string): Observable<Tenant[]> {
    return this.httpClient.get<Tenant[]>(
      this.config.securityApiUrlPrefix + '/user-directories/' + userDirectoryId + '/tenants',
      {reportProgress: true})
    .pipe(map((tenants: Tenant[]) => {
      return tenants;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the tenants associated with the user directory.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the user.
   *
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory.
   * @param username        The username for the user.
   *
   * @return The user.
   */
  getUser(userDirectoryId: string, username: string): Observable<User> {
    return this.httpClient.get<User>(
      this.config.securityApiUrlPrefix + '/user-directories/' + userDirectoryId + '/users/' +
      encodeURIComponent(username), {reportProgress: true}).pipe(map((user: User) => {
      return user;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, UserNotFoundError.TYPE)) {
        return throwError(new UserNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the user.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the user directory.
   *
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory.
   *
   * @return The user directory.
   */
  getUserDirectory(userDirectoryId: string): Observable<UserDirectory> {
    return this.httpClient.get<UserDirectory>(this.config.securityApiUrlPrefix + '/user-directories/' + userDirectoryId,
      {reportProgress: true})
    .pipe(map((userDirectory: UserDirectory) => {
      return userDirectory;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the user directory.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the capabilities for the user directory.
   *
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory.
   *
   * @return The capabilities for the user directory.
   */
  getUserDirectoryCapabilities(userDirectoryId: string): Observable<UserDirectoryCapabilities> {
    return this.httpClient.get<UserDirectoryCapabilities>(
      this.config.securityApiUrlPrefix + '/user-directories/' + userDirectoryId + '/capabilities',
      {reportProgress: true})
    .pipe(map((capabilities: UserDirectoryCapabilities) => {
      return capabilities;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the capabilities for the user directory.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the name of the user directory.
   *
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory.
   *
   * @return The name of the user directory.
   */
  getUserDirectoryName(userDirectoryId: string): Observable<string> {
    return this.httpClient.get<string>(
      this.config.securityApiUrlPrefix + '/user-directories/' + userDirectoryId + '/name', {reportProgress: true})
    .pipe(map((userDirectoryName: string) => {
      return userDirectoryName;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the user directory name.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the user directory summaries.
   *
   * @param filter        The optional filter to apply to the user directory summaries.
   * @param sortDirection The optional sort direction to apply to the user directory summaries.
   * @param pageIndex     The optional page index.
   * @param pageSize      The optional page size.
   *
   * @return The user directory summaries.
   */
  getUserDirectorySummaries(filter?: string, sortDirection?: SortDirection, pageIndex?: number,
                            pageSize?: number): Observable<UserDirectorySummaries> {

    let params = new HttpParams();

    if (filter != null) {
      params = params.append('filter', filter);
    }

    if (sortDirection != null) {
      params = params.append('sortDirection', sortDirection);
    }

    if (pageIndex != null) {
      params = params.append('pageIndex', String(pageIndex));
    }

    if (pageSize != null) {
      params = params.append('pageSize', String(pageSize));
    }

    return this.httpClient.get<UserDirectorySummaries>(this.config.securityApiUrlPrefix + '/user-directory-summaries', {
      params,
      reportProgress: true,
    }).pipe(map((userDirectorySummaries: UserDirectorySummaries) => {
      return userDirectorySummaries;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the user directory summaries.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the summaries for the user directories the tenant is associated with.
   *
   * @param tenantId The Universally Unique Identifier (UUID) for the tenant.
   *
   * @return The summaries for the user directories the tenant is associated with.
   */
  getUserDirectorySummariesForTenant(tenantId: string): Observable<UserDirectorySummary[]> {
    return this.httpClient.get<UserDirectorySummary[]>(
      this.config.securityApiUrlPrefix + '/tenants/' + tenantId + '/user-directory-summaries',
      {reportProgress: true})
    .pipe(map((codeCategories: UserDirectorySummary[]) => {
      return codeCategories;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, TenantNotFoundError.TYPE)) {
        return throwError(new TenantNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the summaries for the user directories associated with the tenant.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the user directory type for the user directory.
   *
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory.
   *
   * @return The user directory type for the user directory.
   */
  getUserDirectoryTypeForUserDirectory(userDirectoryId: string): Observable<UserDirectoryType> {
    return this.httpClient.get<UserDirectoryType>(
      this.config.securityApiUrlPrefix + '/user-directories/' + userDirectoryId + '/user-directory-type',
      {reportProgress: true})
    .pipe(map((userDirectoryType: UserDirectoryType) => {
      return userDirectoryType;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the user directory type for the user directory.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the user directory types.
   *
   * @return The user directory types.
   */
  getUserDirectoryTypes(): Observable<UserDirectoryType[]> {
    return this.httpClient.get<UserDirectoryType[]>(this.config.securityApiUrlPrefix + '/user-directory-types',
      {reportProgress: true})
    .pipe(map((userDirectoryTypes: UserDirectoryType[]) => {
      return userDirectoryTypes;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the user directory types.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the name of the user.
   *
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory.
   * @param username        The username for the user.
   *
   * @return The name of the user.
   */
  getUserName(userDirectoryId: string, username: string): Observable<string> {
    return this.httpClient.get<string>(
      this.config.securityApiUrlPrefix + '/user-directories/' + userDirectoryId + '/users/' +
      encodeURIComponent(username) + '/name', {reportProgress: true})
    .pipe(map((userName: string) => {
      return userName;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, UserNotFoundError.TYPE)) {
        return throwError(new UserNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the name of the user.', httpErrorResponse));
    }));
  }

  /**
   * Retrieve the users.
   *
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory.
   * @param filter          The optional filter to apply to the users.
   * @param sortBy          The optional method used to sort the users e.g. by name.
   * @param sortDirection   The optional sort direction to apply to the users.
   * @param pageIndex       The optional page index.
   * @param pageSize        The optional page size.
   *
   * @return The users.
   */
  getUsers(userDirectoryId: string, filter?: string, sortBy?: UserSortBy, sortDirection?: SortDirection,
           pageIndex?: number, pageSize?: number): Observable<Users> {

    let params = new HttpParams();

    if (filter != null) {
      params = params.append('filter', filter);
    }

    if (sortBy != null) {
      params = params.append('sortBy', String(sortBy));
    }

    if (sortDirection != null) {
      params = params.append('sortDirection', sortDirection);
    }

    if (pageIndex != null) {
      params = params.append('pageIndex', String(pageIndex));
    }

    if (pageSize != null) {
      params = params.append('pageSize', String(pageSize));
    }

    return this.httpClient.get<Users>(
      this.config.securityApiUrlPrefix + '/user-directories/' + userDirectoryId + '/users', {
        params,
        reportProgress: true,
      }).pipe(map((users: Users) => {
      return users;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to retrieve the users.', httpErrorResponse));
    }));
  }

  /**
   * Initiate the password reset process for the user.
   *
   * @param username         The username for the user.
   * @param resetPasswordUrl The reset password URL.
   *
   * @return True if the password reset process was initiated successfully or false otherwise.
   */
  initiatePasswordReset(username: string, resetPasswordUrl: string): Observable<boolean> {
    let params = new HttpParams();
    params = params.append('resetPasswordUrl', resetPasswordUrl);

    return this.httpClient.post<boolean>(
      this.config.securityApiUrlPrefix + '/users/' + encodeURIComponent(username) + '/reset-password', null, {
        observe: 'response',
        params,
      }).pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to initiate the password reset.', httpErrorResponse));
    }));
  }

  /**
   * Remove the group member from the group.
   *
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory.
   * @param groupName       The name of the group.
   * @param memberType      The group member type.
   * @param memberName      The name of the group member.
   *
   * @return True if the group member was successfully removed from the group or false otherwise.
   */
  removeMemberFromGroup(userDirectoryId: string, groupName: string, memberType: GroupMemberType,
                        memberName: string): Observable<boolean> {
    return this.httpClient.delete<boolean>(
      this.config.securityApiUrlPrefix + '/user-directories/' + userDirectoryId + '/groups/' +
      encodeURIComponent(groupName) + '/members/' + memberType + '/' + encodeURIComponent(memberName),
      {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, GroupNotFoundError.TYPE)) {
        return throwError(new GroupNotFoundError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, GroupMemberNotFoundError.TYPE)) {
        return throwError(new GroupMemberNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to remove the group member from the group.', httpErrorResponse));
    }));
  }

  /**
   * Remove the role from the group.
   *
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory.
   * @param groupName       The name of the group.
   * @param roleCode        The code for the role.
   *
   * @return True if the role was successfully removed from the group or false otherwise.
   */
  removeRoleFromGroup(userDirectoryId: string, groupName: string, roleCode: string): Observable<boolean> {
    return this.httpClient.delete<boolean>(
      this.config.securityApiUrlPrefix + '/user-directories/' + userDirectoryId + '/groups/' +
      encodeURIComponent(groupName) + '/roles/' + roleCode, {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, GroupNotFoundError.TYPE)) {
        return throwError(new GroupNotFoundError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, GroupRoleNotFoundError.TYPE)) {
        return throwError(new GroupRoleNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to remove the role from the group.', httpErrorResponse));
    }));
  }

  /**
   * Remove the user directory from the tenant.
   *
   * @param tenantId        The Universally Unique Identifier (UUID) for the tenant.
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory.
   *
   * @return True if the user directory was successfully removed from the tenant or false
   *         otherwise.
   */
  removeUserDirectoryFromTenant(tenantId: string, userDirectoryId: string): Observable<boolean> {
    return this.httpClient.delete<boolean>(
      this.config.securityApiUrlPrefix + '/tenants/' + tenantId + '/user-directories/' + userDirectoryId,
      {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, TenantNotFoundError.TYPE)) {
        return throwError(new TenantNotFoundError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, TenantUserDirectoryNotFoundError.TYPE)) {
        return throwError(new TenantUserDirectoryNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to remove the user directory from the tenant.', httpErrorResponse));
    }));
  }

  /**
   * Reset the password for the user.
   *
   * @param username     The username for the user.
   * @param newPassword  The new password.
   * @param securityCode The security code.
   *
   * @return True if the user's password was reset successfully or false otherwise.
   */
  resetPassword(username: string, newPassword: string, securityCode: string): Observable<boolean> {
    const passwordChange = new PasswordChange(PasswordChangeReason.Reset, newPassword, undefined, securityCode);

    return this.httpClient.put<boolean>(
      this.config.securityApiUrlPrefix + '/users/' + encodeURIComponent(username) + '/password', passwordChange, {
        observe: 'response'
      }).pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, InvalidSecurityCodeError.TYPE)) {
        return throwError(new InvalidSecurityCodeError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to reset the password for the user.', httpErrorResponse));
    }));
  }

  /**
   * Update the group.
   *
   * @param group The group to update.
   *
   * @return True if the group was updated successfully or false otherwise.
   */
  updateGroup(group: Group): Observable<boolean> {
    return this.httpClient.put<boolean>(
      this.config.securityApiUrlPrefix + '/user-directories/' + group.userDirectoryId + '/groups/' +
      encodeURIComponent(group.name), group, {
        observe: 'response'
      }).pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, GroupNotFoundError.TYPE)) {
        return throwError(new GroupNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to update the group.', httpErrorResponse));
    }));
  }

  /**
   * Update the tenant.
   *
   * @param tenant The tenant to update.
   *
   * @return True if the tenant was updated successfully or false otherwise.
   */
  updateTenant(tenant: Tenant): Observable<boolean> {
    return this.httpClient.put<boolean>(this.config.securityApiUrlPrefix + '/tenants/' + tenant.id,
      tenant, {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, TenantNotFoundError.TYPE)) {
        return throwError(new TenantNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to update the tenant.', httpErrorResponse));
    }));
  }

  /**
   * Update the user.
   *
   * @param user           The user to update.
   * @param expirePassword Expire the user's password?
   * @param lockUser       Lock the user?
   *
   * @return True if the user was updated successfully or false otherwise.
   */
  updateUser(user: User, expirePassword?: boolean, lockUser?: boolean): Observable<boolean> {
    let httpParams = new HttpParams();
    httpParams =
      httpParams.append('expirePassword', expirePassword === undefined ? 'false' : (expirePassword ? 'true' : 'false'));
    httpParams = httpParams.append('lockUser', lockUser === undefined ? 'false' : (lockUser ? 'true' : 'false'));

    return this.httpClient.put<boolean>(
      this.config.securityApiUrlPrefix + '/user-directories/' + user.userDirectoryId + '/users/' +
      encodeURIComponent(user.username), user, {
        params: httpParams,
        observe: 'response'
      }).pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (ProblemDetails.isProblemDetails(httpErrorResponse, UserNotFoundError.TYPE)) {
        return throwError(new UserNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to update the user.', httpErrorResponse));
    }));
  }

  /**
   * Update the user directory.
   *
   * @param userDirectory The user directory to update.
   *
   * @return True if the user directory was updated successfully or false otherwise.
   */
  updateUserDirectory(userDirectory: UserDirectory): Observable<boolean> {
    return this.httpClient.put<boolean>(this.config.securityApiUrlPrefix + '/user-directories/' + userDirectory.id,
      userDirectory, {observe: 'response'})
    .pipe(map((httpResponse: HttpResponse<boolean>) => {
      return httpResponse.status === 204;
    }), catchError((httpErrorResponse: HttpErrorResponse) => {
      if (ProblemDetails.isProblemDetails(httpErrorResponse, UserDirectoryNotFoundError.TYPE)) {
        return throwError(new UserDirectoryNotFoundError(httpErrorResponse));
      } else if (AccessDeniedError.isAccessDeniedError(httpErrorResponse)) {
        return throwError(new AccessDeniedError(httpErrorResponse));
      } else if (CommunicationError.isCommunicationError(httpErrorResponse)) {
        return throwError(new CommunicationError(httpErrorResponse));
      } else if (InvalidArgumentError.isInvalidArgumentError(httpErrorResponse)) {
        return throwError(new InvalidArgumentError(httpErrorResponse));
      }

      return throwError(new ServiceUnavailableError('Failed to update the user directory.', httpErrorResponse));
    }));
  }
}
