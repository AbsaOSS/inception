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

import {
  CoreModule, InceptionAppModule, InceptionConfig, NavigationBadge, NavigationItem, NavigationTitle
} from '@absaoss/ngx-inception/core';
import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';

const ngxInceptionConfig: InceptionConfig = {
  // Application Information
  applicationId: 'demo',
  applicationVersion: '1.0.0',

  // OAuth Token URL
  oauthTokenUrl: 'http://localhost:8080/oauth/token',

  // Logout redirect URL
  logoutRedirectUri: '/',

  // Inception API URLs
  codesApiUrlPrefix: 'http://localhost:8080/api/codes',
  configApiUrlPrefix: 'http://localhost:8080/api/config',
  errorApiUrlPrefix: 'http://localhost:8080/api/error',
  mailApiUrlPrefix: 'http://localhost:8080/api/mail',
  partyApiUrlPrefix: 'http://localhost:8080/api/party',
  partyReferenceApiUrlPrefix: 'http://localhost:8080/api/party/reference',
  referenceApiUrlPrefix: 'http://localhost:8080/api/reference',
  reportingApiUrlPrefix: 'http://localhost:8080/api/reporting',
  schedulerApiUrlPrefix: 'http://localhost:8080/api/scheduler',
  securityApiUrlPrefix: 'http://localhost:8080/api/security',

  // Flags
  forgottenPasswordEnabled: true,
  userProfileEnabled: true,

  // Testing values
  prepopulatedLoginUsername: 'Administrator',
  prepopulatedLoginPassword: 'Password1'
};

@NgModule({
  bootstrap: [AppComponent],
  declarations: [AppComponent
  ],
  exports: [CoreModule],
  imports: [
    AppRoutingModule,
    BrowserAnimationsModule,
    BrowserModule,
    CoreModule.forRoot(ngxInceptionConfig)
  ]
})
export class AppModule extends InceptionAppModule {
  constructor() {
    super();
  }

  /**
   * Initialise the navigation for the application.
   *
   * @returns The navigation for the application.
   */
  protected initNavigation(): NavigationItem[] {

    const navigation: NavigationItem[] = [];

    navigation.push(new NavigationItem('fa fa-tachometer-alt', 'Dashboard', '/dashboard',
      ['ROLE_Administrator', 'FUNCTION_Dashboard.Dashboard'], undefined, undefined, undefined,
      new NavigationBadge('info', 'NEW')));

    navigation.push(new NavigationItem('fa fa-layer-group', 'Inception', '/inception', [], [

      new NavigationItem('fa fa-puzzle-piece', 'Components', '/inception/components', [],
        [new NavigationItem('fa fa-puzzle-piece', 'Buttons', '/inception/components/buttons'),
          new NavigationItem('fa fa-puzzle-piece', 'Cards', '/inception/components/cards'),
          new NavigationItem('fa fa-puzzle-piece', 'Charts', '/inception/components/charts'),
          new NavigationItem('fa fa-puzzle-piece', 'Dialogs', '/inception/components/dialogs'),
          new NavigationItem('fa fa-puzzle-piece', 'Expansion Panels', '/inception/components/expansion-panels'),
          new NavigationItem('fa fa-puzzle-piece', 'Grid List', '/inception/components/grid-list', []),
          new NavigationItem('fa fa-puzzle-piece', 'Lists', '/inception/components/lists'),
          new NavigationItem('fa fa-puzzle-piece', 'Progress', '/inception/components/progress', []),
          new NavigationItem('fa fa-puzzle-piece', 'Switches', '/inception/components/switches', []),
          new NavigationItem('fa fa-puzzle-piece', 'Tabs', '/inception/components/tabs'),
          new NavigationItem('fa fa-puzzle-piece', 'Tooltips', '/inception/components/tooltips')
        ]),

      new NavigationItem('fa fa-file-alt', 'Form', '/inception/form', [],
        [new NavigationItem('fa fa-file-alt', 'Example Form', '/inception/form/example-form'),
          new NavigationItem('fa fa-file-alt', 'Reference Form', '/inception/form/reference-form')
        ]),

      new NavigationItem('fa fa-table', 'Tables', '/inception/tables', [],
        [new NavigationItem('fa fa-table', 'Action List Table', '/inception/tables/action-list-table'),
          new NavigationItem('fa fa-table', 'Action Menu Table', '/inception/tables/action-menu-table'),
          new NavigationItem('fa fa-table', 'Basic Table', '/inception/tables/basic-table'),
          new NavigationItem('fa fa-table', 'Filterable Table', '/inception/tables/filterable-table'),
          new NavigationItem('fa fa-table', 'Pagination Table', '/inception/tables/pagination-table'),
          new NavigationItem('fa fa-table', 'Sortable Table', '/inception/tables/sortable-table', [])
        ]),

      new NavigationItem('fa fa-tint', 'Theme', '/inception/theme', [],
        [new NavigationItem('fa fa-palette', 'Colors', '/inception/theme/colors'),
          new NavigationItem('fa fa-font', 'Typography', '/inception/theme/typography')
        ])
    ]));

    navigation.push(new NavigationItem('fa fa-cogs', 'Administration', '/administration',
      ['ROLE_Administrator', 'FUNCTION_Codes.CodeAdministration', 'FUNCTION_Config.ConfigAdministration',
        'FUNCTION_Security.GroupAdministration', 'FUNCTION_Security.TenantAdministration',
        'FUNCTION_Security.ResetUserPassword', 'FUNCTION_Security.UserAdministration',
        'FUNCTION_Security.UserDirectoryAdministration', 'FUNCTION_Security.UserGroups',
        'FUNCTION_Scheduler.SchedulerAdministration', 'FUNCTION_Scheduler.JobAdministration',
        'FUNCTION_Mail.MailAdministration', 'FUNCTION_Mail.MailTemplateAdministration'
      ], [new NavigationItem('fa fa-shield-alt', 'Security', '/administration/security',
        ['ROLE_Administrator', 'FUNCTION_Security.GroupAdministration', 'FUNCTION_Security.TenantAdministration',
          'FUNCTION_Security.ResetUserPassword', 'FUNCTION_Security.UserAdministration',
          'FUNCTION_Security.UserDirectoryAdministration', 'FUNCTION_Security.UserGroups'
        ], [new NavigationItem('fas fa-user', 'Users', '/administration/security/users',
          ['ROLE_Administrator', 'FUNCTION_Security.ResetUserPassword', 'FUNCTION_Security.UserAdministration',
            'FUNCTION_Security.UserGroups'
          ]), new NavigationItem('fas fa-users', 'Groups', '/administration/security/groups',
          ['ROLE_Administrator', 'FUNCTION_Security.GroupAdministration']),
          new NavigationItem('far fa-building', 'Tenants', '/administration/security/tenants',
            ['ROLE_Administrator', 'FUNCTION_Security.TenantAdministration']),
          new NavigationItem('far fa-address-book', 'User Directories', '/administration/security/user-directories',
            ['ROLE_Administrator', 'FUNCTION_Security.UserDirectoryAdministration'])
        ]), new NavigationItem('fa fa-cog', 'System', '/administration/system',
        ['ROLE_Administrator', 'FUNCTION_Codes.CodeAdministration',
          'FUNCTION_Config.ConfigAdministration', 'FUNCTION_Mail.MailAdministration',
          'FUNCTION_Mail.MailTemplateAdministration', 'FUNCTION_Scheduler.SchedulerAdministration',
          'FUNCTION_Scheduler.JobAdministration'
        ], [new NavigationItem('fa fa-list', 'Codes', '/administration/system/code-categories',
          ['ROLE_Administrator', 'FUNCTION_Codes.CodeAdministration']),
          new NavigationItem('fa fa-list', 'Config', '/administration/system/config',
            ['ROLE_Administrator', 'FUNCTION_Config.ConfigAdministration']),
          new NavigationItem('fas fa-envelope', 'Mail', '/administration/system/mail',
            ['ROLE_Administrator', 'FUNCTION_Mail.MailAdministration', 'FUNCTION_Mail.MailTemplateAdministration'
            ], [new NavigationItem('fas fa-envelope-open-text', 'Mail Templates',
              '/administration/system/mail/mail-templates',
              ['ROLE_Administrator', 'FUNCTION_Mail.MailAdministration', 'FUNCTION_Mail.MailTemplateAdministration'
              ])
            ]), new NavigationItem('fas fa-clock', 'Scheduler', '/administration/system/scheduler',
            ['ROLE_Administrator', 'FUNCTION_Scheduler.SchedulerAdministration', 'FUNCTION_Scheduler.JobAdministration'
            ], [new NavigationItem('fas fa-cogs', 'Jobs', '/administration/system/scheduler/jobs',
              ['ROLE_Administrator', 'FUNCTION_Scheduler.SchedulerAdministration',
                'FUNCTION_Scheduler.JobAdministration'
              ])
            ]), new NavigationItem('fas fa-file-invoice', 'Reporting', '/administration/system/reporting',
            ['ROLE_Administrator', 'FUNCTION_Reporting.ReportingAdministration',
              'FUNCTION_Reporting.ReportDefinitionAdministration'
            ], [new NavigationItem('far fa-copy', 'Report Definitions',
              '/administration/system/reporting/report-definitions',
              ['ROLE_Administrator', 'FUNCTION_Reporting.ReportingAdministration',
                'FUNCTION_Reporting.ReportDefinitionAdministration'
              ])
            ])
        ])
      ]));

    navigation.push(new NavigationTitle('Menus'));

    navigation.push(new NavigationItem('far fa-file', 'Menu 1', '/menu1'));

    navigation.push(new NavigationItem('far fa-file', 'Menu 2', '/menu2', [],
      [new NavigationItem('far fa-file', 'Menu 2.1', '/menu2/menu21'),
        new NavigationItem('far fa-file', 'Menu 2.2', '/menu2/menu22')
      ]));

    navigation.push(new NavigationItem('far fa-file', 'Menu 3', '/menu3', [],
      [new NavigationItem('far fa-file', 'Menu 3.1', '/menu3/menu31', [],
        [new NavigationItem('far fa-file', 'Menu 3.1.1', '/menu3/menu31/menu311'),
          new NavigationItem('far fa-file', 'Menu 3.1.2', '/menu3/menu31/menu312')
        ]), new NavigationItem('far fa-file', 'Menu 3.2', '/menu3/menu32', [],
        [new NavigationItem('far fa-file', 'Menu 3.2.1', '/menu3/menu32/menu321'),
          new NavigationItem('far fa-file', 'Menu 3.2.2', '/menu3/menu32/menu322')
        ])
      ]));

    return navigation;
  }
}
