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

package africa.absa.inception.jpa;

import java.io.Serializable;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentityGenerator;

/**
 * The <b>AssignedIdentityGenerator</b> implements a Hibernate generator that allows a value to be
 * specified in place of an automatically generated value for an identity column in the database.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings({"unused"})
public class AssignedIdentityGenerator extends IdentityGenerator {

  /**
   * Generate a new identifier.
   *
   * @param session the session from which the request originates
   * @param object the entity or collection for which the ID is being generated
   * @return a new identifier
   */
  @Override
  public Serializable generate(SharedSessionContractImplementor session, Object object) {
    if (object instanceof Identifiable) {
      Identifiable identifiable = (Identifiable) object;
      Serializable id = identifiable.getId();
      if (id != null) {
        return id;
      }
    }

    return super.generate(session, object);
  }
}
