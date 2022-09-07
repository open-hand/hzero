package org.hzero.fragment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * excel配置类
 *
 * @author shuangfei.zhu@hand-china.com 2020/2/03 11:43
 */
@Component
@ConfigurationProperties(prefix = FragmentConfig.PREFIX)
public class FragmentConfig {

    private FragmentConfig() {
    }

    static final String PREFIX = "hzero.file";

    /**
     * 网关地址
     */
    private String gatewayPath = "";

    private String rootPath;

    public String getGatewayPath() {
        return gatewayPath;
    }

    public FragmentConfig setGatewayPath(String gatewayPath) {
        this.gatewayPath = gatewayPath;
        return this;
    }

    public String getRootPath() {
        return rootPath;
    }

    public FragmentConfig setRootPath(String rootPath) {
        this.rootPath = rootPath;
        return this;
    }
}
