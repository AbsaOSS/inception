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
 * The JobParameter class holds the information for a job parameter.
 *
 * @author Marcus Portmann
 */
export class JobParameter {

  /**
   * The name of the job parameter.
   */
  name: string;

  /**
   * The value of the job parameter.
   */
  value: string;

  /**
   * Constructs a new JobParameter.
   *
   * @param name  The name of the job parameter.
   * @param value The value of the job parameter.
   */
  constructor(name: string, value: string) {
    this.name = name;
    this.value = value;
  }
}
