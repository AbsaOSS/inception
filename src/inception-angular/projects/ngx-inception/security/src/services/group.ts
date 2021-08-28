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
 * The Group class holds the information for a group.
 *
 * @author Marcus Portmann
 */
export class Group {

  /**
   * The description for the group.
   */
  description: string;

  /**
   * The name of the group.
   */
  name: string;

  /**
   * The Universally Unique Identifier (UUID) for the user directory the group is associated with.
   */
  userDirectoryId: string;

  /**
   * Constructs a new Group.
   *
   * @param userDirectoryId The Universally Unique Identifier (UUID) for the user directory the
   *                        group is associated with.
   * @param name            The name of the group.
   * @param description     The description for the group.
   */
  constructor(userDirectoryId: string, name: string, description: string) {
    this.userDirectoryId = userDirectoryId;
    this.name = name;
    this.description = description;
  }
}

