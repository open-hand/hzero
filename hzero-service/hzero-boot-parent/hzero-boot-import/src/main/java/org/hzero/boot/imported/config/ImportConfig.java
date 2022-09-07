package org.hzero.boot.imported.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2018/12/10 16:21
 */

@ConfigurationProperties(prefix = ImportConfig.PREFIX)
@Component
public class ImportConfig {

    static final String PREFIX = "hzero.import";

    /**
     * 事务控制
     */
    private Boolean transactionControl = true;

    /**
     * 每次读取excel的数据条数
     */
    private Integer batchSize = 3000;

    /**
     * 缓存大小 (字节)
     */
    private Integer bufferMemory = 4096;

    /**
     * 导入进度刷新最小步长
     */
    private Integer minStepSize = 100;

    /**
     * 导入进度刷新频次
     */
    private Integer frequency = 10;

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

    public Boolean getTransactionControl() {
        return transactionControl;
    }

    public ImportConfig setTransactionControl(Boolean transactionControl) {
        this.transactionControl = transactionControl;
        return this;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public ImportConfig setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public Integer getBufferMemory() {
        return bufferMemory;
    }

    public ImportConfig setBufferMemory(Integer bufferMemory) {
        this.bufferMemory = bufferMemory;
        return this;
    }

    public Integer getMinStepSize() {
        return minStepSize;
    }

    public ImportConfig setMinStepSize(Integer minStepSize) {
        this.minStepSize = minStepSize;
        return this;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public ImportConfig setFrequency(Integer frequency) {
        this.frequency = frequency;
        return this;
    }

    public ThreadPoolProperties getThreadPoolProperties() {
        return threadPoolProperties;
    }

    public void setThreadPoolProperties(ThreadPoolProperties threadPoolProperties) {
        this.threadPoolProperties = threadPoolProperties;
    }
}
