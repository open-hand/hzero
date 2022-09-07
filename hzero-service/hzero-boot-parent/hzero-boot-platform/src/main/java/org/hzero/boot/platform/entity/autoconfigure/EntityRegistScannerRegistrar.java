package org.hzero.boot.platform.entity.autoconfigure;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author xingxing.wu@hand-china.com 2019/07/16 9:40
 */
public class EntityRegistScannerRegistrar implements ImportBeanDefinitionRegistrar {
    public static final String DEFAULT_BASE_PACKAGE = "org.hzero/**/entity/**";
    public static final String BASE_PACKAGE = "basePackages";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EntityRegistScan.class.getName()));
        if (annoAttrs == null) {
            return;
        }
        //构建扫描包
        List<String> basePackages = new ArrayList<>(1);
        for (String pkg : annoAttrs.getStringArray(BASE_PACKAGE)) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        EntityRegistScanner scanner = new EntityRegistScanner(registry);
        scanner.registerFilters();
        scanner.doScan(StringUtils.toStringArray(basePackages));
    }
}
