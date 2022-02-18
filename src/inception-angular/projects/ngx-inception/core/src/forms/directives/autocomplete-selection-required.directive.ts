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

import {AfterViewInit, Directive, Host, Input, OnDestroy, Self} from '@angular/core';
import {NgControl} from '@angular/forms';
import {MatAutocomplete, MatAutocompleteTrigger} from '@angular/material/autocomplete';
import {MatOptionSelectionChange} from '@angular/material/core';
import {Subscription} from 'rxjs';

@Directive({
  // eslint-disable-next-line @angular-eslint/directive-selector
  selector: '[autocompleteSelectionRequired]'
})
export class AutocompleteSelectionRequiredDirective implements AfterViewInit, OnDestroy {

  @Input()
  matAutocomplete: MatAutocomplete | undefined;

  private subscriptions: Subscription = new Subscription();

  constructor(@Host() @Self() private readonly autoCompleteTrigger: MatAutocompleteTrigger,
              private readonly ngControl: NgControl) {
  }

  ngAfterViewInit() {
    this.subscriptions.add(this.autoCompleteTrigger.panelClosingActions.subscribe((next: MatOptionSelectionChange | null) => {
      if (this.matAutocomplete) {
        const selected = this.matAutocomplete.options
        .map(option => option.value)
        .find(option => option === this.ngControl.value);

        if (selected == null) {
          if (this.ngControl.control && (!!this.ngControl.control.value)) {
            this.ngControl.control.setValue('');
          }
        }
      }
    }));
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }
}
