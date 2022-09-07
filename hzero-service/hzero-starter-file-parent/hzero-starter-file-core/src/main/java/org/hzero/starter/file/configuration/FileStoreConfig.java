package org.hzero.starter.file.configuration;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 通用文件配置
 *
 * @author shuangfei.zhu@hand-china.com 2019/12/18 15:49
 */
@Component
@ConfigurationProperties(prefix = "hzero.file")
public class FileStoreConfig {

    /**
     * 默认临时访问路径时间 300秒
     */
    private Long defaultExpires = 300L;

    /**
     * 华为、百度跨域配置
     */
    private List<String> origins;

    /**
     * 忽略证书验证
     */
    private boolean ignoreCertCheck = false;

    /**
     * 分片阈值(超过时开启分片上传), 默认5M
     */
    private Integer shardingThreshold = 5242880;

    /**
     * 默认分片大小, 默认1M
     */
    private Integer defaultSharedSize = 1048576;

    /**
     * 协议  http或https
     */
    private String protocol = "https";

    public Long getDefaultExpires() {
        return defaultExpires;
    }

    public FileStoreConfig setDefaultExpires(Long defaultExpires) {
        this.defaultExpires = defaultExpires;
        return this;
    }

    public List<String> getOrigins() {
        return origins;
    }

    public FileStoreConfig setOrigins(List<String> origins) {
        this.origins = origins;
        return this;
    }

    public boolean isIgnoreCertCheck() {
        return ignoreCertCheck;
    }

    public FileStoreConfig setIgnoreCertCheck(boolean ignoreCertCheck) {
        this.ignoreCertCheck = ignoreCertCheck;
        return this;
    }

    public Integer getShardingThreshold() {
        return shardingThreshold;
    }

    public FileStoreConfig setShardingThreshold(Integer shardingThreshold) {
        this.shardingThreshold = shardingThreshold;
        return this;
    }

    public Integer getDefaultSharedSize() {
        return defaultSharedSize;
    }

    public FileStoreConfig setDefaultSharedSize(Integer defaultSharedSize) {
        this.defaultSharedSize = defaultSharedSize;
        return this;
    }

    public String getProtocol() {
        return protocol;
    }

    public FileStoreConfig setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }
}
