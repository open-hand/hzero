package io.choerodon.resource.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import io.choerodon.core.oauth.CustomTokenConverter;
import io.choerodon.resource.filter.JwtTokenExtractor;
import io.choerodon.resource.filter.JwtTokenFilter;
import io.choerodon.resource.permission.PublicPermissionOperationPlugin;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.properties.CoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import static javax.servlet.DispatcherType.REQUEST;

/**
 * @author dongfan117@gmail.com
 */
public class ChoerodonResourceServerConfiguration extends WebSecurityConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChoerodonResourceServerConfiguration.class);

    private final CoreProperties properties;

    private final AdminServerProperties adminServerProperties;

    public ChoerodonResourceServerConfiguration(CoreProperties properties, AdminServerProperties adminServerProperties) {
        this.properties = properties;
        this.adminServerProperties = adminServerProperties;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/actuator/**").antMatchers("/prometheus");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(adminServerProperties.getContextPath());
        http
            .headers().frameOptions().disable()
            .and()
            .httpBasic()
            .and()
            .formLogin()
            .loginPage(adminServerProperties.getContextPath() + "/login")
            .successHandler(successHandler)
            .and()
            .logout().logoutUrl(adminServerProperties.getContextPath() + "/logout")
            .and()
            .httpBasic()
            .and()
            .csrf().disable()
            .authorizeRequests()
            .antMatchers(adminServerProperties.getContextPath() + "/login").permitAll()
            .antMatchers(adminServerProperties.getContextPath() + "/assets/**").permitAll()
            .antMatchers(adminServerProperties.getContextPath() + "/**").authenticated()
            .anyRequest().permitAll();
    }

    @Bean
    public FilterRegistrationBean someFilterRegistration(JwtTokenFilter jwtTokenFilter) {
        String[] pattern = StringUtils.replace(properties.getResource().getPattern(), " ", "").split(",");
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(jwtTokenFilter);
        registration.addUrlPatterns(pattern);
        registration.setName("jwtTokenFilter");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE - 100);
        registration.setDispatcherTypes(REQUEST);
        return registration;
    }

    @Bean
    public JwtTokenExtractor jwtTokenExtractor() {
        return new JwtTokenExtractor();
    }

    @Bean
    public JwtTokenFilter jwtTokenFilter(PublicPermissionOperationPlugin publicPermissionOperationPlugin,
                                         JwtTokenExtractor jwtTokenExtractor) {
        return new JwtTokenFilter(tokenServices(),
                jwtTokenExtractor,
                publicPermissionOperationPlugin.getPublicPaths(),
                properties.getResource().getSkipPath());
    }

    /**
     * DefaultTokenService Bean
     *
     * @return DefaultTokenService对象
     */
    private DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }

    private TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    /**
     * 返回converter
     *
     * @return converter
     */
    private JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setAccessTokenConverter(new CustomTokenConverter());
        converter.setSigningKey(properties.getOauthJwtKey());
        try {
            converter.afterPropertiesSet();
        } catch (Exception e) {
            LOGGER.warn("error.ResourceServerConfiguration.accessTokenConverter {}", e.getMessage());
        }
        return converter;
    }

}