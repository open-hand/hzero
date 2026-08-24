//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.hzero.starter.keyencrypt.mvc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class EncryptArgParamResolverConfiguration implements WebMvcConfigurer {
    public EncryptArgParamResolverConfiguration() {
    }

    @Bean
    public EncryptServletModelAttributeMethodProcessor encryptMethodProcessor() {
        return new EncryptServletModelAttributeMethodProcessor();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(encryptMethodProcessor());
    }
}
