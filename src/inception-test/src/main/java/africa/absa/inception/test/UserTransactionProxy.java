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

package africa.absa.inception.test;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The <b>UserTransactionProxy</b> class provides a proxy that tracks the Java Transaction (JTA) API
 * transactions associated with the current thread and managed by a <b>
 * javax.transaction.UserTransaction</b> implementation.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings({"unused"})
public class UserTransactionProxy implements UserTransaction {

  /** The stack traces for the active transactions associated with the current thread. */
  private static final ThreadLocal<Map<Transaction, StackTraceElement[]>>
      activeTransactionStackTraces = ThreadLocal.withInitial(ConcurrentHashMap::new);

  /* Logger */
  private static final Logger logger = LoggerFactory.getLogger(UserTransactionProxy.class);

  /** The JTA transaction manager. */
  private final TransactionManager transactionManager;

  /** The JTA user transaction. */
  private final UserTransaction userTransaction;

  /**
   * Constructs a new <b>UserTransactionProxy</b>.
   *
   * @param userTransaction the JTA user transaction
   * @param transactionManager the JTA transaction manager
   */
  public UserTransactionProxy(
      UserTransaction userTransaction, TransactionManager transactionManager) {
    this.transactionManager = transactionManager;
    this.userTransaction = userTransaction;
  }

  /**
   * Returns the active transaction stack traces for the current thread.
   *
   * @return the active transaction stack traces for the current thread
   */
  static Map<Transaction, StackTraceElement[]> getActiveTransactionStackTraces() {
    return activeTransactionStackTraces.get();
  }

  @Override
  public void begin() throws NotSupportedException, SystemException {
    try {
      userTransaction.begin();
    } finally {
      Optional<Transaction> afterTransactionOptional = getCurrentTransaction();

      if (afterTransactionOptional.isPresent()) {
        getActiveTransactionStackTraces()
            .put(afterTransactionOptional.get(), Thread.currentThread().getStackTrace());
      }
    }
  }

  @Override
  public void commit()
      throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
          SecurityException, IllegalStateException, SystemException {
    Optional<Transaction> beforeTransactionOptional = getCurrentTransaction();

    try {
      userTransaction.commit();
    } finally {
      Optional<Transaction> afterTransactionOptional = getCurrentTransaction();

      if (beforeTransactionOptional.isPresent() && (afterTransactionOptional.isEmpty())) {
        getActiveTransactionStackTraces().remove(beforeTransactionOptional.get());
      }
    }
  }

  @Override
  public int getStatus() throws SystemException {
    return userTransaction.getStatus();
  }

  @Override
  public void rollback() throws IllegalStateException, SecurityException, SystemException {
    Optional<Transaction> beforeTransactionOptional = getCurrentTransaction();

    try {
      userTransaction.rollback();
    } finally {
      Optional<Transaction> afterTransactionOptional = getCurrentTransaction();

      if (beforeTransactionOptional.isPresent() && (afterTransactionOptional.isEmpty())) {
        getActiveTransactionStackTraces().remove(beforeTransactionOptional.get());
      }
    }
  }

  @Override
  public void setRollbackOnly() throws IllegalStateException, SystemException {
    userTransaction.setRollbackOnly();
  }

  @Override
  public void setTransactionTimeout(int i) throws SystemException {
    userTransaction.setTransactionTimeout(i);
  }

  /**
   * Returns the current transaction.
   *
   * @return an Optional containing the current transaction or an empty Optional if there is no
   *     current transaction
   */
  private Optional<Transaction> getCurrentTransaction() {
    try {
      return Optional.ofNullable(transactionManager.getTransaction());
    } catch (Throwable e) {
      logger.error("Failed to retrieve the current transaction", e);

      return Optional.empty();
    }
  }
}
