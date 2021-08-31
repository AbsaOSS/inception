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
 * The <b>MeasurementUnitTypeRepository</b> interface declares the repository for the
 * <b>MeasurementUnitType</b> domain type.
 *
 * @author Marcus Portmann
 */
public interface MeasurementUnitTypeRepository
    extends JpaRepository<MeasurementUnitType, MeasurementUnitTypeId> {

  @Query(
      "select mut from MeasurementUnitType mut order by mut.localeId, -mut.sortIndex DESC, mut.name")
  List<MeasurementUnitType> findAll();

  @Query(
      "select mut from MeasurementUnitType mut where upper(mut.localeId) = upper(:localeId) order by mut.localeId, -mut.sortIndex DESC, mut.name")
  List<MeasurementUnitType> findByLocaleIdIgnoreCase(String localeId);
}
