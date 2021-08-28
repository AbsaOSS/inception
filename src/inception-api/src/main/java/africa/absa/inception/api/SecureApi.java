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

package africa.absa.inception.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

/**
 * The <b>SecureApi</b> class provides the base class from which all RESTful controllers that
 * implement secure APIs should be derived.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings("unused")
public abstract class SecureApi {

  /** The code for the Administrator role. */
  private static final String ADMINISTRATOR_ROLE_CODE = "Administrator";

  /** Is debugging enabled for the Inception Framework? */
  private boolean inDebugMode;

  /** Is API security enabled for the Inception Framework? */
  private boolean isSecurityEnabled;

  /**
   * Constructs a new <b>SecureApi</b>.
   *
   * @param applicationContext the Spring application context
   */
  public SecureApi(ApplicationContext applicationContext) {

    // Check if debugging is enabled for the Inception Framework
    try {
      if (StringUtils.hasText(
          applicationContext.getEnvironment().getProperty("inception.debug.enabled"))) {
        this.inDebugMode =
            Boolean.parseBoolean(
                applicationContext.getEnvironment().getProperty("inception.debug.enabled"));
      }
    } catch (Throwable e) {
      this.inDebugMode = false;
    }

    // Check if security is enabled for the Inception Framework
    try {
      if (StringUtils.hasText(
          applicationContext.getEnvironment().getProperty("inception.api.security.enabled"))) {
        this.isSecurityEnabled =
            Boolean.parseBoolean(
                applicationContext.getEnvironment().getProperty("inception.api.security.enabled"));
      }
    } catch (Throwable e) {
      this.isSecurityEnabled = !this.inDebugMode;
    }
  }

  /**
   * Returns whether debugging is enabled for the Inception Framework.
   *
   * @return <b>true</b> if debugging is enabled for the Inception Framework or <b>false</b>
   *     otherwise
   */
  public boolean inDebugMode() {
    return inDebugMode;
  }

  /**
   * Returns whether API security is disabled.
   *
   * @return <b>true</b> if API security is disabled or <b>false</b> otherwise
   */
  public boolean isSecurityDisabled() {
    return !isSecurityEnabled;
  }

  /**
   * Returns whether API security is enabled.
   *
   * @return <b>true</b> if API security is enabled or <b>false</b> otherwise
   */
  public boolean isSecurityEnabled() {
    return isSecurityEnabled;
  }

  /**
   * Returns the <b>Long</b> value portion of the authorities with the specified prefix.
   *
   * @param authentication the authenticated principal associated with the authenticated request
   * @param prefix the authority prefix
   * @return the <b>Long</b> value portion of the authorities with the specified prefix
   */
  protected List<Long> getLongValuesForAuthoritiesWithPrefix(
      Authentication authentication, String prefix) {
    var values = new ArrayList<Long>();

    for (GrantedAuthority authority : authentication.getAuthorities()) {
      if (authority.getAuthority().startsWith(prefix)) {
        try {
          values.add(Long.parseLong(authority.getAuthority().substring(prefix.length())));
        } catch (Throwable ignored) {
        }
      }
    }

    return values;
  }

  /**
   * Returns the <b>UUID</b> value portion of the authorities with the specified prefix.
   *
   * @param authentication the authenticated principal associated with the authenticated request
   * @param prefix the authority prefix
   * @return the <b>UUID</b> value portion of the authorities with the specified prefix
   */
  protected List<UUID> getUUIDValuesForAuthoritiesWithPrefix(
      Authentication authentication, String prefix) {
    var values = new ArrayList<UUID>();

    for (GrantedAuthority authority : authentication.getAuthorities()) {
      if (authority.getAuthority().startsWith(prefix)) {
        try {
          values.add(UUID.fromString(authority.getAuthority().substring(prefix.length())));
        } catch (Throwable ignored) {
        }
      }
    }

    return values;
  }

  /**
   * Returns the value portion of the authority with the specified prefix.
   *
   * @param authentication the authenticated principal associated with the authenticated request
   * @param prefix the authority prefix
   * @return an Optional containing the value portion of the authority with the specified prefix or
   *     an empty Optional if the authority with the specified prefix could not be found
   */
  protected Optional<String> getValueForAuthorityWithPrefix(
      Authentication authentication, String prefix) {
    for (GrantedAuthority authority : authentication.getAuthorities()) {
      if (authority.getAuthority().startsWith(prefix)) {
        return Optional.of(authority.getAuthority().substring(prefix.length()));
      }
    }

    return Optional.empty();
  }

  /**
   * Returns the value portion of the authorities with the specified prefix.
   *
   * @param authentication the authenticated principal associated with the authenticated request
   * @param prefix the authority prefix
   * @return the value portion of the authorities with the specified prefix
   */
  protected List<String> getValuesForAuthoritiesWithPrefix(
      Authentication authentication, String prefix) {
    var values = new ArrayList<String>();

    for (GrantedAuthority authority : authentication.getAuthorities()) {
      if (authority.getAuthority().startsWith(prefix)) {
        values.add(authority.getAuthority().substring(prefix.length()));
      }
    }

    return values;
  }

  /**
   * Confirm that the user associated with the authenticated request has access to the specified
   * function.
   *
   * @param functionCode the code for the function
   * @return <b>true</b> if the user associated with the authenticated request has access to the
   *     function identified by the specified function code or <b>false</b> otherwise
   */
  protected boolean hasAccessToFunction(String functionCode) {
    return hasAuthority("FUNCTION_" + functionCode);
  }

  /**
   * Confirm that the user associated with the authenticated request has access to the tenant.
   *
   * @param tenantId the Universally Unique Identifier (UUID) for the tenant
   * @return <b>true</b> if the user associated with the authenticated request has access to the
   *     tenant or <b>false</b> otherwise
   */
  protected boolean hasAccessToTenant(UUID tenantId) {
    if (isSecurityEnabled) {
      if (tenantId == null) {
        return false;
      }

      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      // Could not retrieve the currently authenticated principal
      if (authentication == null) {
        return false;
      }

      // If the user is not authenticated then they cannot have access
      if (!authentication.isAuthenticated()) {
        return false;
      }

      // If the user has the "Administrator" role they always have access
      if (hasRole(ADMINISTRATOR_ROLE_CODE)) {
        return true;
      }

      List<UUID> tenantIds = getUUIDValuesForAuthoritiesWithPrefix(authentication, "TENANT_");

      return tenantIds.contains(tenantId);
    } else {
      return true;
    }
  }

  /**
   * Confirm that the user associated with the authenticated request has the specified authority.
   *
   * @param authority the authority
   * @return <b>true</b> if the user associated with the authenticated request has the specified
   *     authority or <b>false</b> otherwise
   */
  protected boolean hasAuthority(String authority) {
    if (isSecurityEnabled) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      // Could not retrieve the currently authenticated principal
      if (authentication == null) {
        return false;
      }

      if (!StringUtils.hasText(authority)) {
        return false;
      }

      if (!authentication.isAuthenticated()) {
        return false;
      }

      for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
        if (grantedAuthority.getAuthority().equalsIgnoreCase(authority)) {
          return true;
        }
      }

      return false;
    } else {
      return true;
    }
  }

  /**
   * Confirm that the user associated with the authenticated request has the specified role.
   *
   * @param roleName the name of the role
   * @return <b>true</b> if the user associated with the authenticated request has the specified
   *     role or <b>false</b> otherwise
   */
  protected boolean hasRole(String roleName) {
    return hasAuthority("ROLE_" + roleName);
  }
}
