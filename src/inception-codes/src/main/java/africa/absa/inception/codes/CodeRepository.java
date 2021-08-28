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

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * The <b>CodeRepository</b> interface declares the repository for the <b>Code</b> domain type.
 *
 * @author Marcus Portmann
 */
public interface CodeRepository extends JpaRepository<Code, CodeId> {

  long countByCodeCategoryId(String codeCategoryId);

  @Modifying
  @Query("delete from Code c where c.codeCategoryId = :#{#id.codeCategoryId} and c.id = :#{#id.id}")
  void deleteById(@Param("id") CodeId id);

  @Modifying
  @Query("delete from Code c where c.codeCategoryId = :codeCategoryId and c.id = :codeId")
  void deleteById(@Param("codeCategoryId") String codeCategoryId, @Param("codeId") String codeId);

  List<Code> findByCodeCategoryId(String codeCategoryId);

  @Query("select c.name from Code c where c.codeCategoryId = :codeCategoryId and c.id = :codeId")
  Optional<String> getNameById(
      @Param("codeCategoryId") String codeCategoryId, @Param("codeId") String codeId);
}
