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

package africa.absa.inception.jpa;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.FatalBeanException;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * The <b>JpaUtil</b> class provides JPA-related utility methods.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings("unused")
public class JpaUtil {

  /**
   * Create a new entity manager.
   *
   * @param persistenceUnitName the name of the persistence unit
   * @param dataSource the data source
   * @param platformTransactionManager the Spring platform transaction manager
   * @param packagesToScan the packages to scan for entities
   * @return the entity manager
   */
  public static LocalContainerEntityManagerFactoryBean createEntityManager(
      String persistenceUnitName,
      DataSource dataSource,
      PlatformTransactionManager platformTransactionManager,
      String... packagesToScan) {
    try {
      LocalContainerEntityManagerFactoryBean entityManagerFactoryBean =
          new LocalContainerEntityManagerFactoryBean();

      entityManagerFactoryBean.setPersistenceUnitName(persistenceUnitName);

      entityManagerFactoryBean.setJtaDataSource(dataSource);

      entityManagerFactoryBean.setPackagesToScan(packagesToScan);

      // TODO: Detect the JPA vendor and create the correct adapter -- MARCUS
      HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
      jpaVendorAdapter.setGenerateDdl(false);

      try (Connection connection = dataSource.getConnection()) {
        DatabaseMetaData metaData = connection.getMetaData();

        switch (metaData.getDatabaseProductName()) {
          case "H2":
            jpaVendorAdapter.setDatabase(Database.H2);
            jpaVendorAdapter.setShowSql(true);

            break;

          case "Microsoft SQL Server":
            jpaVendorAdapter.setDatabase(Database.SQL_SERVER);
            jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.SQLServer2012Dialect");
            jpaVendorAdapter.setShowSql(false);

            break;

          default:
            jpaVendorAdapter.setDatabase(Database.DEFAULT);
            jpaVendorAdapter.setShowSql(false);

            break;
        }
      }

      entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);

      Map<String, Object> jpaPropertyMap = entityManagerFactoryBean.getJpaPropertyMap();

      if (platformTransactionManager instanceof JtaTransactionManager) {
        jpaPropertyMap.put("hibernate.transaction.coordinator_class", "jta");
        jpaPropertyMap.put("hibernate.transaction.jta.platform", "JBossTS");
      }

      return entityManagerFactoryBean;
    } catch (Throwable e) {
      throw new FatalBeanException(
          "Failed to create the entity manager factory bean for the persistence unit ("
              + persistenceUnitName
              + ")",
          e);
    }
  }
}
