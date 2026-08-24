package org.hzero.feign.autoconfigure;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/23 9:07
 */
@Component
@ConfigurationProperties(prefix = LocalFeignConfig.PREFIX)
public class LocalFeignConfig {

    static final String PREFIX = "hzero.feign";

    /**
     * 使用全类名注入的包前缀
     */
    private List<String> packages;

    public List<String> getPackages() {
        return packages;
    }

    public LocalFeignConfig setPackages(List<String> packages) {
        this.packages = packages;
        return this;
    }
}
