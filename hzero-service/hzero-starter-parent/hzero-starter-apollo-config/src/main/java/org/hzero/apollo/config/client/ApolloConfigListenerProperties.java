package org.hzero.apollo.config.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

/**
 * 阿波罗配置客户端的监听配置属性
 * @author XCXCXCXCX
 */
@ConfigurationProperties(ApolloConfigListenerProperties.PREFIX)
public class ApolloConfigListenerProperties {

    public static final String PREFIX = "spring.cloud.apollo.config.listener";

    private Set<String> interestedKeys;

    private Set<String> interestedKeyPrefixes;

    public Set<String> getInterestedKeys() {
        return interestedKeys;
    }

    public void setInterestedKeys(Set<String> interestedKeys) {
        this.interestedKeys = interestedKeys;
    }

    public Set<String> getInterestedKeyPrefixes() {
        return interestedKeyPrefixes;
    }

    public void setInterestedKeyPrefixes(Set<String> interestedKeyPrefixes) {
        this.interestedKeyPrefixes = interestedKeyPrefixes;
    }
}
