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

  long countByNameContainingIgnoreCase(String name);

  @Modifying
  @Query("delete from UserDirectory ud where ud.id = :userDirectoryId")
  void deleteById(@Param("userDirectoryId") UUID userDirectoryId);

  boolean existsByNameIgnoreCase(String name);

  Page<UserDirectory> findAllByOrderByNameAsc(Pageable pageable);

  Page<UserDirectory> findAllByOrderByNameDesc(Pageable pageable);

  @Query("select ud from UserDirectory ud join ud.tenants as o where o.id = :tenantId")
  List<UserDirectory> findAllByTenantId(@Param("tenantId") UUID tenantId);

  Page<UserDirectory> findByNameContainingIgnoreCaseOrderByNameAsc(String name, Pageable pageable);

  Page<UserDirectory> findByNameContainingIgnoreCaseOrderByNameDesc(String name, Pageable pageable);

  @Query("select ud.name from UserDirectory ud where ud.id = :userDirectoryId")
  Optional<String> getNameById(@Param("userDirectoryId") UUID userDirectoryId);

  @Query("select o.id from Tenant o join o.userDirectories as ud where ud.id = :userDirectoryId")
  List<UUID> getTenantIdsById(@Param("userDirectoryId") UUID userDirectoryId);

  @Query("select ud.type from UserDirectory ud where ud.id = :userDirectoryId")
  Optional<String> getTypeForUserDirectoryById(@Param("userDirectoryId") UUID userDirectoryId);
}
