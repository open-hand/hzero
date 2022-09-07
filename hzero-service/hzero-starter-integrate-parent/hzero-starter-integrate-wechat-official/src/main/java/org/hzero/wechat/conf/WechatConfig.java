package org.hzero.wechat.conf;

import java.nio.charset.StandardCharsets;

import org.hzero.wechat.service.BaseWechatService;
import org.hzero.wechat.service.WeChatMenuService;
import org.hzero.wechat.service.WeChatMessageManageService;
import org.hzero.wechat.service.WechatMaterialManageService;
import org.hzero.wechat.service.impl.BaseWechatServiceImpl;
import org.hzero.wechat.service.impl.WeChatMenuServiceImp;
import org.hzero.wechat.service.impl.WeChatMessageManageServiceImp;
import org.hzero.wechat.service.impl.WechatMaterialManageServiceImp;
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
public class WechatConfig {

    @Bean
    @ConditionalOnMissingBean
    public BaseWechatService baseWechatService() {
        return new BaseWechatServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(name = "wdRestTemplate")
    public RestTemplate wdRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }

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
    @ConditionalOnMissingBean
    public WechatMaterialManageService wechatMaterialManageService() {
        return  new WechatMaterialManageServiceImp();
    }

    @Bean
    @ConditionalOnMissingBean
    public WeChatMenuService weChatMenuService() {
        return new WeChatMenuServiceImp();
    }

    @Bean
    @ConditionalOnMissingBean
    public WeChatMessageManageService weChatMessageManageService() {
        return new WeChatMessageManageServiceImp();
    }

}
