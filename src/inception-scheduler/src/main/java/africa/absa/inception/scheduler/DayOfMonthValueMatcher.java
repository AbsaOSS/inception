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
 * The <b>DayOfMonthValueMatcher</b> class implements a <b>ValueMatcher</b> whose rules are in a
 * plain array of integer values.
 *
 * <p>When asked to validate a value, this ValueMatcher checks if it is in the array and, if not,
 * checks whether the last-day-of-month setting applies.
 *
 * @author Paul Fernley
 * @author Marcus Portmann
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class DayOfMonthValueMatcher extends IntArrayValueMatcher {

  private static final int[] lastDays = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

  /**
   * Constructs a new <b>DayOfMonthValueMatcher</b>.
   *
   * @param integers The Integer elements, one for every value accepted by the matcher. The match()
   *     method will return <b>true</b> only if its parameter will be one of this list or the
   *     last-day-of-month setting applies.
   */
  public DayOfMonthValueMatcher(List<Integer> integers) {
    super(integers);
  }

  /**
   * Returns <b>true</b> if the given value is included in the matcher list or the last-day-of-month
   * setting applies otherwise <b>false</b>.
   *
   * @param value the value
   * @param month the month
   * @param isLeapYear <b>true</b> if this is a leap year <b>false</b> otherwise
   * @return <b>true</b> if the given value matches the rules of the <b>ValueMatcher</b> ,
   *     <b>false</b> otherwise
   */
  public boolean match(int value, int month, boolean isLeapYear) {
    return (super.match(value)
        || ((value > 27) && match(32) && isLastDayOfMonth(value, month, isLeapYear)));
  }

  private boolean isLastDayOfMonth(int value, int month, boolean isLeapYear) {
    if (isLeapYear && (month == 2)) {
      return value == 29;
    } else {
      return value == lastDays[month - 1];
    }
  }
}
