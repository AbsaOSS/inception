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

import {InceptionInjector} from './inception-injector';
import {NavigationItem} from './layout/services/navigation-item';
import {NavigationService} from './layout/services/navigation.service';

/**
 * The InceptionAppModule class provides the base class that all application module classes that
 * make use of the Inception framework.
 *
 * @author Marcus Portmann
 */
export abstract class InceptionAppModule {

  /**
   * Constructs a new InceptionAppModule.
   */
  protected constructor() {

    const navigationService: NavigationService = InceptionInjector.get(NavigationService);

    if (navigationService) {
      navigationService.initNavigation(this.initNavigation());
    } else {
      console.error('Failed to initialise the Inception Navigation Service');
    }
  }

  /**
   * Initialise the navigation for the application.
   *
   * @returns The navigation for the application.
   */
  protected abstract initNavigation(): NavigationItem[];
}
