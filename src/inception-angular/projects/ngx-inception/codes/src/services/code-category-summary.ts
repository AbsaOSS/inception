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
 * The CodeCategorySummary class holds the summary information for a code category.
 *
 * @author Marcus Portmann
 */
export class CodeCategorySummary {

  /**
   * The ID for the code category.
   */
  id: string;

  /**
   * The name of the code category.
   */
  name: string;

  /**
   * The date and time the code category was last updated.
   */
  updated: Date | null = null;

  /**
   * Constructs a new CodeCategorySummary.
   *
   * @param id      The ID for the code category.
   * @param name    The name of the code category.
   * @param updated The date and time the code category was last updated.
   */
  constructor(id: string, name: string, updated?: Date) {
    this.id = id;
    this.name = name;
    this.updated = !!updated ? updated : null;
  }
}
