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

package africa.absa.inception.security;

import java.util.UUID;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * The <b>SecurityServiceAuthenticationManager</b> class provides an authentication manager
 * implementation based on the Security Service.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings("unused")
public class SecurityServiceAuthenticationManager implements AuthenticationManager {

  /** The Security Service. */
  private final ISecurityService securityService;

  /** The User Details Service. */
  private final UserDetailsService userDetailsService;

  /**
   * Constructs a new <b>SecurityServiceAuthenticationManager</b>.
   *
   * @param securityService the Security Service
   * @param userDetailsService the User Details Service
   */
  public SecurityServiceAuthenticationManager(
      ISecurityService securityService, UserDetailsService userDetailsService) {
    this.securityService = securityService;
    this.userDetailsService = userDetailsService;
  }

  /**
   * Attempts to authenticate the passed {@link Authentication} object, returning a fully populated
   * <b>Authentication</b> object (including granted authorities) if successful.
   *
   * <p>An <b>AuthenticationManager</b> must honour the following contract concerning exceptions:
   *
   * <ul>
   *   <li>A {@link DisabledException} must be thrown if an account is disabled and the <b>
   *       AuthenticationManager</b> can test for this state.
   *   <li>A {@link LockedException} must be thrown if an account is locked and the <b>
   *       AuthenticationManager</b> can test for account locking.
   *   <li>A {@link BadCredentialsException} must be thrown if incorrect credentials are presented.
   *       Whilst the above exceptions are optional, an <b>AuthenticationManager</b> must
   *       <B>always</B> test credentials.
   * </ul>
   *
   * <p>Exceptions should be tested for and if applicable thrown in the order expressed above (i.e.
   * if an account is disabled or locked, the authentication request is immediately rejected and the
   * credentials testing process is not performed). This prevents credentials being tested against
   * disabled or locked accounts.
   *
   * @param authentication the authentication request object
   * @return a fully authenticated object including credentials
   */
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    try {
      // Authenticate the user
      UUID authenticationUserDirectoryId =
          securityService.authenticate(
              authentication.getPrincipal().toString(), authentication.getCredentials().toString());

      // Retrieve the details for the user
      UserDetails userDetails =
          userDetailsService.loadUserByUsername(authentication.getPrincipal().toString());

      return new AuthenticationToken(userDetails);
    } catch (AuthenticationFailedException | UserNotFoundException e) {
      throw new BadCredentialsException(
          "Failed to authenticate the user ("
              + authentication.getPrincipal()
              + "): Bad credentials");
    } catch (UserLockedException e) {
      throw new LockedException(
          "Failed to authenticate the user (" + authentication.getPrincipal() + "): User locked");
    } catch (ExpiredPasswordException e) {
      throw new CredentialsExpiredException(
          "Failed to authenticate the user ("
              + authentication.getPrincipal()
              + "): Credentials expired");
    } catch (Throwable e) {
      throw new AuthenticationServiceException(
          "Failed to authenticate the user (" + authentication.getPrincipal() + ")", e);
    }
  }
}
