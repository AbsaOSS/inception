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

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * The <b>DateTimeModule</b> implements the custom Jackson module that registers the serializer and
 * deserializer extensions that support the Java 8 Date/Time API.
 *
 * @author Marcus Portmann
 */
public class DateTimeModule extends SimpleModule {

  /** Constructs a new <b>DateTimeModule</b>. */
  public DateTimeModule() {
    super("DateTimeModule", new Version(1, 0, 0, null, null, null));

    addSerializer(Date.class, new DateSerializer());
    addDeserializer(Date.class, new DateDeserializer());
    addSerializer(LocalDate.class, new LocalDateSerializer());
    addDeserializer(LocalDate.class, new LocalDateDeserializer());
    addSerializer(LocalTime.class, new LocalTimeSerializer());
    addDeserializer(LocalTime.class, new LocalTimeDeserializer());
    addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
    addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
    addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer());
    addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeserializer());
  }
}
