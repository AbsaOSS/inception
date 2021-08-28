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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * The <b>UserDetailsService</b> class provides the User Details Service implementation that
 * provides the details for users.
 *
 * @author Marcus Portmann
 */
public class UserDetailsService
    implements org.springframework.security.core.userdetails.UserDetailsService {

  /* Security Service */
  @Autowired private ISecurityService securityService;

  /**
   * Locates the user based on the username. In the actual implementation, the search may possibly
   * be case sensitive, or case insensitive depending on how the implementation instance is
   * configured. In this case, the <b>UserDetails</b> object that comes back may have a username
   * that is of a different case than what was actually requested.
   *
   * @param username the username for the user whose data is required.
   * @return a fully populated user record (never <b>null</b>)
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    try {
      Optional<UUID> userDirectoryIdOptional = securityService.getUserDirectoryIdForUser(username);

      if (userDirectoryIdOptional.isEmpty()) {
        throw new UsernameNotFoundException(
            "Failed to retrieve the details for the user ("
                + username
                + "): The user is not associated with any of the configured user directories");
      } else {
        UUID userDirectoryId = userDirectoryIdOptional.get();

        // Retrieve the details for the user
        User user = securityService.getUser(userDirectoryId, username);

        // Retrieve the function codes for the user
        List<String> functionCodes =
            securityService.getFunctionCodesForUser(userDirectoryId, username);

        // Retrieve the list of IDs for the tenants the user is associated with
        List<UUID> tenantIds = securityService.getTenantIdsForUserDirectory(userDirectoryId);

        /*
         * Retrieve the list of IDs for the user directories the user is associated with as a result
         * of being associated with one or more tenants.
         */
        List<UUID> userDirectoryIdsForTenants = new ArrayList<>();

        for (var tenantId : tenantIds) {
          // Retrieve the list of user directories associated with the tenant
          var userDirectoryIdsForTenant = securityService.getUserDirectoryIdsForTenant(tenantId);

          userDirectoryIdsForTenants.addAll(userDirectoryIdsForTenant);
        }

        // Retrieve the list of roles for the user
        List<String> roleCodes = securityService.getRoleCodesForUser(userDirectoryId, username);

        return new africa.absa.inception.security.UserDetails(
            user, roleCodes, functionCodes, tenantIds, userDirectoryIdsForTenants);
      }
    } catch (UserNotFoundException e) {
      throw new UsernameNotFoundException(
          "Failed to retrieve the details for the user ("
              + username
              + "): The user could not be found");
    } catch (UsernameNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new RuntimeException(
          "Failed to retrieve the details for the user (" + username + ")", e);
    }
  }
}
