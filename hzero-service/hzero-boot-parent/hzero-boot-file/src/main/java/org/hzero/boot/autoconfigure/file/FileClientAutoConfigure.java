package org.hzero.boot.autoconfigure.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.hzero.boot.file.FileClient;
import org.hzero.boot.file.OnlyOfficeClient;
import org.hzero.boot.file.config.FileInitializeConfig;
import org.hzero.boot.file.feign.FileRemoteService;
import org.hzero.boot.file.feign.fallback.FileRemoteServiceFallback;
import org.hzero.boot.file.service.OnlyOfficeCacheService;
import org.hzero.boot.file.service.OnlyOfficeService;
import org.hzero.core.redis.RedisHelper;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动装配
 *
 * @author xianzhi.chen@hand-china.com 2019年1月31日上午9:55:49
 */
@Configuration
@EnableFeignClients(basePackageClasses = FileRemoteService.class)
public class FileClientAutoConfigure {

    @Bean
    @ConditionalOnMissingBean(Encoder.class)
    public Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }

    @Bean
    public FileRemoteServiceFallback fileRemoteServiceFallback() {
        return new FileRemoteServiceFallback();
    }

    @Bean
    public FileClient fileClient(FileRemoteService fileRemoteService) {
        return new FileClient(fileRemoteService);
    }

    @Bean
    public OnlyOfficeService onlyOfficeService(OnlyOfficeConfigProperties onlyOfficeConfig, ObjectMapper objectMapper) {
        return new OnlyOfficeService(onlyOfficeConfig, objectMapper);
    }

    @Bean
    public OnlyOfficeClient onlyOfficeClient(FileClient fileClient, OnlyOfficeService onlyOfficeService, OnlyOfficeConfigProperties onlyOfficeConfig, ObjectMapper objectMapper, OnlyOfficeCacheService onlyOfficeCacheService) {
        return new OnlyOfficeClient(fileClient, onlyOfficeService, onlyOfficeConfig, objectMapper, onlyOfficeCacheService);
    }

    @Bean
    public FileInitializeConfig fileInitializeConfig() {
        return new FileInitializeConfig();
    }

    @Bean
    public OnlyOfficeCacheService onlyOfficeCacheService(RedisHelper redisHelper, OnlyOfficeConfigProperties onlyOfficeConfig) {
        return new OnlyOfficeCacheService(redisHelper, onlyOfficeConfig);
    }
}
