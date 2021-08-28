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
 * The Code class holds the information for a code.
 *
 * @author Marcus Portmann
 */
export class Code {

  /**
   * The ID for the code category the code is associated with.
   */
  codeCategoryId: string;

  /**
   * The ID for the code.
   */
  id: string;

  /**
   * The name of the code.
   */
  name: string;

  /**
   * The value for the code.
   */
  value: string;

  /**
   * Constructs a new Code.
   *
   * @param id             The ID for the code.
   * @param codeCategoryId The ID for the code category the code is associated with.
   * @param name           The name of the code category.
   * @param value          The value for the code.
   */
  constructor(id: string, codeCategoryId: string, name: string, value: string) {
    this.id = id;
    this.codeCategoryId = codeCategoryId;
    this.name = name;
    this.value = value;
  }
}
