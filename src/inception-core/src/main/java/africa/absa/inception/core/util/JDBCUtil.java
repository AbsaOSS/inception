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

package africa.absa.inception.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.util.StringUtils;

/**
 * The <b>JDBCUtil</b> class provides JDBCUtil utility functions.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class JDBCUtil {

  /** Private default constructor to enforce utility pattern. */
  private JDBCUtil() {}

  /**
   * Close the connection.
   *
   * @param connection the connection to close
   */
  public static void close(Connection connection) {
    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException e) {
        // Do nothing
      }
    }
  }

  /**
   * Close the result set.
   *
   * @param rs the result set to close
   */
  public static void close(ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
      } catch (SQLException e) {
        // Do nothing
      }
    }
  }

  /**
   * Close the statement.
   *
   * @param statement the statement to close
   */
  public static void close(Statement statement) {
    if (statement != null) {
      try {
        statement.close();
      } catch (SQLException e) {
        // Do nothing
      }
    }
  }

  /**
   * Execute the SQL statement using the database connection.
   *
   * @param connection the database connection to use
   * @param sql the SQL statement to execute
   * @return the row count
   * @throws SQLException if the statement execution failed
   */
  public static int executeStatement(Connection connection, String sql) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      return statement.executeUpdate(sql);
    } catch (Throwable e) {
      throw new SQLException("Failed to execute the SQL statement: " + sql, e);
    }
  }

  /**
   * Execute the SQL statements in the file with the specified resource path using the database
   * connection.
   *
   * @param connection the database connection to use
   * @param resourcePath the resource path to the file containing the SQL statements
   * @return the number of SQL statements successfully executed
   * @throws SQLException if the statement execution failed
   */
  public static int executeStatements(Connection connection, String resourcePath)
      throws SQLException {
    int numberOfStatementsExecuted = 0;

    try {
      List<String> sqlStatements =
          loadSQL(Thread.currentThread().getContextClassLoader().getResource(resourcePath));

      for (String sqlStatement : sqlStatements) {
        System.out.println("EXECUTING: " + sqlStatement);

        executeStatement(connection, sqlStatement);
        numberOfStatementsExecuted++;
      }

      return numberOfStatementsExecuted;
    } catch (Throwable e) {
      throw new SQLException(
          "Failed to execute the SQL statements in the resource file (" + resourcePath + ")", e);
    }
  }

  /**
   * Retrieve the schema separator for the database associated with the specified data source.
   *
   * @param dataSource the data source
   * @return the schema separator for the database associated with the specified data source
   * @throws SQLException if the schema separator could not be retrieved
   */
  public static String getSchemaSeparator(DataSource dataSource) throws SQLException {
    try (Connection connection = dataSource.getConnection()) {
      DatabaseMetaData metaData = connection.getMetaData();

      // Retrieve the schema separator for the database
      String schemaSeparator = metaData.getCatalogSeparator();

      if ((schemaSeparator == null) || (schemaSeparator.length() == 0)) {
        schemaSeparator = ".";
      }

      return schemaSeparator;
    }
  }

  /**
   * Is the database associated with the specified data source an in-memory H2 database.
   *
   * @param dataSource the data source
   * @return <b>true</b> if the database associated with the specified data source is an in-memory
   *     H2 database or <b>false</b> otherwise
   * @throws SQLException if the check for an in-memory H2 database failed
   */
  public static boolean isInMemoryH2Database(DataSource dataSource) throws SQLException {
    try (Connection connection = dataSource.getConnection()) {
      DatabaseMetaData metaData = connection.getMetaData();

      switch (metaData.getDatabaseProductName()) {
        case "H2":
          return true;

        default:
          return false;
      }
    }
  }

  /**
   * Load the SQL statements from the specified URL.
   *
   * @param url the URL
   * @return the SQL statements loaded from the specified URL
   * @throws IOException if the SQL statements could not be loaded from the specified URL
   */
  public static List<String> loadSQL(URL url) throws IOException {
    List<String> sqlStatements = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
      StringBuilder multiLineBuffer = null;
      String line;

      while ((line = reader.readLine()) != null) {
        /*
         * Remove any whitespace at the beginning or end of the line, any newline characters and
         * any multiple spaces.
         */
        line = cleanSQL(line);

        if (line.length() == 0) {
          continue;
        }

        // Only process the line if it is not a SQL comment
        if (!line.startsWith("--")) {
          // If the line contains a SQL comment then only process the portion before the comment
          if (line.contains("--")) {
            line = line.substring(0, line.indexOf("--"));
            line = line.trim();
          }

          /*
           * NOTE: Removed support for handling multiple SQL statements on a single line that are
           *       terminated by a ';' delimiter. This is because correctly handling the delimiter
           *       in single or double quotes is Very Hard (TM).
           */
          if (line.endsWith(";")) {
            if (multiLineBuffer != null) {
              multiLineBuffer.append(" ");
              multiLineBuffer.append(line);
              sqlStatements.add(multiLineBuffer.toString());
              multiLineBuffer = null;
            } else {
              sqlStatements.add(line);
            }
          } else {
            /*
             * The line does not contain the end of a SQL statement which means it is either
             * the start of a new multi-line SQL statement or the continuation of an existing
             * multi-line SQL statement.
             */
            if (multiLineBuffer != null) {
              multiLineBuffer.append(" ");
              multiLineBuffer.append(line);
            } else {
              multiLineBuffer = new StringBuilder();
              multiLineBuffer.append(line);
            }
          }
        }
      }

      if ((multiLineBuffer != null) && (StringUtils.hasText(multiLineBuffer.toString()))) {
        throw new IOException(
            "Failed to process the last SQL statement from the file ("
                + url.getPath()
                + ") since it was not terminated by a ';'");
      }

      return sqlStatements;
    }
  }

  /**
   * Read the blob associated with the column with the specified index from the specified result
   * set.
   *
   * @param rs the result set
   * @param index the index of the column containing the blob
   * @return the binary data for the BLOB
   * @throws SQLException if the blob could not be read
   */
  public static byte[] readBlob(ResultSet rs, int index) throws SQLException {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedInputStream in = new BufferedInputStream(rs.getBinaryStream(index))) {
      int noBytes;
      byte[] tmpBuffer = new byte[1024];

      while ((noBytes = in.read(tmpBuffer)) != -1) {
        baos.write(tmpBuffer, 0, noBytes);
      }

      return baos.toByteArray();
    } catch (IOException e) {
      throw new SQLException(
          "An IO error occurred while reading the BLOB from the database: " + e.getMessage());
    }
  }

  /**
   * Rollback the current transaction on the specified connection.
   *
   * @param connection the connection to rollback the transaction on
   */
  public static void rollback(Connection connection) {
    if (connection != null) {
      try {
        connection.rollback();
      } catch (SQLException e) {
        // Do nothing
      }
    }
  }

  /**
   * Checks whether the schema with the specified name exists for the database referenced by the
   * connection.
   *
   * @param connection the database connection to use
   * @param catalog the catalog name or <b>null</b> if a catalog should not be used
   * @param schema the schema name
   * @return true if the schema exists or false otherwise
   * @throws SQLException if the schema check failed
   */
  @SuppressWarnings("resource")
  public static boolean schemaExists(Connection connection, String catalog, String schema)
      throws SQLException {
    if (schema == null) {
      throw new SQLException("Failed to check whether the schema (null) exists");
    }

    DatabaseMetaData metaData = connection.getMetaData();

    try (ResultSet rs = metaData.getSchemas()) {
      while (rs.next()) {
        String tmpCatalog =
            StringUtils.hasText(rs.getString("TABLE_CATALOG")) ? rs.getString("TABLE_CATALOG") : "";
        String tmpSchema =
            StringUtils.hasText(rs.getString("TABLE_SCHEM")) ? rs.getString("TABLE_SCHEM") : "";

        if ((catalog == null) || catalog.equalsIgnoreCase(tmpCatalog)) {
          if (tmpSchema.equalsIgnoreCase(schema)) {
            return true;
          }
        }
      }
    }

    return false;
  }

  /**
   * Checks whether the schema with the specified name exists for the database referenced by the
   * data source.
   *
   * @param dataSource the data source to use
   * @param catalog the catalog name or <b>null</b> if a catalog should not be used
   * @param schema the schema name
   * @return true if the schema exists or false otherwise
   * @throws SQLException if the check for the existing schema failed
   */
  @SuppressWarnings("resource")
  public static boolean schemaExists(DataSource dataSource, String catalog, String schema)
      throws SQLException {
    if (schema == null) {
      throw new SQLException("Failed to check whether the schema (null) exists");
    }

    try (Connection connection = dataSource.getConnection()) {
      return schemaExists(connection, catalog, schema);
    }
  }

  /**
   * Close and release all connections that are currently stored in the connection pool associated
   * with the data source.
   *
   * <p>The HSQLDB database associated with the data source will also be shutdown.
   *
   * @param connection the HSQLDB database connection
   * @throws SQLException if the HSQLDB database could not be shutdown
   */
  public static void shutdownHsqlDatabase(Connection connection) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      // language=H2
      statement.executeUpdate("SHUTDOWN");
    }
  }

  /**
   * Checks whether the table with the specified name exists under the catalog and schema for the
   * database referenced by the data source.
   *
   * @param connection the database connection to use
   * @param catalog the catalog name or <b>null</b> if a catalog should not be used
   * @param schema the schema name or <b>null</b> if a schema should not be used
   * @param table the name of the table
   * @return true if the table exists or false otherwise
   * @throws SQLException if the check for the existing table failed
   */
  @SuppressWarnings("resource")
  public static boolean tableExists(
      Connection connection, String catalog, String schema, String table) throws SQLException {
    if (table == null) {
      throw new SQLException("Failed to check whether the table (null) exists");
    }

    // First check if the schema exists
    if ((schema != null) && (!schemaExists(connection, catalog, schema))) {
      return false;
    }

    DatabaseMetaData metaData = connection.getMetaData();

    try (ResultSet rs = metaData.getTables(catalog, schema, table, new String[] {"TABLE"})) {
      while (rs.next()) {
        String tmpTable =
            StringUtils.hasText(rs.getString("TABLE_NAME")) ? rs.getString("TABLE_NAME") : "";

        if (table.equals(tmpTable)) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Checks whether the table with the specified name exists under the catalog and schema for the
   * database referenced by the data source.
   *
   * @param dataSource the data source to use
   * @param catalog the catalog name or <b>null</b> if a catalog should not be used
   * @param schema the schema name or <b>null</b> if a schema should not be used
   * @param table the name of the table
   * @return true if the table exists or false otherwise
   * @throws SQLException if the check for the existing table failed
   */
  @SuppressWarnings("resource")
  public static boolean tableExists(
      DataSource dataSource, String catalog, String schema, String table) throws SQLException {
    if (table == null) {
      throw new SQLException("Failed to check whether the table (null) exists");
    }

    try (Connection connection = dataSource.getConnection()) {
      return tableExists(connection, catalog, schema, table);
    }
  }

  private static String cleanSQL(String text) {
    if (text == null) {
      throw new NullPointerException("Failed to clean the null SQL string");
    }

    // If this is an empty string then stop here
    if (text.length() == 0) {
      return text;
    }

    // First remove the new line characters
    int index = text.length() - 1;

    while ((index >= 0) && ((text.charAt(index) == '\r') || (text.charAt(index) == '\n'))) {
      index--;
    }

    if (index < 0) {
      return "";
    }

    text = text.substring(0, index + 1);

    // Replace multiple spaces with a single space
    while (text.contains("  ")) {
      text = text.replaceAll(" {2}", " ");
    }

    // Replace tabs with a single space
    text = text.replaceAll("\t", " ");

    // Strip whitespace from the beginning and end of the text
    text = text.trim();

    return text;
  }
}
