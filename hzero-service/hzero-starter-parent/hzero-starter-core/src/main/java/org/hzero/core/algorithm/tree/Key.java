package org.hzero.core.algorithm.tree;

/**
 * <p>
 * 当前节点的 key
 * </p>
 *
 * @author qingsheng.chen 2018/8/2 星期四 20:47
 */
public interface Key<P, T extends Child<T>> {
    /**
     * 获取当前节点的 key
     *
     * @param obj 对象
     * @return key
     */
    P getKey(T obj);
}
