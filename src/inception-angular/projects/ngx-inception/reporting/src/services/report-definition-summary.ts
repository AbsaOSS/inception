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
 * The ReportDefinitionSummary class holds the summary information for a report definition.
 *
 * @author Marcus Portmann
 */
export class ReportDefinitionSummary {

  /**
   * The ID for the report definition.
   */
  id: string;

  /**
   * The name of the report definition.
   */
  name: string;

  /**
   * Constructs a new ReportDefinitionSummary.
   *
   * @param id   The ID for the report definition.
   * @param name The name of the report definition.
   */
  constructor(id: string, name: string) {
    this.id = id;
    this.name = name;
  }
}
