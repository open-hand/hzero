package org.hzero.gateway.helper.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * @author 废柴 2020/8/7 10:42
 */
@Component
@ConfigurationProperties(prefix = "hzero.gateway.helper.authenticate.call-redis")
public class RedisAuthenticateProperties {
    private boolean enable = true;
    private boolean callOauthServiceOnSuccess = false;
    private boolean callOauthServiceOnFailure = false;
    private boolean callOauthServiceOnExpired = false;
    private int corePoolSize = 4;
    private int maximumPoolSize = 32;
    private Duration keepAliveTime = Duration.ofMinutes(5);
    private int queueCapacity = 1 << 8;
    private String callerName = "OAuthCaller";

    public boolean isEnable() {
        return enable;
    }

    public RedisAuthenticateProperties setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public boolean isCallOauthServiceOnSuccess() {
        return callOauthServiceOnSuccess;
    }

    public RedisAuthenticateProperties setCallOauthServiceOnSuccess(boolean callOauthServiceOnSuccess) {
        this.callOauthServiceOnSuccess = callOauthServiceOnSuccess;
        return this;
    }

    public boolean isCallOauthServiceOnFailure() {
        return callOauthServiceOnFailure;
    }

    public RedisAuthenticateProperties setCallOauthServiceOnFailure(boolean callOauthServiceOnFailure) {
        this.callOauthServiceOnFailure = callOauthServiceOnFailure;
        return this;
    }

    public boolean isCallOauthServiceOnExpired() {
        return callOauthServiceOnExpired;
    }

    public RedisAuthenticateProperties setCallOauthServiceOnExpired(boolean callOauthServiceOnExpired) {
        this.callOauthServiceOnExpired = callOauthServiceOnExpired;
        return this;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public RedisAuthenticateProperties setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public RedisAuthenticateProperties setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
        return this;
    }

    public Duration getKeepAliveTime() {
        return keepAliveTime;
    }

    public RedisAuthenticateProperties setKeepAliveTime(Duration keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public RedisAuthenticateProperties setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
        return this;
    }

    public String getCallerName() {
        return callerName;
    }

    public RedisAuthenticateProperties setCallerName(String callerName) {
        this.callerName = callerName;
        return this;
    }
}
