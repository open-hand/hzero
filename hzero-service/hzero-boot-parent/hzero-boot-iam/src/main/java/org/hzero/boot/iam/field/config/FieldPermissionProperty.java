package org.hzero.boot.iam.field.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author qingsheng.chen@hand-china.com
 */
@ConfigurationProperties(prefix = "hzero.field-permission")
public class FieldPermissionProperty {
    /**
     * 是否启用
     */
    private boolean enable = false;

    /**
     * 缓存数据过期时间，单位秒，建议设置与 access_token 过期时间保持一致，默认1天
     */
    private long expire = 86400;

    public boolean isEnable() {
        return enable;
    }

    public FieldPermissionProperty setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public long getExpire() {
        return expire;
    }

    public FieldPermissionProperty setExpire(long expire) {
        this.expire = expire;
        return this;
    }
}
