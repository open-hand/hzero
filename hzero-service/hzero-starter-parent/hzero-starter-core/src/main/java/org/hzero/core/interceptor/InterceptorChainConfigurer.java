package org.hzero.core.interceptor;

import org.springframework.core.annotation.Order;

/**
 * 拦截器链配置器，可通过 {@link Order} 注解设置配置器的顺序
 *
 * @author bojiangzhou 2020/06/04
 */
public interface InterceptorChainConfigurer<T, B extends InterceptorChainBuilder<T>> {

    /**
     * 拦截器链配置
     *
     * @param chainBuilder 拦截器链构造器
     */
    void configure(B chainBuilder);
}
