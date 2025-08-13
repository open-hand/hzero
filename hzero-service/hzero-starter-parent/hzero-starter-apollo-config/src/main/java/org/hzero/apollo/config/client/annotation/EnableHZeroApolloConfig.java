package org.hzero.apollo.config.client.annotation;

import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.hzero.apollo.config.client.HZeroApolloConfiguration;
import org.hzero.apollo.config.client.namespace.ApolloConfigInterceptRegistry;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 基于{@link EnableApolloConfig}的增强配置注解
 * @author XCXCXCXCX
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@EnableApolloConfig
@Import({
        ApolloConfigInterceptRegistry.class,
        HZeroApolloConfiguration.class
})
public @interface EnableHZeroApolloConfig {

    /**
     * Apollo namespaces to inject configuration into Spring Property Sources.
     */
    @AliasFor(annotation = EnableApolloConfig.class, attribute = "value")
    String[] value() default {ConfigConsts.NAMESPACE_APPLICATION};

    /**
     * The order of the apollo config, default is {@link Ordered#LOWEST_PRECEDENCE}, which is Integer.MAX_VALUE.
     * If there are properties with the same name in different apollo configs, the apollo config with smaller order wins.
     * @return
     */
    @AliasFor(annotation = EnableApolloConfig.class, attribute = "order")
    int order() default Ordered.LOWEST_PRECEDENCE;
}
