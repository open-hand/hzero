package org.hzero.boot.autoconfigure.file;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/06/01 10:07
 */
@Configuration
@ConfigurationProperties(prefix = BootFileConfigProperties.PREFIX)
public class BootFileConfigProperties {

    /**
     * 配置前缀
     */
    static final String PREFIX = "hzero.file";

    /**
     * 默认密钥
     */
    private String defaultKey = "RGarqXE1wpAnW6V5hQs0Lg==";

    public String getDefaultKey() {
        return defaultKey;
    }

    public BootFileConfigProperties setDefaultKey(String defaultKey) {
        this.defaultKey = defaultKey;
        return this;
    }
}
