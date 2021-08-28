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

package africa.absa.inception.api;

import java.io.StringWriter;
import org.springframework.util.StringUtils;

/**
 * The <b>RestUtil</b> class provides utility methods that are useful when working with APIs
 * implemented as RESTful web services.
 *
 * @author Marcus Portmann
 */
public class ApiUtil {

  /**
   * Produce a string in double quotes with backslash sequences in all the right places. A backslash
   * will be inserted within &lt;/, producing &lt;\/, allowing JSON text to be delivered in HTML. In
   * JSON text, a string cannot contain a control character or an unescaped quote or backslash.
   *
   * @param string a String
   * @return a String correctly formatted for insertion in a JSON text
   */
  public static String quote(String string) {
    if (!StringUtils.hasText(string)) {
      return "\"\"";
    }

    StringWriter sw = new StringWriter();

    char b;
    char c = 0;
    String hhhh;
    int i;
    int len = string.length();

    sw.write('"');

    for (i = 0; i < len; i += 1) {
      b = c;
      c = string.charAt(i);

      switch (c) {
        case '\\':
        case '"':
          sw.write('\\');
          sw.write(c);

          break;

        case '/':
          if (b == '<') {
            sw.write('\\');
          }

          sw.write(c);

          break;

        case '\b':
          sw.write("\\b");

          break;

        case '\t':
          sw.write("\\t");

          break;

        case '\n':
          sw.write("\\n");

          break;

        case '\f':
          sw.write("\\f");

          break;

        case '\r':
          sw.write("\\r");

          break;

        default:
          if ((c < ' ')
              || ((c >= '\u0080') && (c < '\u00a0'))
              || ((c >= '\u2000') && (c < '\u2100'))) {
            sw.write("\\u");
            hhhh = Integer.toHexString(c);
            sw.write("0000", 0, 4 - hhhh.length());
            sw.write(hhhh);
          } else {
            sw.write(c);
          }
      }
    }

    sw.write('"');

    return sw.getBuffer().toString();
  }
}
