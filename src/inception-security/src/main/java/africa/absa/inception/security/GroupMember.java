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
 * The <b>GroupMember</b> class holds the information for a group member.
 *
 * @author Marcus Portmann
 */
@Schema(description = "A group member association")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"userDirectoryId", "groupName", "memberName", "memberType"})
@SuppressWarnings({"unused"})
public class GroupMember implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The name of the group. */
  @Schema(description = "The name of the group", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  private String groupName;

  /** The name of the group member. */
  @Schema(description = "The name of the group member", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  private String memberName;

  /** The group member type. */
  @Schema(description = "The group member type", required = true)
  @JsonProperty(required = true)
  @NotNull
  private GroupMemberType memberType;

  /**
   * The Universally Unique Identifier (UUID) for the user directory the group is associated with.
   */
  @Schema(
      description =
          "The Universally Unique Identifier (UUID) for the user directory the "
              + "group is associated with",
      required = true)
  @JsonProperty(required = true)
  @NotNull
  private UUID userDirectoryId;

  /** Constructs a new <b>GroupMember</b>. */
  public GroupMember() {}

  /**
   * Constructs a new <b>GroupMember</b>.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) for the user directory the
   *     group is associated with
   * @param groupName the name of the group
   * @param memberType the group member type
   * @param memberName the name of the group member
   */
  public GroupMember(
      UUID userDirectoryId, String groupName, GroupMemberType memberType, String memberName) {
    this.userDirectoryId = userDirectoryId;
    this.groupName = groupName;
    this.memberType = memberType;
    this.memberName = memberName;
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
   * Returns the name of the group member.
   *
   * @return the name of the group member
   */
  public String getMemberName() {
    return memberName;
  }

  /**
   * Returns the group member type.
   *
   * @return the group member type
   */
  public GroupMemberType getMemberType() {
    return memberType;
  }

  /**
   * Returns the Universally Unique Identifier (UUID) for the user directory the group is associated
   * with.
   *
   * @return the Universally Unique Identifier (UUID) for the user directory the group is associated
   *     with
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
   * Set the name of the group member.
   *
   * @param memberName the name of the group member
   */
  public void setMemberName(String memberName) {
    this.memberName = memberName;
  }

  /**
   * Set the group member type.
   *
   * @param memberType the group member type
   */
  public void setMemberType(GroupMemberType memberType) {
    this.memberType = memberType;
  }

  /**
   * Set the Universally Unique Identifier (UUID) for the user directory the group is associated
   * with.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) for the user directory the
   *     group is associated with
   */
  public void setUserDirectoryId(UUID userDirectoryId) {
    this.userDirectoryId = userDirectoryId;
  }
}
