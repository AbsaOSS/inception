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

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;

/**
 * The <b>XAConnectionProxy</b> class implements a proxy for JDBC XA connections.
 *
 * @author Marcus Portmann
 */
public class XAConnectionProxy implements XAConnection {

  private final XAConnection xaConnection;

  XAConnectionProxy(XAConnection xaConnection) {
    this.xaConnection = xaConnection;
  }

  @Override
  public void addConnectionEventListener(ConnectionEventListener listener) {
    xaConnection.addConnectionEventListener(listener);
  }

  @Override
  public void addStatementEventListener(StatementEventListener listener) {
    xaConnection.addStatementEventListener(listener);
  }

  @Override
  public void close() throws SQLException {
    XADataSourceProxy.getActiveXADatabaseConnections().remove(this);

    xaConnection.close();
  }

  @Override
  public Connection getConnection() throws SQLException {
    Connection connection = new ConnectionProxy(xaConnection.getConnection());

    DataSourceProxy.addActiveDatabaseConnection(connection);

    return connection;
  }

  @Override
  public XAResource getXAResource() throws SQLException {
    return xaConnection.getXAResource();
  }

  @Override
  public void removeConnectionEventListener(ConnectionEventListener listener) {
    xaConnection.removeConnectionEventListener(listener);
  }

  @Override
  public void removeStatementEventListener(StatementEventListener listener) {
    xaConnection.removeStatementEventListener(listener);
  }
}
