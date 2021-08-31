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

import static org.junit.jupiter.api.Assertions.fail;

import africa.absa.inception.reference.Country;
import africa.absa.inception.reference.IReferenceService;
import africa.absa.inception.reference.Language;
import africa.absa.inception.reference.MeasurementSystem;
import africa.absa.inception.reference.MeasurementUnit;
import africa.absa.inception.reference.MeasurementUnitType;
import africa.absa.inception.reference.Region;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * The <code>GenerateLiquibaseChangelog</code> class.
 *
 * @author Marcus Portmann
 */
@SpringBootApplication
@ComponentScan(
    basePackages = {"digital.inception"},
    lazyInit = true,
    excludeFilters = {
      @ComponentScan.Filter(value = SpringBootApplication.class, type = FilterType.ANNOTATION),
      @ComponentScan.Filter(
          pattern = "digital\\.inception\\.application\\.ApplicationDataSourceConfiguration",
          type = FilterType.REGEX),
      @ComponentScan.Filter(
          pattern = "digital\\.inception\\.application\\.ApplicationTransactionManager",
          type = FilterType.REGEX),
      @ComponentScan.Filter(
          pattern = "digital\\.inception\\.persistence\\.PersistenceConfiguration",
          type = FilterType.REGEX)
    })
public class GenerateLiquibaseDataChangelog implements CommandLineRunner {

  private static final Set<String> LOCALE_IDS = Set.of("en-US", "en-ZA");

  /* Logger */
  private static final Logger logger =
      LoggerFactory.getLogger(GenerateLiquibaseDataChangelog.class);

  /** The Spring application context. */
  private final ApplicationContext applicationContext;

  /** The Reference Service. */
  private final IReferenceService referenceService;

  /**
   * Constructs a new <b>GenerateLiquibaseDataChangelog</b>.
   *
   * @param applicationContext the Spring application context
   * @param referenceService the Reference Service
   */
  public GenerateLiquibaseDataChangelog(
      ApplicationContext applicationContext, IReferenceService referenceService) {
    this.applicationContext = applicationContext;
    this.referenceService = referenceService;
  }

  /**
   * The main method.
   *
   * @param args the command-line arguments
   */
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(GenerateLiquibaseDataChangelog.class);
    app.setBannerMode(Mode.OFF);
    app.setWebApplicationType(WebApplicationType.NONE);
    app.run(args);
  }

  @Override
  public void run(String... args) throws Exception {
    String fileName =
        System.getProperty("user.dir")
            + File.separator
            + "inception-database"
            + File.separator
            + "inception-reference-data.changelog.xml";

    logger.info("Writing reference data to file " + fileName);

    try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
      writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      writer.println("");
      writer.println("<databaseChangeLog");
      writer.println("  xmlns=\"http://www.liquibase.org/xml/ns/dbchangelog\"");
      writer.println("  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
      writer.println("  xsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog");
      writer.println("         http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.8.xsd\">");
      writer.println("");
      writer.println(
          "  <changeSet id=\"inception-reference-data-1.0.0\" author=\"Marcus Portmann\">");
      writer.println("    <comment>Inception - Reference - Data - 1.0.0</comment>");
      writer.println("");

      writeReferenceData(writer);

      writer.println("  </changeSet>");
      writer.println("</databaseChangeLog>");
    }

    System.exit(SpringApplication.exit(applicationContext));
  }

  private void writeReferenceData(PrintWriter writer) throws Exception {
    for (String localeId : LOCALE_IDS) {
      // Countries
      for (Country country : referenceService.getCountries(localeId)) {

        if (country.getNationality().contains(",")) {
          fail("Invalid nationality for country (" + country.getCode() + ")");
        }

        writer.println("    <insert schemaName=\"reference\" tableName=\"countries\">");
        writer.println("      <column name=\"code\" value=\"" + country.getCode() + "\"/>");
        writer.println(
            "      <column name=\"iso3_code\" value=\"" + country.getIso3Code() + "\"/>");
        writer.println(
            "      <column name=\"locale_id\" value=\"" + country.getLocaleId() + "\"/>");

        if (country.getSortIndex() != null) {
          writer.println(
              "      <column name=\"sort_index\" value=\"" + country.getSortIndex() + "\"/>");
        }

        writer.println("      <column name=\"name\" value=\"" + country.getName() + "\"/>");
        writer.println(
            "      <column name=\"short_name\" value=\"" + country.getShortName() + "\"/>");
        writer.println(
            "      <column name=\"description\" value=\"" + country.getDescription() + "\"/>");
        writer.println(
            "      <column name=\"sovereign_state\" value=\""
                + country.getSovereignState()
                + "\"/>");
        writer.println(
            "      <column name=\"nationality\" value=\"" + country.getNationality() + "\"/>");
        writer.println("    </insert>");
      }

      writer.println();

      // Languages
      for (Language language : referenceService.getLanguages(localeId)) {

        writer.println("    <insert schemaName=\"reference\" tableName=\"languages\">");
        writer.println(
            "      <column name=\"code\" value=\"" + language.getCode().toUpperCase() + "\"/>");
        writer.println(
            "      <column name=\"iso3_code\" value=\""
                + language.getIso3Code().toUpperCase()
                + "\"/>");
        writer.println(
            "      <column name=\"locale_id\" value=\"" + language.getLocaleId() + "\"/>");

        if (language.getSortIndex() != null) {
          writer.println(
              "      <column name=\"sort_index\" value=\"" + language.getSortIndex() + "\"/>");
        }

        writer.println("      <column name=\"name\" value=\"" + language.getName() + "\"/>");
        writer.println(
            "      <column name=\"short_name\" value=\"" + language.getShortName() + "\"/>");
        writer.println(
            "      <column name=\"description\" value=\"" + language.getDescription() + "\"/>");
        writer.println("    </insert>");
      }

      writer.println();

      // Measurement Systems
      for (MeasurementSystem measurementSystem : referenceService.getMeasurementSystems(localeId)) {

        writer.println("    <insert schemaName=\"reference\" tableName=\"measurement_systems\">");
        writer.println(
            "      <column name=\"code\" value=\"" + measurementSystem.getCode() + "\"/>");
        writer.println(
            "      <column name=\"locale_id\" value=\"" + measurementSystem.getLocaleId() + "\"/>");

        if (measurementSystem.getSortIndex() != null) {
          writer.println(
              "      <column name=\"sort_index\" value=\""
                  + measurementSystem.getSortIndex()
                  + "\"/>");
        }

        writer.println(
            "      <column name=\"name\" value=\"" + measurementSystem.getName() + "\"/>");
        writer.println(
            "      <column name=\"description\" value=\""
                + measurementSystem.getDescription()
                + "\"/>");
        writer.println("    </insert>");
      }

      writer.println();

      // Measurement Unit Types
      for (MeasurementUnitType measurementUnitType :
          referenceService.getMeasurementUnitTypes(localeId)) {

        writer.println(
            "    <insert schemaName=\"reference\" tableName=\"measurement_unit_types\">");
        writer.println(
            "      <column name=\"code\" value=\"" + measurementUnitType.getCode() + "\"/>");
        writer.println(
            "      <column name=\"locale_id\" value=\""
                + measurementUnitType.getLocaleId()
                + "\"/>");

        if (measurementUnitType.getSortIndex() != null) {
          writer.println(
              "      <column name=\"sort_index\" value=\""
                  + measurementUnitType.getSortIndex()
                  + "\"/>");
        }

        writer.println(
            "      <column name=\"name\" value=\"" + measurementUnitType.getName() + "\"/>");
        writer.println(
            "      <column name=\"description\" value=\""
                + measurementUnitType.getDescription()
                + "\"/>");
        writer.println("    </insert>");
      }

      writer.println();

      // Measurement Units
      for (MeasurementUnit measurementUnit : referenceService.getMeasurementUnits(localeId)) {

        writer.println("    <insert schemaName=\"reference\" tableName=\"measurement_units\">");
        writer.println("      <column name=\"code\" value=\"" + measurementUnit.getCode() + "\"/>");
        writer.println(
            "      <column name=\"locale_id\" value=\"" + measurementUnit.getLocaleId() + "\"/>");

        if (measurementUnit.getSortIndex() != null) {
          writer.println(
              "      <column name=\"sort_index\" value=\""
                  + measurementUnit.getSortIndex()
                  + "\"/>");
        }

        writer.println("      <column name=\"name\" value=\"" + measurementUnit.getName() + "\"/>");
        writer.println(
            "      <column name=\"description\" value=\""
                + measurementUnit.getDescription()
                + "\"/>");
        writer.println(
            "      <column name=\"system\" value=\"" + measurementUnit.getSystem() + "\"/>");
        writer.println("      <column name=\"type\" value=\"" + measurementUnit.getType() + "\"/>");
        writer.println("    </insert>");
      }

      writer.println();

      // Regions
      for (Region region : referenceService.getRegions(localeId)) {

        writer.println("    <insert schemaName=\"reference\" tableName=\"regions\">");
        writer.println("      <column name=\"country\" value=\"" + region.getCountry() + "\"/>");
        writer.println("      <column name=\"code\" value=\"" + region.getCode() + "\"/>");
        writer.println("      <column name=\"locale_id\" value=\"" + region.getLocaleId() + "\"/>");

        if (region.getSortIndex() != null) {
          writer.println(
              "      <column name=\"sort_index\" value=\"" + region.getSortIndex() + "\"/>");
        }

        writer.println("      <column name=\"name\" value=\"" + region.getName() + "\"/>");
        writer.println(
            "      <column name=\"description\" value=\"" + region.getDescription() + "\"/>");
        writer.println("    </insert>");
      }

      writer.println();

      writer.println();
    }
  }
}
