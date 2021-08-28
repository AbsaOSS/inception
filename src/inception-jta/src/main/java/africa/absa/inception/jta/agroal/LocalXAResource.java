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

import africa.absa.inception.jta.util.XAExceptionUtil;
import io.agroal.api.transaction.TransactionAware;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/**
 * The <b>LocalXAResource</b> class provides an XAResource implementation that supports local
 * transaction behavior for an Agroal transaction aware connection resource associated with a non-XA
 * data source.
 *
 * @author Marcus Portmann
 */
public class LocalXAResource implements XAResource {

  /** The Agroal transaction aware connection resource. */
  private final TransactionAware transactionAware;

  /** The current transaction ID. */
  private Xid currentXid;

  /**
   * Constructs a new <b>LocalXAResource</b>.
   *
   * @param transactionAware the Agroal transaction aware connection resource
   */
  public LocalXAResource(TransactionAware transactionAware) {
    this.transactionAware = transactionAware;
  }

  @Override
  public void commit(Xid xid, boolean onePhase) throws XAException {
    if (xid == null || !xid.equals(currentXid)) {
      throw XAExceptionUtil.xaException(
          XAException.XAER_NOTA,
          "Failed to commit the transaction ("
              + currentXid
              + ") for the transaction aware connection resource using the invalid xid ("
              + xid
              + ")");
    }

    currentXid = null;
    try {
      transactionAware.transactionBeforeCompletion(true);
      transactionAware.transactionCommit();
    } catch (Throwable e) {
      transactionAware.setFlushOnly();
      throw XAExceptionUtil.xaException(
          onePhase ? XAException.XA_RBROLLBACK : XAException.XAER_RMERR,
          "Failed to commit the transaction ("
              + xid
              + ") for the transaction aware connection resource: "
              + e.getMessage(),
          e);
    }
  }

  @Override
  public void end(Xid xid, int flags) throws XAException {
    if (xid == null || !xid.equals(currentXid)) {
      transactionAware.setFlushOnly();
      throw XAExceptionUtil.xaException(
          XAException.XAER_NOTA,
          "Failed to end the transaction ("
              + currentXid
              + ") for the transaction aware connection resource using the invalid xid ("
              + xid
              + ")");
    }
  }

  @Override
  public void forget(Xid xid) throws XAException {
    transactionAware.setFlushOnly();
    throw XAExceptionUtil.xaException(
        XAException.XAER_NOTA,
        "Forget not supported for the transaction aware connection resource");
  }

  @Override
  public int getTransactionTimeout() {
    return 0;
  }

  @Override
  public boolean isSameRM(XAResource xaResource) {
    return this == xaResource;
  }

  @Override
  public int prepare(Xid xid) {
    return XA_OK;
  }

  @Override
  public Xid[] recover(int flag) throws XAException {
    transactionAware.setFlushOnly();
    throw XAExceptionUtil.xaException(
        XAException.XAER_RMERR,
        "Recover not supported for the transaction aware connection resource");
  }

  @Override
  public void rollback(Xid xid) throws XAException {
    if (xid == null || !xid.equals(currentXid)) {
      throw XAExceptionUtil.xaException(
          XAException.XAER_NOTA,
          "Failed to rollback the transaction ("
              + currentXid
              + ") for the transaction aware connection resource using the invalid xid ("
              + xid
              + ")");
    }

    currentXid = null;
    try {
      transactionAware.transactionBeforeCompletion(false);
      transactionAware.transactionRollback();
    } catch (Throwable e) {
      transactionAware.setFlushOnly();
      throw XAExceptionUtil.xaException(
          XAException.XAER_RMERR,
          "Failed to rollback the transaction ("
              + xid
              + ") for the transaction aware connection resource: "
              + e.getMessage(),
          e);
    }
  }

  @Override
  public boolean setTransactionTimeout(int seconds) {
    return false;
  }

  @Override
  public void start(Xid xid, int flags) throws XAException {
    if (currentXid == null) {
      if (flags != TMNOFLAGS) {
        throw XAExceptionUtil.xaException(
            XAException.XAER_INVAL,
            "Failed to start the transaction ("
                + xid
                + ") for the transaction aware connection resource using the invalid flags ("
                + flags
                + ")");
      }
      try {
        transactionAware.transactionStart();
      } catch (Throwable e) {
        transactionAware.setFlushOnly();
        throw XAExceptionUtil.xaException(
            XAException.XAER_RMERR,
            "Failed to start the transaction ("
                + xid
                + ") for the transaction aware connection resource: "
                + e.getMessage(),
            e);
      }
      currentXid = xid;
    } else {
      if (flags != TMJOIN && flags != TMRESUME) {
        throw XAExceptionUtil.xaException(
            XAException.XAER_DUPID,
            "Failed to join or resume the transaction ("
                + xid
                + ") for the transaction aware connection resource using the invalid flags ("
                + flags
                + ")");
      }
    }
  }
}
