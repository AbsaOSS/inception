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

import {
  Component, ElementRef, EventEmitter, OnDestroy, OnInit, Output, ViewChild
} from '@angular/core';
import {fromEvent, Subscription} from 'rxjs';
import {debounceTime, distinctUntilChanged, tap} from 'rxjs/operators';

@Component({
  // tslint:disable-next-line
  selector: 'table-filter',
  template: `
    <div class="table-filter-container">
      <div class="table-filter-icon">
        <i class="fa fa-search"></i>
      </div>
      <input class="table-filter-input" matInput #tableFilterInput placeholder="Search..."
             autocomplete="off">
      <button class="table-filter-reset" mat-icon-button *ngIf="changed | async"
              (click)="reset(true)">
        <i class="fa fa-times"></i>
      </button>
    </div>
  `,
  styles: [`
    .table-filter-container {
      display: flex;
      align-items: center;
      color: rgba(0, 0, 0, 0.42);
      border: 1px solid rgba(0, 0, 0, 0.12);
      border-radius: 2px;
    }

    .table-filter-icon {
      display: flex;
      width: 28px;
      height: 28px;
      align-items: center;
      justify-content: center;
      border: none;
    }

    .table-filter-input {
      -webkit-appearance: none;
      border-radius: 0;
      width: 12em;
      height: 28px;
      padding-left: 2px;
      padding-right: 28px;
      border: none;
      color: inherit;
      background-color: transparent;
    }

    .table-filter-reset {
      position: absolute;
      right: 16px;
      width: 28px;
      height: 28px;
      line-height: 28px;
      min-width: 28px;
      padding: 0;
      border: none !important;
    }
  `
  ]
}) // tslint:disable-next-line
export class TableFilterComponent implements OnInit, OnDestroy {

  @Output() changed: EventEmitter<string> = new EventEmitter<string>();

  filter = '';

  @ViewChild('tableFilterInput', {static: true}) tableFilterInput!: ElementRef;

  private tableFilterInputSubscription?: Subscription;

  ngOnDestroy(): void {
    if (this.tableFilterInputSubscription) {
      this.tableFilterInputSubscription.unsubscribe();
    }
  }

  ngOnInit(): void {
    this.tableFilterInputSubscription = fromEvent(this.tableFilterInput.nativeElement, 'keyup')
    .pipe(debounceTime(250), distinctUntilChanged(), tap(() => {
      this.filter = this.tableFilterInput.nativeElement.value;
      this.changed.emit(this.filter);
    }))
    .subscribe();
  }

  reset(emitEvent: boolean): void {
    this.filter = '';
    this.tableFilterInput.nativeElement.value = this.filter;

    if (emitEvent) {
      this.changed.emit(this.filter);
    }
  }
}
