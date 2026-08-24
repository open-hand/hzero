package org.hzero.starter.keyencrypt;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.Decoder;
import feign.optionals.OptionalDecoder;
import org.hzero.core.jackson.config.ObjectMapperPostProcess;
import org.hzero.core.util.ResponseUtils;
import org.hzero.starter.keyencrypt.core.EncryptProperties;
import org.hzero.starter.keyencrypt.core.EncryptionService;
import org.hzero.starter.keyencrypt.core.IEncryptionService;
import org.hzero.starter.keyencrypt.core.WebBeanPostProcessor;
import org.hzero.starter.keyencrypt.mvc.EncryptHeaderContextFilter;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.PriorityOrdered;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * @author xiangyu.qi01@hand-china.com
 */
@Configuration
@EnableConfigurationProperties(EncryptProperties.class)
public class EncryptConfiguration implements PriorityOrdered {

    @Bean
    public EncryptHeaderContextFilter HeaderContextFilter() {
        return new EncryptHeaderContextFilter();
    }

    @Bean
    public IEncryptionService encryptionService(EncryptProperties encryptProperties) {
        return new EncryptionService(encryptProperties);
    }

    @Bean
    public WebBeanPostProcessor webBeanPostProcessor(ConfigurableBeanFactory beanFactory,
                                                     IEncryptionService encryptionService,
                                                     ObjectMapperPostProcess objectMapperPostProcess) {
        return new WebBeanPostProcessor(beanFactory, encryptionService, customObjectMapper(objectMapperPostProcess));
    }

    @Bean
    public Decoder feignDecoder(ObjectMapperPostProcess objectMapperPostProcess) {
        ObjectMapper customObjectMapper = customObjectMapper(objectMapperPostProcess);
        HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(customObjectMapper);
        ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(jacksonConverter);
        ResponseUtils.setObjectMapper(customObjectMapper);
        return new OptionalDecoder(new ResponseEntityDecoder(new SpringDecoder(objectFactory)));
    }

    public ObjectMapper customObjectMapper(ObjectMapperPostProcess objectMapperPostProcess) {
        return (ObjectMapper) objectMapperPostProcess.postProcessAfterInitialization(new ObjectMapper(), "customObjectMapper");
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
