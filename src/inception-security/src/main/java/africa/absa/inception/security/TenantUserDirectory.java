package africa.absa.inception.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * The <b>TenantUserDirectory</b> class holds the information for a tenant user directory.
 *
 * @author Marcus Portmann
 */
@Schema(description = "A tenant user directory association")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"tenantId", "userDirectoryId"})
@SuppressWarnings({"unused"})
public class TenantUserDirectory implements Serializable {

  /** The ID for the tenant. */
  @Schema(description = "The ID for the tenant", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Id
  @Column(name = "tenantId", nullable = false)
  private UUID tenantId;

  /** The ID for the user directory. */
  @Schema(description = "The ID for the user directory", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Id
  @Column(name = "userDirectoryId", nullable = false)
  private UUID userDirectoryId;

  /** Constructs a new <b>TenantUserDirectory</b>. */
  public TenantUserDirectory() {}

  /**
   * Constructs a new <b>TenantUserDirectory</b>.
   *
   * @param tenantId the ID for the tenant
   * @param userDirectoryId the ID for the user directory
   */
  public TenantUserDirectory(UUID tenantId, UUID userDirectoryId) {
    this.tenantId = tenantId;
    this.userDirectoryId = userDirectoryId;
  }

  /**
   * Returns the ID for the tenant.
   *
   * @return the ID for the tenant
   */
  public UUID getTenantId() {
    return tenantId;
  }

  /**
   * Returns the ID for the user directory.
   *
   * @return the ID for the user directory
   */
  public UUID getUserDirectoryId() {
    return userDirectoryId;
  }

  /**
   * Set the ID for the tenant.
   *
   * @param tenantId the ID for the tenant
   */
  public void setTenantId(UUID tenantId) {
    this.tenantId = tenantId;
  }

  /**
   * Set the ID for the user directory.
   *
   * @param userDirectoryId the ID for the user directory
   */
  public void setUserDirectoryId(UUID userDirectoryId) {
    this.userDirectoryId = userDirectoryId;
  }
}
