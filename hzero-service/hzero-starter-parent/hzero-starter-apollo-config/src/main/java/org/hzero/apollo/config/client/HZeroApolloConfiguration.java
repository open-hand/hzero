package org.hzero.apollo.config.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * hzero对于阿波罗客户端的增强配置类
 * @author XCXCXCXCX
 */
@Configuration
@EnableConfigurationProperties(ApolloConfigListenerProperties.class)
@ConditionalOnProperty(name = "spring.cloud.apollo.config.enabled", matchIfMissing = true)
public class HZeroApolloConfiguration {

    @Bean
    public EnvironmentRefresher environmentRefresher(ContextRefresher contextRefresher, ApolloConfigListenerProperties properties){
        return new EnvironmentRefresher(contextRefresher, properties);
    }
}
