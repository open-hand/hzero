package org.hzero.boot.admin.transport;

import org.hzero.common.HZeroService;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author XCXCXCXCX
 * @date 2020/6/11 11:15 上午
 */
@ConfigurationProperties(prefix = AdminClientProperties.PREFIX)
public class AdminClientProperties {

    public static final String PREFIX = "hzero.admin.client";

    private String[] serverList;

    private long retryInternal = 3000;

    private int connectTimeout = 5000;

    private int readTimeout = 8000;

    private Discovery discovery = new Discovery();

    public static class Discovery {
        private boolean enabled = true;
        private String serviceId = HZeroService.Admin.NAME;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getServiceId() {
            return HZeroService.getRealName(serviceId);
        }

        public void setServiceId(String serviceId) {
            this.serviceId = serviceId;
        }
    }

    public String[] getServerList() {
        return serverList;
    }

    public void setServerList(String[] serverList) {
        this.serverList = serverList;
    }

    public long getRetryInternal() {
        return retryInternal;
    }

    public void setRetryInternal(long retryInternal) {
        this.retryInternal = retryInternal;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Discovery getDiscovery() {
        return discovery;
    }

    public void setDiscovery(Discovery discovery) {
        this.discovery = discovery;
    }
}
