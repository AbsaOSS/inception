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

import {AbstractControl, ValidatorFn} from '@angular/forms';

/**
 * The FileValidator class.
 *
 * @author Marcus Portmann
 */
export class FileValidator {

  /**
   * Validator function to validate that the total length of one or more files does not exceed the
   * maximum number of bytes allowed.
   *
   * @param maximumBytes The maximum number of bytes allowed.
   *
   * @returns The validator function.
   */
  public static maxSize(maximumBytes: number): ValidatorFn {
    const validatorFn = (control: AbstractControl): { [key: string]: any } | null => {

      if (control && control.value) {
        let numberOfBytes = 0;

        for (const file of (control.value as File[])) {
          numberOfBytes += file.size;
        }

        if (numberOfBytes > maximumBytes) {
          return {
            maxSize: {
              actualSize: numberOfBytes,
              allowedSize: maximumBytes
            }
          };
        }
      }

      return null;
    };

    return validatorFn;
  }

  /**
   * Validator function to validate that the total length of one or more files is greater than the
   * minimum number of bytes required.
   *
   * @param minimumBytes The minimum number of bytes required.
   *
   * @returns The validator function.
   */
  public static minSize(minimumBytes: number): ValidatorFn {
    const validatorFn = (control: AbstractControl): { [key: string]: any } | null => {

      if (control && control.value) {
        let numberOfBytes = 0;

        for (const file of (control.value as File[])) {
          numberOfBytes += file.size;
        }

        if (numberOfBytes < minimumBytes) {
          return {
            minSize: {
              actualSize: numberOfBytes,
              requiredSize: minimumBytes
            }
          };
        }
      }

      return null;
    };

    return validatorFn;
  }
}
