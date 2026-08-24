package org.hzero.gateway.helper.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/08/18 17:33
 */
@Component
@ConfigurationProperties(prefix = "hzero.gateway")
public class GatewayConfig {

    private String skipPrefix = "pub";

    public String getSkipPrefix() {
        return skipPrefix;
    }

    public GatewayConfig setSkipPrefix(String skipPrefix) {
        this.skipPrefix = skipPrefix;
        return this;
    }
}
