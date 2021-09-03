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
import {FormsModule} from '@angular/forms';
import {RouterModule, Routes} from '@angular/router';
import {CoreModule} from '@absaoss/ngx-inception/core';
import {ButtonsComponent} from './buttons.component';
import {CardsComponent} from './cards.component';
import {ChartsComponent} from './charts.component';
import {DialogsComponent} from './dialogs.component';
import {ExpansionPanelsComponent} from './expansion-panels.component';
import {GridListComponent} from './grid-list.component';
import {ListsComponent} from './lists.component';
import {ProgressComponent} from './progress.component';
import {SwitchesComponent} from './switches.component';
import {TabsComponent} from './tabs.component';
import {TooltipsComponent} from './tooltips.component';

const routes: Routes = [{
  path: '',
  redirectTo: 'buttons'
}, {
  path: 'buttons',
  component: ButtonsComponent,
  data: {
    title: 'Buttons',
  }
}, {
  path: 'cards',
  component: CardsComponent,
  data: {
    title: 'Cards',
  }
}, {
  path: 'charts',
  component: ChartsComponent,
  data: {
    title: 'Charts',
  }
}, {
  path: 'dialogs',
  component: DialogsComponent,
  data: {
    title: 'Dialogs',
  }
}, {
  path: 'expansion-panels',
  component: ExpansionPanelsComponent,
  data: {
    title: 'Expansion Panels',
  }
}, {
  path: 'grid-list',
  component: GridListComponent,
  data: {
    title: 'Grid List',
  }
}, {
  path: 'lists',
  component: ListsComponent,
  data: {
    title: 'Lists',
  }
}, {
  path: 'progress',
  component: ProgressComponent,
  data: {
    title: 'Progress',
  }
}, {
  path: 'switches',
  component: SwitchesComponent,
  data: {
    title: 'Switches',
  }
}, {
  path: 'tabs',
  component: TabsComponent,
  data: {
    title: 'Tabs',
  }
}, {
  path: 'tooltips',
  component: TooltipsComponent,
  data: {
    title: 'Tooltips',
  }
}
];

@NgModule({
  imports: [
    // Angular modules
    CommonModule, FormsModule, RouterModule.forChild(routes),

    // Inception modules
    CoreModule
  ],
  declarations: [ButtonsComponent, CardsComponent, ChartsComponent, DialogsComponent,
    ExpansionPanelsComponent, GridListComponent, ListsComponent, ProgressComponent,
    SwitchesComponent, TabsComponent, TooltipsComponent
  ]
})
export class ComponentsModule {
}
