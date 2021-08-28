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

/**
 * The GroupRole class holds the information for a group role.
 *
 * @author Marcus Portmann
 */
export class GroupRole {

  /**
   * The name of the group.
   */
  groupName: string;

  /**
   * The code for the role.
   */
  roleCode: string;

  /**
   * The Universally Unique Identifier (UUID) for the user directory the group is associated with.
   */
  userDirectoryId: string;

  /**
   * Constructs a new GroupRole.
   *
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory the
   *                        group is associated with.
   * @param groupName       The name of the group.
   * @param roleCode        The code for the role.
   */
  constructor(userDirectoryId: string, groupName: string, roleCode: string) {
    this.userDirectoryId = userDirectoryId;
    this.groupName = groupName;
    this.roleCode = roleCode;
  }
}
