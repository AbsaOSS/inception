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
import africa.absa.inception.core.service.ValidationError;
import africa.absa.inception.core.sorting.SortDirection;
import africa.absa.inception.core.util.PasswordUtil;
import africa.absa.inception.core.util.RandomStringGenerator;
import africa.absa.inception.core.util.ResourceUtil;
import africa.absa.inception.mail.IMailService;
import africa.absa.inception.mail.MailTemplate;
import africa.absa.inception.mail.MailTemplateContentType;
import java.lang.reflect.Constructor;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * The <b>SecurityService</b> class provides the Security Service implementation.
 *
 * @author Marcus Portmann
 */
@Service
@SuppressWarnings({"unused", "WeakerAccess", "DuplicatedCode"})
public class SecurityService implements ISecurityService, InitializingBean {

  /** The maximum number of filtered tenants. */
  private static final int MAX_FILTERED_ORGANISATIONS = 100;

  /** The maximum number of filtered user directories. */
  private static final int MAX_FILTERED_USER_DIRECTORIES = 100;

  /** The code for the password reset mail template. */
  private static final String PASSWORD_RESET_MAIL_TEMPLATE_ID =
      "Inception.Security.PasswordResetMail";

  /* Logger */
  private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);

  /** The Spring application context. */
  private final ApplicationContext applicationContext;

  /** The Function Repository. */
  private final FunctionRepository functionRepository;

  /** The Group repository. */
  private final GroupRepository groupRepository;

  /** The Mail Service. */
  private final IMailService mailService;

  /** The Password Reset Repository. */
  private final PasswordResetRepository passwordResetRepository;

  /** The Role Repository. */
  private final RoleRepository roleRepository;

  /**
   * The random alphanumeric string generator that will be used to generate security codes for
   * password resets.
   */
  private final RandomStringGenerator securityCodeGenerator =
      new RandomStringGenerator(
          20, new SecureRandom(), "1234567890ACEFGHJKLMNPQRUVWXYabcdefhijkprstuvwx");

  /** The Tenant Repository. */
  private final TenantRepository tenantRepository;

  /** The User Directory Repository. */
  private final UserDirectoryRepository userDirectoryRepository;

  /** The User Directory Summary Repository. */
  private final UserDirectorySummaryRepository userDirectorySummaryRepository;

  /** The User Directory Type Repository. */
  private final UserDirectoryTypeRepository userDirectoryTypeRepository;

  /** The User Repository. */
  private final UserRepository userRepository;

  /** The JSR-303 validator. */
  private final Validator validator;

  /** The user directories. */
  private Map<UUID, IUserDirectory> userDirectories = new ConcurrentHashMap<>();

  /**
   * Constructs a new <b>SecurityService</b>.
   *
   * @param applicationContext the Spring application context
   * @param validator the JSR-303 validator
   * @param mailService the Mail Service
   * @param functionRepository the Function Repository
   * @param groupRepository the Group Repository
   * @param tenantRepository the Tenant Repository
   * @param passwordResetRepository the Password Reset Repository
   * @param roleRepository the Role Repository
   * @param userDirectoryRepository the User Directory Repository
   * @param userDirectorySummaryRepository the User Directory Summary Repository
   * @param userDirectoryTypeRepository the User Directory Type Repository
   * @param userRepository the User Repository
   */
  public SecurityService(
      ApplicationContext applicationContext,
      Validator validator,
      IMailService mailService,
      FunctionRepository functionRepository,
      GroupRepository groupRepository,
      TenantRepository tenantRepository,
      PasswordResetRepository passwordResetRepository,
      RoleRepository roleRepository,
      UserDirectoryRepository userDirectoryRepository,
      UserDirectorySummaryRepository userDirectorySummaryRepository,
      UserDirectoryTypeRepository userDirectoryTypeRepository,
      UserRepository userRepository) {
    this.applicationContext = applicationContext;
    this.validator = validator;
    this.mailService = mailService;
    this.functionRepository = functionRepository;
    this.groupRepository = groupRepository;
    this.tenantRepository = tenantRepository;
    this.passwordResetRepository = passwordResetRepository;
    this.roleRepository = roleRepository;
    this.userDirectoryRepository = userDirectoryRepository;
    this.userDirectorySummaryRepository = userDirectorySummaryRepository;
    this.userDirectoryTypeRepository = userDirectoryTypeRepository;
    this.userRepository = userRepository;
  }

  @Override
  @Transactional
  public void addMemberToGroup(
      UUID userDirectoryId, String groupName, GroupMemberType memberType, String memberName)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          UserNotFoundException, ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (!StringUtils.hasText(groupName)) {
      throw new InvalidArgumentException("groupName");
    }

    if (memberType == null) {
      throw new InvalidArgumentException("memberType");
    }

    if (!StringUtils.hasText(memberName)) {
      throw new InvalidArgumentException("memberName");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    userDirectory.addMemberToGroup(groupName, memberType, memberName);
  }

  @Override
  @Transactional
  public void addRoleToGroup(UUID userDirectoryId, String groupName, String roleCode)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          RoleNotFoundException, ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (!StringUtils.hasText(groupName)) {
      throw new InvalidArgumentException("groupName");
    }

    if (!StringUtils.hasText(roleCode)) {
      throw new InvalidArgumentException("roleCode");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    userDirectory.addRoleToGroup(groupName, roleCode);
  }

  @Override
  @Transactional
  public void addUserDirectoryToTenant(UUID tenantId, UUID userDirectoryId)
      throws InvalidArgumentException, TenantNotFoundException, UserDirectoryNotFoundException,
          ServiceUnavailableException {
    if (tenantId == null) {
      throw new InvalidArgumentException("tenantId");
    }

    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    try {
      if (!tenantRepository.existsById(tenantId)) {
        throw new TenantNotFoundException(tenantId);
      }

      if (!userDirectoryRepository.existsById(userDirectoryId)) {
        throw new UserDirectoryNotFoundException(userDirectoryId);
      }

      if (tenantRepository.countTenantUserDirectory(tenantId, userDirectoryId) > 0) {
        return;
      }

      tenantRepository.addUserDirectoryToTenant(tenantId, userDirectoryId);
    } catch (TenantNotFoundException | UserDirectoryNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to add the user directory ("
              + userDirectoryId
              + ") to the tenant ("
              + tenantId
              + ")",
          e);
    }
  }

  @Override
  @Transactional
  public void addUserToGroup(UUID userDirectoryId, String groupName, String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          UserNotFoundException, ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (!StringUtils.hasText(groupName)) {
      throw new InvalidArgumentException("groupName");
    }

    if (!StringUtils.hasText(username)) {
      throw new InvalidArgumentException("username");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    userDirectory.addUserToGroup(groupName, username);
  }

  @Override
  @Transactional
  public void adminChangePassword(
      UUID userDirectoryId,
      String username,
      String newPassword,
      boolean expirePassword,
      boolean lockUser,
      boolean resetPasswordHistory,
      PasswordChangeReason reason)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (!StringUtils.hasText(username)) {
      throw new InvalidArgumentException("username");
    }

    if (!StringUtils.hasText(newPassword)) {
      throw new InvalidArgumentException("newPassword");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    userDirectory.adminChangePassword(
        username, newPassword, expirePassword, lockUser, resetPasswordHistory, reason);
  }

  @Override
  public void afterPropertiesSet() {
    try {
      // Load the default password reset mail template
      if (!mailService.mailTemplateExists(PASSWORD_RESET_MAIL_TEMPLATE_ID)) {
        byte[] passwordResetMailTemplate =
            ResourceUtil.getClasspathResource("africa/absa/inception/security/PasswordReset.ftl");

        MailTemplate mailTemplate =
            new MailTemplate(
                PASSWORD_RESET_MAIL_TEMPLATE_ID,
                "Password Reset",
                MailTemplateContentType.HTML,
                passwordResetMailTemplate);

        mailService.createMailTemplate(mailTemplate);
      }

      // Load the user directories
      reloadUserDirectories();
    } catch (Throwable e) {
      throw new RuntimeException("Failed to initialize the Security Service", e);
    }
  }

  @Override
  @Transactional
  public UUID authenticate(String username, String password)
      throws InvalidArgumentException, AuthenticationFailedException, UserLockedException,
          ExpiredPasswordException, UserNotFoundException, ServiceUnavailableException {
    if (!StringUtils.hasText(username)) {
      throw new InvalidArgumentException("username");
    }

    if (!StringUtils.hasText(password)) {
      throw new InvalidArgumentException("password");
    }

    try {
      // First check if this is an internal user and if so determine the user directory ID
      Optional<UUID> internalUserDirectoryIdOptional = getInternalUserDirectoryIdForUser(username);

      if (internalUserDirectoryIdOptional.isPresent()) {
        UUID internalUserDirectoryId = internalUserDirectoryIdOptional.get();

        IUserDirectory internalUserDirectory = userDirectories.get(internalUserDirectoryId);

        if (internalUserDirectory == null) {
          throw new ServiceUnavailableException(
              "The user directory ID ("
                  + internalUserDirectoryId
                  + ") for the internal user ("
                  + username
                  + ") is invalid");
        } else {
          internalUserDirectory.authenticate(username, password);

          return internalUserDirectoryId;
        }
      } else {
        /*
         * Check all of the "external" user directories to see if one of them can authenticate this
         * user.
         */
        for (UUID userDirectoryId : userDirectories.keySet()) {
          IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

          if (userDirectory != null) {
            if (!(userDirectory instanceof InternalUserDirectory)) {
              if (userDirectory.isExistingUser(username)) {
                userDirectory.authenticate(username, password);

                return userDirectoryId;
              }
            }
          }
        }

        throw new UserNotFoundException(username);
      }
    } catch (AuthenticationFailedException
        | UserNotFoundException
        | UserLockedException
        | ExpiredPasswordException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to authenticate the user (" + username + ")", e);
    }
  }

  @Override
  @Transactional
  public UUID changePassword(String username, String password, String newPassword)
      throws InvalidArgumentException, AuthenticationFailedException, UserLockedException,
          ExistingPasswordException, ServiceUnavailableException {
    if (!StringUtils.hasText(username)) {
      throw new InvalidArgumentException("username");
    }

    if (!StringUtils.hasText(password)) {
      throw new InvalidArgumentException("password");
    }

    if (!StringUtils.hasText(newPassword)) {
      throw new InvalidArgumentException("newPassword");
    }

    try {
      // First check if this is an internal user and if so determine the user directory ID
      Optional<UUID> internalUserDirectoryIdOptional = getInternalUserDirectoryIdForUser(username);

      if (internalUserDirectoryIdOptional.isPresent()) {
        UUID internalUserDirectoryId = internalUserDirectoryIdOptional.get();

        IUserDirectory internalUserDirectory = userDirectories.get(internalUserDirectoryId);

        if (internalUserDirectory == null) {
          throw new ServiceUnavailableException(
              "The user directory ID ("
                  + internalUserDirectoryId
                  + ") for the internal user ("
                  + username
                  + ") is invalid");
        } else {
          internalUserDirectory.changePassword(username, password, newPassword);

          return internalUserDirectoryId;
        }
      } else {
        /*
         * Check all of the "external" user directories to see if one of them can change the
         * password for this user.
         */
        for (UUID userDirectoryId : userDirectories.keySet()) {
          IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

          if (userDirectory != null) {
            if (!(userDirectory instanceof InternalUserDirectory)) {
              if (userDirectory.isExistingUser(username)) {
                userDirectory.changePassword(username, password, newPassword);

                return userDirectoryId;
              }
            }
          }
        }

        throw new AuthenticationFailedException(
            "Authentication failed while attempting to change the password for the user ("
                + username
                + ")");
      }
    } catch (AuthenticationFailedException | UserLockedException | ExistingPasswordException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to change the password for the user (" + username + ")", e);
    }
  }

  @Override
  @Transactional
  public void createFunction(Function function)
      throws InvalidArgumentException, DuplicateFunctionException, ServiceUnavailableException {
    validateFunction(function);

    try {
      if (functionRepository.existsById(function.getCode())) {
        throw new DuplicateFunctionException(function.getCode());
      }

      functionRepository.saveAndFlush(function);
    } catch (DuplicateFunctionException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to create the function (" + function.getCode() + ")", e);
    }
  }

  @Override
  @Transactional
  public void createGroup(Group group)
      throws InvalidArgumentException, UserDirectoryNotFoundException, DuplicateGroupException,
          ServiceUnavailableException {
    validateGroup(group);

    IUserDirectory userDirectory = userDirectories.get(group.getUserDirectoryId());

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(group.getUserDirectoryId());
    }

    userDirectory.createGroup(group);
  }

  @Override
  @Transactional
  public Optional<UserDirectory> createTenant(Tenant tenant, boolean createUserDirectory)
      throws InvalidArgumentException, DuplicateTenantException, ServiceUnavailableException {
    validateTenant(tenant);

    UserDirectory userDirectory = null;

    try {
      if ((tenant.getId() != null) && tenantRepository.existsById(tenant.getId())) {
        throw new DuplicateTenantException(tenant.getId());
      }

      if (tenantRepository.existsByNameIgnoreCase(tenant.getName())) {
        throw new DuplicateTenantException(tenant.getName());
      }

      if (createUserDirectory) {
        userDirectory = newInternalUserDirectoryForTenant(tenant);

        tenant.linkUserDirectory(userDirectory);
      }

      tenantRepository.saveAndFlush(tenant);

      try {
        reloadUserDirectories();
      } catch (Throwable e) {
        logger.error("Failed to reload the user directories", e);
      }

      return Optional.ofNullable(userDirectory);
    } catch (DuplicateTenantException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to create the tenant (" + tenant.getId() + ")", e);
    }
  }

  @Override
  @Transactional
  public void createUser(User user, boolean expiredPassword, boolean userLocked)
      throws InvalidArgumentException, UserDirectoryNotFoundException, DuplicateUserException,
          ServiceUnavailableException {
    validateUser(user);

    IUserDirectory userDirectory = userDirectories.get(user.getUserDirectoryId());

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(user.getUserDirectoryId());
    }

    if (getUserDirectoryIdForUser(user.getUsername()).isPresent()) {
      throw new DuplicateUserException(user.getUsername());
    }

    userDirectory.createUser(user, expiredPassword, userLocked);
  }

  @Override
  @Transactional
  public void createUserDirectory(UserDirectory userDirectory)
      throws InvalidArgumentException, DuplicateUserDirectoryException,
          ServiceUnavailableException {
    validateUserDirectory(userDirectory);

    try {
      if ((userDirectory.getId() != null)
          && userDirectoryRepository.existsById(userDirectory.getId())) {
        throw new DuplicateUserDirectoryException(userDirectory.getId());
      }

      if (userDirectoryRepository.existsByNameIgnoreCase(userDirectory.getName())) {
        throw new DuplicateUserDirectoryException(userDirectory.getName());
      }

      userDirectoryRepository.saveAndFlush(userDirectory);

      try {
        reloadUserDirectories();
      } catch (Throwable e) {
        logger.error("Failed to reload the user directories", e);
      }
    } catch (DuplicateUserDirectoryException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to create the user directory (" + userDirectory.getName() + ")", e);
    }
  }

  @Override
  @Transactional
  public void deleteFunction(String functionCode)
      throws InvalidArgumentException, FunctionNotFoundException, ServiceUnavailableException {
    if (!StringUtils.hasText(functionCode)) {
      throw new InvalidArgumentException("functionCode");
    }

    try {
      if (!functionRepository.existsById(functionCode)) {
        throw new FunctionNotFoundException(functionCode);
      }

      functionRepository.deleteById(functionCode);
    } catch (FunctionNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to delete the function (" + functionCode + ")", e);
    }
  }

  @Override
  @Transactional
  public void deleteGroup(UUID userDirectoryId, String groupName)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          ExistingGroupMembersException, ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (!StringUtils.hasText(groupName)) {
      throw new InvalidArgumentException("groupName");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    userDirectory.deleteGroup(groupName);
  }

  @Override
  @Transactional
  public void deleteTenant(UUID tenantId)
      throws InvalidArgumentException, TenantNotFoundException, ServiceUnavailableException {
    if (tenantId == null) {
      throw new InvalidArgumentException("tenantId");
    }

    try {
      if (!tenantRepository.existsById(tenantId)) {
        throw new TenantNotFoundException(tenantId);
      }

      tenantRepository.deleteById(tenantId);
    } catch (TenantNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to delete the tenant (" + tenantId + ")", e);
    }
  }

  @Override
  @Transactional
  public void deleteUser(UUID userDirectoryId, String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (!StringUtils.hasText(username)) {
      throw new InvalidArgumentException("username");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    userDirectory.deleteUser(username);
  }

  @Override
  @Transactional
  public void deleteUserDirectory(UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    try {
      if (!userDirectoryRepository.existsById(userDirectoryId)) {
        throw new UserDirectoryNotFoundException(userDirectoryId);
      }

      userDirectoryRepository.deleteById(userDirectoryId);

      try {
        reloadUserDirectories();
      } catch (Throwable e) {
        logger.error("Failed to reload the user directories", e);
      }
    } catch (UserDirectoryNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to delete the user directory (" + userDirectoryId + ")", e);
    }
  }

  @Override
  public List<User> findUsers(UUID userDirectoryId, List<UserAttribute> userAttributes)
      throws InvalidArgumentException, UserDirectoryNotFoundException, InvalidAttributeException,
          ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (userAttributes == null) {
      throw new InvalidArgumentException("attributes");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.findUsers(userAttributes);
  }

  @Override
  public Function getFunction(String functionCode)
      throws InvalidArgumentException, FunctionNotFoundException, ServiceUnavailableException {
    if (!StringUtils.hasText(functionCode)) {
      throw new InvalidArgumentException("functionCode");
    }

    try {
      Optional<Function> functionOptional = functionRepository.findById(functionCode);

      if (functionOptional.isPresent()) {
        return functionOptional.get();
      } else {
        throw new FunctionNotFoundException(functionCode);
      }
    } catch (FunctionNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the function (" + functionCode + ")", e);
    }
  }

  @Override
  public List<String> getFunctionCodesForUser(UUID userDirectoryId, String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (!StringUtils.hasText(username)) {
      throw new InvalidArgumentException("username");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getFunctionCodesForUser(username);
  }

  @Override
  public List<Function> getFunctions() throws ServiceUnavailableException {
    try {
      return functionRepository.findAll();
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to retrieve the functions", e);
    }
  }

  @Override
  public Group getGroup(UUID userDirectoryId, String groupName)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (!StringUtils.hasText(groupName)) {
      throw new InvalidArgumentException("groupName");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getGroup(groupName);
  }

  @Override
  public List<String> getGroupNames(UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getGroupNames();
  }

  @Override
  public List<String> getGroupNamesForUser(UUID userDirectoryId, String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (!StringUtils.hasText(username)) {
      throw new InvalidArgumentException("username");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getGroupNamesForUser(username);
  }

  @Override
  public List<Group> getGroups(UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getGroups();
  }

  @Override
  public Groups getGroups(
      UUID userDirectoryId,
      String filter,
      SortDirection sortDirection,
      Integer pageIndex,
      Integer pageSize)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if ((pageIndex != null) && (pageIndex < 0)) {
      throw new InvalidArgumentException("pageIndex");
    }

    if ((pageSize != null) && (pageSize <= 0)) {
      throw new InvalidArgumentException("pageSize");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getGroups(filter, sortDirection, pageIndex, pageSize);
  }

  @Override
  public List<Group> getGroupsForUser(UUID userDirectoryId, String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (!StringUtils.hasText(username)) {
      throw new InvalidArgumentException("username");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getGroupsForUser(username);
  }

  @Override
  public List<GroupMember> getMembersForGroup(UUID userDirectoryId, String groupName)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (!StringUtils.hasText(groupName)) {
      throw new InvalidArgumentException("groupName");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getMembersForGroup(groupName);
  }

  @Override
  @Transactional
  public GroupMembers getMembersForGroup(
      UUID userDirectoryId,
      String groupName,
      String filter,
      SortDirection sortDirection,
      Integer pageIndex,
      Integer pageSize)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (!StringUtils.hasText(groupName)) {
      throw new InvalidArgumentException("groupName");
    }

    if ((pageIndex != null) && (pageIndex < 0)) {
      throw new InvalidArgumentException("pageIndex");
    }

    if ((pageSize != null) && (pageSize <= 0)) {
      throw new InvalidArgumentException("pageSize");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getMembersForGroup(groupName, filter, sortDirection, pageIndex, pageSize);
  }

  @Override
  public List<String> getRoleCodesForGroup(UUID userDirectoryId, String groupName)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (!StringUtils.hasText(groupName)) {
      throw new InvalidArgumentException("groupName");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getRoleCodesForGroup(groupName);
  }

  @Override
  public List<String> getRoleCodesForUser(UUID userDirectoryId, String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (!StringUtils.hasText(username)) {
      throw new InvalidArgumentException("username");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getRoleCodesForUser(username);
  }

  @Override
  public List<Role> getRoles() throws ServiceUnavailableException {
    try {
      return roleRepository.findAll();
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to retrieve the roles", e);
    }
  }

  @Override
  public List<GroupRole> getRolesForGroup(UUID userDirectoryId, String groupName)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (!StringUtils.hasText(groupName)) {
      throw new InvalidArgumentException("groupName");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getRolesForGroup(groupName);
  }

  @Override
  public Tenant getTenant(UUID tenantId)
      throws InvalidArgumentException, TenantNotFoundException, ServiceUnavailableException {
    if (tenantId == null) {
      throw new InvalidArgumentException("tenantId");
    }

    try {
      Optional<Tenant> tenantOptional = tenantRepository.findById(tenantId);

      if (tenantOptional.isPresent()) {
        return tenantOptional.get();
      } else {
        throw new TenantNotFoundException(tenantId);
      }
    } catch (TenantNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to retrieve the tenant (" + tenantId + ")", e);
    }
  }

  @Override
  public List<UUID> getTenantIdsForUserDirectory(UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    try {
      if (!userDirectoryRepository.existsById(userDirectoryId)) {
        throw new UserDirectoryNotFoundException(userDirectoryId);
      }

      return userDirectoryRepository.getTenantIdsById(userDirectoryId);
    } catch (UserDirectoryNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the IDs for the tenants for the user directory ("
              + userDirectoryId
              + ")",
          e);
    }
  }

  @Override
  public String getTenantName(UUID tenantId)
      throws InvalidArgumentException, TenantNotFoundException, ServiceUnavailableException {
    if (tenantId == null) {
      throw new InvalidArgumentException("tenantId");
    }

    try {
      Optional<String> nameOptional = tenantRepository.getNameById(tenantId);

      if (nameOptional.isPresent()) {
        return nameOptional.get();
      } else {
        throw new TenantNotFoundException(tenantId);
      }
    } catch (TenantNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the name of the tenant (" + tenantId + ")", e);
    }
  }

  @Override
  public List<Tenant> getTenants() throws ServiceUnavailableException {
    try {
      return tenantRepository.findAll();
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to retrieve the tenants", e);
    }
  }

  @Override
  public Tenants getTenants(
      String filter, SortDirection sortDirection, Integer pageIndex, Integer pageSize)
      throws InvalidArgumentException, ServiceUnavailableException {
    if ((pageIndex != null) && (pageIndex < 0)) {
      throw new InvalidArgumentException("pageIndex");
    }

    if ((pageSize != null) && (pageSize <= 0)) {
      throw new InvalidArgumentException("pageSize");
    }

    if (pageIndex == null) {
      pageIndex = 0;
    }

    if (pageSize == null) {
      pageSize = MAX_FILTERED_ORGANISATIONS;
    }

    PageRequest pageRequest =
        PageRequest.of(pageIndex, Math.min(pageSize, MAX_FILTERED_ORGANISATIONS));

    try {
      Page<Tenant> tenantPage;

      if (StringUtils.hasText(filter)) {
        if (sortDirection == SortDirection.ASCENDING) {
          tenantPage =
              tenantRepository.findByNameContainingIgnoreCaseOrderByNameAsc(filter, pageRequest);
        } else {
          tenantPage =
              tenantRepository.findByNameContainingIgnoreCaseOrderByNameDesc(filter, pageRequest);
        }
      } else {
        if (sortDirection == SortDirection.ASCENDING) {
          tenantPage = tenantRepository.findAllByOrderByNameAsc(pageRequest);
        } else {
          tenantPage = tenantRepository.findAllByOrderByNameDesc(pageRequest);
        }
      }

      return new Tenants(
          tenantPage.toList(),
          tenantPage.getTotalElements(),
          filter,
          sortDirection,
          pageIndex,
          pageSize);
    } catch (Throwable e) {
      String message = "Failed to retrieve the tenants";

      if (StringUtils.hasText(filter)) {
        message += String.format(" matching the filter \"%s\"", filter);
      }

      message += " for the page " + pageIndex + " using the page size " + pageSize;

      message += ": ";

      message += e.getMessage();

      throw new ServiceUnavailableException(message, e);
    }
  }

  @Override
  public List<Tenant> getTenantsForUserDirectory(UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    try {
      if (!userDirectoryRepository.existsById(userDirectoryId)) {
        throw new UserDirectoryNotFoundException(userDirectoryId);
      }

      return tenantRepository.findAllByUserDirectoryId(userDirectoryId);
    } catch (UserDirectoryNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the tenants associated with the user directory ("
              + userDirectoryId
              + ")",
          e);
    }
  }

  @Override
  public User getUser(UUID userDirectoryId, String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (!StringUtils.hasText(username)) {
      throw new InvalidArgumentException("username");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getUser(username);
  }

  @Override
  public List<UserDirectory> getUserDirectories() throws ServiceUnavailableException {
    try {
      return userDirectoryRepository.findAll();
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to retrieve the user directories", e);
    }
  }

  @Override
  public UserDirectories getUserDirectories(
      String filter, SortDirection sortDirection, Integer pageIndex, Integer pageSize)
      throws InvalidArgumentException, ServiceUnavailableException {
    if ((pageIndex != null) && (pageIndex < 0)) {
      throw new InvalidArgumentException("pageIndex");
    }

    if ((pageSize != null) && (pageSize <= 0)) {
      throw new InvalidArgumentException("pageSize");
    }

    if (pageIndex == null) {
      pageIndex = 0;
    }

    if (pageSize == null) {
      pageSize = MAX_FILTERED_USER_DIRECTORIES;
    }

    PageRequest pageRequest =
        PageRequest.of(pageIndex, Math.min(pageSize, MAX_FILTERED_USER_DIRECTORIES));

    try {
      Page<UserDirectory> userDirectoryPage;

      if (StringUtils.hasText(filter)) {
        if (sortDirection == SortDirection.ASCENDING) {
          userDirectoryPage =
              userDirectoryRepository.findByNameContainingIgnoreCaseOrderByNameAsc(
                  filter, pageRequest);
        } else {
          userDirectoryPage =
              userDirectoryRepository.findByNameContainingIgnoreCaseOrderByNameDesc(
                  filter, pageRequest);
        }
      } else {
        if (sortDirection == SortDirection.ASCENDING) {
          userDirectoryPage = userDirectoryRepository.findAllByOrderByNameAsc(pageRequest);
        } else {
          userDirectoryPage = userDirectoryRepository.findAllByOrderByNameDesc(pageRequest);
        }
      }

      return new UserDirectories(
          userDirectoryPage.toList(),
          userDirectoryPage.getTotalElements(),
          filter,
          sortDirection,
          pageIndex,
          pageSize);
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to retrieve the filtered user directories", e);
    }
  }

  @Override
  public List<UserDirectory> getUserDirectoriesForTenant(UUID tenantId)
      throws InvalidArgumentException, TenantNotFoundException, ServiceUnavailableException {
    if (tenantId == null) {
      throw new InvalidArgumentException("tenantId");
    }

    try {
      if (!tenantRepository.existsById(tenantId)) {
        throw new TenantNotFoundException(tenantId);
      }

      return userDirectoryRepository.findAllByTenantId(tenantId);
    } catch (TenantNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the user directories associated with the tenant (" + tenantId + ")",
          e);
    }
  }

  @Override
  public UserDirectory getUserDirectory(UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    try {
      Optional<UserDirectory> userDirectoryOptional =
          userDirectoryRepository.findById(userDirectoryId);

      if (userDirectoryOptional.isPresent()) {
        return userDirectoryOptional.get();
      } else {
        throw new UserDirectoryNotFoundException(userDirectoryId);
      }
    } catch (UserDirectoryNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the user directory (" + userDirectoryId + ")", e);
    }
  }

  @Override
  public UserDirectoryCapabilities getUserDirectoryCapabilities(UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getCapabilities();
  }

  @Override
  public Optional<UUID> getUserDirectoryIdForUser(String username)
      throws InvalidArgumentException, ServiceUnavailableException {
    if (!StringUtils.hasText(username)) {
      throw new InvalidArgumentException("username");
    }

    try {
      // First check if this is an internal user and if so determine the user directory ID
      Optional<UUID> internalUserDirectoryIdOptional = getInternalUserDirectoryIdForUser(username);

      if (internalUserDirectoryIdOptional.isPresent()) {
        return internalUserDirectoryIdOptional;
      } else {
        /*
         * Check all of the "external" user directories to see if the user is associated with one
         * of them.
         */
        for (UUID userDirectoryId : userDirectories.keySet()) {
          IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

          if (userDirectory != null) {
            if (!(userDirectory instanceof InternalUserDirectory)) {
              if (userDirectory.isExistingUser(username)) {
                return Optional.of(userDirectoryId);
              }
            }
          }
        }

        return Optional.empty();
      }
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the user directory ID for the user (" + username + ")", e);
    }
  }

  @Override
  public List<UUID> getUserDirectoryIdsForTenant(UUID tenantId)
      throws InvalidArgumentException, TenantNotFoundException, ServiceUnavailableException {
    if (tenantId == null) {
      throw new InvalidArgumentException("tenantId");
    }

    try {
      if (!tenantRepository.existsById(tenantId)) {
        throw new TenantNotFoundException(tenantId);
      }

      return tenantRepository.getUserDirectoryIdsById(tenantId);
    } catch (TenantNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the IDs for the user directories associated with the tenant ("
              + tenantId
              + ")",
          e);
    }
  }

  @Override
  public List<UUID> getUserDirectoryIdsForUser(String username)
      throws InvalidArgumentException, UserNotFoundException, ServiceUnavailableException {
    if (!StringUtils.hasText(username)) {
      throw new InvalidArgumentException("username");
    }

    try {
      List<UUID> userDirectoryIdsForUser = new ArrayList<>();

      Optional<UUID> userDirectoryIdOptional = getUserDirectoryIdForUser(username);

      if (userDirectoryIdOptional.isEmpty()) {
        throw new UserNotFoundException(username);
      }

      /*
       * Retrieve the list of IDs for the tenants the user is associated with as a result
       * of their user directory being associated with these tenants.
       */
      List<UUID> tenantIds = getTenantIdsForUserDirectory(userDirectoryIdOptional.get());

      /*
       * Retrieve the list of IDs for the user directories the user is associated with as a result
       * of being associated with one or more tenants.
       */
      for (var tenantId : tenantIds) {
        // Retrieve the list of user directories associated with the tenant
        var userDirectoryIdsForTenant = getUserDirectoryIdsForTenant(tenantId);

        userDirectoryIdsForUser.addAll(userDirectoryIdsForTenant);
      }

      return userDirectoryIdsForUser;
    } catch (UserNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the IDs for the user directories the user ("
              + username
              + ") is associated with",
          e);
    }
  }

  @Override
  public String getUserDirectoryName(UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    try {
      Optional<String> nameOptional = userDirectoryRepository.getNameById(userDirectoryId);

      if (nameOptional.isPresent()) {
        return nameOptional.get();
      } else {
        throw new UserDirectoryNotFoundException(userDirectoryId);
      }
    } catch (UserDirectoryNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the name of the user directory (" + userDirectoryId + ")", e);
    }
  }

  @Override
  public UserDirectorySummaries getUserDirectorySummaries(
      String filter, SortDirection sortDirection, Integer pageIndex, Integer pageSize)
      throws InvalidArgumentException, ServiceUnavailableException {
    if ((pageIndex != null) && (pageIndex < 0)) {
      throw new InvalidArgumentException("pageIndex");
    }

    if ((pageSize != null) && (pageSize <= 0)) {
      throw new InvalidArgumentException("pageSize");
    }

    if (pageIndex == null) {
      pageIndex = 0;
    }

    if (pageSize == null) {
      pageSize = MAX_FILTERED_USER_DIRECTORIES;
    }

    PageRequest pageRequest =
        PageRequest.of(pageIndex, Math.min(pageSize, MAX_FILTERED_USER_DIRECTORIES));

    try {
      Page<UserDirectorySummary> userDirectorySummaryPage;

      if (StringUtils.hasText(filter)) {
        if (sortDirection == SortDirection.ASCENDING) {
          userDirectorySummaryPage =
              userDirectorySummaryRepository.findByNameContainingIgnoreCaseOrderByNameAsc(
                  filter, pageRequest);
        } else {
          userDirectorySummaryPage =
              userDirectorySummaryRepository.findByNameContainingIgnoreCaseOrderByNameDesc(
                  filter, pageRequest);
        }
      } else {
        if (sortDirection == SortDirection.ASCENDING) {
          userDirectorySummaryPage =
              userDirectorySummaryRepository.findAllByOrderByNameAsc(pageRequest);
        } else {
          userDirectorySummaryPage =
              userDirectorySummaryRepository.findAllByOrderByNameDesc(pageRequest);
        }
      }

      return new UserDirectorySummaries(
          userDirectorySummaryPage.toList(),
          userDirectorySummaryPage.getTotalElements(),
          filter,
          sortDirection,
          pageIndex,
          pageSize);
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the filtered summaries for the user directories", e);
    }
  }

  @Override
  public List<UserDirectorySummary> getUserDirectorySummariesForTenant(UUID tenantId)
      throws InvalidArgumentException, TenantNotFoundException, ServiceUnavailableException {
    if (tenantId == null) {
      throw new InvalidArgumentException("tenantId");
    }

    try {
      if (!tenantRepository.existsById(tenantId)) {
        throw new TenantNotFoundException(tenantId);
      }

      return userDirectorySummaryRepository.findAllByTenantId(tenantId);
    } catch (TenantNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the summaries for the user "
              + "directories associated with the tenant ("
              + tenantId
              + ")",
          e);
    }
  }

  @Override
  public UserDirectoryType getUserDirectoryTypeForUserDirectory(UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException,
          UserDirectoryTypeNotFoundException, ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    try {
      Optional<String> typeOptional =
          userDirectoryRepository.getTypeForUserDirectoryById(userDirectoryId);

      if (typeOptional.isEmpty()) {
        throw new UserDirectoryNotFoundException(userDirectoryId);
      }

      Optional<UserDirectoryType> userDirectoryTypeOptional =
          userDirectoryTypeRepository.findById(typeOptional.get());

      if (userDirectoryTypeOptional.isPresent()) {
        return userDirectoryTypeOptional.get();
      } else {
        throw new UserDirectoryTypeNotFoundException(typeOptional.get());
      }
    } catch (UserDirectoryNotFoundException | UserDirectoryTypeNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the user directory type for the user directory ("
              + userDirectoryId
              + ")",
          e);
    }
  }

  @Override
  public List<UserDirectoryType> getUserDirectoryTypes() throws ServiceUnavailableException {
    try {
      return userDirectoryTypeRepository.findAll();
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to retrieve the user directory types", e);
    }
  }

  @Override
  public String getUserName(UUID userDirectoryId, String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (!StringUtils.hasText(username)) {
      throw new InvalidArgumentException("username");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getUserName(username);
  }

  @Override
  public List<User> getUsers(UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getUsers();
  }

  @Override
  public Users getUsers(
      UUID userDirectoryId,
      String filter,
      UserSortBy sortBy,
      SortDirection sortDirection,
      Integer pageIndex,
      Integer pageSize)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (sortBy == null) {
      sortBy = UserSortBy.NAME;
    }

    if (sortDirection == null) {
      sortDirection = SortDirection.ASCENDING;
    }

    if ((pageIndex != null) && (pageIndex < 0)) {
      throw new InvalidArgumentException("pageIndex");
    }

    if ((pageSize != null) && (pageSize <= 0)) {
      throw new InvalidArgumentException("pageSize");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.getUsers(filter, sortBy, sortDirection, pageIndex, pageSize);
  }

  @Override
  @Transactional
  public void initiatePasswordReset(String username, String resetPasswordUrl, boolean sendEmail)
      throws InvalidArgumentException, UserNotFoundException, ServiceUnavailableException {
    initiatePasswordReset(username, resetPasswordUrl, sendEmail, null);
  }

  @Override
  @Transactional
  public void initiatePasswordReset(
      String username, String resetPasswordUrl, boolean sendEmail, String securityCode)
      throws InvalidArgumentException, UserNotFoundException, ServiceUnavailableException {
    if (!StringUtils.hasText(username)) {
      throw new InvalidArgumentException("username");
    }

    if (!StringUtils.hasText(resetPasswordUrl)) {
      throw new InvalidArgumentException("resetPasswordUrl");
    }

    try {
      Optional<UUID> userDirectoryIdOptional = getUserDirectoryIdForUser(username);

      if (userDirectoryIdOptional.isEmpty()) {
        throw new UserNotFoundException(username);
      }

      IUserDirectory userDirectory = userDirectories.get(userDirectoryIdOptional.get());

      User user = userDirectory.getUser(username);

      if (StringUtils.hasText(user.getEmail())) {
        if (!StringUtils.hasText(securityCode)) {
          securityCode = securityCodeGenerator.nextString();
        }

        String securityCodeHash = PasswordUtil.createPasswordHash(securityCode);

        PasswordReset passwordReset = new PasswordReset(username, securityCodeHash);

        if (sendEmail) {
          sendPasswordResetEmail(user, resetPasswordUrl, securityCode);
        }

        passwordResetRepository.saveAndFlush(passwordReset);
      } else {
        logger.warn(
            "Failed to send the password reset communication to the user ("
                + username
                + ") who does not have a valid e-mail address");
      }
    } catch (UserNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to initiate the password reset process for the user (" + username + ")", e);
    }
  }

  @Override
  public boolean isExistingUser(UUID userDirectoryId, String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (!StringUtils.hasText(username)) {
      throw new InvalidArgumentException("username");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.isExistingUser(username);
  }

  @Override
  public boolean isUserInGroup(UUID userDirectoryId, String groupName, String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          GroupNotFoundException, ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (!StringUtils.hasText(groupName)) {
      throw new InvalidArgumentException("groupName");
    }

    if (!StringUtils.hasText(username)) {
      throw new InvalidArgumentException("username");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    return userDirectory.isUserInGroup(groupName, username);
  }

  @Override
  public void reloadUserDirectories() throws ServiceUnavailableException {
    try {
      Map<UUID, IUserDirectory> reloadedUserDirectories = new ConcurrentHashMap<>();

      List<UserDirectoryType> userDirectoryTypes = getUserDirectoryTypes();

      for (UserDirectory userDirectory : getUserDirectories()) {
        UserDirectoryType userDirectoryType;

        userDirectoryType =
            userDirectoryTypes.stream()
                .filter(
                    possibleUserDirectoryType ->
                        possibleUserDirectoryType.getCode().equals(userDirectory.getType()))
                .findFirst()
                .orElse(null);

        if (userDirectoryType == null) {
          logger.error(
              "Failed to load the user directory ("
                  + userDirectory.getId()
                  + "): The user directory type ("
                  + userDirectory.getType()
                  + ") was not loaded");

          continue;
        }

        try {
          Class<?> clazz =
              Thread.currentThread()
                  .getContextClassLoader()
                  .loadClass(userDirectoryType.getUserDirectoryClassName());

          if (!IUserDirectory.class.isAssignableFrom(clazz)) {
            throw new ServiceUnavailableException(
                "The user directory class ("
                    + userDirectoryType.getUserDirectoryClassName()
                    + ") does not implement the IUserDirectory interface");
          }

          Class<? extends IUserDirectory> userDirectoryClass =
              clazz.asSubclass(IUserDirectory.class);

          Constructor<? extends IUserDirectory> userDirectoryClassConstructor;

          try {
            userDirectoryClassConstructor =
                userDirectoryClass.getConstructor(
                    UUID.class,
                    List.class,
                    GroupRepository.class,
                    UserRepository.class,
                    RoleRepository.class);
          } catch (NoSuchMethodException e) {
            throw new ServiceUnavailableException(
                "The user directory class ("
                    + userDirectoryType.getUserDirectoryClassName()
                    + ") does not provide a valid constructor (long, Map<String,String>)");
          }

          IUserDirectory userDirectoryInstance =
              userDirectoryClassConstructor.newInstance(
                  userDirectory.getId(),
                  userDirectory.getParameters(),
                  groupRepository,
                  userRepository,
                  roleRepository);

          applicationContext.getAutowireCapableBeanFactory().autowireBean(userDirectoryInstance);

          reloadedUserDirectories.put(userDirectory.getId(), userDirectoryInstance);
        } catch (Throwable e) {
          throw new ServiceUnavailableException(
              "Failed to initialize the user directory ("
                  + userDirectory.getId()
                  + ")("
                  + userDirectory.getName()
                  + ")",
              e);
        }
      }

      this.userDirectories = reloadedUserDirectories;
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to reload the user directories", e);
    }
  }

  @Override
  @Transactional
  public void removeMemberFromGroup(
      UUID userDirectoryId, String groupName, GroupMemberType memberType, String memberName)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          GroupMemberNotFoundException, ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (!StringUtils.hasText(groupName)) {
      throw new InvalidArgumentException("groupName");
    }

    if (memberType == null) {
      throw new InvalidArgumentException("memberType");
    }

    if (!StringUtils.hasText(memberName)) {
      throw new InvalidArgumentException("memberName");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    userDirectory.removeMemberFromGroup(groupName, memberType, memberName);
  }

  @Override
  @Transactional
  public void removeRoleFromGroup(UUID userDirectoryId, String groupName, String roleCode)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          GroupRoleNotFoundException, ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (!StringUtils.hasText(groupName)) {
      throw new InvalidArgumentException("groupName");
    }

    if (!StringUtils.hasText(roleCode)) {
      throw new InvalidArgumentException("roleCode");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    userDirectory.removeRoleFromGroup(groupName, roleCode);
  }

  @Override
  @Transactional
  public void removeUserDirectoryFromTenant(UUID tenantId, UUID userDirectoryId)
      throws InvalidArgumentException, TenantNotFoundException,
          TenantUserDirectoryNotFoundException, ServiceUnavailableException {
    if (tenantId == null) {
      throw new InvalidArgumentException("tenantId");
    }

    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    try {
      if (!tenantRepository.existsById(tenantId)) {
        throw new TenantNotFoundException(tenantId);
      }

      if (tenantRepository.countTenantUserDirectory(tenantId, userDirectoryId) == 0) {
        throw new TenantUserDirectoryNotFoundException(tenantId, userDirectoryId);
      }

      tenantRepository.removeUserDirectoryFromTenant(tenantId, userDirectoryId);
    } catch (TenantNotFoundException | TenantUserDirectoryNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to add the user directory ("
              + userDirectoryId
              + ") to the tenant ("
              + tenantId
              + ")",
          e);
    }
  }

  @Override
  @Transactional
  public void removeUserFromGroup(UUID userDirectoryId, String groupName, String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          UserNotFoundException, ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (!StringUtils.hasText(groupName)) {
      throw new InvalidArgumentException("groupName");
    }

    if (!StringUtils.hasText(username)) {
      throw new InvalidArgumentException("username");
    }

    IUserDirectory userDirectory = userDirectories.get(userDirectoryId);

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(userDirectoryId);
    }

    userDirectory.removeUserFromGroup(groupName, username);
  }

  @Override
  @Transactional
  public void resetPassword(String username, String newPassword, String securityCode)
      throws InvalidArgumentException, InvalidSecurityCodeException, UserLockedException,
          ExistingPasswordException, ServiceUnavailableException {
    if (!StringUtils.hasText(username)) {
      throw new InvalidArgumentException("username");
    }

    if (!StringUtils.hasText(newPassword)) {
      throw new InvalidArgumentException("newPassword");
    }

    if (!StringUtils.hasText(securityCode)) {
      throw new InvalidArgumentException("securityCode");
    }

    try {
      Optional<UUID> userDirectoryIdOptional = getUserDirectoryIdForUser(username);

      if (userDirectoryIdOptional.isEmpty()) {
        throw new InvalidSecurityCodeException(username);
      }

      List<PasswordReset> passwordResets =
          passwordResetRepository.findAllByUsernameAndStatus(
              username, PasswordResetStatus.REQUESTED);

      String securityCodeHash = PasswordUtil.createPasswordHash(securityCode);

      for (PasswordReset passwordReset : passwordResets) {
        if (passwordReset.getSecurityCodeHash().equals(securityCodeHash)) {
          IUserDirectory userDirectory = userDirectories.get(userDirectoryIdOptional.get());

          userDirectory.resetPassword(username, newPassword);

          return;
        }
      }

      throw new InvalidSecurityCodeException(username);
    } catch (UserLockedException | InvalidSecurityCodeException | ExistingPasswordException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to reset the password for the user (" + username + ")", e);
    }
  }

  @Override
  @Transactional
  public void updateFunction(Function function)
      throws InvalidArgumentException, FunctionNotFoundException, ServiceUnavailableException {
    validateFunction(function);

    try {
      if (!functionRepository.existsById(function.getCode())) {
        throw new FunctionNotFoundException(function.getCode());
      }

      functionRepository.saveAndFlush(function);
    } catch (FunctionNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to update the function (" + function.getCode() + ")", e);
    }
  }

  @Override
  @Transactional
  public void updateGroup(Group group)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          ServiceUnavailableException {
    validateGroup(group);

    IUserDirectory userDirectory = userDirectories.get(group.getUserDirectoryId());

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(group.getUserDirectoryId());
    }

    userDirectory.updateGroup(group);
  }

  @Override
  @Transactional
  public void updateTenant(Tenant tenant)
      throws InvalidArgumentException, TenantNotFoundException, ServiceUnavailableException {
    validateTenant(tenant);

    try {
      Optional<Tenant> tenantOptional = tenantRepository.findById(tenant.getId());

      if (tenantOptional.isPresent()) {
        Tenant existingTenant = tenantOptional.get();

        existingTenant.setName(tenant.getName());
        existingTenant.setStatus(tenant.getStatus());

        tenantRepository.saveAndFlush(existingTenant);
      } else {
        throw new TenantNotFoundException(tenant.getId());
      }
    } catch (TenantNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to update the tenant (" + tenant.getId() + ")", e);
    }
  }

  @Override
  @Transactional
  public void updateUser(User user, boolean expirePassword, boolean lockUser)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          ServiceUnavailableException {
    validateUser(user);

    IUserDirectory userDirectory = userDirectories.get(user.getUserDirectoryId());

    if (userDirectory == null) {
      throw new UserDirectoryNotFoundException(user.getUserDirectoryId());
    }

    userDirectory.updateUser(user, expirePassword, lockUser);
  }

  @Override
  @Transactional
  public void updateUserDirectory(UserDirectory userDirectory)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException {
    validateUserDirectory(userDirectory);

    try {
      if (!userDirectoryRepository.existsById(userDirectory.getId())) {
        throw new UserDirectoryNotFoundException(userDirectory.getId());
      }

      userDirectoryRepository.saveAndFlush(userDirectory);

      reloadUserDirectories();
    } catch (UserDirectoryNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to update the user directory (" + userDirectory.getName() + ")", e);
    }
  }

  /**
   * Returns the Universally Unique Identifier (UUID) for the internal user directory the internal
   * user with the specified username is associated with.
   *
   * @param username the username for the internal user
   * @return an Optional containing the Universally Unique Identifier (UUID) for the internal user
   *     directory the internal user with the specified username is associated with or an empty
   *     Optional if an internal user with the specified username could not be found
   */
  private Optional<UUID> getInternalUserDirectoryIdForUser(String username)
      throws ServiceUnavailableException {
    try {
      return userRepository.getUserDirectoryIdByUsernameIgnoreCase(username);
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the ID for the internal user directory for the internal user ("
              + username
              + ")",
          e);
    }
  }

  /**
   * Checks whether the specified value is <b>null</b> or blank.
   *
   * @param value the value to check
   * @return true if the value is <b>null</b> or blank
   */
  private boolean isNullOrEmpty(Object value) {
    if (value == null) {
      return true;
    }

    if (value instanceof String) {
      return ((String) value).length() == 0;
    }

    return false;
  }

  private UserDirectory newInternalUserDirectoryForTenant(Tenant tenant)
      throws ServiceUnavailableException {
    UserDirectory userDirectory = new UserDirectory();

    if (tenant.getId() != null) {
      userDirectory.setId(tenant.getId());
    }

    userDirectory.setType("InternalUserDirectory");
    userDirectory.setName(tenant.getName() + " Internal User Directory");

    String buffer =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE userDirectory "
            + "SYSTEM \"UserDirectoryConfiguration.dtd\"><userDirectory>"
            + "<parameter><name>MaxPasswordAttempts</name><value>5</value></parameter>"
            + "<parameter><name>PasswordExpiryMonths</name><value>12</value></parameter>"
            + "<parameter><name>PasswordHistoryMonths</name><value>24</value></parameter>"
            + "<parameter><name>MaxFilteredUsers</name><value>100</value></parameter>"
            + "</userDirectory>";

    try {
      userDirectory.setConfiguration(buffer);
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to set the configuration for the user directory", e);
    }

    return userDirectory;
  }

  private void sendPasswordResetEmail(User user, String resetPasswordUrl, String securityCode)
      throws ServiceUnavailableException {
    try {
      if (StringUtils.hasText(user.getEmail())) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("preferredName", user.getPreferredName().toUpperCase());
        parameters.put("securityCode", securityCode);
        parameters.put(
            "resetPasswordUrl",
            resetPasswordUrl
                + "?username="
                + URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8)
                + "&securityCode="
                + URLEncoder.encode(securityCode, StandardCharsets.UTF_8));

        mailService.sendMail(
            Collections.singletonList(user.getEmail()),
            "Password Reset",
            "no-reply@inception.digital",
            "Inception",
            PASSWORD_RESET_MAIL_TEMPLATE_ID,
            parameters);
      }
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to send the password reset e-mail", e);
    }
  }

  private void validateFunction(Function function) throws InvalidArgumentException {
    if (function == null) {
      throw new InvalidArgumentException("function");
    }

    Set<ConstraintViolation<Function>> constraintViolations = validator.validate(function);

    if (!constraintViolations.isEmpty()) {
      throw new InvalidArgumentException(
          "function", ValidationError.toValidationErrors(constraintViolations));
    }
  }

  private void validateGroup(Group group) throws InvalidArgumentException {
    if (group == null) {
      throw new InvalidArgumentException("group");
    }

    Set<ConstraintViolation<Group>> constraintViolations = validator.validate(group);

    if (!constraintViolations.isEmpty()) {
      throw new InvalidArgumentException(
          "group", ValidationError.toValidationErrors(constraintViolations));
    }
  }

  private void validateTenant(Tenant tenant) throws InvalidArgumentException {
    if (tenant == null) {
      throw new InvalidArgumentException("tenant");
    }

    Set<ConstraintViolation<Tenant>> constraintViolations = validator.validate(tenant);

    if (!constraintViolations.isEmpty()) {
      throw new InvalidArgumentException(
          "tenant", ValidationError.toValidationErrors(constraintViolations));
    }
  }

  private void validateUser(User user) throws InvalidArgumentException {
    if (user == null) {
      throw new InvalidArgumentException("user");
    }

    Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);

    if (!constraintViolations.isEmpty()) {
      throw new InvalidArgumentException(
          "user", ValidationError.toValidationErrors(constraintViolations));
    }
  }

  private void validateUserDirectory(UserDirectory userDirectory) throws InvalidArgumentException {
    if (userDirectory == null) {
      throw new InvalidArgumentException("userDirectory");
    }

    Set<ConstraintViolation<UserDirectory>> constraintViolations =
        validator.validate(userDirectory);

    if (!constraintViolations.isEmpty()) {
      throw new InvalidArgumentException(
          "userDirectory", ValidationError.toValidationErrors(constraintViolations));
    }
  }
}
