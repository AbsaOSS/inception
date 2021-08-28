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

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * The <b>GroupRepository</b> interface declares the repository for the <b>Group</b> domain type.
 *
 * @author Marcus Portmann
 */
public interface GroupRepository extends JpaRepository<Group, UUID> {

  @Modifying
  @Query(
      value =
          "insert into security.role_to_group_map(role_code, group_id) "
              + "values (:roleCode, :groupId)",
      nativeQuery = true)
  void addRoleToGroup(@Param("groupId") UUID groupId, @Param("roleCode") String roleCode);

  @Modifying
  @Query(
      value =
          "insert into security.user_to_group_map(user_id, group_id) "
              + "values (:userId, :groupId)",
      nativeQuery = true)
  void addUserToGroup(@Param("groupId") UUID groupId, @Param("userId") UUID userId);

  long countByUserDirectoryId(UUID userDirectoryId);

  @Query(
      "select count(g.id) from Group g where (lower(g.name) like lower(:filter)) and "
          + "g.userDirectoryId = :userDirectoryId")
  long countFiltered(
      @Param("userDirectoryId") UUID userDirectoryId, @Param("filter") String filter);

  @Query(
      "select count(u.id) from Group g join g.users as u where g.userDirectoryId = "
          + ":userDirectoryId and g.id = :groupId and (lower(u.username) like lower(:filter))")
  long countFilteredUsernamesForGroup(
      @Param("userDirectoryId") UUID userDirectoryId,
      @Param("groupId") UUID groupId,
      @Param("filter") String filter);

  @Query(
      value =
          "select count(role_code) from security.role_to_group_map where "
              + "role_code = :roleCode and group_id = :groupId",
      nativeQuery = true)
  long countGroupRole(@Param("groupId") UUID groupId, @Param("roleCode") String roleCode);

  @Query(
      "select count(u.id) from Group g join g.users as u "
          + "where g.userDirectoryId = :userDirectoryId and g.id = :groupId")
  long countUsernamesForGroup(
      @Param("userDirectoryId") UUID userDirectoryId, @Param("groupId") UUID groupId);

  @Query("select count(u.id) from Group g join g.users as u where g.id = :groupId")
  long countUsersById(@Param("groupId") UUID groupId);

  @Modifying
  @Query("delete from Group g where g.id = :groupId")
  void deleteById(@Param("groupId") UUID groupId);

  @Transactional
  boolean existsByUserDirectoryIdAndNameIgnoreCase(UUID userDirectoryId, String name);

  List<Group> findByUserDirectoryId(UUID userDirectoryId);

  Page<Group> findByUserDirectoryId(UUID userDirectoryId, Pageable pageable);

  Optional<Group> findByUserDirectoryIdAndNameIgnoreCase(UUID userDirectoryId, String name);

  @Query(
      "select g from Group g where (lower(g.name) like lower(:filter)) and "
          + "g.userDirectoryId = :userDirectoryId")
  Page<Group> findFiltered(
      @Param("userDirectoryId") UUID userDirectoryId,
      @Param("filter") String filter,
      Pageable pageable);

  @Query(
      "select u.username from Group g join g.users as u where g.userDirectoryId = "
          + ":userDirectoryId and g.id = :groupId and (lower(u.username) like lower(:filter))")
  Page<String> getFilteredUsernamesForGroup(
      @Param("userDirectoryId") UUID userDirectoryId,
      @Param("groupId") UUID groupId,
      @Param("filter") String filter,
      Pageable pageable);

  @Query(
      "select distinct f.code from Group g join g.roles as r join r.functions as f where g.userDirectoryId = :userDirectoryId and lower(g.name) in :groupNames")
  List<String> getFunctionCodesByUserDirectoryIdAndGroupNames(
      @Param("userDirectoryId") UUID userDirectoryId, @Param("groupNames") List<String> groupNames);

  @Query(
      "select g.id from Group g where g.userDirectoryId = :userDirectoryId and "
          + "lower(g.name) like lower(:name)")
  Optional<UUID> getIdByUserDirectoryIdAndNameIgnoreCase(
      @Param("userDirectoryId") UUID userDirectoryId, @Param("name") String name);

  @Query("select g.name from Group g where g.userDirectoryId = :userDirectoryId")
  List<String> getNamesByUserDirectoryId(@Param("userDirectoryId") UUID userDirectoryId);

  @Query("select r.code from Group g join g.roles as r where g.id = :groupId")
  List<String> getRoleCodesByGroupId(@Param("groupId") UUID groupId);

  @Query(
      "select distinct r.code from Group g join g.roles as r where g.userDirectoryId = :userDirectoryId and lower(g.name) in :groupNames")
  List<String> getRoleCodesByUserDirectoryIdAndGroupNames(
      @Param("userDirectoryId") UUID userDirectoryId, @Param("groupNames") List<String> groupNames);

  @Query("select r from Group g join g.roles as r where g.id = :groupId")
  List<Role> getRolesByGroupId(@Param("groupId") UUID groupId);

  @Query(
      "select u.username from Group g join g.users as u "
          + "where g.userDirectoryId = :userDirectoryId and g.id = :groupId")
  List<String> getUsernamesForGroup(
      @Param("userDirectoryId") UUID userDirectoryId, @Param("groupId") UUID groupId);

  @Query(
      "select u.username from Group g join g.users as u "
          + "where g.userDirectoryId = :userDirectoryId and g.id = :groupId")
  Page<String> getUsernamesForGroup(
      @Param("userDirectoryId") UUID userDirectoryId,
      @Param("groupId") UUID groupId,
      Pageable pageable);

  @Modifying
  @Query(
      value =
          "delete from security.role_to_group_map "
              + "where group_id=:groupId and role_code = :roleCode",
      nativeQuery = true)
  int removeRoleFromGroup(@Param("groupId") UUID groupId, @Param("roleCode") String roleCode);

  @Modifying
  @Query(
      value =
          "delete from security.user_to_group_map "
              + "where group_id=:groupId and user_id = :userId",
      nativeQuery = true)
  void removeUserFromGroup(@Param("groupId") UUID groupId, @Param("userId") UUID userId);
}
