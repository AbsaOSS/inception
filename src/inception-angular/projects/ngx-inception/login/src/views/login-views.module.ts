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
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import '@angular/localize/init';
import {RouterModule, Routes} from '@angular/router';
import {CoreModule} from 'ngx-inception/core';
import {ExpiredPasswordTitleResolver} from './expired-password-title-resolver';
import {ExpiredPasswordComponent} from './expired-password.component';
import {ForgottenPasswordTitleResolver} from './forgotten-password-title-resolver';
import {ForgottenPasswordComponent} from './forgotten-password.component';
import {LoginTitleResolver} from './login-title-resolver';
import {LoginComponent} from './login.component';
import {ResetPasswordTitleResolver} from './reset-password-title-resolver';
import {ResetPasswordComponent} from './reset-password.component';
import {SelectTenantTitleResolver} from './select-tenant-title-resolver';
import {SelectTenantComponent} from './select-tenant.component';

const routes: Routes = [{
  path: '',
  component: LoginComponent,
  resolve: {
    title: LoginTitleResolver
  }
}, {
  path: 'expired-password',
  component: ExpiredPasswordComponent,
  resolve: {
    title: ExpiredPasswordTitleResolver
  }
}, {
  path: 'forgotten-password',
  component: ForgottenPasswordComponent,
  resolve: {
    title: ForgottenPasswordTitleResolver
  }
}, {
  path: 'reset-password',
  component: ResetPasswordComponent,
  resolve: {
    title: ResetPasswordTitleResolver
  }
}, {
  path: 'select-tenant',
  component: SelectTenantComponent,
  resolve: {
    title: SelectTenantTitleResolver
  }
}
];

@NgModule({
  declarations: [
    // Components
    ExpiredPasswordComponent, ForgottenPasswordComponent, LoginComponent, ResetPasswordComponent,
    SelectTenantComponent
  ],
  imports: [
    // Angular modules
    CommonModule, FormsModule, ReactiveFormsModule, RouterModule.forChild(routes),

    // Inception modules
    CoreModule
  ],
  providers: [
    // Resolvers
    ExpiredPasswordTitleResolver, ForgottenPasswordTitleResolver, LoginTitleResolver, ResetPasswordTitleResolver,
    SelectTenantTitleResolver
  ]
})
export class LoginViewsModule {
}
