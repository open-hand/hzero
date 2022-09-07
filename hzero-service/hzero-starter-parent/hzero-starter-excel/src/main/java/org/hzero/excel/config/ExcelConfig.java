package org.hzero.excel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * excel配置类
 *
 * @author shuangfei.zhu@hand-china.com 2020/2/03 11:43
 */
@Component
@ConfigurationProperties(prefix = ExcelConfig.PREFIX)
public class ExcelConfig {

    private ExcelConfig() {
    }

    static final String PREFIX = "hzero.excel";

    /**
     * 缓存大小
     */
    private Integer cacheSize = 100;

    /**
     * 缓存大小 (字节)
     */
    private Integer bufferSize = 1024;

    public Integer getCacheSize() {
        return cacheSize;
    }

    public ExcelConfig setCacheSize(Integer cacheSize) {
        this.cacheSize = cacheSize;
        return this;
    }

    public Integer getBufferSize() {
        return bufferSize;
    }

    public ExcelConfig setBufferSize(Integer bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }
}
