package org.hzero.register.event;

import org.hzero.register.event.config.RegisterEventListenerProperties;
import org.hzero.register.event.listener.AbstractEventListener;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 注册中心事件监听自动配置类
 * @author XCXCXCXCX
 */
@Configuration
@EnableConfigurationProperties(RegisterEventListenerProperties.class)
@ConditionalOnProperty(value = {"register.event.listener.enabled"}, matchIfMissing = true)
@ConditionalOnBean(DiscoveryClient.class)
@AutoConfigureAfter(CompositeDiscoveryClientAutoConfiguration.class)
public class RegisterEventAutoConfiguration {

    @Bean
    @ConditionalOnBean(DiscoveryClient.class)
    @ConditionalOnMissingBean
    public AbstractEventListener defaultEventListener(DiscoveryClient discoveryClient,
                                                      RegisterEventListenerProperties properties){
        AbstractEventListener defaultEventListener = new AbstractEventListener(){};
        defaultEventListener.setDiscoveryClient(discoveryClient);
        defaultEventListener.setProperties(properties);
        return defaultEventListener;
    }

}
