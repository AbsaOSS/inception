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
import {DashboardComponent} from './dashboard.component';

// Import 3rd party modules
// import { ChartsModule } from 'ng2-charts/ng2-charts';

const routes: Routes = [{
  path: '',
  component: DashboardComponent
}
];

@NgModule({
  imports: [
    // Angular modules
    CommonModule, RouterModule.forChild(routes),

    // Inception modules
  ],
  declarations: [DashboardComponent]
})
export class DashboardModule {
}
