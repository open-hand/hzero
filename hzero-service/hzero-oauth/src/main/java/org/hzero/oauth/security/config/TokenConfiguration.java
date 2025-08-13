package org.hzero.oauth.security.config;

import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;

import org.hzero.oauth.security.sms.SmsAuthenticationDetailsSource;
import org.hzero.oauth.security.sms.SmsAuthenticationProvider;
import org.hzero.oauth.security.token.MobileLoginTokenService;
import org.hzero.oauth.security.token.OpenLoginTokenService;
import org.hzero.starter.social.core.configuration.SocialWebSecurityConfigurerAdapter;
import org.hzero.starter.social.core.provider.SocialProviderRepository;
import org.hzero.starter.social.core.security.SocialAuthenticationProvider;

/**
 *
 * @author bojiangzhou 2019/09/06
 */
@Configuration
@AutoConfigureAfter({AuthorizationServerEndpointsConfiguration.class, SocialWebSecurityConfigurerAdapter.class})
public class TokenConfiguration {

    private final AuthorizationServerEndpointsConfigurer endpoints = new AuthorizationServerEndpointsConfigurer();

    @Autowired
    private final List<AuthorizationServerConfigurer> configurers = Collections.emptyList();

    @PostConstruct
    public void init() {
        for (AuthorizationServerConfigurer configurer : configurers) {
            try {
                configurer.configure(endpoints);
            } catch (Exception e) {
                throw new IllegalStateException("Cannot configure endpoints", e);
            }
        }
    }

    @Bean
    public MobileLoginTokenService mobileLoginTokenService(SmsAuthenticationProvider smsAuthenticationProvider,
                                                           SmsAuthenticationDetailsSource authenticationDetailsSource,
                                                           SecurityProperties securityProperties) {
        MobileLoginTokenService tokenService = new MobileLoginTokenService(
                endpoints.getTokenGranter(),
                endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory(),
                smsAuthenticationProvider,
                authenticationDetailsSource,
                securityProperties
        );
        return tokenService;
    }


    @Bean
    public OpenLoginTokenService openLoginTokenService(SocialAuthenticationProvider socialAuthenticationProvider,
                                                       SocialProviderRepository socialProviderRepository) {
        OpenLoginTokenService tokenService = new OpenLoginTokenService(
                endpoints.getTokenGranter(),
                endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory(),
                socialAuthenticationProvider,
                socialProviderRepository
        );
        return tokenService;
    }

}
