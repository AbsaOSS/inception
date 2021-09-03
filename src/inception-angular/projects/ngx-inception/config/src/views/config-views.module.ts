/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule, Routes} from '@angular/router';
import {CanActivateFunctionGuard, CoreModule} from '@absaoss/ngx-inception/core';
import {ConfigTitleResolver} from './config-title-resolver';
import {ConfigsTitleResolver} from './configs-title-resolver';
import {ConfigsComponent} from './configs.component';
import {EditConfigTitleResolver} from './edit-config-title-resolver';
import {EditConfigComponent} from './edit-config.component';
import {NewConfigTitleResolver} from './new-config-title-resolver';
import {NewConfigComponent} from './new-config.component';

const routes: Routes = [{
  path: '',
  pathMatch: 'full',
  canActivate: [CanActivateFunctionGuard],
  component: ConfigsComponent,
  data: {
    authorities: ['ROLE_Administrator', 'FUNCTION_Config.ConfigAdministration']
  }
}, {
  path: 'new',
  pathMatch: 'full',
  canActivate: [CanActivateFunctionGuard],
  component: NewConfigComponent,
  data: {
    authorities: ['ROLE_Administrator', 'FUNCTION_Config.ConfigAdministration']
  },
  resolve: {
    title: NewConfigTitleResolver
  }
}, {
  path: ':key',
  pathMatch: 'full',
  redirectTo: ':key/edit'
}, {
  path: ':key',
  resolve: {
    title: ConfigTitleResolver
  },
  children: [{
    path: 'edit',
    canActivate: [CanActivateFunctionGuard],
    component: EditConfigComponent,
    data: {
      authorities: ['ROLE_Administrator', 'FUNCTION_Config.ConfigAdministration']
    },
    resolve: {
      title: EditConfigTitleResolver
    }
  }
  ]
}
];

@NgModule({
  declarations: [
    // Components
    ConfigsComponent, EditConfigComponent, NewConfigComponent
  ],
  imports: [
    // Angular modules
    CommonModule, FormsModule, ReactiveFormsModule, RouterModule.forChild(routes),

    // Inception modules
    CoreModule
  ],
  providers: [
    // Resolvers
    ConfigTitleResolver, ConfigsTitleResolver, EditConfigTitleResolver,
    NewConfigTitleResolver
  ]
})
export class ConfigViewsModule {
}
