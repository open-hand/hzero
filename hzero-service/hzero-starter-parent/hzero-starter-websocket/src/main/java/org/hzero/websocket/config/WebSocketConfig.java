package org.hzero.websocket.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * webSocket配置类
 *
 * @author shuangfei.zhu@hand-china.com 2019/04/19 11:43
 */
@Component
@ConfigurationProperties(prefix = WebSocketConfig.PREFIX)
public class WebSocketConfig {

    static final String PREFIX = "hzero.websocket";
    /**
     * websocket路径
     */
    private String websocket = "/websocket";
    /**
     * sockJs路径
     */
    private String sockJs = "/sock-js";
    /**
     * oauth获取用户信息的地址
     */
    private String oauthUrl = "http://hzero-oauth/oauth/api/user";
    /**
     * redisDb
     */
    private Integer redisDb = 1;
    /**
     * 心跳内容
     */
    private String heartbeat = "hzero-hi";
    /**
     * 后端长连通信密钥
     */
    private String secretKey = "hzero";

    /**
     * 线程池配置
     */
    private ThreadPoolProperties threadPoolProperties = new ThreadPoolProperties();

    /**
     * Thread Pool Properties
     *
     * @author gaokuo.dai@hand-china.com 2018年8月21日下午4:00:19
     */
    public static class ThreadPoolProperties {
        /**
         * 核心线程数 默认 2
         */
        private int corePoolSize = 2;
        /**
         * 最大线程数 默认 10
         */
        private int maxPoolSize = 5;
        /**
         * 线程完成任务后的待机存活时间 默认 60
         */
        private int keepAliveSeconds = 15;
        /**
         * 等待队列长度 默认 Integer.MAX_VALUE
         */
        private int queueCapacity = 0;
        /**
         * 是否允许停止闲置核心线程 默认 false
         */
        private boolean allowCoreThreadTimeOut = false;
        /**
         * 线程名前缀 默认 hzero.import
         */
        private String threadNamePrefix = "websocket-check";

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public int getMaxPoolSize() {
            return maxPoolSize;
        }

        public int getKeepAliveSeconds() {
            return keepAliveSeconds;
        }

        public int getQueueCapacity() {
            return queueCapacity;
        }

        public boolean isAllowCoreThreadTimeOut() {
            return allowCoreThreadTimeOut;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public void setKeepAliveSeconds(int keepAliveSeconds) {
            this.keepAliveSeconds = keepAliveSeconds;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }

        public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
            this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        }

        public String getThreadNamePrefix() {
            return threadNamePrefix;
        }

        public void setThreadNamePrefix(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
        }

    }

    public String getWebsocket() {
        return websocket;
    }

    public WebSocketConfig setWebsocket(String websocket) {
        this.websocket = websocket;
        return this;
    }

    public String getSockJs() {
        return sockJs;
    }

    public WebSocketConfig setSockJs(String sockJs) {
        this.sockJs = sockJs;
        return this;
    }

    public String getOauthUrl() {
        return oauthUrl;
    }

    public WebSocketConfig setOauthUrl(String oauthUrl) {
        this.oauthUrl = oauthUrl;
        return this;
    }

    public Integer getRedisDb() {
        return redisDb;
    }

    public WebSocketConfig setRedisDb(Integer redisDb) {
        this.redisDb = redisDb;
        return this;
    }

    public String getHeartbeat() {
        return heartbeat;
    }

    public WebSocketConfig setHeartbeat(String heartbeat) {
        this.heartbeat = heartbeat;
        return this;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public WebSocketConfig setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    public ThreadPoolProperties getThreadPoolProperties() {
        return threadPoolProperties;
    }

    public WebSocketConfig setThreadPoolProperties(ThreadPoolProperties threadPoolProperties) {
        this.threadPoolProperties = threadPoolProperties;
        return this;
    }
}
