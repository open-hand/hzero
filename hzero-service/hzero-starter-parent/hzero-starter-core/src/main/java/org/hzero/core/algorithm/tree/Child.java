package org.hzero.core.algorithm.tree;

import java.util.List;

/**
 * <p>
 * 子对象列表
 * </p>
 *
 * @author qingsheng.chen 2018/7/26 星期四 11:07
 */
public class Child<T> {
    private List<T> children;

    public List<T> getChildren() {
        return children;
    }

    public Child<T> addChildren(List<T> children) {
        if (this.children == null) {
            this.children = children;
        } else {
            this.children.addAll(children);
        }
        return this;
    }

    public Integer getChildrenCount() {
        if (children == null) {
            return null;
        }
        return children.size();
    }


    @Override
    public String toString() {
        return "Child{" +
                "children=" + children +
                '}';
    }
}
