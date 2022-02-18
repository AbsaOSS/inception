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

import {Component, ElementRef, Input} from '@angular/core';
import {Router} from '@angular/router';
import {NavigationItem} from '../services/navigation-item';

/**
 * The SidebarNavItemComponent class implements the sidebar nav item component.
 *
 * @author Marcus Portmann
 */
@Component({
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'sidebar-nav-item',
  template: `
    <ng-container *ngIf="this.navItem">
      <ng-container *ngIf="isDivider(); else checkForTitle">
        <li class="nav-divider"></li>
      </ng-container>
      <ng-template #checkForTitle>
        <ng-container *ngIf="isTitle(); else checkForDropdown">
          <li class="nav-title">{{navItem.name}}</li>
        </ng-container>
      </ng-template>
      <ng-template #checkForDropdown>
        <ng-container *ngIf="isDropdown(); else sidebarNavLink">
          <li
            [ngClass]="!!this.navItem.cssClass ? 'nav-item nav-dropdown ' + navItem.cssClass : 'nav-item nav-dropdown'"
            [class.open]="isActive()" routerLinkActive="open" sidebarNavDropdown>
            <div class="sidebar-nav-dropdown">
              <a class="nav-link nav-dropdown-toggle" sidebarNavDropdownToggler>
                <i *ngIf="this.navItem.icon && !!this.navItem.icon"
                   class="nav-icon {{ navItem.icon }}"></i>
                {{ navItem.name }}
                <span *ngIf="this.navItem.badge && !!this.navItem.badge"
                      [ngClass]="'badge badge-' + navItem.badge.variant">
                  {{ navItem.badge.text }}
                </span>
              </a>
              <ul class="nav-dropdown-items">
                <sidebar-nav-item *ngFor="let child of navItem.children"
                                  [navItem]='child'></sidebar-nav-item>
              </ul>
            </div>
          </li>
        </ng-container>
      </ng-template>
      <ng-template #sidebarNavLink>
        <li [ngClass]="!!this.navItem.cssClass ? 'nav-item ' + navItem.cssClass : 'nav-item'">
          <a *ngIf="!isExternalLink(); else externalLink"
             [ngClass]="!!this.navItem.variant ? 'nav-link nav-link-' + navItem.variant : 'nav-link'"
             routerLinkActive="active"
             [routerLink]="[navItem.url]"
             (click)="hideMobile()">
            <i *ngIf="!!this.navItem.icon;" class="nav-icon {{ navItem.icon }}"></i>
            {{ navItem.name }}
            <span *ngIf="this.navItem.badge && !!this.navItem.badge"
                  [ngClass]="'badge badge-' + navItem.badge.variant">
            {{ navItem.badge.text }}
          </span>
          </a>
          <ng-template #externalLink>
            <a
              [ngClass]="!!this.navItem.variant ? 'nav-link nav-link-' + navItem.variant : 'nav-link'"
              href="{{navItem.url}}">
              <i *ngIf="!!this.navItem.icon;" class="nav-icon {{ navItem.icon }}"></i>
              {{ navItem.name }}
              <span *ngIf="this.navItem.badge && !!this.navItem.badge"
                    [ngClass]="'badge badge-' + navItem.badge.variant">
              {{ navItem.badge.text }}
            </span>
            </a>
          </ng-template>
        </li>
      </ng-template>
    </ng-container>
  `,
  styles: ['.nav-dropdown-toggle { cursor: pointer; }']
})
export class SidebarNavItemComponent {

  @Input() navItem?: NavigationItem;

  /**
   * Constructs a new SidebarNavItemComponent.
   *
   * @param elementRef The element reference.
   * @param router     The router.
   */
  constructor(private elementRef: ElementRef, private router: Router) {
  }

  hideMobile() {
    if (document.body.classList.contains('sidebar-show')) {
      document.body.classList.toggle('sidebar-show');
    }
  }

  isActive(): boolean {
    return this.navItem ? !!this.navItem.url && this.router.isActive(this.navItem.url, false) : false;
  }

  isDivider(): boolean {
    return this.navItem ? this.navItem.divider : false;
  }

  isDropdown(): boolean {
    return this.navItem ? this.navItem.children.length > 0 : false;
  }

  isExternalLink(): boolean {
    return this.navItem ? !!this.navItem.url && (this.navItem.url.substring(0, 4) === 'http') : false;
  }

  isTitle(): boolean {
    return this.navItem ? this.navItem.title : false;
  }
}
