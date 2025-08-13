package org.hzero.core.jackson.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.jackson.deserializer.DateDeserializer;
import org.hzero.core.jackson.deserializer.LocalDateTimeDeserializer;
import org.hzero.core.jackson.deserializer.TrimStringDeserializer;
import org.hzero.core.jackson.serializer.DateSerializer;
import org.hzero.core.jackson.serializer.LocalDateTimeSerializer;
import org.hzero.core.jackson.serializer.SensitiveStringSerializer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.env.SpringApplicationJsonEnvironmentPostProcessor;
import org.springframework.core.PriorityOrdered;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author qingsheng.chen@hand-china.com
 */
public class ObjectMapperPostProcess implements BeanPostProcessor, PriorityOrdered {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (ObjectMapper.class.equals(bean.getClass())) {
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            javaTimeModule.addSerializer(Date.class, new DateSerializer());
            javaTimeModule.addDeserializer(Date.class, new DateDeserializer());
            javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(BaseConstants.Pattern.DATE)));
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
            javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(BaseConstants.Pattern.DATE)));
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());

            SimpleModule simpleModule = new SimpleModule();
            simpleModule.addSerializer(String.class, new SensitiveStringSerializer());
            simpleModule.addDeserializer(String.class, new TrimStringDeserializer());

            ObjectMapper mapper = (ObjectMapper) bean;

            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            mapper.disable(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS);

            mapper.registerModules(simpleModule, javaTimeModule);
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return SpringApplicationJsonEnvironmentPostProcessor.DEFAULT_ORDER + 100;
    }
}
