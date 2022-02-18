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

/**
 * The ConfirmationDialogData interface defines the data that is displayed by a confirmation dialog.
 *
 * @author Marcus Portmann
 */
export interface ConfirmationDialogData {

  /**
   * The message.
   */
  message: string;
}

/**
 * The ConfirmationDialogComponent class implements the confirmation dialog component.
 *
 * @author Marcus Portmann
 */
@Component({
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'confirmation-dialog',
  template: `
    <div class="header">
      <i class="far fa-3x fa-question-circle"></i>
    </div>
    <div class="message-holder">
      <span class="message">
        {{message}}
      </span>
    </div>
    <div class="button">
      <button mat-flat-button color="primary" (click)="no()" tabindex="-1"
              i18n="@@confirmation_dialog_button_no">
        No
      </button>
      <button mat-flat-button color="primary" (click)="yes()" tabindex="-1"
              i18n="@@confirmation_dialog_button_yes">
        Yes
      </button>
    </div>
  `
})
export class ConfirmationDialogComponent {

  @HostBinding('class') hostClass = 'confirmation-dialog';

  /**
   * Constructs a new ConfirmationDialogComponent.
   *
   * @param dialogRef The dialog reference.
   * @param data      The dialog data.
   */
  constructor(private dialogRef: MatDialogRef<ConfirmationDialogComponent>,
              @Inject(MAT_DIALOG_DATA) private data: ConfirmationDialogData) {
  }

  get message(): string {
    return this.data.message;
  }

  no(): void {
    this.dialogRef.close(false);
  }

  yes(): void {
    this.dialogRef.close(true);
  }
}


