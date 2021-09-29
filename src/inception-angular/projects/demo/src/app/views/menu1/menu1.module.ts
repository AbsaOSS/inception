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

import {CoreModule} from '@absaoss/ngx-inception/core';
import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {Menu1Component} from './menu1.component';

const routes: Routes = [{
  path: '',
  component: Menu1Component,
}
];

/**
 * The Menu1Module class implements a module that contains a single component that is loaded into
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
  declarations: [Menu1Component]
})
export class Menu1Module {
}
