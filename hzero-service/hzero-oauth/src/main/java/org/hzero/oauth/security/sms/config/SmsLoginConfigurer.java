package org.hzero.oauth.security.sms.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import org.hzero.oauth.security.sms.SmsAuthenticationFilter;

/**
 * 短信登录配置器
 *
 * @author bojiangzhou 2019/02/25
 */
public class SmsLoginConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private static final String SMS_DEFAULT_LOGIN_PROCESS_URL = "/login/sms";

    private SmsAuthenticationFilter authFilter;

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

    private AuthenticationSuccessHandler successHandler;

    private AuthenticationFailureHandler failureHandler;

    /**
     * 默认手机+短信验证码 登录处理地址 [POST "/login/sms"]. 默认手机参数 - mobile
     */
    public SmsLoginConfigurer() {
        authFilter = new SmsAuthenticationFilter();
        loginProcessingUrl(SMS_DEFAULT_LOGIN_PROCESS_URL);
        mobileParameter("phone");
    }

    public SmsLoginConfigurer mobileParameter(String mobileParameter) {
        authFilter.setMobileParameter(mobileParameter);
        return this;
    }

    public SmsLoginConfigurer loginProcessingUrl(String loginProcessingUrl) {
        authFilter.setRequiresAuthenticationRequestMatcher(createLoginProcessingUrlMatcher(loginProcessingUrl));
        return this;
    }

    public SmsLoginConfigurer authenticationDetailsSource(
                    AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        this.authenticationDetailsSource = authenticationDetailsSource;
        return this;
    }

    public SmsLoginConfigurer successHandler(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
        return this;
    }

    public SmsLoginConfigurer failureHandler(AuthenticationFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
        return this;
    }

    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        Assert.notNull(successHandler, "successHandler should not be null.");
        Assert.notNull(failureHandler, "failureHandler should not be null.");
        authFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        if (this.successHandler != null) {
            authFilter.setAuthenticationSuccessHandler(this.successHandler);
        }
        if (this.failureHandler != null) {
            authFilter.setAuthenticationFailureHandler(this.failureHandler);
        }
        if (this.authenticationDetailsSource != null) {
            authFilter.setAuthenticationDetailsSource(this.authenticationDetailsSource);
        }
        // 将短信认证过滤器加到 UsernamePasswordAuthenticationFilter 之后
        http.addFilterAfter(authFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
