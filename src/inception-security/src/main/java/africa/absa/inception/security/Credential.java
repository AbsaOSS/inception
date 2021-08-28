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

/**
 * The <b>Credential</b> interface represents the abstract notion of a credential, which is a piece
 * of security information that can be used to confirm the identity of an entity, such as an
 * individual, a corporation, a login id, etc.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings("unused")
public interface Credential {

  /**
   * The equals method checks if the specified object is the same credential as this object.
   *
   * @param another the credential to compare with
   * @return true if the object passed in matches the credential represented by the implementation
   *     of this interface
   */
  @Override
  boolean equals(Object another);

  /**
   * The hashCode method returns an integer hash code to represent this credential. It can be used
   * to test for non-equality, or as an index key in a hash table.
   *
   * @return an integer hash code representing the credential
   */
  @Override
  int hashCode();

  /**
   * Returns a string representation of this credential.
   *
   * @return a string representation of this credential
   */
  @Override
  String toString();
}
