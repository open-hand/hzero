package org.hzero.register.event.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * 注册中心事件监听器
 *
 * @author XCXCXCXCX
 */
@ConfigurationProperties(prefix = RegisterEventListenerProperties.PREFIX)
public class RegisterEventListenerProperties {

    public static final String PREFIX = "hzero.register.event.listener";

    /**
     * default 10s
     */
    private long pollInterval = 10;

    /**
     * 如果连续检测到超过最大下线次数，则认为所有服务的所有列表均已下线，否则认为是网络问题。
     */
    private int maxServiceDownTimes = 5;
    /**
     * 如果连续检测到超过最大下线次数，则认为该服务的所有列表均已下线，否则认为是网络问题。
     */
    private int maxInstanceDownTimes = 5;

    public long getPollInterval() {
        return pollInterval;
    }

    public void setPollInterval(long pollInteval) {
        this.pollInterval = pollInteval;
    }

    public int getMaxServiceDownTimes() {
        return maxServiceDownTimes;
    }

    public void setMaxServiceDownTimes(int maxServiceDownTimes) {
        this.maxServiceDownTimes = maxServiceDownTimes;
    }

    public int getMaxInstanceDownTimes() {
        return maxInstanceDownTimes;
    }

    public void setMaxInstanceDownTimes(int maxInstanceDownTimes) {
        this.maxInstanceDownTimes = maxInstanceDownTimes;
    }
}
