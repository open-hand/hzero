package org.hzero.boot.platform.plugin.search.config;

import org.hzero.boot.platform.plugin.search.resolver.SearchConditionsHandlerMethodArgumentResolver;
import org.hzero.boot.platform.plugin.search.resolver.SearchOrdersHandlerMethodArgumentResolver;
import org.hzero.boot.platform.plugin.search.resolver.SearchRequestHandlerMethodArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class SearchMethodArgumentResolverConfig implements WebMvcConfigurer {
    @Bean
    public SearchRequestHandlerMethodArgumentResolver searchRequestHandlerMethodArgumentResolver() {
        return new SearchRequestHandlerMethodArgumentResolver(searchConditionHandlerMethodArgumentResolver()
                , searchOrdersHandlerMethodArgumentResolver());
    }

    @Bean
    public SearchConditionsHandlerMethodArgumentResolver searchConditionHandlerMethodArgumentResolver() {
        return new SearchConditionsHandlerMethodArgumentResolver();
    }

    @Bean
    public SearchOrdersHandlerMethodArgumentResolver searchOrdersHandlerMethodArgumentResolver() {
        return new SearchOrdersHandlerMethodArgumentResolver();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(searchRequestHandlerMethodArgumentResolver());
        resolvers.add(searchConditionHandlerMethodArgumentResolver());
    }
}
