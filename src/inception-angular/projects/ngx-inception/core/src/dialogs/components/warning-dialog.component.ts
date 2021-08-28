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
import {DialogData} from '../services/dialog-data';

/**
 * The WarningDialogComponent class implements the warning dialog component.
 *
 * @author Marcus Portmann
 */
@Component({
  // tslint:disable-next-line
  selector: 'warning-dialog',
  template: `
    <div class="header">
      <i class="fas fa-3x fa-exclamation-triangle"></i>
    </div>
    <div class="message-holder">
      <span class="message">
        {{data.message}}
      </span>
    </div>
    <div class="button">
      <button mat-flat-button color="warn" *ngIf="data.buttonText; else defaultButton"
              mat-flat-button (click)="ok()"
              tabindex="-1">{{ data.buttonText }}</button>
      <ng-template #defaultButton>
        <button mat-flat-button color="warn" (click)="ok()" tabindex="-1"
                i18n="@@warning_dialog_button_ok">OK
        </button>
      </ng-template>
    </div>
  `
})
export class WarningDialogComponent {

  @HostBinding('class') hostClass = 'warning-dialog';

  /**
   * Constructs a new WarningDialogComponent.
   *
   * @param dialogRef The dialog reference.
   * @param data      The dialog data.
   */
  constructor(private dialogRef: MatDialogRef<WarningDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: DialogData) {
  }

  ok(): void {
    this.dialogRef.close();
  }
}
