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
import {SidebarNavDropdownDirective} from './sidebar-nav-dropdown.directive';

/**
 * The SidebarNavDropdownTogglerDirective class implements the sidebar nav dropdown toggle
 * directive.
 *
 * @author Marcus Portmann
 */
@Directive({
  // tslint:disable-next-line
  selector: '[sidebarNavDropdownToggler]'
})
export class SidebarNavDropdownTogglerDirective {

  /**
   * Constructs a new SidebarNavDropdownDirective.
   *
   * @param dropdown The sidebar nav dropdown directive.
   */
  constructor(private dropdown: SidebarNavDropdownDirective) {
  }

  // tslint:disable-next-line
  @HostListener('click', ['$event']) toggleOpen($event: any): void {
    $event.preventDefault();
    this.dropdown.toggle();
  }
}
