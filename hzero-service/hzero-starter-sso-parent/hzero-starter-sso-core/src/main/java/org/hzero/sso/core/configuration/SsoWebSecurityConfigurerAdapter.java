package org.hzero.sso.core.configuration;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.hzero.sso.core.security.SsoEntryFilter;
import org.hzero.sso.core.security.SsoSecurityConfigurer;
import org.hzero.sso.core.service.SsoLogoutUrlRecordService;
import org.hzero.sso.core.service.SsoTokenClearService;
import org.hzero.sso.core.support.CompatibleService;
import org.hzero.sso.core.support.SslUtils;
import org.hzero.sso.core.support.SsoAuthenticationLocator;

/**
 * SSO 配置
 *
 * @author bojiangzhou 2020/08/18
 */
@Order(-50)
@Configuration
public class SsoWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    private SsoPropertyService propertyService;
    @Autowired
    private SsoProperties ssoProperties;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] permitPaths = new String[]{propertyService.getProcessUrl()};
        permitPaths = ArrayUtils.addAll(permitPaths, CompatibleService.getSsoUris());
        http
                .requestMatchers()
                .antMatchers(permitPaths)
                .and()
                .authorizeRequests()
                .anyRequest()
                .permitAll()
                .and()
                .csrf()
                .disable()
        ;

        SsoSecurityConfigurer configurer = new SsoSecurityConfigurer();
        configurer
                .setFilterProcessesUrl(propertyService.getProcessUrl())
        ;

        http.apply(configurer);

        if (ssoProperties.isIgnoreSsl()) {
            SslUtils.ignoreSsl();
        }
    }

    @Bean
    public FilterRegistrationBean<SsoEntryFilter> registerSsoEntryFilter(SsoAuthenticationLocator authenticationLocator,
                                                                          SsoTokenClearService tokenClearService,
                                                                          SsoLogoutUrlRecordService logoutUrlRecordService) {
        FilterRegistrationBean<SsoEntryFilter> registration = new FilterRegistrationBean<>();
        SsoEntryFilter filter = new SsoEntryFilter(authenticationLocator, tokenClearService, logoutUrlRecordService);
        filter.setFilterProcessesUrl(propertyService.getProcessUrl());
        registration.setFilter(filter);
        registration.addUrlPatterns(propertyService.getProcessUrl().replace("/**", "/*"));
        for (String ssoUri : CompatibleService.getSsoUris()) {
            registration.addUrlPatterns(ssoUri.replace("/**", "/*"));
        }
        registration.setName("ssoEntryFilter");
        registration.setOrder(SsoEntryFilter.ORDER);
        return registration;
    }

}
