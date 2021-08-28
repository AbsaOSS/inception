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

package africa.absa.inception.jta.util;

import javax.transaction.xa.XAException;

/**
 * The <b>XAExceptionUtil</b> class provides a helper class for handling XAExceptions.
 *
 * @author Marcus Portmann
 */
public class XAExceptionUtil {

  /**
   * Constructs a new <b>XAException</b> object.
   *
   * @param errorCode the error code
   * @param message the message
   * @param cause the cause
   * @return the XAException object
   */
  public static XAException xaException(int errorCode, String message, Throwable cause) {
    XAException xaException = xaException(errorCode, message);
    xaException.initCause(cause);
    return xaException;
  }

  /**
   * Constructs a new <b>XAException</b> object.
   *
   * @param errorCode the error code
   * @param message the message
   * @return the XAException object
   */
  public static XAException xaException(int errorCode, String message) {
    XAException xaException = new XAException(message);
    xaException.errorCode = errorCode;
    return xaException;
  }
}
