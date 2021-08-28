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
  selector: 'inception-internal-user-directory',
  templateUrl: 'internal-user-directory.component.html',
  styleUrls: ['internal-user-directory.component.css'],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => InternalUserDirectoryComponent),
    multi: true
  }, {
    provide: NG_VALIDATORS,
    useExisting: forwardRef(() => InternalUserDirectoryComponent),
    multi: true
  }
  ]
})
export class InternalUserDirectoryComponent implements ControlValueAccessor, Validator {

  internalUserDirectoryForm: FormGroup;

  maxFilteredGroupMembersFormControl: FormControl;

  maxFilteredGroupsFormControl: FormControl;

  maxFilteredUsersFormControl: FormControl;

  maxPasswordAttemptsFormControl: FormControl;

  passwordExpiryMonthsFormControl: FormControl;

  passwordHistoryMonthsFormControl: FormControl;

  constructor() {
    // Initialise the form controls
    this.maxFilteredGroupMembersFormControl = new FormControl('', [Validators.required, Validators.pattern('^\\d+$')]);
    this.maxFilteredGroupsFormControl = new FormControl('', [Validators.required, Validators.pattern('^\\d+$')]);
    this.maxFilteredUsersFormControl = new FormControl('', [Validators.required, Validators.pattern('^\\d+$')]);
    this.maxPasswordAttemptsFormControl = new FormControl('', [Validators.required, Validators.pattern('^\\d+$')]);
    this.passwordExpiryMonthsFormControl = new FormControl('', [Validators.required, Validators.pattern('^\\d+$')]);
    this.passwordHistoryMonthsFormControl = new FormControl('', [Validators.required, Validators.pattern('^\\d+$')]);

    // Initialise the form
    this.internalUserDirectoryForm = new FormGroup({
      maxFilteredGroupMembers: this.maxFilteredGroupMembersFormControl,
      maxFilteredGroups: this.maxFilteredGroupsFormControl,
      maxFilteredUsers: this.maxFilteredUsersFormControl,
      maxPasswordAttempts: this.maxPasswordAttemptsFormControl,
      passwordExpiryMonths: this.passwordExpiryMonthsFormControl,
      passwordHistoryMonths: this.passwordHistoryMonthsFormControl
    });
  }

  getParameters(): UserDirectoryParameter[] {
    const parameters: UserDirectoryParameter[] = [];

    UserDirectoryUtil.setParameter(parameters, 'MaxPasswordAttempts', this.maxPasswordAttemptsFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'PasswordExpiryMonths', this.passwordExpiryMonthsFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'PasswordHistoryMonths', this.passwordHistoryMonthsFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'MaxFilteredUsers', this.maxFilteredUsersFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'MaxFilteredGroups', this.maxFilteredGroupsFormControl.value);
    UserDirectoryUtil.setParameter(parameters, 'MaxFilteredGroupMembers',
      this.maxFilteredGroupMembersFormControl.value);

    return parameters;
  }

  // TODO: CHECK IF WE CAN REMOVE THIS -- MARCUS
  onTouched: () => void = () => {
  }

  // tslint:disable-next-line:no-any
  registerOnChange(fn: any): void {
    this.internalUserDirectoryForm.valueChanges.subscribe(fn);
  }

  // tslint:disable-next-line:no-any
  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState?(isDisabled: boolean): void {
    isDisabled ? this.internalUserDirectoryForm.disable() : this.internalUserDirectoryForm.enable();
  }

  setParameters(parameters: UserDirectoryParameter[]) {
    this.maxPasswordAttemptsFormControl.setValue(UserDirectoryUtil.hasParameter(parameters, 'MaxPasswordAttempts') ?
      UserDirectoryUtil.getParameter(parameters, 'MaxPasswordAttempts') : '5');
    this.passwordExpiryMonthsFormControl.setValue(UserDirectoryUtil.hasParameter(parameters, 'PasswordExpiryMonths') ?
      UserDirectoryUtil.getParameter(parameters, 'PasswordExpiryMonths') : '12');
    this.passwordHistoryMonthsFormControl.setValue(UserDirectoryUtil.hasParameter(parameters, 'PasswordHistoryMonths') ?
      UserDirectoryUtil.getParameter(parameters, 'PasswordHistoryMonths') : '24');
    this.maxFilteredUsersFormControl.setValue(UserDirectoryUtil.hasParameter(parameters, 'MaxFilteredUsers') ?
      UserDirectoryUtil.getParameter(parameters, 'MaxFilteredUsers') : '100');
    this.maxFilteredGroupsFormControl.setValue(UserDirectoryUtil.hasParameter(parameters, 'MaxFilteredGroups') ?
      UserDirectoryUtil.getParameter(parameters, 'MaxFilteredGroups') : '100');
    this.maxFilteredGroupMembersFormControl.setValue(
      UserDirectoryUtil.hasParameter(parameters, 'MaxFilteredGroupMembers') ?
        UserDirectoryUtil.getParameter(parameters, 'MaxFilteredGroupMembers') : '100');
  }

  validate(c: AbstractControl): ValidationErrors | null {
    return this.internalUserDirectoryForm.valid ? null : {
      invalidForm: {
        valid: false,
        message: 'internalUserDirectoryForm fields are invalid'
      }
    };
  }

  // tslint:disable-next-line:no-any
  writeValue(val: any): void {
    if (val) {
      this.internalUserDirectoryForm.setValue(val, {emitEvent: false});
    }
  }
}

