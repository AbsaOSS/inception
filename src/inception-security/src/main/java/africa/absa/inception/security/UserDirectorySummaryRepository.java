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
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * The <b>UserDirectorySummaryRepository</b> interface declares the repository for the <b>
 * UserDirectorySummary</b> domain type.
 *
 * @author Marcus Portmann
 */
public interface UserDirectorySummaryRepository extends JpaRepository<UserDirectorySummary, UUID> {

  Page<UserDirectorySummary> findAllByOrderByNameAsc(Pageable pageable);

  Page<UserDirectorySummary> findAllByOrderByNameDesc(Pageable pageable);

  @Query("select uds from UserDirectorySummary uds join uds.tenants as o where o.id = :tenantId")
  List<UserDirectorySummary> findAllByTenantId(@Param("tenantId") UUID tenantId);

  Page<UserDirectorySummary> findByNameContainingIgnoreCaseOrderByNameAsc(
      String name, Pageable pageable);

  Page<UserDirectorySummary> findByNameContainingIgnoreCaseOrderByNameDesc(
      String name, Pageable pageable);
}
