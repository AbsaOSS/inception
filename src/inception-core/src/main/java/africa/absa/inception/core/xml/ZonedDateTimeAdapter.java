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

package africa.absa.inception.core.xml;

import africa.absa.inception.core.util.ISO8601Util;
import java.time.ZonedDateTime;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * The <b>ZonedDateTimeAdapter</b> class implements a JAXB 2.0 adapter used to convert between
 * <b>String</b> and <b>ZonedDateTime</b> types. <br>
 * Can be used when customizing XML Schema to Java Representation Binding (XJC).
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ZonedDateTimeAdapter extends XmlAdapter<String, ZonedDateTime> {

  /**
   * Marshals the <b>java.time.ZonedDateTime</b> value as an ISO8601Util string.
   *
   * @param value the value to marshal
   * @return the <b>java.time.ZonedDateTime</b> value as an ISO8601Util string
   */
  @Override
  public String marshal(ZonedDateTime value) {
    if (value == null) {
      return null;
    }

    return ISO8601Util.fromZonedDateTime(value);
  }

  /**
   * Unmarshals the ISO8601Util string value as a <b>java.time.ZonedDateTime</b>.
   *
   * @param value the ISO8601Util string value
   * @return the ISO8601Util string value as a <b>java.time.ZonedDateTime</b>
   */
  @Override
  public ZonedDateTime unmarshal(String value) {
    if (value == null) {
      return null;
    }

    try {
      return ISO8601Util.toZonedDateTime(value);
    } catch (Throwable e) {
      throw new RuntimeException("Failed to parse the xs:dateTime value (" + value + ")");
    }
  }
}
