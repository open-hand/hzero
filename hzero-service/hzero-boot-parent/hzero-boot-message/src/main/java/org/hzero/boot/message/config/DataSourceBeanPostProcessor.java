package org.hzero.boot.message.config;

import org.hzero.boot.message.MessageClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Objects;

/**
 * @author qingsheng.chen@hand-china.com 2019-06-20 20:22
 */
public class DataSourceBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (Objects.equals(beanName, "sqlSessionFactory")) {
            MessageClient.setSqlSessionFactory(bean);
        }
        return bean;
    }
}
