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

import africa.absa.inception.core.exception.RichRuntimeException;
import org.xml.sax.SAXParseException;

/**
 * The <b>XmlParserException</b> exception is thrown to indicate an error condition when parsing an
 * XML file.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings("unused")
public class XmlParserException extends RichRuntimeException {

  private static final long serialVersionUID = 1000000;

  /** Constructs a new <b>XmlParserException</b> with <b>null</b> as its message. */
  public XmlParserException() {
    super();
  }

  /**
   * Constructs a new <b>XmlParserException</b> using the information contained in the specified
   * <b>SAXParseException</b>.
   *
   * @param cause the <b>SAXParseException</b> giving the cause of the exception
   */
  public XmlParserException(SAXParseException cause) {
    super(
        cause.getMessage()
            + " at line ("
            + cause.getLineNumber()
            + ") and column ("
            + (cause.getColumnNumber()
                + ") with SystemID ("
                + ((cause.getSystemId() != null) ? cause.getSystemId() : "UNKNOWN")
                + ") and PublicID ("
                + ((cause.getPublicId() != null) ? cause.getPublicId() : "UNKNOWN")
                + ")"));
  }

  /**
   * Constructs a new <b>XmlParserException</b> with the specified message.
   *
   * @param message The message saved for later retrieval by the <b>getMessage()</b> method.
   */
  public XmlParserException(String message) {
    super(message);
  }

  /**
   * Constructs a new <b>XmlParserException</b> with the specified cause and a message of
   * <b>(cause==null ? null : cause.toString())</b> (which typically contains the class and message
   * of cause).
   *
   * @param cause The cause saved for later retrieval by the <b>getCause()</b> method. (A
   *     <b>null</b> value is permitted if the cause is nonexistent or unknown)
   */
  public XmlParserException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new <b>XmlParserException</b> with the specified code and message.
   *
   * @param code the error code
   * @param message The message saved for later retrieval by the <b>getMessage()</b> method.
   */
  public XmlParserException(String code, String message) {
    super(code, message);
  }

  /**
   * Constructs a new <b>XmlParserException</b> with the specified message and cause.
   *
   * @param message The message saved for later retrieval by the <b>getMessage()</b> method.
   * @param cause The cause saved for later retrieval by the <b>getCause()</b> method. (A
   *     <b>null</b> value is permitted if the cause is nonexistent or unknown)
   */
  public XmlParserException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new <b>XmlParserException</b> with the specified code, message and cause.
   *
   * @param code the error code
   * @param message The message saved for later retrieval by the <b>getMessage()</b> method.
   * @param cause The cause saved for later retrieval by the <b>getCause()</b> method. (A
   *     <b>null</b> value is permitted if the cause is nonexistent or unknown)
   */
  public XmlParserException(String code, String message, Throwable cause) {
    super(code, message, cause);
  }
}
