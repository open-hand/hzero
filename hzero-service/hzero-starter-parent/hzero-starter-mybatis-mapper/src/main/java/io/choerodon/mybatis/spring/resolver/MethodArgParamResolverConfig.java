package io.choerodon.mybatis.spring.resolver;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;



/**
 * 请求映射pageRequest配置类
 *
 * @author NaccOll
 * 2018/1/30
 **/
@Configuration
public class MethodArgParamResolverConfig extends WebMvcConfigurerAdapter {
    @Bean
    public PageRequestHandlerMethodArgumentResolver pageRequestResolver() {
        return new PageRequestHandlerMethodArgumentResolver(sortHandlerMethodArgumentResolver());
    }

    @Bean
    public SortHandlerMethodArgumentResolver sortHandlerMethodArgumentResolver() {
        return new SortHandlerMethodArgumentResolver();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(pageRequestResolver());
        argumentResolvers.add(sortHandlerMethodArgumentResolver());
    }

}

