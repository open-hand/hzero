package org.hzero.export;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 导出配置属性
 *
 * @author XCXCXCXCX 2019/8/5
 */
@ConfigurationProperties(prefix = ExportProperties.PREFIX)
public class ExportProperties {

    public static final String PREFIX = "hzero.export";

    public static final String SYNC_REQUEST_MODE = "SYNC";
    public static final String ASYNC_REQUEST_MODE = "ASYNC";
    private static final int DEFAULT_CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int DEFAULT_MAX_MUM_POOL_SIZE = DEFAULT_CORE_POOL_SIZE + 2;

    /**
     * 默认请求模式默认不开启，前端可选择同步和异步。
     * 默认请求模式设置为同步，则所有调用均为同步调用；默认请求模式设置为异步，则所有调用均为异步调用。
     * 且前端不显示同步和异步选项。
     * <p>
     * null
     * SYNC
     * ASYNC
     * <p>
     * ps. 当defaultRequestMode="ASYNC" && enableAsync=false时，服务会启动报错。
     */
    private String defaultRequestMode = null;
    /**
     * 是否开启异步导出
     */
    private Boolean enableAsync = false;
    /**
     * 异步阈值，若导出的数据总量超过该阈值，强制执行异步导出
     */
    private Integer asyncThreshold;
    /**
     * 核心线程数
     */
    private Integer corePoolSize = DEFAULT_CORE_POOL_SIZE;
    /**
     * 最大线程数
     */
    private Integer maximumPoolSize = DEFAULT_MAX_MUM_POOL_SIZE;
    /**
     * 线程存活时间
     */
    private Duration keepAliveTime = Duration.ZERO;
    /**
     * 等待队列大小
     */
    private Integer queueSize;
    /**
     * 线程名称
     */
    private String asyncThreadName = "async-export-executor";
    /**
     * 默认最大sheet页数
     */
    private Integer singleExcelMaxSheetNum = 5;

    public String getDefaultRequestMode() {
        return defaultRequestMode;
    }

    public void setDefaultRequestMode(String defaultRequestMode) {
        this.defaultRequestMode = defaultRequestMode;
    }

    public Boolean getEnableAsync() {
        return enableAsync;
    }

    public void setEnableAsync(Boolean enableAsync) {
        this.enableAsync = enableAsync;
    }

    public Integer getAsyncThreshold() {
        return asyncThreshold;
    }

    public void setAsyncThreshold(Integer asyncThreshold) {
        this.asyncThreshold = asyncThreshold;
    }

    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public Integer getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(Integer maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public Duration getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(Duration keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public Integer getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(Integer queueSize) {
        this.queueSize = queueSize;
    }

    public String getAsyncThreadName() {
        return asyncThreadName;
    }

    public void setAsyncThreadName(String asyncThreadName) {
        this.asyncThreadName = asyncThreadName;
    }

    public Integer getSingleExcelMaxSheetNum() {
        return singleExcelMaxSheetNum;
    }

    public void setSingleExcelMaxSheetNum(Integer singleExcelMaxSheetNum) {
        this.singleExcelMaxSheetNum = singleExcelMaxSheetNum;
    }
}
