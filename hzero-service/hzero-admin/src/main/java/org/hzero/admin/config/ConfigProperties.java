package org.hzero.admin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Config 属性配置
 *
 * @author bojiangzhou 2018/12/18
 */
@ConfigurationProperties(prefix = ConfigProperties.PREFIX)
public class ConfigProperties {

    static final String PREFIX = "hzero.config";

    /**
     * 可全局刷新缓存的服务
     */
    private String[] refreshCacheServices = new String[]{"hzero-platform", "hzero-iam"};
    /**
     * 管理端口令牌
     */
    private String managementToken = "JWBJOFGMQPKZFHW4RAKD0DUQNSZSEVOCP8TYMIC29YLE1LH7URNV5XBX63IATGSK";

    /**
     * 跳过刷新服务路由的服务
     */
    private Route route = new Route();

    public static class Route {
        /**
         * 跳过刷新服务路由的服务
         */
        private String[] skipParseServices = new String[]{"register", "gateway", "oauth"};

        private int refreshCheckPeriod = 5;

        private int refreshCheckMaxPendingTimes = 6;

        public String[] getSkipParseServices() {
            return skipParseServices;
        }

        public void setSkipParseServices(String[] skipParseServices) {
            this.skipParseServices = skipParseServices;
        }

        public int getRefreshCheckPeriod() {
            return refreshCheckPeriod;
        }

        public void setRefreshCheckPeriod(int refreshCheckPeriod) {
            this.refreshCheckPeriod = refreshCheckPeriod;
        }

        public int getRefreshCheckMaxPendingTimes() {
            return refreshCheckMaxPendingTimes;
        }

        public void setRefreshCheckMaxPendingTimes(int refreshCheckMaxPendingTimes) {
            this.refreshCheckMaxPendingTimes = refreshCheckMaxPendingTimes;
        }
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public String[] getRefreshCacheServices() {
        return refreshCacheServices;
    }

    public void setRefreshCacheServices(String[] refreshCacheServices) {
        this.refreshCacheServices = refreshCacheServices;
    }

    public String getManagementToken() {
        return managementToken;
    }

    public void setManagementToken(String managementToken) {
        this.managementToken = managementToken;
    }
}
