package org.hzero.oauth.security.social;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.hzero.oauth.security.config.SecurityProperties;
import org.hzero.oauth.security.service.LoginRecordService;
import org.hzero.oauth.security.service.UserAccountService;
import org.hzero.oauth.security.service.UserDetailsBuilder;
import org.hzero.starter.social.core.configuration.SocialPropertyService;
import org.hzero.starter.social.core.provider.SocialProviderRepository;
import org.hzero.starter.social.core.provider.SocialUserProviderRepository;
import org.hzero.starter.social.core.security.SocialAuthenticationProvider;
import org.hzero.starter.social.core.security.SocialSuccessHandler;
import org.hzero.starter.social.core.security.SocialUserDetailsService;

/**
 * Social 配置
 *
 * @author bojiangzhou 2020/08/24
 */
@Configuration
public class SocialSecurityConfiguration {

    @Bean
    @ConditionalOnMissingBean(SocialProviderRepository.class)
    public SocialProviderRepository socialProviderRepository() {
        return new CustomSocialProviderRepository();
    }

    @Bean
    @ConditionalOnMissingBean(SocialUserProviderRepository.class)
    public SocialUserProviderRepository socialUserProviderRepository() {
        return new CustomSocialUserProviderRepository();
    }

    @Bean
    @ConditionalOnMissingBean(SocialUserDetailsService.class)
    public SocialUserDetailsService socialUserDetailsService(UserAccountService userAccountService,
                                                             UserDetailsBuilder userDetailsBuilder,
                                                             LoginRecordService loginRecordService) {
        return new CustomSocialUserDetailsService(userAccountService, userDetailsBuilder, loginRecordService);
    }

    @Bean
    @ConditionalOnMissingBean(SocialAuthenticationProvider.class)
    public SocialAuthenticationProvider socialAuthenticationProvider(SocialUserProviderRepository socialUserProviderRepository,
                                                                     SocialUserDetailsService socialUserDetailsService) {
        return new CustomSocialAuthenticationProvider(socialUserProviderRepository, socialUserDetailsService);
    }

    @Bean
    @ConditionalOnMissingBean(SocialSuccessHandler.class)
    public SocialSuccessHandler socialSuccessHandler(SecurityProperties securityProperties) {
        return new CustomSocialSuccessHandler(securityProperties);
    }

    @Bean
    @ConditionalOnMissingBean(CustomSocialFailureHandler.class)
    public CustomSocialFailureHandler socialFailureHandler(SecurityProperties securityProperties) {
        return new CustomSocialFailureHandler(securityProperties);
    }

    @Bean
    @ConditionalOnMissingBean(SocialPropertyService.class)
    public SocialPropertyService socialPropertyService(SecurityProperties securityProperties) {
        return new CustomSocialPropertyService(securityProperties);
    }

}
