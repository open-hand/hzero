package org.hzero.mybatis.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import oracle.sql.TIMESTAMP;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author qingsheng.chen@hand-china.com
 */
public class TimestampSerializerPostProcess implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (ObjectMapper.class.equals(bean.getClass())) {
            SimpleModule javaTimeModule = new SimpleModule();
            javaTimeModule.addSerializer(TIMESTAMP.class, new TimestampSerializer());

            ObjectMapper mapper = (ObjectMapper) bean;
            mapper.registerModules(javaTimeModule);
        }
        return bean;
    }
}
