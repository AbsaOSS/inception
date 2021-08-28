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
 * The UserStatus enumeration defines the possible statuses for a user.
 *
 * @author Marcus Portmann
 */
export enum UserStatus {

  /**
   * The user is inactive.
   */
  Inactive = 'inactive',

  /**
   * The user is active.
   */
  Active = 'active',

  /**
   * The user is locked.
   */
  Locked = 2,

  /**
   * The user is expired.
   */
  Expired = 3
}
