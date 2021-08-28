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
 * The NavigationBadge class holds the information for a navigation badge.
 *
 * @author Marcus Portmann
 */
export class NavigationBadge {

  /**
   * The text for the navigation badge.
   */
  text: string;

  /**
   * The navigation badge type.
   */
  variant: string;

  /**
   * Constructs a new NavigationBadge.
   *
   * @param variant The variant for the navigation badge.
   * @param text    The text for the navigation badge.
   */
  constructor(variant: string, text: string) {
    this.variant = variant;
    this.text = text;
  }
}
