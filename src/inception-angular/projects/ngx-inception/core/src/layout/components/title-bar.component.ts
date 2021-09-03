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

import {Component, ElementRef, Input, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Observable} from 'rxjs';
import {Replace} from '../../util/replace';
import {TitleBarService} from '../services/title-bar.service';
import {BackNavigation} from './back-navigation';

/**
 * The TitleBarComponent class implements the title bar component.
 *
 * @author Marcus Portmann
 */
@Component({
  // tslint:disable-next-line
  selector: 'title-bar',
  template: `
    <div *ngIf="title | async as title; else noTitle" class="title-bar">
      <div *ngIf="backNavigation | async as backNavigation" class="back"
           (click)="navigateBack(backNavigation)">
        <span class="fa fa-chevron-left"></span> {{backNavigation.title}}</div>
      <div class="title">{{title}}</div>
    </div>

    <ng-template #noTitle>
      <div class="title-bar">
        <div class="title">No Title</div>
      </div>
    </ng-template>
  `
})
export class TitleBarComponent implements OnInit {

  @Input() fixed = false;

  /**
   * Constructs a new TitleBarComponent.
   *
   * @param elementRef      The element reference.
   * @param router          The router.
   * @param titleBarService The title bar service.
   */
  constructor(private elementRef: ElementRef, private router: Router, private titleBarService: TitleBarService) {
  }

  /**
   * The back navigation.
   */
  get backNavigation(): Observable<BackNavigation | null> {
    return this.titleBarService.backNavigation$;
  }

  /**
   * The title.
   */
  get title(): Observable<string | null> {
    return this.titleBarService.title$;
  }

  /**
   * Navigate back.
   */
  navigateBack(backNavigation: BackNavigation): void {
    if (backNavigation) {
      // noinspection JSIgnoredPromiseFromCall
      this.router.navigate(backNavigation.commands, backNavigation.extras).then(navigationResult => {
      });
    }
  }

  ngOnInit(): void {
    Replace(this.elementRef);

    if (!!this.fixed) {
      const bodySelector = document.querySelector('body');

      if (bodySelector) {
        bodySelector.classList.add('title-bar-fixed');
      }
    }
  }
}
