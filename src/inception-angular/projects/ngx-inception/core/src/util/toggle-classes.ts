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

/* tslint:disable */
export const removeClasses = (classNames: string[]): boolean => {
  const bodySelector = document.querySelector('body');

  if (bodySelector) {
    const matchClasses = classNames.map((value) => bodySelector.classList.contains(value));

    return matchClasses.indexOf(true) !== -1;
  } else {
    return false;
  }
};

export const toggleClasses = (toggle: string, classNames: string[]): void => {
  const level = classNames.indexOf(toggle);
  const newClassNames = classNames.slice(0, level + 1);

  const bodySelector = document.querySelector('body');

  if (bodySelector) {
    if (removeClasses(newClassNames)) {
      newClassNames.map((value) => bodySelector.classList.remove(value));
    } else {
      bodySelector.classList.add(toggle);
    }
  }
};


/*
const RemoveClasses = (NewClassNames) => {
  const MatchClasses = NewClassNames.map(
    (Class) => document.querySelector('body').classList.contains(Class));
  return MatchClasses.indexOf(true) !== -1;
};


export const ToggleClasses = (Toggle, ClassNames) => {
  const Level = ClassNames.indexOf(Toggle);
  const NewClassNames = ClassNames.slice(0, Level + 1);

  if (RemoveClasses(NewClassNames)) {
    NewClassNames.map((Class) => document.querySelector('body').classList.remove(Class));
  } else {
    document.querySelector('body').classList.add(Toggle);
  }
};
*/
