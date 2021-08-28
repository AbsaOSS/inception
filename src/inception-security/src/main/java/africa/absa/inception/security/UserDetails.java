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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * The <b>UserDetails</b> class stores the details for a user.
 *
 * @author Marcus Portmann
 */
public class UserDetails implements org.springframework.security.core.userdetails.UserDetails {

  /** The authorities granted to the user. */
  private final List<GrantedAuthority> authorities;

  /** The user. */
  private final User user;

  /**
   * Constructs a new <b>UserDetails</b>.
   *
   * @param user the user
   * @param roleCodes the codes for the roles that the user has been assigned
   * @param functionCodes the function codes for the user
   * @param tenantIds the Universally Unique Identifiers (UUIDs) for the tenants the user is
   *     associated with
   * @param userDirectoryIds the list of Universally Unique Identifiers (UUIDs) for the user user
   *     directories the user is associated with as a result of being associated with one or more
   *     tenants
   */
  UserDetails(
      User user,
      List<String> roleCodes,
      List<String> functionCodes,
      List<UUID> tenantIds,
      List<UUID> userDirectoryIds) {
    this.user = user;

    // Build the list of granted authorities
    this.authorities = new ArrayList<>();

    functionCodes.stream()
        .map(functionCode -> new SimpleGrantedAuthority("FUNCTION_" + functionCode))
        .forEach(authorities::add);

    roleCodes.stream()
        .map(roleCode -> new SimpleGrantedAuthority("ROLE_" + roleCode))
        .forEach(authorities::add);
  }

  /**
   * Returns the authorities granted to the user.
   *
   * @return the authorities
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  /**
   * Returns the password hash for the user.
   *
   * @return the password hash for the user
   */
  @Override
  public String getPassword() {
    return user.getPassword();
  }

  /**
   * Returns the user.
   *
   * @return the user
   */
  public User getUser() {
    return user;
  }

  /**
   * Returns the username used to authenticate the user.
   *
   * @return the username
   */
  @Override
  public String getUsername() {
    return user.getUsername();
  }

  /**
   * Returns whether the user's account has expired.
   *
   * @return <b>true</b> if the user's account has NOT expired or <b>false</b> otherwise
   */
  @Override
  public boolean isAccountNonExpired() {
    return (!user.isExpired());
  }

  /**
   * Returns whether the user's account is locked.
   *
   * @return <b>true</b> if the user's account is NOT locked or <b>false</b> otherwise
   */
  @Override
  public boolean isAccountNonLocked() {
    return (!user.isLocked());
  }

  /**
   * Returns whether the user's credentials have expired.
   *
   * @return <b>true</b> if the user's credentials have NOT expired or <b>false</b> otherwise
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return (!user.hasPasswordExpired());
  }

  /**
   * Returns whether the user's account is enabled.
   *
   * @return <b>true</b> if the user's account is enabled or <b>false</b> otherwise
   */
  @Override
  public boolean isEnabled() {
    return user.isActive();
  }
}
