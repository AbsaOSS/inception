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
 * The UserDirectoryParameter class holds the information for a user directory parameter.
 *
 * @author Marcus Portmann
 */
export class UserDirectoryParameter {

  /**
   * The name for the user directory parameter.
   */
  name: string;

  /**
   * The value for the user directory parameter.
   */
  value: string;

  /**
   * Constructs a new UserDirectoryParameter.
   *
   * @param name  The name for the user directory parameter.
   * @param value The value for the user directory parameter.
   */
  constructor(name: string, value: string) {
    this.name = name;
    this.value = value;
  }
}
