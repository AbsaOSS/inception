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
import {CodeCategoriesTitleResolver} from './code-categories-title-resolver';
import {CodeCategoriesComponent} from './code-categories.component';
import {CodeCategoryTitleResolver} from './code-category-title-resolver';
import {CodeTitleResolver} from './code-title-resolver';
import {CodesTitleResolver} from './codes-title-resolver';
import {CodesComponent} from './codes.component';
import {EditCodeCategoryTitleResolver} from './edit-code-category-title-resolver';
import {EditCodeCategoryComponent} from './edit-code-category.component';
import {EditCodeTitleResolver} from './edit-code-title-resolver';
import {EditCodeComponent} from './edit-code.component';
import {NewCodeCategoryTitleResolver} from './new-code-category-title-resolver';
import {NewCodeCategoryComponent} from './new-code-category.component';
import {NewCodeTitleResolver} from './new-code-title-resolver';
import {NewCodeComponent} from './new-code.component';

const routes: Routes = [{
  path: '',
  pathMatch: 'full',
  canActivate: [CanActivateFunctionGuard],
  component: CodeCategoriesComponent,
  data: {
    authorities: ['ROLE_Administrator', 'FUNCTION_Codes.CodeAdministration']
  }
}, {
  path: 'new',
  pathMatch: 'full',
  canActivate: [CanActivateFunctionGuard],
  component: NewCodeCategoryComponent,
  data: {
    authorities: ['ROLE_Administrator', 'FUNCTION_Codes.CodeAdministration']
  },
  resolve: {
    title: NewCodeCategoryTitleResolver
  }
}, {
  path: ':codeCategoryId',
  pathMatch: 'full',
  redirectTo: ':codeCategoryId/edit'
}, {
  path: ':codeCategoryId',
  resolve: {
    title: CodeCategoryTitleResolver
  },
  children: [{
    path: 'edit',
    canActivate: [CanActivateFunctionGuard],
    component: EditCodeCategoryComponent,
    data: {
      authorities: ['ROLE_Administrator', 'FUNCTION_Codes.CodeAdministration']
    },
    resolve: {
      title: EditCodeCategoryTitleResolver
    }
  }, {
    path: 'codes',
    resolve: {
      title: CodesTitleResolver
    },
    children: [{
      path: '',
      canActivate: [CanActivateFunctionGuard],
      component: CodesComponent,
      data: {
        authorities: ['ROLE_Administrator', 'FUNCTION_Codes.CodeAdministration']
      }
    }, {
      path: 'new',
      pathMatch: 'full',
      canActivate: [CanActivateFunctionGuard],
      component: NewCodeComponent,
      data: {
        authorities: ['ROLE_Administrator', 'FUNCTION_Codes.CodeAdministration']
      },
      resolve: {
        title: NewCodeTitleResolver
      }
    }, {
      path: ':codeId',
      pathMatch: 'full',
      redirectTo: ':codeId/edit'
    }, {
      path: ':codeId',
      resolve: {
        title: CodeTitleResolver
      },
      children: [{
        path: 'edit',
        canActivate: [CanActivateFunctionGuard],
        component: EditCodeComponent,
        data: {
          authorities: ['ROLE_Administrator', 'FUNCTION_Codes.CodeAdministration']
        },
        resolve: {
          title: EditCodeTitleResolver
        }
      }
      ]
    }
    ]
  }
  ]
}
];

@NgModule({
  declarations: [
    // Components
    CodeCategoriesComponent, CodesComponent, EditCodeCategoryComponent, EditCodeComponent,
    NewCodeCategoryComponent, NewCodeComponent
  ],
  imports: [
    // Angular modules
    CommonModule, FormsModule, ReactiveFormsModule, RouterModule.forChild(routes),

    // Inception modules
    CoreModule
  ],
  providers: [
    // Resolvers
    CodeCategoriesTitleResolver, CodeCategoryTitleResolver, CodesTitleResolver, CodeTitleResolver,
    EditCodeCategoryTitleResolver, EditCodeTitleResolver, NewCodeCategoryTitleResolver,
    NewCodeTitleResolver
  ]
})
export class CodesViewsModule {
}



