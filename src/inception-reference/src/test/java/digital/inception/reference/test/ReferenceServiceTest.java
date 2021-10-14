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

package africa.absa.inception.reference.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import africa.absa.inception.reference.Country;
import africa.absa.inception.reference.IReferenceService;
import africa.absa.inception.reference.Language;
import africa.absa.inception.reference.MeasurementSystem;
import africa.absa.inception.reference.MeasurementUnit;
import africa.absa.inception.reference.MeasurementUnitType;
import africa.absa.inception.reference.Region;
import africa.absa.inception.reference.TimeZone;
import africa.absa.inception.test.InceptionExtension;
import africa.absa.inception.test.TestConfiguration;
import java.util.List;
import java.util.Optional;
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
 * The <b>ReferenceServiceTest</b> class contains the implementation of the JUnit tests for the
 * <b>ReferenceService</b> class.
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
public class ReferenceServiceTest {

  /** The Reference Service. */
  @Autowired private IReferenceService referenceService;

  /** Test the country reference functionality. */
  @Test
  public void countryTest() throws Exception {
    List<Country> retrievedCountries =
        referenceService.getCountries(IReferenceService.DEFAULT_LOCALE_ID);

    assertEquals(
        246, retrievedCountries.size(), "The correct number of countries was not retrieved");

    Optional<Country> retrievedCountryOptional =
        retrievedCountries.stream().filter(country -> country.getCode().equals("AF")).findFirst();

    if (retrievedCountryOptional.isEmpty()) {
      fail("Failed to retrieve the country with code (AF)");
    } else {
      Country retrievedCountry = retrievedCountryOptional.get();

      assertEquals(
          "AF", retrievedCountry.getCode(), "The correct code was not retrieved for the country");
      assertEquals(
          IReferenceService.DEFAULT_LOCALE_ID,
          retrievedCountry.getLocaleId(),
          "The correct locale was not retrieved for the country");
      assertEquals(
          "Afghanistan",
          retrievedCountry.getName(),
          "The correct name was not retrieved for the country");
      assertEquals(
          "Afghanistan",
          retrievedCountry.getName(),
          "The correct short name was not retrieved for the country");
      assertEquals(
          "Afghanistan",
          retrievedCountry.getName(),
          "The correct description was not retrieved for the country");
    }

    retrievedCountries = referenceService.getCountries();

    assertEquals(
        492, retrievedCountries.size(), "The correct number of countries was not retrieved");
  }

  /** Test the language reference functionality. */
  @Test
  public void languageTest() throws Exception {

    List<Language> retrievedLanguages =
        referenceService.getLanguages(IReferenceService.DEFAULT_LOCALE_ID);

    assertEquals(
        93, retrievedLanguages.size(), "The correct number of languages was not retrieved");

    retrievedLanguages = referenceService.getLanguages();

    assertEquals(
        186, retrievedLanguages.size(), "The correct number of languages was not retrieved");
  }

  /** Test the measurement systems reference functionality. */
  @Test
  public void measurementSystemsTest() throws Exception {
    List<MeasurementSystem> retrievedMeasurementSystems =
        referenceService.getMeasurementSystems(IReferenceService.DEFAULT_LOCALE_ID);

    assertEquals(
        3,
        retrievedMeasurementSystems.size(),
        "The correct number of measurement systems was not retrieved");

    retrievedMeasurementSystems = referenceService.getMeasurementSystems();

    assertEquals(
        6,
        retrievedMeasurementSystems.size(),
        "The correct number of measurement systems was not retrieved");
  }

  /** Test the measurement unit types reference functionality. */
  @Test
  public void measurementUnitTypesTest() throws Exception {
    List<MeasurementUnitType> retrievedMeasurementUnitTypes =
        referenceService.getMeasurementUnitTypes(IReferenceService.DEFAULT_LOCALE_ID);

    assertEquals(
        3,
        retrievedMeasurementUnitTypes.size(),
        "The correct number of measurement unit types was not retrieved");

    retrievedMeasurementUnitTypes = referenceService.getMeasurementUnitTypes();

    assertEquals(
        6,
        retrievedMeasurementUnitTypes.size(),
        "The correct number of measurement unit types was not retrieved");
  }

  /** Test the measurement units reference functionality. */
  @Test
  public void measurementUnitsTest() throws Exception {
    List<MeasurementUnit> retrievedMeasurementUnits =
        referenceService.getMeasurementUnits(IReferenceService.DEFAULT_LOCALE_ID);

    assertEquals(
        9,
        retrievedMeasurementUnits.size(),
        "The correct number of measurement units was not retrieved");

    retrievedMeasurementUnits = referenceService.getMeasurementUnits();

    assertEquals(
        18,
        retrievedMeasurementUnits.size(),
        "The correct number of measurement units was not retrieved");
  }

  /** Test the region reference functionality. */
  @Test
  public void regionTest() throws Exception {
    List<Region> retrievedRegions =
        referenceService.getRegions(IReferenceService.DEFAULT_LOCALE_ID);

    assertEquals(9, retrievedRegions.size(), "The correct number of regions was not retrieved");

    retrievedRegions = referenceService.getRegions();

    assertEquals(18, retrievedRegions.size(), "The correct number of regions was not retrieved");
  }

  /** Test the time zone reference functionality. */
  @Test
  public void timeZoneTest() throws Exception {
    List<TimeZone> retrievedTimeZones =
        referenceService.getTimeZones(IReferenceService.DEFAULT_LOCALE_ID);

    if (retrievedTimeZones.size() <= 0) {
      fail("Failed to retrieve the time zones");
    }
  }

  /** Test the reference validity check functionality. */
  @Test
  public void validityTest() throws Exception {
    assertTrue(referenceService.isValidCountry("ZA"));
    assertTrue(referenceService.isValidLanguage("EN"));
    assertTrue(referenceService.isValidMeasurementSystem("metric"));
    assertTrue(referenceService.isValidMeasurementUnit("metric_centimeter"));
    assertTrue(referenceService.isValidMeasurementUnitType("length"));
    assertTrue(referenceService.isValidTimeZone("Africa/Johannesburg"));
    assertTrue(referenceService.isValidRegion("EC"));
  }
}
