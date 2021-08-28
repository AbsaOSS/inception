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

import africa.absa.inception.core.xml.DtdJarResolver;
import africa.absa.inception.core.xml.XmlParserErrorHandler;
import africa.absa.inception.core.xml.XmlUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * The <b>UserDirectory</b> class holds the information for a user directory.
 *
 * @author Marcus Portmann
 */
@Schema(description = "A user directory")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "type", "name", "parameters"})
@Entity
@Table(schema = "security", name = "user_directories")
@SuppressWarnings({"unused"})
public class UserDirectory implements Serializable {

  private static final long serialVersionUID = 1000000;

  /** The date and time the user directory was created. */
  @JsonIgnore
  @Column(name = "created", nullable = false, updatable = false)
  private LocalDateTime created;

  /** The Universally Unique Identifier (UUID) for the user directory. */
  @Schema(
      description = "The Universally Unique Identifier (UUID) for the user directory",
      required = true)
  @JsonProperty(required = true)
  @NotNull
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  /** The name of the user directory. */
  @Schema(description = "The name of the user directory", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  @Column(name = "name", length = 100, nullable = false)
  private String name;

  /** The parameters for the user directory. */
  @Schema(description = "The parameters for the user directory", required = true)
  @JsonProperty(required = true)
  @Valid
  @Transient
  private List<UserDirectoryParameter> parameters = new ArrayList<>();

  /** The tenants the user directory is associated with. */
  @JsonIgnore
  @ManyToMany(mappedBy = "userDirectories")
  private Set<Tenant> tenants = new HashSet<>();

  /** The code for the user directory type. */
  @Schema(description = "The code for the user directory type", required = true)
  @JsonProperty(required = true)
  @NotNull
  @Size(min = 1, max = 100)
  @Column(name = "type", length = 100, nullable = false)
  private String type;

  /** The date and time the user directory was last updated. */
  @JsonIgnore
  @Column(name = "updated", insertable = false)
  private LocalDateTime updated;

  /** Constructs a new <b>UserDirectory</b>. */
  public UserDirectory() {}

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

    UserDirectory other = (UserDirectory) object;

    return Objects.equals(id, other.id);
  }

  /**
   * Returns the XML configuration data for the user directory.
   *
   * @return the XML configuration data for the user directory
   */
  @JsonIgnore
  @Access(AccessType.PROPERTY)
  @Column(name = "configuration", length = 4000, nullable = false)
  public String getConfiguration() {
    StringBuilder buffer = new StringBuilder();

    buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    buffer.append(
        "<!DOCTYPE userDirectory SYSTEM \"UserDirectoryConfiguration.dtd\"><userDirectory>");

    for (UserDirectoryParameter parameter : parameters) {
      buffer.append("<parameter>");
      buffer.append("<name>").append(parameter.getName()).append("</name>");
      buffer
          .append("<value>")
          .append(StringUtils.hasText(parameter.getValue()) ? parameter.getValue() : "")
          .append("</value>");
      buffer.append("</parameter>");
    }

    buffer.append("</userDirectory>");

    return buffer.toString();
  }

  /**
   * Returns the date and time the user directory was created.
   *
   * @return the date and time the user directory was created
   */
  public LocalDateTime getCreated() {
    return created;
  }

  /**
   * Returns the Universally Unique Identifier (UUID) for the user directory.
   *
   * @return the Universally Unique Identifier (UUID) for the user directory
   */
  public UUID getId() {
    return id;
  }

  /**
   * Returns the name of the user directory.
   *
   * @return the name of the user directory
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the parameters for the user directory.
   *
   * @return the parameters for the user directory
   */
  public List<UserDirectoryParameter> getParameters() {
    return parameters;
  }

  /**
   * Returns the tenants the user directory is associated with.
   *
   * @return the tenants the user directory is associated with
   */
  public Set<Tenant> getTenants() {
    return tenants;
  }

  /**
   * Returns the code for the user directory type.
   *
   * @return the code for the user directory type
   */
  public String getType() {
    return type;
  }

  /**
   * Returns the date and time the user directory was last updated.
   *
   * @return the date and time the user directory was last updated
   */
  public LocalDateTime getUpdated() {
    return updated;
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
   * Set the XML configuration data for the user directory.
   *
   * @param configuration the XML configuration data for the user directory
   */
  public void setConfiguration(String configuration) throws InvalidConfigurationException {
    try {
      // Parse the XML configuration data
      DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

      builderFactory.setValidating(true);

      DocumentBuilder builder = builderFactory.newDocumentBuilder();

      builder.setEntityResolver(
          new DtdJarResolver(
              "UserDirectoryConfiguration.dtd", "META-INF/UserDirectoryConfiguration.dtd"));
      builder.setErrorHandler(new XmlParserErrorHandler());

      InputSource inputSource = new InputSource(new ByteArrayInputStream(configuration.getBytes()));
      Document document = builder.parse(inputSource);

      Element rootElement = document.getDocumentElement();

      // Read the user directory parameters configuration
      parameters = new ArrayList<>();

      NodeList parameterElements = rootElement.getElementsByTagName("parameter");

      for (int i = 0; i < parameterElements.getLength(); i++) {
        Element parameterElement = (Element) parameterElements.item(i);

        Optional<String> nameOptional = XmlUtil.getChildElementText(parameterElement, "name");
        Optional<String> valueOptional = XmlUtil.getChildElementText(parameterElement, "value");

        if (nameOptional.isPresent() && valueOptional.isPresent()) {
          parameters.add(new UserDirectoryParameter(nameOptional.get(), valueOptional.get()));
        }
      }
    } catch (Throwable e) {
      throw new InvalidConfigurationException(
          "Failed to parse the XML configuration data for the user directory", e);
    }
  }

  /**
   * Set the Universally Unique Identifier (UUID) for the user directory.
   *
   * @param id the Universally Unique Identifier (UUID) for the user directory
   */
  public void setId(UUID id) {
    this.id = id;
  }

  /**
   * Set the name of the user directory.
   *
   * @param name the name of the user directory
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Set the parameters for the user directory.
   *
   * @param parameters the parameters for the user directory
   */
  public void setParameters(List<UserDirectoryParameter> parameters) {
    this.parameters = parameters;
  }

  /**
   * Set the tenants the user directory is associated with.
   *
   * @param tenants the tenants the user directory is associated with
   */
  public void setTenants(Set<Tenant> tenants) {
    this.tenants = tenants;
  }

  /**
   * Set the code for the user directory type.
   *
   * @param type the code for the user directory type
   */
  public void setType(String type) {
    this.type = type;
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
