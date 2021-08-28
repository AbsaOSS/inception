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

import {Injector} from '@angular/core';

/**
 * The Angular injector that allows singletons to be retrieved by the Inception framework.
 */
export let InceptionInjector: Injector;

/**
 * Helper to set the exported {@link InceptionInjector}, needed as ES6 modules export immutable
 * bindings (see http://2ality.com/2015/07/es6-module-exports.html) for which trying to make changes
 * after using `import {InceptionInjector}` would throw:
 * "TS2539: Cannot assign to 'InceptionInjector' because it is not a variable".
 */
export function setInceptionInjector(injector: Injector) {

  if (!InceptionInjector) {
    InceptionInjector = injector;
  }
}
