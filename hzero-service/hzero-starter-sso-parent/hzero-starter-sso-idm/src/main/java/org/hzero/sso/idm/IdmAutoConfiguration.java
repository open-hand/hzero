package org.hzero.sso.idm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.hzero.sso.core.service.SsoUserDetailsService;


@Configuration
public class IdmAutoConfiguration {

    @Autowired
    private SsoUserDetailsService userDetailsService;

    @Bean
    @ConditionalOnMissingBean(IdmAuthenticationProvider.class)
    public IdmAuthenticationProvider idmAuthenticationProvider() {
        return new IdmAuthenticationProvider(userDetailsService);
    }

    @Bean
    @ConditionalOnMissingBean(IdmAuthenticationRouter.class)
    public IdmAuthenticationRouter idmAuthenticationRouter() {
        return new IdmAuthenticationRouter();
    }

    @Bean
    @ConditionalOnMissingBean(IdmAuthorizeSuccessHandler.class)
    public IdmAuthorizeSuccessHandler idmAuthorizeSuccessHandler() {
        return new IdmAuthorizeSuccessHandler();
    }

    @Bean
    @ConditionalOnMissingBean(IdmAuthenticationFactory.class)
    public IdmAuthenticationFactory idmAuthenticationFactory() {
        return new IdmAuthenticationFactory();
    }

    @Bean
    @ConditionalOnMissingBean(IdmAuthenticationSuccessHandler.class)
    public IdmAuthenticationSuccessHandler idmAuthenticationSuccessHandler() {
        return new IdmAuthenticationSuccessHandler();
    }

}
