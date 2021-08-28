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

package africa.absa.inception.codes;

import java.io.Serializable;

/**
 * The <b>CodeProviderConfig</b> class stores the configuration information for a code provider.
 * This is the configuration read from the META-INF/code-providers.xml configuration files on the
 * classpath.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings("WeakerAccess")
public class CodeProviderConfig implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The fully qualified name of the class that implements the code provider. */
  private final String className;

  /** The name of the code provider. */
  private final String name;

  /**
   * Constructs a new <b>CodeProviderConfig</b>.
   *
   * @param name the name of the code provider
   * @param className fully qualified name of the class that implements the code provider
   */
  CodeProviderConfig(String name, String className) {
    this.name = name;
    this.className = className;
  }

  /**
   * Return the fully qualified name of the class that implements the code provider.
   *
   * @return the fully qualified name of the class that implements the code provider
   */
  public String getClassName() {
    return className;
  }

  /**
   * Returns the name of the code provider.
   *
   * @return the name of the code provider
   */
  public String getName() {
    return name;
  }
}
