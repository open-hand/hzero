package org.hzero.boot.platform.permission.tenant;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * 临时可访问租户设置
 * </p>
 *
 * @author qingsheng.chen 2019/2/26 星期二 11:23
 */
@ConfigurationProperties(prefix = "hzero.permission.tenant")
public class TemporaryTenantProperties {
    /**
     * 默认超时时间（默认：7天）
     */
    private long timeout = 7;
    /**
     * 默认超时时间单位（默认：天）
     */
    private TimeUnit timeUnit = TimeUnit.DAYS;

    public long getTimeout() {
        return timeout;
    }

    public TemporaryTenantProperties setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public TemporaryTenantProperties setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }
}
