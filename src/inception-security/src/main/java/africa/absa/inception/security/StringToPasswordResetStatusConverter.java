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
 * The <b>StringToPasswordResetStatusConverter</b> class implements the Spring converter that
 * converts a <b>String</b> type into a <b>PasswordResetStatus</b> type.
 *
 * @author Marcus Portmann
 */
@Component
@ReadingConverter
public class StringToPasswordResetStatusConverter
    implements Converter<String, PasswordResetStatus> {

  /** Constructs a new <b>StringToPasswordResetStatusConverter</b>. */
  public StringToPasswordResetStatusConverter() {}

  @Override
  public PasswordResetStatus convert(String source) {
    return PasswordResetStatus.fromCode(source);
  }
}
