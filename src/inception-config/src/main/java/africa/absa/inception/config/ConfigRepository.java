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

package africa.absa.inception.config;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * The <b>ConfigRepository</b> interface declares the repository for the <b> Config</b> domain type.
 *
 * @author Marcus Portmann
 */
public interface ConfigRepository extends JpaRepository<Config, String> {

  /**
   * Delete the config with the specified key.
   *
   * @param key the key for the config
   */
  void deleteByKeyIgnoreCase(String key);

  /**
   * Check whether the config exists.
   *
   * @param key the key for the config
   * @return <b>true</b> if the config exists or <b>false</b> otherwise
   */
  boolean existsByKeyIgnoreCase(String key);

  /**
   * Retrieve all the configs ordered by key descending.
   *
   * @return all the configs ordered by key descending
   */
  List<Config> findAllByOrderByKeyDesc();

  /**
   * Retrieve the config.
   *
   * @param key the key for the config
   * @return an Optional containing the config or an empty Optional if the config could not be found
   */
  Optional<Config> findByKeyIgnoreCase(String key);

  /**
   * Retrieve the filtered configs.
   *
   * @param filter the filter to apply to the keys for the configs
   * @return the filtered configs
   */
  List<Config> findByKeyIgnoreCaseContaining(String filter);

  /**
   * Retrieve the value for the config.
   *
   * @param key the key for the config
   * @return an Optional containing the value for the config or an empty Optional if the config
   *     could not be found
   */
  @Query("select c.value from Config c where lower(c.key) = lower(:key)")
  Optional<String> getValueByKeyIgnoreCase(@Param("key") String key);
}
