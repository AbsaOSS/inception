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

import {Directive, HostListener} from '@angular/core';

/**
 * The SidebarMinimizerDirective class implements the sidebar minimizer directive.
 *
 * @author Marcus Portmann
 */
@Directive({
  // eslint-disable-next-line @angular-eslint/directive-selector
  selector: '[sidebarMinimizer]'
})
export class SidebarMinimizerDirective {

  /**
   * Constructs a new SidebarMinimizerDirective.
   */
  constructor() {
  }

  // eslint:disable-next-line
  @HostListener('click', ['$event']) toggleOpen($event: any): void {
    $event.preventDefault();

    const bodySelector = document.querySelector('body');

    if (bodySelector) {
      bodySelector.classList.toggle('sidebar-minimized');
    }
  }
}
