package org.hzero.oauth.security.sms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.hzero.core.captcha.CaptchaMessageHelper;
import org.hzero.oauth.domain.service.AuditLoginService;
import org.hzero.oauth.security.config.SecurityProperties;
import org.hzero.oauth.security.service.LoginRecordService;
import org.hzero.oauth.security.service.UserAccountService;
import org.hzero.oauth.security.sms.SmsAuthenticationDetailsSource;
import org.hzero.oauth.security.sms.SmsAuthenticationFailureHandler;
import org.hzero.oauth.security.sms.SmsAuthenticationProvider;

/**
 * 短信登录配置
 *
 * @author bojiangzhou 2019/02/25
 */
@Configuration
public class SmsLoginConfiguration {

    @Autowired
    private UserDetailsService userDetailsService;
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
    public SmsAuthenticationFailureHandler smsAuthenticationFailureHandler() {
        return new SmsAuthenticationFailureHandler(loginRecordService, securityProperties, auditLoginService);
    }

    @Bean
    public SmsAuthenticationDetailsSource smsAuthenticationDetailsSource() {
        return new SmsAuthenticationDetailsSource();
    }

    @Bean
    public SmsAuthenticationProvider smsAuthenticationProvider() {
        return new SmsAuthenticationProvider(userDetailsService, captchaMessageHelper,
                userAccountService, loginRecordService);
    }

}
