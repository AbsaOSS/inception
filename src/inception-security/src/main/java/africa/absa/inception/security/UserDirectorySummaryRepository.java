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

  /**
   * Retrieve the user directory summaries.
   *
   * @param pageable the pagination information
   * @return the user directory summaries
   */
  Page<UserDirectorySummary> findAll(Pageable pageable);

  /**
   * Retrieve the user directory summaries for the tenant.
   *
   * @param tenantId the ID for the tenant
   * @return the user directory summaries for the tenant
   */
  @Query("select uds from UserDirectorySummary uds join uds.tenants as o where o.id = :tenantId")
  List<UserDirectorySummary> findAllByTenantId(@Param("tenantId") UUID tenantId);

  /**
   * Retrieve the filtered user directory summaries.
   *
   * @param filter the filter to apply to the user directory summaries
   * @param pageable the pagination information
   * @return the filtered user directory summaries
   */
  @Query("select uds from UserDirectorySummary uds where (lower(uds.name) like lower(:filter))")
  Page<UserDirectorySummary> findFiltered(String filter, Pageable pageable);
}
