package org.hzero.core.interceptor;

/**
 * Common handler interceptor.
 *
 * @author bojiangzhou 2020/05/28
 */
public interface HandlerInterceptor<T> {

    /**
     * @param obj the obj to handle.
     */
    void interceptor(T obj);

}
