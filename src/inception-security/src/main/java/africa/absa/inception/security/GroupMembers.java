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

import africa.absa.inception.core.sorting.SortDirection;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * The <b>GroupMembers</b> class holds the results of a request to retrieve a list of group members.
 *
 * @author Marcus Portmann
 */
@Schema(description = "The results of a request to retrieve a list of group members")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "userDirectoryId",
  "groupName",
  "groupMembers",
  "total",
  "filter",
  "sortDirection",
  "pageIndex",
  "pageSize"
})
@SuppressWarnings({"unused"})
public class GroupMembers implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The optional filter that was applied to the group members. */
  @Schema(description = "The optional filter that was applied to the group members")
  @JsonProperty
  private String filter;

  /** The group members. */
  @Schema(description = "The group members", required = true)
  @JsonProperty(required = true)
  private List<GroupMember> groupMembers;

  /** The name of the group the group members are associated with. */
  @Schema(
      description = "The name of the group the group members are associated with",
      required = true)
  @JsonProperty(required = true)
  private String groupName;

  /** The optional page index. */
  @Schema(description = "The optional page index")
  @JsonProperty
  private Integer pageIndex;

  /** The optional page size. */
  @Schema(description = "The optional page size")
  @JsonProperty
  private Integer pageSize;

  /** The optional sort direction that was applied to the group members. */
  @Schema(description = "The optional sort direction that was applied to the group members")
  @JsonProperty
  private SortDirection sortDirection;

  /** The total number of group members. */
  @Schema(description = "The total number of group members", required = true)
  @JsonProperty(required = true)
  private long total;

  /**
   * The Universally Unique Identifier (UUID) for the user directory the group members are
   * associated with.
   */
  @Schema(
      description =
          "The Universally Unique Identifier (UUID) for the user directory the group members are associated with",
      required = true)
  @JsonProperty(required = true)
  private UUID userDirectoryId;

  /** Constructs a new <b>GroupMembers</b>. */
  public GroupMembers() {}

  /**
   * Constructs a new <b>GroupMembers</b>.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) for the user directory the
   *     group members are associated with
   * @param groupName the name of the group the group members are associated with
   * @param groupMembers the group members
   * @param total the total number of group members
   * @param filter the optional filter that was applied to the group members
   * @param sortDirection the optional sort direction that was applied to the group members
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   */
  public GroupMembers(
      UUID userDirectoryId,
      String groupName,
      List<GroupMember> groupMembers,
      long total,
      String filter,
      SortDirection sortDirection,
      Integer pageIndex,
      Integer pageSize) {
    this.userDirectoryId = userDirectoryId;
    this.groupName = groupName;
    this.groupMembers = groupMembers;
    this.total = total;
    this.filter = filter;
    this.sortDirection = sortDirection;
    this.pageIndex = pageIndex;
    this.pageSize = pageSize;
  }

  /**
   * Returns the optional filter that was applied to the group members.
   *
   * @return the optional filter that was applied to the group members
   */
  public String getFilter() {
    return filter;
  }

  /**
   * Returns the group members.
   *
   * @return the group members
   */
  public List<GroupMember> getGroupMembers() {
    return groupMembers;
  }

  /**
   * Returns the name of the group the group members are associated with.
   *
   * @return the name of the group the group members are associated with
   */
  public String getGroupName() {
    return groupName;
  }

  /**
   * Returns the optional page index.
   *
   * @return the optional page index
   */
  public Integer getPageIndex() {
    return pageIndex;
  }

  /**
   * Returns the optional page size.
   *
   * @return the optional page size
   */
  public Integer getPageSize() {
    return pageSize;
  }

  /**
   * Returns the optional sort direction that was applied to the group members.
   *
   * @return the optional sort direction that was applied to the group members
   */
  public SortDirection getSortDirection() {
    return sortDirection;
  }

  /**
   * Returns the total number of group members.
   *
   * @return the total number of group members
   */
  public Long getTotal() {
    return total;
  }

  /**
   * Returns the Universally Unique Identifier (UUID) for the user directory the group members are
   * associated with.
   *
   * @return the Universally Unique Identifier (UUID) for the user directory the group members are
   *     associated with
   */
  public UUID getUserDirectoryId() {
    return userDirectoryId;
  }
}
