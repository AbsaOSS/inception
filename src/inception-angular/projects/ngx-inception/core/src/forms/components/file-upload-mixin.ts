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

import {FormGroupDirective, NgControl, NgForm} from '@angular/forms';
import {CanUpdateErrorStateCtor, ErrorStateMatcher, mixinErrorState} from '@angular/material/core';

/**
 * See https://github.com/merlosy/ngx-material-file-input.
 */
export class FileUploadBase {
  /* tslint:disable:variable-name */
  constructor(public _defaultErrorStateMatcher: ErrorStateMatcher, public _parentForm: NgForm,
              public _parentFormGroup: FormGroupDirective, public ngControl: NgControl) {
  }
}

/**
 * Allows a custom ErrorStateMatcher to be used with the file-upload component.
 */
export const FileUploadMixinBase: CanUpdateErrorStateCtor & typeof FileUploadBase = mixinErrorState(FileUploadBase);
