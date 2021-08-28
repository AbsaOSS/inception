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
 * The <b>Users</b> class holds the results of a request to retrieve a list of users.
 *
 * @author Marcus Portmann
 */
@Schema(description = "The results of a request to retrieve a list of users")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "userDirectoryId",
  "users",
  "total",
  "filter",
  "sortBy",
  "sortDirection",
  "pageIndex",
  "pageSize"
})
@SuppressWarnings({"unused"})
public class Users implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The optional filter that was applied to the users. */
  @Schema(description = "The optional filter that was applied to the users")
  @JsonProperty
  private String filter;

  /** The optional page index. */
  @Schema(description = "The optional page index")
  @JsonProperty
  private Integer pageIndex;

  /** The optional page size. */
  @Schema(description = "The optional page size")
  @JsonProperty
  private Integer pageSize;

  /** The optional method used to sort the users e.g. by name. */
  @Schema(description = "The optional method used to sort the users e.g. by name")
  @JsonProperty
  private UserSortBy sortBy;

  /** The optional sort direction that was applied to the users. */
  @Schema(description = "The optional sort direction that was applied to the users")
  @JsonProperty
  private SortDirection sortDirection;

  /** The total number of users. */
  @Schema(description = "The total number of users", required = true)
  @JsonProperty(required = true)
  private long total;

  /**
   * The Universally Unique Identifier (UUID) for the user directory the users are associated with.
   */
  @Schema(
      description =
          "The Universally Unique Identifier (UUID) for the user directory the users are associated with",
      required = true)
  @JsonProperty(required = true)
  private UUID userDirectoryId;

  /** The users. */
  @Schema(description = "The users", required = true)
  @JsonProperty(required = true)
  private List<User> users;

  /** Constructs a new <b>Users</b>. */
  public Users() {}

  /**
   * Constructs a new <b>Users</b>.
   *
   * @param userDirectoryId the Universally Unique Identifier (UUID) for the user directory the
   *     users are associated with
   * @param users the users
   * @param total the total number of users
   * @param filter the optional filter that was applied to the users
   * @param sortBy the optional method used to sort the users e.g. by name
   * @param sortDirection the optional sort direction that was applied to the users
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   */
  public Users(
      UUID userDirectoryId,
      List<User> users,
      long total,
      String filter,
      UserSortBy sortBy,
      SortDirection sortDirection,
      Integer pageIndex,
      Integer pageSize) {
    this.userDirectoryId = userDirectoryId;
    this.users = users;
    this.total = total;
    this.filter = filter;
    this.sortBy = sortBy;
    this.sortDirection = sortDirection;
    this.pageIndex = pageIndex;
    this.pageSize = pageSize;
  }

  /**
   * Returns the optional filter that was applied to the users.
   *
   * @return the optional filter that was applied to the users
   */
  public String getFilter() {
    return filter;
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
   * Returns the optional method used to sort the users e.g. by name.
   *
   * @return the optional method used to sort the users
   */
  public UserSortBy getSortBy() {
    return sortBy;
  }

  /**
   * Returns the optional sort direction that was applied to the users.
   *
   * @return the optional sort direction that was applied to the users
   */
  public SortDirection getSortDirection() {
    return sortDirection;
  }

  /**
   * Returns the total number of users.
   *
   * @return the total number of users
   */
  public Long getTotal() {
    return total;
  }

  /**
   * Returns the Universally Unique Identifier (UUID) for the user directory the users are
   * associated with.
   *
   * @return the Universally Unique Identifier (UUID) for the user directory the users are
   *     associated with
   */
  public UUID getUserDirectoryId() {
    return userDirectoryId;
  }

  /**
   * Returns the users.
   *
   * @return the users
   */
  public List<User> getUsers() {
    return users;
  }
}
