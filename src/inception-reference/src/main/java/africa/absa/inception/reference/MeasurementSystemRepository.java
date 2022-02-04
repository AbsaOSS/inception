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
 * The <b>MeasurementSystemRepository</b> interface declares the repository for the
 * <b>MeasurementSystem</b> domain type.
 *
 * @author Marcus Portmann
 */
public interface MeasurementSystemRepository
    extends JpaRepository<MeasurementSystem, MeasurementSystemId> {

  /**
   * Retrieve all the measurement systems sorted by locale ID, sort index, and name.
   *
   * @return all the measurement systems sorted by locale ID, sort index, and name.
   */
  @Query("select ms from MeasurementSystem ms order by ms.localeId, -ms.sortIndex DESC, ms.name")
  List<MeasurementSystem> findAll();

  /**
   * Retrieve the measurement systems for the specified locale sorted by locale ID, sort index, and
   * name.
   *
   * @param localeId the Unicode locale identifier for the locale to retrieve the measurement
   *     systems for
   * @return the measurement systems for the specified locale sorted by locale ID, sort index, and
   *     name
   */
  @Query(
      "select ms from MeasurementSystem ms where upper(ms.localeId) = upper(:localeId) order by ms.localeId, -ms.sortIndex DESC, ms.name")
  List<MeasurementSystem> findByLocaleIdIgnoreCase(String localeId);
}
