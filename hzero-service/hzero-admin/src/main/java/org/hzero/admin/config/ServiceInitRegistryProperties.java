package org.hzero.admin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author XCXCXCXCX
 * @date 2020/9/15 10:44 上午
 */
@ConfigurationProperties(prefix = ServiceInitRegistryProperties.PREFIX)
public class ServiceInitRegistryProperties {
    public static final String PREFIX = "hzero.service-init-registry";
    private HealthCheck healthCheck = new HealthCheck();
    private String skipExpression;

    public HealthCheck getHealthCheck() {
        return healthCheck;
    }

    public void setHealthCheck(HealthCheck healthCheck) {
        this.healthCheck = healthCheck;
    }

    public String getSkipExpression() {
        return skipExpression;
    }

    public void setSkipExpression(String skipExpression) {
        this.skipExpression = skipExpression;
    }

    public static class HealthCheck {
        private int connectTimeout = 3000;
        private int readTimeout = 6000;

        public int getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public int getReadTimeout() {
            return readTimeout;
        }

        public void setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
        }
    }

}
