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

import {AfterViewInit, Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {
  AccessDeniedError, AdminContainerView, BackNavigation, DialogService, Error, InvalidArgumentError,
  ServiceUnavailableError, SpinnerService
} from 'ngx-inception/core';
import {finalize, first} from 'rxjs/operators';
import {SecurityService} from '../services/security.service';
import {Tenant} from '../services/tenant';

/**
 * The EditTenantComponent class implements the edit tenant component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'edit-tenant.component.html',
  styleUrls: ['edit-tenant.component.css'],
})
export class EditTenantComponent extends AdminContainerView implements AfterViewInit {

  editTenantForm: FormGroup;

  nameFormControl: FormControl;

  tenant?: Tenant;

  tenantId: string;

  constructor(private router: Router, private activatedRoute: ActivatedRoute, private securityService: SecurityService,
              private dialogService: DialogService, private spinnerService: SpinnerService) {
    super();

    // Retrieve the route parameters
    const tenantId = this.activatedRoute.snapshot.paramMap.get('tenantId');

    if (!tenantId) {
      throw(new Error('No tenantId route parameter found'));
    }

    this.tenantId = decodeURIComponent(tenantId);

    // Initialise the form controls
    this.nameFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);

    // Initialise the form
    this.editTenantForm = new FormGroup({
      name: this.nameFormControl
    });
  }

  get backNavigation(): BackNavigation {
    return new BackNavigation($localize`:@@security_edit_tenant_back_navigation:Tenants`,
      ['../..'], {relativeTo: this.activatedRoute});
  }

  get title(): string {
    return $localize`:@@security_edit_tenant_title:Edit Tenant`
  }

  cancel(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['../..'], {relativeTo: this.activatedRoute});
  }

  ngAfterViewInit(): void {
    // Retrieve the existing user and initialise the form fields
    this.spinnerService.showSpinner();

    this.securityService.getTenant(this.tenantId)
    .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
    .subscribe((tenant: Tenant) => {
      this.tenant = tenant;

      this.nameFormControl.setValue(tenant.name);
    }, (error: Error) => {
      // noinspection SuspiciousTypeOfGuard
      if ((error instanceof AccessDeniedError) || (error instanceof InvalidArgumentError) ||
        (error instanceof ServiceUnavailableError)) {
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigateByUrl('/error/send-error-report', {state: {error}});
      } else {
        this.dialogService.showErrorDialog(error).afterClosed()
        .pipe(first())
        .subscribe(() => {
          this.router.navigate(['../..'], {relativeTo: this.activatedRoute});
        });
      }
    });
  }

  ok(): void {
    if (this.tenant && this.editTenantForm.valid) {
      this.tenant.name = this.nameFormControl.value;

      this.spinnerService.showSpinner();

      this.securityService.updateTenant(this.tenant)
      .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
      .subscribe(() => {
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigate(['../..'], {relativeTo: this.activatedRoute});
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
