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

package africa.absa.inception.jta.agroal;

import africa.absa.inception.jta.util.TransactionUtil;
import com.arjuna.ats.arjuna.recovery.RecoveryManager;
import com.arjuna.ats.internal.jta.recovery.arjunacore.XARecoveryModule;
import com.arjuna.ats.jta.recovery.XAResourceRecoveryHelper;
import io.agroal.api.transaction.TransactionAware;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.transaction.Synchronization;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.xa.XAResource;

/**
 * The <b>NarayanaTransactionIntegration</b> class.
 *
 * @author Marcus Portmann
 */
public class NarayanaTransactionIntegration
    implements io.agroal.api.transaction.TransactionIntegration {

  private static final ConcurrentMap<ResourceRecoveryFactory, XAResourceRecoveryHelperImpl>
      xaResourceRecoveryHelperImplCache = new ConcurrentHashMap<>();

  // In order to construct a UID that is globally unique, simply pair a UID with an InetAddress.
  private final UUID key = UUID.randomUUID();

  private final RecoveryManager recoveryManager;

  private final TransactionManager transactionManager;

  private final TransactionSynchronizationRegistry transactionSynchronizationRegistry;

  /**
   * Constructs a new <b>NarayanaTransactionIntegration</b>.
   *
   * @param transactionManager the transaction manager
   * @param transactionSynchronizationRegistry the transaction synchronization registry
   */
  public NarayanaTransactionIntegration(
      TransactionManager transactionManager,
      TransactionSynchronizationRegistry transactionSynchronizationRegistry) {
    this(transactionManager, transactionSynchronizationRegistry, null);
  }

  /**
   * Constructs a new <b>NarayanaTransactionIntegration</b>.
   *
   * @param transactionManager the transaction manager
   * @param transactionSynchronizationRegistry the transaction synchronization registry
   * @param recoveryManager the recovery manager
   */
  public NarayanaTransactionIntegration(
      TransactionManager transactionManager,
      TransactionSynchronizationRegistry transactionSynchronizationRegistry,
      RecoveryManager recoveryManager) {
    this.transactionManager = transactionManager;
    this.transactionSynchronizationRegistry = transactionSynchronizationRegistry;
    this.recoveryManager = recoveryManager;
  }

  @Override
  public void addResourceRecoveryFactory(ResourceRecoveryFactory resourceRecoveryFactory) {
    XARecoveryModule xaRecoveryModule =
        (XARecoveryModule)
            recoveryManager.getModules().stream()
                .filter(recoveryModule -> recoveryModule instanceof XARecoveryModule)
                .findFirst()
                .orElse(null);

    if (xaRecoveryModule == null) {
      throw new IllegalStateException(
          "Failed to retrieve the XARecoveryModule from the Narayana Recovery Manager");
    }

    xaRecoveryModule.addXAResourceRecoveryHelper(
        xaResourceRecoveryHelperImplCache.computeIfAbsent(
            resourceRecoveryFactory,
            rrf -> new XAResourceRecoveryHelperImpl(new RecoveryXAResource(rrf))));
  }

  @Override
  public void associate(TransactionAware transactionAware, XAResource xaResource)
      throws SQLException {
    try {
      if (transactionRunning()) {
        if (transactionSynchronizationRegistry.getResource(key) == null) {
          transactionSynchronizationRegistry.registerInterposedSynchronization(
              new InterposedSynchronization(transactionAware));
          transactionSynchronizationRegistry.putResource(key, transactionAware);

          XAResource xaResourceToEnlist;
          if (xaResource != null) {
            xaResourceToEnlist = new TransactionAwareXAResource(transactionAware, xaResource);
          } else {
            xaResourceToEnlist = new LocalXAResource(transactionAware);
          }
          transactionManager.getTransaction().enlistResource(xaResourceToEnlist);
        } else {
          transactionAware.transactionStart();
        }
      }
      transactionAware.transactionCheckCallback(this::transactionRunning);
    } catch (Exception e) {
      throw new SQLException("Failed to associate the connection with an existing transaction", e);
    }
  }

  @Override
  public boolean disassociate(TransactionAware transactionAware) {
    if (transactionRunning()) {
      transactionSynchronizationRegistry.putResource(key, null);
    }

    return true;
  }

  @Override
  public TransactionAware getTransactionAware() {
    if (transactionRunning()) {
      return (TransactionAware) transactionSynchronizationRegistry.getResource(key);
    }
    return null;
  }

  @Override
  public void removeResourceRecoveryFactory(ResourceRecoveryFactory resourceRecoveryFactory) {
    XARecoveryModule xaRecoveryModule =
        (XARecoveryModule)
            recoveryManager.getModules().stream()
                .filter(recoveryModule -> recoveryModule instanceof XARecoveryModule)
                .findFirst()
                .orElse(null);

    if (xaRecoveryModule == null) {
      throw new IllegalStateException(
          "Failed to retrieve the XARecoveryModule from the Narayana Recovery Manager");
    }

    xaRecoveryModule.removeXAResourceRecoveryHelper(
        xaResourceRecoveryHelperImplCache.remove(resourceRecoveryFactory));
  }

  private boolean transactionRunning() {
    return TransactionUtil.transactionExists(transactionManager);
  }

  private static class InterposedSynchronization implements Synchronization {

    private final TransactionAware transactionAware;

    InterposedSynchronization(TransactionAware transactionAware) {
      this.transactionAware = transactionAware;
    }

    public void afterCompletion(int status) {
      try {
        transactionAware.transactionEnd();
      } catch (Throwable ignored) {
      }
    }

    public void beforeCompletion() {}
  }

  /**
   * The <b>XAResourceRecoveryImpl</b> class provides an implementation of the the
   * XAResourceRecoveryHelper interface.
   *
   * @author Marcus Portmann
   */
  private static class XAResourceRecoveryHelperImpl implements XAResourceRecoveryHelper {

    private final XAResource[] xaResources;

    public XAResourceRecoveryHelperImpl(XAResource xaResource) {
      this.xaResources = new XAResource[1];
      this.xaResources[0] = xaResource;
    }

    @Override
    public XAResource[] getXAResources() {
      return xaResources;
    }

    @Override
    public boolean initialise(String p) {
      return true;
    }
  }
}
