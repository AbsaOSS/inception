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

package africa.absa.inception.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

/**
 * The <b>StringToUserStatusConverter</b> class implements the Spring converter that converts a
 * <b>String</b> type into a <b>UserStatus</b> type.
 *
 * @author Marcus Portmann
 */
@Component
@ReadingConverter
public class StringToUserStatusConverter implements Converter<String, UserStatus> {

  /** Constructs a new <b>StringToUserStatusConverter</b>. */
  public StringToUserStatusConverter() {}

  @Override
  public UserStatus convert(String source) {
    return UserStatus.fromCode(source);
  }
}
