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

package africa.absa.inception.reporting;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * The <b>ReportDefinitionRepository</b> interface declares the repository for the <b>
 * ReportDefinition</b> domain type.
 *
 * @author Marcus Portmann
 */
public interface ReportDefinitionRepository extends JpaRepository<ReportDefinition, String> {

  /**
   * Delete the report definition.
   *
   * @param reportDefinitionId the ID for the report definition
   */
  @Modifying
  @Query("delete from ReportDefinition rd where rd.id = :reportDefinitionId")
  void deleteById(@Param("reportDefinitionId") String reportDefinitionId);

  /**
   * Retrieve the name for the report definition.
   *
   * @param reportDefinitionId the ID for the report definition
   * @return an Optional containing the name for the report definition or an empty Optional if the
   *     report definition could not be found
   */
  @Query("select rd.name from ReportDefinition rd where rd.id = :reportDefinitionId")
  Optional<String> getNameById(@Param("reportDefinitionId") String reportDefinitionId);
}
