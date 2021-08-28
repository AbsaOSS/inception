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
 * The Config class stores the key, value and description for the config.
 *
 * @author Marcus Portmann
 */
export class Config {

  /**
   * The description for the config.
   */
  description: string;

  /**
   * The key for the config.
   */
  key: string;

  /**
   * The value for the config.
   */
  value: string;

  /**
   * Constructs a new Config.
   *
   * @param key         The key for the config.
   * @param value       The value for the config.
   * @param description The description for the config.
   */
  constructor(key: string, value: string, description: string) {

    this.key = key;
    this.value = value;
    this.description = description;
  }
}
