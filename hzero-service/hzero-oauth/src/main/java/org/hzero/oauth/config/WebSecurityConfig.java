package org.hzero.oauth.config;

import org.apache.commons.lang3.ArrayUtils;
import org.hzero.oauth.domain.service.ClearResourceFilter;
import org.hzero.oauth.domain.service.ClearResourceService;
import org.hzero.oauth.security.config.SecurityProperties;
import org.hzero.oauth.security.custom.*;
import org.hzero.oauth.security.secheck.SecCheckAuthenticationDetailsSource;
import org.hzero.oauth.security.secheck.SecCheckAuthenticationFailureHandler;
import org.hzero.oauth.security.secheck.SecCheckAuthenticationProvider;
import org.hzero.oauth.security.secheck.SecCheckAuthenticationSuccessHandler;
import org.hzero.oauth.security.secheck.config.SecCheckLoginConfigurer;
import org.hzero.oauth.security.sms.SmsAuthenticationDetailsSource;
import org.hzero.oauth.security.sms.SmsAuthenticationFailureHandler;
import org.hzero.oauth.security.sms.SmsAuthenticationProvider;
import org.hzero.oauth.security.sms.config.EnableSmsLogin;
import org.hzero.oauth.security.sms.config.SmsLoginConfigurer;
import org.hzero.sso.core.configuration.EnableSsoLogin;
import org.hzero.starter.social.core.configuration.EnableSocialLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.PortMapper;
import org.springframework.security.web.PortResolver;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.matcher.*;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author bojiangzhou
 */
@EnableSocialLogin
@EnableSmsLogin
@EnableSsoLogin
@Configuration
@Order(org.springframework.boot.autoconfigure.security.SecurityProperties.BASIC_AUTH_ORDER)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private CustomAuthenticationDetailsSource detailsSource;
    @Autowired
    private CustomAuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    private CustomAuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    private CustomLogoutSuccessHandler logoutSuccessHandler;
    @Autowired
    private ClearResourceService clearResourceService;
    @Autowired(required = false)
    @Qualifier("authenticationEntryPoint")
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired(required = false)
    private SmsAuthenticationDetailsSource smsAuthenticationDetailsSource;
    @Autowired(required = false)
    private SmsAuthenticationFailureHandler smsAuthenticationFailureHandler;
    @Autowired(required = false)
    private SmsAuthenticationProvider smsAuthenticationProvider;

    @Autowired
    private PortMapper portMapper;
    @Autowired
    private PortResolver portResolver;

    @Autowired(required = false)
    private SecCheckAuthenticationDetailsSource secCheckAuthenticationDetailsSource;
    private static final String[] PERMIT_PATHS = new String[]{
            "/login", "/login/**", "/open-bind", "/token/**", "/admin/**", "/oauth/user",
            "/v2/choerodon/**", "/choerodon/**", "/public/**", "/password/**",
            "/admin/**", "/static/**", "/saml/metadata", "/actuator/**", "/v2/market/**"
    };
    @Autowired(required = false)
    private SecCheckAuthenticationSuccessHandler secCheckAuthenticationSuccessHandler;
    @Autowired(required = false)
    private SecCheckAuthenticationProvider secCheckAuthenticationProvider;

    @Autowired(required = false)
    private CustomHttpSecurityConfigurer customHttpSecurityConfigurer;
    @Autowired(required = false)
    private SecCheckAuthenticationFailureHandler secCheckAuthenticationFailureHandler;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        String[] permitPaths = ArrayUtils.addAll(PERMIT_PATHS, securityProperties.getPermitPaths());

        http
                .authorizeRequests()
                .antMatchers(permitPaths)
                .permitAll()
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage(securityProperties.getLogin().getPage())
                .authenticationDetailsSource(detailsSource)
                .failureHandler(authenticationFailureHandler)
                .successHandler(authenticationSuccessHandler)
                .and()
                .logout().deleteCookies("access_token").invalidateHttpSession(true)
                .logoutSuccessHandler(logoutSuccessHandler)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .and()
                .portMapper()
                .portMapper(portMapper)
                .and()
                .requestCache()
                .requestCache(getRequestCache(http))
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .cors().disable()
                .headers().frameOptions().disable()
                .and()
                .csrf()
                .disable()
        ;

        // 配置短信登录方式
        SmsLoginConfigurer smsLoginConfigurer = new SmsLoginConfigurer();
        smsLoginConfigurer
                .authenticationDetailsSource(smsAuthenticationDetailsSource)
                .successHandler(authenticationSuccessHandler)
                .failureHandler(smsAuthenticationFailureHandler)
                .mobileParameter(securityProperties.getLogin().getMobileParameter())
                .loginProcessingUrl(securityProperties.getLogin().getMobileLoginProcessUrl())
        ;
        http.apply(smsLoginConfigurer);
        http.authenticationProvider(smsAuthenticationProvider);

        // 配置二次校验登录方式
        SecCheckLoginConfigurer secCheckLoginConfigurer = new SecCheckLoginConfigurer();
        secCheckLoginConfigurer
                .authenticationDetailsSource(this.secCheckAuthenticationDetailsSource)
                .successHandler(this.secCheckAuthenticationSuccessHandler)
                .failureHandler(this.secCheckAuthenticationFailureHandler)
                .secCheckTypeParameter(this.securityProperties.getLogin().getSecCheckTypeParameter())
                .loginProcessingUrl(this.securityProperties.getLogin().getSecCheckLoginProcessUrl());
        http.apply(secCheckLoginConfigurer);
        http.authenticationProvider(this.secCheckAuthenticationProvider);

        if (customHttpSecurityConfigurer != null) {
            customHttpSecurityConfigurer.configureHttpSecurity(http);
        }
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public FilterRegistrationBean<ClearResourceFilter> registerClearResourceFilter() {
        FilterRegistrationBean<ClearResourceFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ClearResourceFilter(clearResourceService));
        registration.addUrlPatterns("/*");
        registration.setName("clearResourceFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }


    private RequestCache getRequestCache(HttpSecurity http) {
        RequestCache result = http.getSharedObject(RequestCache.class);
        if (result != null) {
            return result;
        }
        HttpSessionRequestCache defaultCache = new HttpSessionRequestCache();
        defaultCache.setRequestMatcher(createDefaultSavedRequestMatcher(http));
        // 标准 HttpSessionRequestCache 无法设置 PortResolver
        defaultCache.setPortResolver(portResolver);
        return defaultCache;
    }

    @SuppressWarnings("unchecked")
    private RequestMatcher createDefaultSavedRequestMatcher(HttpSecurity http) {
        ContentNegotiationStrategy contentNegotiationStrategy = http
                .getSharedObject(ContentNegotiationStrategy.class);
        if (contentNegotiationStrategy == null) {
            contentNegotiationStrategy = new HeaderContentNegotiationStrategy();
        }

        RequestMatcher notFavIcon = new NegatedRequestMatcher(new AntPathRequestMatcher(
                "/**/favicon.ico"));

        MediaTypeRequestMatcher jsonRequest = new MediaTypeRequestMatcher(
                contentNegotiationStrategy, MediaType.APPLICATION_JSON);
        jsonRequest.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));
        RequestMatcher notJson = new NegatedRequestMatcher(jsonRequest);

        RequestMatcher notXRequestedWith = new NegatedRequestMatcher(
                new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest"));

        boolean isCsrfEnabled = http.getConfigurer(CsrfConfigurer.class) != null;

        List<RequestMatcher> matchers = new ArrayList<>();
        if (isCsrfEnabled) {
            RequestMatcher getRequests = new AntPathRequestMatcher("/**", "GET");
            matchers.add(0, getRequests);
        }
        matchers.add(notFavIcon);
        matchers.add(notJson);
        matchers.add(notXRequestedWith);

        return new AndRequestMatcher(matchers);
    }

}
