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

import {NgModule} from '@angular/core';

@NgModule({
  declarations: [],
  imports: [],
  exports: []
})
export class InceptionModule {
  constructor() {
  }
}


// /**
//  * The InceptionModule class implements the Inception framework module.
//  *
//  * @author Marcus Portmann
//  */
// @NgModule({
//   declarations: [
//     // Components
//     NgxInceptionComponent
//   ],
//   imports: [
//     // Angular modules
//     CommonModule, FormsModule, ReactiveFormsModule, RouterModule,
//
//     // Inception modules
//     CoreModule.forRoot(), LayoutModule.forRoot(),
//   ],
//   exports: [
//     // Angular modules
//     CommonModule, FormsModule, HttpClientModule, ReactiveFormsModule, RouterModule,
//
//     // Inception modules
//     CoreModule, LayoutModule,
//
//     // Components
//     NgxInceptionComponent
//   ]
// })
// export class InceptionModule {
//   constructor(injector: Injector) {
//     setInceptionInjector(injector);
//   }
//
//   // TODO: MOVE THE PROVISION OF THE INCEPTION CONFIGURATION TO THE APPLICATION MODULE -- MARCUS
//   static forRoot(config: InceptionConfig): ModuleWithProviders<InceptionModule> {
//     return {
//       ngModule: InceptionModule,
//       providers: [{
//         provide: INCEPTION_CONFIG,
//         useValue: config
//       }, {
//         provide: LocationStrategy,
//         useClass: HashLocationStrategy,
//       }, {
//         provide: DateAdapter,
//         useClass: MomentDateAdapter,
//         deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS]
//       }, {
//         provide: MAT_DATE_FORMATS,
//         useValue: INCEPTION_DATE_FORMATS
//       }, {
//         provide: MAT_FORM_FIELD_DEFAULT_OPTIONS,
//         useValue: {appearance: 'standard'}
//       },
//         // Function Guards
//         CanActivateFunctionGuard, DisabledFunctionGuard
//       ]
//     };
//   }
// }
