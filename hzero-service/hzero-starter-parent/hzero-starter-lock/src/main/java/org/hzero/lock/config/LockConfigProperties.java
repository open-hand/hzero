package org.hzero.lock.config;

import org.hzero.lock.constant.LockConstant;
import org.hzero.lock.enums.ServerPattern;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 锁配置属性类
 *
 * @author xianzhi.chen@hand-china.com 2019年1月14日下午5:12:30
 */
@ConfigurationProperties(prefix = LockConfigProperties.PREFIX)
public class LockConfigProperties {

    public static final String PREFIX = "hzero.lock";

    /**
     * Redis的运行模式,默认使用单机模式
     */
    private String pattern = ServerPattern.SINGLE.getPattern();
    /**
     * 单节点模式
     */
    private SingleConfig singleServer;
    /**
     * 集群模式
     */
    private ClusterConfig clusterServer;
    /**
     * 云托管模式
     */
    private ReplicatedConfig replicatedServer;
    /**
     * 哨兵模式
     */
    private SentinelConfig sentinelServer;
    /**
     * 主从模式
     */
    private MasterSlaveConfig masterSlaveServer;
    /**
     * 客户端名称
     */
    private String clientName = LockConstant.LOCK_CLIENT_NAME;
    /**
     * 启用SSL终端识别
     */
    private boolean sslEnableEndpointIdentification = false;
    /**
     * SSL实现方式，确定采用哪种方式（JDK或OPENSSL）来实现SSL连接
     */
    private String sslProvider = LockConstant.JDK;
    /**
     * SSL信任证书库路径
     */
    private String sslTruststore;
    /**
     * SSL信任证书库密码
     */
    private String sslTruststorePassword;
    /**
     * SSL钥匙库路径
     */
    private String sslKeystore;
    /**
     * SSL钥匙库密码
     */
    private String sslKeystorePassword;
    /**
     * RTopic共享线程数量
     */
    private int threads = 0;
    /**
     * Redisson使用的所有redis客户端之间共享的线程数量
     */
    private int nettyThreads = 0;
    /**
     * 仅在没有leaseTimeout参数定义的情况下获取锁定时才使用此参数。看门狗过期时间
     */
    private long lockWatchdogTimeout = 30000;
    /**
     * 是否串行处理消息
     */
    private boolean keepPubSubOrder = true;
    /**
     * 否在Redis端使用Lua脚本缓存
     */
    private boolean useScriptCache = false;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public boolean isSslEnableEndpointIdentification() {
        return sslEnableEndpointIdentification;
    }

    public void setSslEnableEndpointIdentification(boolean sslEnableEndpointIdentification) {
        this.sslEnableEndpointIdentification = sslEnableEndpointIdentification;
    }

    public String getSslProvider() {
        return sslProvider;
    }

    public void setSslProvider(String sslProvider) {
        this.sslProvider = sslProvider;
    }

    public String getSslTruststore() {
        return sslTruststore;
    }

    public void setSslTruststore(String sslTruststore) {
        this.sslTruststore = sslTruststore;
    }

    public String getSslTruststorePassword() {
        return sslTruststorePassword;
    }

    public void setSslTruststorePassword(String sslTruststorePassword) {
        this.sslTruststorePassword = sslTruststorePassword;
    }

    public String getSslKeystore() {
        return sslKeystore;
    }

    public void setSslKeystore(String sslKeystore) {
        this.sslKeystore = sslKeystore;
    }

    public String getSslKeystorePassword() {
        return sslKeystorePassword;
    }

    public void setSslKeystorePassword(String sslKeystorePassword) {
        this.sslKeystorePassword = sslKeystorePassword;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public SingleConfig getSingleServer() {
        return singleServer;
    }

    public void setSingleServer(SingleConfig singleServer) {
        this.singleServer = singleServer;
    }

    public ClusterConfig getClusterServer() {
        return clusterServer;
    }

    public void setClusterServer(ClusterConfig clusterServer) {
        this.clusterServer = clusterServer;
    }

    public ReplicatedConfig getReplicatedServer() {
        return replicatedServer;
    }

    public void setReplicatedServer(ReplicatedConfig replicatedServer) {
        this.replicatedServer = replicatedServer;
    }

    public SentinelConfig getSentinelServer() {
        return sentinelServer;
    }

    public void setSentinelServer(SentinelConfig sentinelServer) {
        this.sentinelServer = sentinelServer;
    }

    public MasterSlaveConfig getMasterSlaveServer() {
        return masterSlaveServer;
    }

    public void setMasterSlaveServer(MasterSlaveConfig masterSlaveServer) {
        this.masterSlaveServer = masterSlaveServer;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public int getNettyThreads() {
        return nettyThreads;
    }

    public void setNettyThreads(int nettyThreads) {
        this.nettyThreads = nettyThreads;
    }

    public long getLockWatchdogTimeout() {
        return lockWatchdogTimeout;
    }

    public void setLockWatchdogTimeout(long lockWatchdogTimeout) {
        this.lockWatchdogTimeout = lockWatchdogTimeout;
    }

    public boolean getKeepPubSubOrder() {
        return keepPubSubOrder;
    }

    public void setKeepPubSubOrder(boolean keepPubSubOrder) {
        this.keepPubSubOrder = keepPubSubOrder;
    }

    public boolean getUseScriptCache() {
        return useScriptCache;
    }

    public void setUseScriptCache(boolean useScriptCache) {
        this.useScriptCache = useScriptCache;
    }

    /**
     * 主从模式
     *
     * @author xianzhi.chen@hand-china.com 2019年4月4日下午2:26:31
     */
    public static class MasterSlaveConfig {
        /**
         * DNS监控间隔，单位:毫秒
         */
        private long dnsMonitoringInterval = 5000;
        /**
         * 主节点地址，可以通过host:port的格式来指定主节点地址。
         */
        private String masterAddress;
        /**
         * 从节点地址
         */
        private String slaveAddresses;
        /**
         * 读取操作的负载均衡模式
         */
        private String readMode = LockConstant.SubReadMode.SLAVE;
        /**
         * 订阅操作的负载均衡模式
         */
        private String subMode = LockConstant.SubReadMode.SLAVE;
        /**
         * 负载均衡算法类的选择，默认：轮询调度算法
         */
        private String loadBalancer = LockConstant.LoadBalancer.ROUND_ROBIN_LOAD_BALANCER;
        /**
         * 默认权重值，当负载均衡算法是权重轮询调度算法时该属性有效
         */
        private int defaultWeight = 0;
        /**
         * 权重值设置，格式为 host1:port1,权重值1;host2:port2,权重值2 当负载均衡算法是权重轮询调度算法时该属性有效
         */
        private String weightMaps;
        /**
         * 从节点发布和订阅连接的最小空闲连接数
         */
        private int subscriptionConnectionMinimumIdleSize = 1;
        /**
         * 从节点发布和订阅连接池大小
         */
        private int subscriptionConnectionPoolSize = 50;
        /**
         * 从节点最小空闲连接数
         */
        private int slaveConnectionMinimumIdleSize = 32;
        /**
         * 从节点连接池大小
         */
        private int slaveConnectionPoolSize = 64;
        /**
         * 主节点最小空闲连接数
         */
        private int masterConnectionMinimumIdleSize = 32;
        /**
         * 主节点连接池大小
         */
        private int masterConnectionPoolSize = 64;
        /**
         * 连接空闲超时，单位：毫秒
         */
        private int idleConnectionTimeout = 10000;
        /**
         * 连接超时，单位：毫秒
         */
        private int connectTimeout = 10000;
        /**
         * 心跳间隔，单位：毫秒，默认值0表示关闭心跳
         */
        private int pingConnectionInterval = 0;
        /**
         * 命令等待超时，单位：毫秒
         */
        private int timeout = 3000;
        /**
         * 命令失败重试次数
         */
        private int retryAttempts = 3;
        /**
         * 命令重试发送时间间隔，单位：毫秒
         */
        private int retryInterval = 1500;
        /**
         * 重新连接时间间隔，单位：毫秒
         */
        private int reconnectionTimeout = 3000;
        /**
         * 执行失败最大次数
         */
        private int failedAttempts = 3;
        /**
         * 数据库编号
         */
        private int database = 0;
        /**
         * 密码，用于节点身份验证的密码
         */
        private String password;
        /**
         * 单个连接最大订阅数量
         */
        private int subscriptionsPerConnection = 5;

        public long getDnsMonitoringInterval() {
            return dnsMonitoringInterval;
        }

        public void setDnsMonitoringInterval(long dnsMonitoringInterval) {
            this.dnsMonitoringInterval = dnsMonitoringInterval;
        }

        public String getMasterAddress() {
            return masterAddress;
        }

        public void setMasterAddress(String masterAddress) {
            this.masterAddress = masterAddress;
        }

        public String getSlaveAddresses() {
            return slaveAddresses;
        }

        public void setSlaveAddresses(String slaveAddresses) {
            this.slaveAddresses = slaveAddresses;
        }

        public String getReadMode() {
            return readMode;
        }

        public void setReadMode(String readMode) {
            this.readMode = readMode;
        }

        public String getSubMode() {
            return subMode;
        }

        public void setSubMode(String subMode) {
            this.subMode = subMode;
        }

        public String getLoadBalancer() {
            return loadBalancer;
        }

        public void setLoadBalancer(String loadBalancer) {
            this.loadBalancer = loadBalancer;
        }

        public int getDefaultWeight() {
            return defaultWeight;
        }

        public void setDefaultWeight(int defaultWeight) {
            this.defaultWeight = defaultWeight;
        }

        public String getWeightMaps() {
            return weightMaps;
        }

        public void setWeightMaps(String weightMaps) {
            this.weightMaps = weightMaps;
        }

        public int getSubscriptionConnectionMinimumIdleSize() {
            return subscriptionConnectionMinimumIdleSize;
        }

        public void setSubscriptionConnectionMinimumIdleSize(int subscriptionConnectionMinimumIdleSize) {
            this.subscriptionConnectionMinimumIdleSize = subscriptionConnectionMinimumIdleSize;
        }

        public int getSubscriptionConnectionPoolSize() {
            return subscriptionConnectionPoolSize;
        }

        public void setSubscriptionConnectionPoolSize(int subscriptionConnectionPoolSize) {
            this.subscriptionConnectionPoolSize = subscriptionConnectionPoolSize;
        }

        public int getSlaveConnectionMinimumIdleSize() {
            return slaveConnectionMinimumIdleSize;
        }

        public void setSlaveConnectionMinimumIdleSize(int slaveConnectionMinimumIdleSize) {
            this.slaveConnectionMinimumIdleSize = slaveConnectionMinimumIdleSize;
        }

        public int getSlaveConnectionPoolSize() {
            return slaveConnectionPoolSize;
        }

        public void setSlaveConnectionPoolSize(int slaveConnectionPoolSize) {
            this.slaveConnectionPoolSize = slaveConnectionPoolSize;
        }

        public int getMasterConnectionMinimumIdleSize() {
            return masterConnectionMinimumIdleSize;
        }

        public void setMasterConnectionMinimumIdleSize(int masterConnectionMinimumIdleSize) {
            this.masterConnectionMinimumIdleSize = masterConnectionMinimumIdleSize;
        }

        public int getMasterConnectionPoolSize() {
            return masterConnectionPoolSize;
        }

        public void setMasterConnectionPoolSize(int masterConnectionPoolSize) {
            this.masterConnectionPoolSize = masterConnectionPoolSize;
        }

        public int getIdleConnectionTimeout() {
            return idleConnectionTimeout;
        }

        public void setIdleConnectionTimeout(int idleConnectionTimeout) {
            this.idleConnectionTimeout = idleConnectionTimeout;
        }

        public int getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public int getPingConnectionInterval() {
            return pingConnectionInterval;
        }

        public MasterSlaveConfig setPingConnectionInterval(int pingConnectionInterval) {
            this.pingConnectionInterval = pingConnectionInterval;
            return this;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public int getRetryAttempts() {
            return retryAttempts;
        }

        public void setRetryAttempts(int retryAttempts) {
            this.retryAttempts = retryAttempts;
        }

        public int getRetryInterval() {
            return retryInterval;
        }

        public void setRetryInterval(int retryInterval) {
            this.retryInterval = retryInterval;
        }

        public int getReconnectionTimeout() {
            return reconnectionTimeout;
        }

        public void setReconnectionTimeout(int reconnectionTimeout) {
            this.reconnectionTimeout = reconnectionTimeout;
        }

        public int getFailedAttempts() {
            return failedAttempts;
        }

        public void setFailedAttempts(int failedAttempts) {
            this.failedAttempts = failedAttempts;
        }

        public int getDatabase() {
            return database;
        }

        public void setDatabase(int database) {
            this.database = database;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getSubscriptionsPerConnection() {
            return subscriptionsPerConnection;
        }

        public void setSubscriptionsPerConnection(int subscriptionsPerConnection) {
            this.subscriptionsPerConnection = subscriptionsPerConnection;
        }
    }

    public static class SentinelConfig {
        /**
         * DNS监控间隔，单位：毫秒；用-1来禁用该功能。
         */
        private long dnsMonitoringInterval = 5000;
        /**
         * 主服务器的名称
         */
        private String masterName;
        /**
         * 哨兵节点地址，多个节点可以一次性批量添加。
         */
        private String sentinelAddresses;
        /**
         * 读取操作的负载均衡模式
         */
        private String readMode = LockConstant.SubReadMode.SLAVE;
        /**
         * 订阅操作的负载均衡模式
         */
        private String subMode = LockConstant.SubReadMode.SLAVE;
        /**
         * 负载均衡算法类的选择，默认：轮询调度算法
         */
        private String loadBalancer = LockConstant.LoadBalancer.ROUND_ROBIN_LOAD_BALANCER;
        /**
         * 默认权重值，当负载均衡算法是权重轮询调度算法时该属性有效
         */
        private int defaultWeight = 0;
        /**
         * 权重值设置，格式为 host1:port1,权重值1;host2:port2,权重值2 当负载均衡算法是权重轮询调度算法时该属性有效
         */
        private String weightMaps;
        /**
         * 从节点发布和订阅连接的最小空闲连接数
         */
        private int subscriptionConnectionMinimumIdleSize = 1;
        /**
         * 从节点发布和订阅连接池大小
         */
        private int subscriptionConnectionPoolSize = 50;
        /**
         * 从节点最小空闲连接数
         */
        private int slaveConnectionMinimumIdleSize = 32;
        /**
         * 从节点连接池大小
         */
        private int slaveConnectionPoolSize = 64;
        /**
         * 主节点最小空闲连接数
         */
        private int masterConnectionMinimumIdleSize = 32;
        /**
         * 主节点连接池大小
         */
        private int masterConnectionPoolSize = 64;
        /**
         * 连接空闲超时，单位：毫秒
         */
        private int idleConnectionTimeout = 10000;
        /**
         * 连接超时，单位：毫秒
         */
        private int connectTimeout = 10000;
        /**
         * 心跳间隔，单位：毫秒，默认值0表示关闭心跳
         */
        private int pingConnectionInterval = 0;
        /**
         * 命令等待超时，单位：毫秒
         */
        private int timeout = 3000;
        /**
         * 命令失败重试次数
         */
        private int retryAttempts = 3;
        /**
         * 命令重试发送时间间隔，单位：毫秒
         */
        private int retryInterval = 1500;
        /**
         * 数据库编号
         */
        private int database = 0;
        /**
         * 密码，用于节点身份验证的密码
         */
        private String password;
        /**
         * 单个连接最大订阅数量
         */
        private int subscriptionsPerConnection = 5;

        public long getDnsMonitoringInterval() {
            return dnsMonitoringInterval;
        }

        public void setDnsMonitoringInterval(long dnsMonitoringInterval) {
            this.dnsMonitoringInterval = dnsMonitoringInterval;
        }

        public String getMasterName() {
            return masterName;
        }

        public void setMasterName(String masterName) {
            this.masterName = masterName;
        }

        public String getSentinelAddresses() {
            return sentinelAddresses;
        }

        public void setSentinelAddresses(String sentinelAddresses) {
            this.sentinelAddresses = sentinelAddresses;
        }

        public String getReadMode() {
            return readMode;
        }

        public void setReadMode(String readMode) {
            this.readMode = readMode;
        }

        public String getSubMode() {
            return subMode;
        }

        public void setSubMode(String subMode) {
            this.subMode = subMode;
        }

        public String getLoadBalancer() {
            return loadBalancer;
        }

        public void setLoadBalancer(String loadBalancer) {
            this.loadBalancer = loadBalancer;
        }

        public int getDefaultWeight() {
            return defaultWeight;
        }

        public void setDefaultWeight(int defaultWeight) {
            this.defaultWeight = defaultWeight;
        }

        public String getWeightMaps() {
            return weightMaps;
        }

        public void setWeightMaps(String weightMaps) {
            this.weightMaps = weightMaps;
        }

        public int getSubscriptionConnectionMinimumIdleSize() {
            return subscriptionConnectionMinimumIdleSize;
        }

        public void setSubscriptionConnectionMinimumIdleSize(int subscriptionConnectionMinimumIdleSize) {
            this.subscriptionConnectionMinimumIdleSize = subscriptionConnectionMinimumIdleSize;
        }

        public int getSubscriptionConnectionPoolSize() {
            return subscriptionConnectionPoolSize;
        }

        public void setSubscriptionConnectionPoolSize(int subscriptionConnectionPoolSize) {
            this.subscriptionConnectionPoolSize = subscriptionConnectionPoolSize;
        }

        public int getSlaveConnectionMinimumIdleSize() {
            return slaveConnectionMinimumIdleSize;
        }

        public void setSlaveConnectionMinimumIdleSize(int slaveConnectionMinimumIdleSize) {
            this.slaveConnectionMinimumIdleSize = slaveConnectionMinimumIdleSize;
        }

        public int getSlaveConnectionPoolSize() {
            return slaveConnectionPoolSize;
        }

        public void setSlaveConnectionPoolSize(int slaveConnectionPoolSize) {
            this.slaveConnectionPoolSize = slaveConnectionPoolSize;
        }

        public int getMasterConnectionMinimumIdleSize() {
            return masterConnectionMinimumIdleSize;
        }

        public void setMasterConnectionMinimumIdleSize(int masterConnectionMinimumIdleSize) {
            this.masterConnectionMinimumIdleSize = masterConnectionMinimumIdleSize;
        }

        public int getMasterConnectionPoolSize() {
            return masterConnectionPoolSize;
        }

        public void setMasterConnectionPoolSize(int masterConnectionPoolSize) {
            this.masterConnectionPoolSize = masterConnectionPoolSize;
        }

        public int getIdleConnectionTimeout() {
            return idleConnectionTimeout;
        }

        public void setIdleConnectionTimeout(int idleConnectionTimeout) {
            this.idleConnectionTimeout = idleConnectionTimeout;
        }

        public int getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public int getPingConnectionInterval() {
            return pingConnectionInterval;
        }

        public SentinelConfig setPingConnectionInterval(int pingConnectionInterval) {
            this.pingConnectionInterval = pingConnectionInterval;
            return this;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public int getRetryAttempts() {
            return retryAttempts;
        }

        public void setRetryAttempts(int retryAttempts) {
            this.retryAttempts = retryAttempts;
        }

        public int getRetryInterval() {
            return retryInterval;
        }

        public void setRetryInterval(int retryInterval) {
            this.retryInterval = retryInterval;
        }

        public int getDatabase() {
            return database;
        }

        public void setDatabase(int database) {
            this.database = database;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getSubscriptionsPerConnection() {
            return subscriptionsPerConnection;
        }

        public void setSubscriptionsPerConnection(int subscriptionsPerConnection) {
            this.subscriptionsPerConnection = subscriptionsPerConnection;
        }
    }

    public static class ReplicatedConfig {
        /**
         * 集群节点地址
         */
        private String nodeAddresses;
        /**
         * 主节点变化扫描间隔时间
         */
        private int scanInterval = 1000;
        /**
         * DNS监控间隔，单位：毫秒；用-1来禁用该功能。
         */
        private long dnsMonitoringInterval = 5000;
        /**
         * 读取操作的负载均衡模式
         */
        private String readMode = LockConstant.SubReadMode.SLAVE;
        /**
         * 订阅操作的负载均衡模式
         */
        private String subscriptionMode = LockConstant.SubReadMode.SLAVE;
        /**
         * 负载均衡算法类的选择，默认：轮询调度算法
         */
        private String loadBalancer = LockConstant.LoadBalancer.ROUND_ROBIN_LOAD_BALANCER;
        /**
         * 默认权重值，当负载均衡算法是权重轮询调度算法时该属性有效
         */
        private int defaultWeight = 0;
        /**
         * 权重值设置，格式为 host1:port1,权重值1;host2:port2,权重值2 当负载均衡算法是权重轮询调度算法时该属性有效
         */
        private String weightMaps;
        /**
         * 从节点发布和订阅连接的最小空闲连接数
         */
        private int subscriptionConnectionMinimumIdleSize = 1;
        /**
         * 从节点发布和订阅连接池大小
         */
        private int subscriptionConnectionPoolSize = 50;
        /**
         * 从节点最小空闲连接数
         */
        private int slaveConnectionMinimumIdleSize = 32;
        /**
         * 从节点连接池大小
         */
        private int slaveConnectionPoolSize = 64;
        /**
         * 主节点最小空闲连接数
         */
        private int masterConnectionMinimumIdleSize = 32;
        /**
         * 主节点连接池大小
         */
        private int masterConnectionPoolSize = 64;
        /**
         * 连接空闲超时，单位：毫秒
         */
        private int idleConnectionTimeout = 10000;
        /**
         * 连接超时，单位：毫秒
         */
        private int connectTimeout = 10000;
        /**
         * 心跳间隔，单位：毫秒，默认值0表示关闭心跳
         */
        private int pingConnectionInterval = 0;
        /**
         * 命令等待超时，单位：毫秒
         */
        private int timeout = 3000;
        /**
         * 命令失败重试次数
         */
        private int retryAttempts = 3;
        /**
         * 命令重试发送时间间隔，单位：毫秒
         */
        private int retryInterval = 1500;
        /**
         * 数据库编号
         */
        private int database = 0;
        /**
         * 密码，用于节点身份验证的密码
         */
        private String password;
        /**
         * 单个连接最大订阅数量
         */
        private int subscriptionsPerConnection = 5;

        public String getNodeAddresses() {
            return nodeAddresses;
        }

        public void setNodeAddresses(String nodeAddresses) {
            this.nodeAddresses = nodeAddresses;
        }

        public int getScanInterval() {
            return scanInterval;
        }

        public void setScanInterval(int scanInterval) {
            this.scanInterval = scanInterval;
        }

        public long getDnsMonitoringInterval() {
            return dnsMonitoringInterval;
        }

        public void setDnsMonitoringInterval(long dnsMonitoringInterval) {
            this.dnsMonitoringInterval = dnsMonitoringInterval;
        }

        public String getReadMode() {
            return readMode;
        }

        public void setReadMode(String readMode) {
            this.readMode = readMode;
        }

        public String getSubscriptionMode() {
            return subscriptionMode;
        }

        public void setSubscriptionMode(String subscriptionMode) {
            this.subscriptionMode = subscriptionMode;
        }

        public String getLoadBalancer() {
            return loadBalancer;
        }

        public void setLoadBalancer(String loadBalancer) {
            this.loadBalancer = loadBalancer;
        }

        public int getDefaultWeight() {
            return defaultWeight;
        }

        public void setDefaultWeight(int defaultWeight) {
            this.defaultWeight = defaultWeight;
        }

        public String getWeightMaps() {
            return weightMaps;
        }

        public void setWeightMaps(String weightMaps) {
            this.weightMaps = weightMaps;
        }

        public int getSubscriptionConnectionMinimumIdleSize() {
            return subscriptionConnectionMinimumIdleSize;
        }

        public void setSubscriptionConnectionMinimumIdleSize(int subscriptionConnectionMinimumIdleSize) {
            this.subscriptionConnectionMinimumIdleSize = subscriptionConnectionMinimumIdleSize;
        }

        public int getSubscriptionConnectionPoolSize() {
            return subscriptionConnectionPoolSize;
        }

        public void setSubscriptionConnectionPoolSize(int subscriptionConnectionPoolSize) {
            this.subscriptionConnectionPoolSize = subscriptionConnectionPoolSize;
        }

        public int getSlaveConnectionMinimumIdleSize() {
            return slaveConnectionMinimumIdleSize;
        }

        public void setSlaveConnectionMinimumIdleSize(int slaveConnectionMinimumIdleSize) {
            this.slaveConnectionMinimumIdleSize = slaveConnectionMinimumIdleSize;
        }

        public int getSlaveConnectionPoolSize() {
            return slaveConnectionPoolSize;
        }

        public void setSlaveConnectionPoolSize(int slaveConnectionPoolSize) {
            this.slaveConnectionPoolSize = slaveConnectionPoolSize;
        }

        public int getMasterConnectionMinimumIdleSize() {
            return masterConnectionMinimumIdleSize;
        }

        public void setMasterConnectionMinimumIdleSize(int masterConnectionMinimumIdleSize) {
            this.masterConnectionMinimumIdleSize = masterConnectionMinimumIdleSize;
        }

        public int getMasterConnectionPoolSize() {
            return masterConnectionPoolSize;
        }

        public void setMasterConnectionPoolSize(int masterConnectionPoolSize) {
            this.masterConnectionPoolSize = masterConnectionPoolSize;
        }

        public int getIdleConnectionTimeout() {
            return idleConnectionTimeout;
        }

        public void setIdleConnectionTimeout(int idleConnectionTimeout) {
            this.idleConnectionTimeout = idleConnectionTimeout;
        }

        public int getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public int getPingConnectionInterval() {
            return pingConnectionInterval;
        }

        public ReplicatedConfig setPingConnectionInterval(int pingConnectionInterval) {
            this.pingConnectionInterval = pingConnectionInterval;
            return this;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public int getRetryAttempts() {
            return retryAttempts;
        }

        public void setRetryAttempts(int retryAttempts) {
            this.retryAttempts = retryAttempts;
        }

        public int getRetryInterval() {
            return retryInterval;
        }

        public void setRetryInterval(int retryInterval) {
            this.retryInterval = retryInterval;
        }

        public int getDatabase() {
            return database;
        }

        public void setDatabase(int database) {
            this.database = database;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getSubscriptionsPerConnection() {
            return subscriptionsPerConnection;
        }

        public void setSubscriptionsPerConnection(int subscriptionsPerConnection) {
            this.subscriptionsPerConnection = subscriptionsPerConnection;
        }
    }

    public static class ClusterConfig {
        /**
         * 集群节点地址
         */
        private String nodeAddresses;
        /**
         * 集群扫描间隔时间
         */
        private int scanInterval = 1000;
        /**
         * 读取操作的负载均衡模式
         */
        private String readMode = LockConstant.SubReadMode.SLAVE;
        /**
         * 订阅操作的负载均衡模式
         */
        private String subMode = LockConstant.SubReadMode.SLAVE;
        /**
         * 负载均衡算法类的选择，默认：轮询调度算法
         */
        private String loadBalancer = LockConstant.LoadBalancer.ROUND_ROBIN_LOAD_BALANCER;
        /**
         * 默认权重值，当负载均衡算法是权重轮询调度算法时该属性有效
         */
        private int defaultWeight = 0;
        /**
         * 权重值设置，格式为 host1:port1,权重值1;host2:port2,权重值2 当负载均衡算法是权重轮询调度算法时该属性有效
         */
        private String weightMaps;
        /**
         * 从节点发布和订阅连接的最小空闲连接数
         */
        private int subConnMinIdleSize = 1;
        /**
         * 从节点发布和订阅连接池大小
         */
        private int subConnPoolSize = 50;
        /**
         * 从节点最小空闲连接数
         */
        private int slaveConnMinIdleSize = 32;
        /**
         * 从节点连接池大小
         */
        private int slaveConnPoolSize = 64;
        /**
         * 主节点最小空闲连接数
         */
        private int masterConnMinIdleSize = 32;
        /**
         * 主节点连接池大小
         */
        private int masterConnPoolSize = 64;
        /**
         * 连接空闲超时，单位：毫秒
         */
        private int idleConnTimeout = 10000;
        /**
         * 连接超时，单位：毫秒
         */
        private int connTimeout = 10000;
        /**
         * 心跳间隔，单位：毫秒，默认值0表示关闭心跳
         */
        private int pingConnectionInterval = 0;
        /**
         * 命令等待超时，单位：毫秒
         */
        private int timeout = 3000;
        /**
         * 命令失败重试次数
         */
        private int retryAttempts = 3;
        /**
         * 命令重试发送时间间隔，单位：毫秒
         */
        private int retryInterval = 1500;
        /**
         * 密码
         */
        private String password;
        /**
         * 单个连接最大订阅数量
         */
        private int subPerConn = 5;

        public String getNodeAddresses() {
            return nodeAddresses;
        }

        public void setNodeAddresses(String nodeAddresses) {
            this.nodeAddresses = nodeAddresses;
        }

        public int getScanInterval() {
            return scanInterval;
        }

        public void setScanInterval(int scanInterval) {
            this.scanInterval = scanInterval;
        }

        public String getReadMode() {
            return readMode;
        }

        public void setReadMode(String readMode) {
            this.readMode = readMode;
        }

        public String getSubMode() {
            return subMode;
        }

        public void setSubMode(String subMode) {
            this.subMode = subMode;
        }

        public String getLoadBalancer() {
            return loadBalancer;
        }

        public void setLoadBalancer(String loadBalancer) {
            this.loadBalancer = loadBalancer;
        }

        public int getSubConnMinIdleSize() {
            return subConnMinIdleSize;
        }

        public void setSubConnMinIdleSize(int subConnMinIdleSize) {
            this.subConnMinIdleSize = subConnMinIdleSize;
        }

        public int getSubConnPoolSize() {
            return subConnPoolSize;
        }

        public void setSubConnPoolSize(int subConnPoolSize) {
            this.subConnPoolSize = subConnPoolSize;
        }

        public int getSlaveConnMinIdleSize() {
            return slaveConnMinIdleSize;
        }

        public void setSlaveConnMinIdleSize(int slaveConnMinIdleSize) {
            this.slaveConnMinIdleSize = slaveConnMinIdleSize;
        }

        public int getSlaveConnPoolSize() {
            return slaveConnPoolSize;
        }

        public void setSlaveConnPoolSize(int slaveConnPoolSize) {
            this.slaveConnPoolSize = slaveConnPoolSize;
        }

        public int getMasterConnMinIdleSize() {
            return masterConnMinIdleSize;
        }

        public void setMasterConnMinIdleSize(int masterConnMinIdleSize) {
            this.masterConnMinIdleSize = masterConnMinIdleSize;
        }

        public int getMasterConnPoolSize() {
            return masterConnPoolSize;
        }

        public void setMasterConnPoolSize(int masterConnPoolSize) {
            this.masterConnPoolSize = masterConnPoolSize;
        }

        public int getIdleConnTimeout() {
            return idleConnTimeout;
        }

        public void setIdleConnTimeout(int idleConnTimeout) {
            this.idleConnTimeout = idleConnTimeout;
        }

        public int getConnTimeout() {
            return connTimeout;
        }

        public void setConnTimeout(int connTimeout) {
            this.connTimeout = connTimeout;
        }

        public int getPingConnectionInterval() {
            return pingConnectionInterval;
        }

        public ClusterConfig setPingConnectionInterval(int pingConnectionInterval) {
            this.pingConnectionInterval = pingConnectionInterval;
            return this;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public int getRetryAttempts() {
            return retryAttempts;
        }

        public void setRetryAttempts(int retryAttempts) {
            this.retryAttempts = retryAttempts;
        }

        public int getRetryInterval() {
            return retryInterval;
        }

        public void setRetryInterval(int retryInterval) {
            this.retryInterval = retryInterval;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getSubPerConn() {
            return subPerConn;
        }

        public void setSubPerConn(int subPerConn) {
            this.subPerConn = subPerConn;
        }

        public int getDefaultWeight() {
            return defaultWeight;
        }

        public void setDefaultWeight(int defaultWeight) {
            this.defaultWeight = defaultWeight;
        }

        public String getWeightMaps() {
            return weightMaps;
        }

        public void setWeightMaps(String weightMaps) {
            this.weightMaps = weightMaps;
        }
    }

    public static class SingleConfig {
        /**
         * 节点地址
         */
        private String address;
        /**
         * 节点端口
         */
        private int port = 6379;
        /**
         * 发布和订阅连接的最小空闲连接数
         */
        private int subConnMinIdleSize = 1;
        /**
         * 发布和订阅连接池大小
         */
        private int subConnPoolSize = 50;
        /**
         * 最小空闲连接数
         */
        private int connMinIdleSize = 32;
        /**
         * 连接池大小
         */
        private int connPoolSize = 64;
        /**
         * 是否启用DNS监测
         */
        private boolean dnsMonitoring = false;
        /**
         * DNS监测时间间隔，单位：毫秒，该配置需要dnsMonitoring设为true
         */
        private int dnsMonitoringInterval = 5000;
        /**
         * 连接空闲超时，单位：毫秒
         */
        private int idleConnTimeout = 10000;
        /**
         *
         */
        private boolean keepAlive = false;
        /**
         * 连接超时，单位：毫秒
         */
        private int connTimeout = 10000;
        /**
         * 心跳间隔，单位：毫秒，默认值0表示关闭心跳
         */
        private int pingConnectionInterval = 0;
        /**
         * 命令等待超时，单位：毫秒
         */
        private int timeout = 3000;
        /**
         * 命令失败重试次数 如果尝试达到 retryAttempts（命令失败重试次数） 仍然不能将命令发送至某个指定的节点时，将抛出错误。如果尝试在此限制之内发送成功，则开始启用
         * timeout（命令等待超时） 计时。
         */
        private int retryAttempts = 3;
        /**
         * 命令重试发送时间间隔，单位：毫秒
         */
        private int retryInterval = 1500;
        /**
         * 数据库编号
         */
        private int database = 0;
        /**
         * 密码
         */
        private String password;
        /**
         * 单个连接最大订阅数量
         */
        private int subPerConn = 5;


        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public int getSubConnMinIdleSize() {
            return subConnMinIdleSize;
        }

        public void setSubConnMinIdleSize(int subConnMinIdleSize) {
            this.subConnMinIdleSize = subConnMinIdleSize;
        }

        public int getSubConnPoolSize() {
            return subConnPoolSize;
        }

        public void setSubConnPoolSize(int subConnPoolSize) {
            this.subConnPoolSize = subConnPoolSize;
        }

        public int getConnMinIdleSize() {
            return connMinIdleSize;
        }

        public void setConnMinIdleSize(int connMinIdleSize) {
            this.connMinIdleSize = connMinIdleSize;
        }

        public int getConnPoolSize() {
            return connPoolSize;
        }

        public void setConnPoolSize(int connPoolSize) {
            this.connPoolSize = connPoolSize;
        }

        public boolean isDnsMonitoring() {
            return dnsMonitoring;
        }

        public void setDnsMonitoring(boolean dnsMonitoring) {
            this.dnsMonitoring = dnsMonitoring;
        }

        public int getDnsMonitoringInterval() {
            return dnsMonitoringInterval;
        }

        public void setDnsMonitoringInterval(int dnsMonitoringInterval) {
            this.dnsMonitoringInterval = dnsMonitoringInterval;
        }

        public int getIdleConnTimeout() {
            return idleConnTimeout;
        }

        public void setIdleConnTimeout(int idleConnTimeout) {
            this.idleConnTimeout = idleConnTimeout;
        }

        public int getConnTimeout() {
            return connTimeout;
        }

        public void setConnTimeout(int connTimeout) {
            this.connTimeout = connTimeout;
        }

        public int getPingConnectionInterval() {
            return pingConnectionInterval;
        }

        public SingleConfig setPingConnectionInterval(int pingConnectionInterval) {
            this.pingConnectionInterval = pingConnectionInterval;
            return this;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public int getRetryAttempts() {
            return retryAttempts;
        }

        public void setRetryAttempts(int retryAttempts) {
            this.retryAttempts = retryAttempts;
        }

        public int getRetryInterval() {
            return retryInterval;
        }

        public void setRetryInterval(int retryInterval) {
            this.retryInterval = retryInterval;
        }

        public int getDatabase() {
            return database;
        }

        public void setDatabase(int database) {
            this.database = database;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getSubPerConn() {
            return subPerConn;
        }

        public void setSubPerConn(int subPerConn) {
            this.subPerConn = subPerConn;
        }

        public boolean isKeepAlive() {
            return keepAlive;
        }

        public void setKeepAlive(boolean keepAlive) {
            this.keepAlive = keepAlive;
        }
    }
}
