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

package africa.absa.inception.application.test;

import africa.absa.inception.test.DataSourceProxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * The <b>TestTransactionalService</b> class provides the Test Transactional Service implementation.
 *
 * @author Marcus Portmann
 */
@Service
@Transactional
@SuppressWarnings("unused")
public class TestTransactionalService implements ITestTransactionalService {

  /** The data source used to provide connections to the application database. */
  private final DataSource dataSource;

  /**
   * Constructs a new <b>TestTransactionalService</b>.
   *
   * @param dataSource the data source used to provide connections to the application database
   */
  public TestTransactionalService(@Qualifier("applicationDataSource") DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * Create the new test data.
   *
   * @param testData the test data
   */
  public void createTestData(TestData testData) throws TestTransactionalServiceException {
    String createTestDataSQL = "INSERT INTO TEST.TEST_DATA  (ID, NAME, VALUE) VALUES (?, ?, ?)";

    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(createTestDataSQL)) {
      statement.setString(1, testData.getId());
      statement.setString(2, testData.getName());
      statement.setString(3, testData.getValue());

      Set<Connection> connections = DataSourceProxy.getActiveDatabaseConnections().keySet();

      Map<Connection, StackTraceElement[]> connectionMap =
          DataSourceProxy.getActiveDatabaseConnections();

      if (statement.executeUpdate() != 1) {
        throw new RuntimeException(
            "No rows were affected as a result of executing the SQL statement ("
                + createTestDataSQL
                + ")");
      }
    } catch (Throwable e) {
      throw new TestTransactionalServiceException("Failed to create the test data", e);
    }
  }

  /**
   * Create the new test data in a new transaction.
   *
   * @param testData the test data
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void createTestDataInNewTransaction(TestData testData)
      throws TestTransactionalServiceException {
    String createTestDataSQL = "INSERT INTO TEST.TEST_DATA  (ID, NAME, VALUE) VALUES (?, ?, ?)";

    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(createTestDataSQL)) {
      statement.setString(1, testData.getId());
      statement.setString(2, testData.getName());
      statement.setString(3, testData.getValue());

      if (statement.executeUpdate() != 1) {
        throw new RuntimeException(
            "No rows were affected as a result of executing the SQL statement ("
                + createTestDataSQL
                + ")");
      }
    } catch (Throwable e) {
      throw new TestTransactionalServiceException(
          "Failed to create the test data in a new transaction", e);
    }
  }

  /**
   * Create the new test data in a new transaction with a checked exception.
   *
   * @param testData the test data
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void createTestDataInNewTransactionWithCheckedException(TestData testData)
      throws TestTransactionalServiceException {
    createTestData(testData);

    throw new TestTransactionalServiceException(
        "Failed with a checked exception in a new transaction");
  }

  /**
   * Create the new test data in a new transaction with a runtime exception.
   *
   * @param testData the test data
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void createTestDataInNewTransactionWithRuntimeException(TestData testData)
      throws TestTransactionalServiceException {
    createTestData(testData);

    throw new RuntimeException("Failed with a runtime exception in a new transaction");
  }

  /**
   * Create the new test data with a checked exception.
   *
   * @param testData the test data
   */
  public void createTestDataWithCheckedException(TestData testData)
      throws TestTransactionalServiceException {
    createTestData(testData);

    throw new TestTransactionalServiceException(
        "Failed with a checked exception in an existing transaction");
  }

  /**
   * Create the new test data with a runtime exception.
   *
   * @param testData the test data
   */
  public void createTestDataWithRuntimeException(TestData testData)
      throws TestTransactionalServiceException {
    createTestData(testData);

    throw new RuntimeException("Failed with a runtime exception in an existing transaction");
  }

  /**
   * Retrieve the test data.
   *
   * @param id the ID
   * @return an Optional containing the test data or an empty Optional if the test data cannot be
   *     found
   */
  public Optional<TestData> getTestData(String id) throws TestTransactionalServiceException {
    String getTestDataSQL = "SELECT ID, NAME, VALUE FROM TEST.TEST_DATA WHERE ID=?";

    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(getTestDataSQL)) {
      statement.setString(1, id);

      try (ResultSet rs = statement.executeQuery()) {
        if (rs.next()) {
          return Optional.of(buildTestDataFromResultSet(rs));
        } else {
          return Optional.empty();
        }
      }
    } catch (Throwable e) {
      throw new TestTransactionalServiceException(
          "Failed to create the test data in a new transaction", e);
    }
  }

  private TestData buildTestDataFromResultSet(ResultSet rs) throws SQLException {
    return new TestData(rs.getString(1), rs.getString(2), rs.getString(3));
  }
}
