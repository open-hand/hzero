package org.hzero.starter.keyencrypt.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hzero.core.net.RequestHeaderCopyInterceptor;
import org.hzero.starter.keyencrypt.json.CryptoModule;
import org.hzero.starter.keyencrypt.mvc.EncryptPathVariableMethodArgumentResolver;
import org.hzero.starter.keyencrypt.mvc.EncryptRequestParamMethodArgumentResolver;
import org.hzero.starter.keyencrypt.mvc.EncryptRequestResponseBodyMethodProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.PriorityOrdered;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiangyu.qi01@hand-china.com on 2019-11-29.
 */
public class WebBeanPostProcessor implements BeanPostProcessor, PriorityOrdered {

    protected ConfigurableBeanFactory beanFactory;
    protected IEncryptionService encryptionService;
    protected ObjectMapper objectMapper;

    public WebBeanPostProcessor(ConfigurableBeanFactory beanFactory,
                                IEncryptionService encryptionService,
                                ObjectMapper objectMapper) {
        this.beanFactory = beanFactory;
        this.encryptionService = encryptionService;
        this.objectMapper = objectMapper;
    }


    @Nullable
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RestTemplate) {
            List<HttpMessageConverter<?>> messageConverters = ((RestTemplate) bean).getMessageConverters();
            if (!CollectionUtils.isEmpty(messageConverters)) {
                messageConverters.forEach(messageConverter -> {
                    if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
                        ((MappingJackson2HttpMessageConverter) messageConverter).setObjectMapper(objectMapper);
                    }
                });
            }
            List<ClientHttpRequestInterceptor> interceptors = ((RestTemplate) bean).getInterceptors();
            if (!CollectionUtils.isEmpty(interceptors)) {
                interceptors.forEach(interceptor -> {
                    if (interceptor instanceof RequestHeaderCopyInterceptor) {
                        ((RequestHeaderCopyInterceptor) interceptor).addIgnoreHeader(EncryptContext.HEADER_ENCRYPT);
                    }
                });
            }
        }
        //扩展objectMapper配置
        if (bean instanceof ObjectMapper) {
            ObjectMapper objectMapper = (ObjectMapper) bean;
            CryptoModule cryptoModule = new CryptoModule();
            encryptionService.setObjectMapper(objectMapper);
            cryptoModule.addEncryptionService(encryptionService);
            objectMapper.registerModule(cryptoModule);
            return objectMapper;
        }
        if (bean instanceof RequestMappingHandlerAdapter) {
            RequestMappingHandlerAdapter adapter = (RequestMappingHandlerAdapter) bean;
            List<HandlerMethodArgumentResolver> currentResolvers = adapter.getArgumentResolvers();
            if (currentResolvers == null) {
                throw new IllegalStateException(
                        String.format("No HandlerMethodArgumentResolvers found in RequestMappingHandlerAdapter %s!", beanName));
            }
            //IEncryptionService encryptionService = new EncryptionService();
            //替换PathVariableMethodArgumentResolver 和 RequestParamMethodArgumentResolver
            PathVariableMethodArgumentResolver pathVariableMethodArgumentResolver = new EncryptPathVariableMethodArgumentResolver(encryptionService);
            RequestParamMethodArgumentResolver requestParamMethodArgumentResolverFalse = new EncryptRequestParamMethodArgumentResolver(encryptionService, beanFactory, false);
            RequestParamMethodArgumentResolver requestParamMethodArgumentResolverTrue = new EncryptRequestParamMethodArgumentResolver(encryptionService, beanFactory, true);
            EncryptRequestResponseBodyMethodProcessor encryptRequestResponseBodyMethodProcessor;
            try {
                encryptRequestResponseBodyMethodProcessor = new EncryptRequestResponseBodyMethodProcessor(adapter.getMessageConverters(),
                        (List<Object>) FieldUtils.readDeclaredField(adapter, "requestResponseBodyAdvice", true));
            } catch (IllegalAccessException e) {
                throw new CommonException(e);
            }
            encryptRequestResponseBodyMethodProcessor.setEncryptionService(encryptionService);
            List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>(adapter.getArgumentResolvers().size());
            //spring 默认注册了2个requestParamMethodArgumentResolver
            boolean isFirst = true;
            for (HandlerMethodArgumentResolver resolver : adapter.getArgumentResolvers()) {
                if (resolver instanceof PathVariableMethodArgumentResolver) {
                    resolvers.add(pathVariableMethodArgumentResolver);
                    continue;
                }
                if (resolver instanceof RequestParamMethodArgumentResolver) {
                    if (isFirst) {
                        resolvers.add(requestParamMethodArgumentResolverFalse);
                        isFirst = false;
                        continue;
                    }
                    resolvers.add(requestParamMethodArgumentResolverTrue);
                    continue;
                }
                if (resolver instanceof RequestResponseBodyMethodProcessor) {
                    resolvers.add(encryptRequestResponseBodyMethodProcessor);
                    continue;
                }
                resolvers.add(resolver);
            }
            adapter.setArgumentResolvers(resolvers);

            resolvers = new ArrayList<>(adapter.getInitBinderArgumentResolvers().size());
            isFirst = true;
            for (HandlerMethodArgumentResolver resolver : adapter.getInitBinderArgumentResolvers()) {
                if (resolver instanceof PathVariableMethodArgumentResolver) {
                    resolvers.add(pathVariableMethodArgumentResolver);
                    continue;
                }
                if (resolver instanceof RequestParamMethodArgumentResolver) {
                    if (isFirst) {
                        resolvers.add(requestParamMethodArgumentResolverFalse);
                        isFirst = false;
                        continue;
                    }
                    resolvers.add(requestParamMethodArgumentResolverTrue);
                    continue;
                }
                resolvers.add(resolver);

            }
            adapter.setInitBinderArgumentResolvers(resolvers);
            return adapter;
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
