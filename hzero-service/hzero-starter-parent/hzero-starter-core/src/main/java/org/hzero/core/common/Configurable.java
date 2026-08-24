package org.hzero.core.common;

/**
 * 可配置
 *
 * @author XCXCXCXCX
 * @date 2020/5/8 10:44 上午
 */
public interface Configurable<T> {

    /**
     * 配置
     * @param configurer
     */
    void configure(Configurer<T> configurer);

}
