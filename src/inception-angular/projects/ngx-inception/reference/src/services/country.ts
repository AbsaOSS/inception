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
 * The Country class holds the information for a country.
 *
 * @author Marcus Portmann
 */
export class Country {

  /**
   * The ISO 3166-1 alpha-2 code for the country.
   */
  code: string;

  /**
   * The description for the country.
   */
  description: string;

  /**
   * The ISO 3166-1 alpha-3 code for the country.
   */
  iso3Code: string;

  /**
   * The Unicode locale identifier for the country.
   */
  localeId: string;

  /**
   * The name of the country.
   */
  name: string;

  /**
   * The nationality for the country.
   */
  nationality: string;

  /**
   * The short name for the country.
   */
  shortName: string;

  /**
   * The sort index for the country.
   */
  sortIndex: number;

  /**
   * The code for the sovereign state the country is associated with.
   */
  sovereignState: string;

  /**
   * Constructs a new Country.
   *
   * @param code           The ISO 3166-1 alpha-2 code for the country.
   * @param iso3Code       The ISO 3166-1 alpha-3 code for the country.
   * @param localeId       The Unicode locale identifier for the country.
   * @param sortIndex      The sort index for the country.
   * @param name           The name of the country.
   * @param shortName      The short name for the country.
   * @param description    The description for the country.
   * @param sovereignState The code for the sovereign state the country is associated with.
   * @param nationality    The nationality for the country.
   */
  constructor(code: string, iso3Code: string, localeId: string, sortIndex: number, name: string,
              shortName: string, description: string, sovereignState: string, nationality: string) {
    this.code = code;
    this.iso3Code = iso3Code;
    this.localeId = localeId;
    this.sortIndex = sortIndex;
    this.name = name;
    this.shortName = shortName;
    this.description = description;
    this.sovereignState = sovereignState;
    this.nationality = nationality;
  }
}
