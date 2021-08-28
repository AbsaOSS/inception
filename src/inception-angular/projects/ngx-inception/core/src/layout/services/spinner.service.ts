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

import {Overlay, OverlayRef} from '@angular/cdk/overlay';
import {ComponentPortal} from '@angular/cdk/portal';
import {Injectable} from '@angular/core';
import {SpinnerComponent} from '../components/spinner.component';

/**
 * The Spinner Service implementation.
 *
 * @author Marcus Portmann
 */
@Injectable({
  providedIn: 'root'
})
export class SpinnerService {

  private overlayRef?: OverlayRef;

  private spinnerComponentPortal?: ComponentPortal<SpinnerComponent>;

  /**
   * Constructs a new SpinnerService.
   *
   * @param overlay The overlay.
   */
  constructor(private overlay: Overlay) {
    console.log('Initializing the Spinner Service');

    // Create ComponentPortal that can be attached to a PortalHost
    this.spinnerComponentPortal = new ComponentPortal(SpinnerComponent);
  }

  /**
   * Hide the spinner.
   */
  hideSpinner(): void {
    if (this.spinnerComponentPortal) {
      // this.spinnerComponentPortal.detach();
      // this.spinnerComponentPortal = undefined;
    }

    if (this.overlayRef) {
      this.overlayRef.dispose();
      this.overlayRef = undefined;
    }
  }

  /**
   * Show the spinner.
   */
  showSpinner(): void {
    if (!this.overlayRef) {
      // Returns an OverlayRef (which is a PortalHost)
      this.overlayRef = this.overlay.create();

      // // Create ComponentPortal that can be attached to a PortalHost
      // this.spinnerComponentPortal = new ComponentPortal(SpinnerComponent);

      // Attach ComponentPortal to PortalHost
      this.overlayRef.attach(this.spinnerComponentPortal);
    }
  }
}
