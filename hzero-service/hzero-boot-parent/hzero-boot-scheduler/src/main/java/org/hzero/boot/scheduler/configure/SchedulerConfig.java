package org.hzero.boot.scheduler.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 调度服务客户端属性
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/17 11:13
 */
@Component
@ConfigurationProperties(prefix = SchedulerConfig.PREFIX)
public class SchedulerConfig {

    static final String PREFIX = "hzero.scheduler";

    /**
     * 执行器编码
     */
    private String executorCode;

    /**
     * 是否开启自动注册
     */
    private boolean autoRegister = true;

    /**
     * 日志文件形式上传
     */
    private boolean uploadLog = true;

    /**
     * 自动注册失败重试时间间隔  单位秒
     */
    private Integer retry = 60;

    /**
     * 自动注册重试次数
     */
    private Integer retryTime = 5;

    /**
     * 任务停止重试时间   单位秒
     */
    private Integer stopRetryTime = 5;

    /**
     * 最大线程数
     */
    private Integer maxPoolSize = 30;

    /**
     * 核心线程数
     */
    private Integer corePoolSize = 5;

    public String getExecutorCode() {
        return executorCode;
    }

    public SchedulerConfig setExecutorCode(String executorCode) {
        this.executorCode = executorCode;
        return this;
    }

    public boolean isAutoRegister() {
        return autoRegister;
    }

    public SchedulerConfig setAutoRegister(boolean autoRegister) {
        this.autoRegister = autoRegister;
        return this;
    }

    public boolean isUploadLog() {
        return uploadLog;
    }

    public SchedulerConfig setUploadLog(boolean uploadLog) {
        this.uploadLog = uploadLog;
        return this;
    }

    public Integer getRetry() {
        return retry;
    }

    public SchedulerConfig setRetry(Integer retry) {
        this.retry = retry;
        return this;
    }

    public Integer getRetryTime() {
        return retryTime;
    }

    public SchedulerConfig setRetryTime(Integer retryTime) {
        this.retryTime = retryTime;
        return this;
    }

    public Integer getStopRetryTime() {
        return stopRetryTime;
    }

    public SchedulerConfig setStopRetryTime(Integer stopRetryTime) {
        this.stopRetryTime = stopRetryTime;
        return this;
    }

    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public SchedulerConfig setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    public SchedulerConfig setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }
}
