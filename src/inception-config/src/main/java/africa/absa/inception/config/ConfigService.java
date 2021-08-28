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
import africa.absa.inception.core.service.ValidationError;
import africa.absa.inception.core.util.Base64Util;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * The <b>ConfigService</b> class provides the Config Service implementation.
 *
 * @author Marcus Portmann
 */
@Service
@SuppressWarnings("unused")
public class ConfigService implements IConfigService {

  /** The Config Repository. */
  private final ConfigRepository configRepository;

  /** The Config Summary Repository. */
  private final ConfigSummaryRepository configSummaryRepository;

  /** The JSR-303 validator. */
  private final Validator validator;

  /**
   * Constructs a new <b>ConfigService</b>.
   *
   * @param validator the JSR-303 validator
   * @param configRepository the Config Repository
   * @param configSummaryRepository the Config Summary Repository
   */
  public ConfigService(
      Validator validator,
      ConfigRepository configRepository,
      ConfigSummaryRepository configSummaryRepository) {
    this.validator = validator;
    this.configRepository = configRepository;
    this.configSummaryRepository = configSummaryRepository;
  }

  /**
   * Remove the config with the specified key.
   *
   * @param key the key for the config
   */
  @Override
  @Transactional
  public void deleteConfig(String key)
      throws InvalidArgumentException, ConfigNotFoundException, ServiceUnavailableException {
    if (!StringUtils.hasText(key)) {
      throw new InvalidArgumentException("key");
    }

    try {
      if (!configRepository.existsByKeyIgnoreCase(key)) {
        throw new ConfigNotFoundException(key);
      }

      configRepository.deleteByKeyIgnoreCase(key);
    } catch (ConfigNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to delete the config with the key (" + key + ")", e);
    }
  }

  /**
   * Retrieve the binary config.
   *
   * @param key the key for the config
   * @return the binary config
   */
  @Override
  public byte[] getBinary(String key)
      throws InvalidArgumentException, ConfigNotFoundException, ServiceUnavailableException {
    if (!StringUtils.hasText(key)) {
      throw new InvalidArgumentException("key");
    }

    try {
      Optional<String> valueOptional = configRepository.getValueByKeyIgnoreCase(key);

      if (valueOptional.isPresent()) {
        return Base64Util.decode(valueOptional.get());
      } else {
        throw new ConfigNotFoundException(key);
      }
    } catch (ConfigNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the binary config with the key (" + key + ")", e);
    }
  }

  /**
   * Retrieve the binary config.
   *
   * @param key the key for the config
   * @param defaultValue the default value to return if the config does not exist
   * @return the binary config or the default value if the config does not exist
   */
  @Override
  public byte[] getBinary(String key, byte[] defaultValue)
      throws InvalidArgumentException, ServiceUnavailableException {
    if (!StringUtils.hasText(key)) {
      throw new InvalidArgumentException("key");
    }

    try {
      Optional<String> valueOptional = configRepository.getValueByKeyIgnoreCase(key);

      return valueOptional.map(Base64Util::decode).orElse(defaultValue);
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the binary config with the key (" + key + ")", e);
    }
  }

  /**
   * Retrieve the <b>Boolean</b> config.
   *
   * @param key the key for the config
   * @return the <b>Boolean</b> config
   */
  @Override
  public boolean getBoolean(String key)
      throws InvalidArgumentException, ConfigNotFoundException, ServiceUnavailableException {
    if (!StringUtils.hasText(key)) {
      throw new InvalidArgumentException("key");
    }

    try {
      Optional<String> valueOptional = configRepository.getValueByKeyIgnoreCase(key);

      if (valueOptional.isPresent()) {
        return Boolean.parseBoolean(valueOptional.get());
      } else {
        throw new ConfigNotFoundException(key);
      }
    } catch (ConfigNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the Boolean config with the key (" + key + ")", e);
    }
  }

  /**
   * Retrieve the <b>Boolean</b> config.
   *
   * @param key the key for the config
   * @param defaultValue the default value to return if the config does not exist
   * @return the <b>Boolean</b> config or the default value if the config value does not exist
   */
  @Override
  public boolean getBoolean(String key, boolean defaultValue)
      throws InvalidArgumentException, ServiceUnavailableException {
    if (!StringUtils.hasText(key)) {
      throw new InvalidArgumentException("key");
    }

    try {
      Optional<String> valueOptional = configRepository.getValueByKeyIgnoreCase(key);

      return valueOptional.map(Boolean::parseBoolean).orElse(defaultValue);
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the Boolean config with the key (" + key + ")", e);
    }
  }

  /**
   * Retrieve the config.
   *
   * @param key the key for the config
   * @return the config
   */
  @Override
  public Config getConfig(String key)
      throws InvalidArgumentException, ConfigNotFoundException, ServiceUnavailableException {
    if (!StringUtils.hasText(key)) {
      throw new InvalidArgumentException("key");
    }

    try {
      Optional<Config> configOptional = configRepository.findByKeyIgnoreCase(key);

      if (configOptional.isEmpty()) {
        throw new ConfigNotFoundException(key);
      } else {
        return configOptional.get();
      }
    } catch (ConfigNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the config with the key (" + key + ")", e);
    }
  }

  /**
   * Retrieve all the config summaries.
   *
   * @return all the config summaries
   */
  @Override
  public List<ConfigSummary> getConfigSummaries() throws ServiceUnavailableException {
    try {
      return configSummaryRepository.findAllByOrderByKeyDesc();
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to retrieve the config summaries", e);
    }
  }

  /**
   * Retrieve all the configs.
   *
   * @return all the configs
   */
  @Override
  public List<Config> getConfigs() throws ServiceUnavailableException {
    try {
      return configRepository.findAllByOrderByKeyDesc();
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to retrieve the configs", e);
    }
  }

  /**
   * Retrieve the <b>Double</b> config.
   *
   * @param key the key for the config
   * @return the <b>Double</b> config
   */
  @Override
  public Double getDouble(String key)
      throws InvalidArgumentException, ConfigNotFoundException, ServiceUnavailableException {
    if (!StringUtils.hasText(key)) {
      throw new InvalidArgumentException("key");
    }

    try {
      Optional<String> valueOptional = configRepository.getValueByKeyIgnoreCase(key);

      if (valueOptional.isPresent()) {
        return Double.parseDouble(valueOptional.get());
      } else {
        throw new ConfigNotFoundException(key);
      }
    } catch (ConfigNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the Double config with the key (" + key + ")", e);
    }
  }

  /**
   * Retrieve the <b>Double</b> config.
   *
   * @param key the key for the config
   * @param defaultValue the default value to return if the config does not exist
   * @return the <b>Double</b> config or the default value if the config entry does not exist
   */
  @Override
  public double getDouble(String key, double defaultValue)
      throws InvalidArgumentException, ServiceUnavailableException {
    if (!StringUtils.hasText(key)) {
      throw new InvalidArgumentException("key");
    }

    try {
      Optional<String> valueOptional = configRepository.getValueByKeyIgnoreCase(key);

      return valueOptional.map(Double::parseDouble).orElse(defaultValue);
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the Double config with the key (" + key + ")", e);
    }
  }

  /**
   * Retrieve the filtered config summaries.
   *
   * @param filter the filter to apply to the keys for the config summaries
   * @return the filtered config summaries
   */
  @Override
  public List<ConfigSummary> getFilteredConfigSummaries(String filter)
      throws ServiceUnavailableException {
    try {
      if (StringUtils.hasText(filter)) {
        return configSummaryRepository.findByKeyIgnoreCaseContaining(filter);
      } else {
        return configSummaryRepository.findAllByOrderByKeyDesc();
      }
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the config summaries matching the filter (" + filter + ")", e);
    }
  }

  /**
   * Retrieve the filtered configs.
   *
   * @param filter the filter to apply to the keys for the configs
   * @return the configs
   */
  @Override
  public List<Config> getFilteredConfigs(String filter) throws ServiceUnavailableException {
    try {
      if (StringUtils.hasText(filter)) {
        return configRepository.findByKeyIgnoreCaseContaining(filter);
      } else {
        return configRepository.findAllByOrderByKeyDesc();
      }
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the config matching the filter (" + filter + ")", e);
    }
  }

  /**
   * Retrieve the <b>Integer</b> config.
   *
   * @param key the key for the config
   * @return the <b>Integer</b> config
   */
  @Override
  public Integer getInteger(String key)
      throws InvalidArgumentException, ConfigNotFoundException, ServiceUnavailableException {
    if (!StringUtils.hasText(key)) {
      throw new InvalidArgumentException("key");
    }

    try {
      Optional<String> valueOptional = configRepository.getValueByKeyIgnoreCase(key);

      if (valueOptional.isPresent()) {
        return Integer.parseInt(valueOptional.get());
      } else {
        throw new ConfigNotFoundException(key);
      }
    } catch (ConfigNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the Integer config with the key (" + key + ")", e);
    }
  }

  /**
   * Retrieve the <b>Integer</b> config.
   *
   * @param key the key for the config
   * @param defaultValue the default value to return if the config does not exist
   * @return the <b>Integer</b> config or the default value if the config entry does not exist
   */
  @Override
  public int getInteger(String key, int defaultValue)
      throws InvalidArgumentException, ServiceUnavailableException {
    if (!StringUtils.hasText(key)) {
      throw new InvalidArgumentException("key");
    }

    try {
      Optional<String> valueOptional = configRepository.getValueByKeyIgnoreCase(key);

      return valueOptional.map(Integer::parseInt).orElse(defaultValue);
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the Integer config with the key (" + key + ")", e);
    }
  }

  /**
   * Retrieve the <b>Long</b> config.
   *
   * @param key the key for the config
   * @return the <b>Long</b> config
   */
  @Override
  public Long getLong(String key)
      throws InvalidArgumentException, ConfigNotFoundException, ServiceUnavailableException {
    if (!StringUtils.hasText(key)) {
      throw new InvalidArgumentException("key");
    }

    try {
      Optional<String> valueOptional = configRepository.getValueByKeyIgnoreCase(key);

      if (valueOptional.isPresent()) {
        return Long.parseLong(valueOptional.get());
      } else {
        throw new ConfigNotFoundException(key);
      }
    } catch (ConfigNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the Long config with the key (" + key + ")", e);
    }
  }

  /**
   * Retrieve the <b>Long</b> config.
   *
   * @param key the key for the config
   * @param defaultValue the default value to return if the config does not exist
   * @return the <b>Long</b> config or the default value if the config entry does not exist
   */
  @Override
  public long getLong(String key, long defaultValue)
      throws InvalidArgumentException, ServiceUnavailableException {
    if (!StringUtils.hasText(key)) {
      throw new InvalidArgumentException("key");
    }

    try {
      Optional<String> valueOptional = configRepository.getValueByKeyIgnoreCase(key);

      return valueOptional.map(Long::parseLong).orElse(defaultValue);
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the Long config with the key (" + key + ")", e);
    }
  }

  /**
   * Retrieve the value for the <b>String</b> config.
   *
   * @param key the key for the config
   * @return the value for the <b>String</b> config
   */
  @Override
  public String getString(String key)
      throws InvalidArgumentException, ConfigNotFoundException, ServiceUnavailableException {
    if (!StringUtils.hasText(key)) {
      throw new InvalidArgumentException("key");
    }

    try {
      Optional<String> valueOptional = configRepository.getValueByKeyIgnoreCase(key);

      if (valueOptional.isPresent()) {
        return valueOptional.get();
      } else {
        throw new ConfigNotFoundException(key);
      }
    } catch (ConfigNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the String config with the key (" + key + ")", e);
    }
  }

  /**
   * Retrieve the value for the <b>String</b> config.
   *
   * @param key the key for the config
   * @param defaultValue the default value to return if the config does not exist
   * @return the value for the <b>String</b> config or the default value if the config does not
   *     exist
   */
  @Override
  public String getString(String key, String defaultValue)
      throws InvalidArgumentException, ServiceUnavailableException {
    if (!StringUtils.hasText(key)) {
      throw new InvalidArgumentException("key");
    }

    try {
      Optional<String> valueOptional = configRepository.getValueByKeyIgnoreCase(key);

      return valueOptional.orElse(defaultValue);
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the String config with the key (" + key + ")", e);
    }
  }

  /**
   * Check if a config with the specified key exists.
   *
   * @param key the key for the config
   * @return <b>true</b> if a config with the specified key exists or <b>false</b> otherwise
   */
  @Override
  public boolean keyExists(String key)
      throws InvalidArgumentException, ServiceUnavailableException {
    if (!StringUtils.hasText(key)) {
      throw new InvalidArgumentException("key");
    }

    try {
      return configRepository.existsByKeyIgnoreCase(key);
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to checked whether the config key (" + key + ") exists", e);
    }
  }

  /**
   * Set the config.
   *
   * @param config the config
   */
  @Override
  @Transactional
  public void setConfig(Config config)
      throws InvalidArgumentException, ServiceUnavailableException {
    if (config == null) {
      throw new InvalidArgumentException("config");
    }

    Set<ConstraintViolation<Config>> constraintViolations = validator.validate(config);

    if (!constraintViolations.isEmpty()) {
      throw new InvalidArgumentException(
          "config", ValidationError.toValidationErrors(constraintViolations));
    }

    try {
      configRepository.saveAndFlush(config);
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to set the config with the key (" + config.getKey() + ")", e);
    }
  }

  /**
   * Set the config key to the specified value.
   *
   * @param key the key for the config
   * @param value the value for the config
   * @param description the description for the config
   */
  @Override
  @Transactional
  public void setConfig(String key, Object value, String description)
      throws InvalidArgumentException, ServiceUnavailableException {
    if (!StringUtils.hasText(key)) {
      throw new InvalidArgumentException("key");
    }

    if (value == null) {
      throw new InvalidArgumentException("value");
    }

    if (description == null) {
      throw new InvalidArgumentException("description");
    }

    try {
      String stringValue;

      if (value instanceof String) {
        stringValue = (String) value;
      } else if (value instanceof byte[]) {
        stringValue = Base64Util.encodeBytes((byte[]) value);
      } else {
        stringValue = value.toString();
      }

      configRepository.save(new Config(key, stringValue, description));
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to set the config with the key (" + key + ")", e);
    }
  }
}
