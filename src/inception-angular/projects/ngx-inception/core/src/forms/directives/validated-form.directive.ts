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

import {AfterViewInit, Directive, Host, Optional, Self, ViewContainerRef} from '@angular/core';
import {FormGroupDirective} from '@angular/forms';

/**
 * The ValidatedFormDirective class implements the validated form directive.
 *
 * @author Marcus Portmann
 */
@Directive({
  // tslint:disable-next-line
  selector: 'form[validatedForm]', // tslint:disable-next-line
  host: {
    '(submit)': 'onSubmit()',
    '(reset)': 'onReset()'
  }
})
export class ValidatedFormDirective implements AfterViewInit {

  /**
   * Constructs a new ValidatedFormDirective.
   *
   * @param viewContainerRef   The view container reference.
   * @param formGroupDirective The form group directive.
   */
  constructor(private viewContainerRef: ViewContainerRef,
              @Host() @Self() @Optional() private formGroupDirective: FormGroupDirective) {
  }

  ngAfterViewInit(): void {
  }

  onReset(): void {
  }

  onSubmit(): boolean {
    // Mark all controls as touched
    if (this.formGroupDirective && this.formGroupDirective.control && this.formGroupDirective.control.controls) {
      const form = this.formGroupDirective.control;

      Object.keys(form.controls).forEach(key => {
        const formControl = form.get(key);

        if (formControl) {
          formControl.markAsTouched();
        }
      });
    }

    // Find the first invalid form control and set focus to it
    return !this.checkForInvalidFormControlAndSetFocus(this.viewContainerRef.element.nativeElement);
  }

  private static isFormElement(nodeName: string): boolean {
    switch (nodeName) {
      case 'INPUT': {
        return true;
      }

      default: {
        return false;
      }
    }
  }

  // tslint:disable-next-line
  private checkForInvalidFormControlAndSetFocus(nativeElement: any): boolean {
    if (nativeElement.children && (nativeElement.children.length > 0)) {
      for (const nativeChildElement of nativeElement.children) {
        if (nativeChildElement && nativeChildElement.nodeName &&
          ValidatedFormDirective.isFormElement(nativeChildElement.nodeName)) {
          if (this.formGroupDirective && this.formGroupDirective.control && this.formGroupDirective.control.controls) {
            const formControl = this.formGroupDirective.control.controls[nativeChildElement.name];

            if (formControl && formControl.invalid) {
              nativeChildElement.focus();

              return true;
            }
          }
        }

        if (this.checkForInvalidFormControlAndSetFocus(nativeChildElement)) {
          return true;
        }
      }
    }

    return false;
  }
}
