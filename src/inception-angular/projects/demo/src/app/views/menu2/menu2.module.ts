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
import {Menu21Component} from './menu21.component';
import {Menu22Component} from './menu22.component';

const routes: Routes = [{
  path: '',
  redirectTo: 'menu21',
}, {
  path: 'menu21',
  component: Menu21Component,
  data: {
    title: 'Menu 2.1',
  }
}, {
  path: 'menu22',
  component: Menu22Component,
  data: {
    title: 'Menu 2.2',
  }
}
];

/**
 * The Menu2Module class implements a module that contains two components that can be loaded into
 * the AdminContainerComponent component when the corresponding menu option is selected.
 *
 * @author Marcus Portmann
 */
@NgModule({
  imports: [
    // Angular modules
    CommonModule, RouterModule.forChild(routes),

    // Inception modules
    CoreModule
  ],
  declarations: [Menu21Component, Menu22Component]
})
export class Menu2Module {
}
