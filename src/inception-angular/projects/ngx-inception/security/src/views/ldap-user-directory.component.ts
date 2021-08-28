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

import {Component, forwardRef} from '@angular/core';
import {
  AbstractControl, ControlValueAccessor, FormControl, FormGroup, NG_VALIDATORS, NG_VALUE_ACCESSOR,
  ValidationErrors, Validator, Validators
} from '@angular/forms';
import {UserDirectoryParameter} from '../services/user-directory-parameter';
import {UserDirectoryUtil} from '../services/user-directory-util';

@Component({
  selector: 'inception-ldap-user-directory',
  templateUrl: 'ldap-user-directory.component.html',
  styleUrls: ['ldap-user-directory.component.css'],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => LdapUserDirectoryComponent),
    multi: true
  }, {
    provide: NG_VALIDATORS,
    useExisting: forwardRef(() => LdapUserDirectoryComponent),
    multi: true
  }
  ]
})
export class LdapUserDirectoryComponent implements ControlValueAccessor, Validator {

  baseDNFormControl: FormControl;

  bindDNFormControl: FormControl;

  bindPasswordFormControl: FormControl;

  groupBaseDNFormControl: FormControl;

  groupDescriptionAttributeFormControl: FormControl;

  groupMemberAttributeFormControl: FormControl;

  groupNameAttributeFormControl: FormControl;

  groupObjectClassFormControl: FormControl;

  hostFormControl: FormControl;

  ldapUserDirectoryForm: FormGroup;

  maxFilteredGroupMembersFormControl: FormControl;

  maxFilteredGroupsFormControl: FormControl;

  maxFilteredUsersFormControl: FormControl;

  portFormControl: FormControl;

  supportsAdminChangePasswordFormControl: FormControl;

  supportsChangePasswordFormControl: FormControl;

  supportsGroupAdministrationFormControl: FormControl;

  supportsGroupMemberAdministrationFormControl: FormControl;

  supportsUserAdministrationFormControl: FormControl;
  useSSLFormControl: FormControl;
  userBaseDNFormControl: FormControl;
  userEmailAttributeFormControl: FormControl;
  userMobileNumberAttributeFormControl: FormControl;
  userNameAttributeFormControl: FormControl;
  userObjectClassFormControl: FormControl;
  userPhoneNumberAttributeFormControl: FormControl;
  userPreferredNameAttributeFormControl: FormControl;
  userUsernameAttributeFormControl: FormControl;

  constructor() {
    // Initialise the form controls
    this.baseDNFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.bindDNFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.bindPasswordFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.groupBaseDNFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.groupDescriptionAttributeFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.groupMemberAttributeFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.groupNameAttributeFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.groupObjectClassFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.hostFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.maxFilteredGroupMembersFormControl = new FormControl('', [Validators.required, Validators.pattern('^\\d+$')]);
    this.maxFilteredGroupsFormControl = new FormControl('', [Validators.required, Validators.pattern('^\\d+$')]);
    this.maxFilteredUsersFormControl = new FormControl('', [Validators.required, Validators.pattern('^\\d+$')]);
    this.portFormControl = new FormControl('', [Validators.required, Validators.pattern('^\\d+$')]);
    this.supportsAdminChangePasswordFormControl =
      new FormControl('', [Validators.required, Validators.pattern('^(true|false)$')]);
    this.supportsChangePasswordFormControl =
      new FormControl('', [Validators.required, Validators.pattern('^(true|false)$')]);
    this.supportsGroupAdministrationFormControl =
      new FormControl('', [Validators.required, Validators.pattern('^(true|false)$')]);
    this.supportsGroupMemberAdministrationFormControl =
      new FormControl('', [Validators.required, Validators.pattern('^(true|false)$')]);
    this.supportsUserAdministrationFormControl =
      new FormControl('', [Validators.required, Validators.pattern('^(true|false)$')]);
    this.userBaseDNFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.userObjectClassFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.userEmailAttributeFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.userNameAttributeFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.userPreferredNameAttributeFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.userMobileNumberAttributeFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.userPhoneNumberAttributeFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.userUsernameAttributeFormControl = new FormControl('', [Validators.required, Validators.maxLength(100)]);
    this.useSSLFormControl = new FormControl('', [Validators.required, Validators.pattern('^(true|false)$')]);

    // Initialise the form
    this.ldapUserDirectoryForm = new FormGroup({
      baseDN: this.baseDNFormControl,
      bindDN: this.bindDNFormControl,
      bindPassword: this.bindPasswordFormControl,
      groupBaseDN: this.groupBaseDNFormControl,
      groupDescriptionAttribute: this.groupDescriptionAttributeFormControl,
      groupMemberAttribute: this.groupMemberAttributeFormControl,
      groupNameAttribute: this.groupNameAttributeFormControl,
      groupObjectClass: this.groupObjectClassFormControl,
      host: this.hostFormControl,
      maxFilteredGroupMembers: this.maxFilteredGroupMembersFormControl,
      maxFilteredGroups: this.maxFilteredGroupsFormControl,
      maxFilteredUsers: this.maxFilteredUsersFormControl,
      port: this.portFormControl,
      supportsAdminChangePassword: this.supportsAdminChangePasswordFormControl,
      supportsChangePassword: this.supportsChangePasswordFormControl,
      supportsGroupAdministration: this.supportsGroupAdministrationFormControl,
      supportsGroupMemberAdministration: this.supportsGroupMemberAdministrationFormControl,
      supportsUserAdministration: this.supportsUserAdministrationFormControl,
      userBaseDN: this.userBaseDNFormControl,
      userObjectClass: this.userObjectClassFormControl,
      userEmailAttribute: this.userEmailAttributeFormControl,
      userNameAttribute: this.userNameAttributeFormControl,
      userPreferredNameAttribute: this.userPreferredNameAttributeFormControl,
      userMobileNumberAttribute: this.userMobileNumberAttributeFormControl,
      userPhoneNumberAttribute: this.userPhoneNumberAttributeFormControl,
      userUsernameAttribute: this.userUsernameAttributeFormControl,
      useSSL: this.useSSLFormControl
    });
  }

  getParameters(): UserDirectoryParameter[] {

    const parameters: UserDirectoryParameter[] = [];

    UserDirectoryUtil.setParameter(parameters, 'BaseDN', this.baseDNFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'BindDN', this.bindDNFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'BindPassword', this.bindPasswordFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'GroupBaseDN', this.groupBaseDNFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'GroupDescriptionAttribute',
      this.groupDescriptionAttributeFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'GroupMemberAttribute', this.groupMemberAttributeFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'GroupNameAttribute', this.groupNameAttributeFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'GroupObjectClass', this.groupObjectClassFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'Host', this.hostFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'MaxFilteredGroupMembers',
      this.maxFilteredGroupMembersFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'MaxFilteredGroups', this.maxFilteredGroupsFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'MaxFilteredUsers', this.maxFilteredUsersFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'Port', this.portFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'SupportsAdminChangePassword',
      this.supportsAdminChangePasswordFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'SupportsChangePassword', this.supportsChangePasswordFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'SupportsGroupAdministration',
      this.supportsGroupAdministrationFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'SupportsGroupMemberAdministration',
      this.supportsGroupMemberAdministrationFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'SupportsUserAdministration',
      this.supportsUserAdministrationFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'UserBaseDN', this.userBaseDNFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'UserObjectClass', this.userObjectClassFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'UserEmailAttribute', this.userEmailAttributeFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'UserNameAttribute', this.userNameAttributeFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'UserPreferredNameAttribute', this.userPreferredNameAttributeFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'UserMobileNumberAttribute',
      this.userMobileNumberAttributeFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'UserPhoneNumberAttribute',
      this.userPhoneNumberAttributeFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'UserUsernameAttribute', this.userUsernameAttributeFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'UseSSL', this.useSSLFormControl.value);

    return parameters;
  }

  onTouched: () => void = () => {
  };

  // tslint:disable-next-line:no-any
  registerOnChange(fn: any): void {
    this.ldapUserDirectoryForm.valueChanges.subscribe(fn);
  }

  // tslint:disable-next-line:no-any
  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState?(isDisabled: boolean): void {
    isDisabled ? this.ldapUserDirectoryForm.disable() : this.ldapUserDirectoryForm.enable();
  }

  setParameters(parameters: UserDirectoryParameter[]) {
    this.baseDNFormControl.setValue(
      UserDirectoryUtil.hasParameter(parameters, 'BaseDN') ? UserDirectoryUtil.getParameter(parameters, 'BaseDN') : '');
    this.bindDNFormControl.setValue(
      UserDirectoryUtil.hasParameter(parameters, 'BindDN') ? UserDirectoryUtil.getParameter(parameters, 'BindDN') : '');
    this.bindPasswordFormControl.setValue(UserDirectoryUtil.hasParameter(parameters, 'BindPassword') ?
      UserDirectoryUtil.getParameter(parameters, 'BindPassword') : '');
    this.groupBaseDNFormControl.setValue(UserDirectoryUtil.hasParameter(parameters, 'GroupBaseDN') ?
      UserDirectoryUtil.getParameter(parameters, 'GroupBaseDN') : '');
    this.groupDescriptionAttributeFormControl.setValue(
      UserDirectoryUtil.hasParameter(parameters, 'GroupDescriptionAttribute') ?
        UserDirectoryUtil.getParameter(parameters, 'GroupDescriptionAttribute') : 'description');
    this.groupMemberAttributeFormControl.setValue(UserDirectoryUtil.hasParameter(parameters, 'GroupMemberAttribute') ?
      UserDirectoryUtil.getParameter(parameters, 'GroupMemberAttribute') : 'member');
    this.groupNameAttributeFormControl.setValue(UserDirectoryUtil.hasParameter(parameters, 'GroupNameAttribute') ?
      UserDirectoryUtil.getParameter(parameters, 'GroupNameAttribute') : 'cn');
    this.groupObjectClassFormControl.setValue(UserDirectoryUtil.hasParameter(parameters, 'GroupObjectClass') ?
      UserDirectoryUtil.getParameter(parameters, 'GroupObjectClass') : 'groupOfNames');
    this.hostFormControl.setValue(
      UserDirectoryUtil.hasParameter(parameters, 'Host') ? UserDirectoryUtil.getParameter(parameters, 'Host') : '');
    this.maxFilteredGroupMembersFormControl.setValue(
      UserDirectoryUtil.hasParameter(parameters, 'MaxFilteredGroupMembers') ?
        UserDirectoryUtil.getParameter(parameters, 'MaxFilteredGroupMembers') : '100');
    this.maxFilteredGroupsFormControl.setValue(UserDirectoryUtil.hasParameter(parameters, 'MaxFilteredGroups') ?
      UserDirectoryUtil.getParameter(parameters, 'MaxFilteredGroups') : '100');
    this.maxFilteredUsersFormControl.setValue(UserDirectoryUtil.hasParameter(parameters, 'MaxFilteredUsers') ?
      UserDirectoryUtil.getParameter(parameters, 'MaxFilteredUsers') : '100');
    this.portFormControl.setValue(
      UserDirectoryUtil.hasParameter(parameters, 'Port') ? UserDirectoryUtil.getParameter(parameters, 'Port') : '');
    this.supportsAdminChangePasswordFormControl.setValue(
      UserDirectoryUtil.hasParameter(parameters, 'SupportsAdminChangePassword') ?
        UserDirectoryUtil.getParameter(parameters, 'SupportsAdminChangePassword') : '');
    this.supportsChangePasswordFormControl.setValue(
      UserDirectoryUtil.hasParameter(parameters, 'SupportsChangePassword') ?
        UserDirectoryUtil.getParameter(parameters, 'SupportsChangePassword') : '');
    this.supportsGroupAdministrationFormControl.setValue(
      UserDirectoryUtil.hasParameter(parameters, 'SupportsGroupAdministration') ?
        UserDirectoryUtil.getParameter(parameters, 'SupportsGroupAdministration') : '');
    this.supportsGroupMemberAdministrationFormControl.setValue(
      UserDirectoryUtil.hasParameter(parameters, 'SupportsGroupMemberAdministration') ?
        UserDirectoryUtil.getParameter(parameters, 'SupportsGroupMemberAdministration') : '');
    this.supportsUserAdministrationFormControl.setValue(
      UserDirectoryUtil.hasParameter(parameters, 'SupportsUserAdministration') ?
        UserDirectoryUtil.getParameter(parameters, 'SupportsUserAdministration') : '');
    this.userBaseDNFormControl.setValue(UserDirectoryUtil.hasParameter(parameters, 'UserBaseDN') ?
      UserDirectoryUtil.getParameter(parameters, 'UserBaseDN') : '');
    this.userObjectClassFormControl.setValue(UserDirectoryUtil.hasParameter(parameters, 'UserObjectClass') ?
      UserDirectoryUtil.getParameter(parameters, 'UserObjectClass') : 'inetOrgPerson');
    this.userEmailAttributeFormControl.setValue(UserDirectoryUtil.hasParameter(parameters, 'UserEmailAttribute') ?
      UserDirectoryUtil.getParameter(parameters, 'UserEmailAttribute') : 'mail');
    this.userNameAttributeFormControl.setValue(UserDirectoryUtil.hasParameter(parameters, 'UserNameAttribute') ?
      UserDirectoryUtil.getParameter(parameters, 'UserNameAttribute') : 'Name');
    this.userPreferredNameAttributeFormControl.setValue(UserDirectoryUtil.hasParameter(parameters, 'UserPreferredNameAttribute') ?
      UserDirectoryUtil.getParameter(parameters, 'UserPreferredNameAttribute') : 'nickName');
    this.userMobileNumberAttributeFormControl.setValue(
      UserDirectoryUtil.hasParameter(parameters, 'UserMobileNumberAttribute') ?
        UserDirectoryUtil.getParameter(parameters, 'UserMobileNumberAttribute') : 'mobile');
    this.userPhoneNumberAttributeFormControl.setValue(
      UserDirectoryUtil.hasParameter(parameters, 'UserPhoneNumberAttribute') ?
        UserDirectoryUtil.getParameter(parameters, 'UserPhoneNumberAttribute') : 'telephoneNumber');
    this.userUsernameAttributeFormControl.setValue(UserDirectoryUtil.hasParameter(parameters, 'UserUsernameAttribute') ?
      UserDirectoryUtil.getParameter(parameters, 'UserUsernameAttribute') : 'uid');
    this.useSSLFormControl.setValue(
      UserDirectoryUtil.hasParameter(parameters, 'UseSSL') ? UserDirectoryUtil.getParameter(parameters, 'UseSSL') :
        'false');
  }

  validate(c: AbstractControl): ValidationErrors | null {
    return this.ldapUserDirectoryForm.valid ? null : {
      invalidForm: {
        valid: false,
        message: 'ldapUserDirectoryForm fields are invalid'
      }
    };
  }


  // tslint:disable-next-line:no-any
  writeValue(val: any): void {
    if (val) {
      this.ldapUserDirectoryForm.setValue(val, {emitEvent: false});
    }
  }
}
