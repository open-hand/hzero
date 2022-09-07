package org.hzero.dd.config;


import java.nio.charset.StandardCharsets;

import org.hzero.dd.service.*;
import org.hzero.dd.service.impl.*;
import org.hzero.starter.integrate.service.AbstractCorpSyncService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * @Author J
 * @Date 2019/8/28
 */
@Configuration
public class DingConfig {

    @Bean
    @ConditionalOnMissingBean(name = "wdRedisTemplate")
    public RedisTemplate wdRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(name = "wdRestTemplate")
    public RestTemplate wdRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }


    @Bean
    public AbstractCorpSyncService dingCorpSyncService() {
        return new DingCorpSyncServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public DingCorpMessageService dingCorpMessageService() {
        return new DingCorpMessageServiceImp();
    }

    @Bean
    @ConditionalOnMissingBean
    DingFileStorageService dingFileStorageService() {
        return new DingFileStorageServiceImp();
    }

    @Bean
    @ConditionalOnMissingBean
    DingTicketService dingTicketService() {
        return new DingTicketServiceImp();
    }

    @Bean
    @ConditionalOnMissingBean
    DingCorpAddressService dingCorpAddressService() {
        return new DingCorpAddressServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    DingCorpAgentManageService dingCorpAgentManageService() {
        return new DingCorpAgentManageServiceImp();
    }

    @Bean
    @ConditionalOnMissingBean
    DingTokenService dingTokenService() {
        return new DingTokenServiceImpl();
    }
}
