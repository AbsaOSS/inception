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

import {GroupMemberType} from './group-member-type';

/**
 * The GroupMember class holds the information for a group member.
 *
 * @author Marcus Portmann
 */
export class GroupMember {

  /**
   * The name of the group.
   */
  groupName: string;

  /**
   * The name of the group member.
   */
  memberName: string;

  /**
   * The group member type.
   */
  memberType: GroupMemberType;

  /**
   * The Universally Unique Identifier (UUID) for the user directory the group is associated with.
   */
  userDirectoryId: string;

  /**
   * Constructs a new GroupMember.
   *
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory the
   *                        group is associated with.
   * @param groupName       The name of the group.
   * @param memberType      The group member type.
   * @param memberName      The name of the group member.
   */
  constructor(userDirectoryId: string, groupName: string, memberType: GroupMemberType, memberName: string) {
    this.userDirectoryId = userDirectoryId;
    this.groupName = groupName;
    this.memberType = memberType;
    this.memberName = memberName;
  }
}
