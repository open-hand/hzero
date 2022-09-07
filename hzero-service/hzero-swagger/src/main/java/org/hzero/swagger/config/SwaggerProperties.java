package org.hzero.swagger.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * gateway类型的服务名称列表
 */
@ConfigurationProperties(prefix = SwaggerProperties.PREFIX)
public class SwaggerProperties {

    static final String PREFIX = "hzero.swagger";

    /**
     * Client
     */
    private String client = "client";
    /**
     * 是否启用 https
     *
     * @deprecated 建议直接配置 baseUrl
     */
    @Deprecated
    private boolean enableHttps;
    /**
     * 跳过的服务
     */
    private String[] skipService = new String[]{"register", "gateway", "oauth"};
    /**
     * 拉取Swagger的次数
     */
    private int fetchTime = 10;
    /**
     * 拉取Swagger间隔秒数
     */
    private int fetchSeconds = 60;
    /**
     * 拉取Swagger是否需要重试
     */
    private boolean fetchCallback = true;

    /**
     * 基础地址
     */
    private String baseUrl;

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String[] getSkipService() {
        return skipService;
    }

    public void setSkipService(String[] skipService) {
        this.skipService = skipService;
    }

    public int getFetchTime() {
        return fetchTime;
    }

    public void setFetchTime(int fetchTime) {
        this.fetchTime = fetchTime;
    }

    public int getFetchSeconds() {
        return fetchSeconds;
    }

    public void setFetchSeconds(int fetchSeconds) {
        this.fetchSeconds = fetchSeconds;
    }

    public boolean isFetchCallback() {
        return fetchCallback;
    }

    public void setFetchCallback(boolean fetchCallback) {
        this.fetchCallback = fetchCallback;
    }

    public boolean isEnableHttps() {
        return enableHttps;
    }

    public void setEnableHttps(boolean enableHttps) {
        this.enableHttps = enableHttps;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
