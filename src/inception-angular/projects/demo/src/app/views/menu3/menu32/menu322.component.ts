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

import {Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {AdminContainerView, BackNavigation} from '@absaoss/ngx-inception/core';
import {Observable} from 'rxjs';

/**
 * The Menu322Component class implements the menu 3.2.2 component.
 *
 * @author Marcus Portmann
 */
@Component({
  template: `
    <mat-card class="flex-grow-1">
      <mat-card-content>
        Menu 3.2.2
      </mat-card-content>
    </mat-card>
  `
})
export class Menu322Component extends AdminContainerView {

  /**
   * Constructs a new Menu322Component.
   *
   * @param activatedRoute The activated route.
   */
  constructor(private activatedRoute: ActivatedRoute) {
    super();
  }

  /**
   * Tne back navigation for admin container view.
   */
  get backNavigation(): BackNavigation {
    return new BackNavigation($localize`:@@demo_menu322_back_navigation:Menu 3.2.1`,
      ['../menu321'], {relativeTo: this.activatedRoute});
  }

  /**
   * The title for the admin container view.
   */
  get title(): string | Observable<string> {
    return $localize`:@@demo_menu322_title:Custom Menu 3.2.2 Title`
  }
}


