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

package africa.absa.inception.reference;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * The <b>RegionRepository</b> interface declares the repository for the <b>Region</b> domain type.
 *
 * @author Marcus Portmann
 */
public interface RegionRepository extends JpaRepository<Region, RegionId> {

  @Query("select r from Region r order by r.localeId, -r.sortIndex DESC, r.name")
  List<Region> findAll();

  @Query(
      "select r from Region r where upper(r.localeId) = upper(:localeId) order by r.localeId, -r.sortIndex DESC, r.name")
  List<Region> findByLocaleIdIgnoreCase(String localeId);
}
