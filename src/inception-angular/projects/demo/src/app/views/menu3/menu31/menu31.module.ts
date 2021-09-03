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

import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';

import {RouterModule, Routes} from '@angular/router';
import {CoreModule} from '@absaoss/ngx-inception/core';
import {Menu311Component} from './menu311.component';
import {Menu312Component} from './menu312.component';

const routes: Routes = [{
  path: '',
  redirectTo: 'menu311'
}, {
  path: 'menu311',
  component: Menu311Component,
  data: {
    title: 'Menu 3.1.1',
  }
}, {
  path: 'menu312',
  component: Menu312Component,
  data: {
    title: 'Menu 3.1.2',
  }
}
];

/**
 * The Menu31Module class implements a module that contains two components that can be loaded into
 * the AdminContainerComponent component when the corresponding menu option is selected.
 *
 * @author Marcus Portmann
 */
@NgModule({
  imports: [CommonModule,
    // Angular Modules
    RouterModule.forChild(routes),

    // Inception modules
    CoreModule
  ],
  declarations: [Menu311Component, Menu312Component]
})
export class Menu31Module {
}
