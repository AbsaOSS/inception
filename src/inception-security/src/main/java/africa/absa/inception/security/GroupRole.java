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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The <b>GroupRole</b> class holds the information for a group role.
 *
 * @author Marcus Portmann
 */
@Schema(description = "A group role association")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"userDirectoryId", "groupName", "roleCode"})
@SuppressWarnings({"unused"})
public class GroupRole implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The name of the group. */
  @Schema(description = "The name of the group", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  private String groupName;

  /** The code for the role. */
  @Schema(description = "The code for the role", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  private String roleCode;

  /** The ID for the user directory the group is associated with. */
  @Schema(
      description = "The ID for the user directory the " + "group is associated with",
      required = true)
  @JsonProperty(required = true)
  @NotNull
  private UUID userDirectoryId;

  /** Constructs a new <b>GroupRole</b>. */
  public GroupRole() {}

  /**
   * Constructs a new <b>GroupRole</b>.
   *
   * @param userDirectoryId the ID for the user directory the group is associated with
   * @param groupName the name of the group
   * @param roleCode the code for the role
   */
  public GroupRole(UUID userDirectoryId, String groupName, String roleCode) {
    this.userDirectoryId = userDirectoryId;
    this.groupName = groupName;
    this.roleCode = roleCode;
  }

  /**
   * Returns the name of the group.
   *
   * @return the name of the group
   */
  public String getGroupName() {
    return groupName;
  }

  /**
   * Returns the code for the role.
   *
   * @return the code for the role
   */
  public String getRoleCode() {
    return roleCode;
  }

  /**
   * Returns the ID for the user directory the group is associated with.
   *
   * @return the ID for the user directory the group is associated with
   */
  public UUID getUserDirectoryId() {
    return userDirectoryId;
  }

  /**
   * Set the name of the group.
   *
   * @param groupName the name of the group
   */
  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  /**
   * Set the code for the role.
   *
   * @param roleCode the code for the role
   */
  public void setRoleCode(String roleCode) {
    this.roleCode = roleCode;
  }

  /**
   * Set the ID for the user directory the group is associated with.
   *
   * @param userDirectoryId the ID for the user directory the group is associated with
   */
  public void setUserDirectoryId(UUID userDirectoryId) {
    this.userDirectoryId = userDirectoryId;
  }
}
