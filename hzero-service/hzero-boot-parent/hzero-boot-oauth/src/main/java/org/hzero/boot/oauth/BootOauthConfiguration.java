package org.hzero.boot.oauth;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.hzero.boot.oauth.domain.service.BaseUserService;
import org.hzero.boot.oauth.domain.service.PasswordErrorTimesService;
import org.hzero.boot.oauth.domain.service.UserPasswordService;
import org.hzero.boot.oauth.domain.service.impl.BaseUserServiceImpl;
import org.hzero.boot.oauth.domain.service.impl.PasswordErrorTimesServiceImpl;
import org.hzero.boot.oauth.domain.service.impl.UserPasswordServiceImpl;
import org.hzero.boot.oauth.config.BootOauthProperties;
import org.hzero.boot.oauth.util.CustomBCryptPasswordEncoder;

/**
 *
 * @author bojiangzhou 2019/08/07
 */
@ComponentScan(value = {
        "org.hzero.boot.oauth.domain",
        "org.hzero.boot.oauth.infra",
        "org.hzero.boot.oauth.policy",
        "org.hzero.boot.oauth.strategy",
        "org.hzero.boot.oauth.config",
})
@Configuration
@EnableConfigurationProperties(BootOauthProperties.class)
public class BootOauthConfiguration {

    @Bean
    @ConditionalOnMissingBean(BaseUserService.class)
    public BaseUserService baseUserService() {
        return new BaseUserServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(PasswordErrorTimesService.class)
    public PasswordErrorTimesService passwordErrorTimesService() {
        return new PasswordErrorTimesServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(UserPasswordService.class)
    public UserPasswordService userPasswordService() {
        return new UserPasswordServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return new CustomBCryptPasswordEncoder();
    }

}
