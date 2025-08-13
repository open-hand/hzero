package org.hzero.boot.report.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/06/23 16:47
 */
@ConfigurationProperties(prefix = ReportClientConfig.PREFIX)
@Component
public class ReportClientConfig {

    static final String PREFIX = "hzero.report";

    /**
     * 线程池配置
     */
    private ThreadPoolProperties threadPoolProperties = new ThreadPoolProperties();

    public ThreadPoolProperties getThreadPoolProperties() {
        return threadPoolProperties;
    }

    public ReportClientConfig setThreadPoolProperties(ThreadPoolProperties threadPoolProperties) {
        this.threadPoolProperties = threadPoolProperties;
        return this;
    }

    /**
     * Thread Pool Properties
     */
    public static class ThreadPoolProperties {
        /**
         * 核心线程数 默认 2
         */
        private int corePoolSize = 2;
        /**
         * 最大线程数 默认 10
         */
        private int maxPoolSize = 10;
        /**
         * 线程完成任务后的待机存活时间 默认 60
         */
        private int keepAliveSeconds = 60;
        /**
         * 等待队列长度 默认 Integer.MAX_VALUE
         */
        private int queueCapacity = Integer.MAX_VALUE;
        /**
         * 是否允许停止闲置核心线程 默认 false
         */
        private boolean allowCoreThreadTimeOut = false;
        /**
         * 线程名前缀 默认 hzero.import
         */
        private String threadNamePrefix = PREFIX;

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
}
