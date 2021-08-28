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
 * The <b>Tenants</b> class holds the results of a request to retrieve a list of tenants.
 *
 * @author Marcus Portmann
 */
@Schema(description = "The results of a request to retrieve a list of tenants")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"tenants", "total", "filter", "sortDirection", "pageIndex", "pageSize"})
@SuppressWarnings({"unused"})
public class Tenants implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The optional filter that was applied to the tenants. */
  @Schema(description = "The optional filter that was applied to the tenants")
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

  /** The optional sort direction that was applied to the tenants. */
  @Schema(description = "The optional sort direction that was applied to the tenants")
  @JsonProperty
  private SortDirection sortDirection;

  /** The tenants. */
  @Schema(description = "The tenants", required = true)
  @JsonProperty(required = true)
  private List<Tenant> tenants;

  /** The total number of tenants. */
  @Schema(description = "The total number of tenants", required = true)
  @JsonProperty(required = true)
  private long total;

  /** Constructs a new <b>Tenants</b>. */
  public Tenants() {}

  /**
   * Constructs a new <b>Tenants</b>.
   *
   * @param tenants the tenants
   * @param total the total number of tenants
   * @param filter the optional filter that was applied to the tenants
   * @param sortDirection the optional sort direction that was applied to the tenants
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   */
  public Tenants(
      List<Tenant> tenants,
      long total,
      String filter,
      SortDirection sortDirection,
      Integer pageIndex,
      Integer pageSize) {
    this.tenants = tenants;
    this.total = total;
    this.filter = filter;
    this.sortDirection = sortDirection;
    this.pageIndex = pageIndex;
    this.pageSize = pageSize;
  }

  /**
   * Returns the optional filter that was applied to the tenants.
   *
   * @return the optional filter that was applied to the tenants
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
   * Returns the optional sort direction that was applied to the tenants.
   *
   * @return the optional sort direction that was applied to the tenants
   */
  public SortDirection getSortDirection() {
    return sortDirection;
  }

  /**
   * Returns the tenants.
   *
   * @return the tenants
   */
  public List<Tenant> getTenants() {
    return tenants;
  }

  /**
   * Returns the total number of tenants.
   *
   * @return the total number of tenants
   */
  public Long getTotal() {
    return total;
  }
}
