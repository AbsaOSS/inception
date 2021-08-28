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

package demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Properties;
import java.util.UUID;

public class PostgresJsonTest {

  public static void main(String[] args) {
    try {
      String url = "jdbc:postgresql://localhost/demo";
      Properties properties = new Properties();
      properties.setProperty("user", "demo");
      properties.setProperty("password", "demo");

      try (Connection connection = DriverManager.getConnection(url, properties)) {
        System.out.println("[DEBUG] Connected to Postgres database (demo)");

        try (PreparedStatement deleteStatement =
            connection.prepareStatement("DELETE FROM test.test")) {
          deleteStatement.executeUpdate();
        }

        try (PreparedStatement insertStatement =
            connection.prepareStatement("INSERT INTO test.test (id, data) VALUES (?, ?)")) {
          insertStatement.setObject(1, UUID.randomUUID(), Types.OTHER);

          //          PGobject pgObject = new PGobject();
          //          pgObject.setType("jsonb");
          //          pgObject.setValue("{\"customer\": \"Joe Bloggs\", \"items\": [{\"product\":
          // \"Beer\", \"quantity\": 6 }]}");
          //
          //          insertStatement.setObject(2, pgObject, Types.OTHER);

          // Inserting JSON as a string is made possible by the following casts:
          //   CREATE CAST (varchar AS jsonb) WITH INOUT AS ASSIGNMENT
          //   CREATE CAST (text AS jsonb) WITH INOUT AS ASSIGNMENT
          insertStatement.setString(
              2,
              "{\"customer\": \"Joe Bloggs\", \"items\": [{\"product\":  \"Beer\", \"quantity\": 6 }]}");

          insertStatement.executeUpdate();
        }

        try (PreparedStatement queryStatement =
            connection.prepareStatement("SELECT id, data FROM test.test")) {
          try (ResultSet rs = queryStatement.executeQuery()) {
            while (rs.next()) {
              Object idObject = rs.getObject(1);

              String data = rs.getString(2);

              System.out.println("data = " + data);
            }
          }
        }
      }

    } catch (Throwable e) {
      System.err.println("[ERROR] " + e.getMessage());
      e.printStackTrace(System.err);
    }
  }
}
