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

import {ObserversModule} from '@angular/cdk/observers';
import {CommonModule, HashLocationStrategy, LocationStrategy} from '@angular/common';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {Injector, ModuleWithProviders, NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import '@angular/localize/init';
import {MAT_MOMENT_DATE_ADAPTER_OPTIONS, MomentDateAdapter} from '@angular/material-moment-adapter';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatButtonModule} from '@angular/material/button';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {MatCardModule} from '@angular/material/card';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE} from '@angular/material/core';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatDialogModule} from '@angular/material/dialog';
import {MatExpansionModule} from '@angular/material/expansion';
import {MAT_FORM_FIELD_DEFAULT_OPTIONS, MatFormFieldModule} from '@angular/material/form-field';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatListModule} from '@angular/material/list';
import {MatMenuModule} from '@angular/material/menu';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatRadioModule} from '@angular/material/radio';
import {MatSelectModule} from '@angular/material/select';
import {MatSliderModule} from '@angular/material/slider';
import {MatSortModule} from '@angular/material/sort';
import {MatTableModule} from '@angular/material/table';
import {MatTabsModule} from '@angular/material/tabs';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatTooltipModule} from '@angular/material/tooltip';
import {RouterModule} from '@angular/router';
import {
  PERFECT_SCROLLBAR_CONFIG, PerfectScrollbarConfigInterface, PerfectScrollbarModule
} from 'ngx-perfect-scrollbar';
import {ConfirmationDialogComponent} from './dialogs/components/confirmation-dialog.component';
import {ErrorDialogComponent} from './dialogs/components/error-dialog.component';
import {InformationDialogComponent} from './dialogs/components/information-dialog.component';
import {WarningDialogComponent} from './dialogs/components/warning-dialog.component';
import {DialogService} from './dialogs/services/dialog.service';
import {FileUploadComponent} from './forms/components/file-upload.component';
import {GroupFormFieldComponent} from './forms/components/group-form-field.component';
import {TableFilterComponent} from './forms/components/table-filter.component';
import {AutocompleteSelectionRequiredDirective} from './forms/directives/autocomplete-selection-required.directive';
import {AutofocusDirective} from './forms/directives/autofocus.directive';
import {ValidatedFormDirective} from './forms/directives/validated-form.directive';
import {INCEPTION_CONFIG, InceptionConfig} from './inception-config';
import {setInceptionInjector} from './inception-injector';
import {AdminContainerComponent} from './layout/components/admin-container.component';
import {AdminFooterComponent} from './layout/components/admin-footer.component';
import {AdminHeaderComponent} from './layout/components/admin-header.component';
import {BreadcrumbsComponent} from './layout/components/breadcrumbs.component';
import {NotFoundComponent} from './layout/components/not-found.component';
import {SidebarFooterComponent} from './layout/components/sidebar-footer.component';
import {SidebarFormComponent} from './layout/components/sidebar-form.component';
import {SidebarHeaderComponent} from './layout/components/sidebar-header.component';
import {SidebarMinimizerComponent} from './layout/components/sidebar-minimizer.component';
import {SidebarNavItemComponent} from './layout/components/sidebar-nav-item.component';
import {SidebarNavComponent} from './layout/components/sidebar-nav.component';
import {SidebarComponent} from './layout/components/sidebar.component';
import {SimpleContainerComponent} from './layout/components/simple-container.component';
import {SpinnerComponent} from './layout/components/spinner.component';
import {TitleBarComponent} from './layout/components/title-bar.component';
import {BrandMinimizerDirective} from './layout/directives/brand-minimizer.directive';
import {MobileSidebarTogglerDirective} from './layout/directives/mobile-sidebar-toggler.directive';
import {SidebarMinimizerDirective} from './layout/directives/sidebar-minimizer.directive';
import {SidebarNavDropdownTogglerDirective} from './layout/directives/sidebar-nav-dropdown-toggler.directive';
import {SidebarNavDropdownDirective} from './layout/directives/sidebar-nav-dropdown.directive';
import {SidebarOffCanvasCloseDirective} from './layout/directives/sidebar-off-canvas-close.directive';
import {SidebarTogglerDirective} from './layout/directives/sidebar-toggler.directive';
import {BreadcrumbsService} from './layout/services/breadcrumbs.service';
import {NavigationService} from './layout/services/navigation.service';
import {SpinnerService} from './layout/services/spinner.service';
import {TitleBarService} from './layout/services/title-bar.service';
import {CanActivateFunctionGuard} from './routing/can-activate-function-guard';
import {DisabledFunctionGuard} from './routing/disabled-function-guard';
import {HasAuthorityDirective} from './session/directives/has-authority.directive';
import {SessionInterceptor} from './session/services/session.interceptor';
import {SessionService} from './session/services/session.service';

const INCEPTION_PERFECT_SCROLLBAR_CONFIG: PerfectScrollbarConfigInterface = {
  suppressScrollX: true
};

// See the Moment.js docs for the meaning of these formats:
// https://momentjs.com/docs/#/displaying/format/
export const INCEPTION_DATE_FORMATS = {
  parse: {
    dateInput: 'LL',
  },
  display: {
    dateInput: 'YYYY-MM-DD',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  },
};


/**
 * The Core module for the Inception framework.
 *
 * @author Marcus Portmann
 */
@NgModule({
  declarations: [
    // Dialog Components
    ConfirmationDialogComponent, ErrorDialogComponent, InformationDialogComponent,
    WarningDialogComponent,


    // Forms Components
    FileUploadComponent, GroupFormFieldComponent, TableFilterComponent,

    // Forms  Directives
    AutocompleteSelectionRequiredDirective, AutofocusDirective, ValidatedFormDirective,


    // Layout Components
    AdminContainerComponent, AdminFooterComponent, AdminHeaderComponent, BreadcrumbsComponent,
    NotFoundComponent, SidebarComponent, SidebarFooterComponent, SidebarFormComponent,
    SidebarHeaderComponent, SidebarMinimizerComponent, SidebarNavComponent,
    SidebarNavItemComponent, SimpleContainerComponent, SpinnerComponent, TitleBarComponent,

    // Layout Directives
    BrandMinimizerDirective, MobileSidebarTogglerDirective, SidebarMinimizerDirective,
    SidebarNavDropdownDirective, SidebarNavDropdownTogglerDirective, SidebarOffCanvasCloseDirective,
    SidebarTogglerDirective,


    // Session Directives
    HasAuthorityDirective
  ],
  imports: [
    // Angular modules
    CommonModule, FormsModule, HttpClientModule, ReactiveFormsModule, RouterModule,

    // 3rd party modules
    // ChartsModule,
    PerfectScrollbarModule,

    // CDK modules
    ObserversModule,

    // Material modules
    MatAutocompleteModule, MatButtonModule, MatButtonToggleModule, MatCardModule, MatCheckboxModule,
    MatDatepickerModule, MatDialogModule, MatExpansionModule, MatFormFieldModule, MatGridListModule,
    MatIconModule, MatInputModule, MatListModule, MatMenuModule, MatPaginatorModule,
    MatProgressBarModule, MatRadioModule, MatSelectModule, MatSliderModule, MatSortModule,
    MatTableModule, MatTabsModule, MatToolbarModule, MatTooltipModule
  ],
  exports: [
    // Angular modules
    CommonModule, FormsModule, HttpClientModule, ReactiveFormsModule, RouterModule,

    // 3rd party modules
    // ChartsModule,
    PerfectScrollbarModule,

    // Material modules
    MatAutocompleteModule, MatButtonModule, MatButtonToggleModule, MatCardModule, MatCheckboxModule,
    MatDatepickerModule, MatDialogModule, MatExpansionModule, MatFormFieldModule, MatGridListModule,
    MatIconModule, MatInputModule, MatListModule, MatMenuModule, MatPaginatorModule,
    MatProgressBarModule, MatRadioModule, MatSelectModule, MatSliderModule, MatSortModule,
    MatTableModule, MatTabsModule, MatToolbarModule, MatTooltipModule,


    // Forms Components
    FileUploadComponent, GroupFormFieldComponent, TableFilterComponent,

    // Forms Directives
    AutocompleteSelectionRequiredDirective, AutofocusDirective, ValidatedFormDirective,


    // Layout Components
    AdminContainerComponent, AdminFooterComponent, AdminHeaderComponent, BreadcrumbsComponent,
    NotFoundComponent, SidebarComponent, SidebarFooterComponent, SidebarFormComponent,
    SidebarHeaderComponent, SidebarMinimizerComponent, SidebarNavComponent,
    SidebarNavItemComponent, SimpleContainerComponent, SpinnerComponent, TitleBarComponent,

    // Layout Directives
    BrandMinimizerDirective, MobileSidebarTogglerDirective, SidebarMinimizerDirective,
    SidebarNavDropdownDirective, SidebarNavDropdownTogglerDirective, SidebarOffCanvasCloseDirective,
    SidebarTogglerDirective,


    // Session Directives
    HasAuthorityDirective
  ],
  entryComponents: [
    // Forms Components
    ConfirmationDialogComponent, ErrorDialogComponent, InformationDialogComponent,
    WarningDialogComponent,

    // Layout Components
    SpinnerComponent
  ]
})
export class CoreModule {
  constructor(injector: Injector) {
    setInceptionInjector(injector);
  }

  static forRoot(config: InceptionConfig): ModuleWithProviders<CoreModule> {
    return {
      ngModule: CoreModule,
      providers: [{
        provide: INCEPTION_CONFIG,
        useValue: config
      },

        {
          provide: LocationStrategy,
          useClass: HashLocationStrategy,
        },


        {
          provide: DateAdapter,
          useClass: MomentDateAdapter,
          deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS]
        }, {
          provide: MAT_DATE_FORMATS,
          useValue: INCEPTION_DATE_FORMATS
        }, {
          provide: MAT_FORM_FIELD_DEFAULT_OPTIONS,
          useValue: {appearance: 'standard'}
        },


        {
          provide: HTTP_INTERCEPTORS,
          useClass: SessionInterceptor,
          multi: true
        },


        {
          provide: PERFECT_SCROLLBAR_CONFIG,
          useValue: INCEPTION_PERFECT_SCROLLBAR_CONFIG
        },

        // Dialog Services
        DialogService,

        // Layout Services
        BreadcrumbsService,
        NavigationService,
        SpinnerService,
        TitleBarService,

        // Session Services
        SessionService,

        // Function Guards
        CanActivateFunctionGuard, DisabledFunctionGuard
      ]
    };
  }
}
