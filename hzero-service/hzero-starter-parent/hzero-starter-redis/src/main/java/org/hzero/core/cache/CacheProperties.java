package org.hzero.core.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * CacheValue 配置
 *
 * @author bojiangzhou 2018/08/08
 */
@Component
@ConfigurationProperties(prefix = CacheProperties.PREFIX)
public class CacheProperties {

    public static final String PREFIX = "hzero.cache-value";

    /**
     * 启用 CacheValue 功能
     */
    private boolean enable = true;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

}
