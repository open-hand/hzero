package org.hzero.boot.platform.entity.autoconfigure;

import org.hzero.boot.platform.entity.helper.EntityRegisterHelper;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;

import javax.persistence.Table;
import java.util.Set;

/**
 * 对含有@table注解的entity的扫描类
 *
 * @author xingxing.wu@hand-china.com 2019/07/16 9:41
 */
public class EntityRegistScanner extends ClassPathBeanDefinitionScanner {
    public EntityRegistScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }
    /**
     * 扫描所有的entityClass
     *
     * @param basePackages
     * @return
     */
    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Assert.notEmpty(basePackages, "At least one base package must be specified");
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
            candidates.forEach(item -> {
                try {
                    Class<?> aClass = Class.forName(item.getBeanClassName());
                    //筛选包含Table注解的类
                    if (aClass.isAnnotationPresent(Table.class)) {
                        EntityRegisterHelper.addEntityClass(aClass);
                    }
                } catch (ClassNotFoundException e) {
                    logger.debug("can not get class for:" + item.getBeanClassName(), e);
                }

            });
        }
        return null;
    }

    /**
     * 自定义的过滤器
     */
    public void registerFilters() {
        //新增对Table注解的类的扫描,注意这里而不是说只包含Table注解的类，默认的过滤器有三个，符合默认过滤器的也会被扫描进来
        addIncludeFilter(new AnnotationTypeFilter(Table.class));
    }
}
