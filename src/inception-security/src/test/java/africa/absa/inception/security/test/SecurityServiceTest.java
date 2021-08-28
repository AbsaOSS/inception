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

package africa.absa.inception.security.test;

import static africa.absa.inception.test.Assert.assertEqualsToMillisecond;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import africa.absa.inception.core.sorting.SortDirection;
import africa.absa.inception.security.AuthenticationFailedException;
import africa.absa.inception.security.DuplicateFunctionException;
import africa.absa.inception.security.DuplicateGroupException;
import africa.absa.inception.security.DuplicateTenantException;
import africa.absa.inception.security.DuplicateUserException;
import africa.absa.inception.security.ExistingGroupMembersException;
import africa.absa.inception.security.ExistingPasswordException;
import africa.absa.inception.security.ExpiredPasswordException;
import africa.absa.inception.security.Function;
import africa.absa.inception.security.FunctionNotFoundException;
import africa.absa.inception.security.Group;
import africa.absa.inception.security.GroupMember;
import africa.absa.inception.security.GroupMemberType;
import africa.absa.inception.security.GroupMembers;
import africa.absa.inception.security.GroupNotFoundException;
import africa.absa.inception.security.GroupRole;
import africa.absa.inception.security.Groups;
import africa.absa.inception.security.ISecurityService;
import africa.absa.inception.security.InvalidAttributeException;
import africa.absa.inception.security.PasswordChangeReason;
import africa.absa.inception.security.Role;
import africa.absa.inception.security.SecurityService;
import africa.absa.inception.security.Tenant;
import africa.absa.inception.security.TenantNotFoundException;
import africa.absa.inception.security.TenantStatus;
import africa.absa.inception.security.Tenants;
import africa.absa.inception.security.User;
import africa.absa.inception.security.UserAttribute;
import africa.absa.inception.security.UserDirectories;
import africa.absa.inception.security.UserDirectory;
import africa.absa.inception.security.UserDirectoryNotFoundException;
import africa.absa.inception.security.UserDirectorySummaries;
import africa.absa.inception.security.UserDirectorySummary;
import africa.absa.inception.security.UserDirectoryType;
import africa.absa.inception.security.UserLockedException;
import africa.absa.inception.security.UserNotFoundException;
import africa.absa.inception.security.UserSortBy;
import africa.absa.inception.security.UserStatus;
import africa.absa.inception.security.Users;
import africa.absa.inception.test.InceptionExtension;
import africa.absa.inception.test.TestConfiguration;
import com.github.f4b6a3.uuid.UuidCreator;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

/**
 * The <b>SecurityServiceTest</b> class contains the implementation of the JUnit tests for the
 * <b>SecurityService</b> class.
 *
 * @author Marcus Portmann
 */
@ExtendWith(SpringExtension.class)
@ExtendWith(InceptionExtension.class)
@ContextConfiguration(
    classes = {TestConfiguration.class},
    initializers = {ConfigDataApplicationContextInitializer.class})
@TestExecutionListeners(
    listeners = {
      DependencyInjectionTestExecutionListener.class,
      DirtiesContextTestExecutionListener.class,
      TransactionalTestExecutionListener.class
    })
public class SecurityServiceTest {

  private static int functionCount;

  private static int groupCount;

  private static int tenantCount;

  private static int userCount;

  private static int userDirectoryCount;

  /** The Security Service. */
  @Autowired ISecurityService securityService;

  @Autowired
  @Qualifier("applicationDataSource")
  private DataSource dataSource;

  private static synchronized User getNumberedTestUserDetails(UUID userDirectoryId, int number) {
    User user = new User();

    user.setUserDirectoryId(userDirectoryId);
    user.setUsername("Numbered Test Username " + number);
    user.setStatus(UserStatus.ACTIVE);
    user.setEmail("testing" + String.format("%03d", number) + "@inception.digital");
    user.setName("Numbered Test Name " + number);
    user.setPreferredName("Numbered Test Preferred Name " + number);
    user.setPhoneNumber("Numbered Test Phone Number " + number);
    user.setMobileNumber("+2782666" + String.format("%03d", number));
    user.setPassword("Numbered Test Password " + number);

    return user;
  }

  private static synchronized Function getTestFunctionDetails() {
    functionCount++;

    Function function = new Function();

    function.setCode("TestFunctionCode" + functionCount);
    function.setName("Test Function Name " + functionCount);
    function.setDescription("Test Function Description " + functionCount);

    return function;
  }

  private static synchronized Group getTestGroupDetails(UUID userDirectoryId) {
    groupCount++;

    Group group = new Group();

    group.setUserDirectoryId(userDirectoryId);
    group.setName("Test Group " + groupCount);
    group.setDescription("Test Group Description " + groupCount);

    return group;
  }

  private static synchronized Tenant getTestTenantDetails() {
    tenantCount++;

    Tenant tenant = new Tenant();

    tenant.setId(UuidCreator.getShortPrefixComb());
    tenant.setName("Test Tenant Name " + tenantCount);
    tenant.setStatus(TenantStatus.ACTIVE);

    return tenant;
  }

  private static synchronized User getTestUserDetails(UUID userDirectoryId) {
    userCount++;

    User user = new User();

    user.setUserDirectoryId(userDirectoryId);
    user.setUsername("Test User Username " + userCount);
    user.setStatus(UserStatus.ACTIVE);
    user.setEmail("test" + String.format("%03d", userCount) + "@inception.digital");
    user.setName("Test User Name " + userCount);
    user.setPreferredName("Test User Preferred Name " + userCount);
    user.setPhoneNumber("Test User Phone Number " + userCount);
    user.setMobileNumber("+2782555" + String.format("%03d", userCount));
    user.setPassword("Test User Password " + userCount);

    return user;
  }

  private static synchronized UserDirectory getTestUserDirectoryDetails() throws Exception {
    userDirectoryCount++;

    UserDirectory userDirectory = new UserDirectory();

    userDirectory.setId(UuidCreator.getShortPrefixComb());
    userDirectory.setType(SecurityService.INTERNAL_USER_DIRECTORY_TYPE);
    userDirectory.setName("Test User Directory Name " + userDirectoryCount);

    String buffer =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE userDirectory "
            + "SYSTEM \"UserDirectoryConfiguration.dtd\"><userDirectory>"
            + "<parameter><name>MaxPasswordAttempts</name><value>5</value></parameter>"
            + "<parameter><name>PasswordExpiryMonths</name><value>12</value></parameter>"
            + "<parameter><name>PasswordHistoryMonths</name><value>24</value></parameter>"
            + "<parameter><name>MaxFilteredUsers</name><value>100</value></parameter>"
            + "</userDirectory>";

    userDirectory.setConfiguration(buffer);

    return userDirectory;
  }

  /** Test the functionality to add a user to a group. */
  @Test
  public void addUserToGroupTest() throws Exception {
    Tenant tenant = getTestTenantDetails();

    Optional<UserDirectory> userDirectoryOptional = securityService.createTenant(tenant, true);

    if (userDirectoryOptional.isEmpty()) {
      fail("Failed to retrieve the new user directory for the new tenant");
    }

    UserDirectory userDirectory = userDirectoryOptional.get();

    Group group = getTestGroupDetails(userDirectory.getId());

    securityService.createGroup(group);

    User user = getTestUserDetails(userDirectory.getId());

    securityService.createUser(user, false, false);
    securityService.addUserToGroup(userDirectory.getId(), group.getName(), user.getUsername());

    List<String> groupNames =
        securityService.getGroupNamesForUser(userDirectory.getId(), user.getUsername());

    assertEquals(
        1,
        groupNames.size(),
        "The correct number of group names was not retrieved for the user ("
            + user.getUsername()
            + ")");
    assertEquals(
        group.getName(),
        groupNames.get(0),
        "The user ("
            + user.getUsername()
            + ") was not added to the group ("
            + group.getName()
            + ")");

    List<Group> groups =
        securityService.getGroupsForUser(userDirectory.getId(), user.getUsername());

    assertEquals(
        1,
        groups.size(),
        "The correct number of groups was not retrieved for the user (" + user.getUsername() + ")");
    assertEquals(
        group.getName(),
        groups.get(0).getName(),
        "The user ("
            + user.getUsername()
            + ") was not added to the group ("
            + group.getName()
            + ")");
  }

  /** Test the administrative change user password functionality. */
  @Test
  public void adminChangePasswordTest() throws Exception {
    Tenant tenant = getTestTenantDetails();

    Optional<UserDirectory> userDirectoryOptional = securityService.createTenant(tenant, true);

    if (userDirectoryOptional.isEmpty()) {
      fail("Failed to retrieve the new user directory for the new tenant");
    }

    UserDirectory userDirectory = userDirectoryOptional.get();

    User user = getTestUserDetails(userDirectory.getId());

    securityService.createUser(user, false, false);
    securityService.adminChangePassword(
        userDirectory.getId(),
        user.getUsername(),
        "Password2",
        false,
        false,
        true,
        PasswordChangeReason.ADMINISTRATIVE);
    securityService.authenticate(user.getUsername(), "Password2");
  }

  /** Test the change user password functionality. */
  @Test
  public void changePasswordTest() throws Exception {
    Tenant tenant = getTestTenantDetails();

    Optional<UserDirectory> userDirectoryOptional = securityService.createTenant(tenant, true);

    if (userDirectoryOptional.isEmpty()) {
      fail("Failed to retrieve the new user directory for the new tenant");
    }

    UserDirectory userDirectory = userDirectoryOptional.get();

    User user = getTestUserDetails(userDirectory.getId());

    String originalPassword = user.getPassword();

    securityService.createUser(user, false, false);

    UUID userDirectoryId =
        securityService.changePassword(user.getUsername(), originalPassword, "New Password");

    assertEquals(
        userDirectory.getId(), userDirectoryId, "The correct user directory ID was not returned");

    userDirectoryId = securityService.authenticate(user.getUsername(), "New Password");

    assertEquals(
        userDirectory.getId(), userDirectoryId, "The correct user directory ID was not returned");
  }

  //  /** Test the name-value attribute functionality. */
  //  @Test
  //  public void attributeTest() throws AttributeException {
  //    byte[] byteArrayValue = "Hello World".getBytes();
  //
  //    BigDecimal bigDecimalValue = new BigDecimal(12345.12345);
  //
  //    double doubleValue = 12345.12345;
  //
  //    long longValue = 12345L;
  //
  //    String stringValue = "Hello World";
  //
  //    List<Attribute> attributes = new ArrayList<>();
  //
  //    Attribute bigDecimalAttribute = new Attribute("BigDecimal", bigDecimalValue);
  //    attributes.add(bigDecimalAttribute);
  //
  //    Attribute binaryBufferAttribute =
  //        new Attribute("BinaryBuffer", new BinaryBuffer(byteArrayValue));
  //    attributes.add(binaryBufferAttribute);
  //
  //    Attribute byteArrayAttribute = new Attribute("ByteArray", byteArrayValue);
  //    attributes.add(byteArrayAttribute);
  //
  //    Attribute doubleAttribute = new Attribute("Double", doubleValue);
  //    attributes.add(doubleAttribute);
  //
  //    Attribute longAttribute = new Attribute("Long", longValue);
  //    attributes.add(longAttribute);
  //
  //    Attribute stringAttribute = new Attribute("String", stringValue);
  //    attributes.add(stringAttribute);
  //
  //    assertArrayEquals(byteArrayValue, Attribute.getBinaryValue(attributes, "ByteArray"));
  //
  //    assertArrayEquals(byteArrayValue, Attribute.getBinaryValue(attributes, "BinaryBuffer"));
  //
  //    assertEquals(bigDecimalValue, Attribute.getDecimalValue(attributes, "BigDecimal"));
  //
  //    assertEquals(doubleValue, Attribute.getDoubleValue(attributes, "Double"), 0);
  //
  //    assertEquals(longValue, Attribute.getLongValue(attributes, "Long"));
  //
  //    assertEquals(stringValue, Attribute.getStringValue(attributes, "String"));
  //
  //    Attribute.setBinaryValue(attributes, "BinaryBuffer", new BinaryBuffer(byteArrayValue));
  //
  //    Attribute.setBinaryValue(attributes, "ByteArray", byteArrayValue);
  //
  //    Attribute.setDecimalValue(attributes, "BigDecimal", bigDecimalValue);
  //
  //    Attribute.setDoubleValue(attributes, "Double", doubleValue);
  //
  //    Attribute.setLongValue(attributes, "Long", longValue);
  //
  //    Attribute.setStringValue(attributes, "String", stringValue);
  //
  //    assertArrayEquals(byteArrayValue, byteArrayAttribute.getBinaryValue());
  //
  //    assertArrayEquals(byteArrayValue, binaryBufferAttribute.getBinaryValue());
  //
  //    assertEquals(bigDecimalValue, bigDecimalAttribute.getDecimalValue());
  //
  //    assertEquals(doubleValue, doubleAttribute.getDoubleValue(), 0);
  //
  //    assertEquals(longValue, longAttribute.getLongValue());
  //
  //    assertEquals(stringValue, stringAttribute.getStringValue());
  //
  //    binaryBufferAttribute.setBinaryValue(new BinaryBuffer(byteArrayValue));
  //
  //    byteArrayAttribute.setBinaryValue(byteArrayValue);
  //
  //    bigDecimalAttribute.setDecimalValue(bigDecimalValue);
  //
  //    doubleAttribute.setDoubleValue(doubleValue);
  //
  //    longAttribute.setLongValue(longValue);
  //
  //    stringAttribute.setStringValue(stringValue);
  //
  //    assertArrayEquals(byteArrayValue, byteArrayAttribute.getBinaryValue());
  //
  //    assertArrayEquals(byteArrayValue, binaryBufferAttribute.getBinaryValue());
  //
  //    assertEquals(bigDecimalValue, bigDecimalAttribute.getDecimalValue());
  //
  //    assertEquals(doubleValue, doubleAttribute.getDoubleValue(), 0);
  //
  //    assertEquals(longValue, longAttribute.getLongValue());
  //
  //    assertEquals(stringValue, stringAttribute.getStringValue());
  //
  //    assertEquals("BinaryBuffer", binaryBufferAttribute.getName());
  //
  //    assertEquals("ByteArray", byteArrayAttribute.getName());
  //
  //    assertEquals("BigDecimal", bigDecimalAttribute.getName());
  //
  //    assertEquals("Double", doubleAttribute.getName());
  //
  //    assertEquals("Long", longAttribute.getName());
  //
  //    assertEquals("String", stringAttribute.getName());
  //  }

  /** Test the functionality to delete a group with existing members. */
  @Test
  public void deleteGroupWithExistingMembers() throws Exception {
    assertThrows(
        ExistingGroupMembersException.class,
        () -> {
          Tenant tenant = getTestTenantDetails();

          Optional<UserDirectory> userDirectoryOptional =
              securityService.createTenant(tenant, true);

          if (userDirectoryOptional.isEmpty()) {
            fail("Failed to retrieve the new user directory for the new tenant");
          }

          UserDirectory userDirectory = userDirectoryOptional.get();

          Group group = getTestGroupDetails(userDirectory.getId());

          securityService.createGroup(group);

          User user = getTestUserDetails(userDirectory.getId());

          securityService.createUser(user, false, false);
          securityService.addUserToGroup(
              userDirectory.getId(), group.getName(), user.getUsername());

          List<String> groupNames =
              securityService.getGroupNamesForUser(userDirectory.getId(), user.getUsername());

          assertEquals(
              1,
              groupNames.size(),
              "The correct number of group names was not retrieved for the user ("
                  + user.getUsername()
                  + ")");
          assertEquals(
              group.getName(),
              groupNames.get(0),
              "The user ("
                  + user.getUsername()
                  + ") was not added to the group ("
                  + group.getName()
                  + ")");
          securityService.deleteGroup(userDirectory.getId(), group.getName());
        });
  }

  /** Test the delete invalid function functionality. */
  @Test
  public void deleteInvalidFunctionTest() throws Exception {
    assertThrows(
        FunctionNotFoundException.class,
        () -> {
          Function function = getTestFunctionDetails();

          securityService.createFunction(function);
          securityService.deleteFunction("INVALID");
        });
  }

  /** Test the delete invalid group functionality. */
  @Test
  public void deleteInvalidGroupTest() throws Exception {
    assertThrows(
        GroupNotFoundException.class,
        () -> {
          Tenant tenant = getTestTenantDetails();

          securityService.deleteGroup(SecurityService.DEFAULT_USER_DIRECTORY_ID, "INVALID");
        });
  }

  /** Test the delete invalid user functionality. */
  @Test
  public void deleteInvalidUserTest() throws Exception {
    assertThrows(
        UserNotFoundException.class,
        () -> {
          Tenant tenant = getTestTenantDetails();

          Optional<UserDirectory> userDirectoryOptional =
              securityService.createTenant(tenant, true);

          if (userDirectoryOptional.isEmpty()) {
            fail("Failed to retrieve the new user directory for the new tenant");
          }

          UserDirectory userDirectory = userDirectoryOptional.get();

          User user = getTestUserDetails(userDirectory.getId());

          securityService.createUser(user, false, false);
          securityService.deleteUser(userDirectory.getId(), "INVALID");
        });
  }

  /** Test the duplicate function functionality. */
  @Test
  public void duplicateFunctionTest() throws Exception {
    assertThrows(
        DuplicateFunctionException.class,
        () -> {
          Function function = getTestFunctionDetails();

          securityService.createFunction(function);
          securityService.createFunction(function);
        });
  }

  /** Test the duplicate group functionality. */
  @Test
  public void duplicateGroupTest() throws Exception {
    assertThrows(
        DuplicateGroupException.class,
        () -> {
          Tenant tenant = getTestTenantDetails();

          Optional<UserDirectory> userDirectoryOptional =
              securityService.createTenant(tenant, true);

          if (userDirectoryOptional.isEmpty()) {
            fail("Failed to retrieve the new user directory for the new tenant");
          }

          UserDirectory userDirectory = userDirectoryOptional.get();

          Group group = getTestGroupDetails(userDirectory.getId());

          securityService.createGroup(group);
          securityService.createGroup(group);
        });
  }

  /** Test the duplicate tenant functionality. */
  @Test
  public void duplicateTenantTest() throws Exception {
    assertThrows(
        DuplicateTenantException.class,
        () -> {
          Tenant tenant = getTestTenantDetails();

          securityService.createTenant(tenant, false);
          securityService.createTenant(tenant, false);
        });
  }

  /** Test the duplicate user functionality. */
  @Test
  public void duplicateUserTest() throws Exception {
    assertThrows(
        DuplicateUserException.class,
        () -> {
          Tenant tenant = getTestTenantDetails();

          Optional<UserDirectory> userDirectoryOptional =
              securityService.createTenant(tenant, true);

          if (userDirectoryOptional.isEmpty()) {
            fail("Failed to retrieve the new user directory for the new tenant");
          }

          UserDirectory userDirectory = userDirectoryOptional.get();

          User user = getTestUserDetails(userDirectory.getId());

          securityService.createUser(user, false, false);
          securityService.createUser(user, false, false);
        });
  }

  /** Test the expired user password functionality. */
  @Test
  public void expiredUserPasswordTest() throws Exception {
    assertThrows(
        ExpiredPasswordException.class,
        () -> {
          Tenant tenant = getTestTenantDetails();

          Optional<UserDirectory> userDirectoryOptional =
              securityService.createTenant(tenant, true);

          if (userDirectoryOptional.isEmpty()) {
            fail("Failed to retrieve the new user directory for the new tenant");
          }

          UserDirectory userDirectory = userDirectoryOptional.get();

          User user = getTestUserDetails(userDirectory.getId());

          securityService.createUser(user, false, false);
          securityService.adminChangePassword(
              userDirectory.getId(),
              user.getUsername(),
              "Password2",
              true,
              false,
              true,
              PasswordChangeReason.ADMINISTRATIVE);
          securityService.authenticate(user.getUsername(), "Password2");
        });
  }

  /** Test the failed authentication functionality. */
  @Test
  public void failedAuthenticationTest() throws Exception {
    Tenant tenant = getTestTenantDetails();

    Optional<UserDirectory> userDirectoryOptional = securityService.createTenant(tenant, true);

    if (userDirectoryOptional.isEmpty()) {
      fail("Failed to retrieve the new user directory for the new tenant");
    }

    UserDirectory userDirectory = userDirectoryOptional.get();

    User user = getTestUserDetails(userDirectory.getId());

    securityService.createUser(user, false, false);

    try {
      securityService.authenticate(user.getUsername(), "Invalid Password");
    } catch (AuthenticationFailedException ignored) {
    }

    user = securityService.getUser(userDirectory.getId(), user.getUsername());

    assertEquals(
        1,
        user.getPasswordAttempts().intValue(),
        "The correct number of password attempts was not retrieved");
  }

  /** Test the find users functionality. */
  @Test
  public void findUsersTest() throws Exception {
    Tenant tenant = getTestTenantDetails();

    Optional<UserDirectory> userDirectoryOptional = securityService.createTenant(tenant, true);

    if (userDirectoryOptional.isEmpty()) {
      fail("Failed to retrieve the new user directory for the new tenant");
    }

    UserDirectory userDirectory = userDirectoryOptional.get();

    for (int i = 1; i < 20; i++) {
      User user = getNumberedTestUserDetails(userDirectory.getId(), i);

      securityService.createUser(user, false, false);
    }

    List<User> retrievedUsersAll = securityService.getUsers(userDirectory.getId());

    assertEquals(19, retrievedUsersAll.size(), "The correct number of users was not retrieved");

    List<UserAttribute> userAttributes = new ArrayList<>();

    userAttributes.add(new UserAttribute("status", "active"));
    userAttributes.add(new UserAttribute("email", "testing0"));
    userAttributes.add(new UserAttribute("name", "Name 1"));
    userAttributes.add(new UserAttribute("preferredName", "Preferred Name 1"));
    userAttributes.add(new UserAttribute("mobileNumber", "+2782666"));
    userAttributes.add(new UserAttribute("username", "Username 1"));

    try {
      List<User> retrievedUsers = securityService.findUsers(userDirectory.getId(), userAttributes);

      assertEquals(
          11,
          retrievedUsers.size(),
          "The correct number of users was not retrieved matching the search criteria");
    } catch (InvalidAttributeException e) {
      fail("Invalid attribute while finding users: " + e.getMessage());
    }
  }

  /** Test the create function functionality. */
  @Test
  public void functionTest() throws Exception {
    Function function = getTestFunctionDetails();

    Function copyFunction =
        new Function(function.getCode(), function.getName(), function.getDescription());

    compareFunctions(function, copyFunction);

    List<Function> beforeRetrievedFunctions = securityService.getFunctions();

    securityService.createFunction(function);

    Function retrievedFunction = securityService.getFunction(function.getCode());

    compareFunctions(function, retrievedFunction);

    List<Function> afterRetrievedFunctions = securityService.getFunctions();

    assertEquals(
        beforeRetrievedFunctions.size() + 1,
        afterRetrievedFunctions.size(),
        "The correct number of functions was not retrieved");

    boolean foundFunction = false;

    for (Function afterRetrievedFunction : afterRetrievedFunctions) {
      if (afterRetrievedFunction.getCode().equals(function.getCode())) {
        compareFunctions(function, afterRetrievedFunction);

        foundFunction = true;

        break;
      }
    }

    if (!foundFunction) {
      fail("Failed to find the function (" + function.getCode() + ") in the list of functions");
    }

    function.setName("Test Updated Function Name");
    function.setDescription("Test Updated Function Description");
    securityService.updateFunction(function);

    Function retrievedUpdatedFunction = securityService.getFunction(function.getCode());

    compareFunctions(function, retrievedUpdatedFunction);

    securityService.deleteFunction(function.getCode());

    try {
      securityService.getFunction(function.getCode());

      fail("Retrieved the function (" + function.getCode() + ") that should have been deleted");
    } catch (FunctionNotFoundException ignore) {
    }
  }

  /** Test the functionality to retrieve the authorised function codes for the user. */
  @Test
  public void getFunctionCodesForUserTest() throws Exception {
    User user = getTestUserDetails(SecurityService.DEFAULT_USER_DIRECTORY_ID);

    securityService.createUser(user, false, false);

    securityService.addUserToGroup(
        SecurityService.DEFAULT_USER_DIRECTORY_ID, "Administrators", user.getUsername());

    List<String> groupNamesForUser =
        securityService.getGroupNamesForUser(
            SecurityService.DEFAULT_USER_DIRECTORY_ID, user.getUsername());

    assertEquals(
        1,
        groupNamesForUser.size(),
        "The correct number of group names was not retrieved for the user ("
            + user.getUsername()
            + ")");

    List<String> functionCodesForUser =
        securityService.getFunctionCodesForUser(
            SecurityService.DEFAULT_USER_DIRECTORY_ID, user.getUsername());

    assertEquals(
        0,
        functionCodesForUser.size(),
        "The correct number of function codes was not retrieved for the user ("
            + user.getUsername()
            + ")");
  }

  /** Test the group membership functionality. */
  @Test
  public void groupMembershipTest() throws Exception {
    Tenant tenant = getTestTenantDetails();

    Optional<UserDirectory> userDirectoryOptional = securityService.createTenant(tenant, true);

    if (userDirectoryOptional.isEmpty()) {
      fail("Failed to retrieve the new user directory for the new tenant");
    }

    UserDirectory userDirectory = userDirectoryOptional.get();

    Group group = getTestGroupDetails(userDirectory.getId());

    securityService.createGroup(group);

    List<GroupMember> retrievedGroupMembers =
        securityService.getMembersForGroup(userDirectory.getId(), group.getName());

    assertEquals(
        0, retrievedGroupMembers.size(), "The correct number of group members was not retrieved");

    User firstUser = getTestUserDetails(userDirectory.getId());

    securityService.createUser(firstUser, false, false);

    User secondUser = getTestUserDetails(userDirectory.getId());

    securityService.createUser(secondUser, false, false);

    securityService.addMemberToGroup(
        userDirectory.getId(), group.getName(), GroupMemberType.USER, firstUser.getUsername());

    securityService.addMemberToGroup(
        userDirectory.getId(), group.getName(), GroupMemberType.USER, secondUser.getUsername());

    retrievedGroupMembers =
        securityService.getMembersForGroup(userDirectory.getId(), group.getName());

    assertEquals(
        2, retrievedGroupMembers.size(), "The correct number of group members was not retrieved");

    GroupMembers filteredGroupMembers =
        securityService.getMembersForGroup(
            userDirectory.getId(), group.getName(), secondUser.getUsername(), null, null, null);

    assertEquals(
        1,
        filteredGroupMembers.getGroupMembers().size(),
        "The correct number of group members was not retrieved");
  }

  /** Test the group functionality. */
  @Test
  public void groupTest() throws Exception {
    Tenant tenant = getTestTenantDetails();

    Optional<UserDirectory> userDirectoryOptional = securityService.createTenant(tenant, true);

    if (userDirectoryOptional.isEmpty()) {
      fail("Failed to retrieve the new user directory for the new tenant");
    }

    UserDirectory userDirectory = userDirectoryOptional.get();

    assertTrue(
        securityService
            .getUserDirectoryCapabilities(userDirectory.getId())
            .getSupportsGroupAdministration(),
        "The user directory does not support group administration");

    Group group = getTestGroupDetails(userDirectory.getId());

    securityService.createGroup(group);

    Group retrievedGroup = securityService.getGroup(userDirectory.getId(), group.getName());

    compareGroups(group, retrievedGroup);

    List<Group> retrievedGroups = securityService.getGroups(userDirectory.getId());

    assertEquals(1, retrievedGroups.size(), "The correct number of groups was not retrieved");

    compareGroups(group, retrievedGroups.get(0));

    List<String> retrievedGroupNames = securityService.getGroupNames(userDirectory.getId());

    assertEquals(1, retrievedGroups.size(), "The correct number of group names was not retrieved");

    assertEquals(group.getName(), retrievedGroupNames.get(0));

    Groups retrievedFilteredGroups =
        securityService.getGroups(
            userDirectory.getId(), "Test", SortDirection.ASCENDING, null, null);

    assertEquals(
        1,
        retrievedFilteredGroups.getGroups().size(),
        "The correct number of filtered groups was not retrieved");

    compareGroups(group, retrievedFilteredGroups.getGroups().get(0));

    group.setDescription("Test Updated Group Description");
    securityService.updateGroup(group);

    Group retrievedUpdatedGroup = securityService.getGroup(userDirectory.getId(), group.getName());

    compareGroups(group, retrievedUpdatedGroup);

    securityService.deleteGroup(userDirectory.getId(), group.getName());

    try {
      securityService.getGroup(userDirectory.getId(), group.getName());

      fail("Retrieved the group (" + group.getName() + ") that should have been deleted");
    } catch (GroupNotFoundException ignored) {
    }
  }

  /** Test the functionality to check whether a user is a member of a group. */
  @Test
  public void isUserInGroupTest() throws Exception {
    Tenant tenant = getTestTenantDetails();

    Optional<UserDirectory> userDirectoryOptional = securityService.createTenant(tenant, true);

    if (userDirectoryOptional.isEmpty()) {
      fail("Failed to retrieve the new user directory for the new tenant");
    }

    UserDirectory userDirectory = userDirectoryOptional.get();

    Group group = getTestGroupDetails(userDirectory.getId());

    securityService.createGroup(group);

    User user = getTestUserDetails(userDirectory.getId());

    securityService.createUser(user, false, false);
    securityService.addUserToGroup(userDirectory.getId(), group.getName(), user.getUsername());
    assertTrue(
        securityService.isUserInGroup(userDirectory.getId(), group.getName(), user.getUsername()),
        "Could not determine that the user ("
            + user.getUsername()
            + ") is a member of the group ("
            + group.getName()
            + ")");
  }

  /** Test the locked user functionality. */
  @Test
  public void lockedUserTest() throws Exception {
    assertThrows(
        UserLockedException.class,
        () -> {
          Tenant tenant = getTestTenantDetails();

          Optional<UserDirectory> userDirectoryOptional =
              securityService.createTenant(tenant, true);

          if (userDirectoryOptional.isEmpty()) {
            fail("Failed to retrieve the new user directory for the new tenant");
          }

          UserDirectory userDirectory = userDirectoryOptional.get();

          User user = getTestUserDetails(userDirectory.getId());

          securityService.createUser(user, false, false);
          securityService.adminChangePassword(
              userDirectory.getId(),
              user.getUsername(),
              "Password2",
              false,
              true,
              true,
              PasswordChangeReason.ADMINISTRATIVE);
          securityService.authenticate(user.getUsername(), "Password2");
        });
  }

  /** Test the password reset functionality. */
  @Test
  public void passwordResetTest() throws Exception {
    Tenant tenant = getTestTenantDetails();

    Optional<UserDirectory> userDirectoryOptional = securityService.createTenant(tenant, true);

    if (userDirectoryOptional.isEmpty()) {
      fail("Failed to retrieve the new user directory for the new tenant");
    }

    UserDirectory userDirectory = userDirectoryOptional.get();

    Group group = getTestGroupDetails(userDirectory.getId());

    securityService.createGroup(group);

    User user = getTestUserDetails(userDirectory.getId());

    securityService.createUser(user, false, false);

    securityService.initiatePasswordReset(user.getUsername(), "XXX", false, "Testing123");

    securityService.resetPassword(user.getUsername(), "New Password", "Testing123");

    securityService.authenticate(user.getUsername(), "New Password");
  }

  /** Test the reload user directories functionality. */
  @Test
  public void reloadUserDirectoriesTest() throws Exception {
    securityService.reloadUserDirectories();
  }

  /** Test the functionality to remove a user from a group. */
  @Test
  public void removeUserFromGroupTest() throws Exception {
    Tenant tenant = getTestTenantDetails();

    Optional<UserDirectory> userDirectoryOptional = securityService.createTenant(tenant, true);

    if (userDirectoryOptional.isEmpty()) {
      fail("Failed to retrieve the new user directory for the new tenant");
    }

    UserDirectory userDirectory = userDirectoryOptional.get();

    Group group = getTestGroupDetails(userDirectory.getId());

    securityService.createGroup(group);

    User user = getTestUserDetails(userDirectory.getId());

    securityService.createUser(user, false, false);
    securityService.addUserToGroup(userDirectory.getId(), group.getName(), user.getUsername());

    List<String> groupNames =
        securityService.getGroupNamesForUser(userDirectory.getId(), user.getUsername());

    assertEquals(
        1,
        groupNames.size(),
        "The correct number of group names was not retrieved for the user ("
            + user.getUsername()
            + ")");
    assertEquals(
        group.getName(),
        groupNames.get(0),
        "The user ("
            + user.getUsername()
            + ") was not added to the group ("
            + group.getName()
            + ")");
    securityService.removeUserFromGroup(userDirectory.getId(), group.getName(), user.getUsername());

    groupNames = securityService.getGroupNamesForUser(userDirectory.getId(), user.getUsername());

    assertEquals(
        0,
        groupNames.size(),
        "The correct number of group names was not retrieved for the user ("
            + user.getUsername()
            + ")");
  }

  /** Test the retrieve user directory types functionality. */
  @Test
  public void retrieveUserDirectoryTypesTest() throws Exception {
    List<UserDirectoryType> userDirectoryTypes = securityService.getUserDirectoryTypes();

    assertEquals(
        2,
        userDirectoryTypes.size(),
        "The correct number of user directory types was not retrieved");

    boolean foundInternalUserDirectoryType = false;

    for (UserDirectoryType userDirectoryType : userDirectoryTypes) {
      if (userDirectoryType.getCode().equals(SecurityService.INTERNAL_USER_DIRECTORY_TYPE)) {
        foundInternalUserDirectoryType = true;

        break;
      }
    }

    if (!foundInternalUserDirectoryType) {
      fail(
          "Failed to find the internal user directory type ("
              + SecurityService.INTERNAL_USER_DIRECTORY_TYPE
              + ") in the list of user directory types");
    }

    boolean foundLdapUserDirectoryType = false;

    for (UserDirectoryType userDirectoryType : userDirectoryTypes) {
      if (userDirectoryType.getCode().equals(SecurityService.LDAP_USER_DIRECTORY_TYPE)) {
        foundLdapUserDirectoryType = true;

        break;
      }
    }

    if (!foundLdapUserDirectoryType) {
      fail(
          "Failed to find the internal user directory type ("
              + SecurityService.LDAP_USER_DIRECTORY_TYPE
              + ") in the list of user directory types");
    }
  }

  /** Test the role functionality. */
  @Test
  public void roleTest() throws Exception {
    List<Role> retrievedRoles = securityService.getRoles();

    assertEquals(3, retrievedRoles.size(), "The correct number of roles was not retrieved");

    List<String> retrievedRoleCodes =
        securityService.getRoleCodesForUser(
            SecurityService.DEFAULT_USER_DIRECTORY_ID, SecurityService.ADMINISTRATOR_USERNAME);

    assertEquals(
        1, retrievedRoleCodes.size(), "The correct number of role codes was not retrieved");

    assertEquals(
        SecurityService.ADMINISTRATOR_ROLE_CODE,
        retrievedRoleCodes.get(0),
        "The expected role code was not retrieved");

    List<GroupRole> retrievedGroupRoles =
        securityService.getRolesForGroup(
            SecurityService.DEFAULT_USER_DIRECTORY_ID, SecurityService.ADMINISTRATORS_GROUP_NAME);

    assertEquals(
        1, retrievedGroupRoles.size(), "The correct number of group roles was not retrieved");

    assertEquals(
        SecurityService.ADMINISTRATOR_ROLE_CODE,
        retrievedGroupRoles.get(0).getRoleCode(),
        "The expected role code was not retrieved");

    retrievedRoleCodes =
        securityService.getRoleCodesForGroup(
            SecurityService.DEFAULT_USER_DIRECTORY_ID, SecurityService.ADMINISTRATORS_GROUP_NAME);

    assertEquals(
        1, retrievedRoleCodes.size(), "The correct number of role codes was not retrieved");

    assertEquals(
        SecurityService.ADMINISTRATOR_ROLE_CODE,
        retrievedRoleCodes.get(0),
        "The expected role code was not retrieved");

    Tenant tenant = getTestTenantDetails();

    Optional<UserDirectory> userDirectoryOptional = securityService.createTenant(tenant, true);

    if (userDirectoryOptional.isEmpty()) {
      fail("Failed to retrieve the new user directory for the new tenant");
    }

    UserDirectory userDirectory = userDirectoryOptional.get();

    Group group = getTestGroupDetails(userDirectory.getId());

    securityService.createGroup(group);

    securityService.addRoleToGroup(
        userDirectory.getId(), group.getName(), SecurityService.TENANT_ADMINISTRATOR_ROLE_CODE);

    retrievedGroupRoles = securityService.getRolesForGroup(userDirectory.getId(), group.getName());

    assertEquals(
        1, retrievedGroupRoles.size(), "The correct number of group roles was not retrieved");

    assertEquals(
        SecurityService.TENANT_ADMINISTRATOR_ROLE_CODE,
        retrievedGroupRoles.get(0).getRoleCode(),
        "The expected role code was not retrieved");

    retrievedRoleCodes =
        securityService.getRoleCodesForGroup(userDirectory.getId(), group.getName());

    assertEquals(
        1, retrievedRoleCodes.size(), "The correct number of role codes was not retrieved");

    assertEquals(
        SecurityService.TENANT_ADMINISTRATOR_ROLE_CODE,
        retrievedRoleCodes.get(0),
        "The expected role code was not retrieved");

    securityService.removeRoleFromGroup(
        userDirectory.getId(), group.getName(), SecurityService.TENANT_ADMINISTRATOR_ROLE_CODE);

    retrievedGroupRoles = securityService.getRolesForGroup(userDirectory.getId(), group.getName());

    assertEquals(
        0, retrievedGroupRoles.size(), "The correct number of group roles was not retrieved");
  }

  /** Test the tenant functionality. */
  @Test
  public void tenantTest() throws Exception {
    Tenant tenant = getTestTenantDetails();

    List<Tenant> beforeRetrievedTenants = securityService.getTenants();

    Optional<UserDirectory> userDirectoryOptional = securityService.createTenant(tenant, true);

    if (userDirectoryOptional.isEmpty()) {
      fail("Failed to retrieve the new user directory for the new tenant");
    }

    UserDirectory userDirectory = userDirectoryOptional.get();

    Tenant retrievedTenant = securityService.getTenant(tenant.getId());

    compareTenants(tenant, retrievedTenant);

    String retrievedTenantName = securityService.getTenantName(tenant.getId());

    assertEquals(
        tenant.getName(), retrievedTenantName, "The correct tenant name was not retrieved");

    List<Tenant> afterRetrievedTenants = securityService.getTenants();

    assertEquals(
        beforeRetrievedTenants.size() + 1,
        afterRetrievedTenants.size(),
        "The correct number of tenants was not retrieved");

    boolean foundTenant = false;

    for (Tenant afterRetrievedTenant : afterRetrievedTenants) {
      if (afterRetrievedTenant.getId().equals(tenant.getId())) {
        compareTenants(tenant, afterRetrievedTenant);

        foundTenant = true;

        break;
      }
    }

    if (!foundTenant) {
      fail("Failed to find the tenant (" + tenant.getId() + ") in the list of " + "tenants");
    }

    Tenants filteredTenants = securityService.getTenants(tenant.getName(), null, null, null);

    assertEquals(
        1,
        filteredTenants.getTenants().size(),
        "The correct number of filtered tenants was not retrieved");

    compareTenants(tenant, filteredTenants.getTenants().get(0));

    filteredTenants = securityService.getTenants(tenant.getName(), SortDirection.ASCENDING, 0, 100);

    assertEquals(
        1,
        filteredTenants.getTenants().size(),
        "The correct number of filtered tenants was not retrieved");

    compareTenants(tenant, filteredTenants.getTenants().get(0));

    List<UserDirectorySummary> userDirectorySummaries =
        securityService.getUserDirectorySummariesForTenant(tenant.getId());

    assertEquals(
        1,
        userDirectorySummaries.size(),
        "The correct number of user directory summaries was not retrieved for the tenant");

    assertEquals(
        userDirectory.getId(),
        userDirectorySummaries.get(0).getId(),
        "The correct user directory summary was not retrieved");

    tenant.setName("Updated " + tenant.getName());

    securityService.updateTenant(tenant);

    retrievedTenant = securityService.getTenant(tenant.getId());

    compareTenants(tenant, retrievedTenant);

    UserDirectory additionalUserDirectory = getTestUserDirectoryDetails();

    securityService.createUserDirectory(additionalUserDirectory);

    List<UserDirectorySummary> retrievedUserDirectorySummaries =
        securityService.getUserDirectorySummariesForTenant(tenant.getId());

    assertEquals(
        1,
        retrievedUserDirectorySummaries.size(),
        "The correct number of user directory summaries was not retrieved");

    securityService.addUserDirectoryToTenant(tenant.getId(), additionalUserDirectory.getId());

    retrievedUserDirectorySummaries =
        securityService.getUserDirectorySummariesForTenant(tenant.getId());

    assertEquals(
        2,
        retrievedUserDirectorySummaries.size(),
        "The correct number of user directory summaries was not retrieved");

    securityService.removeUserDirectoryFromTenant(tenant.getId(), additionalUserDirectory.getId());

    retrievedUserDirectorySummaries =
        securityService.getUserDirectorySummariesForTenant(tenant.getId());

    assertEquals(
        1,
        retrievedUserDirectorySummaries.size(),
        "The correct number of user directory summaries was not retrieved");

    securityService.deleteTenant(tenant.getId());

    try {
      securityService.getTenant(tenant.getId());

      fail("Retrieved the tenant (" + tenant.getId() + ") that should have been " + "deleted");
    } catch (TenantNotFoundException ignored) {
    }
  }

  /** Test the password encoder functionality. */
  @Test
  public void testPasswordEncoder() throws Exception {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10, new SecureRandom());

    String encodedPassword = passwordEncoder.encode("Password1");
  }

  /** Test the user directory tenant mapping functionality. */
  @Test
  public void userDirectoryTenantMappingTest() throws Exception {
    Tenant tenant = getTestTenantDetails();

    Optional<UserDirectory> userDirectoryOptional = securityService.createTenant(tenant, true);

    if (userDirectoryOptional.isEmpty()) {
      fail("Failed to retrieve the new user directory for the new tenant");
    }

    UserDirectory userDirectory = userDirectoryOptional.get();

    List<Tenant> tenantsForUserDirectory =
        securityService.getTenantsForUserDirectory(userDirectory.getId());

    assertEquals(
        1,
        tenantsForUserDirectory.size(),
        "The correct number of tenants was not retrieved for the user directory");

    List<UUID> tenantIdsForUserDirectory =
        securityService.getTenantIdsForUserDirectory(userDirectory.getId());

    assertEquals(
        1,
        tenantIdsForUserDirectory.size(),
        "The correct number of tenant IDs was not retrieved for the user directory");

    List<UserDirectory> userDirectoriesForTenant =
        securityService.getUserDirectoriesForTenant(tenant.getId());

    assertEquals(
        1,
        userDirectoriesForTenant.size(),
        "The correct number of user directories was not retrieved for the tenant");
  }

  /** Test the user directory functionality. */
  @Test
  public void userDirectoryTest() throws Exception {
    List<UserDirectory> beforeRetrievedUserDirectories = securityService.getUserDirectories();

    UserDirectory userDirectory = getTestUserDirectoryDetails();

    securityService.createUserDirectory(userDirectory);

    assertTrue(
        securityService
            .getUserDirectoryCapabilities(userDirectory.getId())
            .getSupportsUserAdministration(),
        "The user directory does not support user administration");

    UserDirectory retrievedUserDirectory = securityService.getUserDirectory(userDirectory.getId());

    compareUserDirectories(userDirectory, retrievedUserDirectory);

    String retrievedUserDirectoryName = securityService.getUserDirectoryName(userDirectory.getId());

    assertEquals(
        retrievedUserDirectoryName,
        userDirectory.getName(),
        "The correct user directory name was not retrieved");

    List<UserDirectory> afterRetrievedUserDirectories = securityService.getUserDirectories();

    assertEquals(
        beforeRetrievedUserDirectories.size() + 1,
        afterRetrievedUserDirectories.size(),
        "The correct number of user directories was not retrieved");

    boolean foundUserDirectory = false;

    for (UserDirectory afterRetrievedUserDirectory : afterRetrievedUserDirectories) {
      if (afterRetrievedUserDirectory.getId().equals(userDirectory.getId())) {
        compareUserDirectories(userDirectory, afterRetrievedUserDirectory);

        foundUserDirectory = true;

        break;
      }
    }

    if (!foundUserDirectory) {
      fail(
          "Failed to find the user directory ("
              + userDirectory.getId()
              + ") in the list of "
              + "tenants");
    }

    UserDirectories filteredUserDirectories =
        securityService.getUserDirectories(
            userDirectory.getName(), SortDirection.ASCENDING, null, null);

    assertEquals(
        1,
        filteredUserDirectories.getUserDirectories().size(),
        "The correct number of filtered user directories was not retrieved");

    compareUserDirectories(userDirectory, filteredUserDirectories.getUserDirectories().get(0));

    UserDirectorySummaries filteredUserDirectorySummaries =
        securityService.getUserDirectorySummaries(
            userDirectory.getName(), SortDirection.ASCENDING, null, null);

    assertEquals(
        1,
        filteredUserDirectorySummaries.getUserDirectorySummaries().size(),
        "The correct number of filtered user directory summaries was not retrieved");

    userDirectory.setName("Updated " + userDirectory.getName());

    securityService.updateUserDirectory(userDirectory);

    retrievedUserDirectory = securityService.getUserDirectory(userDirectory.getId());

    compareUserDirectories(userDirectory, retrievedUserDirectory);

    securityService.deleteUserDirectory(userDirectory.getId());

    try {
      securityService.getUserDirectory(userDirectory.getId());

      fail(
          "Retrieved the user directory ("
              + userDirectory.getId()
              + ") that should have been "
              + "deleted");
    } catch (UserDirectoryNotFoundException ignored) {
    }
  }

  /** Test the user password history functionality. */
  @Test
  public void userPasswordHistoryTest() throws Exception {
    assertThrows(
        ExistingPasswordException.class,
        () -> {
          Tenant tenant = getTestTenantDetails();

          Optional<UserDirectory> userDirectoryOptional =
              securityService.createTenant(tenant, true);

          if (userDirectoryOptional.isEmpty()) {
            fail("Failed to retrieve the new user directory for the new tenant");
          }

          UserDirectory userDirectory = userDirectoryOptional.get();

          User user = getTestUserDetails(userDirectory.getId());

          String originalPassword = user.getPassword();

          securityService.createUser(user, false, false);
          securityService.changePassword(user.getUsername(), originalPassword, "Password1");
          securityService.changePassword(user.getUsername(), "Password1", "Password2");
          securityService.changePassword(user.getUsername(), "Password2", "Password1");
        });
  }

  /** Test the user functionality. */
  @Test
  public void userTest() throws Exception {
    Tenant tenant = getTestTenantDetails();

    Optional<UserDirectory> userDirectoryOptional = securityService.createTenant(tenant, true);

    if (userDirectoryOptional.isEmpty()) {
      fail("Failed to retrieve the new user directory for the new tenant");
    }

    UserDirectory userDirectory = userDirectoryOptional.get();

    User user = getTestUserDetails(userDirectory.getId());

    securityService.createUser(user, false, false);

    Optional<UUID> userDirectoryIdOptional =
        securityService.getUserDirectoryIdForUser(user.getUsername());

    if (userDirectoryIdOptional.isEmpty()) {
      fail("Failed to retrieve user directory ID for the user");
    }

    UUID userDirectoryId = userDirectoryIdOptional.get();

    assertEquals(
        userDirectory.getId(),
        userDirectoryIdOptional.get(),
        "The correct user directory ID was not retrieved for the user");

    List<UUID> userDirectorIds = securityService.getUserDirectoryIdsForUser(user.getUsername());

    assertEquals(
        1,
        userDirectorIds.size(),
        "The correct number of user director IDs was not retrieved for the user");

    assertEquals(
        userDirectory.getId(),
        userDirectorIds.get(0),
        "The correct user directory ID was not retrieved for the user");

    User retrievedUser = securityService.getUser(userDirectory.getId(), user.getUsername());

    compareUsers(user, retrievedUser, false);

    assertTrue(
        securityService.isExistingUser(userDirectory.getId(), user.getUsername()),
        "Failed to confirm that the user exists");

    String retrievedUserName = securityService.getUserName(userDirectoryId, user.getUsername());

    assertEquals(
        user.getName(), retrievedUserName, "The correct name was not retrieved for the user");

    try {
      securityService.getUserName(userDirectoryId, "INVALID_USERNAME");
      fail("Incorrectly retrieved name for invalid user");
    } catch (UserNotFoundException ignored) {
    }

    List<User> retrievedUsers = securityService.getUsers(userDirectory.getId());

    assertEquals(1, retrievedUsers.size(), "The correct number of users was not retrieved");

    compareUsers(user, retrievedUsers.get(0), true);

    Users retrievedFilteredUsers =
        securityService.getUsers(
            userDirectory.getId(),
            "Test",
            UserSortBy.USERNAME,
            SortDirection.ASCENDING,
            null,
            null);

    assertEquals(
        1,
        retrievedFilteredUsers.getUsers().size(),
        "The correct number of filtered users was not retrieved");

    compareUsers(user, retrievedFilteredUsers.getUsers().get(0), true);

    LocalDateTime passwordExpiry = LocalDateTime.now();
    passwordExpiry = passwordExpiry.plus(10, ChronoUnit.DAYS);

    user.setPassword("Test Updated Password");
    user.setPasswordExpiry(passwordExpiry);
    user.setPasswordAttempts(2);
    user.setEmail("updated" + user.getEmail());
    user.setName("Test Updated Name");
    user.setPreferredName("Test Updated Preferred Name");
    user.setPhoneNumber("Test Updated Phone Number");
    user.setMobileNumber(user.getMobileNumber().replace("+2782", "+2783"));

    securityService.updateUser(user, false, false);

    User retrievedUpdatedUser = securityService.getUser(userDirectory.getId(), user.getUsername());

    compareUsers(user, retrievedUpdatedUser, true);

    securityService.deleteUser(userDirectory.getId(), user.getUsername());

    try {
      securityService.getUser(userDirectory.getId(), user.getUsername());

      fail("Retrieved the user (" + user.getUsername() + ") that should have been deleted");
    } catch (UserNotFoundException ignored) {
    }
  }

  private void compareFunctions(Function function1, Function function2) {
    assertEquals(
        function1.getCode(), function2.getCode(), "The code values for the functions do not match");
    assertEquals(
        function1.getDescription(),
        function2.getDescription(),
        "The description values for the functions do not match");
    assertEquals(
        function1.getName(), function2.getName(), "The name values for the functions do not match");
  }

  private void compareGroups(Group group1, Group group2) {
    assertEquals(
        group1.getDescription(),
        group2.getDescription(),
        "The description values for the groups do not match");
    assertEquals(group1.getId(), group2.getId(), "The ID values for the groups do not match");
    assertEquals(
        group1.getName(), group2.getName(), "The group name values for the groups do not match");
    assertEquals(
        group1.getUserDirectoryId(),
        group2.getUserDirectoryId(),
        "The user directory ID values for the groups do not match");
  }

  private void compareTenants(Tenant tenant1, Tenant tenant2) {
    assertEquals(tenant1.getId(), tenant2.getId(), "The ID values for the tenants do not match");
    assertEquals(
        tenant1.getName(), tenant2.getName(), "The name values for the tenants do not match");
  }

  private void compareUserDirectories(UserDirectory userDirectory1, UserDirectory userDirectory2) {
    assertEquals(
        userDirectory1.getId(),
        userDirectory2.getId(),
        "The ID values for the user directories do not match");
    assertEquals(
        userDirectory1.getName(),
        userDirectory2.getName(),
        "The name values for the user directories do not match");
    assertEquals(
        userDirectory1.getType(),
        userDirectory2.getType(),
        "The type ID values for the user directories do not match");
    assertEquals(
        userDirectory1.getConfiguration(),
        userDirectory2.getConfiguration(),
        "The configuration values for the user directories do not match");
  }

  private void compareUsers(User user1, User user2, boolean checkPasswordExpiry) {
    assertEquals(
        user1.getStatus(), user2.getStatus(), "The status values for the users do not match");
    assertEquals(
        user1.getEmail(), user2.getEmail(), "The e-mail values for the users do not match");
    assertEquals(user1.getName(), user2.getName(), "The name values for the users do not match");
    assertEquals(
        user1.getPreferredName(),
        user2.getPreferredName(),
        "The preferred name values for the users do not match");
    assertEquals(user1.getId(), user2.getId(), "The ID values for the users do not match");
    assertEquals(
        user1.getPhoneNumber(),
        user2.getPhoneNumber(),
        "The phone number values for the users do not match");
    assertEquals(
        user1.getMobileNumber(),
        user2.getMobileNumber(),
        "The mobile number values for the users do not match");
    assertEquals(
        user1.getPasswordAttempts(),
        user2.getPasswordAttempts(),
        "The password attempt values for the users do not match");
    assertEquals(
        user1.getUsername(), user2.getUsername(), "The username values for the users do not match");

    if (checkPasswordExpiry) {
      assertEqualsToMillisecond(
          user1.getPasswordExpiry(),
          user2.getPasswordExpiry(),
          "The password expiry values for the users do not match");
    }
  }
}
