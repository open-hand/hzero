package org.hzero.sso.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.hzero.sso.core.service.SsoUserDetailsService;

@Configuration
public class AuthAutoConfiguration {

    @Autowired
    private SsoUserDetailsService userDetailsService;

    @Bean
    @ConditionalOnMissingBean(AuthAuthenticationRouter.class)
    public AuthAuthenticationRouter authAuthenticationRouter() {
        return new AuthAuthenticationRouter();
    }

    @Bean
    @ConditionalOnMissingBean(AuthAuthenticationProvider.class)
    public AuthAuthenticationProvider authenticationProvider() {
        return new AuthAuthenticationProvider(userDetailsService);
    }

    @Bean
    @ConditionalOnMissingBean(AuthServerLogoutHandler.class)
    public AuthServerLogoutHandler authLogoutHandler() {
        return new AuthServerLogoutHandler();
    }

    @Bean
    @ConditionalOnMissingBean(AuthAuthorizeSuccessHandler.class)
    public AuthAuthorizeSuccessHandler authAuthorizeSuccessHandler() {
        return new AuthAuthorizeSuccessHandler();
    }

    @Bean
    @ConditionalOnMissingBean(AuthAuthenticationFactory.class)
    public AuthAuthenticationFactory authAuthenticationFactory() {
        return new AuthAuthenticationFactory();
    }

}
