package org.hzero.oauth.security.secheck.config;

import org.hzero.core.captcha.CaptchaMessageHelper;
import org.hzero.oauth.domain.service.AuditLoginService;
import org.hzero.oauth.security.config.SecurityProperties;
import org.hzero.oauth.security.custom.CustomUserDetailsService;
import org.hzero.oauth.security.secheck.SecCheckAuthenticationDetailsSource;
import org.hzero.oauth.security.secheck.SecCheckAuthenticationFailureHandler;
import org.hzero.oauth.security.secheck.SecCheckAuthenticationProvider;
import org.hzero.oauth.security.secheck.SecCheckAuthenticationSuccessHandler;
import org.hzero.oauth.security.service.LoginRecordService;
import org.hzero.oauth.security.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 二次认证登录配置
 *
 * @author bergtuirng 2020/08/25
 */
@Configuration
public class SecCheckLoginConfiguration {

    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private CaptchaMessageHelper captchaMessageHelper;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private LoginRecordService loginRecordService;
    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private AuditLoginService auditLoginService;

    @Bean
    @ConditionalOnMissingBean(SecCheckAuthenticationSuccessHandler.class)
    public SecCheckAuthenticationSuccessHandler secCheckAuthenticationSuccessHandler() {
        return new SecCheckAuthenticationSuccessHandler(this.securityProperties);
    }

    @Bean
    @ConditionalOnMissingBean(SecCheckAuthenticationFailureHandler.class)
    public SecCheckAuthenticationFailureHandler secCheckAuthenticationFailureHandler() {
        return new SecCheckAuthenticationFailureHandler(this.loginRecordService, this.securityProperties, this.auditLoginService);
    }

    @Bean
    @ConditionalOnMissingBean(SecCheckAuthenticationDetailsSource.class)
    public SecCheckAuthenticationDetailsSource secCheckAuthenticationDetailsSource() {
        return new SecCheckAuthenticationDetailsSource();
    }

    @Bean
    @ConditionalOnMissingBean(SecCheckAuthenticationProvider.class)
    public SecCheckAuthenticationProvider secCheckAuthenticationProvider() {
        return new SecCheckAuthenticationProvider(this.userDetailsService, this.captchaMessageHelper,
                this.userAccountService, this.loginRecordService);
    }

}
