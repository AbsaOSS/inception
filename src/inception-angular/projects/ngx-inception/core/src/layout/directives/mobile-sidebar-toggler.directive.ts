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
 * The AppMobileSidebarTogglerDirective class implements the app mobile sidebar toggler directive.
 *
 * @author Marcus Portmann
 */
@Directive({
  // tslint:disable-next-line:directive-selector
  selector: '[mobileSidebarToggler]'
})
export class MobileSidebarTogglerDirective {

  /**
   * Constructs a new AppMobileSidebarTogglerDirective.
   */
  constructor() {
  }

  // tslint:disable-next-line
  @HostListener('click', ['$event']) toggleOpen($event: any): void {
    $event.preventDefault();

    const bodySelector = document.querySelector('body');

    if (bodySelector) {
      bodySelector.classList.toggle('sidebar-mobile-show');
    }
  }
}
