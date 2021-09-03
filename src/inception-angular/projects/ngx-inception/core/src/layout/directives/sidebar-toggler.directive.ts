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

import {Directive, HostListener, Input, OnInit} from '@angular/core';
import {toggleClasses} from '../../util/toggle-classes';
import {SIDEBAR_CSS_CLASSES} from '../components/sidebar-css-classes';

/**
 * The SidebarTogglerDirective class implements the sidebar toggler directive.
 *
 * @author Marcus Portmann
 */
@Directive({
  // tslint:disable-next-line
  selector: '[sidebarToggler]'
})
export class SidebarTogglerDirective implements OnInit {

  bp?: string;
  @Input('sidebarToggler') breakpoint?: string;

  /**
   * Constructs a new SidebarTogglerDirective.
   */
  constructor() {
  }

  ngOnInit(): void {
    this.bp = this.breakpoint;
  }

  // tslint:disable-next-line
  @HostListener('click', ['$event']) toggleOpen($event: any): void {
    $event.preventDefault();
    let cssClass;
    (!!this.bp) ? cssClass = `sidebar-${this.bp}-show` : cssClass = SIDEBAR_CSS_CLASSES[0];
    toggleClasses(cssClass, SIDEBAR_CSS_CLASSES);
  }
}
