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
import java.util.Objects;

/**
 * The <b>CodeId</b> class implements the ID class for the <b>Code</b> class.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings({"UnusedDeclaration"})
public class CodeId implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The ID for the code category the code is associated with. */
  private String codeCategoryId;

  /** The ID for the code. */
  private String id;

  /** Constructs a new <b>CodeId</b>. */
  public CodeId() {}

  /**
   * Constructs a new <b>CodeId</b>.
   *
   * @param codeCategoryId the ID for the code category the code is associated with
   * @param id the ID for the code
   */
  public CodeId(String codeCategoryId, String id) {
    this.codeCategoryId = codeCategoryId;
    this.id = id;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param object the reference object with which to compare
   * @return <b>true</b> if this object is the same as the object argument otherwise <b>false</b>
   */
  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }

    if (object == null) {
      return false;
    }

    if (getClass() != object.getClass()) {
      return false;
    }

    CodeId other = (CodeId) object;

    return Objects.equals(codeCategoryId, other.codeCategoryId) && Objects.equals(id, other.id);
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for the object
   */
  @Override
  public int hashCode() {
    return ((codeCategoryId == null) ? 0 : codeCategoryId.hashCode())
        + ((id == null) ? 0 : id.hashCode());
  }
}
