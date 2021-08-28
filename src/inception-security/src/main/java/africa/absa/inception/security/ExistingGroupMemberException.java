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

import africa.absa.inception.core.service.Problem;
import africa.absa.inception.core.service.ServiceException;

/**
 * A <b>ExistingGroupMemberException</b> is thrown to indicate that a security operation failed as a
 * result of an existing group member.
 *
 * <p>This is a checked exception to prevent the automatic rollback of the current transaction.
 *
 * @author Marcus Portmann
 */
@Problem(
    type = "http://inception.absa.africa/problems/security/existing-group-member",
    title = "The group member already exists.",
    status = 409)
public class ExistingGroupMemberException extends ServiceException {

  private static final long serialVersionUID = 1000000;

  /**
   * Constructs a new <b>ExistingGroupMemberException</b>.
   *
   * @param memberType the group member type
   * @param memberName the group member name
   */
  public ExistingGroupMemberException(GroupMemberType memberType, String memberName) {
    super(
        "The group member with type ("
            + memberType.description()
            + ") and name ("
            + memberName
            + ") already exists");
  }
}
