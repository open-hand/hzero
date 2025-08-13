package org.hzero.mybatis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.hzero.mybatis.config.interceptor.HelperInitInterceptor;

/**
 * <p>
 * Web 配置
 * </p>
 *
 * @author qingsheng.chen 2018/11/10 星期六 10:07
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HelperInitInterceptor()).addPathPatterns("/**");
    }
}
