package org.hzero.sso.core.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import org.hzero.sso.core.domain.DomainRepository;
import org.hzero.sso.core.support.SsoAuthenticationLocator;

/**
 * 单点登录配置器
 *
 * @author bojiangzhou 2020/08/18
 */
public class SsoSecurityConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private String filterProcessesUrl;

    public SsoSecurityConfigurer() {}

    @Override
    public void configure(HttpSecurity http) throws Exception {
        ApplicationContext applicationContext = http.getSharedObject(ApplicationContext.class);
        DomainRepository domainRepository = getDependency(applicationContext, DomainRepository.class);
        SsoAuthenticationLocator authenticationLocator = getDependency(applicationContext, SsoAuthenticationLocator.class);
        SsoAuthenticationFilter filter = new SsoAuthenticationFilter(domainRepository, authenticationLocator);

        http.addFilterBefore(postProcess(filter), AbstractPreAuthenticatedProcessingFilter.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T postProcess(T object) {
        SsoAuthenticationFilter filter = (SsoAuthenticationFilter) super.postProcess(object);

        if (filterProcessesUrl != null) {
            filter.setFilterProcessesUrl(filterProcessesUrl);
        }

        return (T) filter;
    }

    private <T> T getDependency(ApplicationContext applicationContext, Class<T> dependencyType) {
        try {
            return applicationContext.getBean(dependencyType);
        } catch (NoSuchBeanDefinitionException e) {
            throw new IllegalStateException("SpringSocialConfigurer depends on " + dependencyType.getName()
                    + ". No single bean of that type found in application context.", e);
        }
    }

    public SsoSecurityConfigurer setFilterProcessesUrl(String filterProcessesUrl) {
        if (StringUtils.isBlank(filterProcessesUrl)) {
            throw new IllegalArgumentException("filterProcessesUrl must not be null");
        }
        this.filterProcessesUrl = filterProcessesUrl;
        return this;
    }
}
