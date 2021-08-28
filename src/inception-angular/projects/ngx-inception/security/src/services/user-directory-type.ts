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
 * The UserDirectoryType class holds the information for a user directory type.
 *
 * @author Marcus Portmann
 */
export class UserDirectoryType {

  /**
   * The code for the user directory type.
   */
  code: string;

  /**
   * The name of the user directory type.
   */
  name: string;

  /**
   * The fully qualified name of the Java class that implements the user directory type.
   */
  userDirectoryClassName: string;

  /**
   * Constructs a new UserDirectoryParameter.
   *
   * @param code                   The code for the user directory type.
   * @param name                   The name of the user directory type.
   * @param userDirectoryClassName The fully qualified name of the Java class that implements the
   *                               user directory type.
   */
  constructor(code: string, name: string, userDirectoryClassName: string) {
    this.code = code;
    this.name = name;
    this.userDirectoryClassName = userDirectoryClassName;
  }
}
