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

import africa.absa.inception.core.service.InvalidArgumentException;
import africa.absa.inception.core.service.ServiceUnavailableException;
import africa.absa.inception.core.sorting.SortDirection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The <b>ISecurityService</b> interface defines the functionality provided by a Security Service
 * implementation, which manages the security related information for an application.
 *
 * @author Marcus Portmann
 */
public interface ISecurityService {

  /** The ID for the Administrators group. */
  UUID ADMINISTRATORS_GROUP_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

  /** The name of the Administrators group. */
  String ADMINISTRATORS_GROUP_NAME = "Administrators";

  /** The code for the Administrator role. */
  String ADMINISTRATOR_ROLE_CODE = "Administrator";

  /** The username for the Administrator user. */
  String ADMINISTRATOR_USERNAME = "administrator";

  /** The ID for the default tenant. */
  UUID DEFAULT_TENANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

  /** The ID for the default internal user directory. */
  UUID DEFAULT_USER_DIRECTORY_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

  /** The code for the internal user directory type. */
  String INTERNAL_USER_DIRECTORY_TYPE = "InternalUserDirectory";

  /** The code for the LDAP user directory type. */
  String LDAP_USER_DIRECTORY_TYPE = "LDAPUserDirectory";

  /** The code for the Password Resetter role. */
  String PASSWORD_RESETTER_ROLE_CODE = "PasswordResetter";

  /** The code for the Tenant Administrator role. */
  String TENANT_ADMINISTRATOR_ROLE_CODE = "TenantAdministrator";

  /**
   * Add the group member to the group.
   *
   * @param userDirectoryId the ID for the user directory
   * @param groupName the name of the group
   * @param memberType the group member type
   * @param memberName the group member name
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws GroupNotFoundException if the group could not be found
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the group member could not be added to the group
   */
  void addMemberToGroup(
      UUID userDirectoryId, String groupName, GroupMemberType memberType, String memberName)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          UserNotFoundException, ServiceUnavailableException;

  /**
   * Add the role to the group.
   *
   * @param userDirectoryId the ID for the user directory
   * @param groupName the name of the group
   * @param roleCode the code for the role
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws GroupNotFoundException if the group could not be found
   * @throws RoleNotFoundException if the role could not be found
   * @throws ServiceUnavailableException if the role could not be added to the group
   */
  void addRoleToGroup(UUID userDirectoryId, String groupName, String roleCode)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          RoleNotFoundException, ServiceUnavailableException;

  /**
   * Add the user directory to the tenant.
   *
   * @param tenantId the ID for the tenant
   * @param userDirectoryId the ID for the user directory
   * @throws InvalidArgumentException if an argument is invalid
   * @throws TenantNotFoundException if the tenant could not be found
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws ServiceUnavailableException if the user directory could not be added to the tenant
   */
  void addUserDirectoryToTenant(UUID tenantId, UUID userDirectoryId)
      throws InvalidArgumentException, TenantNotFoundException, UserDirectoryNotFoundException,
          ServiceUnavailableException;

  /**
   * Add the user to the group.
   *
   * @param userDirectoryId the ID for the user directory
   * @param groupName the name of the group
   * @param username the username for the user
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws GroupNotFoundException if the group could not be found
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the user could not be added to the group
   */
  void addUserToGroup(UUID userDirectoryId, String groupName, String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          UserNotFoundException, ServiceUnavailableException;

  /**
   * Administratively change the password for the user.
   *
   * @param userDirectoryId the ID for the user directory
   * @param username the username for the user
   * @param newPassword the new password
   * @param expirePassword expire the user's password
   * @param lockUser lock the user
   * @param resetPasswordHistory reset the user's password history
   * @param reason the reason for changing the password
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the password could not be administratively changed
   */
  void adminChangePassword(
      UUID userDirectoryId,
      String username,
      String newPassword,
      boolean expirePassword,
      boolean lockUser,
      boolean resetPasswordHistory,
      PasswordChangeReason reason)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          ServiceUnavailableException;

  /**
   * Authenticate the user.
   *
   * @param username the username for the user
   * @param password the password being used to authenticate
   * @return the ID for the user directory
   * @throws InvalidArgumentException if an argument is invalid
   * @throws AuthenticationFailedException if the authentication failed
   * @throws UserLockedException if the user is locked
   * @throws ExpiredPasswordException if the password for the user has expired
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the user could not be authenticated
   */
  UUID authenticate(String username, String password)
      throws InvalidArgumentException, AuthenticationFailedException, UserLockedException,
          ExpiredPasswordException, UserNotFoundException, ServiceUnavailableException;

  /**
   * Change the password for the user.
   *
   * @param username the username for the user
   * @param password the password for the user that is used to authorise the operation
   * @param newPassword the new password
   * @return the ID for the user directory
   * @throws InvalidArgumentException if an argument is invalid
   * @throws AuthenticationFailedException if the authentication failed
   * @throws UserLockedException if the user is locked
   * @throws ExistingPasswordException if the user has previously used the new password
   * @throws ServiceUnavailableException if the password could not be changed
   */
  UUID changePassword(String username, String password, String newPassword)
      throws InvalidArgumentException, AuthenticationFailedException, UserLockedException,
          ExistingPasswordException, ServiceUnavailableException;

  /**
   * Create the new function.
   *
   * @param function the function
   * @throws InvalidArgumentException if an argument is invalid
   * @throws DuplicateFunctionException if the function already exists
   * @throws ServiceUnavailableException if the function could not be created
   */
  void createFunction(Function function)
      throws InvalidArgumentException, DuplicateFunctionException, ServiceUnavailableException;

  /**
   * Create the new group.
   *
   * @param group the group
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws DuplicateGroupException if the group already exists
   * @throws ServiceUnavailableException if the group could not be created
   */
  void createGroup(Group group)
      throws InvalidArgumentException, UserDirectoryNotFoundException, DuplicateGroupException,
          ServiceUnavailableException;

  /**
   * Create the new tenant.
   *
   * @param tenant the tenant
   * @param createUserDirectory should a new internal user directory be created for the tenant
   * @return an Optional containing the new internal user directory that was created for the tenant
   *     or an empty Optional if no user directory was created
   * @throws InvalidArgumentException if an argument is invalid
   * @throws DuplicateTenantException if the tenant already exists
   * @throws ServiceUnavailableException if the tenant could not be created
   */
  Optional<UserDirectory> createTenant(Tenant tenant, boolean createUserDirectory)
      throws InvalidArgumentException, DuplicateTenantException, ServiceUnavailableException;

  /**
   * Create the new user.
   *
   * @param user the user
   * @param expiredPassword create the user with its password expired
   * @param userLocked create the user locked
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws DuplicateUserException if the user already exists
   * @throws ServiceUnavailableException if the user could not be created
   */
  void createUser(User user, boolean expiredPassword, boolean userLocked)
      throws InvalidArgumentException, UserDirectoryNotFoundException, DuplicateUserException,
          ServiceUnavailableException;

  /**
   * Create the new user directory.
   *
   * @param userDirectory the user directory
   * @throws InvalidArgumentException if an argument is invalid
   * @throws DuplicateUserDirectoryException if the user directory already exists
   * @throws ServiceUnavailableException if the user directory could not be created
   */
  void createUserDirectory(UserDirectory userDirectory)
      throws InvalidArgumentException, DuplicateUserDirectoryException, ServiceUnavailableException;

  /**
   * Delete the function.
   *
   * @param functionCode the code for the function
   * @throws InvalidArgumentException if an argument is invalid
   * @throws FunctionNotFoundException if the function could not be found
   * @throws ServiceUnavailableException if the function could not be created
   */
  void deleteFunction(String functionCode)
      throws InvalidArgumentException, FunctionNotFoundException, ServiceUnavailableException;

  /**
   * Delete the group.
   *
   * @param userDirectoryId the ID for the user directory
   * @param groupName the name of the group
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws GroupNotFoundException if the group could not be found
   * @throws ExistingGroupMembersException if the group has existing members
   * @throws ServiceUnavailableException if the group could not be deleted
   */
  void deleteGroup(UUID userDirectoryId, String groupName)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          ExistingGroupMembersException, ServiceUnavailableException;

  /**
   * Delete the tenant.
   *
   * @param tenantId the ID for the tenant
   * @throws InvalidArgumentException if an argument is invalid
   * @throws TenantNotFoundException if the tenant could not be found
   * @throws ServiceUnavailableException if the tenant could not be deleted
   */
  void deleteTenant(UUID tenantId)
      throws InvalidArgumentException, TenantNotFoundException, ServiceUnavailableException;

  /**
   * Delete the user.
   *
   * @param userDirectoryId the ID for the user directory
   * @param username the username for the user
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the user could not be deleted
   */
  void deleteUser(UUID userDirectoryId, String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          ServiceUnavailableException;

  /**
   * Delete the user directory.
   *
   * @param userDirectoryId the ID for the user directory
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws ServiceUnavailableException if the user directory could not be deleted
   */
  void deleteUserDirectory(UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the users matching the user attribute criteria.
   *
   * @param userDirectoryId the ID for the user directory
   * @param userAttributes the user attribute criteria used to select the users
   * @return the users whose attributes match the user attribute criteria
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws InvalidAttributeException if an attribute is invalid
   * @throws ServiceUnavailableException if the users matching the user attribute criteria could not
   *     be found
   */
  List<User> findUsers(UUID userDirectoryId, List<UserAttribute> userAttributes)
      throws InvalidArgumentException, UserDirectoryNotFoundException, InvalidAttributeException,
          ServiceUnavailableException;

  /**
   * Retrieve the function.
   *
   * @param functionCode the code for the function
   * @return the function
   * @throws InvalidArgumentException if an argument is invalid
   * @throws FunctionNotFoundException if the function could not be found
   * @throws ServiceUnavailableException if the function could not be retrieved
   */
  Function getFunction(String functionCode)
      throws InvalidArgumentException, FunctionNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the function codes for the user.
   *
   * @param userDirectoryId the ID for the user directory
   * @param username the username for the user
   * @return the function codes for the user
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the function codes could not be retrieved for the user
   */
  List<String> getFunctionCodesForUser(UUID userDirectoryId, String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          ServiceUnavailableException;

  /**
   * Retrieve all the functions.
   *
   * @return the functions
   * @throws ServiceUnavailableException if the functions could not be retrieved
   */
  List<Function> getFunctions() throws ServiceUnavailableException;

  /**
   * Retrieve the group.
   *
   * @param userDirectoryId the ID for the user directory
   * @param groupName the name of the group
   * @return the group
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws GroupNotFoundException if the group could not be found
   * @throws ServiceUnavailableException if the group could not be retrieved
   */
  Group getGroup(UUID userDirectoryId, String groupName)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          ServiceUnavailableException;

  /**
   * Retrieve all the group names.
   *
   * @param userDirectoryId the ID for the user directory
   * @return the group names
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws ServiceUnavailableException if the group names could not be retrieved
   */
  List<String> getGroupNames(UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the names of the groups the user is a member of.
   *
   * @param userDirectoryId the ID for the user directory
   * @param username the username for the user
   * @return the names of the groups the user is a member of
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the names of the groups the user is a member of could
   *     not be retrieved
   */
  List<String> getGroupNamesForUser(UUID userDirectoryId, String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          ServiceUnavailableException;

  /**
   * Retrieve all the groups.
   *
   * @param userDirectoryId the ID for the user directory
   * @return the groups
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws ServiceUnavailableException if the groups could not be retrieved
   */
  List<Group> getGroups(UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the groups.
   *
   * @param userDirectoryId the ID for the user directory
   * @param filter the optional filter to apply to the groups
   * @param sortDirection the optional sort direction to apply to the groups
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the groups
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws ServiceUnavailableException if the groups could not be retrieved
   */
  Groups getGroups(
      UUID userDirectoryId,
      String filter,
      SortDirection sortDirection,
      Integer pageIndex,
      Integer pageSize)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the groups the user is a member of.
   *
   * @param userDirectoryId the ID for the user directory
   * @param username the username for the user
   * @return the groups the user is a member of
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the groups the user is a member of could not be
   *     retrieved
   */
  List<Group> getGroupsForUser(UUID userDirectoryId, String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          ServiceUnavailableException;

  /**
   * Retrieve the group members for the group.
   *
   * @param userDirectoryId the ID for the user directory
   * @param groupName the name of the group
   * @return the group members for the group
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws GroupNotFoundException if the group could not be found
   * @throws ServiceUnavailableException if the group members could not be retrieved for the group
   */
  List<GroupMember> getMembersForGroup(UUID userDirectoryId, String groupName)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          ServiceUnavailableException;

  /**
   * Retrieve the group members for the group.
   *
   * @param userDirectoryId the ID for the user directory
   * @param groupName the name of the group
   * @param filter the optional filter to apply to the group members
   * @param sortDirection the optional sort direction to apply to the group members
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the group members for the group
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws GroupNotFoundException if the group could not be found
   * @throws ServiceUnavailableException if the group members could not be retrieved for the group
   */
  GroupMembers getMembersForGroup(
      UUID userDirectoryId,
      String groupName,
      String filter,
      SortDirection sortDirection,
      Integer pageIndex,
      Integer pageSize)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          ServiceUnavailableException;

  /**
   * Retrieve the codes for the roles that have been assigned to the group.
   *
   * @param userDirectoryId the ID for the user directory
   * @param groupName the name of the group
   * @return the codes for the roles that have been assigned to the group
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws GroupNotFoundException if the group could not be found
   * @throws ServiceUnavailableException if the codes for the roles assigned to the group could not
   *     be retrieved
   */
  List<String> getRoleCodesForGroup(UUID userDirectoryId, String groupName)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          ServiceUnavailableException;

  /**
   * Retrieve the codes for the roles that have been assigned to the user.
   *
   * @param userDirectoryId the ID for the user directory
   * @param username the username for the user
   * @return the codes for the roles that have been assigned to the user
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the codes for the roles assigned to the user could not
   *     be retrieved
   */
  List<String> getRoleCodesForUser(UUID userDirectoryId, String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          ServiceUnavailableException;

  /**
   * Retrieve all the roles.
   *
   * @return the roles
   * @throws ServiceUnavailableException if the roles could not be retrieved
   */
  List<Role> getRoles() throws ServiceUnavailableException;

  /**
   * Retrieve the roles that have been assigned to the group.
   *
   * @param userDirectoryId the ID for the user directory
   * @param groupName the name of the group
   * @return the roles that have been assigned to the group
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws GroupNotFoundException if the group could not be found
   * @throws ServiceUnavailableException if the codes for the roles assigned to the group could not
   *     be retrieved
   */
  List<GroupRole> getRolesForGroup(UUID userDirectoryId, String groupName)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          ServiceUnavailableException;

  /**
   * Retrieve the tenant.
   *
   * @param tenantId the ID for the tenant
   * @return the tenant
   * @throws InvalidArgumentException if an argument is invalid
   * @throws TenantNotFoundException if the tenant could not be found
   * @throws ServiceUnavailableException if the tenant could not be retrieved
   */
  Tenant getTenant(UUID tenantId)
      throws InvalidArgumentException, TenantNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the IDs for the tenants the user directory is associated with.
   *
   * @param userDirectoryId the ID for the user directory
   * @return the IDs for the tenants the user directory is associated with
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws ServiceUnavailableException if the tenant IDs could not be retrieved for the user
   *     directory
   */
  List<UUID> getTenantIdsForUserDirectory(UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the name of the tenant.
   *
   * @param tenantId the ID for the tenant
   * @return the name of the tenant
   * @throws InvalidArgumentException if an argument is invalid
   * @throws TenantNotFoundException if the tenant could not be found
   * @throws ServiceUnavailableException if the name of the tenant could not be retrieved
   */
  String getTenantName(UUID tenantId)
      throws InvalidArgumentException, TenantNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the tenants.
   *
   * @return the tenants
   * @throws ServiceUnavailableException if the tenants could not be retrieved
   */
  List<Tenant> getTenants() throws ServiceUnavailableException;

  /**
   * Retrieve the tenants.
   *
   * @param filter the optional filter to apply to the tenants
   * @param sortDirection the optional sort direction to apply to the tenants
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the tenants
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the tenants could not be retrieved
   */
  Tenants getTenants(
      String filter, SortDirection sortDirection, Integer pageIndex, Integer pageSize)
      throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Retrieve the tenants the user directory is associated with.
   *
   * @param userDirectoryId the ID for the user directory
   * @return the tenants the user directory is associated with
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws ServiceUnavailableException if the tenants could not be retrieved for the user
   *     directory
   */
  List<Tenant> getTenantsForUserDirectory(UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the user.
   *
   * @param userDirectoryId the ID for the user directory
   * @param username the username for the user
   * @return the user
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the user could not be retrieved
   */
  User getUser(UUID userDirectoryId, String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          ServiceUnavailableException;

  /**
   * Retrieve the user directories.
   *
   * @return the user directories
   * @throws ServiceUnavailableException if the user directories could not be retrieved
   */
  List<UserDirectory> getUserDirectories() throws ServiceUnavailableException;

  /**
   * Retrieve the user directories.
   *
   * @param filter the optional filter to apply to the user directories
   * @param sortDirection the optional sort direction to apply to the user directories
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the user directories
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the user directories could not be retrieved
   */
  UserDirectories getUserDirectories(
      String filter, SortDirection sortDirection, Integer pageIndex, Integer pageSize)
      throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Retrieve the user directories the tenant is associated with.
   *
   * @param tenantId the ID for the tenant
   * @return the user directories the tenant is associated with
   * @throws InvalidArgumentException if an argument is invalid
   * @throws TenantNotFoundException if the tenant could not be found
   * @throws ServiceUnavailableException if the user directories could not be retrieved for the
   *     tenant
   */
  List<UserDirectory> getUserDirectoriesForTenant(UUID tenantId)
      throws InvalidArgumentException, TenantNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the user directory.
   *
   * @param userDirectoryId the ID for the user directory
   * @return the user directory
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws ServiceUnavailableException if the user directory could not be retrieved
   */
  UserDirectory getUserDirectory(UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the capabilities the user directory supports.
   *
   * @param userDirectoryId the ID for the user directory
   * @return the capabilities the user directory supports
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws ServiceUnavailableException if the user directory capabilities could not be retrieved
   */
  UserDirectoryCapabilities getUserDirectoryCapabilities(UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the ID for the user directory that the user with the specified username is associated
   * with.
   *
   * @param username the username for the user
   * @return an Optional containing the ID for the user directory that the user with the specified
   *     username is associated with or an empty Optional if the user cannot be found
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the user directory ID could not be retrieved for the
   *     user
   */
  Optional<UUID> getUserDirectoryIdForUser(String username)
      throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Retrieve the IDs for the user directories the tenant is associated with.
   *
   * @param tenantId the ID for the tenant
   * @return the IDs for the user directories the tenant is associated with
   * @throws InvalidArgumentException if an argument is invalid
   * @throws TenantNotFoundException if the tenant could not be found
   * @throws ServiceUnavailableException if the user directory IDs could not be retrieved for the
   *     tenant
   */
  List<UUID> getUserDirectoryIdsForTenant(UUID tenantId)
      throws InvalidArgumentException, TenantNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the IDs for the user directories the user is associated with. Every user is associated
   * with a user directory, which is in turn associated with one or more tenants, which are in turn
   * associated with one or more user directories. The user is therefore associated indirectly with
   * all these user directories.
   *
   * @param username the username for the user
   * @return the IDs for the user directories the user is associated with
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the user directory IDs could not be retrieved for the
   *     user
   */
  List<UUID> getUserDirectoryIdsForUser(String username)
      throws InvalidArgumentException, UserNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the name of the user directory.
   *
   * @param userDirectoryId the ID for the user directory
   * @return the name of the user directory
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws ServiceUnavailableException if the name of the user directory could not be retrieved
   */
  String getUserDirectoryName(UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the summaries for the user directories.
   *
   * @param filter the optional filter to apply to the user directories
   * @param sortDirection the optional sort direction to apply to the user directories
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the summaries for the user directories
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the user directory summaries could not be retrieved
   */
  UserDirectorySummaries getUserDirectorySummaries(
      String filter, SortDirection sortDirection, Integer pageIndex, Integer pageSize)
      throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Retrieve the summaries for the user directories the tenant is associated with.
   *
   * @param tenantId the ID for the tenant
   * @return the summaries for the user directories the tenant is associated with
   * @throws InvalidArgumentException if an argument is invalid
   * @throws TenantNotFoundException if the tenant could not be found
   * @throws ServiceUnavailableException if the user directory summaries could not be retrieved for
   *     the tenant
   */
  List<UserDirectorySummary> getUserDirectorySummariesForTenant(UUID tenantId)
      throws InvalidArgumentException, TenantNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the user directory type for the user directory.
   *
   * @param userDirectoryId the ID for the user directory
   * @return the user directory type for the user directory
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws UserDirectoryTypeNotFoundException if the user directory type could not be found
   * @throws ServiceUnavailableException if the user directory type could not be retrieved for the
   *     user directory
   */
  UserDirectoryType getUserDirectoryTypeForUserDirectory(UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException,
          UserDirectoryTypeNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the user directory types.
   *
   * @return the user directory types
   * @throws ServiceUnavailableException if the user directory types could not be retrieved
   */
  List<UserDirectoryType> getUserDirectoryTypes() throws ServiceUnavailableException;

  /**
   * Retrieve the name of the user.
   *
   * @param userDirectoryId the ID for the user directory
   * @param username the username for the user
   * @return the name of the user
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the name of the user could not be retrieved
   */
  String getUserName(UUID userDirectoryId, String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          ServiceUnavailableException;

  /**
   * Retrieve all the users.
   *
   * @param userDirectoryId the ID for the user directory
   * @return the users
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws ServiceUnavailableException if the users could not be retrieved
   */
  List<User> getUsers(UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the users.
   *
   * @param userDirectoryId the ID for the user directory
   * @param filter the optional filter to apply to the users
   * @param sortBy the optional method used to sort the users e.g. by name
   * @param sortDirection the optional sort direction to apply to the users
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the users
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws ServiceUnavailableException if the users could not be retrieved
   */
  Users getUsers(
      UUID userDirectoryId,
      String filter,
      UserSortBy sortBy,
      SortDirection sortDirection,
      Integer pageIndex,
      Integer pageSize)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException;

  /**
   * Initiate the password reset process for the user.
   *
   * @param username the username for the user
   * @param resetPasswordUrl the reset password URL
   * @param sendEmail should the password reset e-mail be sent to the user
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the password reset could not be initiated
   */
  void initiatePasswordReset(String username, String resetPasswordUrl, boolean sendEmail)
      throws InvalidArgumentException, UserNotFoundException, ServiceUnavailableException;

  /**
   * Initiate the password reset process for the user.
   *
   * @param username the username for the user
   * @param resetPasswordUrl the reset password URL
   * @param sendEmail should the password reset e-mail be sent to the user
   * @param securityCode the pre-generated security code to use
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the password reset could not be initiated
   */
  void initiatePasswordReset(
      String username, String resetPasswordUrl, boolean sendEmail, String securityCode)
      throws InvalidArgumentException, UserNotFoundException, ServiceUnavailableException;

  /**
   * Does the user with the specified username exist?
   *
   * @param userDirectoryId the ID for the user directory
   * @param username the username for the user
   * @return <b>true</b> if a user with specified username exists or <b>false</b> otherwise
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws ServiceUnavailableException if the check for the existing user failed
   */
  boolean isExistingUser(UUID userDirectoryId, String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException;

  /**
   * Is the user in the group?
   *
   * @param userDirectoryId the ID for the user directory
   * @param groupName the name of the group
   * @param username the username for the user
   * @return <b>true</b> if the user is a member of the group or <b>false</b> otherwise
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws UserNotFoundException if the user could not be found
   * @throws GroupNotFoundException if the group could not be found
   * @throws ServiceUnavailableException if the check to confirm if the user is a member of the
   *     group failed
   */
  boolean isUserInGroup(UUID userDirectoryId, String groupName, String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          GroupNotFoundException, ServiceUnavailableException;

  /**
   * Reload the user directories.
   *
   * @throws ServiceUnavailableException if the user directories could not be realoded
   */
  void reloadUserDirectories() throws ServiceUnavailableException;

  /**
   * Remove the group member from the group.
   *
   * @param userDirectoryId the ID for the user directory
   * @param groupName the name of the group
   * @param memberType the group member type
   * @param memberName the group member name
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws GroupNotFoundException if the group could not be found
   * @throws GroupMemberNotFoundException if the group member could not be found
   * @throws ServiceUnavailableException if the group member could not be removed from the group
   */
  void removeMemberFromGroup(
      UUID userDirectoryId, String groupName, GroupMemberType memberType, String memberName)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          GroupMemberNotFoundException, ServiceUnavailableException;

  /**
   * Remove the role from the group.
   *
   * @param userDirectoryId the ID for the user directory
   * @param groupName the name of the group
   * @param roleCode the code for the role
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws GroupNotFoundException if the group could not be found
   * @throws GroupRoleNotFoundException if the group role could not be found
   * @throws ServiceUnavailableException if the role could not be removed from the group
   */
  void removeRoleFromGroup(UUID userDirectoryId, String groupName, String roleCode)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          GroupRoleNotFoundException, ServiceUnavailableException;

  /**
   * Remove the user directory from the tenant.
   *
   * @param tenantId the ID for the tenant
   * @param userDirectoryId the ID for the user directory
   * @throws InvalidArgumentException if an argument is invalid
   * @throws TenantNotFoundException if the tenant could not be found
   * @throws TenantUserDirectoryNotFoundException if the tenant user directory could not be found
   * @throws ServiceUnavailableException if the user directory could not be removed from the tenant
   */
  void removeUserDirectoryFromTenant(UUID tenantId, UUID userDirectoryId)
      throws InvalidArgumentException, TenantNotFoundException,
          TenantUserDirectoryNotFoundException, ServiceUnavailableException;

  /**
   * Remove the user from the group.
   *
   * @param userDirectoryId the ID for the user directory
   * @param groupName the name of the group
   * @param username the username for the user
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws GroupNotFoundException if the group could not be found
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the user could not be removed from the group
   */
  void removeUserFromGroup(UUID userDirectoryId, String groupName, String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          UserNotFoundException, ServiceUnavailableException;

  /**
   * Reset the password for the user.
   *
   * @param username the username for the user
   * @param newPassword the new password
   * @param securityCode the security code
   * @throws InvalidArgumentException if an argument is invalid
   * @throws InvalidSecurityCodeException if the security code is invalid
   * @throws UserLockedException if the user is locked
   * @throws ExistingPasswordException if the user has previously used the new password
   * @throws ServiceUnavailableException if the password for the user could not be reset
   */
  void resetPassword(String username, String newPassword, String securityCode)
      throws InvalidArgumentException, InvalidSecurityCodeException, UserLockedException,
          ExistingPasswordException, ServiceUnavailableException;

  /**
   * Update the function.
   *
   * @param function the function
   * @throws InvalidArgumentException if an argument is invalid
   * @throws FunctionNotFoundException if the function could not be found
   * @throws ServiceUnavailableException if the function could not be updated
   */
  void updateFunction(Function function)
      throws InvalidArgumentException, FunctionNotFoundException, ServiceUnavailableException;

  /**
   * Update the group.
   *
   * @param group the group
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws GroupNotFoundException if the group could not be found
   * @throws ServiceUnavailableException if the group could not be updated
   */
  void updateGroup(Group group)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          ServiceUnavailableException;

  /**
   * Update the tenant.
   *
   * @param tenant the tenant
   * @throws InvalidArgumentException if an argument is invalid
   * @throws TenantNotFoundException if the tenant could not be found
   * @throws ServiceUnavailableException if the tenant could not be updated
   */
  void updateTenant(Tenant tenant)
      throws InvalidArgumentException, TenantNotFoundException, ServiceUnavailableException;

  /**
   * Update the user.
   *
   * @param user the user
   * @param expirePassword expire the user's password as part of the update
   * @param lockUser lock the user as part of the update
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the user could not be updated
   */
  void updateUser(User user, boolean expirePassword, boolean lockUser)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          ServiceUnavailableException;

  /**
   * Update the user directory.
   *
   * @param userDirectory the user directory
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws ServiceUnavailableException if the user directory could not be updated
   */
  void updateUserDirectory(UserDirectory userDirectory)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException;
}
