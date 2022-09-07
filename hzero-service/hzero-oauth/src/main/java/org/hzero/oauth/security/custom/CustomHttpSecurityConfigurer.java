package org.hzero.oauth.security.custom;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * 自定义HttpSecurity配置器
 *
 * @author bojiangzhou 2020/10/21
 */
public interface CustomHttpSecurityConfigurer {

    /**
     * 配置 HttpSecurity
     *
     * @param httpSecurity HttpSecurity
     */
    void configureHttpSecurity(HttpSecurity httpSecurity);

}
