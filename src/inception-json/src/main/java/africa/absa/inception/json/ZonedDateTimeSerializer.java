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

package africa.absa.inception.json;

import africa.absa.inception.core.util.ISO8601Util;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.ZonedDateTime;

/**
 * The <b>ZonedDateTimeDeserializer</b> class implements the Jackson serializer for the <b>
 * ZonedDateTime</b> type.
 *
 * @author Marcus Portmann
 */
public class ZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime> {

  @Override
  public void serialize(
      ZonedDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializers)
      throws IOException {
    jsonGenerator.writeString(ISO8601Util.fromZonedDateTime(localDateTime));
  }
}
