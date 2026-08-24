package org.hzero.boot.message.config;

import org.hzero.boot.message.DefaultMessageGenerator;
import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.controller.MessageController;
import org.hzero.boot.message.feign.MessageRemoteService;
import org.hzero.boot.message.feign.PlatformRemoteService;
import org.hzero.boot.message.feign.fallback.MessageRemoteImpl;
import org.hzero.boot.message.feign.fallback.PlatformRemoteImpl;
import org.hzero.boot.message.registry.MessageInit;
import org.hzero.boot.message.service.MessageAsyncService;
import org.hzero.boot.message.service.MessageGenerator;
import org.hzero.core.redis.RedisHelper;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <p>
 * 消息客户端配置嘞
 * </p>
 *
 * @author qingsheng.chen 2018/8/7 星期二 11:51
 */
@EnableFeignClients(basePackageClasses = {MessageRemoteService.class, PlatformRemoteService.class})
@EnableConfigurationProperties(MessageClientProperties.class)
@EnableAsync
public class MessageClientConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MessageInit messageInit() {
        return new MessageInit();
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageRemoteImpl messageRemoteFallback() {
        return new MessageRemoteImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public PlatformRemoteImpl platformRemoteFallback() {
        return new PlatformRemoteImpl();
    }

    @Bean
    public MessageAsyncService messageAsyncService(MessageRemoteService messageRemoteService) {
        return new MessageAsyncService(messageRemoteService);
    }

    @Bean
    public DefaultMessageGenerator defaultMessageGenerator(MessageClientProperties messageClientProperties,
                                                           MessageRemoteService messageRemoteService,
                                                           RedisHelper redisHelper) {
        return new DefaultMessageGenerator(messageClientProperties, messageRemoteService, redisHelper);
    }

    @Bean
    public DataSourceBeanPostProcessor dataSourceBeanPostProcessor() {
        return new DataSourceBeanPostProcessor();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean
    public MessageClient messageClient(MessageRemoteService messageRemoteService,
                                       PlatformRemoteService platformRemoteService,
                                       MessageClientProperties messageClientProperties,
                                       MessageGenerator messageGenerator,
                                       RedisTemplate<String, String> redisTemplate,
                                       ObjectMapper objectMapper) {
        return new MessageClient(messageRemoteService, platformRemoteService, messageAsyncService(messageRemoteService), messageClientProperties, messageGenerator, redisTemplate, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageController messageController() {
        return new MessageController();
    }
}
