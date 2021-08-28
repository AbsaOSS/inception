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

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

/**
 * The <b>JobStatusToStringConverter</b> class implements the Spring converter that converts a
 * <b>JobStatus</b> type into a <b>String</b> type.
 *
 * @author Marcus Portmann
 */
@Component
@WritingConverter
public class JobStatusToStringConverter implements Converter<JobStatus, String> {

  /** Constructs a new <b>JobStatusToStringConverter</b>. */
  public JobStatusToStringConverter() {}

  @Override
  public String convert(JobStatus source) {
    return source.code();
  }
}
