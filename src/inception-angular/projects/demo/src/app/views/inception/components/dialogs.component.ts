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

import {ConfirmationDialogComponent, DialogService, Error} from '@absaoss/ngx-inception/core';
import {Component} from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';
import {first} from 'rxjs/operators';

/**
 * The DialogsComponent class implements the lists component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'dialogs.component.html'
})
export class DialogsComponent {

  /**
   * Constructs a new DialogsComponent.
   *
   * @param dialogService The dialog service.
   */
  constructor(private dialogService: DialogService) {
  }

  confirmation(): void {
    const dialogRef: MatDialogRef<ConfirmationDialogComponent, boolean> = this.dialogService.showConfirmationDialog(
      {message: 'Perform the action?'});

    dialogRef.afterClosed()
    .pipe(first())
    .subscribe((confirmation: boolean | undefined) => {
    });
  }

  error(): void {
    const error: Error = new Error('This is the error message');

    this.dialogService.showErrorDialog(error);
  }

  info(): void {
    this.dialogService.showInformationDialog({message: 'This is an information message.'});
  }

  warning(): void {
    this.dialogService.showWarningDialog({message: 'This is a warning message.'});
  }
}
