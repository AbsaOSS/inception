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
import {AfterViewInit, ChangeDetectorRef, Component, OnDestroy, ViewChild} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs';
import {debounceTime, finalize, first, pairwise, startWith} from 'rxjs/operators';
import {v4 as uuid} from 'uuid';
import {SecurityService} from '../services/security.service';
import {UserDirectory} from '../services/user-directory';
import {UserDirectoryType} from '../services/user-directory-type';
import {InternalUserDirectoryComponent} from './internal-user-directory.component';
import {LdapUserDirectoryComponent} from './ldap-user-directory.component';

/**
 * The NewUserDirectoryComponent class implements the new user directory component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'new-user-directory.component.html',
  styleUrls: ['new-user-directory.component.css'],
})
export class NewUserDirectoryComponent extends AdminContainerView implements AfterViewInit, OnDestroy {

  @ViewChild(InternalUserDirectoryComponent) internalUserDirectory?: InternalUserDirectoryComponent;

  @ViewChild(LdapUserDirectoryComponent) ldapUserDirectory?: LdapUserDirectoryComponent;

  nameFormControl: FormControl;

  newUserDirectoryForm: FormGroup;

  userDirectory: UserDirectory | null = null;

  userDirectoryTypeFormControl: FormControl;

  userDirectoryTypes: UserDirectoryType[] = [];

  private subscriptions: Subscription = new Subscription();

  constructor(private changeDetectorRef: ChangeDetectorRef, private router: Router,
              private activatedRoute: ActivatedRoute, private securityService: SecurityService,
              private dialogService: DialogService, private spinnerService: SpinnerService) {
    super();

    // Initialise the form controls
    this.nameFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.userDirectoryTypeFormControl = new FormControl('', [Validators.required]);

    // Initialise the form
    this.newUserDirectoryForm = new FormGroup({
      name: this.nameFormControl,
      userDirectoryType: this.userDirectoryTypeFormControl
    });

    this.subscriptions.add(this.userDirectoryTypeFormControl.valueChanges
    .pipe(startWith(null), debounceTime(500), pairwise())
    .subscribe(([previousUserDirectoryType, currentUserDirectoryType]: [string, string]) => {
      this.userDirectoryTypeSelected(previousUserDirectoryType, currentUserDirectoryType);
    }));
  }

  get backNavigation(): BackNavigation {
    return new BackNavigation($localize`:@@security_new_user_directory_back_navigation:User Directories`,
      ['../..'], {relativeTo: this.activatedRoute});
  }

  get title(): string {
    return $localize`:@@security_new_user_directory_title:New User Directory`
  }

  cancel(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['..'], {relativeTo: this.activatedRoute});
  }

  ngAfterViewInit(): void {
    this.spinnerService.showSpinner();

    this.securityService.getUserDirectoryTypes()
    .pipe(first(), finalize(() => this.spinnerService.hideSpinner()))
    .subscribe((userDirectoryTypes: UserDirectoryType[]) => {
      this.userDirectoryTypes = userDirectoryTypes;
      this.userDirectory = new UserDirectory(uuid(), '', '', []);
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

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  ok(): void {
    if (this.userDirectory && this.newUserDirectoryForm.valid) {
      this.userDirectory.name = this.nameFormControl.value;
      this.userDirectory.type = this.userDirectoryTypeFormControl.value;

      if (this.internalUserDirectory) {
        this.userDirectory.parameters = this.internalUserDirectory.getParameters();
      } else if (this.ldapUserDirectory) {
        this.userDirectory.parameters = this.ldapUserDirectory.getParameters();
      }

      this.spinnerService.showSpinner();

      this.securityService.createUserDirectory(this.userDirectory)
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

  userDirectoryTypeSelected(previousUserDirectoryType: string, currentUserDirectoryType: string): void {
    // Clear the user directory parameters if required
    if (!!previousUserDirectoryType && this.userDirectory) {
      this.userDirectory.parameters = [];
    }

    // Remove the controls for the user directory types
    this.newUserDirectoryForm.removeControl('internalUserDirectory');
    this.newUserDirectoryForm.removeControl('ldapUserDirectory');

    // Add the appropriate control for the user directory type that was selected
    if (currentUserDirectoryType === 'InternalUserDirectory') {
      this.newUserDirectoryForm.addControl('internalUserDirectory', new FormControl(''));
    } else if (currentUserDirectoryType === 'LDAPUserDirectory') {
      this.newUserDirectoryForm.addControl('ldapUserDirectory', new FormControl(''));
    }

    this.changeDetectorRef.detectChanges();

    // Populate the nested InternalUserDirectoryComponent or LdapUserDirectoryComponent
    if (this.userDirectory) {
      if (this.internalUserDirectory) {
        this.internalUserDirectory.setParameters(this.userDirectory.parameters);
      } else if (this.ldapUserDirectory) {
        this.ldapUserDirectory.setParameters(this.userDirectory.parameters);
      }
    }
  }
}
