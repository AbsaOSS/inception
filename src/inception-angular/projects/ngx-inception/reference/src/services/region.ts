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
 * The Region class holds the information for a region.
 *
 * @author Marcus Portmann
 */
export class Region {

  /**
   * The ISO 3166-2 subdivision code for the region.
   */
  code: string;

  /**
   * The ISO 3166-1 alpha-2 code for the country the region is associated with.
   */
  country: string;

  /**
   * The description for the region.
   */
  description: string;

  /**
   * The Unicode locale identifier for the region.
   */
  localeId: string;

  /**
   * The name of the region.
   */
  name: string;

  /**
   * The sort index for the region.
   */
  sortIndex: number;

  /**
   * Constructs a new Region.
   *
   * @param country     The ISO 3166-1 alpha-2 code for the country the region is associated with.
   * @param code        The ISO 3166-2 subdivision code for the region.
   * @param localeId    The Unicode locale identifier for the region.
   * @param sortIndex   The sort index for the region.
   * @param name        The name of the region.
   * @param description The description for the region.
   */
  constructor(country: string, code: string, localeId: string, sortIndex: number, name: string,
              description: string) {
    this.country = country;
    this.code = code;
    this.localeId = localeId;
    this.sortIndex = sortIndex;
    this.name = name;
    this.description = description;
  }
}
