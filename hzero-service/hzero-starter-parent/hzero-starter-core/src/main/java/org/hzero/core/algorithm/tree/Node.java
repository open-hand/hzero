package org.hzero.core.algorithm.tree;

/**
 * <p>
 * 节点的方法
 * </p>
 *
 * @author qingsheng.chen 2018/7/26 星期四 13:42
 */
public interface Node<P, T extends Child<T>> extends Key<P, T>, ParentKey<P, T> {


}
