package org.hzero.core.endpoint.request;

/**
 *
 * 可变的服务端点，允许多版本兼容调用，且端点url可调整
 *
 * @author XCXCXCXCX
 * @date 2020/4/22 12:30 下午
 */
public enum EndpointKey {

    /**
     * 数据源 endpoint key
     */
    DATASOURCE_ENDPOINT_KEY("datasource.endpoint"),
    /**
     * 异步导出 endpoint key
     */
    ASYNC_EXPORT_ENDPOINT_KEY("async.export.endpoint"),
    /**
     * 在线运维 endpoint key
     */
    MAINTAIN_ENDPOINT_KEY("maintain.endpoint");

    private String key;

    EndpointKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
