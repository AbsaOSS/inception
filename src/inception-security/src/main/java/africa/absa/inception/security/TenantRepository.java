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
 * The <b>TenantRepository</b> interface declares the repository for the <b> Tenant</b> domain type.
 *
 * @author Marcus Portmann
 */
public interface TenantRepository extends JpaRepository<Tenant, UUID> {

  /**
   * Add the user directory to the tenant.
   *
   * @param tenantId the ID for the tenant
   * @param userDirectoryId the ID for the user directory
   */
  @Modifying
  @Query(
      value =
          "insert into security.user_directory_to_tenant_map(tenant_id, user_directory_id) "
              + "values (:tenantId, :userDirectoryId)",
      nativeQuery = true)
  void addUserDirectoryToTenant(
      @Param("tenantId") UUID tenantId, @Param("userDirectoryId") UUID userDirectoryId);

  /**
   * Delete the tenant.
   *
   * @param tenantId the ID for the tenant
   */
  @Modifying
  @Query("delete from Tenant u where u.id = :tenantId")
  void deleteById(@Param("tenantId") UUID tenantId);

  /**
   * Check whether the tenant with the specified name exists.
   *
   * @param name the name of the tenant
   * @return <b>true</b> if a tenant with the specified name exists or <b>false</b> otherwise
   */
  boolean existsByNameIgnoreCase(String name);

  /**
   * Retrieve the tenants.
   *
   * @param pageable the pagination information
   * @return the tenants
   */
  Page<Tenant> findAll(Pageable pageable);

  /**
   * Retrieve the tenants for the user directory.
   *
   * @param userDirectoryId the ID for the user directory
   * @return the tenants for the user directory
   */
  @Query("select o from Tenant o join o.userDirectories as ud where ud.id = :userDirectoryId")
  List<Tenant> findAllByUserDirectoryId(@Param("userDirectoryId") UUID userDirectoryId);

  /**
   * Retrieve the filtered tenants.
   *
   * @param filter the filter to apply to the tenants
   * @param pageable the pagination information
   * @return the filtered tenants
   */
  @Query("select t from Tenant t where (lower(t.name) like lower(:filter))")
  Page<Tenant> findFiltered(String filter, Pageable pageable);

  /**
   * Retrieve the name of the tenant.
   *
   * @param tenantId the ID for the tenant
   * @return an Optional containing the name of the tenant or an empty Optional if the tenant could
   *     not be found
   */
  @Query("select o.name from Tenant o where o.id = :tenantId")
  Optional<String> getNameById(@Param("tenantId") UUID tenantId);

  /**
   * Retrieve the IDs for the user directories for the tenant.
   *
   * @param tenantId the ID for the tenant
   * @return the IDs for the user directories for the tenant
   */
  @Query("select ud.id from UserDirectory ud join ud.tenants as o where o.id = :tenantId")
  List<UUID> getUserDirectoryIdsById(@Param("tenantId") UUID tenantId);

  /**
   * Remove the user directory from the tenant
   *
   * @param tenantId the ID for the tenant
   * @param userDirectoryId the ID for the user directory
   */
  @Modifying
  @Query(
      value =
          "delete from security.user_directory_to_tenant_map "
              + "where tenant_id=:tenantId and user_directory_id = :userDirectoryId",
      nativeQuery = true)
  void removeUserDirectoryFromTenant(
      @Param("tenantId") UUID tenantId, @Param("userDirectoryId") UUID userDirectoryId);

  /**
   * Check whether the user directory to tenant mapping exists.
   *
   * @param tenantId the ID for the tenant
   * @param userDirectoryId the ID for the user directory
   * @return <b>true</b> if the user directory to tenant mapping exists or <b>false</b> otherwise
   */
  @Query(
      value =
          "select (count(user_directory_id) > 0) from security.user_directory_to_tenant_map where "
              + "tenant_id = :tenantId and user_directory_id = :userDirectoryId",
      nativeQuery = true)
  boolean userDirectoryToTenantMappingExists(
      @Param("tenantId") UUID tenantId, @Param("userDirectoryId") UUID userDirectoryId);
}
