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

import java.util.Optional;

/**
 * The <b>ITestTransactionalService</b> interface defines the functionality provided by a Test
 * Transactional Service implementation.
 *
 * @author Marcus Portmann
 */
public interface ITestTransactionalService {

  /**
   * Create the new test data.
   *
   * @param testData the test data
   */
  void createTestData(TestData testData) throws TestTransactionalServiceException;

  /**
   * Create the new test data in a new transaction.
   *
   * @param testData the test data
   */
  void createTestDataInNewTransaction(TestData testData) throws TestTransactionalServiceException;

  /**
   * Create the new test data in a new transaction with a checked exception.
   *
   * @param testData the test data
   */
  void createTestDataInNewTransactionWithCheckedException(TestData testData)
      throws TestTransactionalServiceException;

  /**
   * Create the new test data in a new transaction with a runtime exception.
   *
   * @param testData the test data
   */
  void createTestDataInNewTransactionWithRuntimeException(TestData testData)
      throws TestTransactionalServiceException;

  /**
   * Create the new test data with a checked exception.
   *
   * @param testData the test data
   */
  void createTestDataWithCheckedException(TestData testData)
      throws TestTransactionalServiceException;

  /**
   * Create the new test data with a runtime exception.
   *
   * @param testData the test data
   */
  void createTestDataWithRuntimeException(TestData testData)
      throws TestTransactionalServiceException;

  /**
   * Retrieve the test data.
   *
   * @param id the ID
   * @return an Optional containing the test data or an empty Optional if the test data cannot be
   *     found
   */
  Optional<TestData> getTestData(String id) throws TestTransactionalServiceException;
}
