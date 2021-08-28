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
import java.sql.Connection;
import java.sql.ConnectionBuilder;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.ShardingKeyBuilder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * The <b>DataSourceProxy</b> class provides a proxy that tracks the JDBC connections associated
 * with the current thread and managed by a <b>javax.sql.DataSource</b> implementation.
 *
 * @author Marcus Portmann
 */
public class DataSourceProxy implements DataSource {

  /** The active database connections associated with the current thread. */
  private static final ThreadLocal<Map<Connection, StackTraceElement[]>> activeDatabaseConnections =
      ThreadLocal.withInitial(ConcurrentHashMap::new);

  /** The data source. */
  private final DataSource dataSource;

  /**
   * Constructs a new <b>DataSourceProxy</b>.
   *
   * @param dataSource the data source
   */
  public DataSourceProxy(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * Add the database connection to the tracked active database connections associated with the
   * current thread.
   *
   * @param connection the connection
   */
  public static void addActiveDatabaseConnection(Connection connection) {
    getActiveDatabaseConnections().put(connection, Thread.currentThread().getStackTrace());
  }

  /**
   * Returns the active database connections for all Data Sources associated with the tracker.
   *
   * @return the active database connections for all Data Sources associated with the tracker
   */
  public static Map<Connection, StackTraceElement[]> getActiveDatabaseConnections() {
    return activeDatabaseConnections.get();
  }

  @Override
  public ConnectionBuilder createConnectionBuilder() throws SQLException {
    return dataSource.createConnectionBuilder();
  }

  @Override
  public ShardingKeyBuilder createShardingKeyBuilder() throws SQLException {
    return dataSource.createShardingKeyBuilder();
  }

  @Override
  public Connection getConnection() throws SQLException {
    Connection connection = new ConnectionProxy(dataSource.getConnection());

    addActiveDatabaseConnection(connection);

    return connection;
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    Connection connection = new ConnectionProxy(dataSource.getConnection(username, password));

    addActiveDatabaseConnection(connection);

    return connection;
  }

  @Override
  public PrintWriter getLogWriter() throws SQLException {
    return dataSource.getLogWriter();
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    return dataSource.getLoginTimeout();
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return dataSource.getParentLogger();
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return iface.isAssignableFrom(getClass());
  }

  @Override
  public void setLogWriter(PrintWriter out) throws SQLException {
    dataSource.setLogWriter(out);
  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {
    dataSource.setLoginTimeout(seconds);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    if (isWrapperFor(iface)) {
      return (T) this;
    }

    if (iface.isAssignableFrom(this.dataSource.getClass())) {
      return (T) this.dataSource;
    }

    throw new SQLException(getClass() + " is not a wrapper for " + iface);
  }
}
