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

  @Override
  public List<ConfigSummary> getConfigSummaries() throws ServiceUnavailableException {
    try {
      return configSummaryRepository.findAllByOrderByKeyDesc();
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to retrieve the config summaries", e);
    }
  }

  @Override
  public List<Config> getConfigs() throws ServiceUnavailableException {
    try {
      return configRepository.findAllByOrderByKeyDesc();
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to retrieve the configs", e);
    }
  }

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
