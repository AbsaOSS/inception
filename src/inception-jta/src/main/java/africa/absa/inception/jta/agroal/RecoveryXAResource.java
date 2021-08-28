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
import io.agroal.api.transaction.TransactionIntegration.ResourceRecoveryFactory;
import java.sql.SQLException;
import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/**
 * The <b>RecoveryXAResource</b> class provides a closeable wrapper around the XAResource associated
 * with an XAConnection that keeps track of the lifecycle of the XAResource and closes the
 * connection when required.
 *
 * @author Marcus Portmann
 */
public class RecoveryXAResource implements AutoCloseable, XAResource {

  private final ResourceRecoveryFactory resourceRecoveryFactory;

  private XAConnection xaConnection;

  private XAResource xaResource;

  public RecoveryXAResource(ResourceRecoveryFactory resourceRecoveryFactory) {
    this.resourceRecoveryFactory = resourceRecoveryFactory;
  }

  @Override
  public void close() throws XAException {
    try {
      if (xaConnection != null) {
        xaConnection.close();
      }
    } catch (SQLException e) {
      XAExceptionUtil.xaException(XAException.XAER_RMFAIL, "Failed to close the XAConnection", e);
    } finally {
      xaConnection = null;
      xaResource = null;
    }
  }

  @Override
  public void commit(Xid xid, boolean onePhase) throws XAException {
    xaResource.commit(xid, onePhase);
  }

  @Override
  public void end(Xid xid, int flags) throws XAException {
    xaResource.end(xid, flags);
  }

  @Override
  public void forget(Xid xid) throws XAException {
    xaResource.forget(xid);
  }

  @Override
  public int getTransactionTimeout() throws XAException {
    return xaResource.getTransactionTimeout();
  }

  @Override
  public boolean isSameRM(XAResource xaResource) throws XAException {
    if (xaResource instanceof RecoveryXAResource) {
      return this.xaResource.isSameRM(((RecoveryXAResource) xaResource).xaResource);
    } else {
      return this.xaResource.isSameRM(xaResource);
    }
  }

  @Override
  public int prepare(Xid xid) throws XAException {
    return xaResource.prepare(xid);
  }

  @Override
  public Xid[] recover(int flag) throws XAException {
    if (flag == TMSTARTRSCAN) {
      try {
        xaConnection = resourceRecoveryFactory.getRecoveryConnection();
        xaResource = xaConnection.getXAResource();
      } catch (SQLException e) {
        XAExceptionUtil.xaException(
            XAException.XAER_RMFAIL,
            "Failed to retrieve the recovery XAConnection from the ResourceRecoveryFactory",
            e);
      }
    }

    if (xaConnection == null) {
      throw new XAException(XAException.XAER_RMFAIL);
    }
    Xid[] value = xaResource.recover(flag);

    if (flag == TMENDRSCAN && (value == null || value.length == 0)) {
      close();
    }
    return value;
  }

  @Override
  public void rollback(Xid xid) throws XAException {
    xaResource.rollback(xid);
  }

  @Override
  public boolean setTransactionTimeout(int seconds) throws XAException {
    return xaResource.setTransactionTimeout(seconds);
  }

  @Override
  public void start(Xid xid, int flags) throws XAException {
    xaResource.start(xid, flags);
  }
}
