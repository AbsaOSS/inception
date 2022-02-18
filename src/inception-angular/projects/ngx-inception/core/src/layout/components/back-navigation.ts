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

import {NavigationExtras} from '@angular/router';

/**
 * The BackNavigation class holds the information for the back navigation for an admin container
 * view component.
 *
 * @author Marcus Portmann
 */
export class BackNavigation {

  /**
   * The router navigation commands for the back navigation.
   */
    // eslint:disable-next-line
  commands: any[];

  /**
   * The optional router navigation extras for the back navigation.
   */
  extras?: NavigationExtras;

  /**
   * The title for the back navigation.
   */
  title: string;

  /**
   * Constructs a new BackNavigation.
   *
   * @param title    The title for the back navigation.
   * @param commands The router navigation commands for the back navigation.
   * @param extras   The optional router navigation extras for the back navigation.
   */
  // eslint:disable-next-line
  constructor(title: string, commands: any[], extras?: NavigationExtras) {
    this.title = title;
    this.commands = commands;
    this.extras = extras;
  }
}
