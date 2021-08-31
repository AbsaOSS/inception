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
 * The <b>MeasurementUnitRepository</b> interface declares the repository for the
 * <b>MeasurementUnit</b> domain type.
 *
 * @author Marcus Portmann
 */
public interface MeasurementUnitRepository
    extends JpaRepository<MeasurementUnit, MeasurementUnitId> {

  @Query(
      "select mu from MeasurementUnit mu order by mu.localeId, -mu.sortIndex DESC, mu.system, mu.type, mu.name")
  List<MeasurementUnit> findAll();

  @Query(
      "select mu from MeasurementUnit mu where upper(mu.localeId) = upper(:localeId) order by mu.localeId, -mu.sortIndex DESC, mu.system, mu.type, mu.name")
  List<MeasurementUnit> findByLocaleIdIgnoreCase(String localeId);
}
