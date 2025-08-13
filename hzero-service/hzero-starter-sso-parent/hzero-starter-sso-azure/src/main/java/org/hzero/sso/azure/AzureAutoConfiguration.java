package org.hzero.sso.azure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.hzero.sso.core.service.SsoUserDetailsService;

@Configuration
public class AzureAutoConfiguration {

    @Autowired
    private SsoUserDetailsService userDetailsService;

    @Bean
    @ConditionalOnMissingBean(AzureAuthenticationProvider.class)
    public AzureAuthenticationProvider azureAuthenticationProvider() {
        return new AzureAuthenticationProvider(userDetailsService);
    }

    @Bean
    @ConditionalOnMissingBean(AzureAuthenticationRouter.class)
    public AzureAuthenticationRouter azureAuthenticationRouter() {
        return new AzureAuthenticationRouter();
    }

    @Bean
    @ConditionalOnMissingBean(AzureAuthorizeSuccessHandler.class)
    public AzureAuthorizeSuccessHandler azureAuthorizeSuccessHandler() {
        return new AzureAuthorizeSuccessHandler();
    }

    @Bean
    @ConditionalOnMissingBean(AzureAuthenticationFactory.class)
    public AzureAuthenticationFactory azureAuthenticationFactory() {
        return new AzureAuthenticationFactory();
    }

}
