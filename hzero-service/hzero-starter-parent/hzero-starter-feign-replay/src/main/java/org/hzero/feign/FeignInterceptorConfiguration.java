package org.hzero.feign;

import java.util.List;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.hzero.core.properties.CoreProperties;
import org.hzero.feign.aspect.FeignClientAspect;
import org.hzero.feign.interceptor.AccessTokenInterceptor;
import org.hzero.feign.interceptor.CompositeRequestInterceptor;
import org.hzero.feign.interceptor.FeignRequestInterceptor;
import org.hzero.feign.interceptor.JwtRequestInterceptor;

/**
 * 拦截feign 配置类
 * @author XCXCXCXCX
 */
@Configuration
public class FeignInterceptorConfiguration {

    @Bean
    public FeignClientAspect feignClientAspect(){
        return new FeignClientAspect();
    }

    @Bean
    public JwtRequestInterceptor jwtRequestInterceptor(CoreProperties coreProperties){
        return new JwtRequestInterceptor(coreProperties);
    }

    @Bean
    public AccessTokenInterceptor accessTokenInterceptor() {
        return new AccessTokenInterceptor();
    }

    @Bean
    public RequestInterceptor requestInterceptor(List<FeignRequestInterceptor> interceptors){
        return new CompositeRequestInterceptor(interceptors);
    }

}
