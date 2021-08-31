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
 * The Language class holds the information for a language.
 *
 * @author Marcus Portmann
 */
export class Language {

  /**
   * The ISO 639-1 alpha-2 code for the language.
   */
  code: string;

  /**
   * The description for the language.
   */
  description: string;

  /**
   * The ISO 639-2 alpha-3 code for the language.
   */
  iso3Code: string;

  /**
   * The Unicode locale identifier for the language.
   */
  localeId: string;

  /**
   * The name of the language.
   */
  name: string;

  /**
   * The short name for the language.
   */
  shortName: string;

  /**
   * The sort index for the language.
   */
  sortIndex: number;

  /**
   * Constructs a new Language.
   *
   * @param code        The ISO 639-1 alpha-2 for the language.
   * @param iso3Code    The ISO 639-2 alpha-3 code for the language.
   * @param localeId    The Unicode locale identifier for the language.
   * @param sortIndex   The sort index for the language.
   * @param name        The name of the language.
   * @param shortName   The short name for the language.
   * @param description The description for the language.
   */
  constructor(code: string, iso3Code: string, localeId: string, sortIndex: number, name: string, shortName: string,
              description: string) {
    this.code = code;
    this.iso3Code = iso3Code;
    this.localeId = localeId;
    this.sortIndex = sortIndex;
    this.name = name;
    this.shortName = shortName;
    this.description = description;
  }
}
