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

import {FocusMonitor} from '@angular/cdk/a11y';
import {coerceBooleanProperty} from '@angular/cdk/coercion';
import {
  Component, DoCheck, ElementRef, HostBinding, HostListener, Input, OnDestroy, OnInit, Optional,
  Renderer2, Self
} from '@angular/core';
import {ControlValueAccessor, FormGroupDirective, NgControl, NgForm} from '@angular/forms';
import {ErrorStateMatcher, mixinColor, mixinErrorState} from '@angular/material/core';
import {MatFormFieldControl} from '@angular/material/form-field';

const _FileUploadMixinBase = mixinErrorState(
  class {
    /* eslint-disable @typescript-eslint/naming-convention, no-underscore-dangle, id-blacklist, id-match */
    constructor(public _defaultErrorStateMatcher: ErrorStateMatcher, public _parentForm: NgForm,
                public _parentFormGroup: FormGroupDirective, public ngControl: NgControl) {
    }
  }
);


/* eslint-disable @typescript-eslint/naming-convention, no-underscore-dangle, id-blacklist, id-match */
@Component({
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'file-upload',
  template: `
    <input #input type="file" [attr.multiple]="multiple? '' : null" [attr.accept]="accept">
    <span class="filename">{{ fileNames }}</span>
  `,
  styles: [`
    :host {
      display: inline-block;
    }

    :host:not(.file-input-disabled) {
      cursor: pointer;
    }

    input {
      width: 0;
      height: 0;
      opacity: 0;
      overflow: hidden;
      position: absolute;
      z-index: -1;
    }

    .filename {
      display: inline-block;
    }
  `
  ],
  providers: [{
    provide: MatFormFieldControl,
    useExisting: FileUploadComponent
  }
  ]
})
export class FileUploadComponent extends _FileUploadMixinBase implements MatFormFieldControl<File[]>,
  ControlValueAccessor, OnInit, OnDestroy, DoCheck {
  static nextId = 0;

  @Input() accept?: string;

  @Input() autofilled = false;

  controlType = 'file-input';

  @HostBinding('attr.aria-describedby') describedBy = '';

  @Input() errorStateMatcher: ErrorStateMatcher;

  focused = false;

  @HostBinding() id = `ngx-mat-file-input-${FileUploadComponent.nextId++}`;

  @Input() multiple = false;

  @Input() placeholder = '';

  /**
   * @see https://angular.io/api/forms/ControlValueAccessor
   */
  constructor(private fm: FocusMonitor, private _elementRef: ElementRef, private _renderer: Renderer2,
              public _defaultErrorStateMatcher: ErrorStateMatcher, @Optional() @Self() public ngControl: NgControl,
              @Optional() public _parentForm: NgForm, @Optional() public _parentFormGroup: FormGroupDirective) {
    super(_defaultErrorStateMatcher, _parentForm, _parentFormGroup, ngControl);

    this.errorStateMatcher = _defaultErrorStateMatcher;

    if (this.ngControl != null) {
      this.ngControl.valueAccessor = this;
    }
    fm.monitor(_elementRef.nativeElement, true).subscribe(origin => {
      this.focused = !!origin;
      this.stateChanges.next();
    });
  }

  private _required = false;

  @Input() get required() {
    return this._required;
  }

  set required(req: boolean) {
    this._required = coerceBooleanProperty(req);
    this.stateChanges.next();
  }

  @Input() get disabled(): boolean {
    return this._elementRef.nativeElement.disabled;
  }

  set disabled(dis: boolean) {
    this.setDisabledState(coerceBooleanProperty(dis));
    this.stateChanges.next();
  }

  /**
   * Whether the current input has files
   */
  get empty() {
    return !this._elementRef.nativeElement.value || this._elementRef.nativeElement.value.length === 0;
  }

  get fileNames() {
    if (this.value) {
      return this.value.map((f: File) => f.name).join(',');
    } else {
      return this.placeholder;
    }
  }

  @HostBinding('class.file-input-disabled') get isDisabled() {
    return this.disabled;
  }

  @HostBinding('class.mat-form-field-should-float') get shouldLabelFloat() {
    return this.focused || !this.empty || this.placeholder !== undefined;
  }

  @Input() get value(): File[] | null {
    return this.empty ? null : this._elementRef.nativeElement.value || [];
  }

  set value(files: File[] | null) {
    if (files) {
      this.writeValue(files);
      this.stateChanges.next();
    }
  }

  @HostListener('focusout') blur() {
    this.focused = false;
    this._onTouched();
  }

  @HostListener('change', ['$event']) change(event: Event) {
    const fileList: FileList | null = (event.target as HTMLInputElement).files;
    const fileArray: File[] = [];
    if (fileList) {
      // eslint-disable-next-line @typescript-eslint/prefer-for-of
      for (let i = 0; i < fileList.length; i++) {
        fileArray.push(fileList[i]);
      }
    }
    this.value = fileArray;
    this._onChange(this.value);
  }

  /**
   * Remove all files from the file input component
   * @param [event] optional event that may have triggered the clear action
   */
  clear(event?: Event) {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }
    this.value = null;
    this._elementRef.nativeElement.querySelector('input').value = null;
    this._onChange(this.value);
  }

  ngDoCheck(): void {
    if (this.ngControl) {
      // We need to re-evaluate this on every change detection cycle, because there are some
      // error triggers that we can't subscribe to (e.g. parent form submissions). This means
      // that whatever logic is in here has to be super lean or we risk destroying the performance.
      this.updateErrorState();
    }
  }

  ngOnDestroy() {
    this.stateChanges.complete();
    this.fm.stopMonitoring(this._elementRef.nativeElement);
  }

  ngOnInit() {
    this.multiple = coerceBooleanProperty(this.multiple);
  }

  onContainerClick(event: MouseEvent) {
    if ((event.target as Element).tagName.toLowerCase() !== 'input' && !this.disabled) {
      this._elementRef.nativeElement.querySelector('input').focus();
      this.focused = true;
      this.open();
    }
  }

  open() {
    if (!this.disabled) {
      this._elementRef.nativeElement.querySelector('input').click();
    }
  }

  registerOnChange(fn: (_: any) => void): void {
    this._onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this._onTouched = fn;
  }

  setDescribedByIds(ids: string[]) {
    this.describedBy = ids.join(' ');
  }

  setDisabledState(isDisabled: boolean): void {
    this._renderer.setProperty(this._elementRef.nativeElement, 'disabled', isDisabled);
  }

  writeValue(files: File[] | null): void {
    this._renderer.setProperty(this._elementRef.nativeElement, 'value', files);
  }

  private _onChange = (_: any) => {
  };

  private _onTouched = () => {
  };
}
