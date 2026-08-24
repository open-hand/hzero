package org.hzero.oauth.security.secheck.config;

import org.hzero.oauth.security.secheck.SecCheckAuthenticationFilter;
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

import javax.servlet.http.HttpServletRequest;

/**
 * 二次校验登录配置器
 *
 * @author bergturing 2020/08/25
 */
public class SecCheckLoginConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private static final String SEC_CHECK_DEFAULT_LOGIN_PROCESS_URL = "/login/secCheck";

    /**
     * 授权过滤器
     */
    private final SecCheckAuthenticationFilter authFilter;
    /**
     * 授权详细信息源
     */
    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;
    /**
     * 授权成功处理器
     */
    private AuthenticationSuccessHandler successHandler;
    /**
     * 授权失败处理器
     */
    private AuthenticationFailureHandler failureHandler;

    /**
     * 默认手机+短信验证码 登录处理地址 [POST "/login/secCheck"].
     */
    public SecCheckLoginConfigurer() {
        this.authFilter = new SecCheckAuthenticationFilter();
        this.loginProcessingUrl(SEC_CHECK_DEFAULT_LOGIN_PROCESS_URL);
        this.secCheckTypeParameter("secCheckType");
    }

    public SecCheckLoginConfigurer secCheckTypeParameter(String secCheckTypeParameter) {
        this.authFilter.setSecCheckTypeParameter(secCheckTypeParameter);
        return this;
    }

    public SecCheckLoginConfigurer loginProcessingUrl(String loginProcessingUrl) {
        this.authFilter.setRequiresAuthenticationRequestMatcher(createLoginProcessingUrlMatcher(loginProcessingUrl));
        return this;
    }

    public SecCheckLoginConfigurer authenticationDetailsSource(
            AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        this.authenticationDetailsSource = authenticationDetailsSource;
        return this;
    }

    public SecCheckLoginConfigurer successHandler(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
        return this;
    }

    public SecCheckLoginConfigurer failureHandler(AuthenticationFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
        return this;
    }

    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        Assert.notNull(this.successHandler, "successHandler should not be null.");
        Assert.notNull(this.failureHandler, "failureHandler should not be null.");
        this.authFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        if (this.successHandler != null) {
            this.authFilter.setAuthenticationSuccessHandler(this.successHandler);
        }
        if (this.failureHandler != null) {
            this.authFilter.setAuthenticationFailureHandler(this.failureHandler);
        }
        if (this.authenticationDetailsSource != null) {
            this.authFilter.setAuthenticationDetailsSource(this.authenticationDetailsSource);
        }

        // 将二次校验认证过滤器加到 UsernamePasswordAuthenticationFilter 之后
        http.addFilterAfter(this.authFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
