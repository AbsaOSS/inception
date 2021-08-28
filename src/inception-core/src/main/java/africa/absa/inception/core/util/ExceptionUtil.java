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

package africa.absa.inception.core.util;

/**
 * The <b>ExceptionUtil</b> class is a utility class which provides methods for manipulating
 * exceptions.
 *
 * @author Marcus Portmann
 */
public final class ExceptionUtil {

  /**
   * Returns the nested messages for an exception.
   *
   * @param e the exception
   * @return the nested messages for the exception
   */
  public static String getNestedMessages(Throwable e) {
    StringBuilder buffer = new StringBuilder();

    Throwable current = e;

    do {
      if (buffer.length() > 0) {
        buffer.append(": ");
      }

      buffer.append(current.getMessage());

      current = current.getCause();
    } while (current != null);

    return buffer.toString();
  }
}
