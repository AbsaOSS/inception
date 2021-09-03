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

import {Component, ElementRef, Inject, Input, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {INCEPTION_CONFIG, InceptionConfig} from '../../inception-config';
import {Session} from '../../session/services/session';
import {SessionService} from '../../session/services/session.service';
import {Replace} from '../../util/replace';

/**
 * The AdminHeaderComponent class implements the admin header component.
 *
 * @author Marcus Portmann
 */
@Component({
  // tslint:disable-next-line
  selector: 'admin-header',
  template: `
    <header class="admin-header">
      <button class="toggler d-lg-none" type="button" sidebarToggler>
        <span class="toggler-icon"></span>
      </button>
      <a class="brand" href="#">
        <div class="brand-full"></div>
        <div class="brand-minimized"></div>
      </a>

      <button class="toggler d-md-down-none" type="button" [sidebarToggler]="sidebarToggler">
        <span class="toggler-icon"></span>
      </button>

      <ul class="nav ml-auto">
        <!--
        <li class="nav-item d-md-down-none">
          <a href="#" class="nav-link"><i class="icon-bell"></i><span class="badge badge-danger">5</span></a>
        </li>
        -->
        <li *ngIf="isLoggedIn() | async; else login_link" class="nav-item"
            [matMenuTriggerFor]="userMenu">
          <a href="#" class="nav-link" (click)="false">
            <span class="user-icon"></span>
            <span class="user-name d-md-down-none">{{ userName() | async }}</span>
          </a>
        </li>

        <mat-menu #userMenu="matMenu" yPosition="below" overlapTrigger="false" class="user-menu">
          <a *ngIf="isUserProfileEnabled()" mat-menu-item (click)="profile()">
            <i class="fas fa-user-circle"></i>
            <span i18n="@@admin_header_component_menu_item_profile">Profile</span>
          </a>
          <a mat-menu-item (click)="logout()">
            <i class="fas fa-sign-out-alt"></i>
            <span i18n="@@admin_header_component_menu_item_logout">Logout</span>
          </a>
        </mat-menu>

        <ng-template #login_link>
          <li class="nav-item">
            <a class="nav-link" (click)="login()">
              <span class="login-icon"></span>
              <span class="login d-md-down-none"
                    i18n="@@admin_header_component_link_login">Login</span>
            </a>
          </li>
        </ng-template>
      </ul>
    </header>
  `
})
export class AdminHeaderComponent implements OnInit {

  @Input() fixed = false;

  // tslint:disable-next-line
  @Input() sidebarToggler!: any;

  /**
   * Constructs a new AdminHeaderComponent.
   *
   * @param elementRef     The element reference.
   * @param config         The Inception configuration.
   * @param router         The router.
   * @param sessionService The session service.
   */
  constructor(private elementRef: ElementRef,
              @Inject(INCEPTION_CONFIG) private config: InceptionConfig, private router: Router,
              private sessionService: SessionService) {
  }

  isLoggedIn(): Observable<boolean> {
    return this.sessionService.session$.pipe(map((session: Session | null) => {
      return (!!session);
    }));
  }

  // // tslint:disable-next-line
  // static breakpoint(breakpoint: any): any {
  //   console.log(breakpoint);
  //   return breakpoint ? breakpoint : '';
  // }

  isUserProfileEnabled(): boolean {
    return this.config.userProfileEnabled;
  }

  login(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['/login']);
  }

  logout(): void {
    this.sessionService.logout();
  }

  ngOnInit(): void {
    Replace(this.elementRef);

    if (this.fixed) {
      const bodySelector = document.querySelector('body');

      if (bodySelector) {
        bodySelector.classList.add('admin-header-fixed');
      }
    }
  }

  profile(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['/profile']).then((result: boolean) => {
    });
  }

  userName(): Observable<string> {
    return this.sessionService.session$.pipe(map((session: Session | null) => {
      if (!!session) {
        return session.name;
      } else {
        return '';
      }
    }));
  }
}


// <a mat-menu-item>
// <i class="fas fa-cogs"></i>
//   <span i18n="@@admin_header_component_menu_item_settings">Settings</span>
//   </a>
