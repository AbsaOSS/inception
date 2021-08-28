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
import {CoreModule} from "../../../../../../../dist/ngx-inception/core";
import {Menu321TitleResolver} from './menu321-title-resolver';
import {Menu321Component} from './menu321.component';
import {Menu322TitleResolver} from './menu322-title-resolver';
import {Menu322Component} from './menu322.component';

const routes: Routes = [{
  path: '',
  redirectTo: 'menu321'
}, {
  path: 'menu321',
  component: Menu321Component,
  resolve: {
    title: Menu321TitleResolver
  }
}, {
  path: 'menu322',
  component: Menu322Component,
  resolve: {
    title: Menu322TitleResolver
  }
}
];

/**
 * The Menu32Module class implements a module that contains two components that can be loaded into
 * the AdminContainerComponent component when the corresponding menu option is selected.
 *
 * @author Marcus Portmann
 */
@NgModule({
  imports: [CommonModule,
    // Angular modules
    RouterModule.forChild(routes),

    // Inception modules
    CoreModule
  ],
  declarations: [Menu321Component, Menu322Component],
  providers: [Menu321TitleResolver, Menu322TitleResolver]
})
export class Menu32Module {
}
