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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The <b>Tenant</b> class holds the information for a tenant.
 *
 * @author Marcus Portmann
 */
@Schema(
    description =
        "A group of users who share common access with specific privileges to a particular subset of the information managed by a multi-tenant application")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "name", "status"})
@Entity
@Table(schema = "security", name = "tenants")
@SuppressWarnings({"unused", "WeakerAccess"})
public class Tenant implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The date and time the tenant was created. */
  @JsonIgnore
  @Column(name = "created", nullable = false, updatable = false)
  private LocalDateTime created;

  /** The ID for the tenant. */
  @Schema(description = "The ID for the tenant", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  /** The name of the tenant. */
  @Schema(description = "The name of the tenant", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  @Column(name = "name", length = 100, nullable = false)
  private String name;

  /** The status for the tenant. */
  @Schema(description = "The status for the tenant", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Column(name = "status", nullable = false)
  private TenantStatus status;

  /** The date and time the tenant was last updated. */
  @JsonIgnore
  @Column(name = "updated", insertable = false)
  private LocalDateTime updated;

  /** The user directories associated with the tenant. */
  @JsonIgnore
  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      schema = "security",
      name = "user_directory_to_tenant_map",
      joinColumns = @JoinColumn(name = "tenant_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "user_directory_id", referencedColumnName = "id"))
  private Set<UserDirectory> userDirectories = new HashSet<>();

  /** Constructs a new <b>Tenant</b>. */
  public Tenant() {}

  /**
   * Constructs a new <b>Tenant</b>.
   *
   * @param name the name of the tenant
   * @param status the status for the tenant
   */
  public Tenant(String name, TenantStatus status) {
    this.name = name;
    this.status = status;
  }

  /**
   * Constructs a new <b>Tenant</b>.
   *
   * @param id the ID for the tenant
   * @param name the name of the tenant
   * @param status the status for the tenant
   */
  public Tenant(UUID id, String name, TenantStatus status) {
    this.id = id;
    this.name = name;
    this.status = status;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param object the reference object with which to compare
   * @return <b>true</b> if this object is the same as the object argument otherwise <b> false</b>
   */
  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }

    if (object == null) {
      return false;
    }

    if (getClass() != object.getClass()) {
      return false;
    }

    Tenant other = (Tenant) object;

    return Objects.equals(id, other.id);
  }

  /**
   * Returns the date and time the tenant was created.
   *
   * @return the date and time the tenant was created
   */
  public LocalDateTime getCreated() {
    return created;
  }

  /**
   * Returns the ID for the tenant.
   *
   * @return the ID for the tenant
   */
  public UUID getId() {
    return id;
  }

  /**
   * Returns the name of the tenant.
   *
   * @return the name of the tenant
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the status for the tenant.
   *
   * @return the status for the tenant
   */
  public TenantStatus getStatus() {
    return status;
  }

  /**
   * Returns the date and time the tenant was last updated.
   *
   * @return the date and time the tenant was last updated
   */
  public LocalDateTime getUpdated() {
    return updated;
  }

  /**
   * Returns the user directories associated with the tenant.
   *
   * @return the user directories associated with the tenant
   */
  public Set<UserDirectory> getUserDirectories() {
    return userDirectories;
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for the object
   */
  @Override
  public int hashCode() {
    return (id == null) ? 0 : id.hashCode();
  }

  /**
   * Link the user directory to the tenant.
   *
   * @param userDirectory the user directory
   */
  public void linkUserDirectory(UserDirectory userDirectory) {
    userDirectories.add(userDirectory);
    userDirectory.getTenants().add(this);
  }

  /**
   * Set the ID for the tenant.
   *
   * @param id the ID for the tenant
   */
  public void setId(UUID id) {
    this.id = id;
  }

  /**
   * Set the name of the tenant.
   *
   * @param name the name of the tenant
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Set the status for the tenant.
   *
   * @param status the status for the tenant
   */
  public void setStatus(TenantStatus status) {
    this.status = status;
  }

  /**
   * Set the user directories associated with the tenant.
   *
   * @param userDirectories the user directories associated with the tenant
   */
  public void setUserDirectories(Set<UserDirectory> userDirectories) {
    this.userDirectories = userDirectories;
  }

  /**
   * Unlink the user directory from the tenant.
   *
   * @param userDirectory the user directory
   */
  public void unlinkUserDirectory(UserDirectory userDirectory) {
    userDirectories.remove(userDirectory);
    userDirectory.getTenants().remove(this);
  }

  @PrePersist
  protected void onCreate() {
    created = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updated = LocalDateTime.now();
  }
}
