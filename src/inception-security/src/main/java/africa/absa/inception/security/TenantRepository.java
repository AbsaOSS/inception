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

  @Modifying
  @Query(
      value =
          "insert into security.user_directory_to_tenant_map(tenant_id, user_directory_id) "
              + "values (:tenantId, :userDirectoryId)",
      nativeQuery = true)
  void addUserDirectoryToTenant(
      @Param("tenantId") UUID tenantId, @Param("userDirectoryId") UUID userDirectoryId);

  long countByNameContainingIgnoreCase(String name);

  @Query(
      value =
          "select count(user_directory_id) from security.user_directory_to_tenant_map where "
              + "tenant_id = :tenantId and user_directory_id = :userDirectoryId",
      nativeQuery = true)
  long countTenantUserDirectory(
      @Param("tenantId") UUID tenantId, @Param("userDirectoryId") UUID userDirectoryId);

  @Modifying
  @Query("delete from Tenant u where u.id = :tenantId")
  void deleteById(@Param("tenantId") UUID tenantId);

  boolean existsByNameIgnoreCase(String name);

  Page<Tenant> findAllByOrderByNameAsc(Pageable pageable);

  Page<Tenant> findAllByOrderByNameDesc(Pageable pageable);

  @Query("select o from Tenant o join o.userDirectories as ud where ud.id = :userDirectoryId")
  List<Tenant> findAllByUserDirectoryId(@Param("userDirectoryId") UUID userDirectoryId);

  Page<Tenant> findByNameContainingIgnoreCaseOrderByNameAsc(String name, Pageable pageable);

  Page<Tenant> findByNameContainingIgnoreCaseOrderByNameDesc(String name, Pageable pageable);

  @Query("select o.name from Tenant o where o.id = :tenantId")
  Optional<String> getNameById(@Param("tenantId") UUID tenantId);

  @Query("select ud.id from UserDirectory ud join ud.tenants as o where o.id = :tenantId")
  List<UUID> getUserDirectoryIdsById(@Param("tenantId") UUID tenantId);

  @Modifying
  @Query(
      value =
          "delete from security.user_directory_to_tenant_map "
              + "where tenant_id=:tenantId and user_directory_id = :userDirectoryId",
      nativeQuery = true)
  int removeUserDirectoryFromTenant(
      @Param("tenantId") UUID tenantId, @Param("userDirectoryId") UUID userDirectoryId);
}
