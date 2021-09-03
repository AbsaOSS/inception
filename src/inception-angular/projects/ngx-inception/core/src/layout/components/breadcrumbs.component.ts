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

import {Component, ElementRef, Input, OnDestroy, OnInit} from '@angular/core';
import {Observable, Subscription} from 'rxjs';
import {Replace} from '../../util/replace';
import {Breadcrumb} from '../services/breadcrumb';
import {BreadcrumbsService} from '../services/breadcrumbs.service';

/**
 * The BreadcrumbsComponent class implements the breadcrumbs component.
 *
 * @author Marcus Portmann
 */
@Component({
  // tslint:disable-next-line
  selector: 'breadcrumbs',
  template: `
    <ol class="breadcrumb">
      <ng-template ngFor let-breadcrumb [ngForOf]="breadcrumbs | async" let-last=last>
        <li class="breadcrumb-item"
            *ngIf="(breadcrumb.label)"
            [ngClass]="{active: last}">
          <a *ngIf="!last && (!!breadcrumb.url)"
             [routerLink]="breadcrumb.url">{{breadcrumb.label}}</a>
          <span *ngIf="last || (!breadcrumb.url)">{{breadcrumb.label}}</span>
        </li>
      </ng-template>
    </ol>
  `
})
export class BreadcrumbsComponent implements OnInit, OnDestroy {

  breadcrumbs: Observable<Array<{}>>;
  @Input() fixed = false;
  private subscriptions: Subscription = new Subscription();

  /**
   * Constructs a new BreadcrumbsComponent.
   *
   * @param elementRef         The element reference.
   * @param breadcrumbsService The breadcrumbs service.
   */
  constructor(private elementRef: ElementRef, private breadcrumbsService: BreadcrumbsService) {
    this.breadcrumbs = this.breadcrumbsService.breadcrumbs$;
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  ngOnInit(): void {
    Replace(this.elementRef);

    this.subscriptions.add(this.breadcrumbsService.breadcrumbs$.subscribe((breadcrumbs: Breadcrumb[]) => {
      const bodySelector = document.querySelector('body');

      if (bodySelector) {
        if ((this.fixed) && (breadcrumbs.length > 0)) {
          bodySelector.classList.add('breadcrumbs-fixed');
        } else {
          bodySelector.classList.remove('breadcrumbs-fixed');
        }
      }
    }));
  }
}
