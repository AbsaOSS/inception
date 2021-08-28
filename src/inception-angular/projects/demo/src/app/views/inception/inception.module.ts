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

const routes: Routes = [{
  path: '',
  redirectTo: 'components/buttons'
}, {
  path: 'components',
  loadChildren: () => import('./components/components.module').then(m => m.ComponentsModule),
  data: {
    title: 'Components',
  }
}, {
  path: 'form',
  loadChildren: () => import('./form/form.module').then(m => m.FormModule),
  data: {
    title: 'Form',
  }
}, {
  path: 'tables',
  loadChildren: () => import('./tables/tables.module').then(m => m.TablesModule),
  data: {
    title: 'Tables',
  }
}, {
  path: 'theme',
  loadChildren: () => import('./theme/theme.module').then(m => m.ThemeModule),
  data: {
    title: 'Theme',
  }
}
];

/**
 * The InceptionModule class implements the module that contains the sub-modules that illustrate the
 * use of the Inception framework.
 *
 * @author Marcus Portmann
 */
@NgModule({
  imports: [CommonModule,

    RouterModule.forChild(routes)
  ],
  declarations: []
})
export class InceptionModule {
}
