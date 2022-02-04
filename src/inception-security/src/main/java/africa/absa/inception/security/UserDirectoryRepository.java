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

/**
 * The <b>UserDirectoryRepository</b> interface declares the repository for the <b>
 * UserDirectory</b> domain type.
 *
 * @author Marcus Portmann
 */
public interface UserDirectoryRepository extends JpaRepository<UserDirectory, UUID> {

  /**
   * Delete the user directory.
   *
   * @param userDirectoryId the ID for the user directory
   */
  @Modifying
  @Query("delete from UserDirectory ud where ud.id = :userDirectoryId")
  void deleteById(@Param("userDirectoryId") UUID userDirectoryId);

  /**
   * Check whether the user directory with the specified name exists.
   *
   * @param name the name of the user directory
   * @return <b>true</b> if a user directory with the specified name exists or <b>false</b>
   *     otherwise
   */
  boolean existsByNameIgnoreCase(String name);

  /**
   * Retrieve the user directories.
   *
   * @param pageable the pagination information
   * @return the user directories
   */
  Page<UserDirectory> findAll(Pageable pageable);

  /**
   * Retrieve the user directories for the tenant.
   *
   * @param tenantId the ID for the tenant
   * @return the user directories for the tenant
   */
  @Query("select ud from UserDirectory ud join ud.tenants as o where o.id = :tenantId")
  List<UserDirectory> findAllByTenantId(@Param("tenantId") UUID tenantId);

  /**
   * Retrieve the filtered user directories.
   *
   * @param filter the filter to apply to the user directories
   * @param pageable the pagination information
   * @return the filtered user directories
   */
  @Query("select ud from UserDirectory ud where (lower(ud.name) like lower(:filter))")
  Page<UserDirectory> findFiltered(String filter, Pageable pageable);

  /**
   * Retrieve the name of the user directory.
   *
   * @param userDirectoryId the ID for the user directory
   * @return an Optional containing the name of the user directory or an empty Optional if the user
   *     directory could not be found
   */
  @Query("select ud.name from UserDirectory ud where ud.id = :userDirectoryId")
  Optional<String> getNameById(@Param("userDirectoryId") UUID userDirectoryId);

  /**
   * Retrieve the IDs for the tenants for the user directory.
   *
   * @param userDirectoryId the ID for the user directory
   * @return the IDs for the tenants for the user directory
   */
  @Query("select o.id from Tenant o join o.userDirectories as ud where ud.id = :userDirectoryId")
  List<UUID> getTenantIdsById(@Param("userDirectoryId") UUID userDirectoryId);

  /**
   * Retrieve the type for the user directory.
   *
   * @param userDirectoryId the ID for the user directory
   * @return an Optional containing the type for the user directory or an empty Optional if the user
   *     directory could not be found
   */
  @Query("select ud.type from UserDirectory ud where ud.id = :userDirectoryId")
  Optional<String> getTypeForUserDirectoryById(@Param("userDirectoryId") UUID userDirectoryId);
}
