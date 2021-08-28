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

package africa.absa.inception.config.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import africa.absa.inception.config.Config;
import africa.absa.inception.config.IConfigService;
import africa.absa.inception.test.InceptionExtension;
import africa.absa.inception.test.TestConfiguration;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

/**
 * The <b>ConfigServiceTest</b> class contains the implementation of the JUnit tests for the
 * <b>ConfigService</b> class.
 *
 * @author Marcus Portmann
 */
@ExtendWith(SpringExtension.class)
@ExtendWith(InceptionExtension.class)
@ContextConfiguration(
    classes = {TestConfiguration.class},
    initializers = {ConfigDataApplicationContextInitializer.class})
@TestExecutionListeners(
    listeners = {
      DependencyInjectionTestExecutionListener.class,
      DirtiesContextTestExecutionListener.class,
      TransactionalTestExecutionListener.class
    })
public class ConfigServiceTest {

  private static final String TEST_BINARY_KEY = "TestBinaryKey";

  private static final byte[] TEST_BINARY_UPDATED_VALUE = "TestBinaryUpdatedValue".getBytes();

  private static final byte[] TEST_BINARY_VALUE = "TestBinaryValue".getBytes();

  private static final String TEST_BOOLEAN_KEY = "TestBooleanKey";

  private static final boolean TEST_BOOLEAN_VALUE = true;

  private static final String TEST_CONFIGURATION_KEY = "TestConfigKey";

  private static final String TEST_CONFIGURATION_VALUE = "TestConfigValue";

  private static final String TEST_DESCRIPTION = "Test Description";

  private static final String TEST_DOUBLE_KEY = "TestDoubleKey";

  private static final Double TEST_DOUBLE_VALUE = 1234.4321;

  private static final String TEST_FILTERED_KEY = "TestFilteredKey";

  private static final String TEST_INTEGER_KEY = "TestIntegerKey";

  private static final Integer TEST_INTEGER_VALUE = 1234;

  private static final String TEST_LONG_KEY = "TestLongKey";

  private static final Long TEST_LONG_VALUE = 4321L;

  private static final String TEST_STRING_KEY = "TestStringKey";

  private static final String TEST_STRING_VALUE = "TestStringValue";

  /** The Config Service. */
  @Autowired private IConfigService configService;

  /** Test the <b>Binary</b> config. */
  @Test
  public void binaryConfigTest() throws Exception {
    if (configService.keyExists(TEST_BINARY_KEY)) {
      fail("Found the Binary config key (" + TEST_BINARY_KEY + ") that should not exist");
    }

    configService.setConfig(TEST_BINARY_KEY, TEST_BINARY_VALUE, TEST_DESCRIPTION);

    if (!configService.keyExists(TEST_BINARY_KEY)) {
      fail("Failed to confirm that the Binary config key (" + TEST_BINARY_KEY + ") exists");
    }

    byte[] value = configService.getBinary(TEST_BINARY_KEY);

    assertArrayEquals(
        TEST_BINARY_VALUE,
        value,
        "The required Binary value was not retrieved for the config key (" + TEST_BINARY_KEY + ")");

    configService.setConfig(
        TEST_BINARY_KEY, TEST_BINARY_UPDATED_VALUE, TEST_DESCRIPTION + " Updated");

    if (!configService.keyExists(TEST_BINARY_KEY)) {
      fail("Failed to confirm that the Binary config key (" + TEST_BINARY_KEY + ") exists");
    }

    value = configService.getBinary(TEST_BINARY_KEY);

    assertArrayEquals(
        TEST_BINARY_UPDATED_VALUE,
        value,
        "The required updated Binary value was not retrieved for the config key ("
            + TEST_BINARY_KEY
            + ")");

    value = configService.getBinary(TEST_BINARY_KEY, new byte[0]);

    assertArrayEquals(
        TEST_BINARY_UPDATED_VALUE,
        value,
        "The required updated Binary value was not retrieved for the config key ("
            + TEST_BINARY_KEY
            + ")");
  }

  /** Test the <b>Boolean</b> config. */
  @Test
  public void booleanConfigTest() throws Exception {
    if (configService.keyExists(TEST_BOOLEAN_KEY)) {
      fail("Found the Boolean config key (" + TEST_BOOLEAN_KEY + ") that should not exist");
    }

    configService.setConfig(TEST_BOOLEAN_KEY, TEST_BOOLEAN_VALUE, TEST_DESCRIPTION);

    if (!configService.keyExists(TEST_BOOLEAN_KEY)) {
      fail("Failed to confirm that the Boolean config key (" + TEST_BOOLEAN_KEY + ") exists");
    }

    boolean value = configService.getBoolean(TEST_BOOLEAN_KEY);

    assertEquals(
        TEST_BOOLEAN_VALUE,
        value,
        "The required Boolean value was not retrieved for the config key ("
            + TEST_BOOLEAN_KEY
            + ")");

    boolean booleanValue = configService.getBoolean(TEST_BOOLEAN_KEY, false);

    assertEquals(
        TEST_BOOLEAN_VALUE,
        booleanValue,
        "The required double value was not retrieved for the config key (" + booleanValue + ")");

    configService.setConfig(TEST_BOOLEAN_KEY, false, TEST_DESCRIPTION + " Updated");

    if (!configService.keyExists(TEST_BOOLEAN_KEY)) {
      fail("Failed to confirm that the Boolean config key (" + TEST_BOOLEAN_KEY + ") exists");
    }

    value = configService.getBoolean(TEST_BOOLEAN_KEY);

    assertFalse(
        value,
        "The required updated Boolean value was not retrieved for the config key ("
            + TEST_BOOLEAN_KEY
            + ")");
  }

  /** Test the <b>Config</b> config. */
  @Test
  public void configConfigTest() throws Exception {
    if (configService.keyExists(TEST_CONFIGURATION_KEY)) {
      fail("Found the config key (" + TEST_BINARY_KEY + ") that should not exist");
    }

    Config config = new Config();

    config.setKey(TEST_CONFIGURATION_KEY);
    config.setValue(TEST_CONFIGURATION_VALUE);
    config.setDescription(TEST_DESCRIPTION);

    configService.setConfig(config.getKey(), config.getValue(), config.getDescription());

    Config retrievedConfig = configService.getConfig(TEST_CONFIGURATION_KEY);

    compareConfig(config, retrievedConfig);

    config.setValue(TEST_CONFIGURATION_VALUE + " Updated");
    config.setDescription(TEST_DESCRIPTION + " Updated");

    configService.setConfig(config.getKey(), config.getValue(), config.getDescription());

    retrievedConfig = configService.getConfig(TEST_CONFIGURATION_KEY);

    compareConfig(config, retrievedConfig);
  }

  /** Test the configs. */
  @Test
  public void configsTest() throws Exception {
    List<Config> configs = configService.getConfigs();
  }

  /** Test the <b>Double</b> config. */
  @Test
  public void doubleConfigTest() throws Exception {
    if (configService.keyExists(TEST_DOUBLE_KEY)) {
      fail("Found the Double config key (" + TEST_DOUBLE_KEY + ") that should not exist");
    }

    configService.setConfig(TEST_DOUBLE_KEY, TEST_DOUBLE_VALUE, TEST_DESCRIPTION);

    if (!configService.keyExists(TEST_DOUBLE_KEY)) {
      fail("Failed to confirm that the Double config key (" + TEST_DOUBLE_KEY + ") exists");
    }

    Double value = configService.getDouble(TEST_DOUBLE_KEY);

    assertEquals(
        TEST_DOUBLE_VALUE,
        value,
        0.0,
        "The required Double value was not retrieved for the config key (" + TEST_DOUBLE_KEY + ")");

    double doubleValue = configService.getDouble(TEST_DOUBLE_KEY, 666.666);

    assertEquals(
        TEST_DOUBLE_VALUE,
        doubleValue,
        0.0,
        "The required double value was not retrieved for the config key (" + doubleValue + ")");

    configService.setConfig(
        TEST_DOUBLE_KEY, TEST_DOUBLE_VALUE + 1.1, TEST_DESCRIPTION + " Updated");

    if (!configService.keyExists(TEST_DOUBLE_KEY)) {
      fail("Failed to confirm that the Double config key (" + TEST_DOUBLE_KEY + ") exists");
    }

    value = configService.getDouble(TEST_DOUBLE_KEY);

    assertEquals(
        TEST_DOUBLE_VALUE + 1.1,
        value,
        0.0,
        "The required updated Double value was not retrieved for the config key ("
            + TEST_DOUBLE_KEY
            + ")");
  }

  /** Test the filtered configs. */
  @Test
  public void filteredConfigsTest() throws Exception {
    configService.setConfig(TEST_FILTERED_KEY, TEST_STRING_VALUE, TEST_DESCRIPTION);

    if (!configService.keyExists(TEST_FILTERED_KEY)) {
      fail("Failed to confirm that the config key (" + TEST_FILTERED_KEY + ") exists");
    }

    List<Config> filteredConfigs = configService.getFilteredConfigs("testfiltered");

    assertEquals(
        1, filteredConfigs.size(), "The required number of filtered configs was not retrieved");

    assertEquals(
        TEST_STRING_VALUE,
        filteredConfigs.get(0).getValue(),
        "The required filtered configs (" + TEST_STRING_VALUE + ") was not retrieved");
  }

  /** Test the <b>Integer</b> config. */
  @Test
  public void integerConfigTest() throws Exception {
    if (configService.keyExists(TEST_INTEGER_KEY)) {
      fail("Found the Integer config key (" + TEST_INTEGER_KEY + ") that should not exist");
    }

    configService.setConfig(TEST_INTEGER_KEY, TEST_INTEGER_VALUE, TEST_DESCRIPTION);

    if (!configService.keyExists(TEST_INTEGER_KEY)) {
      fail("Failed to confirm that the Integer config key (" + TEST_INTEGER_KEY + ") exists");
    }

    Integer value = configService.getInteger(TEST_INTEGER_KEY);

    assertEquals(
        TEST_INTEGER_VALUE,
        value,
        "The required Integer value was not retrieved for the Integer config key ("
            + TEST_INTEGER_KEY
            + ")");

    int integerValue = configService.getInteger(TEST_INTEGER_KEY, 666);

    assertEquals(
        (int) TEST_INTEGER_VALUE,
        integerValue,
        "The required integer value was not retrieved for the Integer config key ("
            + TEST_INTEGER_KEY
            + ")");

    configService.setConfig(
        TEST_INTEGER_KEY, TEST_INTEGER_VALUE + 1, TEST_DESCRIPTION + " Updated");

    if (!configService.keyExists(TEST_INTEGER_KEY)) {
      fail("Failed to confirm that the Integer config key (" + TEST_INTEGER_KEY + ") exists");
    }

    value = configService.getInteger(TEST_INTEGER_KEY);

    assertEquals(
        TEST_INTEGER_VALUE + 1,
        value.intValue(),
        "The required updated Integer value was not retrieved for the config key ("
            + TEST_INTEGER_KEY
            + ")");
  }

  /** Test the <b>Long</b> config. */
  @Test
  public void longConfigTest() throws Exception {
    if (configService.keyExists(TEST_LONG_KEY)) {
      fail("Found the Long config key (" + TEST_LONG_KEY + ") that should not exist");
    }

    configService.setConfig(TEST_LONG_KEY, TEST_LONG_VALUE, TEST_DESCRIPTION);

    if (!configService.keyExists(TEST_LONG_KEY)) {
      fail("Failed to confirm that the Long config key (" + TEST_LONG_KEY + ") exists");
    }

    Long value = configService.getLong(TEST_LONG_KEY);

    assertEquals(
        TEST_LONG_VALUE,
        value,
        "The required Long value was not retrieved for the Long config key ("
            + TEST_LONG_KEY
            + ")");

    Long longValue = configService.getLong(TEST_LONG_KEY, 666);

    assertEquals(
        TEST_LONG_VALUE,
        longValue,
        "The required Long value was not retrieved for the Long config key ("
            + TEST_LONG_KEY
            + ")");

    configService.setConfig(TEST_LONG_KEY, TEST_LONG_VALUE + 1, TEST_DESCRIPTION + " Updated");

    if (!configService.keyExists(TEST_LONG_KEY)) {
      fail("Failed to confirm that the Long config key (" + TEST_LONG_KEY + ") exists");
    }

    value = configService.getLong(TEST_LONG_KEY);

    assertEquals(
        TEST_LONG_VALUE + 1L,
        value.longValue(),
        "The required updated Long value was not retrieved for the config key ("
            + TEST_LONG_KEY
            + ")");
  }

  /** Test the <b>String</b> config. */
  @Test
  public void stringConfigTest() throws Exception {
    if (configService.keyExists(TEST_STRING_KEY)) {
      fail("Found the String config key (" + TEST_STRING_KEY + ") that should not exist");
    }

    configService.setConfig(TEST_STRING_KEY, TEST_STRING_VALUE, TEST_DESCRIPTION);

    if (!configService.keyExists(TEST_STRING_KEY)) {
      fail("Failed to confirm that the String config key (" + TEST_STRING_KEY + ") exists");
    }

    String value = configService.getString(TEST_STRING_KEY);

    assertEquals(
        TEST_STRING_VALUE,
        value,
        "The required String value was not retrieved for the String config key ("
            + TEST_STRING_KEY
            + ")");

    configService.setConfig(
        TEST_STRING_KEY, TEST_STRING_VALUE + "Updated", TEST_DESCRIPTION + " Updated");

    if (!configService.keyExists(TEST_STRING_KEY)) {
      fail("Failed to confirm that the String config key (" + TEST_STRING_KEY + ") exists");
    }

    value = configService.getString(TEST_STRING_KEY);

    assertEquals(
        TEST_STRING_VALUE + "Updated",
        value,
        "The required updated String value was not retrieved for the config key ("
            + TEST_STRING_KEY
            + ")");

    configService.deleteConfig(TEST_STRING_KEY);

    if (configService.keyExists(TEST_STRING_KEY)) {
      fail("Failed to confirm that the String config key (" + TEST_STRING_KEY + ") does not exist");
    }

    value = configService.getString(TEST_STRING_KEY, "DEFAULT VALUE");

    if (!value.equals("DEFAULT VALUE")) {
      fail(
          "Failed to retrieve the default value for the String config key ("
              + TEST_STRING_KEY
              + ")");
    }
  }

  private void compareConfig(Config config1, Config config2) {
    assertEquals(config1.getKey(), config2.getKey(), "The key values for the codes do not match");
    assertEquals(
        config1.getValue(), config2.getValue(), "The value values for the codes do not match");
    assertEquals(
        config1.getDescription(),
        config2.getDescription(),
        "The description values for the codes do not match");
  }
}
