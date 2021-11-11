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
  AccessDeniedError, AdminContainerView, BackNavigation, DialogService, Error, InvalidArgumentError,
  ServiceUnavailableError, SpinnerService
} from '@absaoss/ngx-inception/core';
import {AfterViewInit, Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {finalize, first} from 'rxjs/operators';
import {v4 as uuid} from 'uuid';
import {SecurityService} from '../services/security.service';
import {Tenant} from '../services/tenant';
import {TenantStatus} from '../services/tenant-status';

/**
 * The NewTenantComponent class implements the new tenant component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'new-tenant.component.html',
  styleUrls: ['new-tenant.component.css'],
})
export class NewTenantComponent extends AdminContainerView implements AfterViewInit {

  createUserDirectoryFormControl: FormControl;

  nameFormControl: FormControl;

  newTenantForm: FormGroup;

  tenant: Tenant | null = null;

  constructor(private router: Router, private activatedRoute: ActivatedRoute, private securityService: SecurityService,
              private dialogService: DialogService, private spinnerService: SpinnerService) {
    super();

    // Initialise the form controls
    this.createUserDirectoryFormControl = new FormControl(false);
    this.nameFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);

    // Initialise the form
    this.newTenantForm = new FormGroup({
      createUserDirectory: this.createUserDirectoryFormControl,
      name: this.nameFormControl
    });
  }

  get backNavigation(): BackNavigation {
    return new BackNavigation($localize`:@@security_new_tenant_back_navigation:Tenants`,
      ['..'], {relativeTo: this.activatedRoute});
  }

  get title(): string {
    return $localize`:@@security_new_tenant_title:New Tenant`
  }

  cancel(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['..'], {relativeTo: this.activatedRoute});
  }

  ngAfterViewInit(): void {
    this.tenant = new Tenant(uuid(), '', TenantStatus.Active);
  }

  ok(): void {
    if (this.tenant && this.newTenantForm.valid) {
      this.tenant.name = this.nameFormControl.value;

      this.spinnerService.showSpinner();

      this.securityService.createTenant(this.tenant, this.createUserDirectoryFormControl.value)
      .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
      .subscribe(() => {
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigate(['..'], {relativeTo: this.activatedRoute});
      }, (error: Error) => {
        // noinspection SuspiciousTypeOfGuard
        if ((error instanceof AccessDeniedError) || (error instanceof InvalidArgumentError) ||
          (error instanceof ServiceUnavailableError)) {
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigateByUrl('/error/send-error-report', {state: {error}});
        } else {
          this.dialogService.showErrorDialog(error);
        }
      });
    }
  }
}
