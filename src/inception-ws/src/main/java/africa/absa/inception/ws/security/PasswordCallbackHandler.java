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

package africa.absa.inception.ws.security;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import org.apache.wss4j.common.ext.WSPasswordCallback;

/**
 * The <b>PasswordCallbackHandler</b> class implements the password callback handler.
 *
 * @author Marcus Portmann
 */
public class PasswordCallbackHandler implements CallbackHandler {

  private final String password;

  private final String username;

  /**
   * Constructs a new <b>PasswordCallbackHandler</b>.
   *
   * @param username the username
   * @param password the password
   */
  public PasswordCallbackHandler(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Override
  public void handle(Callback[] callbacks) {
    WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
    if (username.equals(pc.getIdentifier())) {
      pc.setPassword(password);
    }
  }
}
