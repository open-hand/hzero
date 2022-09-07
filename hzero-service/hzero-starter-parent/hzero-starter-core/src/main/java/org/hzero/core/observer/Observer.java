package org.hzero.core.observer;

import javax.annotation.Nonnull;

/**
 * 观察者
 *
 * @param <T> 观察者主体对象
 * @author bojiangzhou 2020/07/08
 */
public interface Observer<T> {

    /**
     * 观察者顺序
     *
     * @return default 0
     */
    default int order() {
        return 0;
    }

    /**
     * 观察者更新
     *
     * @param target 变化主体
     * @param args   参数
     */
    void update(@Nonnull T target, Object... args);
}
