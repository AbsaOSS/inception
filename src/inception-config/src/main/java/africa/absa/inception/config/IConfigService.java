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

import africa.absa.inception.core.service.InvalidArgumentException;
import africa.absa.inception.core.service.ServiceUnavailableException;
import java.util.List;

/**
 * The <b>IConfigService</b> interface defines the functionality provided by a Config Service
 * implementation, which manages the config information for an application or service.
 *
 * @author Marcus Portmann
 */
public interface IConfigService {

  /**
   * Delete the config with the specified key.
   *
   * @param key the key for the config
   */
  void deleteConfig(String key)
      throws InvalidArgumentException, ConfigNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the binary config.
   *
   * @param key the key for the config
   * @return the binary config
   */
  byte[] getBinary(String key)
      throws InvalidArgumentException, ConfigNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the binary config.
   *
   * @param key the key for the config
   * @param defaultValue the default value to return if the config does not exist
   * @return the binary config or the default value if the config does not exist
   */
  byte[] getBinary(String key, byte[] defaultValue)
      throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Retrieve the <b>Boolean</b> config.
   *
   * @param key the key for the config
   * @return the <b>Boolean</b> config
   */
  boolean getBoolean(String key)
      throws InvalidArgumentException, ConfigNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the <b>Boolean</b> config.
   *
   * @param key the key for the config
   * @param defaultValue the default value to return if the config does not exist
   * @return the <b>Boolean</b> config or the default value if the config value does not exist
   */
  boolean getBoolean(String key, boolean defaultValue)
      throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Retrieve the config.
   *
   * @param key the key for the config
   * @return the config
   */
  Config getConfig(String key)
      throws InvalidArgumentException, ConfigNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve all the config summaries.
   *
   * @return all the config summaries
   */
  List<ConfigSummary> getConfigSummaries() throws ServiceUnavailableException;

  /**
   * Retrieve all the configs.
   *
   * @return all the configs
   */
  List<Config> getConfigs() throws ServiceUnavailableException;

  /**
   * Retrieve the <b>Double</b> config.
   *
   * @param key the key for the config
   * @return the <b>Double</b> config
   */
  Double getDouble(String key)
      throws InvalidArgumentException, ConfigNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the <b>Double</b> config.
   *
   * @param key the key for the config
   * @param defaultValue the default value to return if the config does not exist
   * @return the <b>Double</b> config or the default value if the config entry does not exist
   */
  double getDouble(String key, double defaultValue)
      throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Retrieve the filtered config summaries.
   *
   * @param filter the filter to apply to the keys for the config summaries
   * @return the config summaries
   */
  List<ConfigSummary> getFilteredConfigSummaries(String filter) throws ServiceUnavailableException;

  /**
   * Retrieve the filtered configs.
   *
   * @param filter the filter to apply to the keys for the config
   * @return the configs
   */
  List<Config> getFilteredConfigs(String filter) throws ServiceUnavailableException;

  /**
   * Retrieve the <b>Integer</b> config.
   *
   * @param key the key for the config
   * @return the <b>Integer</b> config
   */
  Integer getInteger(String key)
      throws InvalidArgumentException, ConfigNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the <b>Integer</b> config.
   *
   * @param key the key for the config
   * @param defaultValue the default value to return if the config does not exist
   * @return the <b>Integer</b> config or the default value if the config entry does not exist
   */
  int getInteger(String key, int defaultValue)
      throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Retrieve the <b>Long</b> config.
   *
   * @param key the key for the config
   * @return the <b>Long</b> config
   */
  Long getLong(String key)
      throws InvalidArgumentException, ConfigNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the <b>Long</b> config.
   *
   * @param key the key for the config
   * @param defaultValue the default value to return if the config does not exist
   * @return the <b>Long</b> config or the default value if the config entry does not exist
   */
  long getLong(String key, long defaultValue)
      throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Retrieve the value for the <b>String</b> config.
   *
   * @param key the key for the config
   * @return the value for the <b>String</b> config
   */
  String getString(String key)
      throws InvalidArgumentException, ConfigNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the value for the <b>String</b> config.
   *
   * @param key the key for the config
   * @param defaultValue the default value to return if the config does not exist
   * @return the value for the <b>String</b> config or the default value if the config does not
   *     exist
   */
  String getString(String key, String defaultValue)
      throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Check if a config with the specified key exists.
   *
   * @param key the key for the config
   * @return <b>true</b> if the config with the specified key exists or <b>false</b> otherwise
   */
  boolean keyExists(String key) throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Set the config.
   *
   * @param config the config
   */
  void setConfig(Config config) throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Set the config key to the specified value.
   *
   * @param key the key for the config
   * @param value the value for the config
   * @param description the description for the config
   */
  void setConfig(String key, Object value, String description)
      throws InvalidArgumentException, ServiceUnavailableException;
}
