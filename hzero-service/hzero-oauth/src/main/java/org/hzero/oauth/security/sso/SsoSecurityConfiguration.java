package org.hzero.oauth.security.sso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.hzero.oauth.security.config.SecurityProperties;
import org.hzero.oauth.security.service.LoginRecordService;
import org.hzero.oauth.security.service.UserAccountService;
import org.hzero.oauth.security.service.UserDetailsBuilder;
import org.hzero.sso.core.configuration.SsoProperties;
import org.hzero.sso.core.configuration.SsoPropertyService;
import org.hzero.sso.core.service.SsoLogoutUrlRecordService;
import org.hzero.sso.core.service.SsoUserDetailsService;

/**
 * SSO 配置
 *
 * @author bojiangzhou 2020/08/24
 */
@Configuration
public class SsoSecurityConfiguration {

    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private UserDetailsBuilder userDetailsBuilder;
    @Autowired
    private LoginRecordService loginRecordService;

    @Bean
    @ConditionalOnMissingBean(SsoUserDetailsService.class)
    public SsoUserDetailsService ssoUserDetailsService() {
        return new CustomSsoUserDetailsService(userAccountService, userDetailsBuilder, loginRecordService);
    }

    @Bean
    @ConditionalOnMissingBean(SsoLogoutUrlRecordService.class)
    public SsoLogoutUrlRecordService ssoLogoutUrlRecordService() {
        return new CustomSsoLogoutUrlRecordService(loginRecordService);
    }

    @Bean
    public SsoPropertyService ssoPropertyService(SecurityProperties securityProperties, SsoProperties ssoProperties) {
        return new CustomSsoPropertyService(securityProperties, ssoProperties);
    }

    @Bean
    @ConditionalOnMissingBean(SsoClientLogoutListener.class)
    public SsoClientLogoutListener ssoClientLogoutListener() {
        return new SsoClientLogoutListener();
    }

}
