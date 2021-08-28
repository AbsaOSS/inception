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

package africa.absa.inception.jta.util;

import javax.transaction.Status;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import org.springframework.transaction.TransactionSystemException;

/**
 * The <b>TransactionUtil</b> class provides a helper class for working with transactions.
 *
 * @author Marcus Portmann
 */
public class TransactionUtil {

  /**
   * Check whether there is an existing JTA transaction.
   *
   * @param transactionManager the JTA transaction manager
   * @return true if there is an existing JTA transaction or false otherwise
   */
  public static boolean transactionExists(TransactionManager transactionManager) {
    try {
      Transaction transaction = transactionManager.getTransaction();
      if (transaction == null) {
        return false;
      }
      int status = transaction.getStatus();
      return ((status != Status.STATUS_UNKNOWN)
          && (status != Status.STATUS_NO_TRANSACTION)
          && (status != Status.STATUS_COMMITTED)
          && (status != Status.STATUS_ROLLEDBACK));
      // other states are active transaction: ACTIVE, MARKED_ROLLBACK, PREPARING, PREPARED,
      // COMMITTING, ROLLING_BACK
    } catch (Exception e) {
      throw new TransactionSystemException(
          "Failed to check for an existing active JTA transaction", e);
    }
  }
}
