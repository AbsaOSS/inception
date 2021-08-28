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

import {Component, ElementRef} from '@angular/core';

/**
 * The SpinnerComponent class implements the spinner component.
 *
 * @author Marcus Portmann
 */
@Component({
  // tslint:disable-next-line
  selector: 'spinner',
  template: `
    <div class="spinner spinner-oval"></div>`,
  styles: [`
    .spinner {
      display: block;
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      z-index: 10000;
      background-color: rgba(0, 0, 0, 0.6);
      background-position: center center;
      background-size: 50px 50px;
      background-repeat: no-repeat, repeat;
    }

    .spinner-oval {
      background-image: url("data:image/svg+xml,%3Csvg width='38' height='38' viewBox='0 0 38 38' xmlns='http://www.w3.org/2000/svg' stroke='%23fff'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg transform='translate(1 1)' stroke-width='2'%3E%3Ccircle stroke-opacity='.5' cx='18' cy='18' r='18'/%3E%3Cpath d='M36 18c0-9.94-8.06-18-18-18'%3E%3CanimateTransform attributeName='transform' type='rotate' from='0 18 18' to='360 18 18' dur='1s' repeatCount='indefinite'/%3E%3C/path%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
    }
  `
  ]
})
export class SpinnerComponent {

  /**
   * Constructs a new SpinnerComponent.
   *
   * @param elementRef The element reference.
   */
  constructor(private elementRef: ElementRef) {
  }

  show(): void {
    this.elementRef.nativeElement.focus();
  }
}
