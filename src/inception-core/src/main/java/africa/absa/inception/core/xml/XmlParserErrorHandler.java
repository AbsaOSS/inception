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

import org.slf4j.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * The <b>XmlParserErrorHandler</b> class handles warnings and errors that arise while parsing an
 * XML document.
 *
 * @author Marcus Portmann
 */
public class XmlParserErrorHandler implements ErrorHandler {

  private Logger logger = null;

  /** Constructs a new <b>XmlParserErrorHandler</b>. */
  public XmlParserErrorHandler() {}

  /**
   * Constructs a new <b>XmlParserErrorHandler</b> using the specified <b>Logger</b>.
   *
   * @param logger the logger to use to log errors and warnings
   */
  @SuppressWarnings("unused")
  public XmlParserErrorHandler(Logger logger) {
    this.logger = logger;
  }

  /**
   * Process an error that occurred while parsing the XML.
   *
   * @param e the exception containing the error details
   */
  public void error(SAXParseException e) {
    if (logger != null) {
      logger.error(buildLogMessage(e));
    }

    throw new XmlParserException(e);
  }

  /**
   * Process a fatal error that occurred while parsing the XML.
   *
   * @param e the exception containing the fatal error details
   */
  public void fatalError(SAXParseException e) {
    if (logger != null) {
      logger.error(buildLogMessage(e));
    }

    throw new XmlParserException(e);
  }

  /**
   * Process a warning that occurred while parsing the XML.
   *
   * @param e the exception containing the warning details
   */
  public void warning(SAXParseException e) {
    if (logger != null) {
      logger.warn(buildLogMessage(e));
    }
  }

  private String buildLogMessage(SAXParseException e) {
    StringBuilder buffer = new StringBuilder();

    buffer.append(e.getMessage());
    buffer.append("\n\tat line (");
    buffer.append(e.getLineNumber());
    buffer.append(") and column (");
    buffer.append(e.getColumnNumber());
    buffer.append(")\n");

    if ((e.getSystemId() != null) && (e.getPublicId() != null)) {
      buffer.append("\twith SystemID (");
      buffer.append(e.getSystemId());
      buffer.append(") and PublicID (");
      buffer.append(e.getPublicId());
      buffer.append(")");
    } else if (e.getSystemId() != null) {
      buffer.append("\twith SystemID (");
      buffer.append(e.getSystemId());
      buffer.append(")");
    } else if (e.getPublicId() != null) {
      buffer.append("\twith PublicID (");
      buffer.append(e.getPublicId());
      buffer.append(")");
    }

    return buffer.toString();
  }
}
