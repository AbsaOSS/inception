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

package africa.absa.inception.error;

import africa.absa.inception.core.sorting.SortDirection;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;

/**
 * The <b>ErrorReportSummaries</b> class holds the results of a request to retrieve the summaries
 * for a list of error reports.
 *
 * @author Marcus Portmann
 */
@Schema(
    description = "The results of a request to retrieve the summaries for a list of error reports")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "errorReportSummaries",
  "total",
  "filter",
  "sortBy",
  "sortDirection",
  "pageIndex",
  "pageSize"
})
@SuppressWarnings({"unused"})
public class ErrorReportSummaries implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The error report summaries. */
  @Schema(description = "The error report summaries", required = true)
  @JsonProperty(required = true)
  private List<ErrorReportSummary> errorReportSummaries;

  /** The optional filter that was applied to the error reports. */
  @Schema(description = "The optional filter that was applied to the error reports")
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

  /** The optional method used to sort the error reports e.g. by who submitted them. */
  @Schema(
      description = "The optional method used to sort the error reports e.g. by who submitted them")
  @JsonProperty
  private ErrorReportSortBy sortBy;

  /** The optional sort direction that was applied to the error reports. */
  @Schema(description = "The optional sort direction that was applied to the error reports")
  @JsonProperty
  private SortDirection sortDirection;

  /** The total number of error report summaries. */
  @Schema(description = "The total number of error report summaries", required = true)
  @JsonProperty(required = true)
  private long total;

  /** Constructs a new <b>ErrorReportSummaries</b>. */
  public ErrorReportSummaries() {}

  /**
   * Constructs a new <b>ErrorReportSummaries</b>.
   *
   * @param errorReportSummaries the error report summaries
   * @param total the total number of error report summaries
   * @param filter the optional filter that was applied to the error reports
   * @param sortBy the optional method used to sort the error reports e.g. by who submitted them
   * @param sortDirection the optional sort direction that was applied to the error reports
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   */
  public ErrorReportSummaries(
      List<ErrorReportSummary> errorReportSummaries,
      long total,
      String filter,
      ErrorReportSortBy sortBy,
      SortDirection sortDirection,
      Integer pageIndex,
      Integer pageSize) {
    this.errorReportSummaries = errorReportSummaries;
    this.total = total;
    this.filter = filter;
    this.sortBy = sortBy;
    this.sortDirection = sortDirection;
    this.pageIndex = pageIndex;
    this.pageSize = pageSize;
  }

  /**
   * Returns the error report summaries.
   *
   * @return the error report summaries
   */
  public List<ErrorReportSummary> getErrorReportSummaries() {
    return errorReportSummaries;
  }

  /**
   * Returns the optional filter that was applied to the error reports.
   *
   * @return the optional filter that was applied to the error reports
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
   * Returns the optional method used to sort the error reports e.g. by who submitted them.
   *
   * @return the optional method used to sort the error reports
   */
  public ErrorReportSortBy getSortBy() {
    return sortBy;
  }

  /**
   * Returns the optional sort direction that was applied to the error reports.
   *
   * @return the optional sort direction that was applied to the error reports
   */
  public SortDirection getSortDirection() {
    return sortDirection;
  }

  /**
   * Returns the total number of error report summaries.
   *
   * @return the total number of error report summaries
   */
  public Long getTotal() {
    return total;
  }
}
