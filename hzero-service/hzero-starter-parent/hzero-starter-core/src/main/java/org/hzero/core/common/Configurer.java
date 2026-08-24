package org.hzero.core.common;

/**
 * 配置器
 *
 * @author XCXCXCXCX
 * @date 2020/5/8 10:43 上午
 */
@FunctionalInterface
public interface Configurer<T> {

    /**
     * 配置param
     * @param param
     */
    void configure(T param);

}
