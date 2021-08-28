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

import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.ShardingKeyBuilder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import javax.sql.XAConnection;
import javax.sql.XAConnectionBuilder;
import javax.sql.XADataSource;

/**
 * The <b>DataSourceProxy</b> class provides a proxy that tracks the JDBC connections associated
 * with the current thread and managed by a <b>javax.sql.XADataSource</b> implementation.
 *
 * @author Marcus Portmann
 */
public class XADataSourceProxy implements XADataSource {

  /** The active XA database connections associated with the current thread. */
  private static final ThreadLocal<Map<XAConnection, StackTraceElement[]>>
      activeXADatabaseConnections = ThreadLocal.withInitial(ConcurrentHashMap::new);

  /** The XA data source. */
  private final XADataSource xaDataSource;

  /**
   * Constructs a new <b>XADataSourceProxy</b>.
   *
   * @param xaDataSource the XA data source
   */
  public XADataSourceProxy(XADataSource xaDataSource) {
    this.xaDataSource = xaDataSource;
  }

  /**
   * Add the XA database connection to the tracked active XA database connections associated with
   * the current thread.
   *
   * @param connection the connection
   */
  public static void addActiveXADatabaseConnection(XAConnection connection) {
    getActiveXADatabaseConnections().put(connection, Thread.currentThread().getStackTrace());
  }

  /**
   * Returns the active XA database connections for all Data Sources associated with the tracker.
   *
   * @return the active XA database connections for all Data Sources associated with the tracker
   */
  public static Map<XAConnection, StackTraceElement[]> getActiveXADatabaseConnections() {
    return activeXADatabaseConnections.get();
  }

  @Override
  public ShardingKeyBuilder createShardingKeyBuilder() throws SQLException {
    return xaDataSource.createShardingKeyBuilder();
  }

  @Override
  public XAConnectionBuilder createXAConnectionBuilder() throws SQLException {
    return xaDataSource.createXAConnectionBuilder();
  }

  @Override
  public PrintWriter getLogWriter() throws SQLException {
    return xaDataSource.getLogWriter();
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    return xaDataSource.getLoginTimeout();
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return xaDataSource.getParentLogger();
  }

  @Override
  public XAConnection getXAConnection() throws SQLException {
    XAConnection xaConnection = new XAConnectionProxy(xaDataSource.getXAConnection());

    addActiveXADatabaseConnection(xaConnection);

    return xaConnection;
  }

  @Override
  public XAConnection getXAConnection(String user, String password) throws SQLException {
    XAConnection xaConnection = new XAConnectionProxy(xaDataSource.getXAConnection(user, password));

    addActiveXADatabaseConnection(xaConnection);

    return xaConnection;
  }

  @Override
  public void setLogWriter(PrintWriter out) throws SQLException {
    xaDataSource.setLogWriter(out);
  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {
    xaDataSource.setLoginTimeout(seconds);
  }
}
