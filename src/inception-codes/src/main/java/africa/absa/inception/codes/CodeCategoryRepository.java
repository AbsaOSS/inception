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

package africa.absa.inception.codes;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * The <b>CodeCategoryRepository</b> interface declares the repository for the <b>CodeCategory</b>
 * domain type.
 *
 * @author Marcus Portmann
 */
public interface CodeCategoryRepository extends JpaRepository<CodeCategory, String> {

  /**
   * Delete the code category.
   *
   * @param codeCategoryId the ID for the code category
   */
  @Modifying
  @Query("delete from CodeCategory cc where cc.id = :codeCategoryId")
  void deleteById(@Param("codeCategoryId") String codeCategoryId);

  /**
   * Retrieve the data for the code category.
   *
   * @param codeCategoryId the ID for the code category
   * @return an Optional containing the data for the code category or an empty Optional if the code
   *     category could not be found
   */
  @Query("select cc.data from CodeCategory cc where cc.id = :codeCategoryId")
  Optional<String> getDataById(@Param("codeCategoryId") String codeCategoryId);

  /**
   * Retrieve the name for the code category.
   *
   * @param codeCategoryId the ID for the code category
   * @return an Optional containing the name for the code category or an empty Optional if the code
   *     category could not be found
   */
  @Query("select cc.name from CodeCategory cc where cc.id = :codeCategoryId")
  Optional<String> getNameById(@Param("codeCategoryId") String codeCategoryId);

  /**
   * Retrieve the date and time the code category was last updated.
   *
   * @param codeCategoryId the ID for the code category
   * @return an Optional containing the date and time the code category was last updated or an empty
   *     Optional if the code category could not be found
   */
  @Query("select cc.updated from CodeCategory cc where cc.id = :codeCategoryId")
  Optional<LocalDateTime> getUpdatedById(@Param("codeCategoryId") String codeCategoryId);

  /**
   * Set the data and the date and time the code category was last updated.
   *
   * @param codeCategoryId the ID for the code category
   * @param data the data
   * @param updated the date and time the code category was last updated
   * @return the number of code categories that were updated
   */
  @Modifying
  @Query(
      "update CodeCategory cc set cc.data = :data, cc.updated = :updated "
          + "where cc.id = :codeCategoryId")
  int setDataAndUpdatedById(
      @Param("codeCategoryId") String codeCategoryId,
      @Param("data") String data,
      @Param("updated") LocalDateTime updated);
}
