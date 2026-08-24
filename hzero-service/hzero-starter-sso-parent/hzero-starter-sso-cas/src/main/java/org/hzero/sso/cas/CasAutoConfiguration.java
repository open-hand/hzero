package org.hzero.sso.cas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.hzero.core.redis.RedisHelper;
import org.hzero.sso.cas.common.UrlSuffixSsoPropertiesConfig;
import org.hzero.sso.core.common.config.SsoConfigManager;
import org.hzero.sso.core.configuration.SsoProperties;
import org.hzero.sso.core.service.SsoUserDetailsService;

@Configuration
public class CasAutoConfiguration {

    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private SsoUserDetailsService userDetailsService;

    @Bean
    @ConditionalOnMissingBean(CasAuthenticationRouter.class)
    public CasAuthenticationRouter casAuthenticationRouter(CasServiceHelper casServiceHelper) {
        return new CasAuthenticationRouter(casServiceHelper);
    }

    @Bean
    @ConditionalOnMissingBean(CasAuthenticationProvider.class)
    public CasAuthenticationProvider casAuthenticationProvider(CasServiceHelper casServiceHelper, SsoConfigManager ssoConfigManager) {
        CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider(userDetailsService, casServiceHelper);
        casAuthenticationProvider.setSsoConfigManager(ssoConfigManager);
        return casAuthenticationProvider;
    }

    @Bean
    @ConditionalOnMissingBean(CasAuthenticationSuccessHandler.class)
    public CasAuthenticationSuccessHandler casAuthenticationSuccessHandler(CasServiceHelper casServiceHelper) {
        return new CasAuthenticationSuccessHandler(casServiceHelper);
    }

    @Bean
    @ConditionalOnMissingBean(CasServerLogoutHandler.class)
    public CasServerLogoutHandler casLogoutHandler() {
        return new CasServerLogoutHandler(redisHelper);
    }

    @Bean
    @ConditionalOnMissingBean(CasAuthorizeSuccessHandler.class)
    public CasAuthorizeSuccessHandler casAuthorizeSuccessHandler() {
        return new CasAuthorizeSuccessHandler(redisHelper);
    }

    @Bean
    @ConditionalOnMissingBean(CasClientLogoutHandler.class)
    public CasClientLogoutHandler casLogoutSuccessHandler() {
        return new CasClientLogoutHandler(redisHelper);
    }

    @Bean
    @ConditionalOnMissingBean(CasAuthenticationFactory.class)
    public CasAuthenticationFactory casAuthenticationFactory() {
        return new CasAuthenticationFactory();
    }

    @Bean
    @ConditionalOnMissingBean(CasServiceHelper.class)
    public CasServiceHelper casServiceHelper() {
        return new CasServiceHelper();
    }

    @Bean
    @ConditionalOnMissingBean(UrlSuffixSsoPropertiesConfig.class)
    public UrlSuffixSsoPropertiesConfig urlSuffixPropertiesConfig(SsoProperties ssoProperties) {
        return new UrlSuffixSsoPropertiesConfig(ssoProperties);
    }
}
