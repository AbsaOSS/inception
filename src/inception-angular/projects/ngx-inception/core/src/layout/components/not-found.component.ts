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

import {Component} from '@angular/core';

/**
 * The NotFoundComponent class implements the not found component.
 *
 * @author Marcus Portmann
 */
@Component({
  template: `
    <div class="app flex-row align-items-center">
      <div class="container">
        <div class="row justify-content-center">
          <div class="col-md-6">
            <div class="clearfix">
              <h1 class="float-left display-3 mr-4">404</h1>
              <h4 class="pt-3" i18n="@@not_found_component_heading">Oops! You're lost.</h4>
              <p class="text-muted" i18n="@@not_found_component_message">The page you are looking
                for was not found.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class NotFoundComponent {

  /**
   * Constructs a new NotFoundComponent.
   */
  constructor() {
  }
}
