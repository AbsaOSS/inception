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

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Map;
import java.util.Optional;
import javax.sql.XAConnection;
import javax.transaction.Transaction;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.LoggerFactory;

/**
 * The <b>InceptionExtension</b> class.
 *
 * @author Marcus Portmann
 */
public class InceptionExtension implements AfterEachCallback {

  /** Constructs a new <b>InceptionExtension</b>. */
  public InceptionExtension() {}

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    Optional<Method> testMethodOptional = context.getTestMethod();

    if (testMethodOptional.isPresent()) {
      checkForActiveTransactions(
          testMethodOptional.get(), TransactionManagerProxy.getActiveTransactionStackTraces());

      checkForActiveTransactions(
          testMethodOptional.get(), UserTransactionProxy.getActiveTransactionStackTraces());

      checkForOpenDatabaseConnections(
          testMethodOptional.get(), DataSourceProxy.getActiveDatabaseConnections());

      checkForOpenXADatabaseConnections(
          testMethodOptional.get(), XADataSourceProxy.getActiveXADatabaseConnections());
    }
  }

  private void checkForActiveTransactions(
      Method method, Map<Transaction, StackTraceElement[]> activeTransactionStackTraces) {
    for (Transaction transaction : activeTransactionStackTraces.keySet()) {
      StackTraceElement[] stackTrace = activeTransactionStackTraces.get(transaction);

      for (int i = 0; i < stackTrace.length; i++) {
        if (stackTrace[i].getMethodName().equals("begin")
            && (stackTrace[i].getLineNumber() != -1)) {
          LoggerFactory.getLogger(InceptionExtension.class)
              .warn(
                  "Failed to successfully execute the test ("
                      + method.getName()
                      + "): Found an "
                      + "unexpected active transaction ("
                      + transaction.toString()
                      + ") that was "
                      + "started by the method ("
                      + stackTrace[i + 1].getMethodName()
                      + ") on the class ("
                      + stackTrace[i + 1].getClassName()
                      + ") on line ("
                      + stackTrace[i + 1].getLineNumber()
                      + ")");

          throw new RuntimeException(
              "Failed to successfully execute the test ("
                  + method.getName()
                  + "): Found an unexpected active transaction ("
                  + transaction.toString()
                  + ") that was started by the method ("
                  + stackTrace[i + 1].getMethodName()
                  + ") on the class ("
                  + stackTrace[i + 1].getClassName()
                  + ") on line ("
                  + stackTrace[i + 1].getLineNumber()
                  + ")");
        }
      }
    }
  }

  private void checkForOpenDatabaseConnections(
      Method method, Map<Connection, StackTraceElement[]> activeDatabaseConnections) {
    for (Connection connection : activeDatabaseConnections.keySet()) {
      StackTraceElement[] stackTrace = activeDatabaseConnections.get(connection);

      for (int i = 0; i < stackTrace.length; i++) {
        if (stackTrace[i].getMethodName().equals("getConnection")) {
          LoggerFactory.getLogger(InceptionExtension.class)
              .warn(
                  "Failed to successfully execute the test ("
                      + method.getName()
                      + "): Found an "
                      + "unexpected open database connection ("
                      + connection.toString()
                      + ") that was "
                      + "retrieved by the method ("
                      + stackTrace[i + 1].getMethodName()
                      + ") on the class ("
                      + stackTrace[i + 1].getClassName()
                      + ") on line ("
                      + stackTrace[i + 1].getLineNumber()
                      + ")");

          throw new RuntimeException(
              "Failed to successfully execute the test ("
                  + method.getName()
                  + "): Found an unexpected open database connection ("
                  + connection.toString()
                  + ") that was retrieved by the method ("
                  + stackTrace[i + 1].getMethodName()
                  + ") on the class ("
                  + stackTrace[i + 1].getClassName()
                  + ") on line ("
                  + stackTrace[i + 1].getLineNumber()
                  + ")");
        }
      }
    }
  }

  private void checkForOpenXADatabaseConnections(
      Method method, Map<XAConnection, StackTraceElement[]> activeXADatabaseConnections) {
    for (XAConnection connection : activeXADatabaseConnections.keySet()) {
      StackTraceElement[] stackTrace = activeXADatabaseConnections.get(connection);

      for (int i = 0; i < stackTrace.length; i++) {
        if (stackTrace[i].getMethodName().equals("getXAConnection")) {
          LoggerFactory.getLogger(InceptionExtension.class)
              .warn(
                  "Failed to successfully execute the test ("
                      + method.getName()
                      + "): Found an "
                      + "unexpected open XA database connection ("
                      + connection.toString()
                      + ") that was "
                      + "retrieved by the method ("
                      + stackTrace[i + 1].getMethodName()
                      + ") on the class ("
                      + stackTrace[i + 1].getClassName()
                      + ") on line ("
                      + stackTrace[i + 1].getLineNumber()
                      + ")");

          throw new RuntimeException(
              "Failed to successfully execute the test ("
                  + method.getName()
                  + "): Found an unexpected open XA database connection ("
                  + connection.toString()
                  + ") that was retrieved by the method ("
                  + stackTrace[i + 1].getMethodName()
                  + ") on the class ("
                  + stackTrace[i + 1].getClassName()
                  + ") on line ("
                  + stackTrace[i + 1].getLineNumber()
                  + ")");
        }
      }
    }
  }
}
