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

package africa.absa.inception.jta.narayana;

import africa.absa.inception.jta.agroal.NarayanaTransactionIntegration;
import com.arjuna.ats.arjuna.coordinator.TxControl;
import com.arjuna.ats.arjuna.recovery.RecoveryManager;
import io.agroal.api.transaction.TransactionIntegration;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * The <b>NarayanaFactory</b> class produces the JTA transaction management beans for the
 * Narayana transactions toolkit.
 *
 * @author Marcus Portmann
 * @see <a href="https://narayana.io">Naryana</a>
 */
@Component
@SuppressWarnings("unused")
public class NarayanaFactory {

  static {
    TxControl.setXANodeName(nodeName());
  }

  /**
   * Retrieve the XA node name.
   *
   * @return the XA node name
   */
  private static String nodeName() {
    try {
      java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();

      return localMachine.getHostName().toLowerCase();
    } catch (Throwable e) {
      return "Unknown";
    }
  }

  /**
   * Returns the Narayana recovery manager.
   *
   * @return the Narayana recovery manager
   */
  @Bean
  public RecoveryManager narayanaRecoveryManager() {
    RecoveryManager recoveryManager = RecoveryManager.manager();

    recoveryManager.initialize();

    return RecoveryManager.manager();
  }

  /**
   * Returns the Narayana transaction integration.
   *
   * @param transactionManager the transaction manager
   * @param transactionSynchronizationRegistry the transaction synchronization registry
   * @param recoveryManager the recovery manager
   * @return the Narayana transaction integration
   */
  @Bean
  public TransactionIntegration narayanaTransactionIntegration(
      TransactionManager transactionManager,
      TransactionSynchronizationRegistry transactionSynchronizationRegistry,
      RecoveryManager recoveryManager) {
    return new NarayanaTransactionIntegration(
        transactionManager, transactionSynchronizationRegistry, recoveryManager);
  }

  /**
   * Returns the Narayana JTA transaction manager.
   *
   * @return the Narayana JTA transaction manager
   */
  @Bean
  public TransactionManager narayanaTransactionManager() {
    return com.arjuna.ats.jta.TransactionManager.transactionManager();
  }

  /**
   * Returns the Narayana JTA transaction synchronization registry.
   *
   * @param transactionManager the transaction manager
   * @return the Narayana JTA transaction synchronization registry
   */
  @Bean
  public TransactionSynchronizationRegistry narayanaTransactionSynchronizationRegistry(
      TransactionManager transactionManager) {
    return new com.arjuna.ats.internal.jta.transaction.arjunacore
        .TransactionSynchronizationRegistryImple();
  }

  /**
   * Returns the Narayana JTA user transaction.
   *
   * @return the Narayana JTA user transaction
   */
  @Bean
  public UserTransaction narayanaUserTransaction() {
    return com.arjuna.ats.jta.UserTransaction.userTransaction();
  }

  /**
   * Returns the Spring JTA platform transaction manager.
   *
   * @param userTransaction the user transaction
   * @param transactionManager the transaction manager
   * @return the Spring JTA platform transaction manager
   */
  @Bean
  public PlatformTransactionManager transactionManager(
      UserTransaction userTransaction, TransactionManager transactionManager) {
    try {
      return new JtaTransactionManager(userTransaction, transactionManager);
    } catch (Throwable e) {
      throw new RuntimeException("Failed to initialize the JTA transaction manager");
    }
  }
}
