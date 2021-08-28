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

package africa.absa.inception.cache;

import com.hazelcast.config.AwsConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizePolicy;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The <b>HazelcastServerCacheConfiguration</b> class provides the Hazelcast server cache
 * configuration.
 *
 * @author Marcus Portmann
 */
@Configuration
@ConditionalOnClass(name = "com.hazelcast.config.Config")
@ConditionalOnProperty(value = "inception.cache.hazelcast.server.enabled", havingValue = "true")
@ConfigurationProperties("inception.cache.hazelcast.server")
public class HazelcastServerCacheConfiguration {

  /** The distributed in-memory caches. */
  private List<CacheConfig> caches;

  /** The distributed in-memory cache cluster configuration. */
  private ClusterConfig cluster;

  /** Is the Hazelcast server cache enabled? */
  private boolean enabled;

  /** Is port auto increment enabled? */
  private boolean portAutoIncrement;

  /**
   * Returns the distributed in-memory caches.
   *
   * @return the distributed in-memory caches
   */
  public List<CacheConfig> getCaches() {
    return caches;
  }

  /**
   * Returns the distributed in-memory cache cluster configuration.
   *
   * @return the distributed in-memory cache cluster configuration
   */
  public ClusterConfig getCluster() {
    return cluster;
  }

  /**
   * Returns whether port auto increment is enabled.
   *
   * @return whether port auto increment is enabled
   */
  public boolean getPortAutoIncrement() {
    return portAutoIncrement;
  }

  /**
   * Returns the Hazelcast configuration.
   *
   * @return the Hazelcast configuration
   */
  @Bean(name = "hazelcastConfig")
  public Config hazelcastConfig() {
    Config config = new Config();

    config.setInstanceName(getCluster().getName());

    config.setProperty("hazelcast.logging.type", "slf4j");
    config.setProperty("hazelcast.rest.enabled", "false");

    NetworkConfig networkConfig = config.getNetworkConfig();

    networkConfig.setPort(getCluster().getPort());
    networkConfig.setPortAutoIncrement(portAutoIncrement);
    networkConfig.setReuseAddress(true);

    JoinConfig joinConfig = networkConfig.getJoin();

    MulticastConfig multicastConfig = joinConfig.getMulticastConfig();
    multicastConfig.setEnabled(false);

    AwsConfig awsConfig = joinConfig.getAwsConfig();
    awsConfig.setEnabled(false);

    TcpIpConfig tcpIpConfig = joinConfig.getTcpIpConfig();
    tcpIpConfig.setEnabled(true);

    // Add the cache members
    String[] members = {"127.0.0.1"};

    if ((getCluster().getMembers() != null) && (getCluster().getMembers().trim().length() > 0)) {
      members = getCluster().getMembers().trim().split(",");
    }

    if (members.length > 0) {
      for (String member : members) {
        tcpIpConfig.addMember(member);
      }
    }

    config.setProperty("hazelcast.application.validation.token", getCluster().getPassword());

    config.setClusterName(getCluster().getName());

    // Initialise the caches
    for (CacheConfig cacheConfig : getCaches()) {
      MapConfig mapConfig = config.getMapConfig(cacheConfig.getName());

      mapConfig.setInMemoryFormat(
          Enum.valueOf(InMemoryFormat.class, cacheConfig.getInMemoryFormat()));

      EvictionConfig evictionConfig = new EvictionConfig();
      evictionConfig.setEvictionPolicy(
          Enum.valueOf(EvictionPolicy.class, cacheConfig.getEvictionPolicy()));
      evictionConfig.setMaxSizePolicy(
          Enum.valueOf(MaxSizePolicy.class, cacheConfig.getMaxSizePolicy()));
      evictionConfig.setSize(cacheConfig.getMaxSize());

      mapConfig.setEvictionConfig(evictionConfig);

      mapConfig.setStatisticsEnabled(cacheConfig.getStatisticsEnabled());

      mapConfig.setMaxIdleSeconds(cacheConfig.getMaxIdleSeconds());

      mapConfig.setBackupCount(cacheConfig.getBackupCount());

      mapConfig.setAsyncBackupCount(cacheConfig.getAsyncBackupCount());

      mapConfig.setReadBackupData(cacheConfig.getReadBackupData());
    }

    // HazelcastInstanceFactory

    return config;
  }

  /**
   * Returns whether the Hazelcast server cache is enabled.
   *
   * @return <b>true</b> if the Hazelcast server cache is enabled or <b>false</b> otherwise
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Set the distributed in-memory caches.
   *
   * @param caches the distributed in-memory caches
   */
  public void setCaches(List<CacheConfig> caches) {
    this.caches = caches;
  }

  /**
   * Set the distributed in-memory cache cluster configuration.
   *
   * @param cluster the distributed in-memory cache cluster configuration
   */
  public void setCluster(ClusterConfig cluster) {
    this.cluster = cluster;
  }

  /**
   * Set whether the Hazelcast server cache is enabled.
   *
   * @param enabled <b>true</b> if the Hazelcast server cache is enabled or <b>false</b> otherwise
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Set whether port auto increment is enabled.
   *
   * @param portAutoIncrement is port auto increment enabled
   */
  public void setPortAutoIncrement(boolean portAutoIncrement) {
    this.portAutoIncrement = portAutoIncrement;
  }

  /**
   * The <b>CacheConfig</b> class provides access to the distributed in-memory cache configuration.
   */
  public static class CacheConfig {

    /** Are statistics enabled for the distributed in-memory cache? */
    boolean statisticsEnabled;

    /** The number of asynchronous backups for the distributed in-memory cache. */
    private int asyncBackupCount;

    /** The number of synchronous backups for the distributed in-memory cache. */
    private int backupCount;

    /** The eviction policy for the distributed in-memory cache. */
    private String evictionPolicy;

    /** The in-memory format for the distributed in-memory cache. */
    private String inMemoryFormat;

    /** The maximum idle seconds for the distributed in-memory cache. */
    private int maxIdleSeconds;

    /** The maximum size for the distributed in-memory cache. */
    private int maxSize;

    /** The maximum size policy for the distributed in-memory cache. */
    private String maxSizePolicy;

    /** The name of the distributed in-memory cache. */
    private String name;

    /** Is read-backup-data enabled for the distributed in-memory cache. */
    private boolean readBackupData;

    /**
     * Returns the number of asynchronous backups for the distributed in-memory cache.
     *
     * @return the number of asynchronous backups for the distributed in-memory cache
     */
    public int getAsyncBackupCount() {
      return asyncBackupCount;
    }

    /**
     * Returns the number of synchronous backups for the distributed in-memory cache.
     *
     * @return the number of synchronous backups for the distributed in-memory cache
     */
    public int getBackupCount() {
      return backupCount;
    }

    /**
     * Returns the eviction policy for the distributed in-memory cache.
     *
     * @return the eviction policy for the distributed in-memory cache
     */
    public String getEvictionPolicy() {
      return evictionPolicy;
    }

    /**
     * Returns the in-memory format for the distributed in-memory cache.
     *
     * @return the in-memory format for the distributed in-memory cache
     */
    public String getInMemoryFormat() {
      return inMemoryFormat;
    }

    /**
     * Returns the maximum idle seconds for the distributed in-memory cache.
     *
     * @return the maximum idle seconds for the distributed in-memory cache
     */
    public int getMaxIdleSeconds() {
      return maxIdleSeconds;
    }

    /**
     * Returns the maximum size for the distributed in-memory cache.
     *
     * @return the maximum size for the distributed in-memory cache
     */
    public int getMaxSize() {
      return maxSize;
    }

    /**
     * Returns the maximum size policy for the distributed in-memory cache.
     *
     * @return the maximum size policy for the distributed in-memory cache
     */
    public String getMaxSizePolicy() {
      return maxSizePolicy;
    }

    /**
     * Returns the name of the distributed in-memory cache.
     *
     * @return the name of the distributed in-memory cache
     */
    public String getName() {
      return name;
    }

    /**
     * Returns whether read-backup-data is enabled for the distributed in-memory cache.
     *
     * @return <b>true</b> if read-backup-data is enabled for the distributed in-memory cache or
     *     <b>false</b> otherwise
     */
    public boolean getReadBackupData() {
      return readBackupData;
    }

    /**
     * Returns whether statistics are enabled for the distributed in-memory cache?
     *
     * @return <b>true</b> if statistics are enabled for the distributed in-memory cache or
     *     <b>false</b> otherwise
     */
    public boolean getStatisticsEnabled() {
      return statisticsEnabled;
    }

    /**
     * Set the number of asynchronous backups for the distributed in-memory cache.
     *
     * @param asyncBackupCount the number of asynchronous backups for the distributed in-memory
     *     cache
     */
    public void setAsyncBackupCount(int asyncBackupCount) {
      this.asyncBackupCount = asyncBackupCount;
    }

    /**
     * Set the number of synchronous backups for the distributed in-memory cache.
     *
     * @param backupCount the number of synchronous backups for the distributed in-memory cache
     */
    public void setBackupCount(int backupCount) {
      this.backupCount = backupCount;
    }

    /**
     * Set the eviction policy for the distributed in-memory cache.
     *
     * @param evictionPolicy the eviction policy for the distributed in-memory cache
     */
    public void setEvictionPolicy(String evictionPolicy) {
      this.evictionPolicy = evictionPolicy;
    }

    /**
     * Set the in-memory format for the distributed in-memory cache.
     *
     * @param inMemoryFormat the in-memory format for the distributed in-memory cache
     */
    public void setInMemoryFormat(String inMemoryFormat) {
      this.inMemoryFormat = inMemoryFormat;
    }

    /**
     * Set the maximum idle seconds for the distributed in-memory cache.
     *
     * @param maxIdleSeconds the maximum idle seconds for the distributed in-memory cache
     */
    public void setMaxIdleSeconds(int maxIdleSeconds) {
      this.maxIdleSeconds = maxIdleSeconds;
    }

    /**
     * Set the maximum size for the distributed in-memory cache.
     *
     * @param maxSize the maximum size for the distributed in-memory cache
     */
    public void setMaxSize(int maxSize) {
      this.maxSize = maxSize;
    }

    /**
     * Set the maximum size policy for the distributed in-memory cache.
     *
     * @param maxSizePolicy the maximum size policy for the distributed in-memory cache
     */
    public void setMaxSizePolicy(String maxSizePolicy) {
      this.maxSizePolicy = maxSizePolicy;
    }

    /**
     * Set the name of the distributed in-memory cache.
     *
     * @param name the name of the distributed in-memory cache
     */
    public void setName(String name) {
      this.name = name;
    }

    /**
     * Set whether read-backup-data is enabled for the distributed in-memory cache.
     *
     * @param readBackupData <b>true</b> if read-backup-data enabled for the distributed in-memory
     *     cache or <b>false</b> otherwise
     */
    public void setReadBackupData(boolean readBackupData) {
      this.readBackupData = readBackupData;
    }

    /**
     * Set whether statistics are enabled for the distributed in-memory cache.
     *
     * @param statisticsEnabled <b>true</b> if statistics are enabled for the distributed in-memory
     *     cache or <b>false</b> otherwise
     */
    public void setStatisticsEnabled(boolean statisticsEnabled) {
      this.statisticsEnabled = statisticsEnabled;
    }
  }

  /**
   * The <b>ClusterConfig</b> class provides access to the distributed in-memory cache cluster
   * configuration.
   */
  public static class ClusterConfig {

    /**
     * The comma-delimited IP addresses or hostnames for the members of the distributed in-memory
     * cache cluster.
     */
    private String members;

    /** The name of the distributed in-memory cache cluster. */
    private String name;

    /** The password used to connect to the distributed in-memory cache cluster. */
    private String password;

    /** The port for the distributed in-memory cache cluster. */
    private int port;

    /**
     * Returns the comma-delimited IP addresses or hostnames for the members of the distributed
     * in-memory cache cluster.
     *
     * @return the comma-delimited IP addresses or hostnames for the members of the distributed
     *     in-memory cache cluster
     */
    public String getMembers() {
      return members;
    }

    /**
     * Returns the name of the distributed in-memory cache cluster.
     *
     * @return the name of the distributed in-memory cache cluster
     */
    public String getName() {
      return name;
    }

    /**
     * Returns the password used to connect to the distributed in-memory cache cluster.
     *
     * @return the password used to connect to the distributed in-memory cache cluster
     */
    public String getPassword() {
      return password;
    }

    /**
     * Returns the port for the distributed in-memory cache cluster.
     *
     * @return the port for the distributed in-memory cache cluster
     */
    public int getPort() {
      return port;
    }

    /**
     * Set the comma-delimited IP addresses or hostnames for the members of the distributed
     * in-memory cache cluster.
     *
     * @param members the comma-delimited IP addresses or hostnames for the members of the
     *     distributed in-memory cache cluster
     */
    public void setMembers(String members) {
      this.members = members;
    }

    /**
     * Set the name of the distributed in-memory cache cluster.
     *
     * @param name the name of the distributed in-memory cache cluster
     */
    public void setName(String name) {
      this.name = name;
    }

    /**
     * Set the password used to connect to the distributed in-memory cache cluster.
     *
     * @param password the password used to connect to the distributed in-memory cache cluster
     */
    public void setPassword(String password) {
      this.password = password;
    }

    /**
     * Set the port for the distributed in-memory cache cluster.
     *
     * @param port the port for the distributed in-memory cache cluster
     */
    public void setPort(int port) {
      this.port = port;
    }
  }
}
