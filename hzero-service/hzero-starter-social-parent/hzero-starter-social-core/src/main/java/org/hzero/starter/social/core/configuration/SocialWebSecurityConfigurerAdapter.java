package org.hzero.starter.social.core.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.hzero.starter.social.core.security.SocialFailureHandler;
import org.hzero.starter.social.core.security.SocialSuccessHandler;
import org.hzero.starter.social.core.security.SpringSocialConfigurer;

/**
 *
 * @author bojiangzhou 2019/09/01
 */
@Order(-100)
@EnableConfigurationProperties(value = {SocialProperties.class})
@Configuration
public class SocialWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    private SocialProperties properties;
    @Autowired(required = false)
    private SocialSuccessHandler socialSuccessHandler;
    @Autowired(required = false)
    private SocialFailureHandler socialFailureHandler;
    @Autowired
    private SocialPropertyService socialPropertyService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .antMatcher(properties.getProcessUrl())
            .authorizeRequests()
            .anyRequest()
            .permitAll()
            .and()
            .csrf()
            .disable()
        ;

        SpringSocialConfigurer configurer = new SpringSocialConfigurer();
        configurer
                .bindUrl(properties.getBindUrl())
                .setAttemptBind(properties.isAttemptBind())
                .setEnableHttps(socialPropertyService.isEnableHttps())
                .setFilterProcessesUrl(properties.getProcessUrl())
            ;

        if (socialSuccessHandler != null) {
            configurer.successHandler(socialSuccessHandler);
        }
        if (socialFailureHandler != null) {
            configurer.failureHandler(socialFailureHandler);
        }

        http.apply(configurer);
    }
}
