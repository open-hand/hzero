package org.hzero.sso.core.configuration;

import java.util.List;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;

import org.hzero.core.message.MessageAccessor;
import org.hzero.core.redis.RedisHelper;
import org.hzero.sso.core.common.SsoAuthenticationFactory;
import org.hzero.sso.core.common.config.SsoPropertiesConfig;
import org.hzero.sso.core.common.config.SsoPropertiesConfigProvider;
import org.hzero.sso.core.common.config.SsoConfigManager;
import org.hzero.sso.core.common.config.SsoConfigProvider;
import org.hzero.sso.core.domain.DomainRepository;
import org.hzero.sso.core.domain.impl.DefaultDomainRepository;
import org.hzero.sso.core.security.SsoAuthenticationEntryPoint;
import org.hzero.sso.core.service.SsoTokenClearService;
import org.hzero.sso.core.service.SsoUserDetailsService;
import org.hzero.sso.core.service.impl.DefaultSsoTokenClearService;
import org.hzero.sso.core.standard.StandardAuthenticationFactory;
import org.hzero.sso.core.standard.StandardAuthenticationProvider;
import org.hzero.sso.core.standard.StandardAuthenticationRouter;
import org.hzero.sso.core.standard.StandardSsoPropertyService;
import org.hzero.sso.core.support.SsoAuthenticationLocator;

@Configuration
@EnableConfigurationProperties(SsoProperties.class)
public class SsoAutoConfiguration {

    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private SsoProperties ssoProperties;

    @Bean("ssoSmartInitializingSingleton")
    public SmartInitializingSingleton ssoSmartInitializingSingleton() {
        return () -> {
            // 加入消息文件
            MessageAccessor.addBasenames("classpath:messages/message_sso");
        };
    }

    @Bean
    @ConditionalOnMissingBean(DomainRepository.class)
    public DomainRepository domainRepository() {
        return new DefaultDomainRepository(redisHelper, ssoProperties);
    }

    @Bean
    @ConditionalOnMissingBean(SsoAuthenticationLocator.class)
    public SsoAuthenticationLocator ssoAuthenticationLocator(List<SsoAuthenticationFactory> authenticationFactoryList) {
        return new SsoAuthenticationLocator(authenticationFactoryList);
    }

    @Bean(name = "authenticationEntryPoint")
    @ConditionalOnMissingBean(SsoAuthenticationEntryPoint.class)
    public SsoAuthenticationEntryPoint ssoAuthenticationEntryPoint(SsoAuthenticationLocator ssoAuthenticationLocator, SsoPropertyService propertyService) {
        return new SsoAuthenticationEntryPoint(ssoAuthenticationLocator, propertyService, domainRepository());
    }

    @Bean
    @ConditionalOnMissingBean(StandardAuthenticationRouter.class)
    public StandardAuthenticationRouter standardAuthenticationRouter(SsoPropertyService ssoPropertyService) {
        return new StandardAuthenticationRouter(ssoPropertyService);
    }

    @Bean
    @ConditionalOnMissingBean(StandardAuthenticationProvider.class)
    public StandardAuthenticationProvider standardAuthenticationProvider(SsoUserDetailsService userDetailsService) {
        return new StandardAuthenticationProvider(userDetailsService);
    }

    @Bean
    @ConditionalOnMissingBean(SsoTokenClearService.class)
    public SsoTokenClearService ssoTokenClearService(TokenStore tokenStore) {
        return new DefaultSsoTokenClearService(tokenStore);
    }

    @Bean
    @ConditionalOnMissingBean(SsoPropertyService.class)
    public SsoPropertyService ssoPropertyService(SsoProperties ssoProperties) {
        return new StandardSsoPropertyService(ssoProperties);
    }

    @Bean
    @ConditionalOnMissingBean(StandardAuthenticationFactory.class)
    public StandardAuthenticationFactory standardAuthenticationFactory() {
        return new StandardAuthenticationFactory();
    }

    @Bean
    @ConditionalOnMissingBean(SsoPropertiesConfigProvider.class)
    public SsoPropertiesConfigProvider ssoPropertiesConfigProvider(@Autowired(required = false) List<SsoPropertiesConfig> configs) {
        return new SsoPropertiesConfigProvider(configs);
    }

    @Bean
    @ConditionalOnMissingBean(SsoConfigManager.class)
    public SsoConfigManager ssoConfigManager(@Autowired(required = false) List<SsoConfigProvider> providers) {
        return new SsoConfigManager(providers);
    }
}
