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

import {Component, HostBinding, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {Error} from '../../errors/error';

/**
 * The ErrorDialogData interface defines the data that is displayed by an error dialog.
 *
 * @author Marcus Portmann
 */
export interface ErrorDialogData {

  /**
   * The error.
   */
  error: Error;
}

/**
 * The ErrorDialogComponent class implements the error dialog component.
 *
 * @author Marcus Portmann
 */
@Component({
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'error-dialog',
  template: `
    <div class="header">
      <i class="far fa-3x fa-times-circle"></i>
    </div>
    <div class="message-holder">
      <span class="message">
        {{message}}
      </span>
    </div>
    <div class="button">
      <button mat-flat-button color="error" (click)="ok()" tabindex="-1"
              i18n="@@error_dialog_button_ok">OK
      </button>
    </div>
  `
})
export class ErrorDialogComponent {

  @HostBinding('class') hostClass = 'error-dialog';

  /**
   * Constructs a new ErrorDialogComponent.
   *
   * @param dialogRef The dialog reference.
   * @param data      The dialog data.
   */
  constructor(private dialogRef: MatDialogRef<ErrorDialogComponent>,
              @Inject(MAT_DIALOG_DATA) private data: ErrorDialogData) {
  }

  get message(): string {
    return this.data.error.message;
  }

  ok(): void {
    this.dialogRef.close();
  }
}
