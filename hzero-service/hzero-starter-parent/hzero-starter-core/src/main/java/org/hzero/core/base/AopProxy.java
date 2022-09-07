package org.hzero.core.base;

import org.springframework.aop.framework.AopContext;

/**
 * 封装self()方法便于获取自身接口代理类
 * @param <T> 代理接口类型
 *
 * @author gaokuo.dai@hand-china.com 2018年8月12日下午12:43:08
 */
public interface AopProxy <T>{

    @SuppressWarnings("unchecked")
    default T self() {
        return (T) AopContext.currentProxy();
    }
    
}
