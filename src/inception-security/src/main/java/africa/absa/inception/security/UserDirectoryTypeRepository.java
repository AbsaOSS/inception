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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * The <b>UserDirectoryTypeRepository</b> interface declares the repository for the <b>
 * UserDirectoryType</b> domain type.
 *
 * @author Marcus Portmann
 */
public interface UserDirectoryTypeRepository extends JpaRepository<UserDirectoryType, String> {

  @Modifying
  @Query("delete from UserDirectoryType udt where udt.code = :userDirectoryTypeCode")
  void deleteById(@Param("userDirectoryTypeCode") String userDirectoryTypeCode);
}
