/// *
// * Copyright 2021 original authors
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *   http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
// package africa.absa.inception.security.test;
//
//
//
// import static org.junit.Assert.assertEquals;
// import static org.junit.Assert.assertFalse;
// import static org.junit.Assert.assertTrue;
// import static org.junit.Assert.fail;
//
// import africa.absa.inception.security.Attribute;
// import africa.absa.inception.security.AuthenticationFailedException;
// import africa.absa.inception.security.Group;
// import africa.absa.inception.security.GroupMember;
// import africa.absa.inception.security.GroupMemberType;
// import africa.absa.inception.security.GroupMembers;
// import africa.absa.inception.security.GroupRole;
// import africa.absa.inception.security.Groups;
// import africa.absa.inception.security.ISecurityService;
// import africa.absa.inception.security.PasswordChangeReason;
// import africa.absa.inception.security.SecurityService;
// import africa.absa.inception.security.SortDirection;
// import africa.absa.inception.security.User;
// import africa.absa.inception.security.UserSortBy;
// import africa.absa.inception.security.UserStatus;
// import africa.absa.inception.security.Users;
// import africa.absa.inception.test.TestClassRunner;
// import africa.absa.inception.test.TestConfiguration;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.UUID;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.test.context.ContextConfiguration;
// import org.springframework.test.context.TestExecutionListeners;
// import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
// import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
// import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
//
/// **
// * The <b>SecurityServiceTest</b> class contains the implementation of the JUnit tests for
// the
// * <b>SecurityService</b> class.
// *
// * @author Marcus Portmann
// */
// @RunWith(TestClassRunner.class)
//
// import africa.absa.inception.test.TestConfiguration;
// import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
// import org.springframework.test.context.ContextConfiguration;
//
// @ContextConfiguration(
//    classes = {TestConfiguration.class},
//    initializers = {ConfigDataApplicationContextInitializer.class})
// @TestExecutionListeners(
//    listeners = {
//      DependencyInjectionTestExecutionListener.class,
//      DirtiesContextTestExecutionListener.class,
//      TransactionalTestExecutionListener.class
//    })
// public class LDAPUserDirectoryTest {
//  /**
//   * The ID for the Sample LDAP user directory.
//   */
//  private static final UUID SAMPLE_LDAP_USER_DIRECTORY_ID =
//      UUID.fromString("11111111-1111-1111-1111-111111111111");
//
//  private static int groupCount;
//  private static int userCount;
//
//  /** The Security Service. */
//  @Autowired private ISecurityService securityService;
//
//  private static synchronized Group getTestGroupDetails() {
//    groupCount++;
//
//    Group group = new Group();
//
//    group.setUserDirectoryId(LDAPUserDirectoryTest.SAMPLE_LDAP_USER_DIRECTORY_ID);
//    group.setName("Test Group " + groupCount);
//    group.setDescription("Test Group Description " + groupCount);
//
//    return group;
//  }
//
//  private static synchronized User getTestUserDetails() {
//    userCount++;
//
//    User user = new User();
//
//    user.setUserDirectoryId(LDAPUserDirectoryTest.SAMPLE_LDAP_USER_DIRECTORY_ID);
//    user.setUsername("Test User Username " + userCount);
//    user.setStatus(UserStatus.ACTIVE);
//    user.setEmail("Test User E-Mail " + userCount);
//    user.setName("Test User Name " + userCount);
//    user.setPreferredName("Test User Preferred Name " + userCount);
//    user.setPhoneNumber("Test User Phone Number " + userCount);
//    user.setMobileNumber("Test User Mobile Number " + userCount);
//    user.setPassword("Test User Password " + userCount);
//
//    return user;
//  }
//
//  private static synchronized User getTestUserDetails(String username) {
//    userCount++;
//
//    User user = new User();
//
//    user.setUserDirectoryId(LDAPUserDirectoryTest.SAMPLE_LDAP_USER_DIRECTORY_ID);
//    user.setUsername(username);
//    user.setStatus(UserStatus.ACTIVE);
//    user.setEmail(username + "@test.com");
//    user.setName("Name");
//    user.setPreferredName("PreferredName");
//    user.setPhoneNumber("+27 (11) 555-1234");
//    user.setMobileNumber("+27 (83) 555-1234");
//    user.setPassword("Password1");
//
//    return user;
//  }
//
//  /** Test the administrative change user password functionality. */
//  @Test
//  public void adminChangePasswordTest() throws Exception {
//    User user = getTestUserDetails("adminchangepasswordtest");
//
//    securityService.createUser(user, false, false);
//
//    try {
//      securityService.adminChangePassword(
//          SAMPLE_LDAP_USER_DIRECTORY_ID,
//          user.getUsername(),
//          "Password2",
//          false,
//          false,
//          true,
//          PasswordChangeReason.ADMINISTRATIVE);
//      securityService.authenticate(user.getUsername(), "Password2");
//    } finally {
//      securityService.deleteUser(SAMPLE_LDAP_USER_DIRECTORY_ID, user.getUsername());
//    }
//  }
//
//  /** Test the authenticate functionality. */
//  @Test
//  public void authenticateTest() throws Exception {
//    securityService.authenticate("sample", "Password1");
//  }
//
//  /** Test the delete invalid user functionality. */
//  @Test(expected = africa.absa.inception.security.UserNotFoundException.class)
//  public void deleteInvalidUserTest() throws Exception {
//    securityService.deleteUser(SAMPLE_LDAP_USER_DIRECTORY_ID, "INVALID");
//  }
//
//  /** Test all the functionality of the LDAPUserDirectory user directory type. */
//  @Test
//  public void ldapUserDirectoryTest() throws Exception {
//    Group group = getTestGroupDetails();
//
//    User user = getTestUserDetails();
//
//    try {
//      securityService.createGroup(group);
//
//      Group retrievedGroup =
//          securityService.getGroup(SAMPLE_LDAP_USER_DIRECTORY_ID, group.getName());
//
//      compareGroups(group, retrievedGroup);
//
//      List<Group> retrievedGroups = securityService.getGroups(SAMPLE_LDAP_USER_DIRECTORY_ID);
//
//      assertEquals("The correct number of groups was not retrieved", 28, retrievedGroups.size());
//
//      for (Group groupToCheck : retrievedGroups) {
//        if (groupToCheck.getName().equalsIgnoreCase(group.getName())) {
//          compareGroups(group, groupToCheck);
//        }
//      }
//
//      List<String> retrivedGroupNames =
//          securityService.getGroupNames(SAMPLE_LDAP_USER_DIRECTORY_ID);
//
//      assertEquals(
//          "The correct number of group names was not retrieved", 28, retrivedGroupNames.size());
//
//      Groups filteredGroups =
//          securityService.getGroups(
//              SAMPLE_LDAP_USER_DIRECTORY_ID, "Test", SortDirection.ASCENDING, 0, 100);
//
//      assertEquals(
//          "The correct number of filtered groups was not retrieved",
//          1,
//          filteredGroups.getGroups().size());
//
//      compareGroups(group, filteredGroups.getGroups().get(0));
//
//      group.setDescription(group.getDescription() + " Updated");
//
//      securityService.updateGroup(group);
//
//      retrievedGroup = securityService.getGroup(SAMPLE_LDAP_USER_DIRECTORY_ID, group.getName());
//
//      compareGroups(group, retrievedGroup);
//
//      securityService.addRoleToGroup(
//          SAMPLE_LDAP_USER_DIRECTORY_ID,
//          group.getName(),
//          SecurityService.TENANT_ADMINISTRATOR_ROLE_CODE);
//
//      List<GroupRole> retrievedGroupRoles =
//          securityService.getRolesForGroup(SAMPLE_LDAP_USER_DIRECTORY_ID, group.getName());
//
//      assertEquals(
//          "The correct number of group roles was not retrieved", 1, retrievedGroupRoles.size());
//
//      assertEquals(
//          "The expected role code was not retrieved",
//          SecurityService.TENANT_ADMINISTRATOR_ROLE_CODE,
//          retrievedGroupRoles.get(0).getRoleCode());
//
//      List<String> retrievedRoleCodes =
//          securityService.getRoleCodesForGroup(SAMPLE_LDAP_USER_DIRECTORY_ID, group.getName());
//
//      assertEquals(
//          "The correct number of role codes was not retrieved", 1, retrievedRoleCodes.size());
//
//      assertEquals(
//          "The expected role code was not retrieved",
//          SecurityService.TENANT_ADMINISTRATOR_ROLE_CODE,
//          retrievedRoleCodes.get(0));
//
//      try {
//        securityService.createUser(user, false, false);
//
//        securityService.authenticate(user.getUsername(), user.getPassword());
//
//        User retrievedUser =
//            securityService.getUser(SAMPLE_LDAP_USER_DIRECTORY_ID, user.getUsername());
//
//        compareUsers(user, retrievedUser);
//
//        String retrievedUserName =
//            securityService.getUserName(SAMPLE_LDAP_USER_DIRECTORY_ID, user.getUsername());
//
//        assertEquals(
//            "The correct name was not retrieved for the user (" + user.getUsername() + ")",
//            user.getName(),
//            retrievedUserName);
//
//        assertTrue(
//            "Failed to confirm that the user exists",
//            securityService.isExistingUser(SAMPLE_LDAP_USER_DIRECTORY_ID, user.getUsername()));
//
//        List<User> retrievedUsers = securityService.getUsers(SAMPLE_LDAP_USER_DIRECTORY_ID);
//
//        assertEquals("The correct number of users was not retrieved", 2, retrievedUsers.size());
//
//        for (User userToCheck : retrievedUsers) {
//          if (userToCheck.getUsername().equalsIgnoreCase(user.getUsername())) {
//            compareUsers(user, userToCheck);
//          }
//        }
//
//        Users filteredUsers =
//            securityService.getUsers(
//                SAMPLE_LDAP_USER_DIRECTORY_ID,
//                "Test",
//                UserSortBy.USERNAME,
//                SortDirection.ASCENDING,
//                0,
//                100);
//
//        assertEquals(
//            "The correct number of filtered users was not retrieved",
//            1,
//            filteredUsers.getUsers().size());
//
//        compareUsers(user, filteredUsers.getUsers().get(0));
//
//        List<Attribute> findAttributes = new ArrayList<>();
//
//        findAttributes.add(new Attribute("cn", user.getName()));
//        findAttributes.add(new Attribute("givenname", user.getPreferredName()));
//        findAttributes.add(new Attribute("mobile", user.getMobileNumber()));
//        findAttributes.add(new Attribute("telephoneNumber", user.getPhoneNumber()));
//        findAttributes.add(new Attribute("mail", user.getEmail()));
//
//        List<User> foundUsers =
//            securityService.findUsers(SAMPLE_LDAP_USER_DIRECTORY_ID, findAttributes);
//
//        assertEquals("The correct number of users was not found", 1, foundUsers.size());
//
//        compareUsers(user, foundUsers.get(0));
//
//        user.setPassword("Test Updated Password");
//        user.setEmail("Test Updated E-Mail");
//        user.setName("Test Updated Name");
//        user.setPreferredName("Test Updated Preferred Name");
//        user.setPhoneNumber("Test Updated Phone Number");
//        user.setMobileNumber("Test Updated Mobile Number");
//
//        securityService.updateUser(user, false, false);
//
//        User retrievedUpdatedUser =
//            securityService.getUser(SAMPLE_LDAP_USER_DIRECTORY_ID, user.getUsername());
//
//        compareUsers(user, retrievedUpdatedUser);
//
//        securityService.authenticate(user.getUsername(), user.getPassword());
//
//        securityService.adminChangePassword(
//            SAMPLE_LDAP_USER_DIRECTORY_ID,
//            user.getUsername(),
//            "New Password",
//            false,
//            false,
//            false,
//            PasswordChangeReason.ADMINISTRATIVE);
//
//        securityService.authenticate(user.getUsername(), "New Password");
//
//        try {
//          securityService.authenticate(user.getUsername(), "Wrong Password");
//
//          fail("The user was successfully authenticated with the wrong password");
//        } catch (AuthenticationFailedException ignored) {
//        }
//
//        securityService.addMemberToGroup(
//            SAMPLE_LDAP_USER_DIRECTORY_ID,
//            group.getName(),
//            GroupMemberType.USER,
//            user.getUsername());
//
//        assertTrue(
//            "Failed to confirm that the user ("
//                + user.getUsername()
//                + ") was added to the group ("
//                + group.getName()
//                + ")",
//            securityService.isUserInGroup(
//                SAMPLE_LDAP_USER_DIRECTORY_ID, group.getName(), user.getUsername()));
//
//        retrivedGroupNames =
//            securityService.getGroupNamesForUser(SAMPLE_LDAP_USER_DIRECTORY_ID,
// user.getUsername());
//
//        assertEquals(
//            "The correct number of group names was not retrieved for the user ("
//                + user.getUsername()
//                + ")",
//            1,
//            retrivedGroupNames.size());
//        assertEquals(
//            "The user ("
//                + user.getUsername()
//                + ") was not added to the group ("
//                + group.getName()
//                + ")",
//            group.getName(),
//            retrivedGroupNames.get(0));
//
//        List<Group> groups =
//            securityService.getGroupsForUser(SAMPLE_LDAP_USER_DIRECTORY_ID, user.getUsername());
//
//        assertEquals(
//            "The correct number of groups was not retrieved for the user ("
//                + user.getUsername()
//                + ")",
//            1,
//            groups.size());
//        assertEquals(
//            "The user ("
//                + user.getUsername()
//                + ") was not added to the group ("
//                + group.getName()
//                + ")",
//            group.getName(),
//            groups.get(0).getName());
//
//        List<GroupMember> retrievedGroupMembers =
//            securityService.getMembersForGroup(SAMPLE_LDAP_USER_DIRECTORY_ID, group.getName());
//
//        assertEquals(
//            "The correct number of members was not retrieved for the group ("
//                + group.getName()
//                + ")",
//            1,
//            retrievedGroupMembers.size());
//        assertEquals(
//            "The correct member was not retrieved for the group (" + group.getName() + ")",
//            user.getUsername(),
//            retrievedGroupMembers.get(0).getMemberName());
//
//        GroupMembers filteredGroupMembers =
//            securityService.getMembersForGroup(
//                SAMPLE_LDAP_USER_DIRECTORY_ID,
//                group.getName(),
//                user.getUsername(),
//                SortDirection.ASCENDING,
//                0,
//                100);
//
//        assertEquals(
//            "The correct number of members was not retrieved for the group ("
//                + group.getName()
//                + ")",
//            1,
//            filteredGroupMembers.getGroupMembers().size());
//        assertEquals(
//            "The correct member was not retrieved for the group (" + group.getName() + ")",
//            user.getUsername(),
//            filteredGroupMembers.getGroupMembers().get(0).getMemberName());
//
//        List<String> retrievedFunctionCodes =
//            securityService.getFunctionCodesForUser(
//                SAMPLE_LDAP_USER_DIRECTORY_ID, user.getUsername());
//
//        assertEquals(
//            "The correct number of function codes was not retrieved",
//            6,
//            retrievedFunctionCodes.size());
//
//        retrievedRoleCodes =
//            securityService.getRoleCodesForUser(SAMPLE_LDAP_USER_DIRECTORY_ID,
// user.getUsername());
//
//        assertEquals(
//            "The correct number of role codes was not retrieved", 1, retrievedRoleCodes.size());
//
//        assertEquals(
//            "The expected role code was not retrieved",
//            SecurityService.TENANT_ADMINISTRATOR_ROLE_CODE,
//            retrievedRoleCodes.get(0));
//
//        securityService.removeMemberFromGroup(
//            SAMPLE_LDAP_USER_DIRECTORY_ID,
//            group.getName(),
//            GroupMemberType.USER,
//            user.getUsername());
//
//        assertFalse(
//            "Failed to confirm that the user ("
//                + user.getUsername()
//                + ") was removed from the group ("
//                + group.getName()
//                + ")",
//            securityService.isUserInGroup(
//                SAMPLE_LDAP_USER_DIRECTORY_ID, group.getName(), user.getUsername()));
//
//        securityService.changePassword(user.getUsername(), "New Password", "Newer Password");
//
//        securityService.authenticate(user.getUsername(), "Newer Password");
//
//      } finally {
//        try {
//          securityService.deleteUser(SAMPLE_LDAP_USER_DIRECTORY_ID, user.getUsername());
//        } catch (Throwable ignored) {
//        }
//      }
//
//      securityService.removeRoleFromGroup(
//          SAMPLE_LDAP_USER_DIRECTORY_ID,
//          group.getName(),
//          SecurityService.TENANT_ADMINISTRATOR_ROLE_CODE);
//
//      retrievedGroupRoles =
//          securityService.getRolesForGroup(SAMPLE_LDAP_USER_DIRECTORY_ID, group.getName());
//
//      assertEquals(
//          "The correct number of group roles was not retrieved", 0, retrievedGroupRoles.size());
//
//    } finally {
//      try {
//        securityService.removeMemberFromGroup(
//            SAMPLE_LDAP_USER_DIRECTORY_ID,
//            group.getName(),
//            GroupMemberType.USER,
//            user.getUsername());
//      } catch (Throwable ignored) {
//      }
//
//      try {
//        securityService.deleteGroup(SAMPLE_LDAP_USER_DIRECTORY_ID, group.getName());
//      } catch (Throwable ignored) {
//      }
//    }
//  }
//
//  private void compareGroups(Group group1, Group group2) {
//    assertEquals(
//        "The description values for the groups do not match",
//        group1.getDescription(),
//        group2.getDescription());
//    assertEquals(
//        "The group name values for the groups do not match",
//        group1.getName(),
//        group2.getName());
//    assertEquals(
//        "The user directory ID values for the groups do not match",
//        group1.getUserDirectoryId(),
//        group2.getUserDirectoryId());
//  }
//
//  private void compareUsers(User user1, User user2) {
//    assertEquals(
//        "The status values for the users do not match", user1.getStatus(), user2.getStatus());
//    assertEquals(
//        "The e-mail values for the users do not match", user1.getEmail(), user2.getEmail());
//    assertEquals(
//        "The name values for the users do not match",
//        user1.getName(),
//        user2.getName());
//    assertEquals(
//        "The phone number values for the users do not match",
//        user1.getPhoneNumber(),
//        user2.getPhoneNumber());
//    assertEquals(
//        "The mobile number values for the users do not match",
//        user1.getMobileNumber(),
//        user2.getMobileNumber());
//    assertEquals(
//        "The password attempt values for the users do not match",
//        user1.getPasswordAttempts(),
//        user2.getPasswordAttempts());
//    assertEquals(
//        "The username values for the users do not match",
//        user1.getUsername(),
//        user2.getUsername());
//  }
// }
