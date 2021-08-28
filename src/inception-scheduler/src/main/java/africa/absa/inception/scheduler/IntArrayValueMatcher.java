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

package africa.absa.inception.scheduler;

import java.util.List;

/**
 * The <b>IntArrayValueMatcher</b> class implements a <b>ValueMatcher</b> whose rules are in a plain
 * array of integer values.
 *
 * <p>When asked to validate a value, this ValueMatcher checks if it is in the array.
 *
 * @author Carlo Pelliccia
 * @author Marcus Portmann
 */
public class IntArrayValueMatcher implements ValueMatcher {

  /** The accepted values. */
  private final int[] values;

  /**
   * Builds the ValueMatcher.
   *
   * @param integers The Integer elements, one for every value accepted by the matcher. The match()
   *     method will return true only if its parameter will be one of this list.
   */
  IntArrayValueMatcher(List<Integer> integers) {
    int size = integers.size();

    values = new int[size];

    for (int i = 0; i < size; i++) {
      try {
        values[i] = integers.get(i);
      } catch (Exception e) {
        throw new IllegalArgumentException(e.getMessage());
      }
    }
  }

  /**
   * Validate the given integer value against a set of rules.
   *
   * <p>Returns <b>true</b> if the given value is included in the matcher list.
   *
   * @param value the value
   * @return <b>true</b> if the given value matches the rules of the <b>ValueMatcher</b> ,
   *     <b>false</b> otherwise
   */
  public boolean match(int value) {
    for (int value1 : values) {
      if (value1 == value) {
        return true;
      }
    }

    return false;
  }
}
