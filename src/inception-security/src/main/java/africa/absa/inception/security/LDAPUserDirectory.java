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
import africa.absa.inception.core.util.JNDIUtil;
import com.github.f4b6a3.uuid.UuidCreator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;

/**
 * The <b>LDAPUserDirectory</b> class provides the LDAP user directory implementation.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings({"unused", "Duplicates", "SpringJavaAutowiredMembersInspection"})
public class LDAPUserDirectory extends UserDirectoryBase {

  /** The default maximum number of filtered groups. */
  private static final int DEFAULT_MAX_FILTERED_GROUPS = 100;

  /** The default maximum number of filtered group members. */
  private static final int DEFAULT_MAX_FILTERED_GROUP_MEMBERS = 100;

  /** The default maximum number of filtered users. */
  private static final int DEFAULT_MAX_FILTERED_USERS = 100;

  /** The empty attribute list. */
  private static final String[] EMPTY_ATTRIBUTE_LIST = new String[0];

  /* Logger */
  private static final Logger logger = LoggerFactory.getLogger(LDAPUserDirectory.class);

  private final LdapName baseDN;

  private final String bindDN;

  private final String bindPassword;

  /** The user directory capabilities supported by this user directory instance. */
  private final UserDirectoryCapabilities capabilities;

  private final LdapName groupBaseDN;

  private final String groupMemberAttribute;

  private final String[] groupMemberAttributeArray;

  private final String groupNameAttribute;

  private final String groupObjectClass;

  private final String host;

  /** The maximum number of filtered group members to return. */
  private final int maxFilteredGroupMembers;

  /** The maximum number of filtered groups to return. */
  private final int maxFilteredGroups;

  /** The maximum number of filtered users to return. */
  private final int maxFilteredUsers;

  private final int port;

  private final boolean useSSL;

  private final LdapName userBaseDN;

  private final String userEmailAttribute;

  private final String userMobileNumberAttribute;

  private final String userNameAttribute;

  private final String userObjectClass;

  private final String userPhoneNumberAttribute;

  private final String userPreferredNameAttribute;

  private final String userUsernameAttribute;

  /** The data source used to provide connections to the application database. */
  @Autowired
  @Qualifier("applicationDataSource")
  private DataSource dataSource;

  private String groupDescriptionAttribute;

  /**
   * Constructs a new <b>LDAPUserDirectory</b>.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) for the user directory
   * @param parameters the parameters for the user directory
   * @param groupRepository the Group Repository
   * @param userRepository the User Repository
   * @param roleRepository the Role Repository
   * @throws ServiceUnavailableException if the LDAP user directory could not be initialized
   */
  public LDAPUserDirectory(
      UUID userDirectoryId,
      List<UserDirectoryParameter> parameters,
      GroupRepository groupRepository,
      UserRepository userRepository,
      RoleRepository roleRepository)
      throws ServiceUnavailableException {
    super(userDirectoryId, parameters, groupRepository, userRepository, roleRepository);

    try {
      if (UserDirectoryParameter.contains(parameters, "Host")) {
        host = UserDirectoryParameter.getStringValue(parameters, "Host");
      } else {
        throw new ServiceUnavailableException(
            "No Host parameter found for the user directory (" + userDirectoryId + ")");
      }

      if (UserDirectoryParameter.contains(parameters, "Port")) {
        port = UserDirectoryParameter.getIntegerValue(parameters, "Port");
      } else {
        throw new ServiceUnavailableException(
            "No Port parameter found for the user directory (" + userDirectoryId + ")");
      }

      useSSL =
          UserDirectoryParameter.contains(parameters, "UseSSL")
              && Boolean.parseBoolean(UserDirectoryParameter.getStringValue(parameters, "UseSSL"));

      if (UserDirectoryParameter.contains(parameters, "BindDN")) {
        bindDN = UserDirectoryParameter.getStringValue(parameters, "BindDN");
      } else {
        throw new ServiceUnavailableException(
            "No BindDN parameter found for the user directory (" + userDirectoryId + ")");
      }

      if (UserDirectoryParameter.contains(parameters, "BindPassword")) {
        bindPassword = UserDirectoryParameter.getStringValue(parameters, "BindPassword");
      } else {
        throw new ServiceUnavailableException(
            "No BindPassword parameter found for the user directory (" + userDirectoryId + ")");
      }

      if (UserDirectoryParameter.contains(parameters, "BaseDN")) {
        baseDN = new LdapName(UserDirectoryParameter.getStringValue(parameters, "BaseDN"));
      } else {
        throw new ServiceUnavailableException(
            "No BindDN parameter found for the user directory (" + userDirectoryId + ")");
      }

      if (UserDirectoryParameter.contains(parameters, "UserBaseDN")) {
        userBaseDN = new LdapName(UserDirectoryParameter.getStringValue(parameters, "UserBaseDN"));
      } else {
        throw new ServiceUnavailableException(
            "No UserBaseDN parameter found for the user directory (" + userDirectoryId + ")");
      }

      if (UserDirectoryParameter.contains(parameters, "GroupBaseDN")) {
        groupBaseDN =
            new LdapName(UserDirectoryParameter.getStringValue(parameters, "GroupBaseDN"));
      } else {
        throw new ServiceUnavailableException(
            "No GroupBaseDN parameter found for the user directory (" + userDirectoryId + ")");
      }

      if (UserDirectoryParameter.contains(parameters, "UserObjectClass")) {
        userObjectClass = UserDirectoryParameter.getStringValue(parameters, "UserObjectClass");
      } else {
        throw new ServiceUnavailableException(
            "No UserObjectClass parameter found for the user directory (" + userDirectoryId + ")");
      }

      if (UserDirectoryParameter.contains(parameters, "UserUsernameAttribute")) {
        userUsernameAttribute =
            UserDirectoryParameter.getStringValue(parameters, "UserUsernameAttribute");
      } else {
        throw new ServiceUnavailableException(
            "No UserUsernameAttribute parameter found for the user directory ("
                + userDirectoryId
                + ")");
      }

      if (UserDirectoryParameter.contains(parameters, "UserNameAttribute")) {
        userNameAttribute = UserDirectoryParameter.getStringValue(parameters, "UserNameAttribute");
      } else {
        throw new ServiceUnavailableException(
            "No UserNameAttribute parameter found for the user directory ("
                + userDirectoryId
                + ")");
      }

      if (UserDirectoryParameter.contains(parameters, "UserPreferredNameAttribute")) {
        userPreferredNameAttribute =
            UserDirectoryParameter.getStringValue(parameters, "UserPreferredNameAttribute");
      } else {
        throw new ServiceUnavailableException(
            "No UserPreferredNameAttribute parameter found for the user directory ("
                + userDirectoryId
                + ")");
      }

      if (UserDirectoryParameter.contains(parameters, "UserPhoneNumberAttribute")) {
        userPhoneNumberAttribute =
            UserDirectoryParameter.getStringValue(parameters, "UserPhoneNumberAttribute");
      } else {
        throw new ServiceUnavailableException(
            "No UserPhoneNumberAttribute parameter found for the user directory ("
                + userDirectoryId
                + ")");
      }

      if (UserDirectoryParameter.contains(parameters, "UserMobileNumberAttribute")) {
        userMobileNumberAttribute =
            UserDirectoryParameter.getStringValue(parameters, "UserMobileNumberAttribute");
      } else {
        throw new ServiceUnavailableException(
            "No UserMobileNumberAttribute parameter found for the user directory ("
                + userDirectoryId
                + ")");
      }

      if (UserDirectoryParameter.contains(parameters, "UserEmailAttribute")) {
        userEmailAttribute =
            UserDirectoryParameter.getStringValue(parameters, "UserEmailAttribute");
      } else {
        throw new ServiceUnavailableException(
            "No UserEmailAttribute parameter found for the user directory ("
                + userDirectoryId
                + ")");
      }

      if (UserDirectoryParameter.contains(parameters, "GroupObjectClass")) {
        groupObjectClass = UserDirectoryParameter.getStringValue(parameters, "GroupObjectClass");
      } else {
        throw new ServiceUnavailableException(
            "No GroupObjectClass parameter found for the user directory (" + userDirectoryId + ")");
      }

      if (UserDirectoryParameter.contains(parameters, "GroupNameAttribute")) {
        groupNameAttribute =
            UserDirectoryParameter.getStringValue(parameters, "GroupNameAttribute");
      } else {
        throw new ServiceUnavailableException(
            "No GroupNameAttribute parameter found for the user directory ("
                + userDirectoryId
                + ")");
      }

      if (UserDirectoryParameter.contains(parameters, "GroupMemberAttribute")) {
        groupMemberAttribute =
            UserDirectoryParameter.getStringValue(parameters, "GroupMemberAttribute");

        groupMemberAttributeArray = new String[] {groupMemberAttribute};
      } else {
        throw new ServiceUnavailableException(
            "No GroupMemberAttribute parameter found for the user directory ("
                + userDirectoryId
                + ")");
      }

      if (UserDirectoryParameter.contains(parameters, "GroupDescriptionAttribute")) {
        groupDescriptionAttribute =
            UserDirectoryParameter.getStringValue(parameters, "GroupDescriptionAttribute");
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

      boolean supportsAdminChangePassword = true;
      if (UserDirectoryParameter.contains(parameters, "SupportsAdminChangePassword")) {
        supportsAdminChangePassword =
            UserDirectoryParameter.getBooleanValue(parameters, "SupportsAdminChangePassword");
      }

      boolean supportsChangePassword = true;
      if (UserDirectoryParameter.contains(parameters, "SupportsChangePassword")) {
        supportsChangePassword =
            UserDirectoryParameter.getBooleanValue(parameters, "SupportsChangePassword");
      }

      boolean supportsGroupAdministration = true;
      if (UserDirectoryParameter.contains(parameters, "SupportsGroupAdministration")) {
        supportsGroupAdministration =
            UserDirectoryParameter.getBooleanValue(parameters, "SupportsGroupAdministration");
      }

      boolean supportsGroupMemberAdministration = true;
      if (UserDirectoryParameter.contains(parameters, "SupportsGroupMemberAdministration")) {
        supportsGroupMemberAdministration =
            UserDirectoryParameter.getBooleanValue(parameters, "SupportsGroupMemberAdministration");
      }

      boolean supportsUserAdministration = true;
      if (UserDirectoryParameter.contains(parameters, "SupportsUserAdministration")) {
        supportsUserAdministration =
            UserDirectoryParameter.getBooleanValue(parameters, "SupportsUserAdministration");
      }

      capabilities =
          new UserDirectoryCapabilities(
              supportsAdminChangePassword,
              supportsChangePassword,
              supportsGroupAdministration,
              supportsGroupMemberAdministration,
              false,
              false,
              supportsUserAdministration,
              false);
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to initialize the user directory (" + userDirectoryId + ")", e);
    }
  }

  @Override
  public void addMemberToGroup(String groupName, GroupMemberType memberType, String memberName)
      throws GroupNotFoundException, UserNotFoundException, ServiceUnavailableException {
    if (!capabilities.getSupportsGroupMemberAdministration()) {
      throw new ServiceUnavailableException(
          "The group member administration capability is not supported for the user directory ("
              + getUserDirectoryId()
              + "");
    }

    if (memberType != GroupMemberType.USER) {
      throw new ServiceUnavailableException(
          "Unsupported group member type (" + memberType.description() + ")");
    }

    if (isUserInGroup(groupName, memberName)) {
      return;
    }

    addUserToGroup(groupName, memberName);
  }

  @Override
  public void addRoleToGroup(String groupName, String roleCode)
      throws GroupNotFoundException, RoleNotFoundException, ServiceUnavailableException {
    DirContext dirContext = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      LdapName groupDN = getGroupDN(dirContext, groupName);

      if (groupDN == null) {
        throw new GroupNotFoundException(groupName);
      }

      if (!getRoleRepository().existsById(roleCode)) {
        throw new RoleNotFoundException(roleCode);
      }

      UUID groupId;

      Optional<UUID> groupIdOptional =
          getGroupRepository()
              .getIdByUserDirectoryIdAndNameIgnoreCase(getUserDirectoryId(), groupName);

      if (groupIdOptional.isPresent()) {
        groupId = groupIdOptional.get();
      } else {
        groupId = UuidCreator.getShortPrefixComb();

        Group group = getGroup(groupName);

        group.setId(groupId);

        getGroupRepository().saveAndFlush(group);
      }

      if (getGroupRepository().countGroupRole(groupId, roleCode) > 0) {
        return;
      }

      getGroupRepository().addRoleToGroup(groupId, roleCode);
    } catch (GroupNotFoundException | RoleNotFoundException e) {
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
    } finally {
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public void addUserToGroup(String groupName, String username)
      throws UserNotFoundException, GroupNotFoundException, ServiceUnavailableException {
    if (!capabilities.getSupportsGroupMemberAdministration()) {
      throw new ServiceUnavailableException(
          "The group member administration capability is not supported for the user directory ("
              + getUserDirectoryId()
              + "");
    }

    DirContext dirContext = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      LdapName userDN = getUserDN(dirContext, username);

      if (userDN == null) {
        throw new UserNotFoundException(username);
      }

      LdapName groupDN = getGroupDN(dirContext, groupName);

      if (groupDN == null) {
        throw new GroupNotFoundException(groupName);
      }

      Attributes attributes = dirContext.getAttributes(groupDN, groupMemberAttributeArray);

      BasicAttribute attribute = new BasicAttribute(groupMemberAttribute);

      if (attributes.get(groupMemberAttribute) != null) {
        @SuppressWarnings("unchecked")
        NamingEnumeration<String> groupMembers =
            (NamingEnumeration<String>) attributes.get(groupMemberAttribute).getAll();

        while (groupMembers.hasMore()) {
          LdapName groupMemberDN = new LdapName(groupMembers.next());

          if (groupMemberDN.equals(userDN)) {
            return;
          } else {
            attribute.add(groupMemberDN.toString());
          }
        }
      }

      attribute.add(userDN.toString());

      dirContext.modifyAttributes(
          groupDN,
          new ModificationItem[] {new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attribute)});
    } catch (UserNotFoundException | GroupNotFoundException e) {
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
    } finally {
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public void adminChangePassword(
      String username,
      String newPassword,
      boolean expirePassword,
      boolean lockUser,
      boolean resetPasswordHistory,
      PasswordChangeReason reason)
      throws UserNotFoundException, ServiceUnavailableException {
    if (!capabilities.getSupportsAdminChangePassword()) {
      throw new ServiceUnavailableException(
          "The admin change password capability is not supported for the user directory ("
              + getUserDirectoryId()
              + "");
    }

    DirContext dirContext = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      LdapName userDN = getUserDN(dirContext, username);

      if (userDN == null) {
        throw new UserNotFoundException(username);
      }

      List<ModificationItem> modificationItems = new ArrayList<>();

      BasicAttribute passwordAttribute = new BasicAttribute("userPassword");
      passwordAttribute.add(newPassword);

      modificationItems.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, passwordAttribute));

      dirContext.modifyAttributes(userDN, modificationItems.toArray(new ModificationItem[0]));
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
    } finally {
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public void authenticate(String username, String password)
      throws AuthenticationFailedException, UserLockedException, ExpiredPasswordException,
          UserNotFoundException, ServiceUnavailableException {
    DirContext dirContext = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      LdapName userDN = getUserDN(dirContext, username);

      if (userDN == null) {
        throw new UserNotFoundException(username);
      }

      DirContext userDirContext = null;

      try {
        userDirContext = getDirContext(userDN.toString(), password);
      } catch (Throwable e) {
        if (e.getCause() instanceof javax.naming.AuthenticationException) {
          throw new AuthenticationFailedException(
              "Failed to authenticate the user ("
                  + username
                  + ") for the user directory ("
                  + getUserDirectoryId()
                  + ")");
        } else {
          logger.error(
              "Failed to authenticate the user ("
                  + username
                  + ") for the user directory ("
                  + getUserDirectoryId()
                  + ")",
              e);

          throw new AuthenticationFailedException(
              "Failed to authenticate the user ("
                  + username
                  + ") for the user directory ("
                  + getUserDirectoryId()
                  + ")",
              e);
        }
      } finally {
        JNDIUtil.close(userDirContext);
      }
    } catch (AuthenticationFailedException | UserNotFoundException e) {
      throw e;
    }

    // TODO: FIX THIS IMPLEMENTATION BY CORRECTLY INTERPRETING USER LOCKED ERROR -- MARCUS
    //  catch (AuthenticationFailedException | UserNotFoundException | UserLockedException
    //      | ExpiredPasswordException e)
    //  {
    //    throw e;
    //  }
    catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to authenticate the user ("
              + username
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    } finally {
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public void changePassword(String username, String password, String newPassword)
      throws AuthenticationFailedException, UserLockedException, ExistingPasswordException,
          ServiceUnavailableException {
    if (!capabilities.getSupportsChangePassword()) {
      throw new ServiceUnavailableException(
          "The change password capability is not supported for the user directory ("
              + getUserDirectoryId()
              + "");
    }

    DirContext dirContext = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      LdapName userDN = getUserDN(dirContext, username);

      if (userDN == null) {
        throw new AuthenticationFailedException(
            "Authentication failed while attempting to change the password for the user ("
                + username
                + ")");
      }

      DirContext userDirContext = null;

      try {
        userDirContext = getDirContext(userDN.toString(), password);
      } catch (Throwable e) {
        if (e.getCause() instanceof javax.naming.AuthenticationException) {
          throw new AuthenticationFailedException(
              "Authentication failed while attempting to change the password for the user ("
                  + username
                  + ")");
        } else {
          logger.error(
              "Failed to authenticate the user ("
                  + username
                  + ") for the user directory ("
                  + getUserDirectoryId()
                  + ")",
              e);

          throw new AuthenticationFailedException(
              "Failed to authenticate the user ("
                  + username
                  + ") for the user directory ("
                  + getUserDirectoryId()
                  + ")",
              e);
        }
      } finally {
        JNDIUtil.close(userDirContext);
      }

      List<ModificationItem> modificationItems = new ArrayList<>();

      BasicAttribute passwordAttribute = new BasicAttribute("userPassword");
      passwordAttribute.add(newPassword);

      modificationItems.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, passwordAttribute));

      dirContext.modifyAttributes(userDN, modificationItems.toArray(new ModificationItem[0]));
    } catch (AuthenticationFailedException e) {
      throw e;
    }

    // TODO: FIX THIS IMPLEMENTATION BY CORRECTLY INTERPRETING USER LOCKED ERROR -- MARCUS
    // catch (AuthenticationFailedException | UserNotFoundException | UserLockedException
    // | ExpiredPasswordException e)
    // {
    // throw e;
    // }
    catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to change the password for the user ("
              + username
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    } finally {
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public void createGroup(Group group) throws DuplicateGroupException, ServiceUnavailableException {
    if (!capabilities.getSupportsGroupAdministration()) {
      throw new ServiceUnavailableException(
          "The group administration capability is not supported for the user directory ("
              + getUserDirectoryId()
              + "");
    }

    DirContext dirContext = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      LdapName groupDN = getGroupDN(dirContext, group.getName());

      if (groupDN != null) {
        throw new DuplicateGroupException(group.getName());
      } else {
        groupDN =
            new LdapName(groupNameAttribute + "=" + group.getName() + "," + groupBaseDN.toString());
      }

      Attributes attributes = new BasicAttributes();

      attributes.put(new BasicAttribute("objectclass", "top"));
      attributes.put(new BasicAttribute("objectclass", groupObjectClass));

      /*
       * This is a "hack" to get around the problem where the groupOfNames object class does not
       * support empty groups. We add the group as a member of itself. This "fake" group member
       * will be filtered when returning the group members.
       */
      if (groupObjectClass.equalsIgnoreCase("groupOfNames")) {
        attributes.put(new BasicAttribute(groupMemberAttribute, groupDN.toString()));
      }

      attributes.put(new BasicAttribute(groupNameAttribute, group.getName()));

      if (StringUtils.hasText(groupDescriptionAttribute)) {
        attributes.put(
            new BasicAttribute(
                groupDescriptionAttribute,
                StringUtils.hasText(group.getDescription()) ? group.getDescription() : ""));
      }

      dirContext.bind(groupDN, dirContext, attributes);

      /*
       * Create the corresponding group in the database that will be used to map to one or more
       * roles.
       */
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
    } finally {
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public void createUser(User user, boolean expiredPassword, boolean userLocked)
      throws DuplicateUserException, ServiceUnavailableException {
    if (!capabilities.getSupportsUserAdministration()) {
      throw new ServiceUnavailableException(
          "The user administration capability is not supported for the user directory ("
              + getUserDirectoryId()
              + "");
    }

    DirContext dirContext = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      LdapName userDN = getUserDN(dirContext, user.getUsername());

      if (userDN != null) {
        throw new DuplicateUserException(user.getUsername());
      }

      Attributes attributes = new BasicAttributes();

      attributes.put(new BasicAttribute("objectclass", "top"));
      attributes.put(new BasicAttribute("objectclass", userObjectClass));

      attributes.put(new BasicAttribute(userUsernameAttribute, user.getUsername()));

      if ((StringUtils.hasText(userNameAttribute)) && (StringUtils.hasText(user.getName()))) {
        attributes.put(new BasicAttribute(userNameAttribute, user.getName()));
      }

      if ((StringUtils.hasText(userPreferredNameAttribute))
          && (StringUtils.hasText(user.getPreferredName()))) {
        attributes.put(new BasicAttribute(userPreferredNameAttribute, user.getPreferredName()));
      }

      if ((StringUtils.hasText(userEmailAttribute)) && (StringUtils.hasText(user.getEmail()))) {
        attributes.put(new BasicAttribute(userEmailAttribute, user.getEmail()));
      }

      if ((StringUtils.hasText(userPhoneNumberAttribute))
          && (StringUtils.hasText(user.getPhoneNumber()))) {
        attributes.put(new BasicAttribute(userPhoneNumberAttribute, user.getPhoneNumber()));
      }

      if ((StringUtils.hasText(userMobileNumberAttribute))
          && (StringUtils.hasText(user.getMobileNumber()))) {
        attributes.put(new BasicAttribute(userMobileNumberAttribute, user.getMobileNumber()));
      }

      attributes.put(
          new BasicAttribute(
              "userPassword", StringUtils.hasText(user.getPassword()) ? user.getPassword() : ""));

      userDN =
          new LdapName(
              userUsernameAttribute + "=" + user.getUsername() + "," + userBaseDN.toString());

      dirContext.bind(userDN, dirContext, attributes);
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
    } finally {
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public void deleteGroup(String groupName)
      throws GroupNotFoundException, ExistingGroupMembersException, ServiceUnavailableException {
    if (!capabilities.getSupportsGroupAdministration()) {
      throw new ServiceUnavailableException(
          "The group administration capability is not supported for the user directory ("
              + getUserDirectoryId()
              + "");
    }

    DirContext dirContext = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      LdapName groupDN = getGroupDN(dirContext, groupName);

      if (groupDN == null) {
        throw new GroupNotFoundException(groupName);
      }

      Attributes attributes = dirContext.getAttributes(groupDN, groupMemberAttributeArray);

      if (attributes.get(groupMemberAttribute) != null) {
        var attribute = attributes.get(groupMemberAttribute);

        if (attribute.size() == 1) {
          LdapName lastGroupMember = new LdapName(String.valueOf(attribute.get(0)));

          if (!lastGroupMember.equals(groupDN)) {
            throw new ExistingGroupMembersException(groupName);
          }
        } else if (attribute.size() > 1) {
          throw new ExistingGroupMembersException(groupName);
        }
      }

      dirContext.destroySubcontext(groupDN);

      // Delete the corresponding group in the database
      Optional<UUID> groupIdOptional =
          getGroupRepository()
              .getIdByUserDirectoryIdAndNameIgnoreCase(getUserDirectoryId(), groupName);

      groupIdOptional.ifPresent(uuid -> getGroupRepository().deleteById(uuid));
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
    } finally {
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public void deleteUser(String username)
      throws UserNotFoundException, ServiceUnavailableException {
    if (!capabilities.getSupportsUserAdministration()) {
      throw new ServiceUnavailableException(
          "The user administration capability is not supported for the user directory ("
              + getUserDirectoryId()
              + "");
    }

    DirContext dirContext = null;
    NamingEnumeration<SearchResult> searchResults = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      LdapName userDN = getUserDN(dirContext, username);

      if (userDN == null) {
        throw new UserNotFoundException(username);
      }

      // Remove the user from any groups
      String searchFilter =
          String.format(
              "(&(objectClass=%s)(%s=%s))",
              groupObjectClass, groupMemberAttribute, userDN.toString());

      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      searchControls.setReturningObjFlag(false);

      searchResults = dirContext.search(groupBaseDN, searchFilter, searchControls);

      while (searchResults.hasMore()) {
        SearchResult searchResult = searchResults.next();

        Attributes attributes = searchResult.getAttributes();

        BasicAttribute attribute = new BasicAttribute(groupMemberAttribute);

        if (attributes.get(groupMemberAttribute) != null) {
          @SuppressWarnings("unchecked")
          NamingEnumeration<String> groupMembers =
              (NamingEnumeration<String>) attributes.get(groupMemberAttribute).getAll();

          while (groupMembers.hasMore()) {
            LdapName groupMemberDN = new LdapName(groupMembers.next());

            if (!groupMemberDN.equals(userDN)) {
              attribute.add(groupMemberDN.toString());
            }
          }
        }

        if (attribute.size() > 0) {
          dirContext.modifyAttributes(
              new LdapName(searchResult.getNameInNamespace()),
              new ModificationItem[] {
                new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attribute)
              });
        } else {
          dirContext.modifyAttributes(
              new LdapName(searchResult.getNameInNamespace()),
              new ModificationItem[] {
                new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attribute)
              });
        }
      }

      dirContext.destroySubcontext(userDN);
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
    } finally {
      JNDIUtil.close(searchResults);
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public List<User> findUsers(List<UserAttribute> userAttributes)
      throws InvalidAttributeException, ServiceUnavailableException {
    DirContext dirContext = null;
    NamingEnumeration<SearchResult> searchResults = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      String searchFilter = "(objectClass=" + userObjectClass + ")";

      if (userAttributes.size() > 0) {
        StringBuilder buffer = new StringBuilder();

        buffer.append("(&(objectClass=");
        buffer.append(userObjectClass);
        buffer.append(")");

        for (UserAttribute userAttribute : userAttributes) {
          buffer.append("(");
          buffer.append(userAttribute.getName());
          buffer.append("=*");

          buffer.append(userAttribute.getValue());

          buffer.append("*)");
        }

        buffer.append(")");

        searchFilter = buffer.toString();
      }

      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      searchControls.setReturningObjFlag(false);
      searchControls.setCountLimit(maxFilteredUsers);

      List<User> users = new ArrayList<>();

      searchResults = dirContext.search(userBaseDN, searchFilter, searchControls);

      while (searchResults.hasMore() && (users.size() <= maxFilteredUsers)) {
        users.add(buildUserFromSearchResult(searchResults.next()));
      }

      return users;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to find the users for the user directory (" + getUserDirectoryId() + ")", e);
    } finally {
      JNDIUtil.close(searchResults);
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public UserDirectoryCapabilities getCapabilities() {
    return capabilities;
  }

  @Override
  public List<String> getFunctionCodesForUser(String username)
      throws UserNotFoundException, ServiceUnavailableException {
    DirContext dirContext = null;
    NamingEnumeration<SearchResult> searchResults = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      LdapName userDN = getUserDN(dirContext, username);

      if (userDN == null) {
        throw new UserNotFoundException(username);
      }

      String searchFilter =
          String.format(
              "(&(objectClass=%s)(%s=%s))",
              groupObjectClass, groupMemberAttribute, userDN.toString());

      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      searchControls.setReturningObjFlag(false);

      searchResults = dirContext.search(groupBaseDN, searchFilter, searchControls);

      List<String> groupNames = new ArrayList<>();

      while (searchResults.hasMore()) {
        SearchResult searchResult = searchResults.next();

        if (searchResult.getAttributes().get(groupNameAttribute) != null) {
          groupNames.add(
              String.valueOf(searchResult.getAttributes().get(groupNameAttribute).get())
                  .toLowerCase());
        }
      }

      if (groupNames.isEmpty()) {
        return new ArrayList<>();
      }

      return getGroupRepository()
          .getFunctionCodesByUserDirectoryIdAndGroupNames(getUserDirectoryId(), groupNames);
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
    } finally {
      JNDIUtil.close(searchResults);
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public Group getGroup(String groupName)
      throws GroupNotFoundException, ServiceUnavailableException {
    DirContext dirContext = null;
    NamingEnumeration<SearchResult> searchResults = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      String searchFilter =
          String.format(
              "(&(objectClass=%s)(%s=%s))", groupObjectClass, groupNameAttribute, groupName);

      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      searchControls.setReturningObjFlag(false);

      searchResults = dirContext.search(groupBaseDN, searchFilter, searchControls);

      if (searchResults.hasMore()) {
        return buildGroupFromSearchResult(searchResults.next());
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
    } finally {
      JNDIUtil.close(searchResults);
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public List<String> getGroupNames() throws ServiceUnavailableException {
    DirContext dirContext = null;
    NamingEnumeration<SearchResult> searchResults = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      String searchFilter = String.format("(objectClass=%s)", groupObjectClass);

      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      searchControls.setReturningObjFlag(false);
      searchControls.setCountLimit(maxFilteredGroups);
      searchControls.setReturningAttributes(new String[] {groupNameAttribute});

      searchResults = dirContext.search(groupBaseDN, searchFilter, searchControls);

      List<String> groupNames = new ArrayList<>();

      while (searchResults.hasMore() && (groupNames.size() <= maxFilteredGroups)) {
        SearchResult searchResult = searchResults.next();

        Attributes attributes = searchResult.getAttributes();

        groupNames.add(String.valueOf(attributes.get(groupNameAttribute).get()));
      }

      return groupNames;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the group names for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    } finally {
      JNDIUtil.close(searchResults);
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public List<String> getGroupNamesForUser(String username)
      throws UserNotFoundException, ServiceUnavailableException {
    DirContext dirContext = null;
    NamingEnumeration<SearchResult> searchResults = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      LdapName userDN = getUserDN(dirContext, username);

      if (userDN == null) {
        throw new UserNotFoundException(username);
      }

      String searchFilter =
          String.format(
              "(&(objectClass=%s)(%s=%s))",
              groupObjectClass, groupMemberAttribute, userDN.toString());

      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      searchControls.setReturningObjFlag(false);

      searchResults = dirContext.search(groupBaseDN, searchFilter, searchControls);

      List<String> groupNames = new ArrayList<>();

      while (searchResults.hasMore()) {
        SearchResult searchResult = searchResults.next();

        if (searchResult.getAttributes().get(groupNameAttribute) != null) {
          groupNames.add(
              String.valueOf(searchResult.getAttributes().get(groupNameAttribute).get()));
        }
      }

      return groupNames;
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
    } finally {
      JNDIUtil.close(searchResults);
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public List<Group> getGroups() throws ServiceUnavailableException {
    DirContext dirContext = null;
    NamingEnumeration<SearchResult> searchResults = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      String searchFilter = String.format("(objectClass=%s)", groupObjectClass);

      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      searchControls.setReturningObjFlag(false);
      searchControls.setCountLimit(maxFilteredGroups);

      searchResults = dirContext.search(groupBaseDN, searchFilter, searchControls);

      List<Group> groups = new ArrayList<>();

      while (searchResults.hasMore() && (groups.size() <= maxFilteredGroups)) {
        groups.add(buildGroupFromSearchResult(searchResults.next()));
      }

      return groups;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the groups for the user directory (" + getUserDirectoryId() + ")", e);
    } finally {
      JNDIUtil.close(searchResults);
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public Groups getGroups(
      String filter, SortDirection sortDirection, Integer pageIndex, Integer pageSize)
      throws ServiceUnavailableException {
    DirContext dirContext = null;
    NamingEnumeration<SearchResult> searchResults = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      String searchFilter;

      if (StringUtils.hasText(filter)) {
        searchFilter =
            String.format(
                "(&(objectClass=%s)(%s=*%s*))", groupObjectClass, groupNameAttribute, filter);
      } else {
        searchFilter = String.format("(objectClass=%s)", groupObjectClass);
      }

      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      searchControls.setReturningObjFlag(false);
      searchControls.setCountLimit(maxFilteredGroups);

      searchResults = dirContext.search(groupBaseDN, searchFilter, searchControls);

      List<Group> groups = new ArrayList<>();

      while (searchResults.hasMore() && (groups.size() <= maxFilteredGroups)) {
        groups.add(buildGroupFromSearchResult(searchResults.next()));
      }

      if (sortDirection == SortDirection.ASCENDING) {
        groups.sort(Comparator.comparing(Group::getName));
      } else {
        groups.sort((Group group1, Group group2) -> group2.getName().compareTo(group1.getName()));
      }

      long totalGroups = groups.size();

      if ((pageIndex != null) && (pageSize != null)) {
        int toIndex = (pageIndex * pageSize) + pageSize;

        groups = groups.subList(pageIndex * pageSize, Math.min(toIndex, groups.size()));
      }

      return new Groups(
          getUserDirectoryId(), groups, totalGroups, filter, sortDirection, pageIndex, pageSize);
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the filtered groups for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    } finally {
      JNDIUtil.close(searchResults);
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public List<Group> getGroupsForUser(String username)
      throws UserNotFoundException, ServiceUnavailableException {
    DirContext dirContext = null;
    NamingEnumeration<SearchResult> searchResults = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      LdapName userDN = getUserDN(dirContext, username);

      if (userDN == null) {
        throw new UserNotFoundException(username);
      }

      String searchFilter =
          String.format(
              "(&(objectClass=%s)(%s=%s))",
              groupObjectClass, groupMemberAttribute, userDN.toString());

      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      searchControls.setReturningObjFlag(false);

      searchResults = dirContext.search(groupBaseDN, searchFilter, searchControls);

      List<Group> groups = new ArrayList<>();

      while (searchResults.hasMore()) {
        groups.add(buildGroupFromSearchResult(searchResults.next()));
      }

      return groups;
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
    } finally {
      JNDIUtil.close(searchResults);
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public List<GroupMember> getMembersForGroup(String groupName)
      throws GroupNotFoundException, ServiceUnavailableException {
    DirContext dirContext = null;
    NamingEnumeration<SearchResult> searchResults = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      String searchFilter =
          String.format(
              "(&(objectClass=%s)(%s=%s))", groupObjectClass, groupNameAttribute, groupName);

      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      searchControls.setReturningObjFlag(false);

      searchResults = dirContext.search(groupBaseDN, searchFilter, searchControls);

      if (searchResults.hasMore()) {
        SearchResult searchResult = searchResults.next();

        List<GroupMember> groupMembers = new ArrayList<>();

        var attribute = searchResult.getAttributes().get(groupMemberAttribute);

        if (attribute != null) {
          for (int i = 0; ((i < attribute.size()) && (i < maxFilteredGroupMembers)); i++) {
            LdapName groupMemberDn = new LdapName(String.valueOf(attribute.get(i)));

            for (Rdn rdn : groupMemberDn.getRdns()) {
              if (rdn.getType().equalsIgnoreCase(userUsernameAttribute)) {
                groupMembers.add(
                    new GroupMember(
                        getUserDirectoryId(),
                        groupName,
                        GroupMemberType.USER,
                        String.valueOf(rdn.getValue())));

                break;
              }
            }
          }
        }

        return groupMembers;
      } else {
        throw new GroupNotFoundException(groupName);
      }
    } catch (GroupNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the members for the group ("
              + groupName
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    } finally {
      JNDIUtil.close(searchResults);
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public GroupMembers getMembersForGroup(
      String groupName,
      String filter,
      SortDirection sortDirection,
      Integer pageIndex,
      Integer pageSize)
      throws GroupNotFoundException, ServiceUnavailableException {
    DirContext dirContext = null;
    NamingEnumeration<SearchResult> searchResults = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      if (StringUtils.hasText(filter)) {
        filter = filter.toLowerCase();
      }

      String searchFilter =
          String.format(
              "(&(objectClass=%s)(%s=%s))", groupObjectClass, groupNameAttribute, groupName);

      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      searchControls.setReturningObjFlag(false);

      searchResults = dirContext.search(groupBaseDN, searchFilter, searchControls);

      if (searchResults.hasMore()) {
        SearchResult searchResult = searchResults.next();

        List<GroupMember> groupMembers = new ArrayList<>();

        var attribute = searchResult.getAttributes().get(groupMemberAttribute);

        if (attribute != null) {
          for (int i = 0; i < attribute.size(); i++) {
            LdapName groupMemberDn = new LdapName(String.valueOf(attribute.get(i)));

            for (Rdn rdn : groupMemberDn.getRdns()) {
              if (rdn.getType().equalsIgnoreCase(userUsernameAttribute)) {
                if (!StringUtils.hasText(filter)) {
                  groupMembers.add(
                      new GroupMember(
                          getUserDirectoryId(),
                          groupName,
                          GroupMemberType.USER,
                          String.valueOf(rdn.getValue())));
                } else {
                  /*
                   * NOTE: We perform the filtering here as directory servers like OpenLDAP do not
                   *       support substring case-insensitive matches for the member attribute on
                   *       the groupOfNames objectclass.
                   */
                  String username = String.valueOf(rdn.getValue());

                  if (username.contains(filter)) {
                    groupMembers.add(
                        new GroupMember(
                            getUserDirectoryId(),
                            groupName,
                            GroupMemberType.USER,
                            String.valueOf(rdn.getValue())));
                  }
                }

                break;
              }
            }
          }
        }

        if (sortDirection == SortDirection.ASCENDING) {
          groupMembers.sort(Comparator.comparing(GroupMember::getMemberName));
        } else {
          groupMembers.sort(
              (GroupMember groupMember1, GroupMember groupMember2) ->
                  groupMember2.getMemberName().compareTo(groupMember1.getMemberName()));
        }

        long totalGroupMembers = groupMembers.size();

        if ((pageIndex != null) && (pageSize != null)) {
          pageSize = Math.min(pageSize, maxFilteredGroupMembers);

          int toIndex = (pageIndex * pageSize) + pageSize;

          groupMembers =
              groupMembers.subList(pageIndex * pageSize, Math.min(toIndex, groupMembers.size()));
        }

        return new GroupMembers(
            getUserDirectoryId(),
            groupName,
            groupMembers,
            totalGroupMembers,
            filter,
            sortDirection,
            pageIndex,
            pageSize);
      } else {
        throw new GroupNotFoundException(groupName);
      }
    } catch (GroupNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the filtered members for the group ("
              + groupName
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    } finally {
      JNDIUtil.close(searchResults);
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public List<String> getRoleCodesForGroup(String groupName)
      throws GroupNotFoundException, ServiceUnavailableException {
    DirContext dirContext = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      LdapName groupDN = getGroupDN(dirContext, groupName);

      if (groupDN == null) {
        throw new GroupNotFoundException(groupName);
      }

      Optional<UUID> groupIdOptional =
          getGroupRepository()
              .getIdByUserDirectoryIdAndNameIgnoreCase(getUserDirectoryId(), groupName);

      if (groupIdOptional.isPresent()) {
        return getGroupRepository().getRoleCodesByGroupId(groupIdOptional.get());
      } else {
        return new ArrayList<>();
      }
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
    } finally {
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public List<String> getRoleCodesForUser(String username)
      throws UserNotFoundException, ServiceUnavailableException {
    DirContext dirContext = null;
    NamingEnumeration<SearchResult> searchResults = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      LdapName userDN = getUserDN(dirContext, username);

      if (userDN == null) {
        throw new UserNotFoundException(username);
      }

      String searchFilter =
          String.format(
              "(&(objectClass=%s)(%s=%s))",
              groupObjectClass, groupMemberAttribute, userDN.toString());

      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      searchControls.setReturningObjFlag(false);

      searchResults = dirContext.search(groupBaseDN, searchFilter, searchControls);

      List<String> groupNames = new ArrayList<>();

      while (searchResults.hasMore()) {
        SearchResult searchResult = searchResults.next();

        if (searchResult.getAttributes().get(groupNameAttribute) != null) {
          groupNames.add(
              String.valueOf(searchResult.getAttributes().get(groupNameAttribute).get())
                  .toLowerCase());
        }
      }

      if (groupNames.isEmpty()) {
        return new ArrayList<>();
      }

      return getGroupRepository()
          .getRoleCodesByUserDirectoryIdAndGroupNames(getUserDirectoryId(), groupNames);
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
    } finally {
      JNDIUtil.close(searchResults);
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public List<GroupRole> getRolesForGroup(String groupName)
      throws GroupNotFoundException, ServiceUnavailableException {
    DirContext dirContext = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      LdapName groupDN = getGroupDN(dirContext, groupName);

      if (groupDN == null) {
        throw new GroupNotFoundException(groupName);
      }

      Optional<UUID> groupIdOptional =
          getGroupRepository()
              .getIdByUserDirectoryIdAndNameIgnoreCase(getUserDirectoryId(), groupName);

      List<GroupRole> groupRoles = new ArrayList<>();

      if (groupIdOptional.isPresent()) {
        for (String roleCode : getGroupRepository().getRoleCodesByGroupId(groupIdOptional.get())) {
          groupRoles.add(new GroupRole(getUserDirectoryId(), groupName, roleCode));
        }
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
    } finally {
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public User getUser(String username) throws UserNotFoundException, ServiceUnavailableException {
    DirContext dirContext = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      User user = getUser(dirContext, username);

      if (user == null) {
        throw new UserNotFoundException(username);
      }

      return user;
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
    } finally {
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public String getUserName(String username)
      throws UserNotFoundException, ServiceUnavailableException {
    DirContext dirContext = null;

    try {
      // TODO: MAKE THIS IMPLEMENTATION MORE EFFICIENT AND DO NOT RETRIEVE THE WHOLE USER -- MARCUS
      dirContext = getDirContext(bindDN, bindPassword);

      User user = getUser(dirContext, username);

      if (user == null) {
        throw new UserNotFoundException(username);
      }

      StringBuilder buffer = new StringBuilder(user.getName());

      if (StringUtils.hasText(user.getPreferredName())) {
        if (buffer.length() > 0) {
          buffer.append(" ");
        }

        buffer.append(user.getPreferredName());
      }

      return buffer.toString();
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
    } finally {
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public List<User> getUsers() throws ServiceUnavailableException {
    DirContext dirContext = null;
    NamingEnumeration<SearchResult> searchResults = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      String searchFilter = String.format("(objectClass=%s)", userObjectClass);

      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      searchControls.setReturningObjFlag(false);
      searchControls.setCountLimit(maxFilteredUsers);

      List<User> users = new ArrayList<>();

      searchResults = dirContext.search(userBaseDN, searchFilter, searchControls);

      while (searchResults.hasMore() && (users.size() <= maxFilteredUsers)) {
        users.add(buildUserFromSearchResult(searchResults.next()));
      }

      return users;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the users for the user directory (" + getUserDirectoryId() + ")", e);
    } finally {
      JNDIUtil.close(searchResults);
      JNDIUtil.close(dirContext);
    }
  }

  public Users getUsers(
      String filter,
      UserSortBy sortBy,
      SortDirection sortDirection,
      Integer pageIndex,
      Integer pageSize)
      throws ServiceUnavailableException {
    DirContext dirContext = null;
    NamingEnumeration<SearchResult> searchResults = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      String searchFilter;

      if (StringUtils.hasText(filter)) {
        searchFilter =
            String.format(
                "(&(objectClass=%s)(|(%s=*%s*)(%s=*%s*)(%s=*%s*)))",
                userObjectClass,
                userUsernameAttribute,
                filter,
                userNameAttribute,
                filter,
                userPreferredNameAttribute,
                filter);
      } else {
        searchFilter = String.format("(objectClass=%s)", userObjectClass);
      }

      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      searchControls.setReturningObjFlag(false);
      searchControls.setCountLimit(maxFilteredUsers);

      // TODO: Implement sorting of users for LDAP queries -- MARCUS

      List<User> users = new ArrayList<>();

      searchResults = dirContext.search(userBaseDN, searchFilter, searchControls);

      while (searchResults.hasMore() && (users.size() <= maxFilteredUsers)) {
        users.add(buildUserFromSearchResult(searchResults.next()));
      }

      if (sortDirection == SortDirection.ASCENDING) {
        if (sortBy == UserSortBy.USERNAME) {
          users.sort(Comparator.comparing(User::getUsername));
        } else if (sortBy == UserSortBy.NAME) {
          users.sort(Comparator.comparing(User::getName));
        } else if (sortBy == UserSortBy.PREFERRED_NAME) {
          users.sort(Comparator.comparing(User::getPreferredName));
        }
      } else {
        if (sortBy == UserSortBy.USERNAME) {
          users.sort(
              (User user1, User user2) -> user2.getUsername().compareTo(user1.getUsername()));
        } else if (sortBy == UserSortBy.NAME) {
          users.sort((User user1, User user2) -> user2.getName().compareTo(user1.getName()));
        } else if (sortBy == UserSortBy.PREFERRED_NAME) {
          users.sort(
              (User user1, User user2) ->
                  user2.getPreferredName().compareTo(user1.getPreferredName()));
        }
      }

      long totalUsers = users.size();

      if ((pageIndex != null) && (pageSize != null)) {
        int toIndex = (pageIndex * pageSize) + pageSize;

        users = users.subList(pageIndex * pageSize, Math.min(toIndex, users.size()));
      }

      return new Users(
          getUserDirectoryId(),
          users,
          totalUsers,
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
    } finally {
      JNDIUtil.close(searchResults);
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public boolean isExistingUser(String username) throws ServiceUnavailableException {
    DirContext dirContext = null;
    NamingEnumeration<SearchResult> searchResults = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      String searchFilter =
          String.format(
              "(&(objectClass=%s)(%s=%s))", userObjectClass, userUsernameAttribute, username);

      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      searchControls.setReturningObjFlag(false);

      searchResults = dirContext.search(baseDN, searchFilter, searchControls);

      return searchResults.hasMore();
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to check whether the user ("
              + username
              + ") is an existing user for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    } finally {
      JNDIUtil.close(searchResults);
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public boolean isUserInGroup(String groupName, String username)
      throws UserNotFoundException, GroupNotFoundException, ServiceUnavailableException {
    DirContext dirContext = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      LdapName userDN = getUserDN(dirContext, username);

      if (userDN == null) {
        throw new UserNotFoundException(username);
      }

      LdapName groupDN = getGroupDN(dirContext, groupName);

      if (groupDN == null) {
        throw new GroupNotFoundException(groupName);
      }

      Attributes attributes = dirContext.getAttributes(groupDN, groupMemberAttributeArray);

      if (attributes.get(groupMemberAttribute) != null) {
        NamingEnumeration<?> attributeValues = attributes.get(groupMemberAttribute).getAll();

        while (attributeValues.hasMore()) {
          LdapName memberDN = new LdapName((String) attributeValues.next());

          if (memberDN.equals(userDN)) {
            return true;
          }
        }
      }

      return false;
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
    } finally {
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public void removeMemberFromGroup(String groupName, GroupMemberType memberType, String memberName)
      throws GroupNotFoundException, GroupMemberNotFoundException, ServiceUnavailableException {
    if (!capabilities.getSupportsGroupMemberAdministration()) {
      throw new ServiceUnavailableException(
          "The group member administration capability is not supported for the user directory ("
              + getUserDirectoryId()
              + "");
    }

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

  @Override
  public void removeRoleFromGroup(String groupName, String roleCode)
      throws GroupNotFoundException, GroupRoleNotFoundException, ServiceUnavailableException {
    DirContext dirContext = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      LdapName groupDN = getGroupDN(dirContext, groupName);

      if (groupDN == null) {
        throw new GroupNotFoundException(groupName);
      }

      Optional<UUID> groupIdOptional =
          getGroupRepository()
              .getIdByUserDirectoryIdAndNameIgnoreCase(getUserDirectoryId(), groupName);

      if (groupIdOptional.isPresent()) {
        if (getGroupRepository().removeRoleFromGroup(groupIdOptional.get(), roleCode) == 0) {
          throw new GroupRoleNotFoundException(roleCode);
        }
      } else {
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
    } finally {
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public void removeUserFromGroup(String groupName, String username)
      throws UserNotFoundException, GroupNotFoundException, ServiceUnavailableException {
    DirContext dirContext = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      LdapName userDN = getUserDN(dirContext, username);

      if (userDN == null) {
        throw new UserNotFoundException(username);
      }

      LdapName groupDN = getGroupDN(dirContext, groupName);

      if (groupDN == null) {
        throw new GroupNotFoundException(groupName);
      }

      Attributes attributes = dirContext.getAttributes(groupDN, groupMemberAttributeArray);

      BasicAttribute attribute = new BasicAttribute(groupMemberAttribute);

      if (attributes.get(groupMemberAttribute) != null) {
        @SuppressWarnings("unchecked")
        NamingEnumeration<String> groupMembers =
            (NamingEnumeration<String>) attributes.get(groupMemberAttribute).getAll();

        while (groupMembers.hasMore()) {
          LdapName groupMemberDN = new LdapName(groupMembers.next());

          if (!groupMemberDN.equals(userDN)) {
            attribute.add(groupMemberDN.toString());
          }
        }
      }

      if (attribute.size() > 0) {
        dirContext.modifyAttributes(
            groupDN,
            new ModificationItem[] {new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attribute)});
      } else {
        dirContext.modifyAttributes(
            groupDN,
            new ModificationItem[] {new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attribute)});
      }
    } catch (UserNotFoundException | GroupNotFoundException e) {
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
    } finally {
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public void resetPassword(String username, String newPassword)
      throws UserNotFoundException, UserLockedException, ExistingPasswordException,
          ServiceUnavailableException {
    if (!capabilities.getSupportsChangePassword()) {
      throw new ServiceUnavailableException(
          "The change password capability is not supported for the user directory ("
              + getUserDirectoryId()
              + "");
    }

    DirContext dirContext = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      LdapName userDN = getUserDN(dirContext, username);

      if (userDN == null) {
        throw new UserNotFoundException(username);
      }

      List<ModificationItem> modificationItems = new ArrayList<>();

      BasicAttribute passwordAttribute = new BasicAttribute("userPassword");
      passwordAttribute.add(newPassword);

      modificationItems.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, passwordAttribute));

      dirContext.modifyAttributes(userDN, modificationItems.toArray(new ModificationItem[0]));
    } catch (UserNotFoundException e) {
      throw e;
    }

    // TODO: FIX THIS IMPLEMENTATION BY CORRECTLY INTERPRETING USER LOCKED ERROR -- MARCUS
    // catch (UserNotFoundException | UserLockedException
    // | ExpiredPasswordException e)
    // {
    // throw e;
    // }
    catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to reset the password for the user ("
              + username
              + ") for the user directory ("
              + getUserDirectoryId()
              + ")",
          e);
    } finally {
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public void updateGroup(Group group) throws GroupNotFoundException, ServiceUnavailableException {
    if (!capabilities.getSupportsGroupAdministration()) {
      throw new ServiceUnavailableException(
          "The group administration capability is not supported for the user directory ("
              + getUserDirectoryId()
              + "");
    }

    DirContext dirContext = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      LdapName groupDN = getGroupDN(dirContext, group.getName());

      if (groupDN == null) {
        throw new GroupNotFoundException(group.getName());
      }

      List<ModificationItem> modificationItems = new ArrayList<>();

      if (StringUtils.hasText(groupDescriptionAttribute)) {
        modificationItems.add(
            new ModificationItem(
                DirContext.REPLACE_ATTRIBUTE,
                new BasicAttribute(
                    groupDescriptionAttribute,
                    StringUtils.hasText(group.getDescription()) ? group.getDescription() : "")));
      }

      if (modificationItems.size() > 0) {
        dirContext.modifyAttributes(groupDN, modificationItems.toArray(new ModificationItem[0]));
      }

      // Update the corresponding group in the database
      Optional<UUID> groupIdOptional =
          getGroupRepository()
              .getIdByUserDirectoryIdAndNameIgnoreCase(getUserDirectoryId(), group.getName());

      if (groupIdOptional.isEmpty()) {
        group.setId(UuidCreator.getShortPrefixComb());
      } else {
        group.setId(groupIdOptional.get());
      }

      getGroupRepository().saveAndFlush(group);
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
    } finally {
      JNDIUtil.close(dirContext);
    }
  }

  @Override
  public void updateUser(User user, boolean expirePassword, boolean lockUser)
      throws UserNotFoundException, ServiceUnavailableException {
    if (!capabilities.getSupportsUserAdministration()) {
      throw new ServiceUnavailableException(
          "The user administration capability is not supported for the user directory ("
              + getUserDirectoryId()
              + "");
    }

    DirContext dirContext = null;

    try {
      dirContext = getDirContext(bindDN, bindPassword);

      LdapName userDN = getUserDN(dirContext, user.getUsername());

      if (userDN == null) {
        throw new UserNotFoundException(user.getUsername());
      }

      List<ModificationItem> modificationItems = new ArrayList<>();

      if (StringUtils.hasText(userNameAttribute)) {
        if (StringUtils.hasText(user.getName())) {
          modificationItems.add(
              new ModificationItem(
                  DirContext.REPLACE_ATTRIBUTE,
                  new BasicAttribute(userNameAttribute, user.getName())));
        } else {
          modificationItems.add(
              new ModificationItem(
                  DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(userNameAttribute)));
        }
      }

      if (StringUtils.hasText(userPreferredNameAttribute)) {
        if (StringUtils.hasText(user.getPreferredName())) {
          modificationItems.add(
              new ModificationItem(
                  DirContext.REPLACE_ATTRIBUTE,
                  new BasicAttribute(userPreferredNameAttribute, user.getPreferredName())));
        } else {
          modificationItems.add(
              new ModificationItem(
                  DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(userPreferredNameAttribute)));
        }
      }

      if (StringUtils.hasText(userEmailAttribute)) {
        if (StringUtils.hasText(user.getEmail())) {
          modificationItems.add(
              new ModificationItem(
                  DirContext.REPLACE_ATTRIBUTE,
                  new BasicAttribute(userEmailAttribute, user.getEmail())));
        } else {
          modificationItems.add(
              new ModificationItem(
                  DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(userEmailAttribute)));
        }
      }

      if (StringUtils.hasText(userPhoneNumberAttribute)) {
        if (StringUtils.hasText(user.getPhoneNumber())) {
          modificationItems.add(
              new ModificationItem(
                  DirContext.REPLACE_ATTRIBUTE,
                  new BasicAttribute(userPhoneNumberAttribute, user.getPhoneNumber())));
        } else {
          modificationItems.add(
              new ModificationItem(
                  DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(userPhoneNumberAttribute)));
        }
      }

      if (StringUtils.hasText(userMobileNumberAttribute)) {
        if (StringUtils.hasText(user.getMobileNumber())) {
          modificationItems.add(
              new ModificationItem(
                  DirContext.REPLACE_ATTRIBUTE,
                  new BasicAttribute(userMobileNumberAttribute, user.getMobileNumber())));
        } else {
          modificationItems.add(
              new ModificationItem(
                  DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(userMobileNumberAttribute)));
        }
      }

      if (StringUtils.hasText(user.getPassword())) {
        modificationItems.add(
            new ModificationItem(
                DirContext.REPLACE_ATTRIBUTE,
                new BasicAttribute("userPassword", user.getPassword())));
      }

      if (modificationItems.size() > 0) {
        dirContext.modifyAttributes(userDN, modificationItems.toArray(new ModificationItem[0]));
      }
    } catch (UserNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to update the user ("
              + user.getUsername()
              + ") for the user directory ("
              + getUserDirectoryId()
              + "): "
              + e.getMessage(),
          e);
    } finally {
      JNDIUtil.close(dirContext);
    }
  }

  private Group buildGroupFromSearchResult(SearchResult searchResult) throws NamingException {
    Attributes attributes = searchResult.getAttributes();

    Group group = new Group(String.valueOf(attributes.get(groupNameAttribute).get()));

    group.setId(null);
    group.setUserDirectoryId(getUserDirectoryId());

    if ((StringUtils.hasText(groupDescriptionAttribute))
        && (attributes.get(groupDescriptionAttribute) != null)) {
      group.setDescription(String.valueOf(attributes.get(groupDescriptionAttribute).get()));
    } else {
      group.setDescription("");
    }

    return group;
  }

  private User buildUserFromSearchResult(SearchResult searchResult) throws NamingException {
    Attributes attributes = searchResult.getAttributes();

    User user = new User();

    // user.setId(new LdapName(searchResult.getNameInNamespace().toLowerCase()).toString());
    user.setUsername(String.valueOf(attributes.get(userUsernameAttribute).get()));
    user.setUserDirectoryId(getUserDirectoryId());

    // TODO: Correctly process LDAP attributes for user to set status -- MARCUS
    user.setStatus(UserStatus.ACTIVE);
    user.setPassword("");

    if ((StringUtils.hasText(userNameAttribute)) && (attributes.get(userNameAttribute) != null)) {
      user.setName(String.valueOf(attributes.get(userNameAttribute).get()));
    } else {
      user.setName("");
    }

    if ((StringUtils.hasText(userPreferredNameAttribute))
        && (attributes.get(userPreferredNameAttribute) != null)) {
      user.setPreferredName(String.valueOf(attributes.get(userPreferredNameAttribute).get()));
    } else {
      user.setPreferredName("");
    }

    if ((StringUtils.hasText(userPhoneNumberAttribute))
        && (attributes.get(userPhoneNumberAttribute) != null)) {
      user.setPhoneNumber(String.valueOf(attributes.get(userPhoneNumberAttribute).get()));
    } else {
      user.setPhoneNumber("");
    }

    if ((StringUtils.hasText(userMobileNumberAttribute))
        && (attributes.get(userMobileNumberAttribute) != null)) {
      user.setMobileNumber(String.valueOf(attributes.get(userMobileNumberAttribute).get()));
    } else {
      user.setMobileNumber("");
    }

    if ((StringUtils.hasText(userEmailAttribute)) && (attributes.get(userEmailAttribute) != null)) {
      user.setEmail(String.valueOf(attributes.get(userEmailAttribute).get()));
    } else {
      user.setEmail("");
    }

    return user;
  }

  private DirContext getDirContext(String userDN, String password)
      throws ServiceUnavailableException {
    try {
      String url = useSSL ? "ldaps://" : "ldap://";
      url += host;
      url += ":";
      url += port;

      String connectionType = "simple";

      Hashtable<String, String> environment = new Hashtable<>();

      environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
      environment.put(Context.PROVIDER_URL, url);
      environment.put(Context.SECURITY_AUTHENTICATION, connectionType);
      environment.put(Context.SECURITY_PRINCIPAL, userDN);
      environment.put(Context.SECURITY_CREDENTIALS, password);

      return new InitialDirContext(environment);
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          String.format(
              "Failed to retrieve the JNDI directory context for the user directory (%s)",
              getUserDirectoryId()),
          e);
    }
  }

  private LdapName getGroupDN(DirContext dirContext, String groupName)
      throws ServiceUnavailableException {
    NamingEnumeration<SearchResult> searchResults = null;

    try {
      List<LdapName> groupDNs = new ArrayList<>();

      String searchFilter =
          String.format(
              "(&(objectClass=%s)(%s=%s))", groupObjectClass, groupNameAttribute, groupName);

      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      searchControls.setReturningObjFlag(false);
      searchControls.setReturningAttributes(EMPTY_ATTRIBUTE_LIST);

      searchResults = dirContext.search(groupBaseDN, searchFilter, searchControls);

      while (searchResults.hasMore()) {
        groupDNs.add(new LdapName(searchResults.next().getNameInNamespace().toLowerCase()));
      }

      if (groupDNs.size() == 0) {
        return null;
      } else if (groupDNs.size() == 1) {
        return groupDNs.get(0);
      } else {
        StringBuilder buffer = new StringBuilder();

        for (LdapName groupDN : groupDNs) {
          if (buffer.length() > 0) {
            buffer.append(" ");
          }

          buffer.append("(").append(groupDN).append(")");
        }

        throw new ServiceUnavailableException(
            String.format(
                "Found multiple groups (%d) with the name of the group (%s) with DNs %s",
                groupDNs.size(), groupName, buffer.toString()));
      }
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          String.format(
              "Failed to retrieve the DN for the group (%s) from the LDAP directory (%s:%d)",
              groupName, host, port),
          e);
    } finally {
      JNDIUtil.close(searchResults);
    }
  }

  private User getUser(DirContext dirContext, String username) throws ServiceUnavailableException {
    NamingEnumeration<SearchResult> searchResults = null;

    try {
      List<User> users = new ArrayList<>();

      String searchFilter =
          String.format(
              "(&(objectClass=%s)(%s=%s))", userObjectClass, userUsernameAttribute, username);

      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      searchControls.setReturningObjFlag(false);

      // First search for a non-shared user
      searchResults = dirContext.search(baseDN, searchFilter, searchControls);

      while (searchResults.hasMore()) {
        users.add(buildUserFromSearchResult(searchResults.next()));
      }

      if (users.size() == 0) {
        return null;
      } else if (users.size() == 1) {
        return users.get(0);
      } else {
        StringBuilder buffer = new StringBuilder();

        for (User user : users) {
          if (buffer.length() > 0) {
            buffer.append(" ");
          }

          buffer.append("(").append(user.getId()).append(")");
        }

        throw new ServiceUnavailableException(
            String.format(
                "Found multiple users (%d) with the username (%s) with DNs %s",
                users.size(), username, buffer.toString()));
      }
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          String.format(
              "Failed to retrieve the details for the user (%s) from the LDAP directory (%s:%d)",
              username, host, port),
          e);
    } finally {
      JNDIUtil.close(searchResults);
    }
  }

  private LdapName getUserDN(DirContext dirContext, String username)
      throws ServiceUnavailableException {
    NamingEnumeration<SearchResult> searchResults = null;

    try {
      List<LdapName> userDNs = new ArrayList<>();

      String searchFilter =
          String.format(
              "(&(objectClass=%s)(%s=%s))", userObjectClass, userUsernameAttribute, username);

      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      searchControls.setReturningObjFlag(false);
      searchControls.setReturningAttributes(EMPTY_ATTRIBUTE_LIST);

      searchResults = dirContext.search(baseDN, searchFilter, searchControls);

      while (searchResults.hasMore()) {
        userDNs.add(new LdapName(searchResults.next().getNameInNamespace().toLowerCase()));
      }

      if (userDNs.size() == 0) {
        return null;
      } else if (userDNs.size() == 1) {
        return userDNs.get(0);
      } else {
        StringBuilder buffer = new StringBuilder();

        for (LdapName userDN : userDNs) {
          if (buffer.length() > 0) {
            buffer.append(" ");
          }

          buffer.append("(").append(userDN).append(")");
        }

        throw new ServiceUnavailableException(
            String.format(
                "Found multiple users (%d) with the username (%s) with DNs %s",
                userDNs.size(), username, buffer.toString()));
      }
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          String.format(
              "Failed to retrieve the DN for the user (%s) from the LDAP directory (%s:%d)",
              username, host, port),
          e);
    } finally {
      JNDIUtil.close(searchResults);
    }
  }
}
