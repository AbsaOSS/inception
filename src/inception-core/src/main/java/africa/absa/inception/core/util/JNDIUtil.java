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

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;

/**
 * The <b>JNDIUtil</b> class provides JNDI utility functions.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings("unused")
public final class JNDIUtil {

  /**
   * Close the JNDIUtil directory context.
   *
   * @param dirContext the JNDIUtil directory context
   */
  public static void close(DirContext dirContext) {
    if (dirContext != null) {
      try {
        dirContext.close();
      } catch (Throwable ignored) {
      }
    }
  }

  /**
   * Close the JNDIUtil search results.
   *
   * @param searchResults the JNDIUtil search results
   */
  public static void close(NamingEnumeration<SearchResult> searchResults) {
    if (searchResults != null) {
      try {
        searchResults.close();
      } catch (Throwable ignored) {
      }
    }
  }

  /**
   * Close the JNDIUtil search result.
   *
   * @param searchResult the JNDIUtil search result
   */
  public static void close(SearchResult searchResult) {
    if ((searchResult != null)
        && (searchResult.getObject() != null)
        && (searchResult.getObject() instanceof Context)) {
      try {
        ((Context) searchResult.getObject()).close();
      } catch (Throwable ignored) {
      }
    }
  }

  /** Dump the contents of the JNDIUtil tree. */
  public static void dumpJNDI() {
    dumpJNDI(new PrintWriter(System.out));
  }

  /**
   * Dump the contents of the JNDIUtil tree.
   *
   * @return the contents of the JNDIUtil as a <b>String</b>
   */
  public static String dumpJNDIToString() {
    StringWriter sw = new StringWriter();

    dumpJNDI(new PrintWriter(sw));

    return sw.getBuffer().toString();
  }

  private static void dumpJNDI(PrintWriter pw) {
    InitialContext ic = null;

    try {
      ic = new InitialContext();

      TreeDumpStatus treeDumpStatus = new TreeDumpStatus();

      pw.println();
      pw.println("[]");
      pw.println();
      dumpNameSpace(pw, treeDumpStatus, ic, "", 1);
      pw.println();

      pw.println("[java:app]");
      pw.println();
      dumpNameSpace(pw, treeDumpStatus, ic, "java:app", 1);
      pw.println();

      pw.println("[java:global]");
      pw.println();
      dumpNameSpace(pw, treeDumpStatus, ic, "java:global", 1);
      pw.println();

      pw.println("[java:comp]");
      pw.println();
      dumpNameSpace(pw, treeDumpStatus, ic, "java:comp", 1);
      pw.println();

      pw.println("[java:comp/env]");
      pw.println();
      dumpNameSpace(pw, treeDumpStatus, ic, "java:comp/env", 1);
      pw.println();

      pw.println("[java:appserver]");
      pw.println();
      dumpNameSpace(pw, treeDumpStatus, ic, "java:appserver", 1);
      pw.println();
    } catch (Throwable e) {
      pw.println("[ERROR] Failed to dump the JNDIUtil tree: " + e.getMessage());
      e.printStackTrace(pw);
    } finally {
      try {
        if (ic != null) {
          ic.close();
        }
      } catch (Throwable ignored) {
      }
    }
  }

  private static void dumpNameSpace(
      PrintWriter pw, TreeDumpStatus treeDumpStatus, Context context, String path, int depth)
      throws NamingException {
    StringBuilder indent = new StringBuilder();

    for (int i = 0; i < depth; i++) {
      indent.append("    ");
    }

    NamingEnumeration<NameClassPair> nameClassPairs;

    try {
      nameClassPairs = context.list(path);
    } catch (NameNotFoundException e) {
      return;
    } catch (Throwable e) {
      pw.println(
          indent
              + "[ERROR] Failed to list the JNDIUtil nodes for the context ("
              + path
              + "): "
              + e.getMessage());
      pw.println("");

      return;
    }

    while (nameClassPairs.hasMoreElements()) {
      NameClassPair nameClassPair = nameClassPairs.nextElement();

      // Process the child node
      try {
        Object object = InitialContext.doLookup(path + "/" + nameClassPair.getName());

        if (!(object instanceof Context)) {
          pw.println(
              indent
                  + nameClassPair.getName()
                  + " ["
                  + nameClassPair.getClassName()
                  + "] = "
                  + object);
          pw.println();
        } else {
          boolean processChildren = true;

          if (nameClassPair.getName().equals("thisNode") && (depth > 1)) {
            processChildren = false;
          } else if (nameClassPair.getName().equals("cell")) {
            if (treeDumpStatus.foundCell) {
              processChildren = false;
            } else {
              treeDumpStatus.foundCell = true;
            }
          } else if (nameClassPair.getName().equals("nodes")) {
            if (treeDumpStatus.foundNodes) {
              processChildren = false;
            } else {
              treeDumpStatus.foundNodes = true;
            }
          } else if (nameClassPair.getName().equals("node")) {
            if (treeDumpStatus.foundNode) {
              processChildren = false;
            } else {
              treeDumpStatus.foundNode = true;
            }
          } else if (nameClassPair.getName().equals("servers")) {
            if (treeDumpStatus.foundServers) {
              processChildren = false;
            } else {
              treeDumpStatus.foundServers = true;
            }
          } else if (nameClassPair.getName().equals("clusters")) {
            if (treeDumpStatus.foundClusters) {
              processChildren = false;
            } else {
              treeDumpStatus.foundClusters = true;
            }
          } else if (nameClassPair.getName().equals("persistent")) {
            if (treeDumpStatus.foundPersistent) {
              processChildren = false;
            } else {
              treeDumpStatus.foundPersistent = true;
            }
          } else if (nameClassPair.getName().equals("domain")) {
            if (treeDumpStatus.foundDomain) {
              processChildren = false;
            } else {
              treeDumpStatus.foundDomain = true;
            }
          }

          if (processChildren) {
            pw.println(
                indent
                    + nameClassPair.getName()
                    + " ["
                    + nameClassPair.getClassName()
                    + "] = "
                    + object);
            pw.println();

            if (path.length() > 0) {
              dumpNameSpace(
                  pw, treeDumpStatus, context, path + "/" + nameClassPair.getName(), depth + 1);
            } else {
              dumpNameSpace(pw, treeDumpStatus, context, nameClassPair.getName(), depth + 1);
            }
          } else {
            pw.println(indent + nameClassPair.getName() + " (Duplicate Reference)");
            pw.println();
          }
        }
      } catch (Throwable e) {
        pw.println(indent + nameClassPair.getName() + " [" + nameClassPair.getClassName() + "]");
        pw.println();
      }
    }
  }

  static class TreeDumpStatus {

    /** foundCell */
    boolean foundCell;

    /** foundClusters */
    boolean foundClusters;

    /** foundDomain */
    boolean foundDomain;

    /** foundNode */
    boolean foundNode;

    /** foundNodes */
    boolean foundNodes;

    /** foundPersistent */
    boolean foundPersistent;

    /** foundServers */
    boolean foundServers;
  }
}
