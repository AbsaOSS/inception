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

import {UserDirectoryParameter} from './user-directory-parameter';

/**
 * The UserDirectory class holds the information for a user directory.
 *
 * @author Marcus Portmann
 */
export class UserDirectory {

  /**
   * The Universally Unique Identifier (UUID) for the user directory.
   */
  id: string;

  /**
   * The name of the user directory.
   */
  name: string;

  /**
   * The parameters for the user directory.
   */
  parameters: UserDirectoryParameter[];

  /**
   * The code for the user directory type.
   */
  type: string;

  /**
   * Constructs a new UserDirectory.
   *
   * @param id         The Universally Unique Identifier (UUID) for the user directory.
   * @param name       The name of the user directory.
   * @param type       The code for the user directory type.
   * @param parameters The parameters for the user directory.
   */
  constructor(id: string, name: string, type: string, parameters?: UserDirectoryParameter[]) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.parameters = (!parameters) ? [] : parameters;
  }
}
