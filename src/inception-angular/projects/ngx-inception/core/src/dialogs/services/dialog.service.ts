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

import {Injectable} from '@angular/core';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {Error} from '../../errors/error';
import {ConfirmationDialogComponent} from '../components/confirmation-dialog.component';
import {ErrorDialogComponent} from '../components/error-dialog.component';
import {InformationDialogComponent} from '../components/information-dialog.component';
import {WarningDialogComponent} from '../components/warning-dialog.component';
import {DialogData} from './dialog-data';

/**
 * The service that provides the capability to show different standard dialogs.
 *
 * @author Marcus Portmann
 */
@Injectable({
  providedIn: 'root'
})
export class DialogService {

  /**
   * Constructs a new DialogService.
   *
   * @param matDialog The material dialog.
   */
  constructor(private matDialog: MatDialog) {
    console.log('Initializing the Dialog Service');
  }

  /**
   * Show a confirmation message using the confirmation dialog.
   *
   * @param data The data.
   */
  showConfirmationDialog(data: DialogData): MatDialogRef<ConfirmationDialogComponent> {
    return this.matDialog.open(ConfirmationDialogComponent, {
      panelClass: 'confirmation-dialog',
      restoreFocus: false,
      data
    });
  }

  /**
   * Show an error using the error dialog.
   *
   * @param error The error.
   */
  showErrorDialog(error: Error): MatDialogRef<ErrorDialogComponent> {
    console.log('Error: ', error);

    if (error.cause) {
      console.log('Cause: ', error.cause);
    }

    return this.matDialog.open(ErrorDialogComponent, {
      panelClass: 'error-dialog',
      restoreFocus: false,
      data: {error}
    });
  }

  /**
   * Show an informational message using the information dialog.
   *
   * @param data The data.
   */
  showInformationDialog(data: DialogData): MatDialogRef<InformationDialogComponent> {
    return this.matDialog.open(InformationDialogComponent, {
      panelClass: 'information-dialog',
      restoreFocus: false,
      data
    });
  }

  /**
   * Show a warning using the warning dialog.
   *
   * @param data The data.
   */
  showWarningDialog(data: DialogData): MatDialogRef<WarningDialogComponent> {
    return this.matDialog.open(WarningDialogComponent, {
      panelClass: 'warning-dialog',
      restoreFocus: false,
      data
    });
  }
}

