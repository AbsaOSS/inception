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

package africa.absa.inception.core.converters;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * The <b>LocalDateTimeToDateConverter</b> class implements the Spring converter that converts a
 * <b>LocalDateTime</b> type into a <b>Date</b> type.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings("unused")
@Component
public final class LocalDateTimeToDateConverter implements Converter<LocalDateTime, Date> {

  /** Constructs a new <b>LocalDateTimeToDateConverter</b>. */
  public LocalDateTimeToDateConverter() {}

  @Override
  public Date convert(LocalDateTime source) {
    return Date.from(source.toInstant(ZoneOffset.UTC));
  }
}
