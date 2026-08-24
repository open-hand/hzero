package org.hzero.platform.domain.service.datasource;

import org.hzero.platform.infra.annocations.DatasourceType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * 数据源类型处理
 *
 * @author xiaoyu.zhao@hand-china.com
 */
@Component
public class DBTypeProcessor implements BeanPostProcessor {

    @Autowired
    private Driver driver;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        DatasourceType type = clazz.getAnnotation(DatasourceType.class);
        if (type == null || !(bean instanceof Connection)) {
            return bean;
        }
        String[] value = type.value();
        for (String s : value) {
            driver.register(s, (Connection) bean);
        }
        return bean;
    }

}
