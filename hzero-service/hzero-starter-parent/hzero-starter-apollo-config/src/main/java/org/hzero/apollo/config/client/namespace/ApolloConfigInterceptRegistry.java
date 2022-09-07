package org.hzero.apollo.config.client.namespace;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 用于在{@link EnableApolloConfig}时的增强处理
 * @author XCXCXCXCX
 */
public class ApolloConfigInterceptRegistry implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata
                .getAnnotationAttributes(EnableApolloConfig.class.getName()));
        if (attributes == null) {
            return;
        }
        String[] namespaces = attributes.getStringArray("value");
        for(String namespace : namespaces){
            NamespaceManager.addNamespace(namespace);
        }
    }
}
