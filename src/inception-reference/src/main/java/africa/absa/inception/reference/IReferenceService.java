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

import africa.absa.inception.core.service.InvalidArgumentException;
import africa.absa.inception.core.service.ServiceUnavailableException;
import java.util.List;

/**
 * The <b>IReferenceService</b> interface defines the functionality provided by a Reference Service
 * implementation.
 *
 * @author Marcus Portmann
 */
public interface IReferenceService {

  /** The default locale ID. */
  String DEFAULT_LOCALE_ID = "en-US";

  /**
   * Retrieve the country reference data for all locales.
   *
   * @return the country reference data
   * @throws ServiceUnavailableException if the country reference data could not be retrieved
   */
  List<Country> getCountries() throws ServiceUnavailableException;

  /**
   * Retrieve the country reference data for a specific locale.
   *
   * @param localeId the Unicode locale identifier for the locale to retrieve the country reference
   *     data for
   * @return the country reference data
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the country reference data could not be retrieved
   */
  List<Country> getCountries(String localeId)
      throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Retrieve the language reference data for all locales.
   *
   * @return the language reference data
   * @throws ServiceUnavailableException if the language reference data could not be retrieved
   */
  List<Language> getLanguages() throws ServiceUnavailableException;

  /**
   * Retrieve the language reference data for a specific locale.
   *
   * @param localeId the Unicode locale identifier for the locale to retrieve the language reference
   *     data for
   * @return the language reference data
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the language reference data could not be retrieved
   */
  List<Language> getLanguages(String localeId)
      throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Retrieve the measurement system reference data for all locales.
   *
   * @return the measurement system reference data
   * @throws ServiceUnavailableException if the measurement system reference data could not be
   *     retrieved
   */
  List<MeasurementSystem> getMeasurementSystems() throws ServiceUnavailableException;

  /**
   * Retrieve the measurement system reference data for a specific locale.
   *
   * @param localeId the Unicode locale identifier for the locale to retrieve the measurement system
   *     reference data for
   * @return the measurement system reference data
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the measurement system reference data could not be
   *     retrieved
   */
  List<MeasurementSystem> getMeasurementSystems(String localeId)
      throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Retrieve the measurement unit type reference data for all locales.
   *
   * @return the measurement unit type reference data
   * @throws ServiceUnavailableException if the measurement unit type reference data could not be
   *     retrieved
   */
  List<MeasurementUnitType> getMeasurementUnitTypes() throws ServiceUnavailableException;

  /**
   * Retrieve the measurement unit type reference data for a specific locale.
   *
   * @param localeId the Unicode locale identifier for the locale to retrieve the measurement unit
   *     type reference data for
   * @return the measurement unit type reference data
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the measurement unit type reference data could not be
   *     retrieved
   */
  List<MeasurementUnitType> getMeasurementUnitTypes(String localeId)
      throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Retrieve the measurement unit reference data for all locales.
   *
   * @return the measurement unit reference data
   * @throws ServiceUnavailableException if the measurement unit reference data could not be
   *     retrieved
   */
  List<MeasurementUnit> getMeasurementUnits() throws ServiceUnavailableException;

  /**
   * Retrieve the measurement unit reference data for a specific locale.
   *
   * @param localeId the Unicode locale identifier for the locale to retrieve the measurement unit
   *     reference data for
   * @return the measurement unit reference data
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the measurement unit reference data could not be
   *     retrieved
   */
  List<MeasurementUnit> getMeasurementUnits(String localeId)
      throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Retrieve the region reference data for all locales.
   *
   * @return the region reference data
   * @throws ServiceUnavailableException if the region reference data could not be retrieved
   */
  List<Region> getRegions() throws ServiceUnavailableException;

  /**
   * Retrieve the region reference data for a specific locale.
   *
   * @param localeId the Unicode locale identifier for the locale to retrieve the region reference
   *     data for
   * @return the region reference data
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the region reference data could not be retrieved
   */
  List<Region> getRegions(String localeId)
      throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Retrieve the time zone reference data for all locales.
   *
   * @return the time zones reference data
   * @throws ServiceUnavailableException if the time zones reference data could not be retrieved
   */
  List<TimeZone> getTimeZones() throws ServiceUnavailableException;

  /**
   * Retrieve the time zone reference data for a specific locale.
   *
   * @param localeId the Unicode locale identifier for the locale to retrieve the time zone
   *     reference data for
   * @return the time zone reference data
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the time zones reference data could not be retrieved
   */
  List<TimeZone> getTimeZones(String localeId)
      throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Check whether the code is a valid code for a country.
   *
   * @param countryCode the code for the country
   * @return <b>true</b> if the code is a valid code for a country or <b>false</b> otherwise
   * @throws ServiceUnavailableException if the country code check failed
   */
  boolean isValidCountry(String countryCode) throws ServiceUnavailableException;

  /**
   * Check whether the code is a valid ISO 639-1 alpha-2 code for a language.
   *
   * @param languageCode the code for the language
   * @return <b>true</b> if the code is a valid ISO 639-1 alpha-2 code for a language or
   *     <b>false</b> otherwise
   * @throws ServiceUnavailableException if the language code check failed
   */
  boolean isValidLanguage(String languageCode) throws ServiceUnavailableException;

  /**
   * Check whether the code is a valid code for a measurement system.
   *
   * @param measurementSystemCode the code for the measurement system
   * @return <b>true</b> if the code is a valid code for a measurement system or <b>false</b>
   *     otherwise
   * @throws ServiceUnavailableException if the measurement system code check failed
   */
  boolean isValidMeasurementSystem(String measurementSystemCode) throws ServiceUnavailableException;

  /**
   * Check whether the code is a valid code for a measurement unit.
   *
   * @param measurementUnitCode the code for the measurement unit
   * @return <b>true</b> if the code is a valid code for a measurement unit or <b>false</b>
   *     otherwise
   * @throws ServiceUnavailableException if the measurement unit code check failed
   */
  boolean isValidMeasurementUnit(String measurementUnitCode) throws ServiceUnavailableException;

  /**
   * Check whether the code is a valid code for a measurement unit type.
   *
   * @param measurementUnitTypeCode the code for the measurement unit type
   * @return <b>true</b> if the code is a valid code for a measurement unit type or <b>false</b>
   *     otherwise
   * @throws ServiceUnavailableException if the measurement unit type code check failed
   */
  boolean isValidMeasurementUnitType(String measurementUnitTypeCode)
      throws ServiceUnavailableException;

  /**
   * Check whether the code is a valid code for a region.
   *
   * @param regionCode the code for the region
   * @return <b>true</b> if the code is a valid code for a region or <b>false</b> otherwise
   * @throws ServiceUnavailableException if the region code check failed
   */
  boolean isValidRegion(String regionCode) throws ServiceUnavailableException;

  /**
   * Check whether the code is a valid code for a time zone.
   *
   * @param timeZoneCode the code for the time zone
   * @return <b>true</b> if the code is a valid code for a time zone or <b>false</b> otherwise
   * @throws ServiceUnavailableException if the time zone code check failed
   */
  boolean isValidTimeZone(String timeZoneCode) throws ServiceUnavailableException;
}
