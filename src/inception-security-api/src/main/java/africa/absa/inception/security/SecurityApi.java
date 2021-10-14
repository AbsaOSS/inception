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

import africa.absa.inception.api.ApiUtil;
import africa.absa.inception.api.ProblemDetails;
import africa.absa.inception.api.SecureApi;
import africa.absa.inception.core.service.InvalidArgumentException;
import africa.absa.inception.core.service.ServiceUnavailableException;
import africa.absa.inception.core.service.ValidationError;
import africa.absa.inception.core.sorting.SortDirection;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * The <b>SecurityApi</b> class.
 *
 * @author Marcus Portmann
 */
@Tag(name = "Security")
@RestController
@RequestMapping(value = "/api/security")
@CrossOrigin
@SuppressWarnings({"unused", "WeakerAccess"})
// @el (isSecurityDisabled: africa.absa.inception.api.ApiSecurityExpressionRoot.isSecurityEnabled)
public class SecurityApi extends SecureApi {

  /* Logger */
  private static final Logger logger = LoggerFactory.getLogger(SecurityApi.class);

  /** The Security Service. */
  private final ISecurityService securityService;

  /** The JSR-303 validator. */
  private final Validator validator;

  /**
   * Constructs a new <b>SecurityRestController</b>.
   *
   * @param applicationContext the Spring application context
   * @param validator the JSR-303 validator
   * @param securityService the Security Service
   */
  public SecurityApi(
      ApplicationContext applicationContext,
      Validator validator,
      ISecurityService securityService) {
    super(applicationContext);

    this.validator = validator;
    this.securityService = securityService;
  }

  /**
   * Add the group member to the group.
   *
   * @param userDirectoryId the ID for the user directory
   * @param groupName the name of the group
   * @param groupMember the group member
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws GroupNotFoundException if the group could not be found
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the group member could not be added to the group
   */
  @Operation(
      summary = "Add the group member to the group",
      description = "Add the group member to the group")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "The group member was successfully added to the group"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory or group or user could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "409",
            description = "The group member already exists",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}/groups/{groupName}/members",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration') or hasAuthority('FUNCTION_Security.GroupAdministration')")
  public void addMemberToGroup(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId,
      @Parameter(name = "groupName", description = "The name of the group", required = true)
          @PathVariable
          String groupName,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The group member association to add",
              required = true)
          @RequestBody
          GroupMember groupMember)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          UserNotFoundException, ServiceUnavailableException {
    if (!hasAccessToUserDirectory(userDirectoryId)) {
      throw new AccessDeniedException(
          "Access denied to the user directory (" + userDirectoryId + ")");
    }

    if (!StringUtils.hasText(groupName)) {
      throw new InvalidArgumentException("groupName");
    }

    if (groupMember == null) {
      throw new InvalidArgumentException("groupMember");
    }

    if (!userDirectoryId.equals(groupMember.getUserDirectoryId())) {
      throw new InvalidArgumentException("groupMember");
    }

    if (!groupName.equals(groupMember.getGroupName())) {
      throw new InvalidArgumentException("groupMember");
    }

    Set<ConstraintViolation<GroupMember>> constraintViolations = validator.validate(groupMember);

    if (!constraintViolations.isEmpty()) {
      throw new InvalidArgumentException(
          "groupMember", ValidationError.toValidationErrors(constraintViolations));
    }

    securityService.addMemberToGroup(
        userDirectoryId, groupName, groupMember.getMemberType(), groupMember.getMemberName());
  }

  /**
   * Add the role to the group.
   *
   * @param userDirectoryId the ID for the user directory
   * @param groupName the name of the group
   * @param groupRole the group role
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws GroupNotFoundException if the group could not be found
   * @throws RoleNotFoundException if the role could not be found
   * @throws ServiceUnavailableException if the role could not be added to the group
   */
  @Operation(summary = "Add the role to the group", description = "Add the role to the group")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "The role was successfully added to the group"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory or group or role could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "409",
            description = "The group role already exists",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}/groups/{groupName}/roles",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration') or hasAuthority('FUNCTION_Security.GroupAdministration')")
  public void addRoleToGroup(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId,
      @Parameter(name = "groupName", description = "The name of the group", required = true)
          @PathVariable
          String groupName,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The group role association to add",
              required = true)
          @RequestBody
          GroupRole groupRole)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          RoleNotFoundException, ServiceUnavailableException {
    if (!hasAccessToUserDirectory(userDirectoryId)) {
      throw new AccessDeniedException(
          "Access denied to the user directory (" + userDirectoryId + ")");
    }

    if (!StringUtils.hasText(groupName)) {
      throw new InvalidArgumentException("groupName");
    }

    if (groupRole == null) {
      throw new InvalidArgumentException("groupRole");
    }

    if (!userDirectoryId.equals(groupRole.getUserDirectoryId())) {
      throw new InvalidArgumentException("groupRole");
    }

    if (!groupName.equals(groupRole.getGroupName())) {
      throw new InvalidArgumentException("groupRole");
    }

    Set<ConstraintViolation<GroupRole>> constraintViolations = validator.validate(groupRole);

    if (!constraintViolations.isEmpty()) {
      throw new InvalidArgumentException(
          "groupRole", ValidationError.toValidationErrors(constraintViolations));
    }

    if (groupRole.getRoleCode().equalsIgnoreCase(SecurityService.ADMINISTRATOR_ROLE_CODE)
        && (!hasRole(SecurityService.ADMINISTRATOR_ROLE_CODE))) {
      throw new AccessDeniedException(
          "Insufficient authority to add the "
              + SecurityService.ADMINISTRATOR_ROLE_CODE
              + " role to the group ("
              + groupName
              + ") for the user directory ("
              + userDirectoryId
              + ")");
    }

    securityService.addRoleToGroup(userDirectoryId, groupName, groupRole.getRoleCode());
  }

  /**
   * Add the user directory to the tenant.
   *
   * @param tenantId the ID for the tenant
   * @param tenantUserDirectory the tenant user directory
   * @throws InvalidArgumentException if an argument is invalid
   * @throws TenantNotFoundException if the tenant could not be found
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws ServiceUnavailableException if the user directory could not be added to the tenant
   */
  @Operation(
      summary = "Add the user directory to the tenant",
      description = "Add the user directory to the tenant")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "The user directory was successfully added to the tenant"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The tenant or user directory could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "409",
            description = "The tenant user directory already exists",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/tenants/{tenantId}/user-directories",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration')")
  public void addUserDirectoryToTenant(
      @Parameter(name = "tenantId", description = "The ID for the tenant", required = true)
          @PathVariable
          UUID tenantId,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The tenant user directory association to add",
              required = true)
          @RequestBody
          TenantUserDirectory tenantUserDirectory)
      throws InvalidArgumentException, TenantNotFoundException, UserDirectoryNotFoundException,
          ServiceUnavailableException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (tenantId == null) {
      throw new InvalidArgumentException("tenantId");
    }

    if (tenantUserDirectory == null) {
      throw new InvalidArgumentException("tenantUserDirectory");
    }

    if (!tenantId.equals(tenantUserDirectory.getTenantId())) {
      throw new InvalidArgumentException("tenantUserDirectory");
    }

    Set<ConstraintViolation<TenantUserDirectory>> constraintViolations =
        validator.validate(tenantUserDirectory);

    if (!constraintViolations.isEmpty()) {
      throw new InvalidArgumentException(
          "tenantUserDirectory", ValidationError.toValidationErrors(constraintViolations));
    }

    if (!tenantUserDirectory.getTenantId().equals(tenantId)) {
      throw new InvalidArgumentException("tenantId");
    }

    securityService.addUserDirectoryToTenant(tenantId, tenantUserDirectory.getUserDirectoryId());
  }

  /**
   * Administratively change the password for the user.
   *
   * @param userDirectoryId the ID for the user directory
   * @param username the username for the user
   * @param passwordChange the password change
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the password could not be administratively changed
   */
  @Operation(
      summary = "Administratively change the password for the user",
      description = "Administratively change the password for the user")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "The password for the user was changed successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory or user could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}/users/{username}/password",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration') or hasAuthority('FUNCTION_Security.UserAdministration') or hasAuthority('FUNCTION_Security.ResetUserPassword')")
  public void adminChangePassword(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId,
      @Parameter(name = "username", description = "The username for the user", required = true)
          @PathVariable
          String username,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The password change to apply",
              required = true)
          @RequestBody
          PasswordChange passwordChange)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          ServiceUnavailableException {
    if (!hasAccessToUserDirectory(userDirectoryId)) {
      throw new AccessDeniedException(
          "Access denied to the user directory (" + userDirectoryId + ")");
    }

    if (!StringUtils.hasText(username)) {
      throw new InvalidArgumentException("username");
    }

    if (passwordChange == null) {
      throw new InvalidArgumentException("passwordChange");
    }

    if (!StringUtils.hasText(passwordChange.getNewPassword())) {
      throw new InvalidArgumentException("passwordChange");
    }

    Set<ConstraintViolation<PasswordChange>> constraintViolations =
        validator.validate(passwordChange);

    if (!constraintViolations.isEmpty()) {
      throw new InvalidArgumentException(
          "passwordChange", ValidationError.toValidationErrors(constraintViolations));
    }

    securityService.adminChangePassword(
        userDirectoryId,
        username,
        passwordChange.getNewPassword(),
        passwordChange.getExpirePassword() != null && passwordChange.getExpirePassword(),
        passwordChange.getLockUser() != null && passwordChange.getLockUser(),
        passwordChange.getResetPasswordHistory() != null
            && passwordChange.getResetPasswordHistory(),
        passwordChange.getReason());
  }

  /**
   * Change the password for the user.
   *
   * @param username the username for the user
   * @param passwordChange the password change
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws UserNotFoundException if the user could not be found
   * @throws AuthenticationFailedException if the authentication failed
   * @throws InvalidSecurityCodeException if the security code is invalid
   * @throws ExistingPasswordException if the user has previously used the new password
   * @throws UserLockedException if the user is locked
   * @throws ServiceUnavailableException if the password could not be changed
   */
  @Operation(
      summary = "Change the password for the user",
      description = "Change the password for the user")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "The password for the user was changed successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication failed or invalid security code",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description =
                "The user has exceeded the maximum number of failed password attempts and has been locked",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory or user could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "409",
            description = "The new password for the user has been used recently and is not valid",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/users/{username}/password",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void changePassword(
      @Parameter(name = "username", description = "The username for the user", required = true)
          @PathVariable
          String username,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The password change to apply",
              required = true)
          @RequestBody
          PasswordChange passwordChange)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          AuthenticationFailedException, InvalidSecurityCodeException, ExistingPasswordException,
          UserLockedException, ServiceUnavailableException {
    if (!StringUtils.hasText(username)) {
      throw new InvalidArgumentException("username");
    }

    if (passwordChange == null) {
      throw new InvalidArgumentException("passwordChange");
    }

    if (!StringUtils.hasText(passwordChange.getNewPassword())) {
      throw new InvalidArgumentException("passwordChange");
    }

    Set<ConstraintViolation<PasswordChange>> constraintViolations =
        validator.validate(passwordChange);

    if (!constraintViolations.isEmpty()) {
      throw new InvalidArgumentException(
          "passwordChange", ValidationError.toValidationErrors(constraintViolations));
    }

    if (passwordChange.getReason() == PasswordChangeReason.ADMINISTRATIVE) {
      if (isSecurityDisabled()
          || hasRole(SecurityService.ADMINISTRATOR_ROLE_CODE)
          || hasAccessToFunction("Security.TenantAdministration")
          || hasAccessToFunction("Security.UserAdministration")
          || hasAccessToFunction("Security.ResetUserPassword")) {

        Optional<UUID> userDirectoryIdOptional =
            securityService.getUserDirectoryIdForUser(username);

        if (userDirectoryIdOptional.isEmpty()) {
          throw new UserNotFoundException(username);
        }

        UUID userDirectoryId = userDirectoryIdOptional.get();

        if (isSecurityEnabled() && (!hasAccessToUserDirectory(userDirectoryId))) {
          throw new AccessDeniedException(
              "Access denied to the user directory (" + userDirectoryId + ")");
        }

        securityService.adminChangePassword(
            userDirectoryId,
            username,
            passwordChange.getNewPassword(),
            passwordChange.getExpirePassword() != null && passwordChange.getExpirePassword(),
            passwordChange.getLockUser() != null && passwordChange.getLockUser(),
            passwordChange.getResetPasswordHistory() != null
                && passwordChange.getResetPasswordHistory(),
            passwordChange.getReason());
      } else {
        throw new AccessDeniedException(
            "Insufficient access to change the password for the user (" + username + ")");
      }
    } else if (passwordChange.getReason() == PasswordChangeReason.USER) {
      if (!StringUtils.hasText(passwordChange.getPassword())) {
        throw new InvalidArgumentException("passwordChange");
      }

      securityService.changePassword(
          username, passwordChange.getPassword(), passwordChange.getNewPassword());
    } else if (passwordChange.getReason() == PasswordChangeReason.RESET) {
      if (!StringUtils.hasText(passwordChange.getSecurityCode())) {
        throw new InvalidArgumentException("passwordChange");
      }

      securityService.resetPassword(
          username, passwordChange.getNewPassword(), passwordChange.getSecurityCode());
    }
  }

  /**
   * Create the new group.
   *
   * @param userDirectoryId the ID for the user directory
   * @param group the group
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws DuplicateGroupException if the group already exists
   * @throws ServiceUnavailableException if the group could not be created
   */
  @Operation(summary = "Create the group", description = "Create the group")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "The group was created successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "409",
            description = "A group with the specified name already exists",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}/groups",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration') or hasAuthority('FUNCTION_Security.GroupAdministration')")
  public void createGroup(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The group to create",
              required = true)
          @RequestBody
          Group group)
      throws InvalidArgumentException, UserDirectoryNotFoundException, DuplicateGroupException,
          ServiceUnavailableException {
    if (!hasAccessToUserDirectory(userDirectoryId)) {
      throw new AccessDeniedException(
          "Access denied to the user directory (" + userDirectoryId + ")");
    }

    if (group == null) {
      throw new InvalidArgumentException("group");
    }

    if (!userDirectoryId.equals(group.getUserDirectoryId())) {
      throw new InvalidArgumentException("group");
    }

    securityService.createGroup(group);
  }

  /**
   * Create the new tenant.
   *
   * @param tenant the tenant
   * @param createUserDirectory should a new internal user directory be created for the tenant
   * @throws InvalidArgumentException if an argument is invalid
   * @throws DuplicateTenantException if the tenant already exists
   * @throws ServiceUnavailableException if the tenant could not be created
   */
  @Operation(summary = "Create the tenant", description = "Create the tenant")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "The tenant was created successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "409",
            description = "An tenant with the specified ID or name already exists",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(value = "/tenants", method = RequestMethod.POST, produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration')")
  public void createTenant(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The tenant to create",
              required = true)
          @RequestBody
          Tenant tenant,
      @Parameter(
              name = "createUserDirectory",
              description = "Should a new internal user directory be created for the tenant")
          @RequestParam(value = "createUserDirectory", required = false)
          Boolean createUserDirectory)
      throws InvalidArgumentException, DuplicateTenantException, ServiceUnavailableException {
    securityService.createTenant(tenant, (createUserDirectory != null) && createUserDirectory);
  }

  /**
   * Create the new user.
   *
   * @param userDirectoryId the ID for the user directory
   * @param user the user
   * @param expiredPassword create the user with its password expired
   * @param userLocked create the user locked
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws DuplicateUserException if the user already exists
   * @throws ServiceUnavailableException if the user could not be created
   */
  @Operation(summary = "Create the user", description = "Create the user")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "The user was created successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "409",
            description = "A user with the specified username already exists",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}/users",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration') or hasAuthority('FUNCTION_Security.UserAdministration')")
  public void createUser(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The user to create",
              required = true)
          @RequestBody
          User user,
      @Parameter(
              name = "expiredPassword",
              description = "Create the user with its password expired")
          @RequestParam(value = "expiredPassword", required = false)
          Boolean expiredPassword,
      @Parameter(name = "userLocked", description = "Create the user locked")
          @RequestParam(value = "userLocked", required = false)
          Boolean userLocked)
      throws InvalidArgumentException, UserDirectoryNotFoundException, DuplicateUserException,
          ServiceUnavailableException {
    if (!hasAccessToUserDirectory(userDirectoryId)) {
      throw new AccessDeniedException(
          "Access denied to the user directory (" + userDirectoryId + ")");
    }

    if (user == null) {
      throw new InvalidArgumentException("user");
    }

    if (!userDirectoryId.equals(user.getUserDirectoryId())) {
      throw new InvalidArgumentException("user");
    }

    securityService.createUser(
        user, (expiredPassword != null) && expiredPassword, (userLocked != null) && userLocked);
  }

  /**
   * Create the new user directory.
   *
   * @param userDirectory the user directory
   * @throws InvalidArgumentException if an argument is invalid
   * @throws DuplicateUserDirectoryException if the user directory already exists
   * @throws ServiceUnavailableException if the user directory could not be created
   */
  @Operation(summary = "Create the user directory", description = "Create the user directory")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "The user directory was created successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "409",
            description = "A user directory with the specified ID or name already exists",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.UserDirectoryAdministration')")
  public void createUserDirectory(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The user directory to create",
              required = true)
          @RequestBody
          UserDirectory userDirectory)
      throws InvalidArgumentException, DuplicateUserDirectoryException,
          ServiceUnavailableException {
    securityService.createUserDirectory(userDirectory);
  }

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
  @Operation(summary = "Delete the group", description = "Delete the group")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "The group was deleted successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory or group could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "409",
            description =
                "The group could not be deleted since it is still associated with 1 or more user(s)",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}/groups/{groupName}",
      method = RequestMethod.DELETE,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration') or hasAuthority('FUNCTION_Security.GroupAdministration')")
  public void deleteGroup(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId,
      @Parameter(name = "groupName", description = "The name of the group", required = true)
          @PathVariable
          String groupName)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          ExistingGroupMembersException, ServiceUnavailableException {
    if (!hasAccessToUserDirectory(userDirectoryId)) {
      throw new AccessDeniedException(
          "Access denied to the user directory (" + userDirectoryId + ")");
    }

    securityService.deleteGroup(userDirectoryId, groupName);
  }

  /**
   * Delete the tenant.
   *
   * @param tenantId the ID for the tenant
   * @throws InvalidArgumentException if an argument is invalid
   * @throws TenantNotFoundException if the tenant could not be found
   * @throws ServiceUnavailableException if the tenant could not be deleted
   */
  @Operation(summary = "Delete the tenant", description = "Delete the tenant")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "The tenant was deleted successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The tenant could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/tenants/{tenantId}",
      method = RequestMethod.DELETE,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration')")
  public void deleteTenant(
      @Parameter(name = "tenantId", description = "The ID for the tenant", required = true)
          @PathVariable
          UUID tenantId)
      throws InvalidArgumentException, TenantNotFoundException, ServiceUnavailableException {
    securityService.deleteTenant(tenantId);
  }

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
  @Operation(summary = "Delete the user", description = "Delete the user")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "The user was deleted successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory or user could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}/users/{username}",
      method = RequestMethod.DELETE,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration') or hasAuthority('FUNCTION_Security.UserAdministration')")
  public void deleteUser(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId,
      @Parameter(name = "username", description = "The username for the user", required = true)
          @PathVariable
          String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          ServiceUnavailableException {
    if (!hasAccessToUserDirectory(userDirectoryId)) {
      throw new AccessDeniedException(
          "Access denied to the user directory (" + userDirectoryId + ")");
    }

    securityService.deleteUser(userDirectoryId, username);
  }

  /**
   * Delete the user directory.
   *
   * @param userDirectoryId the ID for the user directory
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws ServiceUnavailableException if the user directory could not be deleted
   */
  @Operation(summary = "Delete the user directory", description = "Delete the user directory")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "The user directory was deleted successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}",
      method = RequestMethod.DELETE,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.UserDirectoryAdministration')")
  public void deleteUserDirectory(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException {
    securityService.deleteUserDirectory(userDirectoryId);
  }

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
  @Operation(summary = "Retrieve the group", description = "Retrieve the group")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory or group could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}/groups/{groupName}",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration') or hasAuthority('FUNCTION_Security.GroupAdministration')")
  public Group getGroup(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId,
      @Parameter(name = "groupName", description = "The name of the group", required = true)
          @PathVariable
          String groupName)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          ServiceUnavailableException {
    if (!hasAccessToUserDirectory(userDirectoryId)) {
      throw new AccessDeniedException(
          "Access denied to the user directory (" + userDirectoryId + ")");
    }

    return securityService.getGroup(userDirectoryId, groupName);
  }

  /**
   * Retrieve all the group names.
   *
   * @param userDirectoryId the ID for the user directory
   * @return the group names
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws ServiceUnavailableException if the group names could not be retrieved
   */
  @Operation(summary = "Retrieve the group names", description = "Retrieve the group names")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}/group-names",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration') or hasAuthority('FUNCTION_Security.GroupAdministration')")
  public List<String> getGroupNames(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException {
    if (!hasAccessToUserDirectory(userDirectoryId)) {
      throw new AccessDeniedException(
          "Access denied to the user directory (" + userDirectoryId + ")");
    }

    return securityService.getGroupNames(userDirectoryId);
  }

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
  @Operation(
      summary = "Retrieve the names of the groups the user is a member of",
      description = "Retrieve the names of the groups the user is a member of")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory or user could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}/users/{username}/group-names",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration') or hasAuthority('FUNCTION_Security.UserAdministration')")
  public List<String> getGroupNamesForUser(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId,
      @Parameter(name = "username", description = "The username for the user", required = true)
          @PathVariable
          String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          ServiceUnavailableException {
    if (!hasAccessToUserDirectory(userDirectoryId)) {
      throw new AccessDeniedException(
          "Access denied to the user directory (" + userDirectoryId + ")");
    }

    return securityService.getGroupNamesForUser(userDirectoryId, username);
  }

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
  @Operation(summary = "Retrieve the groups", description = "Retrieve the groups")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}/groups",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration') or hasAuthority('FUNCTION_Security.GroupAdministration')")
  public Groups getGroups(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId,
      @Parameter(name = "filter", description = "The optional filter to apply to the groups")
          @RequestParam(value = "filter", required = false)
          String filter,
      @Parameter(
              name = "sortDirection",
              description = "The optional sort direction to apply to the groups")
          @RequestParam(value = "sortDirection", required = false)
          SortDirection sortDirection,
      @Parameter(name = "pageIndex", description = "The optional page index", example = "0")
          @RequestParam(value = "pageIndex", required = false, defaultValue = "0")
          Integer pageIndex,
      @Parameter(name = "pageSize", description = "The optional page size", example = "10")
          @RequestParam(value = "pageSize", required = false, defaultValue = "10")
          Integer pageSize)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException {
    if (pageIndex == null) {
      pageIndex = 0;
    }
    if (pageSize == null) {
      pageSize = 10;
    }

    if (!hasAccessToUserDirectory(userDirectoryId)) {
      throw new AccessDeniedException(
          "Access denied to the user directory (" + userDirectoryId + ")");
    }

    return securityService.getGroups(userDirectoryId, filter, sortDirection, pageIndex, pageSize);
  }

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
  @Operation(summary = "Retrieve the group members", description = "Retrieve the group members")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory or group could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}/groups/{groupName}/members",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration') or hasAuthority('FUNCTION_Security.GroupAdministration')")
  public GroupMembers getMembersForGroup(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId,
      @Parameter(name = "groupName", description = "The name of the group", required = true)
          @PathVariable
          String groupName,
      @Parameter(name = "filter", description = "The optional filter to apply to the group members")
          @RequestParam(value = "filter", required = false)
          String filter,
      @Parameter(
              name = "sortDirection",
              description = "The optional sort direction to apply to the group members")
          @RequestParam(value = "sortDirection", required = false)
          SortDirection sortDirection,
      @Parameter(name = "pageIndex", description = "The optional page index", example = "0")
          @RequestParam(value = "pageIndex", required = false, defaultValue = "0")
          Integer pageIndex,
      @Parameter(name = "pageSize", description = "The optional page size", example = "10")
          @RequestParam(value = "pageSize", required = false, defaultValue = "10")
          Integer pageSize)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          ServiceUnavailableException {
    if (pageIndex == null) {
      pageIndex = 0;
    }
    if (pageSize == null) {
      pageSize = 10;
    }

    if (!hasAccessToUserDirectory(userDirectoryId)) {
      throw new AccessDeniedException(
          "Access denied to the user directory (" + userDirectoryId + ")");
    }

    return securityService.getMembersForGroup(
        userDirectoryId, groupName, filter, sortDirection, pageIndex, pageSize);
  }

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
  @Operation(
      summary = "Retrieve the codes for the roles that have been assigned to the group",
      description = "Retrieve the codes for the roles that have been assigned to the group")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory or group could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}/groups/{groupName}/role-codes",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration') or hasAuthority('FUNCTION_Security.GroupAdministration')")
  public List<String> getRoleCodesForGroup(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId,
      @Parameter(name = "groupName", description = "The name of the group", required = true)
          @PathVariable
          String groupName)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          ServiceUnavailableException {
    if (!hasAccessToUserDirectory(userDirectoryId)) {
      throw new AccessDeniedException(
          "Access denied to the user directory (" + userDirectoryId + ")");
    }

    return securityService.getRoleCodesForGroup(userDirectoryId, groupName);
  }

  /**
   * Retrieve all the roles.
   *
   * @return the roles
   * @throws ServiceUnavailableException if the roles could not be retrieved
   */
  @Operation(summary = "Retrieve all the roles", description = "Retrieve all the roles")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(value = "/roles", method = RequestMethod.GET, produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration') or hasAuthority('FUNCTION_Security.GroupAdministration')")
  public List<Role> getRoles() throws ServiceUnavailableException {
    return securityService.getRoles();
  }

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
  @Operation(
      summary = "Retrieve the roles that have been assigned to the group",
      description = "Retrieve the roles that have been assigned to the group")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory or group could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}/groups/{groupName}/roles",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration') or hasAuthority('FUNCTION_Security.GroupAdministration')")
  public List<GroupRole> getRolesForGroup(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId,
      @Parameter(name = "groupName", description = "The name of the group", required = true)
          @PathVariable
          String groupName)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          ServiceUnavailableException {
    if (!hasAccessToUserDirectory(userDirectoryId)) {
      throw new AccessDeniedException(
          "Access denied to the user directory (" + userDirectoryId + ")");
    }

    return securityService.getRolesForGroup(userDirectoryId, groupName);
  }

  /**
   * Retrieve the tenant.
   *
   * @param tenantId the ID for the tenant
   * @return the tenant
   * @throws InvalidArgumentException if an argument is invalid
   * @throws TenantNotFoundException if the tenant could not be found
   * @throws ServiceUnavailableException if the tenant could not be retrieved
   */
  @Operation(summary = "Retrieve the tenant", description = "Retrieve the tenant")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The tenant could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/tenants/{tenantId}",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration')")
  public Tenant getTenant(
      @Parameter(name = "tenantId", description = "The ID for the tenant", required = true)
          @PathVariable
          UUID tenantId)
      throws InvalidArgumentException, TenantNotFoundException, ServiceUnavailableException {
    return securityService.getTenant(tenantId);
  }

  /**
   * Retrieve the name of the tenant.
   *
   * @param tenantId the ID for the tenant
   * @return the name of the tenant
   * @throws InvalidArgumentException if an argument is invalid
   * @throws TenantNotFoundException if the tenant could not be found
   * @throws ServiceUnavailableException if the name of the tenant could not be retrieved
   */
  @Operation(
      summary = "Retrieve the name of tenant",
      description = "Retrieve the name of the tenant")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The tenant could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/tenants/{tenantId}/name",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration')")
  public String getTenantName(
      @Parameter(name = "tenantId", description = "The ID for the tenant", required = true)
          @PathVariable
          UUID tenantId)
      throws InvalidArgumentException, TenantNotFoundException, ServiceUnavailableException {
    return ApiUtil.quote(securityService.getTenantName(tenantId));
  }

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
  @Operation(summary = "Retrieve the tenants", description = "Retrieve the tenants")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(value = "/tenants", method = RequestMethod.GET, produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration')")
  public Tenants getTenants(
      @Parameter(name = "filter", description = "The optional filter to apply to the tenants")
          @RequestParam(value = "filter", required = false)
          String filter,
      @Parameter(
              name = "sortDirection",
              description = "The optional sort direction to apply to the tenants")
          @RequestParam(value = "sortDirection", required = false)
          SortDirection sortDirection,
      @Parameter(name = "pageIndex", description = "The optional page index", example = "0")
          @RequestParam(value = "pageIndex", required = false, defaultValue = "0")
          Integer pageIndex,
      @Parameter(name = "pageSize", description = "The optional page size", example = "10")
          @RequestParam(value = "pageSize", required = false, defaultValue = "10")
          Integer pageSize)
      throws InvalidArgumentException, ServiceUnavailableException {
    if (pageIndex == null) {
      pageIndex = 0;
    }
    if (pageSize == null) {
      pageSize = 10;
    }

    return securityService.getTenants(filter, sortDirection, pageIndex, pageSize);
  }

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
  @Operation(
      summary = "Retrieve the tenants the user directory is associated with",
      description = "Retrieve the tenants the user directory is associated with")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}/tenants",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<Tenant>> getTenantsForUserDirectory(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException {
    if (isSecurityEnabled() && (!hasAccessToUserDirectory(userDirectoryId))) {
      throw new AccessDeniedException(
          "Access denied to the user directory (" + userDirectoryId + ")");
    }

    List<Tenant> tenants = securityService.getTenantsForUserDirectory(userDirectoryId);

    return new ResponseEntity<>(tenants, HttpStatus.OK);
  }

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
  @Operation(summary = "Retrieve the user", description = "Retrieve the user")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory or user could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}/users/{username}",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public User getUser(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId,
      @Parameter(name = "username", description = "The username for the user", required = true)
          @PathVariable
          String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          ServiceUnavailableException {
    if (!StringUtils.hasText(username)) {
      throw new InvalidArgumentException("username");
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (hasRole("Administrator")) {
      // Administrators always have access to retrieve a user's details
    } else if (isSecurityDisabled()
        || hasAccessToFunction("Security.TenantAdministration")
        || hasAccessToFunction("Security.UserAdministration")
        || hasAccessToFunction("Security.ResetUserPassword")) {
      if (isSecurityEnabled() && (!hasAccessToUserDirectory(userDirectoryId))) {
        throw new AccessDeniedException(
            "Access denied to the user directory (" + userDirectoryId + ")");
      }
    } else if ((authentication != null) && (authentication.getName().equalsIgnoreCase(username))) {
      // Users can retrieve their own details
    } else {
      throw new AccessDeniedException("Access denied to the user (" + username + ")");
    }

    User user = securityService.getUser(userDirectoryId, username);

    // Remove the password information
    user.setPassword(null);
    user.setPasswordAttempts(0);
    user.setPasswordExpiry(null);

    return user;
  }

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
  @Operation(
      summary = "Retrieve the user directories",
      description = "Retrieve the user directories")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.UserDirectoryAdministration')")
  public UserDirectories getUserDirectories(
      @Parameter(
              name = "filter",
              description = "The optional filter to apply to the user directories")
          @RequestParam(value = "filter", required = false)
          String filter,
      @Parameter(
              name = "sortDirection",
              description = "The optional sort direction to apply to the user directories")
          @RequestParam(value = "sortDirection", required = false)
          SortDirection sortDirection,
      @Parameter(name = "pageIndex", description = "The optional page index", example = "0")
          @RequestParam(value = "pageIndex", required = false, defaultValue = "0")
          Integer pageIndex,
      @Parameter(name = "pageSize", description = "The optional page size", example = "10")
          @RequestParam(value = "pageSize", required = false, defaultValue = "10")
          Integer pageSize)
      throws InvalidArgumentException, ServiceUnavailableException {
    if (pageIndex == null) {
      pageIndex = 0;
    }
    if (pageSize == null) {
      pageSize = 10;
    }

    return securityService.getUserDirectories(filter, sortDirection, pageIndex, pageSize);
  }

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
  @Operation(
      summary = "Retrieve the user directories the tenant is associated with",
      description = "Retrieve the user directories the tenant is associated with")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The tenant could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/tenants/{tenantId}/user-directories",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration') or hasAuthority('FUNCTION_Security.UserAdministration') or hasAuthority('FUNCTION_Security.GroupAdministration')")
  public ResponseEntity<List<UserDirectory>> getUserDirectoriesForTenant(
      @Parameter(name = "tenantId", description = "The ID for the tenant", required = true)
          @PathVariable
          UUID tenantId)
      throws InvalidArgumentException, TenantNotFoundException, ServiceUnavailableException {
    List<UserDirectory> userDirectories = securityService.getUserDirectoriesForTenant(tenantId);

    if (hasRole(SecurityService.ADMINISTRATOR_ROLE_CODE)
        || hasAccessToFunction("Security.TenantAdministration")) {
      return new ResponseEntity<>(userDirectories, HttpStatus.OK);
    } else {
      List<UserDirectory> filteredUserDirectories = new ArrayList<>();

      for (UserDirectory userDirectory : userDirectories) {
        if (hasAccessToUserDirectory(userDirectory.getId())) {
          filteredUserDirectories.add(userDirectory);
        }
      }

      return new ResponseEntity<>(filteredUserDirectories, HttpStatus.OK);
    }
  }

  /**
   * Retrieve the user directory.
   *
   * @param userDirectoryId the ID for the user directory
   * @return the user directory
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws ServiceUnavailableException if the user directory could not be retrieved
   */
  @Operation(summary = "Retrieve the user directory", description = "Retrieve the user directory")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.UserDirectoryAdministration')")
  public UserDirectory getUserDirectory(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException {
    return securityService.getUserDirectory(userDirectoryId);
  }

  /**
   * Retrieve the capabilities the user directory supports.
   *
   * @param userDirectoryId the ID for the user directory
   * @return the capabilities the user directory supports
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws ServiceUnavailableException if the user directory capabilities could not be retrieved
   */
  @Operation(
      summary = "Retrieve the capabilities the user directory supports",
      description = "Retrieve the capabilities the user directory supports")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}/capabilities",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.UserDirectoryAdministration') or hasAuthority('FUNCTION_Security.UserAdministration') or hasAuthority('FUNCTION_Security.GroupAdministration') or hasAuthority('FUNCTION_Security.ResetUserPassword')")
  public UserDirectoryCapabilities getUserDirectoryCapabilities(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException {
    if (!hasAccessToUserDirectory(userDirectoryId)) {
      throw new AccessDeniedException(
          "Access denied to the user directory (" + userDirectoryId + ")");
    }

    return securityService.getUserDirectoryCapabilities(userDirectoryId);
  }

  /**
   * Retrieve the name of the user directory.
   *
   * @param userDirectoryId the ID for the user directory
   * @return the name of user directory
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws ServiceUnavailableException if the name of the user directory could not be retrieved
   */
  @Operation(
      summary = "Retrieve the name of the user directory",
      description = "Retrieve the name of the user directory")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}/name",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.UserDirectoryAdministration')")
  public String getUserDirectoryName(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException {
    return ApiUtil.quote(securityService.getUserDirectoryName(userDirectoryId));
  }

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
  @Operation(
      summary = "Retrieve the summaries for the user directories",
      description = "Retrieve the summaries for the user directories")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directory-summaries",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.UserDirectoryAdministration')")
  public UserDirectorySummaries getUserDirectorySummaries(
      @Parameter(
              name = "filter",
              description = "The optional filter to apply to the user directories")
          @RequestParam(value = "filter", required = false)
          String filter,
      @Parameter(
              name = "sortDirection",
              description = "The optional sort direction to apply to the user directories")
          @RequestParam(value = "sortDirection", required = false)
          SortDirection sortDirection,
      @Parameter(name = "pageIndex", description = "The optional page index", example = "0")
          @RequestParam(value = "pageIndex", required = false, defaultValue = "0")
          Integer pageIndex,
      @Parameter(name = "pageSize", description = "The optional page size", example = "10")
          @RequestParam(value = "pageSize", required = false, defaultValue = "10")
          Integer pageSize)
      throws InvalidArgumentException, ServiceUnavailableException {
    if (pageIndex == null) {
      pageIndex = 0;
    }
    if (pageSize == null) {
      pageSize = 10;
    }

    return securityService.getUserDirectorySummaries(filter, sortDirection, pageIndex, pageSize);
  }

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
  @Operation(
      summary = "Retrieve the summaries for the user directories the tenant is associated with",
      description = "Retrieve the summaries for the user directories the tenant is associated with")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The tenant could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/tenants/{tenantId}/user-directory-summaries",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration') or hasAuthority('FUNCTION_Security.ResetUserPassword') or hasAuthority('FUNCTION_Security.UserAdministration') or hasAuthority('FUNCTION_Security.UserGroups')")
  public ResponseEntity<List<UserDirectorySummary>> getUserDirectorySummariesForTenant(
      @Parameter(name = "tenantId", description = "The ID for the tenant", required = true)
          @PathVariable
          UUID tenantId)
      throws InvalidArgumentException, TenantNotFoundException, ServiceUnavailableException {
    List<UserDirectorySummary> userDirectorySummaries =
        securityService.getUserDirectorySummariesForTenant(tenantId);

    if (hasRole("Administrator") || hasAccessToFunction("Security.TenantAdministration")) {
      return new ResponseEntity<>(userDirectorySummaries, HttpStatus.OK);
    } else {
      List<UserDirectorySummary> filteredUserDirectorySummaries = new ArrayList<>();

      for (UserDirectorySummary userDirectorySummary : userDirectorySummaries) {
        if (hasAccessToUserDirectory(userDirectorySummary.getId())) {
          filteredUserDirectorySummaries.add(userDirectorySummary);
        }
      }

      return new ResponseEntity<>(filteredUserDirectorySummaries, HttpStatus.OK);
    }
  }

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
  @Operation(
      summary = "Retrieve the user directory type for the user directory",
      description = "Retrieve the user directory type for the user directory")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory or user directory type could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}/user-directory-type",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.UserDirectoryAdministration') or hasAuthority('FUNCTION_Security.UserAdministration')")
  public UserDirectoryType getUserDirectoryTypeForUserDirectory(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId)
      throws InvalidArgumentException, UserDirectoryNotFoundException,
          UserDirectoryTypeNotFoundException, ServiceUnavailableException {
    if (!hasAccessToUserDirectory(userDirectoryId)) {
      throw new AccessDeniedException(
          "Access denied to the user directory (" + userDirectoryId + ")");
    }

    return securityService.getUserDirectoryTypeForUserDirectory(userDirectoryId);
  }

  /**
   * Retrieve the user directory types.
   *
   * @return the user directory types
   * @throws ServiceUnavailableException if the user directory types could not be retrieved
   */
  @Operation(
      summary = "Retrieve the user directory types",
      description = "Retrieve the user directory types")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directory-types",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.UserDirectoryAdministration')")
  public List<UserDirectoryType> getUserDirectoryTypes() throws ServiceUnavailableException {
    return securityService.getUserDirectoryTypes();
  }

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
  @Operation(
      summary = "Retrieve the name of the user",
      description = "Retrieve the name of the user")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory or user could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}/users/{username}/name",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration') or hasAuthority('FUNCTION_Security.UserAdministration') or hasAuthority('FUNCTION_Security.ResetUserPassword')")
  public String getUserName(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId,
      @Parameter(name = "username", description = "The username for the user", required = true)
          @PathVariable
          String username)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          ServiceUnavailableException {
    if (!hasAccessToUserDirectory(userDirectoryId)) {
      throw new AccessDeniedException(
          "Access denied to the user directory (" + userDirectoryId + ")");
    }

    return ApiUtil.quote(securityService.getUserName(userDirectoryId, username));
  }

  /**
   * Retrieve the users.
   *
   * @param userDirectoryId the ID for the user directory
   * @param filter the optional filter to apply to the users
   * @param sortBy the optional method used to sort the users e.g. by name.
   * @param sortDirection the optional sort direction to apply to the users
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the users
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws ServiceUnavailableException if the users could not be retrieved
   */
  @Operation(summary = "Retrieve the users", description = "Retrieve the users")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}/users",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration') or hasAuthority('FUNCTION_Security.UserAdministration') or hasAuthority('FUNCTION_Security.ResetUserPassword')")
  public Users getUsers(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId,
      @Parameter(name = "filter", description = "The optional filter to apply to the users")
          @RequestParam(value = "filter", required = false)
          String filter,
      @Parameter(
              name = "sortBy",
              description = "The optional method used to sort the users e.g. by name")
          @RequestParam(value = "sortBy", required = false)
          UserSortBy sortBy,
      @Parameter(
              name = "sortDirection",
              description = "The optional sort direction to apply to the users")
          @RequestParam(value = "sortDirection", required = false)
          SortDirection sortDirection,
      @Parameter(name = "pageIndex", description = "The optional page index", example = "0")
          @RequestParam(value = "pageIndex", required = false, defaultValue = "0")
          Integer pageIndex,
      @Parameter(name = "pageSize", description = "The optional page size", example = "10")
          @RequestParam(value = "pageSize", required = false, defaultValue = "10")
          Integer pageSize)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException {
    if (pageIndex == null) {
      pageIndex = 0;
    }
    if (pageSize == null) {
      pageSize = 10;
    }

    if (!hasAccessToUserDirectory(userDirectoryId)) {
      throw new AccessDeniedException(
          "Access denied to the user directory (" + userDirectoryId + ")");
    }

    return securityService.getUsers(
        userDirectoryId, filter, sortBy, sortDirection, pageIndex, pageSize);
  }

  /**
   * Remove the group member from the group.
   *
   * @param userDirectoryId the ID for the user directory
   * @param groupName the name of the group
   * @param memberType the group member type
   * @param memberName the name of the group member
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws GroupNotFoundException if the group could not be found
   * @throws GroupMemberNotFoundException if the group member could not be found
   * @throws ServiceUnavailableException if the group member could not be removed from the group
   */
  @Operation(
      summary = "Remove the group member from the group",
      description = "Remove the group member from the group")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "The group member was successfully removed from the group"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory or group or group member could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value =
          "/user-directories/{userDirectoryId}/groups/{groupName}/members/{memberType}/{memberName}",
      method = RequestMethod.DELETE,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration') or hasAuthority('FUNCTION_Security.GroupAdministration')")
  public void removeMemberFromGroup(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId,
      @Parameter(name = "groupName", description = "The name of the group", required = true)
          @PathVariable
          String groupName,
      @Parameter(name = "memberType", description = "The group member type", required = true)
          @PathVariable
          GroupMemberType memberType,
      @Parameter(name = "memberName", description = "The name of the group member", required = true)
          @PathVariable
          String memberName)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          GroupMemberNotFoundException, ServiceUnavailableException {
    if (!hasAccessToUserDirectory(userDirectoryId)) {
      throw new AccessDeniedException(
          "Access denied to the user directory (" + userDirectoryId + ")");
    }

    securityService.removeMemberFromGroup(userDirectoryId, groupName, memberType, memberName);
  }

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
  @Operation(
      summary = "Remove the role from the group",
      description = "Remove the role from the group")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "The role was successfully removed from the group"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory or group or group role could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}/groups/{groupName}/roles/{roleCode}",
      method = RequestMethod.DELETE,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration') or hasAuthority('FUNCTION_Security.GroupAdministration')")
  public void removeRoleFromGroup(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId,
      @Parameter(name = "groupName", description = "The name of the group", required = true)
          @PathVariable
          String groupName,
      @Parameter(name = "roleCode", description = "The code for the role", required = true)
          @PathVariable
          String roleCode)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          GroupRoleNotFoundException, ServiceUnavailableException {
    if (!hasAccessToUserDirectory(userDirectoryId)) {
      throw new AccessDeniedException(
          "Access denied to the user directory (" + userDirectoryId + ")");
    }

    if (roleCode.equalsIgnoreCase(SecurityService.ADMINISTRATOR_ROLE_CODE)
        && (!hasRole(SecurityService.ADMINISTRATOR_ROLE_CODE))) {
      throw new AccessDeniedException(
          "Insufficient authority to remove the "
              + SecurityService.ADMINISTRATOR_ROLE_CODE
              + " role from the group ("
              + groupName
              + ") for the user directory ("
              + userDirectoryId
              + ")");
    }

    securityService.removeRoleFromGroup(userDirectoryId, groupName, roleCode);
  }

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
  @Operation(
      summary = "Remove the user directory from the tenant",
      description = "Remove the user directory from the tenant")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "The user directory was successfully removed from the tenant"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The tenant or tenant user directory could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/tenants/{tenantId}/user-directories/{userDirectoryId}",
      method = RequestMethod.DELETE,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration')")
  public void removeUserDirectoryFromTenant(
      @Parameter(name = "tenantId", description = "The ID for the tenant", required = true)
          @PathVariable
          UUID tenantId,
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId)
      throws InvalidArgumentException, TenantNotFoundException,
          TenantUserDirectoryNotFoundException, ServiceUnavailableException {
    securityService.removeUserDirectoryFromTenant(tenantId, userDirectoryId);
  }

  /**
   * Initiate the password reset process for the user.
   *
   * @param username the username for the user
   * @param resetPasswordUrl the reset password URL
   * @throws InvalidArgumentException if an argument is invalid
   * @throws ServiceUnavailableException if the password reset could not be initiated
   */
  @Operation(
      summary = "Initiate the password reset process for the user",
      description = "Initiate the password reset process for the user")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "The password reset process was initiated successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/users/{username}/reset-password",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void resetPassword(
      @Parameter(name = "username", description = "The username for the user", required = true)
          @PathVariable
          String username,
      @Parameter(name = "resetPasswordUrl", description = "The reset password URL")
          @RequestParam(value = "resetPasswordUrl")
          String resetPasswordUrl)
      throws InvalidArgumentException, ServiceUnavailableException {
    try {
      securityService.initiatePasswordReset(username, resetPasswordUrl, true);
    } catch (UserNotFoundException ignored) {
      // Swallow exception to prevent disclosing whether a user exists
    }
  }

  /**
   * Update the group.
   *
   * @param userDirectoryId the ID for the user directory
   * @param groupName the name of the group
   * @param group the group
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws GroupNotFoundException if the group could not be found
   * @throws ServiceUnavailableException if the group could not be updated
   */
  @Operation(summary = "Update the group", description = "Update the group")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "The group was updated successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory or group could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}/groups/{groupName}",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration') or hasAuthority('FUNCTION_Security.GroupAdministration')")
  public void updateGroup(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId,
      @Parameter(name = "groupName", description = "The name of the group", required = true)
          @PathVariable
          String groupName,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The group to update",
              required = true)
          @RequestBody
          Group group)
      throws InvalidArgumentException, UserDirectoryNotFoundException, GroupNotFoundException,
          ServiceUnavailableException {
    if (!hasAccessToUserDirectory(userDirectoryId)) {
      throw new AccessDeniedException(
          "Access denied to the user directory (" + userDirectoryId + ")");
    }

    if (!StringUtils.hasText(groupName)) {
      throw new InvalidArgumentException("groupName");
    }

    if (group == null) {
      throw new InvalidArgumentException("group");
    }

    if (!groupName.equals(group.getName())) {
      throw new InvalidArgumentException("group");
    }

    securityService.updateGroup(group);
  }

  /**
   * Update the tenant.
   *
   * @param tenantId the ID for the tenant
   * @param tenant the tenant
   * @throws InvalidArgumentException if an argument is invalid
   * @throws TenantNotFoundException if the tenant could not be found
   * @throws ServiceUnavailableException if the tenant could not be updated
   */
  @Operation(summary = "Update the tenant", description = "Update the tenant")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "The tenant was updated successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The tenant could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/tenants/{tenantId}",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "isSecurityDisabled() or hasRole('Administrator') or hasAuthority('FUNCTION_Security.TenantAdministration')")
  public void updateTenant(
      @Parameter(name = "tenantId", description = "The ID for the tenant", required = true)
          @PathVariable
          UUID tenantId,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The tenant to update",
              required = true)
          @RequestBody
          Tenant tenant)
      throws InvalidArgumentException, TenantNotFoundException, ServiceUnavailableException {
    if (tenantId == null) {
      throw new InvalidArgumentException("tenantId");
    }

    if (tenant == null) {
      throw new InvalidArgumentException("tenant");
    }

    if (!tenantId.equals(tenant.getId())) {
      throw new InvalidArgumentException("tenant");
    }

    securityService.updateTenant(tenant);
  }

  /**
   * Update the user.
   *
   * @param userDirectoryId the ID for the user directory
   * @param username the username for the user
   * @param user the user
   * @param expirePassword expire the user's password
   * @param lockUser lock the user
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws UserNotFoundException if the user could not be found
   * @throws ServiceUnavailableException if the user could not be updated
   */
  @Operation(summary = "Update the user", description = "Update the user")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "The user was updated successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory or user could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}/users/{username}",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateUser(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId,
      @Parameter(name = "username", description = "The username for the user", required = true)
          @PathVariable
          String username,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The user to update",
              required = true)
          @RequestBody
          User user,
      @Parameter(name = "expirePassword", description = "Expire the user's password")
          @RequestParam(value = "expirePassword", required = false)
          Boolean expirePassword,
      @Parameter(name = "lockUser", description = "Lock the user")
          @RequestParam(value = "lockUser", required = false)
          Boolean lockUser)
      throws InvalidArgumentException, UserDirectoryNotFoundException, UserNotFoundException,
          ServiceUnavailableException {
    if (!StringUtils.hasText(username)) {
      throw new InvalidArgumentException("username");
    }

    // Apply access control
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (isSecurityEnabled()) {
      if (hasRole("Administrator")) {
        // Administrators always have access to retrieve a user's details
      } else if (hasAccessToFunction("Security.TenantAdministration")
          || hasAccessToFunction("Security.UserAdministration")) {
        if (!hasAccessToUserDirectory(userDirectoryId)) {
          throw new AccessDeniedException(
              "Access denied to the user directory (" + userDirectoryId + ")");
        }
      } else if ((authentication != null)
          && (authentication.getName().equalsIgnoreCase(username))) {
        // Users can retrieve their own details
      } else {
        throw new AccessDeniedException("Access denied to the user (" + username + ")");
      }
    }

    if (user == null) {
      throw new InvalidArgumentException("user");
    }

    if (!username.equals(user.getUsername())) {
      throw new InvalidArgumentException("user");
    }

    securityService.updateUser(
        user, (expirePassword != null) && expirePassword, (lockUser != null) && lockUser);
  }

  /**
   * Update the user directory.
   *
   * @param userDirectoryId the ID for the user directory
   * @param userDirectory the user directory
   * @throws InvalidArgumentException if an argument is invalid
   * @throws UserDirectoryNotFoundException if the user directory could not be found
   * @throws ServiceUnavailableException if the user directory could not be updated
   */
  @Operation(summary = "Update the user directory", description = "Update the user directory")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "The user directory was updated successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid argument",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "404",
            description = "The user directory could not be found",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "An error has occurred and the request could not be processed at this time",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetails.class)))
      })
  @RequestMapping(
      value = "/user-directories/{userDirectoryId}",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "hasRole('Administrator') or hasAuthority('FUNCTION_Security.UserDirectoryAdministration')")
  public void updateUserDirectory(
      @Parameter(
              name = "userDirectoryId",
              description = "The ID for the user directory",
              required = true)
          @PathVariable
          UUID userDirectoryId,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The user directory to update",
              required = true)
          @RequestBody
          UserDirectory userDirectory)
      throws InvalidArgumentException, UserDirectoryNotFoundException, ServiceUnavailableException {
    if (userDirectoryId == null) {
      throw new InvalidArgumentException("userDirectoryId");
    }

    if (userDirectory == null) {
      throw new InvalidArgumentException("userDirectory");
    }

    if (!userDirectoryId.equals(userDirectory.getId())) {
      throw new InvalidArgumentException("userDirectory");
    }

    securityService.updateUserDirectory(userDirectory);
  }

  /**
   * Confirm that the user associated with the authenticated request has access to the user
   * directory.
   *
   * @param userDirectoryId the ID for the user directory
   * @return <b>true</b> if the user associated with the authenticated request has access to the
   *     user directory or <b>false</b> otherwise
   * @throws InvalidArgumentException if an argument is invalid
   */
  protected boolean hasAccessToUserDirectory(UUID userDirectoryId) throws InvalidArgumentException {
    if (isSecurityEnabled()) {
      if (userDirectoryId == null) {
        throw new InvalidArgumentException("userDirectoryId");
      }

      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      // Could not retrieve the currently authenticated principal
      if (authentication == null) {
        return false;
      }

      try {
        // If the user is not authenticated then they cannot have access
        if (!authentication.isAuthenticated()) {
          return false;
        }

        // If the user has the "Administrator" role they always have access
        if (hasRole(SecurityService.ADMINISTRATOR_ROLE_CODE)) {
          return true;
        }

        List<UUID> userDirectoryIdsForUser = new ArrayList<>();

        List<UUID> tenantIds = getUUIDValuesForAuthoritiesWithPrefix(authentication, "TENANT_");

        for (UUID tenantId : tenantIds) {
          var userDirectoryIdsForTenant = securityService.getUserDirectoryIdsForTenant(tenantId);

          userDirectoryIdsForUser.addAll(userDirectoryIdsForTenant);
        }

        return userDirectoryIdsForUser.stream().anyMatch(userDirectoryId::equals);
      } catch (Throwable e) {
        logger.error(
            "Failed to check if the user ("
                + authentication.getName()
                + ") has access to the user directory ("
                + userDirectoryId
                + ")",
            e);
        return false;
      }
    } else {
      return true;
    }
  }
}
