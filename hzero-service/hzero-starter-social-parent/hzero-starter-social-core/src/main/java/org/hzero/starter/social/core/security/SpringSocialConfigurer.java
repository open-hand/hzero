/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.hzero.starter.social.core.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.SocialAuthenticationFailureHandler;
import org.springframework.social.security.SocialAuthenticationServiceLocator;
import org.springframework.util.Assert;

import org.hzero.starter.social.core.provider.SocialProviderRepository;
import org.hzero.starter.social.core.provider.SocialUserProviderRepository;

/**
 * Configurer that adds {@link SocialAuthenticationFilter} to Spring Security's filter chain. Used
 * with Spring Security 3.2's Java-based configuration support, when overriding
 * WebSecurityConfigurerAdapter#configure(HttpSecurity):
 *
 * <pre>
 * protected void configure(HttpSecurity http) throws Exception {
 *   http.
 *     // HTTP security configuration details snipped
 *     .and()
 *        .apply(new SpringSocialHttpConfigurer());
 * }
 * </pre>
 *
 * @author Craig Walls
 */
public class SpringSocialConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private static final String DEFAULT_FILTER_PROCESSES_URL = "/open/**";

    private String postLoginUrl;

    private String postFailureUrl;

    private String bindUrl;

    private boolean attemptBind;

    private boolean updateBind = true;

    private String defaultFailureUrl;

    private String filterProcessesUrl = DEFAULT_FILTER_PROCESSES_URL;

    private boolean alwaysUsePostLoginUrl = false;

    private boolean enableHttps = false;

    private AuthenticationSuccessHandler successHandler;
    private AuthenticationFailureHandler failureHandler;

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

    /**
     * Constructs a SpringSocialHttpConfigurer. Requires that {@link UsersConnectionRepository},
     * {@link SocialAuthenticationServiceLocator}, and {@link SocialUserDetailsService} beans be
     * available in the application context.
     */
    public SpringSocialConfigurer() {}

    @Override
    public void configure(HttpSecurity http) throws Exception {
        ApplicationContext applicationContext = http.getSharedObject(ApplicationContext.class);
        SocialAuthenticationServiceLocator authServiceLocator = getDependency(applicationContext, SocialAuthenticationServiceLocator.class);
        SocialProviderRepository socialProviderRepository = getDependency(applicationContext, SocialProviderRepository.class);
        SocialUserProviderRepository socialUserProviderRepository = getDependency(applicationContext, SocialUserProviderRepository.class);
        SocialAuthenticationProvider socialAuthenticationProvider = getDependency(applicationContext, SocialAuthenticationProvider.class);
        TokenStore tokenStore = getDependency(applicationContext, TokenStore.class);

        SocialAuthenticationFilter filter = new SocialAuthenticationFilter(http.getSharedObject(AuthenticationManager.class),
                tokenStore,
                socialUserProviderRepository,
                authServiceLocator,
                socialProviderRepository
        );

        RememberMeServices rememberMe = http.getSharedObject(RememberMeServices.class);
        if (rememberMe != null) {
            filter.setRememberMeServices(rememberMe);
        }

        if (postLoginUrl != null) {
            filter.setPostLoginUrl(postLoginUrl);
            filter.setAlwaysUsePostLoginUrl(alwaysUsePostLoginUrl);
        }

        if (postFailureUrl != null) {
            filter.setPostFailureUrl(postFailureUrl);
        }

        if (bindUrl != null) {
            filter.setBindUrl(bindUrl);
        }
        filter.setEnableHttps(enableHttps);
        filter.setAttemptBind(attemptBind);
        filter.setUpdateBind(updateBind);

        if (defaultFailureUrl != null) {
            filter.setDefaultFailureUrl(defaultFailureUrl);
        }

        http
            .authenticationProvider(socialAuthenticationProvider)
            .addFilterBefore(postProcess(filter), AbstractPreAuthenticatedProcessingFilter.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T postProcess(T object) {
        SocialAuthenticationFilter filter = (SocialAuthenticationFilter) super.postProcess(object);
        filter.setFilterProcessesUrl(filterProcessesUrl);
        if (this.successHandler != null) {
            filter.setAuthenticationSuccessHandler(this.successHandler);
        }
        if (this.failureHandler != null) {
            filter.setAuthenticationFailureHandler(new SocialAuthenticationFailureHandler(this.failureHandler));
        }
        if (this.authenticationDetailsSource != null) {
            filter.setAuthenticationDetailsSource(this.authenticationDetailsSource);
        }
        return (T) filter;
    }

    private <T> T getDependency(ApplicationContext applicationContext, Class<T> dependencyType) {
        try {
            T dependency = applicationContext.getBean(dependencyType);
            return dependency;
        } catch (NoSuchBeanDefinitionException e) {
            throw new IllegalStateException("SpringSocialConfigurer depends on " + dependencyType.getName()
                            + ". No single bean of that type found in application context.", e);
        }
    }

    /**
     * Sets the URL to land on after a successful login.
     * 
     * @param postLoginUrl the URL to redirect to after a successful login
     * @return this SpringSocialConfigurer for chained configuration
     */
    public SpringSocialConfigurer postLoginUrl(String postLoginUrl) {
        this.postLoginUrl = postLoginUrl;
        return this;
    }

    /**
     * If true, always redirect to postLoginUrl, even if a pre-signin target is in the request cache.
     * 
     * @param alwaysUsePostLoginUrl if true, always redirect to the postLoginUrl
     * @return this SpringSocialConfigurer for chained configuration
     */
    public SpringSocialConfigurer alwaysUsePostLoginUrl(boolean alwaysUsePostLoginUrl) {
        this.alwaysUsePostLoginUrl = alwaysUsePostLoginUrl;
        return this;
    }

    /**
     * Sets the URL to land on after a failed login.
     * 
     * @param postFailureUrl the URL to redirect to after a failed login
     * @return this SpringSocialConfigurer for chained configuration
     */
    public SpringSocialConfigurer postFailureUrl(String postFailureUrl) {
        this.postFailureUrl = postFailureUrl;
        return this;
    }

    /**
     * bind page
     */
    public SpringSocialConfigurer bindUrl(String bindUrl) {
        this.bindUrl = bindUrl;
        return this;
    }

    public SpringSocialConfigurer setAttemptBind(boolean attemptBind) {
        this.attemptBind = attemptBind;
        return this;
    }

    public SpringSocialConfigurer setUpdateBind(boolean updateBind) {
        this.updateBind = updateBind;
        return this;
    }

    public SpringSocialConfigurer setEnableHttps(boolean enableHttps) {
        this.enableHttps = enableHttps;
        return this;
    }

    /**
     * Sets the URL to redirect to if authentication fails or if authorization is denied by the user.
     * 
     * @param defaultFailureUrl the URL to redirect to after an authentication fail or authorization
     *        deny
     * @return this SpringSocialConfigurer for chained configuration
     */
    public SpringSocialConfigurer defaultFailureUrl(String defaultFailureUrl) {
        this.defaultFailureUrl = defaultFailureUrl;
        return this;
    }

    public SpringSocialConfigurer successHandler(SocialSuccessHandler successHandler) {
        this.successHandler = successHandler;
        return this;
    }

    public SpringSocialConfigurer failureHandler(SocialFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
        return this;
    }

    public SpringSocialConfigurer authenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        this.authenticationDetailsSource = authenticationDetailsSource;
        return this;
    }

    public SpringSocialConfigurer setFilterProcessesUrl(String filterProcessesUrl) {
        Assert.notNull(filterProcessesUrl, "filter process url should not be null.");
        this.filterProcessesUrl = filterProcessesUrl;
        return this;
    }
}
