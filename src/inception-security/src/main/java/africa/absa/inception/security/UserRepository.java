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
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;

/**
 * The <b>UserRepository</b> interface declares the repository for the <b>User</b> domain type.
 *
 * @author Marcus Portmann
 */
public interface UserRepository extends JpaRepository<User, UUID>, QueryByExampleExecutor<User> {

  /**
   * Change the password for the user.
   *
   * @param userId the ID for the user
   * @param password the password
   * @param passwordAttempts the password attempts
   * @param passwordExpiry the password expiry
   */
  @Modifying
  @Query(
      "update User u set u.password = :password, u.passwordAttempts = :passwordAttempts, "
          + "u.passwordExpiry = :passwordExpiry where u.id = :userId")
  void changePassword(
      @Param("userId") UUID userId,
      @Param("password") String password,
      @Param("passwordAttempts") int passwordAttempts,
      @Param("passwordExpiry") Optional<LocalDateTime> passwordExpiry);

  // long countByUserDirectoryId(UUID userDirectoryId);

  /**
   * Delete the user.
   *
   * @param userId the ID for the user
   */
  @Modifying
  @Query("delete from User u where u.id = :userId")
  void deleteById(@Param("userId") UUID userId);

  /**
   * Check whether the user exists.
   *
   * @param userDirectoryId the ID for the user directory
   * @param username the username
   * @return <b>true</b> if the user exists or <b>false</b> otherwise
   */
  boolean existsByUserDirectoryIdAndUsernameIgnoreCase(UUID userDirectoryId, String username);

  /**
   * Retrieve the users for the user directory.
   *
   * @param userDirectoryId the ID for the user directory
   * @return the users for the user directory
   */
  List<User> findByUserDirectoryId(UUID userDirectoryId);

  /**
   * Retrieve the users for the user directory.
   *
   * @param userDirectoryId the ID for the user directory
   * @param pageable the pagination information
   * @return the users for the user directory
   */
  Page<User> findByUserDirectoryId(UUID userDirectoryId, Pageable pageable);

  /**
   * Retrieve the user with the specified username for the user directory.
   *
   * @param userDirectoryId the ID for the user directory
   * @param username the username
   * @return an Optional containing the user or an empty Optional if the user could not be found
   */
  Optional<User> findByUserDirectoryIdAndUsernameIgnoreCase(UUID userDirectoryId, String username);

  /**
   * Retrieve the filtered users for the user directory.
   *
   * @param userDirectoryId the ID for the user directory
   * @param filter the filter to apply to the users
   * @param pageable the pagination information
   * @return the filtered users for the user directory
   */
  @Query(
      "select u from User u where ((lower(u.username) like lower(:filter)) or "
          + "(lower(u.name) like lower(:filter))) and u.userDirectoryId = :userDirectoryId")
  Page<User> findFiltered(
      @Param("userDirectoryId") UUID userDirectoryId,
      @Param("filter") String filter,
      Pageable pageable);

  /**
   * Retrieve the function codes for the user.
   *
   * @param userId the ID for the user
   * @return the function codes for the user
   */
  @Query(
      "select f.code from User u join u.groups as g join g.roles as r join r.functions as f "
          + "where u.id = :userId")
  List<String> getFunctionCodesByUserId(@Param("userId") UUID userId);

  /**
   * Retrieve the group names for the user
   *
   * @param userId the ID for the user
   * @return the group names for the user
   */
  @Query("select g.name from User u join u.groups as g where u.id = :userId")
  List<String> getGroupNamesByUserId(@Param("userId") UUID userId);

  /**
   * Retrieve the groups for the user.
   *
   * @param userId the ID for the user
   * @return the groups for the user
   */
  @Query("select g from User u join u.groups as g where u.id = :userId")
  List<Group> getGroupsByUserId(@Param("userId") UUID userId);

  /**
   * Retrieve the ID for the user with the specified username for the user directory.
   *
   * @param userDirectoryId the ID for the user directory
   * @param username the username
   * @return an Optional containing the ID for the user with the specified username for the user
   *     directory or an empty Optional if the user could not be found
   */
  @Query(
      "select u.id from User u where u.userDirectoryId = :userDirectoryId and lower(u.username) "
          + "like lower(:username)")
  Optional<UUID> getIdByUserDirectoryIdAndUsernameIgnoreCase(
      @Param("userDirectoryId") UUID userDirectoryId, @Param("username") String username);

  /**
   * Retrieve the name for the user with the specified username for the user directory.
   *
   * @param userDirectoryId the ID for the user directory
   * @param username the username
   * @return an Optional containing the name for the user with the specified username for the user
   *     directory or an empty Optional if the user could not be found
   */
  @Query(
      "select u.name from User u where ((lower(u.username) = lower(:username)) "
          + "and u.userDirectoryId = :userDirectoryId)")
  Optional<String> getNameByUserDirectoryIdAndUsernameIgnoreCase(
      UUID userDirectoryId, String username);

  /**
   * Retrieve the password history for the user.
   *
   * @param userId the ID for the user
   * @param after the date and time after which password history entries will be retrieved
   * @return the password history for the user
   */
  @Query(
      value =
          "select password from security.users_password_history "
              + "where user_id = :userId and changed > :after",
      nativeQuery = true)
  List<String> getPasswordHistory(
      @Param("userId") UUID userId, @Param("after") LocalDateTime after);

  /**
   * Retrieve the role codes for the user.
   *
   * @param userId the ID for the user
   * @return the role codes for the user
   */
  @Query("select r.code from User u join u.groups as g join g.roles as r where u.id = :userId")
  List<String> getRoleCodesByUserId(@Param("userId") UUID userId);

  /**
   * Retrieve the ID for the user directory for the user with the specified username.
   *
   * @param username the username
   * @return an Optional containing the ID for the user directory for the user with the specified
   *     username or an empty Optional if the user could not be found
   */
  @Query("select u.userDirectoryId from User u where lower(u.username) = lower(:username)")
  Optional<UUID> getUserDirectoryIdByUsernameIgnoreCase(@Param("username") String username);

  /**
   * Increment the password attempts for the user.
   *
   * @param userId the ID for the user
   */
  @Modifying
  @Query("update User u set u.passwordAttempts = u.passwordAttempts + 1 where u.id = :userId")
  void incrementPasswordAttempts(@Param("userId") UUID userId);

  /**
   * Check whether the user is a member of the group.
   *
   * @param userId the ID for the user
   * @param groupId the ID for the group
   * @return <b>true</b> if the user is a member of the group or <b>false</b> otherwise
   */
  @Query(
      "select case when (count(u.id) > 0) then true else false end from User u join u.groups "
          + "as g where u.id = :userId and g.id = :groupId")
  boolean isUserInGroup(@Param("userId") UUID userId, @Param("groupId") UUID groupId);

  /**
   * Reset the password for the user.
   *
   * @param userId the ID for the user
   * @param password the password
   * @param passwordExpiry the password expiry
   */
  @Modifying
  @Query(
      "update User u set u.password = :password, u.passwordAttempts = 0, "
          + "u.passwordExpiry = :passwordExpiry where u.id = :userId")
  void resetPassword(
      @Param("userId") UUID userId,
      @Param("password") String password,
      @Param("passwordExpiry") LocalDateTime passwordExpiry);

  /**
   * Reset the password history for the user.
   *
   * @param userId the ID for the user
   */
  @Modifying
  @Query(
      value = "delete from security.users_password_history where user_id = :userId",
      nativeQuery = true)
  void resetPasswordHistory(@Param("userId") UUID userId);

  /**
   * Save the password in the password history for the user.
   *
   * @param userId the ID for the user
   * @param password the password
   */
  @Modifying
  @Query(
      value =
          "insert into security.users_password_history(user_id, changed, password) "
              + "values (:userId, current_timestamp, :password)",
      nativeQuery = true)
  void savePasswordInPasswordHistory(
      @Param("userId") UUID userId, @Param("password") String password);
}
