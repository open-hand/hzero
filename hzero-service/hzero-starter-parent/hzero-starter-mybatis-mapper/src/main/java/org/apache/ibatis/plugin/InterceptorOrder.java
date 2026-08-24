package org.apache.ibatis.plugin;

import org.springframework.core.Ordered;

import java.lang.annotation.*;

/**
 * MyBatis 拦截器的顺序
 *
 * @author qingsheng.chen@hand-china.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Documented
public @interface InterceptorOrder {
    /**
     * The order value.
     * <p>Default is {@link Ordered#LOWEST_PRECEDENCE}.
     * @see Ordered#getOrder()
     */
    int value() default Ordered.LOWEST_PRECEDENCE;
}
