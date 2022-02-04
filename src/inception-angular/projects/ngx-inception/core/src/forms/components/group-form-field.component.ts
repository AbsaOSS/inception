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

import {Directionality} from '@angular/cdk/bidi';
import {BooleanInput, coerceBooleanProperty} from '@angular/cdk/coercion';
import {Platform} from '@angular/cdk/platform';
import {
  AfterContentChecked, AfterContentInit, AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef,
  Component, ContentChild, ContentChildren, ElementRef, Inject, InjectionToken, Input, NgZone,
  OnDestroy, Optional, QueryList, ViewChild, ViewEncapsulation,
} from '@angular/core';
import {MatCheckbox} from '@angular/material/checkbox';
import {CanColor, mixinColor,} from '@angular/material/core';
import {
  _MAT_HINT, FloatLabelType, getMatFormFieldDuplicatedHintError, MAT_ERROR,
  MAT_FORM_FIELD_DEFAULT_OPTIONS, MAT_PREFIX, MAT_SUFFIX, MatError, matFormFieldAnimations,
  MatFormFieldAppearance, MatFormFieldDefaultOptions, MatHint, MatLabel, MatPrefix, MatSuffix
} from '@angular/material/form-field';
import {MatRadioButton, MatRadioGroup} from '@angular/material/radio';
import {ANIMATION_MODULE_TYPE} from '@angular/platform-browser/animations';
import {merge, Subject} from 'rxjs';
import {startWith, takeUntil} from 'rxjs/operators';

let nextUniqueId = 0;
const floatingLabelScale = 0.75;
const outlineGapPadding = 5;

/**
 * Boilerplate for applying mixins to GroupFormField.
 * @docs-private
 */
const _GroupFormFieldBase = mixinColor(
  class {
    constructor(public _elementRef: ElementRef) {
    }
  },
  'primary',
);

/**
 * Injection token that can be used to inject an instances of `GroupFormField`. It serves
 * as alternative token to the actual `GroupFormField` class which would cause unnecessary
 * retention of the `GroupFormField` class and its component metadata.
 */
export const GROUP_FORM_FIELD_COMPONENT = new InjectionToken<GroupFormFieldComponent>('GroupFormFieldComponent');

/** Container for form controls that applies Material Design styling and behavior. */
@Component({
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'group-form-field',
  exportAs: 'groupFormField',
  templateUrl: 'group-form-field.component.html',
  styleUrls: [
    'group-form-field.component.scss'
  ],
  animations: [matFormFieldAnimations.transitionMessages],
  // eslint-disable-next-line @angular-eslint/no-host-metadata-property
  host: {
    'class': 'mat-form-field mat-form-field-can-float mat-form-field-should-float',
    '[class.mat-form-field-appearance-standard]': 'appearance == "standard"',
    '[class.mat-form-field-appearance-fill]': 'appearance == "fill"',
    '[class.mat-form-field-appearance-outline]': 'appearance == "outline"',
    '[class.mat-form-field-appearance-legacy]': 'appearance == "legacy"',
    '[class.mat-form-field-invalid]': '_hasError()',
    '[class.mat-form-field-has-label]': '_hasFloatingLabel()',
    '[class.mat-form-field-disabled]': '_isDisabled()',
    '[class.mat-focused]': '_isFocused()',
    '[class._mat-animation-noopable]': '!_animationsEnabled',
    '[class.mat-accent]': 'color == "accent"',
    '[class.mat-warn]': 'color == "warn"',
  },
  // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
  inputs: ['color'],
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {provide: GROUP_FORM_FIELD_COMPONENT, useExisting: GroupFormFieldComponent},
  ]
})

export class GroupFormFieldComponent extends _GroupFormFieldBase
  implements AfterContentInit, AfterContentChecked, AfterViewInit, OnDestroy, CanColor {

  static ngAcceptInputType_hideRequiredMarker: BooleanInput;

  @ContentChildren(MatCheckbox, {descendants: true}) _checkboxChildren!: QueryList<MatCheckbox>;

  @ViewChild('connectionContainer', {static: true}) _connectionContainerRef!: ElementRef;

  @ContentChildren(MAT_ERROR, {descendants: true}) _errorChildren!: QueryList<MatError>;

  @ContentChildren(_MAT_HINT, {descendants: true}) _hintChildren!: QueryList<MatHint>;

  // Unique id for the hint label.
  readonly _hintLabelId: string = `mat-hint-${nextUniqueId++}`;

  @ContentChild(MatLabel) _labelChildNonStatic?: MatLabel;

  @ContentChild(MatLabel, {static: true}) _labelChildStatic!: MatLabel;

  // Unique id for the internal form field label.
  readonly _labelId = `group-form-field-label-${nextUniqueId++}`;

  @ContentChildren(MAT_PREFIX, {descendants: true}) _prefixChildren!: QueryList<MatPrefix>;

  @ContentChild(MatRadioGroup) _radioGroupChildNonStatic!: MatRadioGroup;

  @ContentChild(MatRadioGroup, {static: true}) _radioGroupChildStatic!: MatRadioGroup;

  /** State of the mat-hint and mat-error animations. */
  _subscriptAnimationState: string = '';

  @ContentChildren(MAT_SUFFIX, {descendants: true}) _suffixChildren!: QueryList<MatSuffix>;

  /** Whether the Angular animations are enabled. */
  private readonly _animationsEnabled: boolean;

  private _destroyed = new Subject<void>();

  @ViewChild('label') private _label!: ElementRef;

  /**
   * Whether the outline gap needs to be calculated immediately on the next change detection run.
   */
  private _outlineGapCalculationNeededImmediately = false;

  /** Whether the outline gap needs to be calculated next time the zone has stabilized. */
  private _outlineGapCalculationNeededOnStable = false;

  /** Override for the logic that disables the label animation in certain cases. */
  private _showAlwaysAnimate = false;

  constructor(
    public _elementRef: ElementRef,
    private _changeDetectorRef: ChangeDetectorRef,
    @Optional() private _dir: Directionality,
    @Optional() @Inject(MAT_FORM_FIELD_DEFAULT_OPTIONS) private _defaults:
      MatFormFieldDefaultOptions, private _platform: Platform, private _ngZone: NgZone,
    @Optional() @Inject(ANIMATION_MODULE_TYPE) _animationMode: string) {
    super(_elementRef);

    this.floatLabel = this._getDefaultFloatLabelState();
    this._animationsEnabled = _animationMode !== 'NoopAnimations';

    // Set the default through here so we invoke the setter on the first run.
    this.appearance = _defaults && _defaults.appearance ? _defaults.appearance : 'legacy';
    this._hideRequiredMarker =
      _defaults && _defaults.hideRequiredMarker != null ? _defaults.hideRequiredMarker : false;
  }

  private _appearance!: MatFormFieldAppearance;

  /** The form-field appearance style. */
  @Input()
  get appearance(): MatFormFieldAppearance {
    return this._appearance;
  }

  set appearance(value: MatFormFieldAppearance) {
    const oldValue = this._appearance;

    this._appearance = value || (this._defaults && this._defaults.appearance) || 'legacy';

    if (this._appearance === 'outline' && oldValue !== value) {
      this._outlineGapCalculationNeededOnStable = true;
    }
  }

  private _floatLabel: FloatLabelType = this._getDefaultFloatLabelState();

  /**
   * Whether the label should always float, never float or float as the user types.
   *
   * Note: only the legacy appearance supports the `never` option. `never` was originally added as a
   * way to make the floating label emulate the behavior of a standard input placeholder. However
   * the form field now supports both floating labels and placeholders. Therefore in the non-legacy
   * appearances the `never` option has been disabled in favor of just using the placeholder.
   */
  @Input()
  get floatLabel(): FloatLabelType {
    return this.appearance !== 'legacy' && this._floatLabel === 'never' ? 'auto' : this._floatLabel;
  }

  set floatLabel(value: FloatLabelType) {
    if (value !== this._floatLabel) {
      this._floatLabel = value || this._getDefaultFloatLabelState();
      this._changeDetectorRef.markForCheck();
    }
  }

  private _hideRequiredMarker: boolean;

  /** Whether the required marker should be hidden. */
  @Input()
  get hideRequiredMarker(): boolean {
    return this._hideRequiredMarker;
  }

  set hideRequiredMarker(value: boolean) {
    this._hideRequiredMarker = coerceBooleanProperty(value);
  }

  private _hintLabel: string = '';

  /** Text for the form field hint. */
  @Input()
  get hintLabel(): string {
    return this._hintLabel;
  }

  set hintLabel(value: string) {
    this._hintLabel = value;
    this._processHints();
    // this._syncDescribedByIds();
  }

  get _labelChild(): MatLabel {
    return this._labelChildNonStatic || this._labelChildStatic;
  }

  get _radioGroupChild(): MatRadioGroup {
    return this._radioGroupChildNonStatic || this._radioGroupChildStatic;
  }

  // /** Animates the label up and locks it in position. */
  // private _animateAndLockLabel(): void {
  //   if (this._animationsEnabled && this._label) {
  //     this._showAlwaysAnimate = true;
  //
  //     fromEvent(this._label.nativeElement, 'transitionend').pipe(take(1)).subscribe(() => {
  //       this._showAlwaysAnimate = false;
  //     });
  //   }
  //
  //   this._changeDetectorRef.markForCheck();
  // }

  _controlId(): string | undefined {
    if (!!this._radioGroupChild) {
      if (this._radioGroupChild._radios.length > 0) {
        return this._radioGroupChild._radios.get(0)?.id;
      }
    } else if (this._checkboxChildren.length > 0) {
      return this._checkboxChildren.get(0)?.id;
    }

    return undefined;
  }

  /** Determines whether to display hints or errors. */
  _getDisplayedMessages(): 'error' | 'hint' {
    return (this._errorChildren && this._errorChildren.length > 0) ? 'error' : 'hint';
  }

  /** Whether there are one or more errors associated with the group form field. */
  _hasError(): boolean {
    //console.log('this._errorChildren = ', this._errorChildren);

    console.log('this._radioGroupChild = ', this._radioGroupChild);

    if (this._errorChildren) {
      if (this._errorChildren.length > 0) {
        return true;
      }
    }

    return false;
  }

  _hasFloatingLabel(): boolean {
    return this._hasLabel();
  }

  _hasLabel(): boolean {
    return !!(this._labelChildNonStatic || this._labelChildStatic);
  }

  _isDisabled(): boolean {
    if (!!this._radioGroupChild) {
      return this._radioGroupChild.disabled;
    } else if (this._checkboxChildren && (this._checkboxChildren.length > 0)) {
      const checkboxChildren = this._checkboxChildren.toArray();

      for (let i = 0; i < checkboxChildren.length; i++) {
        if (!checkboxChildren[i].disabled) {
          return false;
        }
      }

      return true;
    } else {
      return false;
    }
  }

  _isFocused(): boolean {
    return false;
  }

  _isRequired(): boolean {
    if (!!this._radioGroupChild) {
      return this._radioGroupChild.required;
    } else {
      return false;
    }
  }

  /** Does any extra processing that is required when handling the hints. */
  _processHints() {
    this._validateHints();
  }

  /**
   * Updates the width and position of the gap in the outline. Only relevant for the outline
   * appearance.
   */
  _updateOutlineGap() {
    const labelEl = this._label ? this._label.nativeElement : null;

    if (this.appearance !== 'outline' || !labelEl || !labelEl.children.length ||
      !labelEl.textContent.trim()) {
      return;
    }

    if (!this._platform.isBrowser) {
      // getBoundingClientRect isn't available on the server.
      return;
    }
    // If the element is not present in the DOM, the outline gap will need to be calculated
    // the next time it is checked and in the DOM.
    if (!this._isAttachedToDOM()) {
      this._outlineGapCalculationNeededImmediately = true;
      return;
    }

    let startWidth = 0;
    let gapWidth = 0;

    const container = this._connectionContainerRef.nativeElement;
    const startEls = container.querySelectorAll('.mat-form-field-outline-start');
    const gapEls = container.querySelectorAll('.mat-form-field-outline-gap');

    if (this._label && this._label.nativeElement.children.length) {
      const containerRect = container.getBoundingClientRect();

      // If the container's width and height are zero, it means that the element is
      // invisible and we can't calculate the outline gap. Mark the element as needing
      // to be checked the next time the zone stabilizes. We can't do this immediately
      // on the next change detection, because even if the element becomes visible,
      // the `ClientRect` won't be reclaculated immediately. We reset the
      // `_outlineGapCalculationNeededImmediately` flag some we don't run the checks twice.
      if (containerRect.width === 0 && containerRect.height === 0) {
        this._outlineGapCalculationNeededOnStable = true;
        this._outlineGapCalculationNeededImmediately = false;
        return;
      }

      const containerStart = this._getStartEnd(containerRect);
      const labelStart = this._getStartEnd(labelEl.children[0].getBoundingClientRect());
      let labelWidth = 0;

      for (const child of labelEl.children) {
        labelWidth += child.offsetWidth;
      }
      startWidth = Math.abs(labelStart - containerStart) - outlineGapPadding;
      gapWidth = labelWidth > 0 ? labelWidth * floatingLabelScale + outlineGapPadding * 2 : 0;
    }

    for (let i = 0; i < startEls.length; i++) {
      startEls[i].style.width = `${startWidth}px`;
    }
    for (let i = 0; i < gapEls.length; i++) {
      gapEls[i].style.width = `${gapWidth}px`;
    }

    this._outlineGapCalculationNeededOnStable =
      this._outlineGapCalculationNeededImmediately = false;
  }

  /** Throws an error if there are no valid child controls for the group form field control. */
  _validateControlChildren() {
    // TODO: Validate that there is at least one MatRadioGroup, MatRadioButton or MatCheckbox control
    // if (!this._control) {
    //   throw getGroupFormFieldMissingControlError();
    // }
  }

  /**
   * Gets an ElementRef for the element that an overlay attached to the form-field should be
   * positioned relative to.
   */
  getConnectedOverlayOrigin(): ElementRef {
    return this._connectionContainerRef || this._elementRef;
  }

  ngAfterContentChecked() {
    this._validateControlChildren();
    if (this._outlineGapCalculationNeededImmediately) {
      this._updateOutlineGap();
    }
  }

  ngAfterContentInit() {
    this._validateControlChildren();

    // Note that we have to run outside of the `NgZone` explicitly,
    // in order to avoid throwing users into an infinite loop
    // if `zone-patch-rxjs` is included.
    this._ngZone.runOutsideAngular(() => {
      this._ngZone.onStable.asObservable().pipe(takeUntil(this._destroyed)).subscribe(() => {
        if (this._outlineGapCalculationNeededOnStable) {
          this._updateOutlineGap();
        }
      });
    });

    // Run change detection and update the outline if the suffix or prefix changes.
    merge(this._prefixChildren.changes, this._suffixChildren.changes).subscribe(() => {
      this._outlineGapCalculationNeededOnStable = true;
      this._changeDetectorRef.markForCheck();
    });

    // Re-validate when the number of hints changes.
    this._hintChildren.changes.pipe(startWith(null)).subscribe(() => {
      this._processHints();
      this._changeDetectorRef.markForCheck();
    });

    // Update the aria-described by when the number of errors changes.
    this._errorChildren.changes.pipe(startWith(null)).subscribe(() => {
      // this._syncDescribedByIds();
      this._changeDetectorRef.markForCheck();
    });

    if (this._dir) {
      this._dir.change.pipe(takeUntil(this._destroyed)).subscribe(() => {
        if (typeof requestAnimationFrame === 'function') {
          this._ngZone.runOutsideAngular(() => {
            requestAnimationFrame(() => this._updateOutlineGap());
          });
        } else {
          this._updateOutlineGap();
        }
      });
    }

    if (!!this._labelChild) {
      if (!!this._radioGroupChild) {
        this._radioGroupChild._radios.forEach((radioButton: MatRadioButton) => {
          radioButton.ariaLabelledby = this._labelId;
        });
      } else if (this._checkboxChildren && (this._checkboxChildren.length > 0)) {
        this._checkboxChildren.forEach((checkbox: MatCheckbox) => {
          checkbox.ariaLabelledby = this._labelId;
        });
      }
    }
  }

  ngAfterViewInit() {
    // Avoid animations on load.
    this._subscriptAnimationState = 'enter';
    this._changeDetectorRef.detectChanges();
  }

  ngOnDestroy() {
    this._destroyed.next();
    this._destroyed.complete();
  }

  /** Gets the default float label state. */
  private _getDefaultFloatLabelState(): FloatLabelType {
    return (this._defaults && this._defaults.floatLabel) || 'auto';
  }

  /** Gets the start end of the rect considering the current directionality. */
  private _getStartEnd(rect: ClientRect): number {
    return (this._dir && this._dir.value === 'rtl') ? rect.right : rect.left;
  }

  /** Checks whether the form field is attached to the DOM. */
  private _isAttachedToDOM(): boolean {
    const element: HTMLElement = this._elementRef.nativeElement;

    if (element.getRootNode) {
      const rootNode = element.getRootNode();
      // If the element is inside the DOM the root node will be either the document
      // or the closest shadow root, otherwise it'll be the element itself.
      return rootNode && rootNode !== element;
    }

    // Otherwise fall back to checking if it's in the document. This doesn't account for
    // shadow DOM, however browser that support shadow DOM should support `getRootNode` as well.
    return document.documentElement!.contains(element);
  }

  // /**
  //  * Sets the list of element IDs that describe the child control. This allows the control to update
  //  * its `aria-describedby` attribute accordingly.
  //  */
  // private _syncDescribedByIds() {
  //   if (this._control) {
  //     let ids: string[] = [];
  //
  //     // TODO(wagnermaciel): Remove the type check when we find the root cause of this bug.
  //     if (
  //       this._control.userAriaDescribedBy &&
  //       typeof this._control.userAriaDescribedBy === 'string'
  //     ) {
  //       ids.push(...this._control.userAriaDescribedBy.split(' '));
  //     }
  //
  //     if (this._getDisplayedMessages() === 'hint') {
  //       const startHint = this._hintChildren
  //         ? this._hintChildren.find(hint => hint.align === 'start')
  //         : null;
  //       const endHint = this._hintChildren
  //         ? this._hintChildren.find(hint => hint.align === 'end')
  //         : null;
  //
  //       if (startHint) {
  //         ids.push(startHint.id);
  //       } else if (this._hintLabel) {
  //         ids.push(this._hintLabelId);
  //       }
  //
  //       if (endHint) {
  //         ids.push(endHint.id);
  //       }
  //     } else if (this._errorChildren) {
  //       ids.push(...this._errorChildren.map(error => error.id));
  //     }
  //
  //     this._control.setDescribedByIds(ids);
  //   }
  // }

  /**
   * Ensure that there is a maximum of one of each `<mat-hint>` alignment specified, with the
   * attribute being considered as `align="start"`.
   */
  private _validateHints() {
    if (this._hintChildren) {
      let startHint: MatHint;
      let endHint: MatHint;
      this._hintChildren.forEach((hint: MatHint) => {
        if (hint.align === 'start') {
          if (startHint || this.hintLabel) {
            throw getMatFormFieldDuplicatedHintError('start');
          }
          startHint = hint;
        } else if (hint.align === 'end') {
          if (endHint) {
            throw getMatFormFieldDuplicatedHintError('end');
          }
          endHint = hint;
        }
      });
    }
  }
}



