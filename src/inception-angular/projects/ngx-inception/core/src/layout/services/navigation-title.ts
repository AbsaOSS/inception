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

import {NavigationItem} from './navigation-item';

/**
 * The NavigationTitle class holds the information for a navigation title.
 *
 * @author Marcus Portmann
 */
export class NavigationTitle extends NavigationItem {

  /**
   * Constructs a new NavigationTitle.
   *
   * @param name The name of navigation title.
   */
  constructor(name: string) {
    super('', name, '');

    this.title = true;
  }
}
