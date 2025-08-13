package org.hzero.file.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * yml文件配置的最大文件大小
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/20 10:16
 */
@ConfigurationProperties(prefix = "spring.servlet.multipart")
@Component
public class MaxSizeConfig {

    private String maxFileSize;

    public String getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(String maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
}
