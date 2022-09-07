package org.hzero.admin.config;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.hzero.admin.api.actuator.CacheRefreshActuatorEndpoint;
import org.hzero.core.message.MessageAccessor;

/**
 * description
 *
 * @author bojiangzhou
 */
@EnableConfigurationProperties(ConfigProperties.class)
@Configuration
public class AdminConfiguration {

    @Bean
    public SmartInitializingSingleton adminSmartInitializingSingleton() {
        return () -> {
            // 加入消息文件
            MessageAccessor.addBasenames("classpath:messages/messages_hadm");
        };
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnEnabledEndpoint
    public CacheRefreshActuatorEndpoint cacheRefreshActuatorEndpoint(DiscoveryClient discoveryClient,
                                                                     ConfigProperties configProperties) {
        return new CacheRefreshActuatorEndpoint(discoveryClient, configProperties);
    }



}
