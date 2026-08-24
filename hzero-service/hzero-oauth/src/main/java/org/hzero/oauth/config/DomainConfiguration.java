package org.hzero.oauth.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.hzero.oauth.domain.service.*;
import org.hzero.oauth.domain.service.impl.*;

/**
 * 领域服务配置
 *
 * @author bojiangzhou 2018/08/02
 */
@Configuration
public class DomainConfiguration {

    @Bean
    @ConditionalOnMissingBean(AuditLoginService.class)
    public AuditLoginService auditLoginService() {
        return new AuditLoginServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(ClearResourceService.class)
    public ClearResourceService clearResourceService() {
        return new ClearResourceServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(LanguageService.class)
    public LanguageService languageService() {
        return new LanguageServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(OauthPageService.class)
    public OauthPageService oauthPageService() {
        return new OauthPageServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(PasswordService.class)
    public PasswordService passwordService() {
        return new PasswordServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(UserLoginService.class)
    public UserLoginService userLoginService() {
        return new UserLoginServiceImpl();
    }


}
