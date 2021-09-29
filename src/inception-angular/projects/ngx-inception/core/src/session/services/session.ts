/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * The Session class holds the information for an active user session associated with an
 * authenticated user. All values are stored as strings to support the serialization of the user
 * session.
 *
 * @author Marcus Portmann
 */
export class Session {

  /**
   * The base-64 encoded OAuth2 JWT access token for the user session.
   */
  accessToken: string;

  /**
   * The date and time the OAuth2 JWT access token for the user session will expire.
   */
  accessTokenExpiry?: Date;

  /**
   * The codes for the functions assigned to the user associated with the user session.
   */
  functionCodes: string[];

  /**
   * The name of the user.
   */
  name: string;

  /**
   * The base-64 encoded OAuth2 refresh token for the user session.
   */
  refreshToken?: string;

  /**
   * The codes for the roles assigned to the user associated with the user session.
   */
  roleCodes: string[];

  /**
   * The OAuth2 scopes for the user session.
   */
  scopes: string[];

  /**
   * The Universally Unique Identifier (UUID) for the selected tenant for the user session.
   */
  tenantId?: string;

  /**
   * The Universally Unique Identifiers (UUIDs) for tenants the user is associated with.
   */
  tenantIds: string[];

  /**
   * The Universally Unique Identifier (UUID) for the user directory the user is associated with.
   */
  userDirectoryId: string;

  /**
   * The username for the user, the user session is associated with.
   */
  username: string;

  /**
   * Constructs a new Session.
   *
   * @param username          The username for the user the user session is associated with.
   * @param userDirectoryId   The Universally Unique Identifier (UUID) for the user directory the
   *                          user is associated with.
   * @param name              The name of the user.
   * @param scopes            The OAuth2 scopes for the user session.
   * @param roleCodes         The codes for the roles assigned to the user associated with
   *                          the user session.
   * @param functionCodes     The codes for the functions assigned to the user associated
   *                          with the user session.
   * @param tenantIds         The Universally Unique Identifiers (UUIDs) for the tenants the user
   *                          is associated with.
   * @param accessToken       The base-64 encoded OAuth2 JWT access token for the user session.
   * @param accessTokenExpiry The string representation of the epoch timestamp giving the date and
   *                          time the OAuth2 JWT access token for the user session will expire.
   * @param refreshToken      The base-64 encoded OAuth2 refresh token for the user session.
   */
  constructor(username: string, userDirectoryId: string, name: string, scopes: string[],
              roleCodes: string[], functionCodes: string[], tenantIds: string[],
              accessToken: string, accessTokenExpiry?: Date, refreshToken?: string) {
    this.username = username;
    this.userDirectoryId = userDirectoryId;
    this.name = name;
    this.scopes = scopes;
    this.roleCodes = roleCodes;
    this.functionCodes = functionCodes;
    this.tenantIds = tenantIds;
    this.accessToken = accessToken;
    this.accessTokenExpiry = accessTokenExpiry;
    this.refreshToken = refreshToken;
  }

  /**
   * Confirm that the user associated with the session has the specified authority.
   *
   * @param requiredAuthority The required authority.
   *
   * @return True if the user associated with the session has the specified authority or false
   *         otherwise.
   */
  hasAuthority(requiredAuthority: string): boolean {
    if (requiredAuthority.startsWith('ROLE_')) {
      if (requiredAuthority.length < 6) {
        return false;
      } else {
        return this.hasRole(requiredAuthority.substr(5));
      }
    }

    if (requiredAuthority.startsWith('FUNCTION_')) {
      if (requiredAuthority.length < 10) {
        return false;
      } else {
        return this.hasFunction(requiredAuthority.substr(9));
      }
    }

    return false;
  }

  /**
   * Confirm that the user associated with the session has been assigned the required function.
   *
   * @param requiredFunctionCode The code for the required function.
   *
   * @return True if the user associated with the session has been assigned the required function
   *         or false otherwise.
   */
  hasFunction(requiredFunctionCode: string): boolean {
    for (const functionCode of this.functionCodes) {
      if (functionCode.localeCompare(requiredFunctionCode, undefined, {sensitivity: 'accent'}) === 0) {
        return true;
      }
    }

    return false;
  }

  /**
   * Confirm that the user associated with the session has been assigned the required role.
   *
   * @param requiredRoleCode The code for the required role.
   *
   * @return True if the user associated with the session has been assigned the required role or
   *         false otherwise.
   */
  hasRole(requiredRoleCode: string): boolean {
    for (const roleCode of this.roleCodes) {
      if (roleCode.localeCompare(requiredRoleCode, undefined, {sensitivity: 'accent'}) === 0) {
        return true;
      }
    }

    return false;
  }
}
