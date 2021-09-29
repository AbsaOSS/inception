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

import {NavigationBadge} from './navigation-badge';

/**
 * The NavigationItem class holds the information for a navigation item.
 *
 * @author Marcus Portmann
 */
export class NavigationItem {

  /**
   * The optional authorities that are used to restrict access to the navigation item.
   */
  authorities: string[];

  /**
   * The optional navigation badge associated with the navigation item.
   */
  badge?: NavigationBadge;

  /**
   * The optional child navigation items.
   */
  children: NavigationItem[];

  /**
   * The optional class associated with the navigation item.
   */
  cssClass?: string;

  /**
   * The optional divider indicator.
   */
  divider = false;

  /**
   * The optional icon associated with the navigation item.
   */
  icon: string;

  /**
   * The optional name of navigation item.
   */
  name: string;

  /**
   * The optional title indicator.
   */
  title = false;

  /**
   * The optional url associated with the navigation item.
   */
  url: string;

  /**
   * The optional variant to apply to the navigation item.
   */
  variant?: string;

  /**
   * Constructs a new NavigationItem.
   *
   * @param icon        The icon associated with the navigation item.
   * @param name        The name of navigation item.
   * @param url         The url associated with the navigation item.
   * @param authorities The optional authorities that are used to restrict access to the navigation item.
   * @param children    The optional child navigation items.
   * @param cssClass    The optional CSS class to apply to the navigation item.
   * @param variant     The optional variant to apply to the navigation item.
   * @param badge       The optional navigation badge associated with the navigation item.
   * @param divider     The optional divider indicator.
   * @param title       The optional title indicator.
   */
  constructor(icon: string, name: string, url: string, authorities?: string[], children?: NavigationItem[],
              cssClass?: string, variant?: string, badge?: NavigationBadge, divider?: boolean, title?: boolean) {
    this.icon = icon;
    this.name = name;
    this.url = url;
    this.authorities = (!!authorities) ? authorities : [];
    this.children = (!!children) ? children : [];
    this.cssClass = cssClass;
    this.variant = variant;
    this.badge = badge;
    if (divider) {
      this.divider = divider;
    }
    if (title) {
      this.title = title;
    }
  }
}
