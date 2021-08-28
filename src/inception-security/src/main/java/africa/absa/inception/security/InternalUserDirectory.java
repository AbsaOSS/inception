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
import africa.absa.inception.core.util.PasswordUtil;
import com.github.f4b6a3.uuid.UuidCreator;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

/**
 * The <b>InternalUserDirectory</b> class provides the internal user directory implementation.
 *
 * @author Marcus Portmann
 */
public class InternalUserDirectory extends UserDirectoryBase {

  /** The default maximum number of filtered groups. */
  private static final int DEFAULT_MAX_FILTERED_GROUPS = 100;

  /** The default maximum number of filtered group members. */
  private static final int DEFAULT_MAX_FILTERED_GROUP_MEMBERS = 100;

  /** The default maximum number of filtered users. */
  private static final int DEFAULT_MAX_FILTERED_USERS = 100;

  /** The default number of failed password attempts before the user is locked. */
  private static final int DEFAULT_MAX_PASSWORD_ATTEMPTS = 5;

  /** The default number of months before a user's password expires. */
  private static final int DEFAULT_PASSWORD_EXPIRY_MONTHS = 3;

  /** The default number of months to check password history against. */
  private static final int DEFAULT_PASSWORD_HISTORY_MONTHS = 12;

  /** The user directory capabilities common to all internal user directory instances. */
  private static final UserDirectoryCapabilities INTERNAL_USER_DIRECTORY_CAPABILITIES =
      new UserDirectoryCapabilities(true, true, true, true, true, true, true, true);

  /** The maximum number of filtered group members to return. */
  private final int maxFilteredGroupMembers;

  /** The maximum number of filtered groups to return. */
  private final int maxFilteredGroups;

  /** The maximum number of filtered users to return. */
  private final int maxFilteredUsers;

  /** The maximum number of password attempts. */
  private final int maxPasswordAttempts;

  /** The password encoder. */
  private final PasswordEncoder passwordEncoder;

  /** The password expiry period in months. */
  private final int passwordExpiryMonths;

  /** The password history period in months. */
  private final int passwordHistoryMonths;

  /**
   * Constructs a new <b>InternalUserDirectory</b>.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) for the user directory
   * @param parameters the parameters for the user directory
   * @param groupRepository the Group Repository
   * @param userRepository the User Repository
   * @param roleRepository the Role Repository
   */
  public InternalUserDirectory(
      UUID userDirectoryId,
      List<UserDirectoryParameter> parameters,
      GroupRepository groupRepository,
      UserRepository userRepository,
      RoleRepository roleRepository)
      throws ServiceUnavailableException {
    super(userDirectoryId, parameters, groupRepository, userRepository, roleRepository);

    this.passwordEncoder = new BCryptPasswordEncoder(10, new SecureRandom());

    try {
      if (UserDirectoryParameter.contains(parameters, "MaxPasswordAttempts")) {
        maxPasswordAttempts =
            UserDirectoryParameter.getIntegerValue(parameters, "MaxPasswordAttempts");
      } else {
        maxPasswordAttempts = DEFAULT_MAX_PASSWORD_ATTEMPTS;
      }

      if (UserDirectoryParameter.contains(parameters, "PasswordExpiryMonths")) {
        passwordExpiryMonths =
            UserDirectoryParameter.getIntegerValue(parameters, "PasswordExpiryMonths");
      } else {
        passwordExpiryMonths = DEFAULT_PASSWORD_EXPIRY_MONTHS;
      }

      if (UserDirectoryParameter.contains(parameters, "PasswordHistoryMonths")) {
        passwordHistoryMonths =
            UserDirectoryParameter.getIntegerValue(parameters, "PasswordHistoryMonths");
      } else {
        passwordHistoryMonths = DEFAULT_PASSWORD_HISTORY_MONTHS;
      }

      if (UserDirectoryParameter.contains(parameters, "MaxFilteredUsers")) {
        maxFilteredUsers = UserDirectoryParameter.getIntegerValue(parameters, "MaxFilteredUsers");
      } else {
        maxFilteredUsers = DEFAULT_MAX_FILTERED_USERS;
      }

      if (UserDirectoryParameter.contains(parameters, "MaxFilteredGroups")) {
        maxFilteredGroups = UserDirectoryParameter.getIntegerValue(parameters, "MaxFilteredGroups");
      } else {
        maxFilteredGroups = DEFAULT_MAX_FILTERED_GROUPS;
      }

      if (UserDirectoryParameter.contains(parameters, "MaxFilteredGroupMembers")) {
        maxFilteredGroupMembers =
            UserDirectoryParameter.getIntegerValue(parameters, "MaxFilteredGroupMembers");
      } else {
        maxFilteredGroupMembers = DEFAULT_MAX_FILTERED_GROUP_MEMBERS;
      }
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to initialize the user directory (" + userDirectoryId + ")", e);
    }
  }

  /**
   * Add the group member to the group.
   *
   * @param groupName the name of the group
   * @param memberType the group member type
   * @param memberName the group member name
   */
  @Override
  public void addMemberToGroup(String groupName, GroupMemberType memberType, String memberName)
      throws GroupNotFoundException, UserNotFoundException, ExistingGroupMemberException,
          ServiceUnavailableException {
    if (memberType != GroupMemberType.USER) {
      throw new ServiceUnavailableException(
          "Unsupported group member type (" + memberType.description() + ")");
    }

    if (isUserInGroup(groupName, memberName)) {
      throw new ExistingGroupMemberException(memberType, memberName);
    }

    addUserToGroup(groupName, memberName);
  }

  /**
   * Add the role to the group.
   *
   * @param groupName the name of the group
   * @param roleCode the code for the role
   */
  @Override
  public void addRoleToGroup(String groupName, String roleCode)
      throws GroupNotFoundException, RoleNotFoundException, ExistingGroupRoleException,
          ServiceUnavailableException {
    try {
      Optional<UUID> groupIdOptional =
          getGroupRepository()
              .getIdByUserDirectoryIdAndNameIgnoreCase(getUserDirectoryId(), groupName);

      if (groupIdOptional.isEmpty()) {
        throw new GroupNotFoundException(groupName);
      }

      if (!getRoleRepository().existsById(roleCode)) {
        throw new RoleNotFoundException(roleCode);
      }

      if (getGroupRepository().countGroupRole(groupIdOptional.get(), roleCode) > 0) {
        throw new ExistingGroupRoleException(roleCode);
      }

      getGroupRepository().addRoleToGroup(groupIdOptional.get(), roleCode);
    } catch (GroupNotFoundException | RoleNotFoundException | ExistingGroupRoleException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to add the role ("
              + roleCode
              + ") to the group ("
              + groupName
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Add the user to the group.
   *
   * @param groupName the name of the group
   * @param username the username for the user
   */
  @Override
  public void addUserToGroup(String groupName, String username)
      throws GroupNotFoundException, UserNotFoundException, ServiceUnavailableException {
    try {
      Optional<UUID> groupIdOptional =
          getGroupRepository()
              .getIdByUserDirectoryIdAndNameIgnoreCase(getUserDirectoryId(), groupName);

      if (groupIdOptional.isEmpty()) {
        throw new GroupNotFoundException(groupName);
      }

      Optional<UUID> userIdOptional =
          getUserRepository()
              .getIdByUserDirectoryIdAndUsernameIgnoreCase(getUserDirectoryId(), username);

      if (userIdOptional.isEmpty()) {
        throw new UserNotFoundException(username);
      }

      getGroupRepository().addUserToGroup(groupIdOptional.get(), userIdOptional.get());
    } catch (GroupNotFoundException | UserNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to add the user ("
              + username
              + ") to the group ("
              + groupName
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Administratively change the password for the user.
   *
   * @param username the username for the user
   * @param newPassword the new password
   * @param expirePassword expire the user's password
   * @param lockUser lock the user
   * @param resetPasswordHistory reset the user's password history
   * @param reason the reason for changing the password
   */
  @Override
  public void adminChangePassword(
      String username,
      String newPassword,
      boolean expirePassword,
      boolean lockUser,
      boolean resetPasswordHistory,
      PasswordChangeReason reason)
      throws UserNotFoundException, ServiceUnavailableException {
    try {
      Optional<UUID> userIdOptional =
          getUserRepository()
              .getIdByUserDirectoryIdAndUsernameIgnoreCase(getUserDirectoryId(), username);

      if (userIdOptional.isEmpty()) {
        throw new UserNotFoundException(username);
      }

      String encodedNewPassword = passwordEncoder.encode(newPassword);

      int passwordAttempts = 0;

      if (lockUser) {
        passwordAttempts = maxPasswordAttempts;
      }

      LocalDateTime passwordExpiry;

      if (expirePassword) {
        passwordExpiry = LocalDateTime.now();
      } else {
        passwordExpiry = LocalDateTime.now();
        passwordExpiry = passwordExpiry.plus(passwordExpiryMonths, ChronoUnit.MONTHS);
      }

      getUserRepository()
          .changePassword(
              userIdOptional.get(),
              encodedNewPassword,
              passwordAttempts,
              Optional.of(passwordExpiry));

      if (resetPasswordHistory) {
        getUserRepository().resetPasswordHistory(userIdOptional.get());
      }

      getUserRepository().savePasswordInPasswordHistory(userIdOptional.get(), encodedNewPassword);
    } catch (UserNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to change the password for the user ("
              + username
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Authenticate the user.
   *
   * @param username the username for the user
   * @param password the password being used to authenticate
   */
  @Override
  public void authenticate(String username, String password)
      throws AuthenticationFailedException, UserLockedException, ExpiredPasswordException,
          UserNotFoundException, ServiceUnavailableException {
    try {
      Optional<User> userOptional =
          getUserRepository()
              .findByUserDirectoryIdAndUsernameIgnoreCase(getUserDirectoryId(), username);

      if (userOptional.isEmpty()) {
        throw new UserNotFoundException(username);
      }

      User user = userOptional.get();

      if ((user.getPasswordAttempts() != null)
          && (user.getPasswordAttempts() >= maxPasswordAttempts)) {
        throw new UserLockedException(username);
      }

      if (!passwordEncoder.matches(password, user.getPassword())) {
        if ((user.getPasswordAttempts() != null) && (user.getPasswordAttempts() != -1)) {
          getUserRepository().incrementPasswordAttempts(user.getId());
        }

        throw new AuthenticationFailedException(
            "Authentication failed for the user (" + username + ")");
      }

      if (user.hasPasswordExpired()) {
        throw new ExpiredPasswordException(username);
      }
    } catch (AuthenticationFailedException
        | UserNotFoundException
        | UserLockedException
        | ExpiredPasswordException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to authenticate the user ("
              + username
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Change the password for the user.
   *
   * @param username the username for the user
   * @param password the password for the user that is used to authorise the operation
   * @param newPassword the new password
   */
  @Override
  public void changePassword(String username, String password, String newPassword)
      throws AuthenticationFailedException, UserLockedException, ExistingPasswordException,
          ServiceUnavailableException {
    try {
      Optional<User> userOptional =
          getUserRepository()
              .findByUserDirectoryIdAndUsernameIgnoreCase(getUserDirectoryId(), username);

      if (userOptional.isEmpty()) {
        throw new AuthenticationFailedException(
            "Authentication failed while attempting to change the password for the user ("
                + username
                + ")");
      }

      User user = userOptional.get();

      if ((user.getPasswordAttempts() != null)
          && (user.getPasswordAttempts() > maxPasswordAttempts)) {
        throw new UserLockedException(username);
      }

      String encodedNewPassword = passwordEncoder.encode(newPassword);

      if (!passwordEncoder.matches(password, user.getPassword())) {
        throw new AuthenticationFailedException(
            "Authentication failed while attempting to change the password for the user ("
                + username
                + ")");
      }

      if (isPasswordInHistory(user.getId(), newPassword)) {
        throw new ExistingPasswordException(username);
      }

      LocalDateTime passwordExpiry = LocalDateTime.now();
      passwordExpiry = passwordExpiry.plus(passwordExpiryMonths, ChronoUnit.MONTHS);

      getUserRepository()
          .changePassword(user.getId(), encodedNewPassword, 0, Optional.of(passwordExpiry));

      getUserRepository().savePasswordInPasswordHistory(user.getId(), encodedNewPassword);
    } catch (AuthenticationFailedException | ExistingPasswordException | UserLockedException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to change the password for the user ("
              + username
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Create the new group.
   *
   * @param group the group
   */
  @Override
  public void createGroup(Group group) throws DuplicateGroupException, ServiceUnavailableException {
    try {
      if (getGroupRepository()
          .existsByUserDirectoryIdAndNameIgnoreCase(getUserDirectoryId(), group.getName())) {
        throw new DuplicateGroupException(group.getName());
      }

      group.setId(UuidCreator.getShortPrefixComb());

      getGroupRepository().saveAndFlush(group);
    } catch (DuplicateGroupException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to create the group ("
              + group.getName()
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Create the new user.
   *
   * @param user the user
   * @param expiredPassword create the user with its password expired
   * @param userLocked create the user locked
   */
  @Override
  public void createUser(User user, boolean expiredPassword, boolean userLocked)
      throws DuplicateUserException, ServiceUnavailableException {
    try {
      if (getUserRepository()
          .existsByUserDirectoryIdAndUsernameIgnoreCase(getUserDirectoryId(), user.getUsername())) {
        throw new DuplicateUserException(user.getUsername());
      }

      user.setId(UuidCreator.getShortPrefixComb());

      String encodedNewPassword;

      if (!isNullOrEmpty(user.getPassword())) {
        encodedNewPassword = passwordEncoder.encode(user.getPassword());
      } else {
        encodedNewPassword = passwordEncoder.encode(PasswordUtil.generateRandomPassword());
      }

      user.setPassword(encodedNewPassword);

      if (userLocked) {
        user.setPasswordAttempts(maxPasswordAttempts);
      } else {
        user.setPasswordAttempts(0);
      }

      if (expiredPassword) {
        user.setPasswordExpiry(LocalDateTime.now());
      } else {
        LocalDateTime passwordExpiry = LocalDateTime.now();

        passwordExpiry = passwordExpiry.plus(passwordExpiryMonths, ChronoUnit.MONTHS);

        user.setPasswordExpiry(passwordExpiry);
      }

      getUserRepository().saveAndFlush(user);

      getUserRepository().savePasswordInPasswordHistory(user.getId(), encodedNewPassword);
    } catch (DuplicateUserException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to create the user ("
              + user.getUsername()
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Delete the group.
   *
   * @param groupName the name of the group
   */
  @Override
  public void deleteGroup(String groupName)
      throws GroupNotFoundException, ExistingGroupMembersException, ServiceUnavailableException {
    try {
      Optional<UUID> groupIdOptional =
          getGroupRepository()
              .getIdByUserDirectoryIdAndNameIgnoreCase(getUserDirectoryId(), groupName);

      if (groupIdOptional.isEmpty()) {
        throw new GroupNotFoundException(groupName);
      }

      if (getGroupRepository().countUsersById(groupIdOptional.get()) > 0) {
        throw new ExistingGroupMembersException(groupName);
      }

      getGroupRepository().deleteById(groupIdOptional.get());
    } catch (GroupNotFoundException | ExistingGroupMembersException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to delete the group ("
              + groupName
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Delete the user.
   *
   * @param username the username for the user
   */
  @Override
  public void deleteUser(String username)
      throws UserNotFoundException, ServiceUnavailableException {
    try {
      Optional<UUID> userIdOptional =
          getUserRepository()
              .getIdByUserDirectoryIdAndUsernameIgnoreCase(getUserDirectoryId(), username);

      if (userIdOptional.isEmpty()) {
        throw new UserNotFoundException(username);
      }

      getUserRepository().deleteById(userIdOptional.get());
    } catch (UserNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to delete the user ("
              + username
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Retrieve the users matching the user attribute criteria.
   *
   * @param userAttributes the user attribute criteria used to select the users
   * @return the users whose attributes match the user attribute criteria
   */
  @Override
  public List<User> findUsers(List<UserAttribute> userAttributes)
      throws InvalidAttributeException, ServiceUnavailableException {
    try {
      User userCriteria = new User();

      userCriteria.setUserDirectoryId(getUserDirectoryId());

      for (UserAttribute userAttribute : userAttributes) {
        if (userAttribute.getName().equalsIgnoreCase("status")) {
          userCriteria.setStatus(UserStatus.fromCode(userAttribute.getValue()));
        } else if (userAttribute.getName().equalsIgnoreCase("email")) {
          userCriteria.setEmail(userAttribute.getValue());
        } else if (userAttribute.getName().equalsIgnoreCase("name")) {
          userCriteria.setName(userAttribute.getValue());
        } else if (userAttribute.getName().equalsIgnoreCase("preferredName")) {
          userCriteria.setPreferredName(userAttribute.getValue());
        } else if (userAttribute.getName().equalsIgnoreCase("phoneNumber")) {
          userCriteria.setPhoneNumber(userAttribute.getValue());
        } else if (userAttribute.getName().equalsIgnoreCase("mobileNumber")) {
          userCriteria.setMobileNumber(userAttribute.getValue());
        } else if (userAttribute.getName().equalsIgnoreCase("username")) {
          userCriteria.setUsername(userAttribute.getValue());
        } else {
          throw new InvalidAttributeException(userAttribute.getName());
        }
      }

      ExampleMatcher matcher =
          ExampleMatcher.matching()
              .withIgnoreNullValues()
              .withIgnoreCase()
              .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

      return getUserRepository().findAll(Example.of(userCriteria, matcher));
    } catch (InvalidAttributeException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to find the users for the user directory (" + getUserDirectoryId() + ")", e);
    }
  }

  /**
   * Retrieve the capabilities the user directory supports.
   *
   * @return the capabilities the user directory supports
   */
  @Override
  public UserDirectoryCapabilities getCapabilities() {
    return INTERNAL_USER_DIRECTORY_CAPABILITIES;
  }

  /**
   * Retrieve the authorised function codes for the user.
   *
   * @param username the username for the user
   * @return the authorised function codes for the user
   */
  @Override
  public List<String> getFunctionCodesForUser(String username)
      throws UserNotFoundException, ServiceUnavailableException {
    try {
      Optional<UUID> userIdOptional =
          getUserRepository()
              .getIdByUserDirectoryIdAndUsernameIgnoreCase(getUserDirectoryId(), username);

      if (userIdOptional.isEmpty()) {
        throw new UserNotFoundException(username);
      }

      return getUserRepository().getFunctionCodesByUserId(userIdOptional.get());
    } catch (UserNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the function codes for the user ("
              + username
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Retrieve the group.
   *
   * @param groupName the name of the group
   * @return the group
   */
  @Override
  public Group getGroup(String groupName)
      throws GroupNotFoundException, ServiceUnavailableException {
    try {
      Optional<Group> groupOptional =
          getGroupRepository()
              .findByUserDirectoryIdAndNameIgnoreCase(getUserDirectoryId(), groupName);

      if (groupOptional.isPresent()) {
        return groupOptional.get();
      } else {
        throw new GroupNotFoundException(groupName);
      }
    } catch (GroupNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the group ("
              + groupName
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Retrieve all the group names.
   *
   * @return the group names
   */
  @Override
  public List<String> getGroupNames() throws ServiceUnavailableException {
    try {
      return getGroupRepository().getNamesByUserDirectoryId(getUserDirectoryId());
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the group names for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Retrieve the names of the groups the user is a member of.
   *
   * @param username the username for the user
   * @return the names of the groups the user is a member of
   */
  @Override
  public List<String> getGroupNamesForUser(String username)
      throws UserNotFoundException, ServiceUnavailableException {
    try {
      Optional<UUID> userIdOptional =
          getUserRepository()
              .getIdByUserDirectoryIdAndUsernameIgnoreCase(getUserDirectoryId(), username);

      if (userIdOptional.isEmpty()) {
        throw new UserNotFoundException(username);
      }

      return getUserRepository().getGroupNamesByUserId(userIdOptional.get());
    } catch (UserNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the names of the groups the user ("
              + username
              + ") is a member of for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Retrieve all the groups.
   *
   * @return the groups
   */
  @Override
  public List<Group> getGroups() throws ServiceUnavailableException {
    try {
      return getGroupRepository().findByUserDirectoryId(getUserDirectoryId());
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the groups for the user directory (" + getUserDirectoryId() + ")", e);
    }
  }

  /**
   * Retrieve the groups.
   *
   * @param filter the optional filter to apply to the groups
   * @param sortDirection the optional sort direction to apply to the groups
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the groups
   */
  @Override
  public Groups getGroups(
      String filter, SortDirection sortDirection, Integer pageIndex, Integer pageSize)
      throws ServiceUnavailableException {
    try {
      if (pageIndex == null) {
        pageIndex = 0;
      }

      if (pageSize == null) {
        pageSize = maxFilteredGroups;
      }

      PageRequest pageRequest =
          PageRequest.of(
              pageIndex,
              Math.min(pageSize, maxFilteredGroups),
              (sortDirection == SortDirection.ASCENDING) ? Sort.Direction.ASC : Sort.Direction.DESC,
              "name");

      Page<Group> groupPage;
      if (StringUtils.hasText(filter)) {
        groupPage =
            getGroupRepository()
                .findFiltered(getUserDirectoryId(), "%" + filter + "%", pageRequest);
      } else {
        groupPage = getGroupRepository().findByUserDirectoryId(getUserDirectoryId(), pageRequest);
      }

      return new Groups(
          getUserDirectoryId(),
          groupPage.toList(),
          groupPage.getTotalElements(),
          filter,
          sortDirection,
          pageIndex,
          pageSize);
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the filtered groups for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Retrieve the groups the user is a member of.
   *
   * @param username the username for the user
   * @return the groups the user is a member of
   */
  @Override
  public List<Group> getGroupsForUser(String username)
      throws UserNotFoundException, ServiceUnavailableException {
    try {
      Optional<UUID> userIdOptional =
          getUserRepository()
              .getIdByUserDirectoryIdAndUsernameIgnoreCase(getUserDirectoryId(), username);

      if (userIdOptional.isEmpty()) {
        throw new UserNotFoundException(username);
      }

      return getUserRepository().getGroupsByUserId(userIdOptional.get());
    } catch (UserNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the groups the user is a member of ("
              + username
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Retrieve the group members for the group.
   *
   * @param groupName the name of the group
   * @return the group members for the group
   */
  @Override
  public List<GroupMember> getMembersForGroup(String groupName)
      throws GroupNotFoundException, ServiceUnavailableException {
    try {
      Optional<UUID> groupIdOptional =
          getGroupRepository()
              .getIdByUserDirectoryIdAndNameIgnoreCase(getUserDirectoryId(), groupName);

      if (groupIdOptional.isEmpty()) {
        throw new GroupNotFoundException(groupName);
      }

      List<String> usernames =
          getGroupRepository().getUsernamesForGroup(getUserDirectoryId(), groupIdOptional.get());

      List<GroupMember> groupMembers = new ArrayList<>();

      for (String username : usernames) {
        groupMembers.add(
            new GroupMember(getUserDirectoryId(), groupName, GroupMemberType.USER, username));
      }

      return groupMembers;
    } catch (GroupNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the group members for the group ("
              + groupName
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Retrieve the group members for the group.
   *
   * @param groupName the name of the group
   * @param filter the optional filter to apply to the group members
   * @param sortDirection the optional sort direction to apply to the group members
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the group members for the group
   */
  @Override
  public GroupMembers getMembersForGroup(
      String groupName,
      String filter,
      SortDirection sortDirection,
      Integer pageIndex,
      Integer pageSize)
      throws GroupNotFoundException, ServiceUnavailableException {
    try {
      Optional<UUID> groupIdOptional =
          getGroupRepository()
              .getIdByUserDirectoryIdAndNameIgnoreCase(getUserDirectoryId(), groupName);

      if (groupIdOptional.isEmpty()) {
        throw new GroupNotFoundException(groupName);
      }

      if (pageIndex == null) {
        pageIndex = 0;
      }

      if (pageSize == null) {
        pageSize = maxFilteredGroups;
      }

      PageRequest pageRequest =
          PageRequest.of(pageIndex, Math.min(pageSize, maxFilteredGroupMembers));

      Page<String> usernamesForGroupPage;

      if (StringUtils.hasText(filter)) {
        usernamesForGroupPage =
            getGroupRepository()
                .getFilteredUsernamesForGroup(
                    getUserDirectoryId(), groupIdOptional.get(), "%" + filter + "%", pageRequest);
      } else {
        usernamesForGroupPage =
            getGroupRepository()
                .getUsernamesForGroup(getUserDirectoryId(), groupIdOptional.get(), pageRequest);
      }

      List<GroupMember> groupMembers = new ArrayList<>();

      for (String username : usernamesForGroupPage) {
        groupMembers.add(
            new GroupMember(getUserDirectoryId(), groupName, GroupMemberType.USER, username));
      }

      if ((sortDirection == null) || (sortDirection == SortDirection.ASCENDING)) {
        groupMembers.sort(Comparator.comparing(GroupMember::getMemberName));
      } else {
        groupMembers.sort(Comparator.comparing(GroupMember::getMemberName).reversed());
      }

      return new GroupMembers(
          getUserDirectoryId(),
          groupName,
          groupMembers,
          usernamesForGroupPage.getTotalElements(),
          filter,
          sortDirection,
          pageIndex,
          pageSize);
    } catch (GroupNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the group members for the group ("
              + groupName
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Retrieve the codes for the roles that have been assigned to the group.
   *
   * @param groupName the name of the group
   * @return the codes for the roles that have been assigned to the group
   */
  @Override
  public List<String> getRoleCodesForGroup(String groupName)
      throws GroupNotFoundException, ServiceUnavailableException {
    try {
      Optional<UUID> groupIdOptional =
          getGroupRepository()
              .getIdByUserDirectoryIdAndNameIgnoreCase(getUserDirectoryId(), groupName);

      if (groupIdOptional.isEmpty()) {
        throw new GroupNotFoundException(groupName);
      }

      return getGroupRepository().getRoleCodesByGroupId(groupIdOptional.get());
    } catch (GroupNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the role codes for the group ("
              + groupName
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Retrieve the codes for the roles that the user has been assigned.
   *
   * @param username the username for the user
   * @return the codes for the roles that the user has been assigned
   */
  @Override
  public List<String> getRoleCodesForUser(String username)
      throws UserNotFoundException, ServiceUnavailableException {
    try {
      Optional<UUID> userIdOptional =
          getUserRepository()
              .getIdByUserDirectoryIdAndUsernameIgnoreCase(getUserDirectoryId(), username);

      if (userIdOptional.isEmpty()) {
        throw new UserNotFoundException(username);
      }

      return getUserRepository().getRoleCodesByUserId(userIdOptional.get());
    } catch (UserNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the role codes for the user ("
              + username
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Retrieve the roles that have been assigned to the group.
   *
   * @param groupName the name of the group
   * @return the roles that have been assigned to the group
   */
  @Override
  public List<GroupRole> getRolesForGroup(String groupName)
      throws GroupNotFoundException, ServiceUnavailableException {
    try {
      Optional<UUID> groupIdOptional =
          getGroupRepository()
              .getIdByUserDirectoryIdAndNameIgnoreCase(getUserDirectoryId(), groupName);

      if (groupIdOptional.isEmpty()) {
        throw new GroupNotFoundException(groupName);
      }

      List<GroupRole> groupRoles = new ArrayList<>();

      for (String roleCode : getGroupRepository().getRoleCodesByGroupId(groupIdOptional.get())) {
        groupRoles.add(new GroupRole(getUserDirectoryId(), groupName, roleCode));
      }

      return groupRoles;
    } catch (GroupNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the roles for the group ("
              + groupName
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Retrieve the user.
   *
   * @param username the username for the user
   * @return the user
   */
  @Override
  public User getUser(String username) throws UserNotFoundException, ServiceUnavailableException {
    try {
      Optional<User> userOptional =
          getUserRepository()
              .findByUserDirectoryIdAndUsernameIgnoreCase(getUserDirectoryId(), username);

      if (userOptional.isPresent()) {
        return userOptional.get();
      } else {
        throw new UserNotFoundException(username);
      }
    } catch (UserNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the user ("
              + username
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Retrieve the name of the user.
   *
   * @param username the username for the user
   * @return the name of the user
   */
  @Override
  public String getUserName(String username)
      throws UserNotFoundException, ServiceUnavailableException {
    try {
      Optional<String> userNameOptional =
          getUserRepository()
              .getNameByUserDirectoryIdAndUsernameIgnoreCase(getUserDirectoryId(), username);

      if (userNameOptional.isPresent()) {
        return userNameOptional.get();
      } else {
        throw new UserNotFoundException(username);
      }
    } catch (UserNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the name of the user ("
              + username
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Retrieve all the users.
   *
   * @return the users
   */
  @Override
  public List<User> getUsers() throws ServiceUnavailableException {
    try {
      return getUserRepository().findByUserDirectoryId(getUserDirectoryId());
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the users for the user directory (" + getUserDirectoryId() + ")", e);
    }
  }

  /**
   * Retrieve the users.
   *
   * @param filter the optional filter to apply to the users
   * @param sortBy the optional method used to sort the users e.g. by name
   * @param sortDirection the optional sort direction to apply to the users
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the users
   */
  @Override
  public Users getUsers(
      String filter,
      UserSortBy sortBy,
      SortDirection sortDirection,
      Integer pageIndex,
      Integer pageSize)
      throws ServiceUnavailableException {
    try {
      PageRequest pageRequest = null;

      if (pageIndex == null) {
        pageIndex = 0;
      }

      if (pageSize == null) {
        pageSize = maxFilteredUsers;
      }

      if (sortBy == UserSortBy.USERNAME) {
        pageRequest =
            PageRequest.of(
                pageIndex,
                Math.min(pageSize, maxFilteredUsers),
                (sortDirection == SortDirection.ASCENDING)
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC,
                "username");
      } else if (sortBy == UserSortBy.NAME) {
        pageRequest =
            PageRequest.of(
                pageIndex,
                Math.min(pageSize, maxFilteredUsers),
                (sortDirection == SortDirection.ASCENDING)
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC,
                "name");
      } else if (sortBy == UserSortBy.PREFERRED_NAME) {
        pageRequest =
            PageRequest.of(
                pageIndex,
                Math.min(pageSize, maxFilteredUsers),
                (sortDirection == SortDirection.ASCENDING)
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC,
                "preferredName");
      }

      Page<User> userPage;
      if (StringUtils.hasText(filter)) {
        userPage =
            getUserRepository().findFiltered(getUserDirectoryId(), "%" + filter + "%", pageRequest);
      } else {
        userPage = getUserRepository().findByUserDirectoryId(getUserDirectoryId(), pageRequest);
      }

      return new Users(
          getUserDirectoryId(),
          userPage.toList(),
          userPage.getTotalElements(),
          filter,
          sortBy,
          sortDirection,
          pageIndex,
          pageSize);
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the filtered users for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Does the user with the specified username exist?
   *
   * @param username the username for the user
   * @return <b>true</b> if a user with specified username exists or <b>false</b> otherwise
   */
  @Override
  public boolean isExistingUser(String username) throws ServiceUnavailableException {
    try {
      return getUserRepository()
          .existsByUserDirectoryIdAndUsernameIgnoreCase(getUserDirectoryId(), username);
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to check whether the user ("
              + username
              + ") is an existing user for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Is the user in the group?
   *
   * @param groupName the name of the group
   * @param username the username for the user
   * @return <b>true</b> if the user is a member of the group or <b>false</b> otherwise
   */
  @Override
  public boolean isUserInGroup(String groupName, String username)
      throws UserNotFoundException, GroupNotFoundException, ServiceUnavailableException {
    try {
      Optional<UUID> userIdOptional =
          getUserRepository()
              .getIdByUserDirectoryIdAndUsernameIgnoreCase(getUserDirectoryId(), username);

      if (userIdOptional.isEmpty()) {
        throw new UserNotFoundException(username);
      }

      Optional<UUID> groupIdOptional =
          getGroupRepository()
              .getIdByUserDirectoryIdAndNameIgnoreCase(getUserDirectoryId(), groupName);

      if (groupIdOptional.isEmpty()) {
        throw new GroupNotFoundException(groupName);
      }

      return getUserRepository().isUserInGroup(userIdOptional.get(), groupIdOptional.get());
    } catch (UserNotFoundException | GroupNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to check if the user ("
              + username
              + ") is in the group ("
              + groupName
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Remove the group member from the group.
   *
   * @param groupName the name of the group
   * @param memberType the group member type
   * @param memberName the group member name
   */
  @Override
  public void removeMemberFromGroup(String groupName, GroupMemberType memberType, String memberName)
      throws GroupNotFoundException, GroupMemberNotFoundException, ServiceUnavailableException {
    if (memberType != GroupMemberType.USER) {
      throw new ServiceUnavailableException(
          "Unsupported group member type (" + memberType.description() + ")");
    }

    try {
      removeUserFromGroup(groupName, memberName);
    } catch (UserNotFoundException e) {
      throw new GroupMemberNotFoundException(memberType, memberName);
    }
  }

  /**
   * Remove the role from the group.
   *
   * @param groupName the name of the group
   * @param roleCode the code for the role
   */
  @Override
  public void removeRoleFromGroup(String groupName, String roleCode)
      throws GroupNotFoundException, GroupRoleNotFoundException, ServiceUnavailableException {
    try {
      Optional<UUID> groupIdOptional =
          getGroupRepository()
              .getIdByUserDirectoryIdAndNameIgnoreCase(getUserDirectoryId(), groupName);

      if (groupIdOptional.isEmpty()) {
        throw new GroupNotFoundException(groupName);
      }

      if (getGroupRepository().removeRoleFromGroup(groupIdOptional.get(), roleCode) == 0) {
        throw new GroupRoleNotFoundException(roleCode);
      }
    } catch (GroupNotFoundException | GroupRoleNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to remove the role ("
              + roleCode
              + ") from the group ("
              + groupName
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Remove the user from the group.
   *
   * @param groupName the name of the group
   * @param username the username for the user
   */
  @Override
  public void removeUserFromGroup(String groupName, String username)
      throws GroupNotFoundException, UserNotFoundException, ServiceUnavailableException {
    try {
      Optional<UUID> groupIdOptional =
          getGroupRepository()
              .getIdByUserDirectoryIdAndNameIgnoreCase(getUserDirectoryId(), groupName);

      if (groupIdOptional.isEmpty()) {
        throw new GroupNotFoundException(groupName);
      }

      Optional<UUID> userIdOptional =
          getUserRepository()
              .getIdByUserDirectoryIdAndUsernameIgnoreCase(getUserDirectoryId(), username);

      if (userIdOptional.isEmpty()) {
        throw new UserNotFoundException(username);
      }

      getGroupRepository().removeUserFromGroup(groupIdOptional.get(), userIdOptional.get());
    } catch (GroupNotFoundException | UserNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to remove the user ("
              + username
              + ") from the group ("
              + groupName
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Reset the password for the user.
   *
   * @param username the username for the user
   * @param newPassword the new password
   */
  @Override
  public void resetPassword(String username, String newPassword)
      throws UserNotFoundException, UserLockedException, ExistingPasswordException,
          ServiceUnavailableException {
    try {
      Optional<User> userOptional =
          getUserRepository()
              .findByUserDirectoryIdAndUsernameIgnoreCase(getUserDirectoryId(), username);

      if (userOptional.isEmpty()) {
        throw new UserNotFoundException(username);
      }

      User user = userOptional.get();

      if ((user.getPasswordAttempts() != null)
          && (user.getPasswordAttempts() > maxPasswordAttempts)) {
        throw new UserLockedException(username);
      }

      String encodedNewPassword = passwordEncoder.encode(newPassword);

      if (isPasswordInHistory(user.getId(), newPassword)) {
        throw new ExistingPasswordException(username);
      }

      LocalDateTime passwordExpiry = LocalDateTime.now();
      passwordExpiry = passwordExpiry.plus(passwordExpiryMonths, ChronoUnit.MONTHS);

      getUserRepository().resetPassword(user.getId(), encodedNewPassword, passwordExpiry);

      getUserRepository().savePasswordInPasswordHistory(user.getId(), encodedNewPassword);
    } catch (UserNotFoundException | UserLockedException | ExistingPasswordException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to reset the password for the user ("
              + username
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Update the group.
   *
   * @param group the group
   */
  @Override
  public void updateGroup(Group group) throws GroupNotFoundException, ServiceUnavailableException {
    try {
      Optional<Group> groupOptional =
          getGroupRepository()
              .findByUserDirectoryIdAndNameIgnoreCase(group.getUserDirectoryId(), group.getName());

      if (groupOptional.isEmpty()) {
        throw new GroupNotFoundException(group.getName());
      }

      Group existingGroup = groupOptional.get();

      existingGroup.setDescription(group.getDescription());

      getGroupRepository().saveAndFlush(existingGroup);
    } catch (GroupNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to update the group ("
              + group.getName()
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Update the user.
   *
   * @param user the user
   * @param expirePassword expire the user's password as part of the update
   * @param lockUser lock the user as part of the update
   */
  @Override
  public void updateUser(User user, boolean expirePassword, boolean lockUser)
      throws UserNotFoundException, ServiceUnavailableException {
    try {
      Optional<User> userOptional =
          getUserRepository()
              .findByUserDirectoryIdAndUsernameIgnoreCase(
                  user.getUserDirectoryId(), user.getUsername());

      if (userOptional.isEmpty()) {
        throw new UserNotFoundException(user.getUsername());
      }

      User existingUser = userOptional.get();

      if (user.getName() != null) {
        existingUser.setName(user.getName());
      }

      if (user.getPreferredName() != null) {
        existingUser.setPreferredName(user.getPreferredName());
      }

      if (user.getEmail() != null) {
        existingUser.setEmail(user.getEmail());
      }

      if (user.getPhoneNumber() != null) {
        existingUser.setPhoneNumber(user.getPhoneNumber());
      }

      if (user.getMobileNumber() != null) {
        existingUser.setMobileNumber(user.getMobileNumber());
      }

      if (StringUtils.hasText(user.getPassword())) {
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
      }

      if (lockUser) {
        existingUser.setPasswordAttempts(maxPasswordAttempts);
      } else if (user.getPasswordAttempts() != null) {
        existingUser.setPasswordAttempts(user.getPasswordAttempts());
      }

      if (expirePassword) {
        existingUser.setPasswordExpiry(LocalDateTime.now());
      } else if (user.getPasswordExpiry() != null) {
        existingUser.setPasswordExpiry(user.getPasswordExpiry());
      }

      getUserRepository().saveAndFlush(existingUser);
    } catch (UserNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to update the user ("
              + user.getUsername()
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    }
  }

  /**
   * Is the password a historical password that cannot be reused for a period of time i.e. was the
   * password used previously in the last X months.
   *
   * @param userId the Universally Unique Identifier (UUID) for the user
   * @param password the password
   * @return <b>true</b> if the password was previously used and cannot be reused for a period of
   *     time or <b>false</b> otherwise
   */
  private boolean isPasswordInHistory(UUID userId, String password) {
    LocalDateTime after = LocalDateTime.now();
    after = after.minus(passwordHistoryMonths, ChronoUnit.MONTHS);

    for (String historicalPassword : getUserRepository().getPasswordHistory(userId, after)) {
      if (passwordEncoder.matches(password, historicalPassword)) {
        return true;
      }
    }

    return false;
  }
}
