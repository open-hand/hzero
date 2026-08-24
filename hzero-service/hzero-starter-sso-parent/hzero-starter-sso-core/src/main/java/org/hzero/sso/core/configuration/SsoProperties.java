package org.hzero.sso.core.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.hzero.sso.core.constant.SsoAttributes;

@ConfigurationProperties(prefix = SsoProperties.PREFIX)
public class SsoProperties {

    public static final String PREFIX = "hzero.oauth.sso";

    /**
     * 单点登录处理地址
     */
    private String processUrl = SsoAttributes.SSO_DEFAULT_PROCESS_URI;
    /**
     * 可通过此参数禁止跳转到 sso 页面
     */
    private String disableSsoParameter = "disable_sso";
    /**
     * 二级缓存失效时间，单位秒
     */
    private long domainCacheTimeout = 10;

    /**
     * 是否启用兼容模式，默认 false
     */
    private boolean enableCompatibilityMode = false;

    /**
     * 忽略SSL校验
     */
    private boolean ignoreSsl = false;

    /**
     * 域名配置
     */
    private List<DomainConfig> domainConfigs = new ArrayList<>();

    public String getProcessUrl() {
        return processUrl;
    }

    public void setProcessUrl(String processUrl) {
        this.processUrl = processUrl;
    }

    public String getDisableSsoParameter() {
        return disableSsoParameter;
    }

    public void setDisableSsoParameter(String disableSsoParameter) {
        this.disableSsoParameter = disableSsoParameter;
    }

    public long getDomainCacheTimeout() {
        return domainCacheTimeout;
    }

    public void setDomainCacheTimeout(long domainCacheTimeout) {
        this.domainCacheTimeout = domainCacheTimeout;
    }

    public boolean isEnableCompatibilityMode() {
        return enableCompatibilityMode;
    }

    public void setEnableCompatibilityMode(boolean enableCompatibilityMode) {
        this.enableCompatibilityMode = enableCompatibilityMode;
    }

    public boolean isIgnoreSsl() {
        return ignoreSsl;
    }

    public void setIgnoreSsl(boolean ignoreSsl) {
        this.ignoreSsl = ignoreSsl;
    }

    public List<DomainConfig> getDomainConfigs() {
        return domainConfigs;
    }

    public void setDomainConfigs(List<DomainConfig> domainConfigs) {
        this.domainConfigs = domainConfigs;
    }

    /**
     * 域名配置
     *
     * @author berg-turing 2022/03/18
     */
    public static class DomainConfig {
        /**
         * 域名
         */
        private String domain;
        /**
         * 配置信息
         * <p>
         * key -> value === configKey -> configValue
         */
        private List<Config> configs = new ArrayList<>();

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public List<Config> getConfigs() {
            return configs;
        }

        public void setConfigs(List<Config> configs) {
            this.configs = configs;
        }

        /**
         * 配置对象
         *
         * @author berg-turing 2022/03/18
         */
        public static class Config {
            /**
             * 配置的key
             */
            private String key;
            /**
             * 配置的value
             */
            private String value;

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
    }
}
