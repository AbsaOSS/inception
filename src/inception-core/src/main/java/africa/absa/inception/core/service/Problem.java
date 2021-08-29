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

package africa.absa.inception.core.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <b>Problem</b> annotation is applied to an exception thrown by a service to specify how the
 * exception should be returned as a RFC 7807 JSON Problem Details Object during an API invocation.
 *
 * @author Marcus Portmann
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Problem {
  /**
   * The HTTP status code generated by the origin server for an occurrence of the problem.
   *
   * @return the HTTP status code generated by the origin server for an occurrence of the problem
   */
  int status() default 500;

  /**
   * The short, human-readable summary of the problem type.
   *
   * @return the short, human-readable summary of the problem type
   */
  String title();

  /**
   * The URI reference that identifies the problem type.
   *
   * @return the URI reference that identifies the problem type
   */
  String type() default "about:blank";
}