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

/**
 * The <b>UserDirectorySummaries</b> class holds the results of a request to retrieve a list of user
 * directory summaries.
 *
 * @author Marcus Portmann
 */
@Schema(description = "The results of a request to retrieve a list of user directory summaries")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "userDirectorySummaries",
  "total",
  "filter",
  "sortDirection",
  "pageIndex",
  "pageSize"
})
@SuppressWarnings({"unused"})
public class UserDirectorySummaries implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The optional filter that was applied to the user directory summaries. */
  @Schema(description = "The optional filter that was applied to the user directory summaries")
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

  /** The optional sort direction that was applied to the user directory summaries. */
  @Schema(
      description = "The optional sort direction that was applied to the user directory summaries")
  @JsonProperty
  private SortDirection sortDirection;

  /** The total number of user directory summaries. */
  @Schema(description = "The total number of user directory summaries", required = true)
  @JsonProperty(required = true)
  private long total;

  /** The user directory summaries. */
  @Schema(description = "The user directory summaries", required = true)
  @JsonProperty(required = true)
  private List<UserDirectorySummary> userDirectorySummaries;

  /** Constructs a new <b>UserDirectorySummaries</b>. */
  public UserDirectorySummaries() {}

  /**
   * Constructs a new <b>UserDirectorySummaries</b>.
   *
   * @param userDirectorySummaries the user directory summaries
   * @param total the total number of user directory summaries
   * @param filter the optional filter that was applied to the user directory summaries
   * @param sortDirection the optional sort direction that was applied to the user directory
   *     summaries
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   */
  public UserDirectorySummaries(
      List<UserDirectorySummary> userDirectorySummaries,
      long total,
      String filter,
      SortDirection sortDirection,
      Integer pageIndex,
      Integer pageSize) {
    this.userDirectorySummaries = userDirectorySummaries;
    this.total = total;
    this.filter = filter;
    this.sortDirection = sortDirection;
    this.pageIndex = pageIndex;
    this.pageSize = pageSize;
  }

  /**
   * Returns the optional filter that was applied to the user directory summaries.
   *
   * @return the optional filter that was applied to the user directory summaries
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
   * Returns the optional sort direction that was applied to the user directory summaries.
   *
   * @return the optional sort direction that was applied to the user directory summaries
   */
  public SortDirection getSortDirection() {
    return sortDirection;
  }

  /**
   * Returns the total number of user directory summaries.
   *
   * @return the total number of user directory summaries
   */
  public Long getTotal() {
    return total;
  }

  /**
   * Returns the user directory summaries.
   *
   * @return the user directory summaries
   */
  public List<UserDirectorySummary> getUserDirectorySummaries() {
    return userDirectorySummaries;
  }
}
