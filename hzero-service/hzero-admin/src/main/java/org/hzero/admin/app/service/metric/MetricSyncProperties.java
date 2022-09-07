package org.hzero.admin.app.service.metric;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/11 10:06 上午
 */
@ConfigurationProperties(prefix = MetricSyncProperties.PREFIX)
public class MetricSyncProperties {

    public static final String PREFIX = "hzero.metric.sync";

    private boolean enable = false;

    /**
     * unit ms
     */
    private long syncInterval = 60000;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public long getSyncInterval() {
        return syncInterval;
    }

    public void setSyncInterval(long syncInterval) {
        this.syncInterval = syncInterval;
    }
}
