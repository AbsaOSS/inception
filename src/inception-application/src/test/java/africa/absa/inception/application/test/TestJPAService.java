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

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * The <b>TestJPAService</b> class provides the Test JPA Service implementation.
 *
 * @author Marcus Portmann
 */
@Service
@SuppressWarnings("JpaQlInspection")
public class TestJPAService implements ITestJPAService {

  /** The Spring platform transaction manager. */
  private final PlatformTransactionManager transactionManager;

  /* Entity Manager */
  @PersistenceContext(unitName = "applicationTest")
  private EntityManager entityManager;

  /**
   * Constructs a new <b>TestJPAService</b>.
   *
   * @param platformTransactionManager the Spring platform transaction manager
   */
  public TestJPAService(PlatformTransactionManager platformTransactionManager) {
    this.transactionManager = platformTransactionManager;
  }

  /**
   * Create the new test data.
   *
   * @param testData the test data
   */
  @Transactional
  public void createTestData(TestData testData) throws TestJPAServiceException {
    try {
      if (!entityManager.contains(testData)) {
        testData = entityManager.merge(testData);
        entityManager.flush();
        entityManager.detach(testData);
      }
    } catch (Throwable e) {
      throw new TestJPAServiceException(
          "Failed to create the test data with ID (" + testData.getId() + ")", e);
    }
  }

  /**
   * Create the new test data in a new transaction.
   *
   * @param testData the test data
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void createTestDataInNewTransaction(TestData testData) throws TestJPAServiceException {
    try {
      if (!entityManager.contains(testData)) {
        testData = entityManager.merge(testData);
        entityManager.flush();
        entityManager.detach(testData);
      }
    } catch (Throwable e) {
      throw new TestJPAServiceException(
          "Failed to create the test data with ID (" + testData.getId() + ")", e);
    }
  }

  /**
   * Create the new test data in a new transaction with a checked exception.
   *
   * @param testData the test data
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void createTestDataInNewTransactionWithCheckedException(TestData testData)
      throws TestJPAServiceException {
    createTestData(testData);

    throw new TestJPAServiceException("Failed with a checked exception in a new transaction");
  }

  /**
   * Create the new test data in a new transaction with a runtime exception.
   *
   * @param testData the test data
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void createTestDataInNewTransactionWithRuntimeException(TestData testData)
      throws TestJPAServiceException {
    createTestData(testData);

    throw new RuntimeException("Failed with a runtime exception in a new transaction");
  }

  /**
   * Create the new test data with a checked exception.
   *
   * @param testData the test data
   */
  @Transactional
  public void createTestDataWithCheckedException(TestData testData) throws TestJPAServiceException {
    createTestData(testData);

    throw new TestJPAServiceException("Failed with a checked exception in an existing transaction");
  }

  /**
   * Create the new test data with a runtime exception.
   *
   * @param testData the test data
   */
  @Transactional
  public void createTestDataWithRuntimeException(TestData testData) throws TestJPAServiceException {
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
  @Transactional
  public Optional<TestData> getTestData(String id) throws TestJPAServiceException {
    try {
      String getTestDataSQL = "SELECT td FROM TestData td WHERE td.id = :id";

      TypedQuery<TestData> query = entityManager.createQuery(getTestDataSQL, TestData.class);

      query.setParameter("id", id);

      List<TestData> testData = query.getResultList();

      if (testData.size() == 0) {
        return Optional.empty();
      } else {
        return Optional.of(testData.get(0));
      }
    } catch (Throwable e) {
      throw new TestJPAServiceException("Failed to retrieve the test data (" + id + ")", e);
    }
  }

  /**
   * Retrieve the test data without a transaction.
   *
   * @param id the ID
   * @return an Optional containing the test data or an empty Optional if the test data cannot be
   *     found
   */
  public Optional<TestData> getTestDataWithoutTransaction(String id)
      throws TestJPAServiceException {
    try {
      String getTestDataSQL = "SELECT td FROM TestData td WHERE td.id = :id";

      TypedQuery<TestData> query = entityManager.createQuery(getTestDataSQL, TestData.class);

      query.setParameter("id", id);

      List<TestData> testData = query.getResultList();

      if (testData.size() == 0) {
        return Optional.empty();
      } else {
        return Optional.of(testData.get(0));
      }
    } catch (Throwable e) {
      throw new TestJPAServiceException("Failed to retrieve the test data (" + id + ")", e);
    }
  }
}
