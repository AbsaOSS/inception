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

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * The <b>PasswordResetRepository</b> interface declares the repository for the <b>
 * PasswordReset</b> domain type.
 *
 * @author Marcus Portmann
 */
public interface PasswordResetRepository extends JpaRepository<PasswordReset, PasswordResetId> {

  /**
   * Expire the password resets.
   *
   * @param currentTimestamp the current date and time
   * @param requestedBefore the date and time to expire password resets after
   */
  @Modifying
  @Query(
      "update PasswordReset pr set pr.expired = :currentTimestamp, pr.status = 3 "
          + "where pr.status = 1 and pr.requested < :requestedBefore")
  void expirePasswordResets(
      @Param("currentTimestamp") LocalDateTime currentTimestamp,
      @Param("requestedBefore") LocalDateTime requestedBefore);

  /**
   * Retrieve the password reset with the specified status for the user.
   *
   * @param username the username for the user
   * @param status the password reset status
   * @return the password resets with the specified status for the user
   */
  List<PasswordReset> findAllByUsernameAndStatus(String username, PasswordResetStatus status);
}
