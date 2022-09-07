package org.hzero.wechat.enterprise.conf;

import org.hzero.starter.integrate.service.AbstractCorpSyncService;
import org.hzero.wechat.enterprise.service.*;
import org.hzero.wechat.enterprise.service.impl.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

/**
 * @Author J
 * @Date 2019/8/28
 */
@Configuration
public class WechatEnterpriseConfig {
    @Bean
    @ConditionalOnMissingBean(name = "wdRedisTemplate")
    public RedisTemplate wdRedisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(name = "wdRestTemplate")
    public RestTemplate wdRestTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }


    @Bean
    public AbstractCorpSyncService wechatCorpSyncService() {
        return new WechatCorpSyncServiceImpl();
    }


    @Bean
    @ConditionalOnMissingBean
    public WechatCorpMessageService wechatCorpMessageService() {
        return new WechatCorpMessageServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public WechatCorpAgentManageService wechatCorpAgentManageService() {
        return new WechatCorpAgentManageServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public WechatCorpAddressService wechatCorpAddressService() {
        return new WechatCorpAddressServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public WechatTicketService  wechatTicketService(){
        return new WechatTicketServiceImp();
    }

    @Bean
    @ConditionalOnMissingBean
    public WechatMaterialManageService  wechatMaterialManageService(){
        return new WechatMaterialManageServiceImp();
    }

    @Bean
    @ConditionalOnMissingBean
    public WechatTokenService wechatTokenService() {
        return new WechatTokenServiceImpl();
    }

}
