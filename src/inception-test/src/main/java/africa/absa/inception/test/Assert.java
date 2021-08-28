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

package africa.absa.inception.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * The <b>Assert</b> class provides additional assertions for JUnit tests.
 *
 * @author Marcus Portmann
 */
public class Assert {

  /**
   * Asserts that two LocalDateTime values are equal.
   *
   * @param expected the expected value
   * @param actual the value to check
   * @param message the for message for the AssertionError
   */
  public static void assertEqualsToMillisecond(
      LocalDateTime expected, LocalDateTime actual, String message) {
    if ((expected != null) && (actual != null)) {
      assertEquals(
          expected.truncatedTo(ChronoUnit.MILLIS), actual.truncatedTo(ChronoUnit.MILLIS), message);
    } else {
      assertEquals(expected, actual, message);
    }
  }
}
