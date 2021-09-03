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

import {Directive, Input, OnInit, TemplateRef, ViewContainerRef} from '@angular/core';
import {first} from 'rxjs/operators';
import {Session} from '../services/session';
import {SessionService} from '../services/session.service';

/**
 * The HasAuthorityDirective class implements the has authority directive.
 *
 * @author Marcus Portmann
 */
@Directive({
  // tslint:disable-next-line
  selector: '[hasAuthority]'
})
export class HasAuthorityDirective implements OnInit {

  @Input('hasAuthority') requiredAuthorities: string[] = [];

  /**
   * Constructs a new HasAuthorityDirective.
   *
   * @param templateRef    The template reference.
   * @param viewContainer  The view container for the element this directive is attached to.
   * @param sessionService The session service.
   */
  // tslint:disable-next-line
  constructor(private templateRef: TemplateRef<any>, private viewContainer: ViewContainerRef,
              private sessionService: SessionService) {
  }

  ngOnInit(): void {
    if (this.requiredAuthorities.length > 0) {
      this.sessionService.session$.pipe(first()).subscribe((session: (Session | null)) => {
        if (session) {
          let foundAuthority = false;

          for (const requiredAuthority of this.requiredAuthorities) {
            if (session.hasAuthority(requiredAuthority)) {
              foundAuthority = true;
              break;
            }
          }

          if (foundAuthority) {
            this.viewContainer.createEmbeddedView(this.templateRef);
          } else {
            this.viewContainer.clear();
          }
        }
      });
    } else {
      this.viewContainer.createEmbeddedView(this.templateRef);
    }
  }
}
