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

import africa.absa.inception.core.service.ServiceUnavailableException;
import africa.absa.inception.core.sorting.SortDirection;
import java.util.List;

/**
 * The <b>IUserDirectoryProvider</b> interface defines the functionality provided by a user
 * directory, which manages users and groups.
 *
 * @author Marcus Portmann
 */
interface IUserDirectory {

  /**
   * Add the group member to the group.
   *
   * @param groupName the name of the group
   * @param memberType the group member type
   * @param memberName the group member name
   * @throws GroupNotFoundException if the group could not be found
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the group member could not be added to the group
   */
  void addMemberToGroup(String groupName, GroupMemberType memberType, String memberName)
      throws GroupNotFoundException, UserNotFoundException, ServiceUnavailableException;

  /**
   * Add the role to the group.
   *
   * @param groupName the name of the group
   * @param roleCode the code for the role
   * @throws GroupNotFoundException if the group could not be found
   * @throws RoleNotFoundException if the role could not be found
   * @throws ServiceUnavailableException if the role could not be added to the group
   */
  void addRoleToGroup(String groupName, String roleCode)
      throws GroupNotFoundException, RoleNotFoundException, ServiceUnavailableException;

  /**
   * Add the user to the group.
   *
   * @param groupName the name of the group
   * @param username the username for the user
   * @throws GroupNotFoundException if the group could not be found
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the user could not be added to the group
   */
  void addUserToGroup(String groupName, String username)
      throws GroupNotFoundException, UserNotFoundException, ServiceUnavailableException;

  /**
   * Administratively change the password for the user.
   *
   * @param username the username for the user
   * @param newPassword the new password
   * @param expirePassword expire the user's password
   * @param lockUser lock the user
   * @param resetPasswordHistory reset the user's password history
   * @param reason the reason for changing the password
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the password could not be administratively changed
   */
  void adminChangePassword(
      String username,
      String newPassword,
      boolean expirePassword,
      boolean lockUser,
      boolean resetPasswordHistory,
      PasswordChangeReason reason)
      throws UserNotFoundException, ServiceUnavailableException;

  /**
   * Authenticate the user.
   *
   * @param username the username for the user
   * @param password the password being used to authenticate
   * @throws AuthenticationFailedException if the authentication failed
   * @throws UserLockedException if the user is locked
   * @throws ExpiredPasswordException if the password for the user has expired
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the user could not be authenticated
   */
  void authenticate(String username, String password)
      throws AuthenticationFailedException, UserLockedException, ExpiredPasswordException,
          UserNotFoundException, ServiceUnavailableException;

  /**
   * Change the password for the user.
   *
   * @param username the username for the user
   * @param password the password for the user that is used to authorise the operation
   * @param newPassword the new password
   * @throws AuthenticationFailedException if the authentication failed
   * @throws UserLockedException if the user is locked
   * @throws ExistingPasswordException if the user has previously used the new password
   * @throws ServiceUnavailableException if the password could not be changed
   */
  void changePassword(String username, String password, String newPassword)
      throws AuthenticationFailedException, UserLockedException, ExistingPasswordException,
          ServiceUnavailableException;

  /**
   * Create the new group.
   *
   * @param group the group
   * @throws DuplicateGroupException if the group already exists
   * @throws ServiceUnavailableException if the group could not be created
   */
  void createGroup(Group group) throws DuplicateGroupException, ServiceUnavailableException;

  /**
   * Create the new user.
   *
   * @param user the user
   * @param expiredPassword create the user with its password expired
   * @param userLocked create the user locked
   * @throws DuplicateUserException if the user already exists
   * @throws ServiceUnavailableException if the user could not be created
   */
  void createUser(User user, boolean expiredPassword, boolean userLocked)
      throws DuplicateUserException, ServiceUnavailableException;

  /**
   * Delete the group.
   *
   * @param groupName the name of the group
   * @throws GroupNotFoundException if the group could not be found
   * @throws ExistingGroupMembersException if the group has existing members
   * @throws ServiceUnavailableException if the group could not be deleted
   */
  void deleteGroup(String groupName)
      throws GroupNotFoundException, ExistingGroupMembersException, ServiceUnavailableException;

  /**
   * Delete the user.
   *
   * @param username the username for the user
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the user could not be deleted
   */
  void deleteUser(String username) throws UserNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the users matching the user attribute criteria.
   *
   * @param userAttributes the user attribute criteria used to select the users
   * @return the users whose attributes match the user attribute criteria
   * @throws InvalidAttributeException if an attribute is invalid
   * @throws ServiceUnavailableException if the users matching the user attribute criteria could not
   *     be found
   */
  List<User> findUsers(List<UserAttribute> userAttributes)
      throws InvalidAttributeException, ServiceUnavailableException;

  /**
   * Retrieve the capabilities the user directory supports.
   *
   * @return the capabilities the user directory supports
   * @throws ServiceUnavailableException if the user directory capabilities could not be retrieved
   */
  UserDirectoryCapabilities getCapabilities() throws ServiceUnavailableException;

  /**
   * Retrieve the function codes for the user.
   *
   * @param username the username for the user
   * @return the function codes for the user
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the function codes could not be retrieved for
   *     the user
   */
  List<String> getFunctionCodesForUser(String username)
      throws UserNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the group.
   *
   * @param groupName the name of the group
   * @return the group
   * @throws GroupNotFoundException if the group could not be found
   * @throws ServiceUnavailableException if the group could not be retrieved
   */
  Group getGroup(String groupName) throws GroupNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve all the group names.
   *
   * @return the group names
   * @throws ServiceUnavailableException if the group names could not be retrieved
   */
  List<String> getGroupNames() throws ServiceUnavailableException;

  /**
   * Retrieve the names of the groups the user is a member of.
   *
   * @param username the username for the user
   * @return the names of the groups the user is a member of
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the names of the groups the user is a member of could
   *     not be retrieved
   */
  List<String> getGroupNamesForUser(String username)
      throws UserNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve all the groups.
   *
   * @return the groups
   * @throws ServiceUnavailableException if the groups could not be retrieved
   */
  List<Group> getGroups() throws ServiceUnavailableException;

  /**
   * Retrieve the groups.
   *
   * @param filter the optional filter to apply to the groups
   * @param sortDirection the optional sort direction to apply to the groups
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the groups
   * @throws ServiceUnavailableException if the groups could not be retrieved
   */
  Groups getGroups(String filter, SortDirection sortDirection, Integer pageIndex, Integer pageSize)
      throws ServiceUnavailableException;

  /**
   * Retrieve the groups the user is a member of.
   *
   * @param username the username for the user
   * @return the groups the user is a member of
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the groups the user is a member of could not be
   *     retrieved
   */
  List<Group> getGroupsForUser(String username)
      throws UserNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the group members for the group.
   *
   * @param groupName the name of the group
   * @return the group members for the group
   * @throws GroupNotFoundException if the group could not be found
   * @throws ServiceUnavailableException if the group members could not be retrieved for the group
   */
  List<GroupMember> getMembersForGroup(String groupName)
      throws GroupNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the group members for the group.
   *
   * @param groupName the name of the group
   * @param filter the optional filter to apply to the group members
   * @param sortDirection the optional sort direction to apply to the group members
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the group members for the group
   * @throws GroupNotFoundException if the group could not be found
   * @throws ServiceUnavailableException if the group members could not be retrieved for the group
   */
  GroupMembers getMembersForGroup(
      String groupName,
      String filter,
      SortDirection sortDirection,
      Integer pageIndex,
      Integer pageSize)
      throws GroupNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the codes for the roles that have been assigned to the group.
   *
   * @param groupName the name of the group
   * @return the codes for the roles that have been assigned to the group
   * @throws GroupNotFoundException if the group could not be found
   * @throws ServiceUnavailableException if the codes for the roles assigned to the group could not
   *     be retrieved
   */
  List<String> getRoleCodesForGroup(String groupName)
      throws GroupNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the codes for the roles that the user has been assigned.
   *
   * @param username the username for the user
   * @return the codes for the roles that the user has been assigned
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the codes for the roles assigned to the user could not
   *     be retrieved
   */
  List<String> getRoleCodesForUser(String username)
      throws UserNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the roles that have been assigned to the group.
   *
   * @param groupName the name of the group
   * @return the roles that have been assigned to the group
   * @throws GroupNotFoundException if the group could not be found
   * @throws ServiceUnavailableException if the codes for the roles assigned to the group could not
   *     be retrieved
   */
  List<GroupRole> getRolesForGroup(String groupName)
      throws GroupNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the user.
   *
   * @param username the username for the user
   * @return the user
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the user could not be retrieved
   */
  User getUser(String username) throws UserNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the name of the user.
   *
   * @param username the username for the user
   * @return the name of the user
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the name of the user could not be retrieved
   */
  String getUserName(String username) throws UserNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve all the users.
   *
   * @return the users
   * @throws ServiceUnavailableException if the users could not be retrieved
   */
  List<User> getUsers() throws ServiceUnavailableException;

  /**
   * Retrieve the users.
   *
   * @param filter the optional filter to apply to the users
   * @param sortBy the optional method used to sort the users e.g. by name
   * @param sortDirection the optional sort direction to apply to the users
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the users
   * @throws ServiceUnavailableException if the users could not be retrieved
   */
  Users getUsers(
      String filter,
      UserSortBy sortBy,
      SortDirection sortDirection,
      Integer pageIndex,
      Integer pageSize)
      throws ServiceUnavailableException;

  /**
   * Does the user with the specified username exist?
   *
   * @param username the username for the user
   * @return <b>true</b> if a user with specified username exists or <b>false</b> otherwise
   * @throws ServiceUnavailableException if the check for the existing user failed
   */
  boolean isExistingUser(String username) throws ServiceUnavailableException;

  /**
   * Is the user in the group?
   *
   * @param groupName the name of the group
   * @param username the username for the user
   * @return <b>true</b> if the user is a member of the group or <b>false</b> otherwise
   * @throws UserNotFoundException if the user could not be found
   * @throws GroupNotFoundException if the group could not be found
   * @throws ServiceUnavailableException if the check to confirm if the user is a member of the
   *     group failed
   */
  boolean isUserInGroup(String groupName, String username)
      throws UserNotFoundException, GroupNotFoundException, ServiceUnavailableException;

  /**
   * Remove the group member from the group.
   *
   * @param groupName the name of the group
   * @param memberType the group member type
   * @param memberName the group member name
   * @throws GroupNotFoundException if the group could not be found
   * @throws GroupMemberNotFoundException if the group member could not be found
   * @throws ServiceUnavailableException if the group member could not be removed from the group
   */
  void removeMemberFromGroup(String groupName, GroupMemberType memberType, String memberName)
      throws GroupNotFoundException, GroupMemberNotFoundException, ServiceUnavailableException;

  /**
   * Remove the role from the group.
   *
   * @param groupName the name of the group
   * @param roleCode the code for the role
   * @throws GroupNotFoundException if the group could not be found
   * @throws GroupRoleNotFoundException if the group role could not be found
   * @throws ServiceUnavailableException if the role could not be removed from the group
   */
  void removeRoleFromGroup(String groupName, String roleCode)
      throws GroupNotFoundException, GroupRoleNotFoundException, ServiceUnavailableException;

  /**
   * Remove the user from the group.
   *
   * @param groupName the name of the group
   * @param username the username for the user
   * @throws GroupNotFoundException if the group could not be found
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the user could not be removed from the group
   */
  void removeUserFromGroup(String groupName, String username)
      throws GroupNotFoundException, UserNotFoundException, ServiceUnavailableException;

  /**
   * Reset the password for the user.
   *
   * @param username the username for the user
   * @param newPassword the new password
   * @throws UserNotFoundException if the user could not be found
   * @throws UserLockedException if the user is locked
   * @throws ExistingPasswordException if the user has previously used the new password
   * @throws ServiceUnavailableException if the password for the user could not be reset
   */
  void resetPassword(String username, String newPassword)
      throws UserNotFoundException, UserLockedException, ExistingPasswordException,
          ServiceUnavailableException;

  /**
   * Update the group.
   *
   * @param group the group
   * @throws GroupNotFoundException if the group could not be found
   * @throws ServiceUnavailableException if the group could not be updated
   */
  void updateGroup(Group group) throws GroupNotFoundException, ServiceUnavailableException;

  /**
   * Update the user.
   *
   * @param user the user
   * @param expirePassword expire the user's password as part of the update
   * @param lockUser lock the user as part of the update
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the user could not be updated
   */
  void updateUser(User user, boolean expirePassword, boolean lockUser)
      throws UserNotFoundException, ServiceUnavailableException;
}
